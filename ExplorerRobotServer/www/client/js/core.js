var joystick;
var joystickSensitivity = 1;

var leftMotorPrevSpeed = 0;
var rightMotorPrevSpeed = 0;

var apiWs = null; // WebSocket connection
var videoStreamWs = null;

var joystickController = null
    joystickControllerRate = 200; // Request every 200ms
    
var sensorRequester = null;
    sensorRefreshRate = 250;

var robotConnectionMode = 'bt';
var isRobotConnected = false;

var wsUrlInput, apiWsUrlInput;
var apiWsUrl = "ws://" + window.location.host + "/ws/api";
var videoStreamWsUrl= "ws://" + window.location.host + "/ws/videostream";

var pic = $('.pic'); 

var map;
var recognition;

function initVoiceRecognition()
{
	recognition = new webkitSpeechRecognition();
	recognition.lang = "fr";
	recognition.continuous = true;
	recognition.interimResults = true;
	
	
	recognition.onresult = function(event)
	{
		var result = event.results[event.results.length-1][0];
		var cmd = result.transcript;
		var confidence = result.confidence;
		
		if(confidence > 0.5)
		{
			console.log(cmd + " -> " + result.confidence);
			   
			if(cmd.indexOf('arrêt') != -1)
			{
				sendCommand({functionality_call: {name: 'mapping', action: 'stop'}});	
				sendCommand({motor_command: [{name: "both", action: "stop"}] });
				return;
			}
			
			if(cmd.indexOf('démarrer carte') != -1)
			{
				$('#mappingTabLink').tab('show');
				sendCommand({functionality_call: {name: 'mapping', action: 'start'}});
				
				return;
			}
			
			if(cmd.indexOf('démarrer') != -1)
			{
				sendCommand({motor_command: [{name: "both", action: "start", speed: 30}] });
				return;
			}
			
			if(cmd.indexOf('gauche') != -1)
			{
				sendCommand({motor_command: [{name: "right", action: "start", speed: 20},{name: "left", action: "start", speed: -20}] });
				return;
			}
			
			if(cmd.indexOf('droite') != -1)
			{
				sendCommand({motor_command: [{name: "right", action: "start", speed: -20},{name: "left", action: "start", speed: 20}] });
				return;
			}
		}		
	}
}

window.onload = function(){
	
	if('webkitSpeechRecognition' in window)
		initVoiceRecognition();
	else
		console.log("Voice recognition only available on chrome");
	

	// Enable tab navigation
	$('.nav-tabs a').click(function (e) {
		e.preventDefault()
		$(this).tab('show')
	});
	
	apiWsUrlInput = $('#apiWsUrl');
	videoStreamWsUrlInput = $('#videoStreamWsUrl');
		
	
	// Put the default address in the input field
	$(apiWsUrlInput).val(apiWsUrl);
	$(videoStreamWsUrlInput).val(videoStreamWsUrl);
	
	
	/* Create joystick */
    joystick = new VirtualJoystick({
        container    : document.getElementById('joystickControlArea'),
        mouseSupport : true
    });
    
    /* Create sensitivity slider */
    $('#joystickSensitivitySlider').slider({
        step    : 0.05,
        min     : 0.01,
        max     : 1,
        slide   : function(event, ui){
                    joystickSensitivity = $(this).slider('value');
                    $('#joystickSensitivityValue').text('x' + joystickSensitivity);
                },
        value: 0.5              
    });
	
	map = new Map('mapCanvas', 'mapCanvas2');
	
	sensorRequester = setInterval(requestSensors,sensorRefreshRate);
	
	/* Send commands from joystick */
    $('#joystickControlArea')
        .mousedown(function(){
            joystickRequester();
            joystickController = setInterval(joystickRequester, joystickControllerRate);
        })
        .mouseup(function(){
            joystickControl(0, 0); // Stop motor on mouse up
            clearInterval(joystickController);
        })
        .on({'touchstart': function(){
            joystickRequester();
            joystickController = setInterval(joystickRequester, joystickControllerRate);
        }})
        .on({'touchend': function(){
            joystickControl(0, 0); // Stop motor on mouse up
            clearInterval(joystickController);
        }});
        
    
    /* Connect to websocket on click */
    $('#btnWebSocketConnect').click(function()
    {
        if (apiWs == null || apiWs.readyState != 1) {
            $(this).button('loading').addClass('btn-warning');
            initApiWS();
        }else{
            apiWs.close();
            apiWs = null;
        }        
    });
	
    /* Update video stream paramaters on click */
    $('#sendCameraConfBtn').click(function()
    {
        if(videoStreamWs == null || videoStreamWs.readySate == 1){
			alert('You are not connected to the video stream websocket');
			return;
        }
		
		var quality = $('#videoStreamQuality').val();
		var fps = $('#videoStreamFPS').val();
		videoStreamWs.send(JSON.stringify({action: 'config', quality: quality, fps: fps}));
    });
	
	$('#downloadMapBtn').click(function(){
		
		this.href = document.getElementById('mapCanvas').toDataURL();
		this.download = 'generatedMap';
	});
	
	
	/* Call the mapping functionality */
	$('#callMappingBtn').click(function(){
		sendCommand({functionality_call: {name: 'mapping', action: 'start'}});	
	});
	
	/* Clear the content of the canvas */
	$('#clearMapBtn').click(function(){
		map.deleteDraw();
	});
	
	/* Zoom in canvas */
	$('#zoomOutBtn').click(function(){
		map.zoom(0.5);
	});
	$('#zoomInBtn').click(function(){
		map.zoom(2);
	});
	
	/* Stop the mapping functionality */
	$('#stopMappingBtn').click(function(){
		sendCommand({functionality_call: {name: 'mapping', action: 'stop'}})
		sendCommand({motor_command: [{name: "both", action: "float"}] });
	});
	
	/* Connection to the websocket for the video stream */
	$('#videoStreamConnectBtn').click(function(){
		
		if(videoStreamWs != null && videoStreamWs.readyState == 1)
			videoStreamWs.close();
		else
			initVideoStreamWs();
	});
    
    /* Ask the connection between the gateway and the robot */
    $('#btnRobotConnect').click(function(){
        if (!isRobotConnected)
            connectRobot();
        else
            disconnectRobot();       
    });
	
	/* Start/stop recogntion */
	$('#startVoiceRecognition').click(function(){
		recognition.start();
	});
    
	$('#stopVoiceRecognition').click(function(){
		recognition.stop();
	});
	
    /* Sensors apparance */
    $('.sensorBox > .panel-heading > label > input').change(function()
	{
        if ($(this).is(':checked'))
            $(this).closest('.sensorBox').find('.panel-body').slideDown();
        else
            $(this).closest('.sensorBox').find('.panel-body').slideUp();
    });
};

function initVideoStreamWs()
{
	videoStreamWs = new WebSocket($(videoStreamWsUrlInput).val());
		
	videoStreamWs.onopen = function(event) {
		videoStreamWs.send(JSON.stringify({type: 'viewer'}));
        console.log("[Status] Connected to the WS for video stream");
        $('#videoStreamConnectBtn').removeClass('btn-danger btn-warning').addClass('btn-success').val('Connected');
    };
    
    videoStreamWs.onmessage = function(event)
	{
		$(pic).attr('src', event.data);
    };

    videoStreamWs.onclose = function(event) {
        $('#videoStreamConnectBtn').removeClass('btn-success btn-warning').addClass('btn-danger').val('disconnected');
        $('#videoStreamConnectBtn').removeClass('btn-success btn-warning').addClass('btn-danger');
        console.log("[Status] VideoStream Disconnected to the WS server");
        isRobotConnected = false;
    };
	
	videoStreamWs.onerror = function(e) {
		alert("Websocket error : " + e);
	};
}

function initApiWS()
{
    apiWs = new WebSocket($(apiWsUrlInput).val());
    
    apiWs.onopen = function(event) {
        console.log("[Status] Connected to the WS server");
        $('#btnWebSocketConnect').button('connected').removeClass('btn-danger btn-warning').addClass('btn-success');
        $('#btnRobotConnect').fadeIn();
    };
    
    apiWs.onmessage = function(event) {
		
        if (event.data == 'ok')
            return;        
        
        var ob = eval('(' + event.data + ')');
		
        if (!isRobotConnected) {
            if ('status' in ob && ob.status == 'connected') {
                isRobotConnected = true;
                $('#btnRobotConnect').removeClass('btn-warning').addClass('btn-success').button('connected');
            }else{
                isRobotConnected = false;
            }
        }
		
        incomingData(ob); 
    };

    apiWs.onclose = function(event) {
        $('#btnWebSocketConnect').button('reset').removeClass('btn-success btn-warning').addClass('btn-danger');
        $('#btnRobotConnect').fadeOut().button('reset').removeClass('btn-success btn-warning').addClass('btn-danger');
        console.log("[Status] Disconnected to the WS server");
        isRobotConnected = false;
    };
	
	apiWs.onerror = function(e) {
		alert("Websocket error : " + e);
	};
}

function incomingData(data) {
    
    if (data.sensor_response)
	{
        var i = 0;
        
        while (data.sensor_response[i])
		{
            switch (data.sensor_response[i].name)
			{
                case "color"        	: proccessColorSensor(data.sensor_response[i].value);
                    break;
                case "leftultrasonic" 	: proccessLeftUltrasonicSensor(data.sensor_response[i].value);
                    break;
				 case "rightultrasonic" : proccessRightUltrasonicSensor(data.sensor_response[i].value);
                    break;
				 case "frontultrasonic" : proccessFrontUltrasonicSensor(data.sensor_response[i].value);
                    break;
                case "touch"        	: proccessTouchSensor(data.sensor_response[i].value);
                    break;
                case "sound"        	: proccessSoundSensor(data.sensor_response[i].value);
                    break;
                case "compass"     	 	: proccessCompassSensor(data.sensor_response[i].value);
                    break;
                case "accelerometer"	: proccessAccelerometerSensor(data.sensor_response[i].value);
                    break;
                case "gyro"         	: proccessGyroSensor(data.sensor_response[i].value);
                    break;
            }
            i++;
        }
		
		return;
    }
	
	if(data.coordinate_update)
	{
		var point = data.coordinate_update;
		var obPoint = new MapObject(point.type);
		
		obPoint.setSize(point.width, point.height);
		obPoint.setCoordinates(point.x, point.y);
		obPoint.orientation = point.orientation;
		
		if(!obPoint.isDefined())
		{
			//ERROR
			console.log(obPoint);
			return;
		}
		
		map.addMapObject(point);
		return;
	}
	
	console.log(data);
}

function connectRobot()
{
    $('#btnRobotConnect').button('loading').removeClass('btn-warning btn-primary').addClass('btn-warning');
	
	var comType = 'bt';
	var robotType = $('input[name=robotType]:checked').val();
	var comPort = 'COM' + $('#ev3ComPort').val();
	var nxtName = $('#nxtName').val();
	var nxtAddress = $('#nxtAddress').val();
	
	var leftUltrasonicPort 	= $('#leftSonarSensor').val();
	var rightUltrasonicPort = $('#rightSonarSensor').val();
	var frontUltrasonicPort = $('#frontSonarSensor').val();
	var compassPort 		= $('#compassSensor').val();
	
	var leftMotorPort 		= $('#leftMotor').val();
	var rightMotorPort 		= $('#rightMotor').val();
	
	var command;
	
	
	switch (robotType) {
		case 'ev3':
			
			command = {
				action: "connect",
				mode: comType,
				robot_type: robotType,
				comPort: comPort,
				leftMotorPort: leftMotorPort,
				rightMotorPort: rightMotorPort,
				compassPort: compassPort,
				leftUltrasonicPort: leftUltrasonicPort,
				frontUltrasonicPort: frontUltrasonicPort,
				rightUltrasonicPort: rightUltrasonicPort
			};
			break;
		
		case 'nxt':
			command = {
				action: "connect",
				mode: comType,
				robot_type: robotType,
				name: nxtName,
				address: nxtAddress,
				leftMotorPort: leftMotorPort,
				rightMotorPort: rightMotorPort,
				compassPort: compassPort,
				leftUltrasonicPort: leftUltrasonicPort,
				frontUltrasonicPort: frontUltrasonicPort,
				rightUltrasonicPort: rightUltrasonicPort
			};
			break;
	}
	
    sendCommand(command, true);
}

function disconnectRobot()
{
    sendCommand({action: "disconnect"});
    isRobotConnected = false;
    clearInterval(sensorRequester);
    $('#btnRobotConnect').button('reset').removeClass('btn-success btn-warning').addClass('btn-danger');
}

function sendCommand(ob, con)
{

    if (isRobotConnected ||  con)
        apiWs.send(JSON.stringify(ob));
    else
        console.log('Not connected to NXT');
}

/* Joystick controls */
function joystickRequester()
{
    
    var x = joystick.deltaX();
    var y = joystick.deltaY();
    $('#axesValues').text('X: ' + x +', Y: ' + y);
	
    if (apiWs != null && apiWs.readyState == 1)
        joystickControl(x, y); 
}

function joystickControl(x, y)
{
    var motorsSpeed = convertAxesValues(x, y);
    
    if (leftMotorPrevSpeed != motorsSpeed[0] && rightMotorPrevSpeed != motorsSpeed[1]) {
        leftMotorPrevSpeed = motorsSpeed[0];
        rightMotorPrevSpeed = motorsSpeed[1];
        
		if(motorsSpeed[0] == 0 && motorsSpeed[1] == 0)
		{
			sendCommand({motor_command: [{name: "both", action: "float"}] });
		}else{
		
			sendCommand(
				{
					motor_command: [
						{name: "right", action: "start", speed: motorsSpeed[0]},
						{name: "left", action: "start", speed: motorsSpeed[1]}                    
					]
				}
			);        
		}
    }    
}

function convertAxesValues(x, y)
{
    if(x <= 0)
    {
        leftMotor = y+x;
        rightMotor = y;
    }else{
        rightMotor = -x+y;
        leftMotor = y;
    }
    
    return [Math.round(rightMotor*joystickSensitivity), Math.round(leftMotor*joystickSensitivity)];
}


/* Sensors */

function requestSensors()
{
    var sensors = new Array();
    
    if ($('#activeColor').is(':checked')) 
        sensors.push({name: "color"});
    if ($('#activeUltrasonics').is(':checked'))
	{		
        sensors.push({name: "leftUltrasonic"});
        sensors.push({name: "rightUltrasonic"});
        sensors.push({name: "frontUltrasonic"});
	}
    if ($('#activeTouch').is(':checked')) 
        sensors.push({name: "touch"});
    if ($('#activeSound').is(':checked'))
        sensors.push({name: "sound"});
    if ($('#activeCompass').is(':checked'))
        sensors.push({name: "compass"});
    if ($('#activeAccelerometer').is(':checked'))
        sensors.push({name: "accelerometer"});
    if ($('#activeGyro').is(':checked'))
        sensors.push({name: "gyro"});
        
    if (sensors.length > 0)
        sendCommand({sensor_request:sensors});
}

function proccessColorSensor(ob)
{
   $('#progressBarColorR').width(ob.r); 
   $('#progressBarColorG').width(ob.g); 
   $('#progressBarColorB').width(ob.b);
}

function proccessLeftUltrasonicSensor(ob)
{
    ob = (ob > 250) ? '??' : ob;
    $('#leftUltrasonicSensorValue').text(ob);       
}

function proccessRightUltrasonicSensor(ob)
{
    ob = (ob > 250) ? '??' : ob;
    $('#rightUltrasonicSensorValue').text(ob);       
}

function proccessFrontUltrasonicSensor(ob)
{
    ob = (ob > 250) ? '??' : ob;
    $('#frontUltrasonicSensorValue').text(ob);       
}

function proccessSoundSensor(ob)
{
    $('#soundSensorValue').text(ob);
}

function proccessCompassSensor(ob)
{
    $('#compassSensorValue').text(ob);
}

function proccessAccelerometerSensor(ob)
{
    $('#accelerometerValue').html('X: ' + ob.x + '<br/>Y: ' + ob.y + '<br/>Z: ' +ob.z);
}

function proccessGyroSensor(ob)
{
    $('#gyroValue').html(ob);
}


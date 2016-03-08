window.addEventListener('load', function(){
				
	var cam = new Camera();
	
	// Open the stream
	// cam.start(function(){
		// console.log('Camera stream opened');
	// });				

	
	/* Button objects */
	var wsConnectButton   = document.querySelector('#wsConnectButton');
	var wsUrlInput 		  = document.querySelector('#wsUrlInput');
	var startToStream 	  = document.querySelector('#startToStream');
	var stopToStream      = document.querySelector('#stopToStream');
	
	var startStream      = document.querySelector('#startStream');
	var stopStream       = document.querySelector('#stopStream');
	
	var ws;
	
	/* Containers */
	var pic = document.querySelector('#pic');
	

	/* Default settings */
	wsUrlInput.value = 'ws://' + location.host + '/ws/videostream';
	var quality = 0.2;
	var frequency = 1000/30;
	document.getElementById("qualityField").innerHTML = "Quality : "+quality;
	document.getElementById("fpsField").innerHTML = "FPS : "+(1000/frequency);
					
	// Timer to send image each "frequency" ms
	var streamTimer;
		
	/*********************
	 * Buttons actions
	***********************/
	wsConnectButton.addEventListener('click', function(){
		ws = new WebSocket(wsUrlInput.value);
	
		ws.onopen = function(){
			ws.send(JSON.stringify({type: 'broadcaster'}));
			document.getElementById("wsConnectButton").style.background = '#00CC33';
			console.log('Connected to ws server');				
		};
		
		ws.onmessage = function(e){
			var message = eval('(' + e.data + ')');
			
			if(message.action && message.action == 'config') {
				quality   = parseFloat(message.quality);
				frequency = (1000/parseFloat(message.fps));
				document.getElementById("qualityField").innerHTML = "Quality : "+quality;
				document.getElementById("fpsField").innerHTML = "FPS : "+message.fps;
				sendStream();
			}
		};
		
		ws.onclose = function(){
			console.log('Disconnected to ws server');
			document.getElementById("wsConnectButton").style.background = '#B80000';
		};
	});
	
	startStream.addEventListener('click', function(){
		cam.start();
		document.getElementById("startStream").style.background = '#00CC33';
	});
	
	stopStream.addEventListener('click', function(){
		cam.stop();		
		document.getElementById("startStream").style.background = '#E8E8E8';
	});
	
	startToStream.addEventListener('click', sendStream);
	
	stopToStream.addEventListener('click', function(){
		clearInterval(streamTimer);
		document.getElementById("startToStream").style.background = '#E8E8E8';
	});
	
	function sendStream() {
		if(streamTimer)
			clearInterval(streamTimer);
		document.getElementById("startToStream").style.background = '#00CC33';
		streamTimer = setInterval(function(){
			
			if (cam.isStreamActive)
			{
				//pic.src = 'data:image/jpeg;base64,'+img1;

				if (ws.readyState == 1){					
					ws.send(JSON.stringify({new_image: cam.getB64Image(quality)}));
			
					
				}
			}
		}, frequency);
	}
	
});
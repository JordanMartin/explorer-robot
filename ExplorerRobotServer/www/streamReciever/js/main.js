window.addEventListener('load', function(){
				
	var wsConnectButton   = document.querySelector('#wsConnectButton');
	var wsUrlInput 		  = document.querySelector('#wsUrlInput');	
	var ws;
	
	/* Containers */
	var canvas = document.createElement('canvas');
	var pic    = document.querySelector('#pic');
	

	/* Default settings */
	wsUrlInput.value = 'ws://' + location.host + '/ws/videostream';

					
	// Timer to send image each "frequency" ms
	var timer;
	
	
	/*********************
	 * Buttons actions
	***********************/
	wsConnectButton.addEventListener('click', function(){
		ws = new WebSocket(wsUrlInput.value);
	
		ws.onopen = function(){
			ws.send(JSON.stringify({type: 'viewer'}));
			console.log('Connected to ws server');				
		};
		
		ws.onmessage = function(e){
			
			pic.src = e.data;
			//console.log(e.data);
			//
			//if (e.data instanceof ArrayBuffer)
			//{
			//	console.log('img ok');
			//	var bytearray = new Uint8Array(e.data);
			//	var ctx = canvas.getContext('2d');
			//	var imgData = ctx.getImageData(0, 0 ,1280, 720).data;
			//	
			//	for(var i = 0; i < imgData.length; i++)
			//	imgData[i] = bytearray[i];
			//	
			//	ctx.putImageData(imgData, 0, 0);
			//	
			//	pic.height = 1024;
			//	pic.width = 720;
			//	pic.src = canvas.toDataURL();
			//}else
			//	console.log('img failed');
			

			 
			
		};
		
		ws.onclose = function(){
			console.log('Disconnected to ws server');
		};
	});

});
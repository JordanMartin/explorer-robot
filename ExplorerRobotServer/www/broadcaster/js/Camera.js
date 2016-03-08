/**
 * @author Jordan Martin
 * @date 28/09/13
 *
 * Get the video stream of user and send it over websocket  
 **/

/* ===== CAMERA CLASS ===== */
var Camera = function()
{
	/* Attributes for the image generation */
	
	
	this.isStreamActive  = false;
	this.isSetted 		 = false;	
	
	this._videoContainer = null;                
	this._picCanvas      = null;
	this._imageWidth  	 = 1024;
	this._imageHeight 	 = 768;	
	this._mediaConstraints = {
		video : {
			mandatory: {
			  maxWidth: 512,
			  maxHeight: 360
			}
		}
		, audio: false
	};	
	this._stream = null;
	
	// navigator.getUserMedia() is not yet a standard so we have to set the right method of the user browser
	navigator.getUserMedia = navigator.getUserMedia || navigator.webkitGetUserMedia || navigator.mozGetUserMedia;
};


// Methods               
Camera.prototype = {
	
	start : function(callbackOnStarted, callbackOnPlayed){
		
		var self = this;
		
		navigator.getUserMedia( // Request user media
			
			this._mediaConstraints,
			
			function(stream){ // Callback when user stream getted
				
				self.isSetted = true;
				self.isStreamActive = false;
				
				if (callbackOnStarted && typeof(callbackOnStarted) === 'function')
					callbackOnStarted();
		
				stream.onended = function(){
					self.isStreamActive = false;
				}
				
				self._videoContainer = document.createElement('video');
				self._picCanvas      = document.createElement('canvas');
				
				self._videoContainer.addEventListener('playing', function(){
	
					var tmpTimer = setInterval(function(){ // Wait for video playing
						if(self._videoContainer.videoHeight > 0){
							self._imageWidth  = self._videoContainer.videoWidth;
							self._imageHeight = self._videoContainer.videoHeight;
							
							if (callbackOnPlayed && typeof(callbackOnPlayed) === 'function')
								callbackOnPlayed();
								
							clearInterval(tmpTimer);
						}
					}, 1);			
				});
				
				if(window.URL) 
					self._videoContainer.src = window.URL.createObjectURL(stream);
				else
					self._videoContainer.src = stream;
					
				self.isStreamActive = true;
				
				self._videoContainer.play(); // Start playing the stream into the hidden video object
				
				self._stream = stream;
			},
			
			function(err){ // Callback when user stream could not be getted
				console.log("Can't open stream : " + JSON.stringify(err));
				self.isStreamActive = false;
			}		
		);  
	},
	
	stop : function(){
		if(this._stream)
			this._stream.stop();
		this.isStreamActive = false;
		this.isSetted	= false;
	},
	
	getB64Image: function(q){
		
		if (!this.isStreamActive) 
			throw "Stream Not opened";
		
		var width   = this._videoContainer.videoWidth;
		var height  = this._videoContainer.videoHeight;
		
		this._picCanvas.setAttribute('width', width);
		this._picCanvas.setAttribute('height', height);
		
		this._picCanvas.getContext('2d').drawImage(this._videoContainer, 0, 0);
		
		//q = (q == undefined) ? 0.5 : q;
		console.log(q);
		return this._picCanvas.toDataURL('image/jpeg', q);//.slice(23); // remove header and return            
	},
	
	getPixelsArray: function(){
		
		var width   = this._videoContainer.videoWidth;
		var height = this._videoContainer.videoHeight;
		
		this._picCanvas.setAttribute('width', width);
		this._picCanvas.setAttribute('height', height);
		
		var context = this._picCanvas.getContext('2d');
		context.drawImage(this._videoContainer, 0, 0);
				
		var imgData = context.getImageData(0, 0, width, height).data;
		var bytearray = new Uint8Array(imgData.length);
		
		for(var i = 0; i < imgData.length; i++)
			bytearray[i] = imgData[i];
			
		return bytearray;
	}
};
/* ===== END CAMERA CLASS ===== */


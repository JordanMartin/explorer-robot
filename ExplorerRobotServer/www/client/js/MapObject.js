var MapObject = function(type){
    
    this.x           = null;
    this.y           = null;
    
    this.type        = type;
    
    this.width       = null;
    this.height      = null;
    this.orientation = null;    
};

MapObject.prototype = {
    
    setCoordinates : function(x, y){
        this.x = x;
        this.y = y;
    },
    
    setSize : function(w, h){
        this.width = w;
        this.height = h;
    },
    
    isDefined : function(){
        return (this.orientation !== null
                && this.type !== null
                && this.x !== null
                && this.y !== null
                && this.width !== null
                && this.height);   
    }
};


var Map = function(canvasId, canvasId2){
    
    this.canvas = document.querySelector('#' + canvasId);
    this.context = this.canvas.getContext('2d');

    // TODO : scale factor 
    this.scale  = null;
    
    this.height = $(this.canvas).parent().height();
    this.width  = $(this.canvas).parent().width();
        
    this.canvas.width  = this.width;
    this.canvas.height = this.height;
    
    if(canvasId2)
    {       
        this.canvas2 = document.querySelector('#' + canvasId2);
        this.context2 = this.canvas2.getContext('2d');
        
        this.canvas2.width  = this.width;
        this.canvas2.height = this.height;
    }
    
    this.points = new Array();
    this.lastRobot;
};

Map.prototype = {
    
    addMapObject : function(mapObject)
    {
        if( ! mapObject instanceof MapObject || ! mapObject)
            return;
        
        // Save all the points
        this.points.push(mapObject);
        
        if(mapObject.type == 'robot')
            this.drawMapObject(mapObject, true);
            
        this.drawMapObject(mapObject);
    },
    
    drawMapObject : function(mapObject, currentRobotPos)
    {
        var color;
        var ctx;
        
        if(currentRobotPos && currentRobotPos === true)
        {
            this.clear(this.canvas2);
            ctx = this.canvas2.getContext('2d');
            color = '#FF5E5E';
            ctx.globalAlpha = 1;
        }else
        {
            ctx = this.canvas.getContext('2d');
            
            // Set the color of the object
            if(mapObject.type == 'robot')
            {
                color = '#C1ADFF';            
                ctx.globalAlpha = 0.05;
            }
            else if(mapObject.type == 'obstacle')
            {
                color = '#474647';
                ctx.globalAlpha = 1;
            }
            else
                return; 
        }
                
               
        var x      = mapObject.x;
        var y      = mapObject.y;
        
        var width  = mapObject.width;
        var height = mapObject.height;
        var angle  = mapObject.orientation * (Math.PI/180);
        
        // Positionate points from the middle of the canvas
        x += this.width/2;
        y += this.height/2;
                
        // Color of the shape
        ctx.fillStyle = color;
        
        // Save the current context
        ctx.save();
        
        // Translation to rotate the rectangle by his center
        ctx.translate(x, y);              
        ctx.rotate(angle);
        ctx.translate(-x,-y);
        
        // Draw the rectangle arround the x, y coordinates
        ctx.fillRect(x - 0.5 * width, y -   0.5 * height, width, height);
        
        // Restore the saved context
        ctx.restore();
    },
    
    clear: function(canvas)
    {
        var ctx = canvas.getContext('2d');
        
		ctx.save();
		ctx.setTransform(1,0,0,1,0,0);
		ctx.clearRect(0, 0, this.width, this.height);
		ctx.restore();
    },
    
    deleteDraw: function()
    {
        this.clear(this.canvas);
        this.clear(this.canvas2);
        this.points = null;
        this.points = new Array();
    },
    
    zoom: function(factor)
    {
        var ctx = this.canvas.getContext('2d');
        var ctx2 = this.canvas2.getContext('2d');
        
        var translationFactor = (factor > 1) ? -factor+1 : factor;
        
        ctx.translate(this.width*translationFactor*0.5, this.height*translationFactor*0.5);
        ctx.scale(factor, factor);
        
        ctx2.translate(this.width*translationFactor*0.5, this.height*translationFactor*0.5);        
        ctx2.scale(factor, factor);
                
        this.draw();
    },
    
    draw: function()
    {
        this.clear(this.canvas);
        this.clear(this.canvas2);
        
        for(var i = 0; i < this.points.length; i++)
        { 
            this.drawMapObject(this.points[i]);
        }
    }
};
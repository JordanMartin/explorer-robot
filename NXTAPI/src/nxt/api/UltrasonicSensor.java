package nxt.api;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



import lejos.nxt.remote.NXTCommand;
import lejos.util.Delay;

/**
 *
 * @author Jordan
 */
public class UltrasonicSensor extends I2CSensor {
    
    NXTCommand nxt = null;
    byte[] byteBuff = new byte[8];
    long dataAvailableTime = 0;
    
    int defaultDelay = 50;
    final byte REG_DISTANCE = 0x42;
    
    public UltrasonicSensor(NXTCommand nxt, int port){
        super(nxt, port);
    }
    
    public int getDistance(){
        
        waitUntil(dataAvailableTime);
        int ret = getData(REG_DISTANCE, byteBuff, 1);
        if (ret < 0)
                return 255;

        // Make a note of when new data should be available.
        dataAvailableTime = System.currentTimeMillis() + defaultDelay;

        return byteBuff[0] & 0xFF;
    }
    
    /**
    * Wait until the specified time
    */
    private void waitUntil(long when)
    {
            long delay = when - System.currentTimeMillis();
            Delay.msDelay(delay);
    }

}

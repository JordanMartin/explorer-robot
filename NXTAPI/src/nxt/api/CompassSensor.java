package nxt.api;



import lejos.nxt.remote.NXTCommand;


/**
 *
 * @author Jordan
 */
public class CompassSensor extends I2CSensor {
    
    NXTCommand nxt = null;
    byte[] buf = new byte[2];
    
    private final static byte COMMAND = 0x41;
    private final static byte BEGIN_CALIBRATION = 0x43;
    private final static byte MEASUREMENT_MODE = 0x00;

    public CompassSensor(NXTCommand nxt, int port){
        super(nxt, port);
    }
    
    public int getDegree(){
        
        int ret = getData(0x42, buf, 2);        
        if(ret != 0) return -1;
        
        float degrees = ((buf[0] & 0xff)<< 1) + buf[1];
        
        if(degrees>=360) degrees -= 360;
		if(degrees<0) degrees += 360;

        return (int) degrees;
    }
    

    public void startCalibration() {
        buf[0] = BEGIN_CALIBRATION;
        super.sendData(COMMAND, buf, 1);
    }

    /**
     * Ends calibration sequence.
     *
     */
    public void stopCalibration() {
        buf[0] = MEASUREMENT_MODE;
        super.sendData(COMMAND, buf, 1);
    }
}

package nxt.api;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



import java.io.IOException;
import lejos.nxt.remote.ErrorMessages;
import lejos.nxt.remote.NXTCommand;


/**
 *
 * @author Jordan
 */
public class I2CSensor {
    
    int port;
    int address = 0x02; // Default I2C address
    NXTCommand nxt = null;
    
    public I2CSensor(NXTCommand nxt, int port){
        this.nxt = nxt;
        this.port = port;
        
        // Flush
        try {
            nxt.LSGetStatus((byte)port); 
            nxt.LSRead((byte)port); 
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }
    
    /**
    * Method for retrieving data values from the sensor. BYTE0 (
    * is usually the primary data value for the sensor.
    * Data is read from registers in the sensor, usually starting at 0x00 and ending around 0x49.
    * Just supply the register to start reading at, and the length of bytes to read (16 maximum).
    * NOTE: The NXT supplies UBYTE (unsigned byte) values but Java converts them into
    * signed bytes (probably more practical to return short/int?)
    * @param register e.g. FACTORY_SCALE_DIVISOR, BYTE0, etc....
    * @param buf
    * @param length Length of data to read (minimum 1, maximum 16) 
    * @return the status
    */
    protected int getData(int register, byte[] buf, int offset, int length) {
        byte[] txData = {(byte) address, (byte) register};
        
        try {
            nxt.LSWrite((byte) port, txData, (byte) length);
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }

        try {
            byte[] status;
            
            do {
                status = nxt.LSGetStatus((byte) port);
            } while (status[0] == ErrorMessages.PENDING_COMMUNICATION_TRANSACTION_IN_PROGRESS
                || status[0] == ErrorMessages.SPECIFIED_CHANNEL_CONNECTION_NOT_CONFIGURED_OR_BUSY);

            byte[] ret = nxt.LSRead((byte) port);
            if (ret == null) {
                return -1;
            }

            System.arraycopy(ret, 0, buf, offset, ret.length);
            return 0;
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
            return -1;
        }
    }

    protected int getData(int register, byte[] buf, int length) {
        return this.getData(register, buf, 0, length);
    }
    
    /**
	 * Sets a single byte in the I2C sensor. 
	 * @param register A data register in the I2C sensor. e.g. ACTUAL_ZERO
	 * @param value The data value.
	 */
	public int sendData(int register, byte value) {
		byte [] txData = {(byte)address, (byte) register, value};
		try {
			int ret = nxt.LSWrite((byte)port, txData, (byte)0);
            return ret;
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
			return -1;
		}
	}
	
	public int sendData(int register, byte [] data, int length) {
		return this.sendData(register, data, 0, length);
	}
	
	/**
	 * Send data top the sensor
	 * @param register A data register in the I2C sensor.
	 * @param data The byte to send.
	 * @param length the number of bytes
	 */
	public int sendData(int register, byte [] data, int offset, int length) {
		byte [] sendData = new byte[length+2];
		sendData[0] = (byte) address;
		sendData[1] = (byte) register;
		// avoid NPE in case length==0 and data==null
		if (length > 0)
			System.arraycopy(data,offset,sendData,2,length);
		try {
			return nxt.LSWrite((byte)port, sendData, (byte)0);
		} catch (IOException ioe) {
			System.out.println(ioe.getMessage());
			return -1;
		}
	}

}

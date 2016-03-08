package nxt.api;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



import lejos.nxt.remote.NXTCommand;
import lejos.nxt.remote.RemoteMotor;
import lejos.pc.comm.NXTComm;

/**
 *
 * @author Jordan
 */
public class NXT extends NXTCommand {
    
    public static final char ID_PORT_A = 'A';
    public static final char ID_PORT_B = 'B';
    public static final char ID_PORT_C = 'C';
    
    public static final int ID_PORT_1 = 0;
    public static final int ID_PORT_2 = 1;
    public static final int ID_PORT_3 = 2;
    public static final int ID_PORT_4 = 3;
    
    
    public CompassSensor compass = null;
    public UltrasonicSensor leftSonar = null;
    public UltrasonicSensor frontSonar = null;
    public UltrasonicSensor rightSonar = null;
    
    public RemoteMotor leftMotor = null;
    public RemoteMotor rightMotor = null;    

    public NXT(NXTComm nxtComm) {
        super(nxtComm);
    }
    
    public void setMotorPort(String which, char port) throws NXTException{
        
        port = Character.toLowerCase(port);
        
        int selectedPort;
        
        switch(port){
            case 'a': 
                selectedPort = 0;
                break;
            case 'b': 
                selectedPort = 1;
                break;
            case 'c': 
                selectedPort = 2;
                break;
            
            default: throw new NXTException("Bad motor port : " + port);
        }
        
        switch(which){
            
            case "left":
                leftMotor = new RemoteMotor(this, selectedPort);
                break;
                
            case "right":
                rightMotor = new RemoteMotor(this, selectedPort);
                break;
                
            default: throw new NXTException("Unknown motor name : " + which);
        }
    }
    
    public void setSensorPort(String which, int port) throws NXTException {
        
        if(port > 4 || port < 1)
            throw new NXTException("Bad sensor port : " + port);
            
        port--;
        
        switch(which){
            
            case "compass":
                compass = new CompassSensor(this, port);
                break;
            
            case "leftSonar":
                leftSonar = new UltrasonicSensor(this, port);
                break;
                
            case "frontSonar":
                frontSonar = new UltrasonicSensor(this, port);
                break;
                
            case "rightSonar":
                rightSonar = new UltrasonicSensor(this, port);
                break;
                
            default: throw new NXTException("Bad senor name : " + which);
        }
    }    
}

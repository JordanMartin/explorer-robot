package nxtmanager;

/*
 * @author Jordan Martin
 * @date 29 october 2013
 */

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import lejos.nxt.*;
import lejos.nxt.addon.CompassHTSensor;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;
import lejos.nxt.comm.RConsole;
import lejos.nxt.comm.USB;

public class NXTManager {

	public static final int BAD_SENSOR_NAME = -100;
	public static final int BAD_REQUEST_ARGUMENT = -101;
	public static final int BAD_COMMAND_ARGUMENT = -102;
	public static final int BAD_NUMBER_COMMAND_ARGUMENT = -103;	
	
	UltrasonicSensor leftSonar;
	UltrasonicSensor frontSonar;
	UltrasonicSensor rightSonar;
	
	NXTRegulatedMotor leftMotor;
	NXTRegulatedMotor rightMotor;
	
	CompassHTSensor compassSensor;
	
	NXTConnection connection = null;
	DataOutputStream dataOut; 		
	DataInputStream  dataIn; 
	
	Boolean enabled = false;
	Boolean running = false;
	
	String mode;
	boolean initialized = false;
	
	public static void main(String args[]){
		
		do{					
			try{
				// Debug with remote console
				//RConsole.openBluetooth(0);
				NXTManager nxt = new NXTManager();
				nxt.waitConnection();
				nxt.startComuunication();
				nxt.exit();
				
			}catch(Exception e){
				LCD.clear();
				LCD.drawString("Exception throwned", 0, 0);
				LCD.drawString(e.getMessage(), 0, 1);
				
				//RConsole.println(e.getMessage());
				//e.printStackTrace(RConsole.getPrintStream());
			}
			
			LCD.clear();
			LCD.drawString("Restart program ?", 0, 4);
			
		}while(Button.waitForAnyPress() == Button.ID_ENTER);				
	}
	
	
	public NXTManager(){
		
		enabled = true;
		
		// Connect remote console for debugging
		//RConsole.openBluetooth(0);
		
		/*
		Button.ENTER.addButtonListener(new ButtonListener() {
			public void buttonPressed(Button b) {
				running = false;
			}

			public void buttonReleased(Button b) {
				
			}
		});*/
	}
	
	public void exit(){
		try{
			connection.close();
			connection = null;
			dataIn = null;
			dataOut = null;
		}catch(Exception e){
			
		}
	}
	
	/**
	 * Wait a input connection and open streams
	 * @return 
	 */
	public void waitConnection(){
		
		LCD.clear();
		LCD.drawString("Connect mode :", 0, 0);
		LCD.drawString("USB < Left btn", 1, 2);
		LCD.drawString("BT  > Right btn", 1, 3);
		
		
		if(Button.waitForAnyPress() == Button.ID_LEFT)
			mode = "usb";
		else
			mode = "bt";
		
		LCD.clear();
		
		if(mode.equals("bt")){
			LCD.drawString("waiting for BT", 0,1 );
			connection = Bluetooth.waitForConnection();
		} else {		
			LCD.drawString("waiting for USB", 0,1 );
			connection = USB.waitForConnection();
		}
		
		// Get the in and out stream
		dataOut = connection.openDataOutputStream();	
		dataIn.readL
		dataIn  = connection.openDataInputStream();	
		
		LCD.clear();				
		LCD.drawString("Connected !", 0, 0);
		LCD.drawString("--------------", 0, 1);
		LCD.drawString("Wait initialization", 0, 3);	
		
		running = true;
	}
	
	/**
	 * Wait and process commands
	 */
	public void startComuunication(){	
		
		LCD.clear();
		
		String ret = "";
		String[] inputData = null;
		
		// Main loop
		while(running){
			
			try {
				String in = readLine(dataIn);	
				
				// Display the input command
				LCD.drawString(in, 0, 4);
				inputData = splitString(in, ',');

			} catch (IOException e) {
				// TODO stop the program on error
			}
			
			switch(inputData[0].toLowerCase()){

				case "sensorrequest":
					if(initialized)
						ret = sensorRequest(inputData);
					else
						ret = "motors and snesors ports are not initialized";
					break;
			
				case "motorcommand":
					if(initialized)
						ret = motorCommand(inputData);
					else
						ret = "motors and snesors ports are not initialized";
					break;								
					
				case "init":
					try{	
				
						ret = init(Integer.parseInt(inputData[1]), 
									Integer.parseInt(inputData[2]),
									Integer.parseInt(inputData[3]),
									Integer.parseInt(inputData[4]),
									inputData[5],inputData[6]);
						
					}catch(ArrayIndexOutOfBoundsException e){							
						// TODO ignore command and ring
						ret = "initfailed";
					}						
					break;
					
				case "disconnect":
					return;
				
				default :
					ret = "unknown command or request";
					break;
			}
			
			if(running){					
				try {
					dataOut.writeChars(ret + "\n");
					dataOut.flush();					
				} catch (IOException e) {
					return;
				}
			}				
		}		
	}
	
	/**
	 * Return the value of a sensor
	 * Request format : "sensorrequest, compass | ultrasonic [,left | front | right]" 
	 * @param request
	 * @return  string that contains the value of the sensor
	 */
	private String sensorRequest(String[] request) {
		
		switch(request[1]){
			case "ultrasonic":
				
				switch(request[2]){			
					case "left"  : return String.valueOf(leftSonar.getDistance());
					case "front" : return String.valueOf(frontSonar.getDistance());
					case "right" : return String.valueOf(rightSonar.getDistance());
					default      : return String.valueOf(BAD_SENSOR_NAME);
				}
				
			case "compass" : return String.valueOf(compassSensor.getDegreesCartesian());
			
			default : return String.valueOf(BAD_REQUEST_ARGUMENT);
		}		
	}
	

	/**
	 * Request format : "motorcommand, left | right | both, start | float | stop | step[, SPEED(int value)][, DEGREE(int value)]" 
	 * @param command
	 * @return
	 */
	private String motorCommand(String[] command) {
		
		NXTRegulatedMotor motorSelected;
		boolean both = false;
		int speed = 0;
		
		if(command[2].equals("start") || command[2].equals("step"))
		{
			try{
				speed = Integer.parseInt(command[3], 10);
			}catch(ArrayIndexOutOfBoundsException e){
				return Integer.toString(BAD_NUMBER_COMMAND_ARGUMENT);
			}
		}
						
		if(command[1].equals("right"))
			motorSelected = rightMotor; 
		else if(command[1].equals("left"))
			motorSelected = leftMotor; 
		else if(command[1].equals("both")){
			motorSelected = leftMotor;
			both = true;
		}else
			return String.valueOf(BAD_COMMAND_ARGUMENT);
								
		switch(command[2]){ // which action to perform
				
			case "start":				
				motorSelected.setSpeed(speed);		
				if(both)
					rightMotor.setSpeed(speed);
				
				if(speed > 0)
					motorSelected.forward();
				else
					motorSelected.backward();
				
				if(both){
					if(speed > 0)
						rightMotor.forward();
					else
						rightMotor.backward();
				}
							
				break;
				
			case "step":
				int angle = Integer.parseInt(command[4], 10);
				
				motorSelected.setSpeed(speed);
				motorSelected.rotate(angle, false);
							
				if(both){
					rightMotor.setSpeed(speed);
					rightMotor.rotate(angle, false);
				}
				break;
				
			case "float":
				motorSelected.flt(true);								
				if(both)
					rightMotor.flt(true);		
				break;
				
			case "stop":
				motorSelected.stop(true);								
				if(both)
					rightMotor.stop(true);				
				break;
				
			default: return String.valueOf(BAD_REQUEST_ARGUMENT); 
		
		}		
		return "ok";		
	}
	

	/**
	 * Setting up the ports of motors and sensors
	 * @param leftSonarPort
	 * @param frontSonarPort
	 * @param rightSonarPort
	 * @param compassPort
	 * @param leftMotorPort
	 * @param rightMotorPort
	 * @throws PortException
	 */
	private String init(int leftSonarPort, int frontSonarPort, int rightSonarPort,
					    int compassPort, String leftMotorPort, String rightMotorPort) {

		try {
			leftSonar  = new UltrasonicSensor(getSensorPort(leftSonarPort));
			frontSonar = new UltrasonicSensor(getSensorPort(frontSonarPort));
			rightSonar = new UltrasonicSensor(getSensorPort(rightSonarPort));
			compassSensor = new CompassHTSensor(getSensorPort(compassPort));			
		} catch (PortException e) {
			
			// TODO Stop the program and ring
			return "initfailed : bad sensor port";
		}


		try{
			leftMotor = getMotor(leftMotorPort);
			rightMotor = getMotor(rightMotorPort);
		}catch(PortException e){			
			// TODO Stop the program and ring
			return "initfailed : bad mortor port";
		}
		
		initialized = true;
		
		LCD.clear();
		LCD.drawString("Wait commands", 0, 0);
		LCD.drawString("=============", 0, 1);
		
		return "initok";
	}
	
	public int getSonarDistance(String whichSonar){
		
		switch(whichSonar){
			case "left": return leftSonar.getDistance();
			case "front": return frontSonar.getDistance();
			case "right": return rightSonar.getDistance();
			default : return -2;			
		}
		
	}
		
	private NXTRegulatedMotor getMotor(String port) throws PortException{
		switch(port.toUpperCase()){
			case "A" : return Motor.A; 
			case "B" : return Motor.B; 
			case "C" : return Motor.C; 
			default : throw new PortException("Bad motor port");
		}
	}
	
	private SensorPort getSensorPort(int port) throws PortException{
		switch(port){
			case 1 : return SensorPort.S1; 
			case 2 : return SensorPort.S2; 
			case 3 : return SensorPort.S3;
			case 4 : return SensorPort.S4;
			default : throw new PortException("Bad sensor port");
		}
	}
	
	public static String[] splitString(String s, char c){
		
		int nbSplit = 1;
		
		for(int i=0; i < s.length(); i++){
			if(s.charAt(i) == c)
				nbSplit++;
		}
		
		String [] splitter = new String[nbSplit];
		
		splitter[0] = new String();
		
		int currentSplit = 0;
				
		for(int i=0; i < s.length(); i++)
		{			
			if(s.charAt(i) == c)
				splitter[++currentSplit] = new String();						
			else
				splitter[currentSplit] += s.charAt(i);	
		}
		
		return splitter;
	}
	
	private static String readLine(DataInputStream dti) throws IOException{

		char c = dti.readChar();

		String line = "";

		while (c != '\n' && c != '\u0000') {

			line += c;
			c = dti.readChar();

		}

		return line;
	}
}


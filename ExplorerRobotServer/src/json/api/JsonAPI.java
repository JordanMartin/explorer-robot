package json.api;

import asawoo.Functionality;
import asawoo.avatar.ExplorerRobot;
import asawoo.avatar.ExplorerRobot.Motor;
import asawoo.avatar.ExplorerRobot.Sensor;
import asawoo.avatar.explorerrobot.functionalities.MappingMemory;
import asawoo.avatar.explorerrobot.implementations.EV3ExplorerRobot.EV3Builder;
import asawoo.avatar.explorerrobot.implementations.NXTExplorerRobot.MotorPort;
import asawoo.avatar.explorerrobot.implementations.NXTExplorerRobot.NXTBuilder;
import asawoo.avatar.explorerrobot.implementations.NXTExplorerRobot.SensorPort;
import designpattern.observer.MessageLog;
import designpattern.observer.Observable;
import designpattern.observer.Observer;
import ev3.api.EV3Types.InputPort;
import ev3.api.EV3Types.OutputPort;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lejos.pc.comm.NXTCommException;
import org.json.me.JSONArray;
import org.json.me.JSONException;
import org.json.me.JSONObject;

/**
 * 
 * @author jordan
 */
public class JsonAPI implements Observable
{
    private final List<Observer> observers = new ArrayList();

    private ExplorerRobot robot = null;

    private final Map<String, Thread> functionalitiesThread;

    public JsonAPI()
    {
        this.functionalitiesThread = new HashMap();
    }

    /**
     * Established a connection with specified NXT
     *
     * @return String the state of the connection
     *
     * @throws NXTCommException
     */
    private String connectToRobot(JSONObject params) throws CommandException
    {

        if (isConnected())
            return "You are already connected to the robot";

        buildRobot(params);

        if (robot.connect())
            return "You are connected to the robot and all it's port are correctly initialized";
        else
            return "Failed to connect";
    }

    /**
     * Setting up the ports configuration on the NXT
     *
     * @param params 
     *
     * @throws json.api.CommandException
     */
    public void buildRobot(JSONObject params) throws CommandException
    {

        String leftMotorString, rightMotorString, compassString,
            leftUltrasonicString, frontUltrasonicString, rightUltrasonicString;

        try
        {
            switch (params.getString("robot_type").toLowerCase())
            {
                case "ev3":

                    EV3Builder ev3 = new EV3Builder();

                    String comPortString = params.getString("comPort").toUpperCase();
                    leftMotorString = params.getString("leftMotorPort").toUpperCase();
                    rightMotorString = params.getString("rightMotorPort").toUpperCase();
                    compassString = params.getString("compassPort").toLowerCase();
                    leftUltrasonicString = params.getString("leftUltrasonicPort").toLowerCase();
                    frontUltrasonicString = params.getString("frontUltrasonicPort").toLowerCase();
                    rightUltrasonicString = params.getString("rightUltrasonicPort").toLowerCase();

                    if (!comPortString.startsWith("COM"))
                        throw new Exception("The com port argument is invalid. (required : COMXX where XX is the number of the port");

                    ev3.comPort(comPortString)
                        .leftUltrasonic(InputPort.valueOf(leftUltrasonicString.substring(0, 1).toUpperCase() + leftUltrasonicString.substring(1)))
                        .frontUltrasonic(InputPort.valueOf(frontUltrasonicString.substring(0, 1).toUpperCase() + frontUltrasonicString.substring(1)))
                        .rightUltrasonic(InputPort.valueOf(rightUltrasonicString.substring(0, 1).toUpperCase() + rightUltrasonicString.substring(1)))
                        .gyroscope(InputPort.valueOf(compassString.substring(0, 1).toUpperCase() + compassString.substring(1)))
                        .leftMotor(OutputPort.valueOf(leftMotorString))
                        .rightMotor(OutputPort.valueOf(rightMotorString));

                    robot = ev3.build();
                    break;

                case "nxt":

                    NXTBuilder nxt = new NXTBuilder();

                    String address = null;
                    
                    if(params.has("address"))
                        address = params.getString("address");
                    
                    String name = params.getString("name");

                    leftMotorString = params.getString("leftMotorPort").toUpperCase();
                    rightMotorString = params.getString("rightMotorPort").toUpperCase();
                    compassString = params.getString("compassPort").toLowerCase();
                    leftUltrasonicString = params.getString("leftUltrasonicPort").toLowerCase();
                    frontUltrasonicString = params.getString("frontUltrasonicPort").toLowerCase();
                    rightUltrasonicString = params.getString("rightUltrasonicPort").toLowerCase();

                    nxt.brickInfos(name, address)
                        .leftUltrasonic(SensorPort.valueOf(leftUltrasonicString.substring(0, 1).toUpperCase() + leftUltrasonicString.substring(1)))
                        .frontUltrasonic(SensorPort.valueOf(frontUltrasonicString.substring(0, 1).toUpperCase() + frontUltrasonicString.substring(1)))
                        .rightUltrasonic(SensorPort.valueOf(rightUltrasonicString.substring(0, 1).toUpperCase() + rightUltrasonicString.substring(1)))
                        .compass(SensorPort.valueOf(compassString.substring(0, 1).toUpperCase() + compassString.substring(1)))
                        .leftMotor(MotorPort.valueOf(leftMotorString))
                        .rightMotor(MotorPort.valueOf(rightMotorString));

                    robot = nxt.build();
                    break;

                default:
                    throw new Exception("The type of the robot must be EV3 or NXT");
            }

        } catch (Exception e)
        {
            throw new CommandException("Initialization failed : " + e.getMessage());
        }
    }

    /**
     * Proccess a request
     *
     * @param jsonString
     *
     * @return String in JSON format : the response for request
     */
    public String doRequest(String jsonString)
    {

        String reponseMessage = "Unknown error";

        try
        {
            JSONObject req = new JSONObject(jsonString);

            if (!req.isNull("action"))
            {
                switch (req.getString("action"))
                {
                    case "connect":
                        reponseMessage = connectToRobot(req);
                        break;
                        
                    case "disconnect":
                        reponseMessage = disconnecToRobot();
                        break;
                        
                    default:
                        throw new CommandException("Bad action command");
                }
            } else if (isConnected())
            {
                if (req.has("motor_command"))
                {
                    if (req.isNull("motor_command"))
                        throw new CommandException("Missing motor command content");
                    else
                        reponseMessage = motorCommand(req.getJSONArray("motor_command"));
                } else if (req.has("sensor_request"))
                {
                    if (req.isNull("sensor_request"))
                        throw new CommandException("Missing sensor request content");
                    else
                        return sensorRequest(req.getJSONArray("sensor_request"));
                } else if (req.has("functionality_call"))
                {
                    if (req.isNull("functionality_call"))
                        throw new CommandException("Missing functionality_call content");
                    else
                        reponseMessage = functionalityCall(req.getJSONObject("functionality_call"));
                } else
                    throw new JSONException("Bad request or command format");

            } else
            {
                fireEvent(this.getClass(), "log", MessageLog.warn(this.getClass(), "Your are not yet connected to the NXT"));
                reponseMessage = "You must be connected to the NXT before you can send command or request";
            }

        } catch (JSONException e)
        {
            fireEvent(this.getClass(), "log", MessageLog.warn(this.getClass(), "Invalid json format : " + e.getMessage()));
            reponseMessage = "Invalid json format : " + e.getMessage();
        } catch (CommandException e)
        {
            fireEvent(this.getClass(), "log", MessageLog.warn(this.getClass(), "Invalid command : " + e.getMessage()));
            reponseMessage = "Invalid command : " + e.getMessage();
        } catch (Exception e)
        {
            fireEvent(this.getClass(), "log", MessageLog.warn(this.getClass(), "Unknown exception : " + e.getClass() + " -> " +  e.getMessage()));
            reponseMessage = "Error during interpretation of request : " + e.getMessage();
        }

        String statusWithRobot = isConnected() ? "connected" : "disconnected";
        return "{\"status\": \"" + statusWithRobot + "\", \"message\": \"" + reponseMessage + "\"}";
    }

    /**
     * Close the communication beetween the NXT and the JsonAPI.
     *
     * @return String in JSON format : the status after the disconnection
     */
    private String disconnecToRobot()
    {
        if (isConnected())
        {
            robot.disconnect();
            robot = null;
            return "You are now disconnected";
        } else
            return "You are not connected";
    }

    /**
     * @return a string which is a json object containing sensors values
     *
     * @throws JSONException
     */
    private String sensorRequest(JSONArray requestArray) throws JSONException, CommandException, IOException
    {
        int length = requestArray.length();
        JSONArray response = new JSONArray();

        for (int i = 0; i < length; i++)
        {
            JSONObject req = requestArray.getJSONObject(i);

            int value;

            String sensorName = req.getString("name").toLowerCase();

            switch (sensorName)
            {
                case "frontultrasonic":
                    value = (int) robot.getSensorValue(Sensor.FRONT_ULTRASONIC)[0];
                    break;

                case "leftultrasonic":
                    value = (int) robot.getSensorValue(Sensor.LEFT_ULTRASONIC)[0];
                    break;

                case "rightultrasonic":
                    value = (int) robot.getSensorValue(Sensor.RIGHT_ULTRASONIC)[0];
                    break;

                case "compass":
                    value = (int) robot.getSensorValue(Sensor.COMPASS)[0];
                    break;

                case "rightmotor":
                    value = (int) robot.getSensorValue(Sensor.RIGHT_MOTOR)[0];
                    break;

                case "lefttmotor":
                    value = (int) robot.getSensorValue(Sensor.LEFT_MOTOR)[0];
                    break;

                default:
                    throw new CommandException("Invalid sensor name");
            }

            response.put(new JSONObject().put("name", sensorName).put("value", value));
        }

        return new JSONObject().put("sensor_response", response).toString();
    }

    /**
     * @param motor
     *
     * @throws JSONException
     * @throws IOException
     */
    private String motorCommand(JSONArray array) throws JSONException, IOException, CommandException
    {

        String name, action;
        int speed = 0, step = 0;
        int length = array.length();

        String responseMessage = "ok";

        for (int i = 0; i < length; i++)
        {
            JSONObject cmd = array.getJSONObject(i);

            name = cmd.getString("name");
            action = cmd.getString("action");

            if (!name.equals("left") && !name.equals("right") && !name.equals("both"))
                throw new CommandException("Invalid name arguement");

            if (!action.equals("start") && !action.equals("stop") && !action.equals("float") && !action.equals("change") && !action.equals("step"))
                throw new CommandException("Invalid action argument");

            if (!action.equals("stop") && !action.equals("float"))
            {
                if (cmd.isNull("speed"))
                    throw new CommandException("Speed argument is missing");
                else
                    speed = cmd.getInt("speed");
            }

            if (action.equals("step"))
            {
                if (cmd.isNull("step"))
                    throw new CommandException("Step argument is missing");
                else
                    step = cmd.getInt("step");
            }

            switch (action)
            {
                case "start":

                    if (name.equals("both"))
                        robot.startMotor(speed, Motor.BOTH);
                    else if (name.equals("left"))
                        robot.startMotor(speed, Motor.LEFT);
                    else if (name.equals("right"))
                        robot.startMotor(speed, Motor.RIGHT);

                    break;

                case "stop":

                    if (name.equals("both"))
                        robot.stopMotor(true, Motor.BOTH);
                    else if (name.equals("left"))
                        robot.stopMotor(true, Motor.LEFT);
                    else if (name.equals("right"))
                        robot.stopMotor(true, Motor.RIGHT);
                    break;

                case "float":

                    if (name.equals("both"))
                        robot.stopMotor(false, Motor.BOTH);
                    else if (name.equals("left"))
                        robot.stopMotor(false, Motor.LEFT);
                    else if (name.equals("right"))
                        robot.stopMotor(false, Motor.RIGHT);
                    break;

                case "step":

                    if (name.equals("both"))
                        robot.stepMotor(speed, step, true, Motor.BOTH);
                    else if (name.equals("left"))
                        robot.stepMotor(speed, step, true, Motor.LEFT);
                    else if (name.equals("right"))
                        robot.stepMotor(speed, step, true, Motor.RIGHT);
                    break;
            }
        }

        return responseMessage;
    }

    private String functionalityCall(JSONObject req) throws CommandException
    {
        final Functionality func;
        
        String functionality, action;
        
        try
        {
            functionality = req.getString("name").toLowerCase();
            action        = req.getString("action").toLowerCase();
        } catch (JSONException e)
        {
            return e.getMessage();
        }

        switch (functionality)
        {
            case "mapping":

                if(action.equals("start"))
                {
                    if(functionalitiesThread.containsKey("mapping"))
                        return "Mapping functionality already started";

                    func = new MappingMemory(robot);
                    
                    // Add a listener for the robot and obstacles coordinates
                    ((MappingMemory) func).addCoordinatesReceivedListener(new MappingMemory.CoordinatesReceivedListener()
                    {
                        @Override
                        public void coordinatesReceived(MappingMemory.MapObject p)
                        {
                            fireEvent(this.getClass(), "coordinate_update", p);
                        }
                    });
                    
                }else if(action.equals("stop"))
                {
                    if(functionalitiesThread.containsKey("mapping")){
                        functionalitiesThread.get("mapping").stop();
                        functionalitiesThread.remove("mapping");
                    }
                    
                    return "ok";
                }else{
                    return "Bad action name for the mapping functionality";
                }
                break;

            default:
                throw new CommandException("Bad functionality argument");
        }

        // Start the functionality in a thread 
        Thread funcThread = new Thread()
        {
            @Override
            public void run()
            {
                func.execute();
            }
        };

        funcThread.start();

        // Save the thread in a list
        functionalitiesThread.put(functionality, funcThread);

        return "ok";
    }

    public boolean isConnected()
    {
        return (robot != null);
    }

    @Override
    public void addObserver(Observer o)
    {
        observers.add(o);
    }

    @Override
    public void deleteObserver(Observer o)
    {
        if(o != null && observers.contains(o))
            observers.remove(o);
    }

    @Override
    public void fireEvent(Class emitter, String event, Object value)
    {
        for(Observer o : observers)
        {
            if(o != null)
                o.triggerEvent(emitter, event, value);
        }
    }
}

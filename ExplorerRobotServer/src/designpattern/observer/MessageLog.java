package designpattern.observer;

/**
 *
 * @author Jordan
 */
public class MessageLog
{
    public static final int STATE = 1;
    public static final int INFO  = 2;
    public static final int WARN  = 3;
    public static final int ERROR = 4;
    public static final int DEBUG = 5;
    
    public int type;
    public String msg;
    public Class emitter;

    private MessageLog(Class emitter, int type, String msg)
    {
        this.type    = type;
        this.msg     = msg;
        this.emitter = (emitter != null) ? emitter : Object.class;
    }
    
    public static MessageLog info(Class emitter, String msg)
    {
        return new MessageLog(emitter, INFO, msg);
    }
    
    public static MessageLog state(Class emitter, String msg)
    {
        return new MessageLog(emitter, STATE, msg);
    }
    
    public static MessageLog warn(Class emitter, String msg)
    {
        return new MessageLog(emitter, WARN, msg);
    }
    
    public static MessageLog error(Class emitter, String msg)
    {
        return new MessageLog(emitter, ERROR, msg);
    }
    
    public static MessageLog debug(Class emitter, String msg)
    {
        return new MessageLog(emitter, DEBUG, msg);
    }
}

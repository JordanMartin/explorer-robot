package designpattern.observer;

/**
 *
 * @author Jordan
 */
public interface Observer
{
    public void triggerEvent(Class emitter, String event, Object value);
}

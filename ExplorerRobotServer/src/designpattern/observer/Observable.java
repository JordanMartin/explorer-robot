package designpattern.observer;

/**
 *
 * @author Jordan
 */
public interface Observable
{
    public void addObserver(Observer o);
    public void deleteObserver(Observer o);
    public void fireEvent(Class emitter, String event, Object value);
}

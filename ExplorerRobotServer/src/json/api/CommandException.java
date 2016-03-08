package json.api;

/**
 * 
 * @author Jordan
 */
public class CommandException extends Exception {
    
    CommandException(){
        super();
    }
    
    CommandException(String message){
        super(message);
    }
}

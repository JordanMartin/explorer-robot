package gui;

import designpattern.observer.MessageLog;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;
import org.eclipse.jetty.io.WriterOutputStream;

/**
 *
 * @author Jordan
 */
public class Main
{
    public static void main(String args[])
    {
        final ServerManagerGUI gui = new ServerManagerGUI();
        
        
        // Redirect the streams into the gui
        PrintStream out = new PrintStream(new WriterOutputStream(new Writer()
        {
            @Override
            public void write(char[] cbuf, int off, int len) throws IOException
            {
                String msg = Main.toString(cbuf, off, len);
                
                if(msg.equals("\r\n") || msg.equals("") || msg.equals("\n"))
                    return;
                
                gui.triggerEvent(Main.class, "log", MessageLog.debug(Main.class, msg));
            }

            @Override
            public void flush() throws IOException{}
            @Override
            public void close() throws IOException{}
        }));
        
        PrintStream err = new PrintStream(new WriterOutputStream(new Writer()
        {
            @Override
            public void write(char[] cbuf, int off, int len) throws IOException
            {
                String msg = Main.toString(cbuf, off, len);
                
                if(msg.equals("\r\n") || msg.equals("") || msg.equals("\n"))
                    return;
                
                gui.triggerEvent(Main.class, "log", MessageLog.error(Main.class, msg));
            }

            @Override
            public void flush() throws IOException{}
            @Override
            public void close() throws IOException{}
        }));
        
        System.setOut(out);
        System.setErr(err);
                
        gui.setVisible(true);
        gui.setLocationRelativeTo(null);
    }    
    
    private static String toString(char[] cbuf, int off, int len)
    {
        StringBuilder str = new StringBuilder();
        
        for(int i = off; i < len; i++)
            str.append(cbuf[i]);
        
            
        return str.toString();
    }
}

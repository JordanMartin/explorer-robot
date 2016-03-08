package server;

import designpattern.observer.MessageLog;
import designpattern.observer.Observable;
import designpattern.observer.Observer;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeRequest;
import org.eclipse.jetty.websocket.servlet.ServletUpgradeResponse;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;


public class ServerContainer implements Observable
{
    
    private final List<Observer> observers;
    private final Server server;
    
    public ServerContainer()
    {
        observers = new ArrayList();
        server = new Server();
        server.setStopAtShutdown(true);
        initServer();
    }
    
    private void initServer()
    {
        ServletHandler handler = new ServletHandler();
        
        final ServerContainer thisServerContainer = this;
        
        /****
         * Listen on a root path for the mini http server
         * Address : http://adr:port
         ****/
        handler.addServletWithMapping(new ServletHolder(new SimpleHttpServerServlet(this)), "/*");
        
        /****
         * Listen for websocket api connection
         * Address : ws://adr:port/ws/api
         ****/ 
        handler.addServletWithMapping(new ServletHolder(new WebSocketServlet(){

            @Override
            public void configure(WebSocketServletFactory factory)
            {
                factory.setCreator(new WebSocketCreator()
                {

                    @Override
                    public Object createWebSocket(ServletUpgradeRequest sur, ServletUpgradeResponse sur1)
                    {
                        return new JSONApiWebsocket(thisServerContainer);
                    }
                });
            }
        }), "/ws/api/*");
        
        /****
         * Listen for videostream brodcaster/viewers connection
         * Adresse : ws://adr:port/ws/videostream
         ****/
        handler.addServletWithMapping(new ServletHolder(new WebSocketServlet()
        {

            @Override
            public void configure(WebSocketServletFactory factory)
            {
                // Increase the maximum size of message to exchange images
                factory.getPolicy().setMaxBinaryMessageSize(3145728);
                factory.getPolicy().setMaxTextMessageSize(3145728);

                factory.setCreator(new WebSocketCreator(){

                    @Override
                    public Object createWebSocket(ServletUpgradeRequest sur, ServletUpgradeResponse sur1)
                    {
                        return new VideoStreamWebsocket(thisServerContainer);
                    }
                });
            }
        }), "/ws/videostream/*");        
       
        // Add the servlets
        server.setHandler(handler);
    }   
    
    /**
     * Set the port where the server will listen
     *
     * @param port the port number
     */
    public void setListeningPort(int port){
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(port);
        server.addConnector(connector);
    }
    
    /**
     * Start listening for client
     */
    public void start() 
    {        
        try
        {
            server.start();
            String url = server.getURI().toString();
            url = url.substring(0, url.length()-1);
            
            String jsonApiWsUrl  = "/ws/api";
            String httpServerUrl = "/";
            String videoStreamWsUrl = "/ws/videostream";
            
            fireEvent(this.getClass(), "log", MessageLog.state(this.getClass(), "Server started"));
            fireEvent(this.getClass(), "log", MessageLog.info(this.getClass(), "Server listen on : " + url));
            fireEvent(this.getClass(), "state", url);
            
            fireEvent(this.getClass(), "httpServerState", httpServerUrl);
            fireEvent(this.getClass(), "log", MessageLog.info(this.getClass(), "HTTP Server listen on : " + url + httpServerUrl));
            
            fireEvent(this.getClass(), "jsonApiWsState", jsonApiWsUrl);
            fireEvent(this.getClass(), "log", MessageLog.info(this.getClass(), "JSON API listen on : " + url + jsonApiWsUrl));
            
            fireEvent(this.getClass(), "videoStreamWsState", videoStreamWsUrl);
            fireEvent(this.getClass(), "log", MessageLog.info(this.getClass(), "Video stream listen on : " + url + videoStreamWsUrl));
        } catch (Exception e)
        {
            fireEvent(this.getClass(), "log", MessageLog.error(this.getClass(), e.getMessage()));
        }
    }
    
    /**
     * Stop the server
     */
    public void stop()
    {
        try
        {
            for(Connector c : server.getConnectors())
                c.shutdown();
            
            for(Handler h : server.getHandlers())
                h.stop();
            
            server.stop();
            fireEvent(this.getClass(), "log", MessageLog.state(this.getClass(), "Server stopped"));
            fireEvent(this.getClass(), "state", false);
            fireEvent(this.getClass(), "jsonApiWsState", false);
            fireEvent(this.getClass(), "videoStreamWsState", false);
            fireEvent(this.getClass(), "httpServerState", false);
        } catch (Exception e)
        {
            fireEvent(this.getClass(), "log", MessageLog.error(this.getClass(), e.getMessage()));
        } 
    }
    
    public boolean isStarted()
    {
        return server.isStarted();
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
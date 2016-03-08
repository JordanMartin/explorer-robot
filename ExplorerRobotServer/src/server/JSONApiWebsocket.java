package server;

import asawoo.avatar.explorerrobot.functionalities.MappingMemory.MapObject;
import designpattern.observer.MessageLog;
import designpattern.observer.Observer;
import json.api.JsonAPI;
import java.io.IOException;
import java.util.HashMap;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;


/**
 * This class is the websocket interface between client and api api
 * 
 * @author Jordan
 */
public class JSONApiWebsocket extends WebSocketAdapter implements Observer
{
    private static final HashMap<Integer, Session> clients = new HashMap();
    private static int NEXT_ID = 1;
    
    private int clientId;
    private String clientRemoteAddress;
    
    private static JsonAPI api = null;
    
    private final ServerContainer server;
    
    public JSONApiWebsocket(ServerContainer server)
    {
        this.server = server;
    }
            
    @Override
    public void onWebSocketConnect(Session session)
    {
        super.onWebSocketConnect(session);
        
        // Set no connection timeout
        session.setIdleTimeout(0);
        
        clientId = pickId();
        clientRemoteAddress = session.getRemoteAddress().toString();
        
        clients.put(clientId, session);
        
        server.fireEvent(this.getClass(), "log", MessageLog.info(this.getClass(), "New client : " + clientRemoteAddress));
        server.fireEvent(this.getClass(), "clientNumber", clients.size());
        
        if(api == null)
        {
            api = new JsonAPI();
            server.fireEvent(this.getClass(), "robotState", true);
        }
        
        api.addObserver(this);
    }
    
    @Override
    public void onWebSocketText(String request)
    {
        server.fireEvent(this.getClass(), "log", MessageLog.debug(this.getClass(), request.toString()));
        
        String response = api.doRequest(request);
        
        try {
            getSession().getRemote().sendString(response);
        } catch (IOException e) {
            server.fireEvent(this.getClass(), "log", MessageLog.info(this.getClass(), "Error when send api response to client : " + e.getMessage()));
        }
    }
    
    @Override
    public void onWebSocketClose(int statusCode, String reason)
    {
        super.onWebSocketClose(statusCode, reason); // set current session to null
        
        api.deleteObserver(this);
        clients.remove(clientId);
        
        server.fireEvent(this.getClass(), "log", MessageLog.info(this.getClass(), "[id#" + clientId + "] " + clientRemoteAddress + " (" + statusCode + " : " + reason + ")"));
        server.fireEvent(this.getClass(), "clientNumber", clients.size());
    }
    
    @Override
    public void onWebSocketError(Throwable cause)
    {
        server.fireEvent(this.getClass(), "log", MessageLog.error(this.getClass(), cause.getMessage()));
    }
    
    /**
     * Return a unique id 
     * @return int the id
     */
    private static int pickId(){
        return NEXT_ID++;        
    }

    @Override
    public void triggerEvent(Class emitter, String event, Object value) 
    {
        if(event.equals("coordinate_update"))
        {
            try
            {
                this.getSession().getRemote().sendString(((MapObject)value).toJsonString());
            } catch (Exception e)
            {
                server.fireEvent(this.getClass(), "log", MessageLog.error(this.getClass(), e.getMessage()));
            }
        }else{
            server.fireEvent(emitter, event, value);
        }
    }
}
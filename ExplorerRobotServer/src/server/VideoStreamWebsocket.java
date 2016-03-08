package server;


import designpattern.observer.MessageLog;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.json.me.JSONException;
import org.json.me.JSONObject;

/**
 * 
 * 
 * @author Jonathan
 */
public class VideoStreamWebsocket extends WebSocketAdapter
{
     
    static int NEXT_ID = 1;
    
    private int clientId;
    private String clientRemoteAddress;
    private boolean isBroadcaster = false;
    private Session session;
    
    
    private static Session broadcaster = null;
    // This variable must be synchronized 
    private static final List<Session> viewers = new ArrayList();
    
    
    private final ServerContainer server;
    
    public VideoStreamWebsocket(ServerContainer server)
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
        this.session = getSession();
    }
    
    @Override
    public void onWebSocketText(String request) {
        
        try {
            JSONObject json = new JSONObject(request);
            
            if(json.has("action")) {
                if(json.getString("action").toLowerCase().equals("config")) {
                    try {
                        broadcaster.getRemote().sendString(request);
                    } catch (IOException ex) {
                    }
                }
                else{
                    try {
                        getRemote().sendString("Type of the action must be config");
                    } catch (IOException ex) {
                    }
                }
            }
            else if(json.has("type"))
            {
                switch(json.getString("type").toLowerCase())
                {
                    case "broadcaster":

                        try {
                            if (broadcaster != null) 
                                broadcaster.disconnect();
                            
                        } catch (IOException ex) {
                        }

                        isBroadcaster = true;
                        broadcaster = getSession();
                        server.fireEvent(this.getClass(), "log", MessageLog.info(this.getClass(), "Broadcaster connected : [id#" + clientId + "] " + clientRemoteAddress));
                        server.fireEvent(this.getClass(), "broadcasterConnected", true);
                        break;

                    case "viewer":
                        synchronized (viewers)
                        {
                            viewers.add(getSession());
                        }
                        
                        server.fireEvent(this.getClass(), "log", MessageLog.info(this.getClass(), "New viewer connected : [id#" + clientId + "] " + clientRemoteAddress));
                        server.fireEvent(this.getClass(), "viewerNumber", viewers.size());
                        
                        break;
                    
                    default:
                        try
                        {
                            getRemote().sendString("{\"error\" : \"Type must be viewer or boradcaster\"}");
                        } catch (IOException ex)
                        {
                        }
                        break;
                }
            } else if (json.has("new_image"))
            {

                String imgString = json.getString("new_image");

                synchronized (viewers)
                {
                    for (Session viewer : viewers)
                    {
                        try
                        {
                            if (viewer.isOpen())
                                viewer.getRemote().sendString(imgString);
                        } catch (IOException ex)
                        {
                        }
                    }
                }
            }

        } catch (JSONException e) {
            try
            {
                getRemote().sendString("{\"error\" : \"" + e.getMessage() + "\"}");
            } catch (IOException ex)
            {
            }
        }
    }
    
    @Override
    public void onWebSocketBinary(byte[] payload, int offset, int len) {
        
        synchronized(viewers)
        {
            for(Session viewer : viewers)
            {
                try {
                    if(viewer.isOpen())
                        viewer.getRemote().sendBytes(ByteBuffer.wrap(payload, offset, len));
                } catch (IOException ex) {
                }
            }
        }
    }
    
    @Override
    public void onWebSocketClose(int statusCode, String reason)
    {
        super.onWebSocketClose(statusCode, reason); // set current session to null
 
        if(isBroadcaster)
        {
            broadcaster = null;
            server.fireEvent(this.getClass(), "log", MessageLog.info(this.getClass(), "Broadcaster disconnected: [id#" + clientId + "] " + clientRemoteAddress));
            server.fireEvent(this.getClass(), "broadcasterConnected", false);
        }else
        {
            synchronized(viewers)
            {
                if(viewers.contains(session))
                    viewers.remove(session);
            }
            server.fireEvent(this.getClass(), "log", MessageLog.info(this.getClass(), "Viewer disconnected: [id#" + clientId + "] " + clientRemoteAddress));
        }
        
        server.fireEvent(this.getClass(), "viewerNumber", viewers.size());
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
}

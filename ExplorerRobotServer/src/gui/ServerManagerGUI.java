package gui;

import designpattern.observer.MessageLog;
import designpattern.observer.Observer;
import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.DefaultCaret;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import json.api.JsonAPI;
import server.JSONApiWebsocket;
import server.ServerContainer;
import server.SimpleHttpServerServlet;
import server.VideoStreamWebsocket;

/**
 * GUI monitor for the websocket server
 * 
 * @author Jordan
 */
public class ServerManagerGUI extends javax.swing.JFrame implements Observer
{
    private ServerContainer server;
    
    /**
     * Creates new form ServerManagerGUI
     */
    public ServerManagerGUI()
    {
        initComponents();
    }

    @Override
    public void triggerEvent(Class emitter, String event, Object value)
    {
        if(event.equals("log") && value instanceof MessageLog)
            logMessage((MessageLog)value);
        else if(emitter.equals(ServerContainer.class))
            eventFromServerContainer(event, value);
        else if(emitter.equals(JSONApiWebsocket.class))
            eventFromJSONAPI(event, value);
        else if(emitter.equals(SimpleHttpServerServlet.class))
            eventFromHttpServer(event, value);
        else if(emitter.equals(VideoStreamWebsocket.class))
            eventFromVideoStream(event, value);
         else
            logMessage(MessageLog.info(null, event + " : " + value.toString()));
    }
    
    private void eventFromJSONAPI(String event, Object value)
    {
        switch(event)
        {
            case "wsState":
                jsonApiWSState.setText((boolean)value ? "WS listen" : "Offline");
                break;
                
            case "clientNumber":
                numberClientJsonApiField.setText(value.toString());
                break;
                
            case "robotState":
                robotStateField.setText(((boolean)value) ? "Instanciated" : "Not instanciated");
                break;
        }
    }
    
    private void eventFromServerContainer(String event, Object value)
    {
        switch(event)
        {
            case "state":
                if(value instanceof String)
                {
                    serverContainerState.setForeground(new Color(61,179,61));
                    serverContainerState.setText("Server is listening on : " + (String)value);
                }
                else
                {
                    serverContainerState.setForeground(Color.red);
                    serverContainerState.setText("Server is not running");
                }
                break;
                
            case "jsonApiWsState":
                if(value instanceof String)
                {
                    jsonApiWSState.setForeground(new Color(61, 179, 61));
                    jsonApiWSState.setText("Online (path : " + (String) value + ")");
                } else
                {
                    jsonApiWSState.setForeground(Color.red);
                    jsonApiWSState.setText("Offline");
                    numberClientJsonApiField.setText("0");
                }
                break;

            case "httpServerState":
                
                if (value instanceof String)
                {
                    httpServerStatus.setForeground(new Color(61, 179, 61));
                    httpServerStatus.setText("Online (path : " + (String) value + ")");
                } else
                {
                    httpServerStatus.setForeground(Color.red);
                    httpServerStatus.setText("Offline");
                }
                break;

            case "videoStreamWsState":
                if (value instanceof String)
                {
                    videoStreamWsStatus.setForeground(new Color(61, 179, 61));
                    videoStreamWsStatus.setText("Online (path : " + (String) value + ")");
                } else
                {
                    videoStreamWsStatus.setForeground(Color.red);
                    videoStreamWsStatus.setText("Offline");
                    numberViewerField.setText("0");
                    broadcasterStatusField.setText("Not connected");
                }
                break;
        }
    }

    private void eventFromVideoStream(String event, Object value)
    {
        switch(event)
        {
            case "viewerNumber":
                numberViewerField.setText(value.toString());
                break;
                
            case "broadcasterConnected":
                broadcasterStatusField.setText(((boolean)value) ? "Connected" : "Not connected");
                break;
        }
    }
    
    private void eventFromHttpServer(String event, Object value)
    {
        switch(event)
        {
            case "wsState":
                jsonApiWSState.setText((boolean)value ? "Listen" : "Offline");
                break;
        }
    }
    
    private void razGui()
    {
        
    }
    
    private synchronized void logMessage(MessageLog msg)
    {
        Color c = Color.white;
        
        switch(msg.type)
        {
            case MessageLog.INFO:
                c = new Color(80, 80, 255);
                break;
                
            case MessageLog.STATE:
                c = Color.green;
                break;
                
            case MessageLog.WARN:
                c = Color.orange;
                break;
                
            case MessageLog.ERROR:
                c = new Color(255, 80, 80);
                break;
                
            case MessageLog.DEBUG:
                c = Color.white;
                break;
        }
        
        String emitterName;
        
        if(msg.emitter.equals(ServerContainer.class))
            emitterName = "Server container";
        else if(msg.emitter.equals(JSONApiWebsocket.class))
            emitterName = "JsonAPI WS";
        else if(msg.emitter.equals(JsonAPI.class))
            emitterName = "JsonAPI";
        else if(msg.emitter.equals(SimpleHttpServerServlet.class))
            emitterName = "HTTP Server";
        else if(msg.emitter.equals(VideoStreamWebsocket.class))
            emitterName = "Video Stream WS";
        else
            emitterName = msg.emitter.getSimpleName();
        
        Calendar cal = Calendar.getInstance();
    	cal.getTime();
    	SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        
        appendToPane(consoleArea, " [" + sdf.format(cal.getTime()) + " | " + emitterName + "] ", Color.lightGray);
        appendToPane(consoleArea, msg.msg + "\n", c);
    }
    
    private void appendToPane(JTextPane tp, String msg, Color c)
    {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

        aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);
        aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Consolas");
        
        int len = tp.getDocument().getLength();
        tp.setCaretPosition(len);
        tp.setCharacterAttributes(aset, false);
        tp.replaceSelection(msg);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        copyrightField = new javax.swing.JLabel();
        windowTitleField = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        startServerBtn = new javax.swing.JButton();
        serverContainerState = new javax.swing.JLabel();
        stopServerBtn = new javax.swing.JButton();
        serverContainerPort = new javax.swing.JTextField();
        ServerContainerPortField = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        consoleScrollPane = new javax.swing.JScrollPane();
        consoleArea = new javax.swing.JTextPane();
        jButton1 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        robotStateField = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        numberClientJsonApiField = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jsonApiWSState = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        numberViewerField = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        broadcasterStatusField = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        videoStreamWsStatus = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        httpServerStatus = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(957, 559));

        copyrightField.setFont(new java.awt.Font("Corbel", 0, 18)); // NOI18N
        copyrightField.setText("Jonathan Taws & Jordan Martin - 2014");

        windowTitleField.setFont(new java.awt.Font("Corbel", 1, 36)); // NOI18N
        windowTitleField.setForeground(new java.awt.Color(102, 102, 102));
        windowTitleField.setText("Explorer Robot Server");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Server Container", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Consolas", 1, 14))); // NOI18N

        startServerBtn.setText("Start server");
        startServerBtn.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                startServerBtnActionPerformed(evt);
            }
        });

        serverContainerState.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        serverContainerState.setForeground(new java.awt.Color(255, 51, 51));
        serverContainerState.setText("Server is not running");

        stopServerBtn.setText("Stop server");
        stopServerBtn.setEnabled(false);
        stopServerBtn.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                stopServerBtnActionPerformed(evt);
            }
        });

        serverContainerPort.setFont(new java.awt.Font("Consolas", 0, 18)); // NOI18N
        serverContainerPort.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        serverContainerPort.setText("8888");

        ServerContainerPortField.setFont(new java.awt.Font("Arial", 0, 15)); // NOI18N
        ServerContainerPortField.setText("Port :");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(stopServerBtn)
                .addGap(18, 18, 18)
                .addComponent(startServerBtn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(ServerContainerPortField)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(serverContainerPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(serverContainerState)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(serverContainerPort, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(startServerBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(stopServerBtn)
                .addComponent(ServerContainerPortField, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(serverContainerState)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Console", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Consolas", 1, 14))); // NOI18N
        jPanel3.setToolTipText("");

        consoleScrollPane.setBorder(null);

        consoleArea.setBackground(new java.awt.Color(51, 51, 51));
        consoleArea.setForeground(new java.awt.Color(255, 255, 255));
        consoleArea.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        consoleScrollPane.setViewportView(consoleArea);
        ((DefaultCaret)consoleArea.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        jButton1.setFont(new java.awt.Font("Consolas", 0, 10)); // NOI18N
        jButton1.setText("clear console");
        jButton1.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(814, Short.MAX_VALUE)
                .addComponent(jButton1))
            .addComponent(consoleScrollPane)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(consoleScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "JSON API Websocket", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Consolas", 1, 14))); // NOI18N

        jLabel3.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel3.setText("Explorer robot :");

        robotStateField.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        robotStateField.setText("Not instanciated");

        jLabel4.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel4.setText("Clients connected :");

        numberClientJsonApiField.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        numberClientJsonApiField.setText("0");

        jLabel10.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel10.setText("Status :");

        jsonApiWSState.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jsonApiWSState.setText("Offline");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(33, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(robotStateField)
                .addGap(28, 28, 28))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(numberClientJsonApiField))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jsonApiWSState)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(jsonApiWSState))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(robotStateField))
                .addGap(11, 11, 11)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(numberClientJsonApiField))
                .addContainerGap())
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Video Stream Websocket", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Consolas", 1, 14))); // NOI18N

        jLabel6.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel6.setText("Broadcaster :");

        numberViewerField.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        numberViewerField.setForeground(new java.awt.Color(255, 51, 51));
        numberViewerField.setText("0");

        jLabel8.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel8.setText("Viewers :");

        broadcasterStatusField.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        broadcasterStatusField.setText("Not connected");

        jLabel11.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel11.setText("Status :");

        videoStreamWsStatus.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        videoStreamWsStatus.setText("Offline");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(46, 46, 46)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(numberViewerField))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(broadcasterStatusField))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(videoStreamWsStatus)))
                .addContainerGap(27, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(videoStreamWsStatus))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(broadcasterStatusField)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(numberViewerField)
                    .addComponent(jLabel8))
                .addContainerGap())
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "HTTP Server", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Consolas", 1, 14))); // NOI18N

        jLabel7.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel7.setText("Status :");

        httpServerStatus.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        httpServerStatus.setText("Offline");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(httpServerStatus)
                .addContainerGap(126, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(httpServerStatus))
                .addContainerGap(74, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 87, Short.MAX_VALUE)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 87, Short.MAX_VALUE)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(copyrightField)
                            .addComponent(windowTitleField))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(windowTitleField)
                .addGap(24, 24, 24)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(copyrightField)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void startServerBtnActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_startServerBtnActionPerformed
    {//GEN-HEADEREND:event_startServerBtnActionPerformed
        int port;
        
        try
        {
            port = Integer.parseInt(serverContainerPort.getText());
            
            if (port < 1024 || port > 49151)
                throw new Exception();
        } catch (Exception ex)
        {
            JOptionPane.showMessageDialog(this, "The port must be a integer between 1024 and 49151\nPlease enter a correct port");
            return;
        }
        
        server = new ServerContainer();
        server.addObserver(this);
        
        server.setListeningPort(port);
        server.start();
        
        if(server.isStarted())
        {
            ServerContainerPortField.setVisible(false);
            serverContainerPort.setVisible(false);

            stopServerBtn.setEnabled(true);
            startServerBtn.setEnabled(false);
        }
    }//GEN-LAST:event_startServerBtnActionPerformed

    private void stopServerBtnActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_stopServerBtnActionPerformed
    {//GEN-HEADEREND:event_stopServerBtnActionPerformed
        server.stop();
        stopServerBtn.setEnabled(false);
        startServerBtn.setEnabled(true);
        ServerContainerPortField.setVisible(true);
        serverContainerPort.setVisible(true);
    }//GEN-LAST:event_stopServerBtnActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButton1ActionPerformed
    {//GEN-HEADEREND:event_jButton1ActionPerformed
        consoleArea.setText("");
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
   

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel ServerContainerPortField;
    private javax.swing.JLabel broadcasterStatusField;
    private javax.swing.JTextPane consoleArea;
    private javax.swing.JScrollPane consoleScrollPane;
    private javax.swing.JLabel copyrightField;
    private javax.swing.JLabel httpServerStatus;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JLabel jsonApiWSState;
    private javax.swing.JLabel numberClientJsonApiField;
    private javax.swing.JLabel numberViewerField;
    private javax.swing.JLabel robotStateField;
    private javax.swing.JTextField serverContainerPort;
    private javax.swing.JLabel serverContainerState;
    private javax.swing.JButton startServerBtn;
    private javax.swing.JButton stopServerBtn;
    private javax.swing.JLabel videoStreamWsStatus;
    private javax.swing.JLabel windowTitleField;
    // End of variables declaration//GEN-END:variables
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package json.api;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author Jordan
 */
public class JsonAPITester extends javax.swing.JFrame implements ActionListener {
    
    JsonAPI nxt;
    
        /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(JsonAPITester.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(JsonAPITester.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(JsonAPITester.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(JsonAPITester.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        JsonAPITester ui = new JsonAPITester();
        ui.setVisible(true);
    }
    
    /**
     * Creates new form CommandTester
     */
    public JsonAPITester() {
        initComponents();
        
        setLocationRelativeTo(null);
        
        connecterbt.addActionListener(this);
        connecterusb.addActionListener(this);
        deconnecter.addActionListener(this);
        avancer.addActionListener(this);
        reculer.addActionListener(this);
        gauche.addActionListener(this);
        droite.addActionListener(this);
        stop.addActionListener(this);
        
        boussole.addActionListener(this);
        sonardevant.addActionListener(this);
        sonardroit.addActionListener(this);
        sonargauche.addActionListener(this);
        allsensors.addActionListener(this);
        
        nxt = new JsonAPI();
    }
    
    private void log(String str){
        console.append("\n receive : " + str);
    }
    
    @Override
    public void actionPerformed(ActionEvent a){
        
        if(a.getSource() == connecterusb){            
            log(nxt.doRequest("{\"action\": \"connect\", \"mode\": \"usb\"}"));
            log(nxt.doRequest("{\"action\": \"init\", \"params\": {"
                + "\"leftUltrasonicSensorPort\": 2,"
                + "\"frontUltrasonicSensorPort\": 1,"
                + "\"rightUltrasonicSensorPort\": 3,"
                + "\"compassSensorPort\": 4,"
                + "\"motorLeftPort\": \"C\","
                + "\"motorRightPort\": \"A\",} }"));
            
        }        
        else if(a.getSource() == connecterbt){
            log(nxt.doRequest("{\"action\": \"connect\", \"mode\": \"bt\"}"));
            log(nxt.doRequest("{\"action\": \"init\", \"params\": {"
                + "\"leftUltrasonicSensorPort\": 2,"
                + "\"frontUltrasonicSensorPort\": 1,"
                + "\"rightUltrasonicSensorPort\": 3,"
                + "\"compassSensorPort\": 4,"
                + "\"motorLeftPort\": \"C\","
                + "\"motorRightPort\": \"A\",} }"));
        }
        
        else if(a.getSource() == deconnecter){
            log(nxt.doRequest("{\"action\": \"disconnect\"}"));
        }
        
        else if(a.getSource() == avancer){
            log(nxt.doRequest("{\"motor_command\": [{\"action\": \"start\", \"name\": \"both\", \"speed\": " + speed.getValue() + "}]}"));
        }
        
        else if(a.getSource() == reculer){
            log(nxt.doRequest("{\"motor_command\": [{\"action\": \"start\", \"name\": \"both\", \"speed\": " + (-speed.getValue()) + "}]}"));
        }
        
        else if(a.getSource() == stop){
            log(nxt.doRequest("{\"motor_command\": [{\"action\": \"float\", \"name\": \"both\"}]}"));
        }
        
        else if(a.getSource() == droite){
            log(nxt.doRequest("{\"motor_command\": [{\"action\": \"start\", \"name\": \"left\", \"speed\": " + speed.getValue() + "},{\"action\": \"start\", \"name\": \"right\", \"speed\": " + (-speed.getValue()) + "}]}"));
        }
        
        else if(a.getSource() == gauche){
            log(nxt.doRequest("{\"motor_command\": [{\"action\": \"start\", \"name\": \"right\", \"speed\": " + speed.getValue() + "},{\"action\": \"start\", \"name\": \"left\", \"speed\": " + (-speed.getValue()) + "}]}"));
        }
        
        else if(a.getSource() == boussole){
            log(nxt.doRequest("{\"sensor_request\": [{\"name\": \"compass\"}]}"));
        }
        
        else if(a.getSource() == sonardevant){
            log(nxt.doRequest("{\"sensor_request\": [{\"name\": \"frontultrasonic\"}]}"));
        }
        
        else if(a.getSource() == sonardroit){
            log(nxt.doRequest("{\"sensor_request\": [{\"name\": \"rightultrasonic\"}]}"));
        }
        
        else if(a.getSource() == sonargauche){
            log(nxt.doRequest("{\"sensor_request\": [{\"name\": \"leftultrasonic\"}]}"));
        }
        
        else if(a.getSource() == allsensors){
            log(nxt.doRequest("{\"sensor_request\": [{\"name\": \"leftultrasonic\"},{\"name\": \"rightultrasonic\"},{\"name\": \"frontultrasonic\"},{\"name\": \"compass\"}]}"));
        }        
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

        avancer = new javax.swing.JButton();
        reculer = new javax.swing.JButton();
        droite = new javax.swing.JButton();
        gauche = new javax.swing.JButton();
        connecterusb = new javax.swing.JButton();
        stop = new javax.swing.JButton();
        deconnecter = new javax.swing.JButton();
        connecterbt = new javax.swing.JButton();
        speed = new javax.swing.JSlider();
        jScrollPane1 = new javax.swing.JScrollPane();
        console = new javax.swing.JTextArea();
        sonardevant = new javax.swing.JButton();
        sonardroit = new javax.swing.JButton();
        sonargauche = new javax.swing.JButton();
        boussole = new javax.swing.JButton();
        allsensors = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("NXT Controller");

        avancer.setText("Avancer");

        reculer.setText("Reculer");

        droite.setText("Droite");
        droite.setPreferredSize(new java.awt.Dimension(60, 15));

        gauche.setText("Gauche");

        connecterusb.setText("Connecter USB");

        stop.setText("Stop");

        deconnecter.setText("Deconnecter");

        connecterbt.setText("Connecter BT");

        speed.setMaximum(1000);
        speed.setValue(100);

        console.setColumns(20);
        console.setRows(5);
        jScrollPane1.setViewportView(console);

        sonardevant.setText("Sonar devant");

        sonardroit.setText("Sonar Droit");

        sonargauche.setText("Sonar Gauche");

        boussole.setText("Boussole");

        allsensors.setText("Tous les capteurs");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(gauche, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(14, 14, 14)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(stop, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(avancer, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(reculer, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(droite, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(connecterbt)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(connecterusb)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(deconnecter))
                            .addComponent(speed, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 378, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 51, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(sonargauche)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(sonardroit, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(sonardevant)
                                        .addGap(53, 53, 53))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(boussole, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(54, 54, 54)))
                                .addGap(23, 23, 23))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(allsensors, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(47, 47, 47))))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(connecterusb)
                    .addComponent(connecterbt)
                    .addComponent(deconnecter))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(60, 60, 60)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(avancer, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(stop, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(reculer, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(droite, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(gauche, javax.swing.GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(sonardevant, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(sonardroit, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(sonargauche, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(boussole, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(speed, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(allsensors, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(17, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton allsensors;
    private javax.swing.JButton avancer;
    private javax.swing.JButton boussole;
    private javax.swing.JButton connecterbt;
    private javax.swing.JButton connecterusb;
    private javax.swing.JTextArea console;
    private javax.swing.JButton deconnecter;
    private javax.swing.JButton droite;
    private javax.swing.JButton gauche;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton reculer;
    private javax.swing.JButton sonardevant;
    private javax.swing.JButton sonardroit;
    private javax.swing.JButton sonargauche;
    private javax.swing.JSlider speed;
    private javax.swing.JButton stop;
    // End of variables declaration//GEN-END:variables
}

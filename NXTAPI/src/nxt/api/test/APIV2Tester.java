/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nxt.api.test;

import java.io.IOException;
import lejos.pc.comm.NXTCommException;
import nxt.api.*;

/**
 *
 * @author Jordan
 */
public class APIV2Tester {

    public static void main(String agrs[]) throws NXTException, IOException, InterruptedException {

        APIV2Tester api = new APIV2Tester();
    }
    

    NXT nxt;
    public int normalSpeed = 300;
    
    public APIV2Tester() {

//        calibrateCompass();        
//        System.out.println("Calib ok");
//        
        
//        while(true)
//            System.out.println("current : " + nxt.compass.getDegree());
//        
//        System.out.println("current : " + nxt.compass.getDegree());
//        halfTurn();
//        Thread.sleep(1000);
//        System.out.println("current : " + nxt.compass.getDegree());

//        turnToCompassDegree(150);
        
//        
        connect();
        straightForward();
        int minObstacle = 20;
        
        while(true)
        {
            int front = nxt.frontSonar.getDistance();
            int right = nxt.rightSonar.getDistance();
            int left = nxt.leftSonar.getDistance();
            
            System.out.println("left : " + left + "\nfront : " + front + "\nright : " + right + "\n");
            
            if(front < minObstacle)
            {
                if(right < minObstacle && left < minObstacle)
                    halfTurn();
                else if(right < minObstacle)
                    turnLeft();
                else
                    turnRight();

            }else{
                straightForward();
            }
        }
        
        /*
        try {
            nxt.setSensorPort("compass", NXT.ID_PORT_4);
            nxt.setSensorPort("leftSonar", NXT.ID_PORT_3);
            nxt.setSensorPort("frontSonar", NXT.ID_PORT_2);
            nxt.setSensorPort("rightSonar", NXT.ID_PORT_1);

            nxt.setMotorPort("left", NXT.ID_PORT_A);
            nxt.setMotorPort("right", NXT.ID_PORT_C);
        } catch (NXTException e) {
            System.err.println("Error : " + e.getMessage());
        }

        int degree, left, right, front;

        int leftTacho, rightTacho;

        for (int i = 0; i < 1050; i++) {

            leftTacho = (int) nxt.leftMotor.getMaxSpeed();
            
            
            System.out.println("Tacho left : " + leftTacho);
*/
//            degree = nxt.compass.getDegree();
//            left = nxt.leftSonar.getDistance();
//            right = nxt.rightSonar.getDistance();
//            front = nxt.frontSonar.getDistance();
//            
//            System.out.println("Compass : " + degree + "°");
//            System.out.print(right + " ");
//            System.out.print(left + " ");
//            System.out.print(front + "\n");

        }
    
    
    public void connect(){
        NXTSearcher searcher = new NXTSearcher();
        try {
            if (!searcher.connect("bt", "NXT", "0016530F2C26")) {
                System.err.println("Not found");
                System.exit(0);
            } else {
                System.out.println("Connected");
            }
        } catch (NXTCommException e) {
            System.err.println("Error : " + e.getMessage());
            System.exit(0);
        }
        
        try{
            nxt = new NXT(searcher.getConnection());
            nxt.setMotorPort("left", 'C');
            nxt.setMotorPort("right", 'A');
            nxt.setSensorPort("compass", 4);
            nxt.setSensorPort("leftSonar", 2);
            nxt.setSensorPort("frontSonar", 1);
            nxt.setSensorPort("rightSonar", 3);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public void disconnect(){
        try {
            nxt.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
 
    public void straightForward() {
        
        nxt.leftMotor.setSpeed(normalSpeed);
        nxt.rightMotor.setSpeed(normalSpeed);
        nxt.leftMotor.forward();
        nxt.rightMotor.forward();
        
    }
    
    public void straightBackward() {
        nxt.leftMotor.setSpeed(normalSpeed);
        nxt.rightMotor.setSpeed(normalSpeed);
        nxt.leftMotor.backward();
        nxt.rightMotor.backward();
    }
    
    public void turnToCompassDegree(int degree) {

        double marginError = 0;

        int turnSpeed = 300;

        double minDegree = modulo((degree - marginError), 360);
        double maxDegree = modulo((degree + marginError), 360);

//        System.out.println("Range : " + minDegree + "," + maxDegree);
        int currentDegree = nxt.compass.getDegree();

        double percentComplete;
        int speed;
        boolean shortTurnIsRight;

        double degreeLeft, degreeTodo;

        if (modulo(currentDegree - degree, 360) < (modulo(degree - currentDegree, 360))) {
            degreeTodo = modulo(currentDegree - degree, 360);
        } else {
            degreeTodo = modulo(degree - currentDegree, 360);
        }
        
        if(degreeTodo < 20)
            turnSpeed = 100;

        while (currentDegree < minDegree || currentDegree > maxDegree) {
            
            if (modulo(currentDegree - degree, 360) < (modulo(degree - currentDegree, 360))) {
                shortTurnIsRight = false;
                degreeLeft = modulo(currentDegree - degree, 360);
            } else {
                shortTurnIsRight = true;
                degreeLeft = modulo(degree - currentDegree, 360);
            }

            percentComplete = degreeLeft / degreeTodo;
            speed = (int) ((percentComplete < 0.3) ? 10 + percentComplete * turnSpeed * 3 : turnSpeed);

            nxt.rightMotor.setSpeed(speed);
            nxt.leftMotor.setSpeed(speed);

//            System.out.println("Objectif : " + degree + " (" + degreeLeft + "/" + degreeTodo + "°)  complete : " + (100.0 - (double)Math.round(percentComplete*1000)/10.0) + "%   speed : " + speed);
//            System.out.println("current " + currentDegree);
            
            if (shortTurnIsRight) {
                nxt.leftMotor.forward();
                nxt.rightMotor.backward();
            } else {
                nxt.leftMotor.backward();
                nxt.rightMotor.forward();
            }

            currentDegree = nxt.compass.getDegree();
        }

        nxt.rightMotor.stop();
        nxt.leftMotor.stop();

    }

    public void halfTurn() {
        int currentDegree = nxt.compass.getDegree();
        turnToCompassDegree((int) modulo((currentDegree + 180), 360));
    }

    public double modulo(double n, int mod) {
        
        double value;
        if (((n % mod) < 0)) value = (n < 0) ? mod + n : mod - n;
        else value = n % mod;
        
//        System.out.println("=======\n" + n + "%" + mod + " = " + value + "\n========");
        
        return value;
    }

    public void calibrateCompass() {

        nxt.compass.startCalibration();

        int speed = 100;

        nxt.rightMotor.setSpeed(speed);
        nxt.leftMotor.setSpeed(speed);

        nxt.rightMotor.rotate(-360 * 8, true);
        nxt.leftMotor.rotate(360 * 8, false);

        nxt.compass.stopCalibration();

    }

    public void turnLeft() {
        int currentDegree = nxt.compass.getDegree();
        turnToCompassDegree((int) modulo((currentDegree - 90), 360));
    }

    public void turnRight() {
        int currentDegree = nxt.compass.getDegree();
        turnToCompassDegree((int) modulo((currentDegree + 90), 360));
    }

    void stopMotors() {
        
        nxt.rightMotor.stop();
        nxt.leftMotor.stop();
    }
}

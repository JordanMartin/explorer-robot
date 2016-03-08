package nxt.api;



import lejos.pc.comm.NXTCommException;

/**
 *
 * @author Jordan
 */
public class RequestPerformanceTest {
    
    public static void main(String args[]){
        
        NXTSearcher searcher = new NXTSearcher();
        try {
            if(!searcher.connect("bt", "NXT", "0016530F2C26")){
                System.err.println("Not found");
                System.exit(0);
            }                
            else
                System.out.println("Connected");
        } catch (NXTCommException e) {
            System.err.println("Error : " + e.getMessage());
            System.exit(0);
        }
        
        NXT nxt = new NXT(searcher.getConnection());        
        
        UltrasonicSensor leftSensor = new UltrasonicSensor(nxt, 0);
        UltrasonicSensor frontSensor = new UltrasonicSensor(nxt, 1);
        UltrasonicSensor rightSensor = new UltrasonicSensor(nxt, 2);
        
        CompassSensor compass = new CompassSensor(nxt, 3);
        
        System.out.println("Début du test");
        
        long before = System.currentTimeMillis();
        int count = 0;
        
        int degree, left, right, front;
        
        double sum = 0;
        double measureCount = 0;
        
        for(int i = 0; i < 50; i++){
            
            degree = compass.getDegree();
            left = leftSensor.getDistance();
            right = rightSensor.getDistance();
            front = frontSensor.getDistance();
            
//            System.out.println("Compass : " + degree + "°");
//            System.out.print(right + " ");
//            System.out.print(left + " ");
//            System.out.print(front + "\n");

            if ((System.currentTimeMillis() - before) > 1000) {
                System.out.print(count + "/s, ");
                sum += count;
                measureCount++;
                count = 0;
                before = System.currentTimeMillis();
            }else
                count++;
        }
        
        System.out.println("Average request per seconde : " + (sum/measureCount));
    }
    
}

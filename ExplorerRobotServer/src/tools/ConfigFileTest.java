package tools;

/**
 * Test of the ConfFile class
 * @author jordan
 */

public class ConfigFileTest {
    
    public static void main(String arg[]){
        ConfigFile c;
        
        /*
        try {            
            c = new ConfigFile("c:/my_new_conf_file.cnf");
            c.set("name", "NXT");
            c.set("SensorPort1", "1");
            c.set("SensorPort2", "2");
            c.delete("name");
            c.save("Configuration of the robot");            
            System.out.println(c.getString("nom"));
        } catch (ConfigFileException e) {
            System.out.println("ConfigFileException : " + e.getMessage());
            System.exit(0);
        } */
        
        try {            
            c = new ConfigFile();
            c.askFilePathWithGui();
        } catch (ConfigFileException e) {
            System.out.println("ConfigFileException : " + e.getMessage());
            System.exit(0);
        } 
        
        
    }    
}

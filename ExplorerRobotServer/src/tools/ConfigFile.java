package tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import javax.swing.JFileChooser;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Easy configuration file managment
 *
 * @date 15 October 2013
 * @author jordan
 */
public class ConfigFile {
    
    File configFile   = null;
    Properties config = null;
    boolean created   = false;
    String filePath   = null;
    
    
    
    /**
     * Load the configuration file or create it if it does not exists
     *
     * @param filePath
     * @throws nxt.api.tools.ConfigFileException
     */
    public ConfigFile(String filePath) throws ConfigFileException {
        this.filePath = filePath;
        openFile();
    }

    public ConfigFile() {
    }
    
    public String askFilePathWithGui() throws ConfigFileException {
        
         try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            
        }
                
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select the file");
        int returnVal = chooser.showOpenDialog(null);        
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            filePath = chooser.getSelectedFile().getAbsolutePath();
            openFile();
            
            return filePath;
        } else {
            filePath = null;
            return null;
        }      
    }
    
    public void openFile() throws ConfigFileException {
        
        if(filePath == null)
            throw new ConfigFileException("File selection canceled");
        
        configFile = new File(filePath);
        
        if (!configFile.exists()) {
            try {
                if (!configFile.createNewFile()) {
                    throw new ConfigFileException("Failed to create the configuration file");
                }
                
                created = true;
            } catch (IOException e) {
                throw new ConfigFileException("Failed to create the configuration file : " + e.getMessage());
            }
        }
        
        config = new Properties();
        
        try {
            try (FileInputStream file = new FileInputStream(configFile)) {
                config.load(file);
                file.close();
            }
        } catch (IOException e) {
            config = null;
            throw new ConfigFileException("Failed to load the configuration file : " + e.getMessage());
        }        
    }

    /**
     * Get the string value of the given property
     *
     * @param property The property name
     * @param defaultValue The default value if the property is not found
     * @return The value of the given property or the default given or null if
     * it is not found
     */
    public String getString(String property, String defaultValue) {
        return config.getProperty(property, defaultValue);
    }
    
    public String getString(String property) {
        return getString(property, null);
    }

    /**
     * Get the char value of the given property
     *
     * @param property The property name
     * @param defaultValue The default value if the property is not found
     * @return The value of the given property or the default given or null if
     * it is not found
     */
    public Character getChar(String property, Character defaultValue) {
        String val = config.getProperty(property, defaultValue.toString());
        return (val == null) ? null : val.charAt(0);
    }
    
    public Character getChar(String property) {
        return getChar(property, null);
    }

    /**
     * Get the Integer value of the given property
     *
     * @param property The property name
     * @param defaultValue The default value if the property is not found
     * @return The value of the given property or the default given or null if
     * it is not found
     * @throws nxt.api.tools.ConfigFileException
     */
    public Integer getInt(String property, Integer defaultValue) throws ConfigFileException {
        
        String stringVal = config.getProperty(property, defaultValue.toString());
        
        try {
            return (stringVal == null) ? null : Integer.parseInt(stringVal);
        } catch (NumberFormatException e) {            
            throw new ConfigFileException("Property : \"" + property + "\" is not a valid integer");
        }
    }
    
    public Integer getInt(String property) throws ConfigFileException {
        return getInt(property, null);
    }

    /**
     * Set the property with the given value in the configuration file. Theses
     * modifications are temporary, to save definitively in file use save()
     * method. If the property already exists it is replace.
     *
     * @param key
     * @param val
     * @throws nxt.api.tools.ConfigFileException
     */
    public void set(String key, String val) throws ConfigFileException {        
        config.setProperty(key, val);
    }

    /**
     * Save all changes in the configuration file
     *
     * @param fileComment The comment writen at the top of file
     * @throws ConfigFileException
     */
    public void save(String fileComment) throws ConfigFileException {        
        try {
            config.store(new FileOutputStream(configFile), fileComment);            
        } catch (IOException e) {
            throw new ConfigFileException("Can't save the file. IOException : " + e.getMessage());
        }
    }

    /**
     * Delete the given property Theses modifications are temporary, to save
     * definitively in file use save() method.
     *
     * @param key
     */
    public void delete(String key) {
        config.remove(key);
    }
    
}

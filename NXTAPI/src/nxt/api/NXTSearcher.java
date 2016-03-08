package nxt.api;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommException;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTInfo;

/**
 *
 * @author Jordan
 */
public class NXTSearcher {
    
    NXTComm connection = null;
    
    /**
     * Search and connect to the NXT with the given name
     * @param mode connection mode (usb or bt)
     * @param name the friendly name of NXT
     * @return
     * @throws NXTCommException 
     */
    public boolean connect(String mode, String name) throws NXTCommException {
        return connect(mode, name, null);        
    }
    
    /**
     * Connect to the NXT with given name and address
     * @param mode
     * @param name
     * @param address
     * @return
     * @throws NXTCommException 
     */
    public boolean connect(String mode, String name, String address) throws NXTCommException {
        
        switch(mode.toLowerCase()){
            case "bt":
                connection = NXTCommFactory.createNXTComm(NXTCommFactory.BLUETOOTH);
                break;
            case "usb":
                connection = NXTCommFactory.createNXTComm(NXTCommFactory.USB);
                break;
            
            default:
                throw new NXTCommException("Unknown connection mode");
        }    
        
        NXTInfo nxtToOpen = null;
        
        // If no address given, search by name
        if(address == null){
            
            NXTInfo[] nxtinfos = connection.search(name);

            for (NXTInfo nxtinfo : nxtinfos) 
            {
                System.out.println(nxtinfo.deviceAddress);
                if (nxtinfo.name.equals(name)) 
                {
                    nxtToOpen = nxtinfo;
                    break;
                }
            }
            
        }else // If address given direct connection (faster)            
           nxtToOpen = new NXTInfo(NXTComm.LCP, name, address);
        
        // NXT not found with the given name
        if(address == null && nxtToOpen == null)
            throw new NXTCommException("The brick : \"" + name + "\" was not found over " + mode);
                
        if(!connection.open(nxtToOpen))
            throw new NXTCommException("The brick : \"" + name + "\" was found but the connection failed");
        
        return true;
    }  
    
    /**
     * 
     * @return the instance of NXTComm connection
     */
    public NXTComm getConnection(){
        return connection;
    }
}

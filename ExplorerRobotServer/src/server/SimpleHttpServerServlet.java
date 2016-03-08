package server;

import designpattern.observer.MessageLog;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Simple http server. 
 * It can get html, css, javascript and images files.
 * Warning : All text files (so not images) must be encoded in utf-8 
 * 
 * @author Jordan
 */
public class SimpleHttpServerServlet extends HttpServlet
{
    private final String webDirectory = System.getProperty("user.dir") + File.separator + "www" + File.separator;
    
    private final ServerContainer server;
    
    public SimpleHttpServerServlet(ServerContainer server)
    {
        this.server = server;
    }
    
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) 
    {
        server.fireEvent(this.getClass(), "log", MessageLog.debug(this.getClass(), request.toString()));
        
        OutputStream os;

        try{
            os = response.getOutputStream();
        }catch(IOException e){
            server.fireEvent(this.getClass(), "log", MessageLog.error(this.getClass(), e.getMessage()));
            return;
        }   

        try {      

            String requestURI = request.getRequestURI();
            String requestedFile = webDirectory + requestURI.substring(1);

            if(requestURI.charAt(requestURI.length()-1) == '/')
                requestedFile = (requestURI.length() > 1) ? requestedFile + "index.htm" : requestedFile;

            int i = requestedFile.lastIndexOf('.');
            String fileExtension = (i >= 0) ? requestedFile.substring(i + 1).toLowerCase() : "";

            if (Files.exists(Paths.get(requestedFile)) && !Files.isDirectory(Paths.get(requestedFile))) {      

                switch(fileExtension){

                    case "html":                        
                    case "css":
                        response.setContentType("text/" + fileExtension);
                        break;

                    case "js":
                        response.setContentType("application/javascript");
                        break;

                    case "htm":
                        response.setContentType("text/html");
                        break;

                    case "jpg":
                    case "jpeg":
                    case "png":
                    case "gif":
                        response.setContentType("image/" + fileExtension);  
                        break;

                    default:
                        throw new Exception("File not supported");
                }


                response.setCharacterEncoding("utf-8");

                switch(fileExtension){
                    case "html":                        
                    case "css":                            
                    case "js":                          
                    case "htm":                             
                        os.write(readTextFile(requestedFile).getBytes(Charset.forName("UTF-8")));                            
                        break;

                    case "jpg":
                    case "jpeg":
                    case "png":
                    case "gif":                             
                        os.write(readRawFile(requestedFile));                             
                }

                response.setStatus(HttpServletResponse.SC_OK);                    

            } else {
                response.setContentType("text/html");  
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                os.write(print("<h1>404 - Not found : " + request.getRequestURI() + "</h1>"));
                os.write(print("<h2>Files must be in : <b>\"" + webDirectory + "\"</b> directory</h2>"));

            }

        }catch(Exception e){
            try{
                response.setContentType("text/html");  
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                os.write(print("<h1>500 - Internal error : " + request.getRequestURI() + "</h1>"));
                os.write(print(e.getMessage()));
            }catch(Exception ex){}                
        }
    }

    public byte [] print(String str) throws UnsupportedEncodingException {
        return str.getBytes("utf-8");
    }


    public String readTextFile(String path) throws Exception
    {
        StringBuilder content;
        try (BufferedReader file = new BufferedReader(new FileReader(path))) {
            content = new StringBuilder();
            String currentLine;
            
            while ((currentLine = file.readLine()) != null)
                content.append(currentLine).append('\n');
        }            

        return content.toString();
    }

    public byte[] readRawFile(String path) throws Exception
    {
        byte[] bytes;
        try (BufferedReader file = new BufferedReader(new FileReader(path))) {
            bytes = Files.readAllBytes(Paths.get(path));
        }
        
        return bytes;
    }    
} 
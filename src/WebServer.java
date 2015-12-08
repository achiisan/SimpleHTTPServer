   // File Name GreetingServer.java

import java.net.*;
import java.text.SimpleDateFormat;
import java.io.*;
import java.util.Date;
import java.util.LinkedList;
import java.util.TimeZone;
import java.util.regex.Pattern;

public class WebServer extends Thread
{   
   private Socket server;
   
   private static ServerSocket serverSocket;
  
   public WebServer(Socket server) throws IOException
   {
         this.server = server ;
         //this.out = new DataOutputStream(this.server.getOutputStream());
   }

   

   public static void main(String [] args) throws IOException
   {
   	
      try
      {
        int port = Integer.parseInt(args[0]);
        waitConn(port);
      }catch(ArrayIndexOutOfBoundsException e)
      {
         System.out.println("Usage: java GreetingServer <port no.> ");
         e.printStackTrace();
      }
   }


public static void waitConn(int port){
	System.out.println("WEB SERVER STARTED...");
	  try
      {
	
    while(true){
    	serverSocket = new ServerSocket(port);
         
          System.out.println("Waiting for client on port " + serverSocket.getLocalPort() + "...");
    
          Socket tempserver = serverSocket.accept();
          WebServer s = new WebServer(tempserver);
          Thread st = new Thread(s);
          st.start();
          
        	
        
            System.out.println("Awaiting for more users...");
            
        serverSocket.close();
      
    }
    }catch(IOException e){
         	e.printStackTrace();
        
          
      
      
        }

}

public static String generateResponse(BufferedReader file, String filename, String table){
	String rh = null;
	final Date currentTime = new Date();

	final SimpleDateFormat sdf =
	        new SimpleDateFormat("EEE, MMM d, yyyy hh:mm:ss a z");

	// Give it to me in GMT time.
	sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
	String[] type = filename.split("\\.");

	String mimetype = "";
	
	
	if(type[1].equals("html")){
		mimetype = "text/html";
	}else if(type[1].equals("css")){
		mimetype = "text/css";
	}else if(type[1].equals("js")){
		mimetype = "application/javascript";
	}
	
	rh = "HTTP/1.1 ";
	if(file != null){
		rh = rh+"200 OK\n";
	}else{
		rh = rh+"404 Not Found\n";
	}
	
	
	int filelength = 0;
	
	String response = "";
	String buf = "";
	
	if(file != null){
		try {
			while((buf = file.readLine())!= null){
				if(Pattern.matches("\\s*<body>\\s*",buf ) && table != null){
					response = response + buf+"\n";
					
					response = response + table + "\n";
				}else{
				response = response+buf+"\n";
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}else{
		response="<h1>Not Found</h1><p>The Document has not been found on the Server</p>";
	}
	
	
	rh = rh+"Connection: keep-alive \nContent-Length: "+response.length()+"\nContent-Type: "+mimetype+"\nAccept-Ranges: bytes\nDate: "+sdf.format(currentTime)+"\nKeep-Alive: timeout=5, max=100\nServer: Apachedaw\n\n";
	
	rh = rh+response;
	
	

	return rh;
	
}



public void run()  {
    InputStreamReader in;
    String table = null;
	try {
		
		in = new InputStreamReader(
		        this.server.getInputStream());
	     BufferedReader inFromClient =
                 new BufferedReader(in);
         String clientSentence = inFromClient.readLine();
//         String clientSentence2;
//                     
//       while(( clientSentence2 = inFromClient.readLine()) != null){
//        	clientSentence += clientSentence2+"\n";
//        	if(clientSentence2.equals("")){
//        		break;
//        	}
//       }  
       
       System.out.println(clientSentence);
         
       
      if(clientSentence != null){
         String[] st = clientSentence.split(" ");
         FileReader in2 = null;
         
         String st4 = st[1];
         String [] st5  = st4.split("\\?");
         
         if(st5.length == 2){
        	 table = "<table border=3><tr><th>Parameter</th><th>Value</th></tr>";
        	String [] s = st5[1].split("\\&");
        	
        	for(String k : s){
        		String [] d = k.split("=");
        		for(String df: d){
        			System.out.println(df);
        		}
        		table = table + "<tr><td>"+d[0]+"</td><td>"+d[1]+"</td></tr>";
        	}
        	table = table+"</table>";
         }else{
        	 table = null;
         }
         
         String st3 = st5[0];
    	 
         if(st3.equals("/")){
        	   in2 = new FileReader("index.html");
        	   st3 = "index.html";
         }else{
        	 try{
        	   in2 = new FileReader(st3.replace("/", ""));
        	 }catch(Exception e){}
         }	 
         String response;
         
         	if(in2 != null){
        	BufferedReader filegetter  = new BufferedReader(in2);
        	if(table != null){
        	 response =  generateResponse(filegetter, st3, table);
        	}else{
        		response =  generateResponse(filegetter, st3, null);
        	}
        	 filegetter.close();
         	}else{
         		 response =  generateResponse(null, st3,null);
         	}
        	
        	 DataOutputStream os;
			try {
				os = new DataOutputStream(this.server.getOutputStream());
			 	 os.writeUTF(response);
			 	 //System.out.println(response);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			this.server.close();
        
           
           
          }

	}catch(Exception e){
		e.printStackTrace();
	}
}
}
/**
a) Socket server = serverSocket.accept();
b) serverSocket = new ServerSocket(port);
**/

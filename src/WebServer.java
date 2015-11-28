   // File Name GreetingServer.java

import java.net.*;
import java.text.SimpleDateFormat;
import java.io.*;
import java.util.Date;
import java.util.LinkedList;
import java.util.TimeZone;

public class WebServer extends Thread
{
	
   
   private Socket server;
   private static LinkedList<WebServer> users = new LinkedList<WebServer>();
   private static ServerSocket serverSocket;
   private DataOutputStream out;  
   public String nickname = "DEFAULT NICKNAME";
   
   
   public WebServer(Socket server) throws IOException
   {
         this.server = server ;
         this.out = new DataOutputStream(this.server.getOutputStream());
   }

   public void broadcast(String message, String addr){
   
        for(WebServer t: users){
            try{
           
            String addr2 = (String) t.server.getRemoteSocketAddress().toString();     
            if(!addr.equals(addr2)){
                t.out.writeUTF("\n"+this.nickname+": "+message);
            }
            }catch(Exception e){
            }
            
        }
   }
   public void run()
   {
        DataInputStream in;
      boolean connected = true;
    
      while(connected)
      {
         try
         {

            System.out.println("Client Connected: " + this.server.getRemoteSocketAddress().toString());
            //GET THE NICKNAME
              in = new DataInputStream(this.server.getInputStream());
             this.nickname = in.readUTF();
             //broadcast(in.readUTF(), this.server.getRemoteSocketAddress().toString());
            
            broadcast(this.nickname+" Joined the Chat", this.server.getRemoteSocketAddress().toString());
                
            while (true){
            /* Read data from the ClientSocket */
             in = new DataInputStream(this.server.getInputStream());
           // System.out.println(in.readUTF());
             String temp = in.readUTF();
             final byte[] utf8Bytes = temp.getBytes("UTF-8");
             System.out.println(utf8Bytes.length);
       
            broadcast(temp, this.server.getRemoteSocketAddress().toString());
            
           
           }
         }catch(SocketTimeoutException s)
         {
            System.out.println("Socket timed out!");
            break;
         }catch(IOException e)
         {
            //e.printStackTrace();
            System.out.println("Server ended connection to"+ server.getRemoteSocketAddress());
            break;
         }
      } 
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
         InputStreamReader in = new InputStreamReader(
        tempserver.getInputStream());
         BufferedReader inFromClient =
                 new BufferedReader(in);
         String clientSentence = inFromClient.readLine();
       
         //while(clientSentence != null){
        //	 System.out.println(clientSentence);
        //	 clientSentence = inFromClient.readLine();
         //}  
         
         String[] st = clientSentence.split(" ");
         FileReader in2 = null;
         String st3 = st[1].replace("/", "");
    	 System.out.println(st3);
    	 
         if(st[1].equals("/")){
        	   in2 = new FileReader("index.html");
        	   st3 = "index.html";
         }else{
        	 try{
        	   in2 = new FileReader(st[1].replace("/", ""));
        	 }catch(Exception e){}
         }	 
         String response;
         	if(in2 != null){
        	BufferedReader filegetter  = new BufferedReader(in2);
        	 response =  generateResponse(filegetter, st3);
        	 filegetter.close();
         	}else{
         		 response =  generateResponse(null, st3);
         	}
        	
        	 DataOutputStream os = new DataOutputStream(tempserver.getOutputStream());
        	 os.writeUTF(response);
        	 
        	 tempserver.close();
         
         
         
         
             
             
             System.out.println(clientSentence);
            System.out.println("Awaiting for more users...");
        serverSocket.close();
    }
    }catch(IOException e){
         	e.printStackTrace();
        
          
       
       
      
        }

}

public static String generateResponse(BufferedReader file, String filename){
	String rh = null;
	final Date currentTime = new Date();

	final SimpleDateFormat sdf =
	        new SimpleDateFormat("EEE, MMM d, yyyy hh:mm:ss a z");

	// Give it to me in GMT time.
	sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
	String[] type = filename.split("\\.");
	System.out.println(filename);
	for(String s: type){
		System.out.println(s);
	}
	String mimetype = "";
	
	
	if(type[1].equals("html")){
		mimetype = "text/html";
	}else if(type[1].equals("css")){
		mimetype = "text/css";
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
				response = response+buf+"\n";
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
	System.out.println(rh);

	return rh;
	
}

}


/**
a) Socket server = serverSocket.accept();
b) serverSocket = new ServerSocket(port);
**/

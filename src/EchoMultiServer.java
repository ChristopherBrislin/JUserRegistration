import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

public class EchoMultiServer {
    private SSLServerSocket serverSocket;
    static String inputLine;
    static LogWriter logger = new LogWriter();
    
	
 
    public void start(int port){
    	ServerSocketFactory factory = SSLServerSocketFactory.getDefault();
    	try {
       serverSocket = (SSLServerSocket) factory.createServerSocket(port); 
    		//serverSocket = new ServerSocket(port);
        while (true)
            new EchoClientHandler(serverSocket.accept()).start();
    	}catch(IOException ex) {
    		ex.printStackTrace();
    	}
    	
    }
 
    public void stop() {
    	try {
        serverSocket.close();
    	} catch(IOException ex) {
    		ex.printStackTrace();
    	}
    }
 
    private static class EchoClientHandler extends Thread {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
       
 
        public EchoClientHandler(Socket socket) {
            this.clientSocket = socket;
        }
 
        public void run(){
        	logger.logFile();
        	try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(
              new InputStreamReader(clientSocket.getInputStream()));
            
            inputLine = in.readLine();
           
   		String payloadArray[] = inputLine.split(Main.SPLIT_TOKEN);
   		
   		/*
   		 * payloadArray[0] = Client Software Type
   		 * payloadArray[1] = Client Software Version
   		 * payloadArray[2] = Command Type
   		 * payloadArray[3] = UUID
   		 * payloadArray[4] = Email/ Username / New Email
   		 * payloadArray[5] = Password / Old Email
   		 */

   		if (Main.DEBUG) {
   			for (String payloadValue : payloadArray) {
   				System.out.print(payloadValue + ", ");
   			}
   			System.out.println("");
   		}
   		
   		int checkSum = Integer.parseInt(payloadArray[(payloadArray.length - 1)]);
   		Float verCheck = Float.parseFloat(payloadArray[1]);
   		
   		if ((inputLine.length() - (payloadArray[(payloadArray.length - 1)]).length()) == checkSum
   				&& payloadArray[0].contentEquals("LogBook") && verCheck >= Main.ACC_SOFT_VERSION) {
   			if (Main.DEBUG)System.out.println("Good String...");
   			
   			DatabaseConnector dbConnect = new DatabaseConnector();
   			dbConnect.connect(Main.DB_CON_DRIVER, Main.DB_HOST, Main.DB_PORT, Main.DB_NAME, Main.DB_USERNAME, Main.DB_PASSWORD );
   			

   			switch (payloadArray[2]) {
   			case "register":

   				if (dbConnect.createNewUser(payloadArray[3], payloadArray[4], payloadArray[5])) {
   					logger.writeLog(payloadArray[1], payloadArray[2], payloadArray[3], payloadArray[4]);
   					out.println("registerSuccess::uuid="+payloadArray[3]);
   				}
   				dbConnect.connClose();
   				if(Main.DEBUG)System.out.println("Database Server connection closed.");
   				break;

   			case "remove":

   				if (dbConnect.removeUser(payloadArray[4], payloadArray[3])) {
   					logger.writeLog(payloadArray[1], payloadArray[2], payloadArray[3], payloadArray[4]);
   					out.println("removeSuccess::uuid="+payloadArray[3]);
   				}

   				dbConnect.connClose();
   				if(Main.DEBUG)System.out.println("Database Server connection closed.");
   				break;

   			case "rename":

   				if (dbConnect.renameUser(payloadArray[4], payloadArray[5], payloadArray[3])) {
   					logger.writeLog(payloadArray[1], payloadArray[2], payloadArray[3], payloadArray[4]);
   					out.println("renameSuccess::newName="+payloadArray[5]);
   				}
   				dbConnect.connClose();
   				if(Main.DEBUG)System.out.println("Database Server connection closed.");
   				break;

   			case "newPassword":

   				if (dbConnect.newPassword(payloadArray[5], payloadArray[4], payloadArray[3])) {
   					logger.writeLog(payloadArray[1], payloadArray[2], payloadArray[3], payloadArray[4]);
   					out.println("newPwordSuccess::uuid="+payloadArray[3]);
   				}
   				dbConnect.connClose();
   				if(Main.DEBUG)System.out.println("Database Server connection closed.");
   				break;

   			case "forgotPassword":

   				String newPassword = PasswordGenerator.generateRandomPassword(12);
   				if (Main.DEBUG) System.out.println("Random Password: " + newPassword);
   				

   				if (dbConnect.newPassword(newPassword, payloadArray[4], payloadArray[3])) {
   					logger.writeLog(payloadArray[1], payloadArray[2], payloadArray[3], payloadArray[4]);
   					
   					// Reset Success
   					EmailSender resetEmail = new EmailSender();
   					String contentString = "\n Dear " + payloadArray[4] + ","
   							+ "\n\n Your password has been reset. Here is your new password:\n\n"
   							+ newPassword
   							+ "\n\n Please be aware this is a temporary password that will expire in 24 hours, please reset it.";
   					String subjectString = "The Logbook: Password Reset";
   					resetEmail.sendMessage(payloadArray[4], contentString, subjectString);
   					out.println("pwordResetSuccess::uuid="+payloadArray[3]);
   					
   				}
   				dbConnect.connClose();
   				if(Main.DEBUG)System.out.println("Database Server connection closed.");
   				break;

   			default:
   				if(Main.DEBUG)System.out.println("Bad Operation Command - terminating connection");
   				break; // Invalid command!! Terminate connection!!

   			}

   		} else if (Main.DEBUG) {
   			System.out.println("Bad String..");
   		}

   		// End of test section

   	
            
            in.close();
            out.close();
            clientSocket.close();
        	}catch(IOException ex) {
        		ex.printStackTrace();
        	}
    }
}
}

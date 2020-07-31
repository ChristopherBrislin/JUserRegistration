import java.io.File;

/**
 * 
 */

/**
 * Christopher Brislin 26 Jul 2020 JUserRegistration
 */
public class Main extends Thread{

	public static String DB_USERNAME;
	public static String DB_PASSWORD;
	public static String DB_HOST;
	public static String DB_PORT;
	public static String DB_CON_DRIVER;
	public static String DB_NAME;

	public static int SERVER_PORT;
	public static String SPLIT_TOKEN;
	public static Float ACC_SOFT_VERSION;
	
	public static String SEND_ADDRESS;
	public static String SEND_EMAIL_PASS;
	public static String SMTP_HOST; 
	public static String SMTP_PORT; 
	//private static String CONF_VERSION;
	public static Boolean DEBUG;
	public static String KEYSTORE_PASS;
	public static String TRUSTSTORE_PASS;

	public static void main(String[] args) {
		
		// boolean register = true;
	   
		 File currentDir = new File("");
			ConfLoader confLoader = new ConfLoader();
			confLoader.loadConfig(currentDir.getAbsolutePath()+"/config.properties");
			

		DB_USERNAME = confLoader.readConfig("DATABASE_USERNAME");
		DB_PASSWORD = confLoader.readConfig("DATABASE_PASS");
		DB_HOST = confLoader.readConfig("DATABASE_HOST");
		DB_PORT = confLoader.readConfig("DATABASE_PORT");
		DB_CON_DRIVER = confLoader.readConfig("DATABASE_DRIVER");
		DB_NAME = confLoader.readConfig("DATABASE_NAME");
		SEND_ADDRESS = confLoader.readConfig("SEND_EMAIL_ADDR");

		SERVER_PORT = Integer.parseInt(confLoader.readConfig("SERVER_PORT"));
		SPLIT_TOKEN = confLoader.readConfig("SPLIT_TOKEN");
		ACC_SOFT_VERSION = Float.parseFloat(confLoader.readConfig("CLIENT_SOFT_VER"));
		//CONF_VERSION = confLoader.readConfig("CONFIGURATION_VER");
		SEND_ADDRESS =confLoader.readConfig("SEND_EMAIL_ADDR");;
		SEND_EMAIL_PASS =confLoader.readConfig("SEND_EMAIL_PASS");;
		SMTP_HOST = confLoader.readConfig("SMTP_HOST");; 
		SMTP_PORT= confLoader.readConfig("SMTP_PORT"); 
		KEYSTORE_PASS = confLoader.readConfig("KEYSTORE_PASS");
		TRUSTSTORE_PASS = confLoader.readConfig("TRUSTSTORE_PASS");

		DEBUG = Boolean.parseBoolean(confLoader.readConfig("DEBUG"));
		
		System.setProperty("javax.net.ssl.keyStore", currentDir.getAbsolutePath()+"/ServerKeyStore.jks");
	    System.setProperty("javax.net.ssl.keyStorePassword", KEYSTORE_PASS);
	   System.setProperty("javax.net.ssl.trustStore", currentDir.getAbsolutePath()+"/ClientKeyStore.jks");
	   System.setProperty("javax.net.ssl.trustStorePassword", TRUSTSTORE_PASS);
		
		EchoMultiServer server = new EchoMultiServer();
		//System.out.println()
		server.start(SERVER_PORT);
		
	}

}

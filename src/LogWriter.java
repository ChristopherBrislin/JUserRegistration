import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;

/**
 * 
 */

/**
 * Christopher Brislin 26 Jul 2020 JUserRegistration
 */
public class LogWriter {

	File logfile;
	FileWriter writer;

	public boolean logFile() {
		File currentDir = new File("");
		logfile = new File(currentDir.getAbsolutePath()+"/logFile.txt");
		if (!logfile.exists() && !logfile.canRead() && !logfile.canWrite()) {
			try {
				logfile.createNewFile();
				System.out.println("Logfile created.");
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return true;
	}
	
	public void writeLog(String userVer, String operation, String uuid, String emailAddr ) {
		if(logFile()) {
		try {
		writer = new FileWriter(logfile, true);
		String content = 
				Instant.now().toString() +
				"      " +
				"operation: " + operation + 
				" uuid: " + uuid + 
				" email: " +emailAddr + 
				" user ver: "+ userVer + "\n";
		
		
		writer.write(content);
		writer.flush();
		writer.close();
		System.out.println("Committed to log.");
		
		}catch(IOException ex) {
			ex.printStackTrace();
		}
		
	}
	}

}

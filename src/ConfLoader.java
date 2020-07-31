import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * 
 */

/**
 * Christopher Brislin 26 Jul 2020 JUserRegistration
 */
public class ConfLoader {
	File configFile;
	Properties properties;
	FileReader reader;
	String setting;

	// The Config Loader

	public boolean loadConfig(String configPath) {
		

		configFile = new File(configPath);
		properties = new Properties();
		try {
			reader = new FileReader(configFile);
		} catch (FileNotFoundException ex) {
			System.err.print(ex);
			return false;
		}
		if (configFile.canRead()) {
			return true;
		} else {
			return false;
		}

	}

	public String readConfig(String confName) {

		try {
			properties.load(reader);
			setting = properties.getProperty(confName);

		} catch (FileNotFoundException ex) {

			System.err.print(ex);
		} catch (IOException ix) {
			System.err.print(ix);
		}
		return setting;

	}

}

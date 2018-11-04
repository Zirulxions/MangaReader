package utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
//import java.nio.file.FileSystems;
//import java.nio.file.FileSystems;
import java.util.Properties;

public class PropertiesReader {
	private static PropertiesReader propr = new PropertiesReader();
	private Properties prop = new Properties();
	private InputStream input = null;
	//private String fileDir = System.getProperty("user.dir");
	//private String separator = FileSystems.getDefault().getSeparator();
	
	public PropertiesReader() {
		try {
			//input = new FileInputStream(new File(fileDir+separator+"config.properties"));
			//input = new FileInputStream(new File(System.getProperty("user.dir")+"\\config.properties"));
			//input = new FileInputStream(new File(System.getProperty("user.dir")+"/config.properties"));
			//input = new FileInputStream(new File(System.getProperty("user.dir")+FileSystems.getDefault().getSeparator()+"config.properties"));
			//input = new FileInputStream(new File(System.getProperty("user.dir")+File.separator+"config.properties"));
			//input = getClass().getClassLoader().getResourceAsStream("config.properties");
			input = new FileInputStream(new File("C:\\Users\\Javier Delgado\\Documents\\Workspace Eclipse\\MangaReader1.0\\config.properties"));
			prop.load(input);
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static PropertiesReader getInstance () {
		return propr;
	}
	public String getValue(String key){
		return prop.getProperty(key);
	}
}

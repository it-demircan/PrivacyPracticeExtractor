package services;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class SettingLoader implements ISettingLoader{

	public String getStopWordListPath() throws FileNotFoundException, IOException {
		return loadProperty("stopWordList");
	}
	
	public String getPolicyTreeStructureFile() throws FileNotFoundException, IOException {
		return loadProperty("policyStructureFile");
	}
	
	public String getTrainingDataFolder() throws FileNotFoundException,IOException{
		return loadProperty("trainingDataFolder");
	}
	
	public String getCorpusFile() throws FileNotFoundException, IOException {
		return loadProperty("corpusFile");
	}
	
	public String getPrototypesFile() throws FileNotFoundException, IOException{
		return loadProperty("prototypesFile");
	}
	
	private String loadProperty(String propIdentifier){
		Properties properties = new Properties();
		String probValue = "";
		try(BufferedInputStream stream = new BufferedInputStream(new FileInputStream("src\\miscellaneous\\settings.properties"))){
			properties.load(stream);
			probValue = properties.getProperty(propIdentifier);
		}catch(FileNotFoundException err){
			Logger.error("Property File not found at \\miscellaneous\\settings.properties");
		} catch (IOException e) {
			Logger.error(e.getMessage());
		}
		return probValue;
	}
}

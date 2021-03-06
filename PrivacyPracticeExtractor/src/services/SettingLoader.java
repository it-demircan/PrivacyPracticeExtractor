package services;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class SettingLoader implements ISettingLoader {

	public String getStopWordListPath() throws FileNotFoundException,
			IOException {
		return loadProperty("stopWordList");
	}

	public String getPolicyTreeStructureFile() throws FileNotFoundException,
			IOException {
		return loadProperty("policyStructureFile");
	}

	public String getTrainingDataFolder() throws FileNotFoundException,
			IOException {
		return loadProperty("trainingDataFolder");
	}

	public String getCorpusFile() throws FileNotFoundException, IOException {
		return loadProperty("corpusFile");
	}

	public String getPrototypesFile() throws FileNotFoundException, IOException {
		return loadProperty("prototypesFile");
	}

	public String getTestDataFolder() throws FileNotFoundException, IOException {
		return loadProperty("testDataFolder");
	}

	public String getLabelDocumentCounterFile() throws FileNotFoundException,
			IOException {
		return loadProperty("labelDocumentCounter");
	}

	public int getCompressionTimeOut() throws FileNotFoundException,
			IOException {
		return Integer.parseInt(loadProperty("compressionTimeOutSeconds"));
	}

	public int getFeatureSelectorSize() throws FileNotFoundException,
			IOException {
		return Integer.parseInt(loadProperty("featureSelection"));
	}

	private String loadProperty(String propIdentifier) {
		Properties properties = new Properties();
		String probValue = "";
		try (BufferedInputStream stream = new BufferedInputStream(
				new FileInputStream("./settings.properties"))) {
			properties.load(stream);
			probValue = properties.getProperty(propIdentifier);
		} catch (FileNotFoundException err) {
			Logger.error("Property File settings.properties not found.");
		} catch (IOException e) {
			Logger.error(e.getMessage());
		}
		return probValue;
	}

}

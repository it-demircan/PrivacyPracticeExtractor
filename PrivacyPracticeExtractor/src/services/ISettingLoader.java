package services;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface ISettingLoader {
	public String getStopWordListPath() throws FileNotFoundException, IOException;
	public String getTrainingDataFolder() throws FileNotFoundException,IOException;
	public String getPolicyTreeStructureFile() throws FileNotFoundException, IOException;
	public String getCorpusFile() throws FileNotFoundException, IOException;
	public String getPrototypesFile() throws FileNotFoundException, IOException;
}

package services;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface ISettingLoader {
	public String getStopWordListPath() throws FileNotFoundException, IOException;
}

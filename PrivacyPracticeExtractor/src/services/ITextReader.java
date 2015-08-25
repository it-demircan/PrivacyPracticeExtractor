package services;
import java.io.IOException;

public interface ITextReader {
	/**
	 * Reads a text file and return its content as a string
	 * @param path - Path to file
	 * @return Content as String
	 * @throws IOException
	 */
	public String readText(String path) throws IOException;
}

package services;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class TextReader implements ITextReader{

	/*
	 * (non-Javadoc)
	 * @see services.ITextReader#readText(java.lang.String)
	 */
	public String readText(String path) throws IOException {
		StringBuffer readText = new StringBuffer();
		String readLine = "";
		try(	FileReader fr = new FileReader(path);
				BufferedReader br = new BufferedReader(fr)){
			while((readLine = br.readLine()) != null)
				readText.append(readLine);
		}
		return readText.toString();
	}
}

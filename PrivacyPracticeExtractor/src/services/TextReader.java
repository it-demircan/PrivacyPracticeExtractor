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
			while((readLine = br.readLine()) != null){
				readText.append(readLine);
				//readText.append(" ");
			}
		}
		return readText.toString();
	}
	
	/**
	 * If a text is not ending with a punctuation, this method adds a punctuation.
	 */
	@Override
	public String readTextAndAddPunctuation(String path)
			throws IOException {
		StringBuffer readText = new StringBuffer();
		String readLine = "";
		try(	FileReader fr = new FileReader(path);
				BufferedReader br = new BufferedReader(fr)){
			while((readLine = br.readLine()) != null){
				if(!readLine.endsWith("."))
					readLine = readLine+". ";
				readText.append(readLine);
				//readText.append(" ");
			}
		}
		return readText.toString();
	}
}

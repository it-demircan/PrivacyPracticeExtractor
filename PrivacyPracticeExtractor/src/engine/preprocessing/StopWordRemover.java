package engine.preprocessing;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import services.ISettingLoader;
import services.ITextReader;
import services.Logger;

public class StopWordRemover implements IStopWordRemover{ 
	ITextReader textReader;
	ISettingLoader settingLoader;
	
	boolean loaded;
	List<String> stopWordList;

	public StopWordRemover(ITextReader _textReader, ISettingLoader _settingLoader){
		textReader = _textReader;
		settingLoader = _settingLoader;
		
		loaded = false;
		stopWordList = new LinkedList<String>();
	}
	
	public String removeStopWords(String text) {
		if(!loaded){
			Logger.info("Loading StopWord-List.");
			loadStopWords();
		}
		Logger.info("Removing stop words.");
		for(String stopWord : stopWordList){
			try{
				text = text.replace(stopWord, "");
			}catch(Exception err){
				//Char Seq. not found ->  try next stop word
			}
		}
		Logger.info("Stop words removed.");
		return text;
	}
	
	private void loadStopWords(){
		String path = "";
		String readStopWords ="";
		String splitToken = ";"; //new line
		try{
			path = settingLoader.getStopWordListPath();
			if(path != ""){
				readStopWords = textReader.readText(settingLoader.getStopWordListPath());
				stopWordList = new LinkedList<String>(Arrays.asList(readStopWords.split(splitToken)));	
				loaded = true;
			}
		}catch(FileNotFoundException err){
			Logger.error("Can't find StopWordList-File in relative path: "+ path);
		}catch(IOException ioEx){
			Logger.error("Can't load StopWordList from relative path: "+ path);
		}
	}
}

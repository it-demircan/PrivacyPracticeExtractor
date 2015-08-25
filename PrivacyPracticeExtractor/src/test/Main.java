package test;

import java.util.List;

import engine.preprocessing.IStopWordRemover;
import engine.preprocessing.StopWordRemover;
import services.*;
import engine.preprocessing.*;

import edu.stanford.*;
import edu.stanford.nlp.trees.WordStemmer;

public class Main {
	public static void main(String[] args) {
		ITextReader textReader = new TextReader();
		ISettingLoader settingLoader = new SettingLoader();
		
		//IStopWordRemover stopWordRemover = new StopWordRemover(textReader,settingLoader);
		//Logger.info("Result : " + stopWordRemover.removeStopWords("Hallo you suck"));
		//ITokenizer tokenizer = new Tokenizer();
		//List<String> tokens = tokenizer.tokenize("Hallo du Kukuku");
		//ISentenceSplitter splitter = new SentenceSplitter();
		//List<String> test = splitter.splitSentences("Hallo du Vogel. Ich bin Doof. Wieso?");
		ILemmatizer lemma = new Lemmatizer();
		Stemmer 
		System.out.println(lemma.lemmatize("we are better"));
	}

}

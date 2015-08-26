package test;

import java.util.List;

import model.Text;
import model.WordType;

import engine.preprocessing.IStopWordRemover;
import engine.preprocessing.StopWordRemover;
import services.*;
import engine.preprocessing.*;
import model.*;

import edu.stanford.*;
import edu.stanford.nlp.trees.WordStemmer;

public class Main {
	public static void main(String[] args) {
		ITextReader textReader = new TextReader();
		ISettingLoader settingLoader = new SettingLoader();
		String exampleTxt = "We collect collect the content and other information you provide when you use our Services, including when you sign up for an account, create or share, and message or communicate with others. This can include information in or about the content you provide, such as the location of a photo or the date a file was created. We also collect information about how you use our Services, such as the types of content you view or engage with or the frequency and duration of your activities.";
		//IStopWordRemover stopWordRemover = new StopWordRemover(textReader,settingLoader);
		//Logger.info("Result : " + stopWordRemover.removeStopWords("Hallo you suck"));
		//ITokenizer tokenizer = new Tokenizer();
		//List<String> tokens = tokenizer.tokenize("Hallo du Kukuku");
		//ISentenceSplitter splitter = new SentenceSplitter();
		//List<String> test = splitter.splitSentences("Hallo du Vogel. Ich bin Doof. Wieso?");
		//ILemmatizer lemma = new Lemmatizer();
		//Text myTest = lemma.lemmatizeToText("We collect the content and other information you provide when you use our Services, including when you sign up for an account, create or share, and message or communicate with others. This can include information in or about the content you provide, such as the location of a photo or the date a file was created. We also collect information about how you use our Services, such as the types of content you view or engage with or the frequency and duration of your activities.");
		//System.out.println(myTest.toString(WordType.Complete));
		IStemmer myStemmer = new Stemmer();
		ITokenizer myTokenizer = new Tokenizer();
		IStopWordRemover myRemover = new StopWordRemover(textReader,settingLoader);
		Text tokenedText = myTokenizer.tokenizeToText(exampleTxt);
		myStemmer.stemm(tokenedText);
		myRemover.markStopWords(tokenedText);
		
		Dictionary corpus = new Dictionary();
		corpus.populate(tokenedText);
		
		IVectorizer mapper = new Vectorizer(corpus);
		Vector test = mapper.mapToVector(tokenedText.getSentences().get(0));
		
		System.out.println(corpus.toString());
		System.out.println(test.toString());
		//System.out.println(tokenedText.toString(WordType.Complete));
		//System.out.println(myStemmer.stemm("biologically"));
		
		
		//System.out.println(myTest.toString(WordType.Raw));
	}

}

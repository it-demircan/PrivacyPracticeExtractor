package test;

import java.util.HashMap;
import java.util.List;

import model.Text;
import model.WordType;

import engine.preprocessing.IStopWordRemover;
import engine.preprocessing.StopWordRemover;
import services.*;
import engine.preprocessing.*;
import engine.classifier.*;
import model.*;

import edu.stanford.*;
import edu.stanford.nlp.trees.WordStemmer;

public class Main {
	public static void main(String[] args) {
		testClassifier();
	}
	
	private static void testClassifier(){
		ITextWriter writer = new TextWriter();
		ITextReader reader = new TextReader();
		String testPath = "C:\\Users\\MoePC\\Desktop\\test2.txt";
		
		Tree<Label> root = new Tree(new Label("Privacy Policy"));
		//Sub Categories
		Tree<Label> sharing = root.addChild(new Label("Sharing"));
		Tree<Label> collecting = root.addChild(new Label("Collecting"));
		
		
		HashMap<Vector, Label> mapping = new HashMap<Vector,Label>();
		Vector one = new Vector(2);
		one.setValue(0, 1);
		
		Vector two = new Vector(2);
		two.setValue(0, 1.5);
		
		Vector three = new Vector(2);
		three.setValue(1, 1);
		
		Vector four = new Vector(2);
		four.setValue(1,1.5);
		
		Vector five = new Vector(2);
		five.setValue(0,1);
		five.setValue(1, 2.5);
		
		Vector six = new Vector(2);
		six.setValue(0,1.5);
		six.setValue(1, 2.5);
		
		mapping.put(one, sharing.getData());
		mapping.put(two, sharing.getData());
		mapping.put(three, collecting.getData());
		mapping.put(four, collecting.getData());
		mapping.put(five, sharing.getData());
		mapping.put(six, sharing.getData());
		
		
		
		Hieron myClassifier = new Hieron();
		try {
			List<Label> myTree = myClassifier.train(mapping, root);
			Label test = myClassifier.predictLabel(one);
			
			System.out.println(test.getName());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		//Sub Sub Categories
//		Tree<Label> sharingWithOther = sharing.addChild(new Label("Sharing With Other"));
//		Tree<Label> sharingGov = sharing.addChild(new Label("Sharing With Gov"));
//		Tree<Label> sharingWithFacebook = sharingWithOther.addChild(new Label("Sharing with Facebook"));
//		try {
//			LabelConverter.write(root, writer, testPath);
//		
//			Tree<Label> testRoot = LabelConverter.read(reader, testPath);
//			LabelConverter.write(testRoot, writer, testPath);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	private static void testPP(){
		ITextReader textReader = new TextReader();
		ISettingLoader settingLoader = new SettingLoader();
		String exampleTxt = "We collect collect the content and other information you provide when you use our Services, including when you sign up for an account, create or share, and message or communicate with others. This can include information in or about the content you provide, such as the location of a photo or the date a file was created. We also collect information about how you use our Services, such as the types of content you view or engage with or the frequency and duration of your activities.";
		String exampleTxt2 = "For example, if you use a Groupon mobile application and your mobile device¡¯s settings allow it, we may collect Mobile Location Information from your device.";
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
		
		Text testText = myTokenizer.tokenizeToText(exampleTxt2);
		myStemmer.stemm(testText);
		myRemover.markStopWords(testText);
		Vector test2 = mapper.mapToVector(testText.getSentences().get(0));
		
		
		System.out.println(corpus.toString());
		System.out.println(test.toString());
		System.out.println(test2.toString());
		//System.out.println(tokenedText.toString(WordType.Complete));
		//System.out.println(myStemmer.stemm("biologically"));
		
		
		//System.out.println(myTest.toString(WordType.Raw));
	}
}



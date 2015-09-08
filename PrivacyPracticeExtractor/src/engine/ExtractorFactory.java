package engine;

import java.io.FileNotFoundException;
import java.io.IOException;

import services.*;
import engine.classifier.*;
import engine.extractor.Extractor;
import engine.extractor.IExtractor;
import engine.extractor.TripletExtractor;
import engine.preprocessing.*;

public class ExtractorFactory {

	public static PrivacyPracticeExtractor createPrivacyPracticeExtractor() throws FileNotFoundException, IOException{
		ITextReader textReader = new TextReader();
		ITextWriter textWriter = new TextWriter();
		ISettingLoader settingLoader = new SettingLoader();
		
		ITokenizer tokenizer = new Tokenizer();
		IVectorizer vectorizer = new Vectorizer();
		IStopWordRemover stopWordRemover = new StopWordRemover(textReader, settingLoader);
		IStemmer stemmer = new Stemmer();
		IFeatureSelector fSelector = new FeatureSelector();
		IParser parser = new Parser();
		
		IPreProcessor preProcessor = new PreProcessor(tokenizer, stopWordRemover, stemmer, vectorizer,fSelector);
		IClassifier classifier = new Hieron();
		IExtractor extractor = new TripletExtractor(parser,settingLoader.getCompressionTimeOut());
		
		PrivacyPracticeExtractor ppe = new PrivacyPracticeExtractor(textReader, textWriter, settingLoader, preProcessor, classifier,extractor);
		return ppe;
	}
}

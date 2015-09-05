package engine;

import services.*;
import engine.classifier.*;
import engine.extractor.Extractor;
import engine.extractor.IExtractor;
import engine.preprocessing.*;

public class ExtractorFactory {

	public static PrivacyPracticeExtractor createPrivacyPracticeExtractor(){
		ITextReader textReader = new TextReader();
		ITextWriter textWriter = new TextWriter();
		ISettingLoader settingLoader = new SettingLoader();
		
		ITokenizer tokenizer = new Tokenizer();
		IVectorizer vectorizer = new Vectorizer();
		IStopWordRemover stopWordRemover = new StopWordRemover(textReader, settingLoader);
		IStemmer stemmer = new Stemmer();
		IFeatureSelector fSelector = new FeatureSelector();
		
		IPreProcessor preProcessor = new PreProcessor(tokenizer, stopWordRemover, stemmer, vectorizer,fSelector);
		IClassifier classifier = new Hieron();
		IExtractor extractor = new Extractor();
		
		PrivacyPracticeExtractor ppe = new PrivacyPracticeExtractor(textReader, textWriter, settingLoader, preProcessor, classifier,extractor);
		return ppe;
	}
}

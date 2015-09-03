package engine;

import services.*;
import engine.classifier.*;
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
		
		IPreProcessor preProcessor = new PreProcessor(tokenizer, stopWordRemover, stemmer, vectorizer);
		IClassifier classifier = new Hieron();
		
		PrivacyPracticeExtractor ppe = new PrivacyPracticeExtractor(textReader, textWriter, settingLoader, preProcessor, classifier);
		return ppe;
	}
}

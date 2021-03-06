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

	/**
	 * Create a instance of PPE class and inject all dependencies.
	 * 
	 * @return PrivacyPracticeExtractor instance
	 */
	public static PrivacyPracticeExtractor createPrivacyPracticeExtractor(
			boolean injectExtractor) throws FileNotFoundException, IOException {
		ITextReader textReader = new TextReader();
		ITextWriter textWriter = new TextWriter();
		ISettingLoader settingLoader = new SettingLoader();

		ITokenizer tokenizer = new Tokenizer();
		IVectorizer vectorizer = new Vectorizer();
		IStopWordRemover stopWordRemover = new StopWordRemover(textReader,
				settingLoader);
		IStemmer stemmer = new Stemmer();
		IFeatureSelector fSelector = new FeatureSelector();

		IPreProcessor preProcessor = new PreProcessor(tokenizer,
				stopWordRemover, stemmer, vectorizer, fSelector);
		IClassifier classifier = new Hieron();

		IExtractor extractor = null;
		if (injectExtractor) {
			IParser parser = new Parser();
			extractor = new TripletExtractor(parser,
					settingLoader.getCompressionTimeOut());
		}
		
		PrivacyPracticeExtractor ppe = new PrivacyPracticeExtractor(textReader,
				textWriter, settingLoader, preProcessor, classifier, extractor);
		return ppe;
	}
}

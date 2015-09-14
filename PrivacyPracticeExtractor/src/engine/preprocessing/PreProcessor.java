	package engine.preprocessing;

import java.util.HashMap;
import java.util.List;

import model.*;

/**
 * Prepares text and sentences and maps them into a vector space.
 * Also a Interface for feature selection is integrated.
 * @author Muhammed Demircan
 */
public class PreProcessor implements IPreProcessor {

	ITokenizer tokenizer;
	IStopWordRemover stopWordRemover;
	IStemmer stemmer;
	IVectorizer vectorizer;
	IFeatureSelector fSelector;
	
	public PreProcessor(ITokenizer tokenizer,
			IStopWordRemover stopWordRemover, IStemmer stemmer,
			IVectorizer vectorizer, IFeatureSelector fSelector) {
		this.tokenizer = tokenizer;
		this.stopWordRemover = stopWordRemover;
		this.stemmer = stemmer;
		this.vectorizer = vectorizer;
		this.fSelector = fSelector;
	}

	/**
	 * Maps a sentence(text) into a vector space
	 */
	public HashMap<Sentence, Vector> preProcessText(String text) throws Exception {
		Text processingText = tokenizer.tokenizeToText(text);
		stemmer.stemm(processingText);
		stopWordRemover.markStopWords(processingText);
		HashMap<Sentence, Vector> mapping = vectorizer
				.mapToVector(processingText);
		return mapping;
	}

	/**
	 * Maps a text into a document object, containing multiple sentences
	 */
	public Text processToText(String text) {
		Text processingText = tokenizer.tokenizeToText(text);
		stemmer.stemm(processingText);
		stopWordRemover.markStopWords(processingText);
		return processingText;
	}

	/**
	 * Maps a document object into a set of vectors (for each sentence in the document)
	 */
	public HashMap<Sentence, Vector> processToVector(Text text) throws Exception {
		HashMap<Sentence, Vector> mapping = vectorizer.mapToVector(text);
		return mapping;
	}
	
	/**
	 * Inject the corpus to all instances, which need it for calculations.
	 */
	public void updateCorpus(Dictionary corpus){
		vectorizer.injectCorpus(corpus);
	}

	/**
	 * Interface to perform feature selection.
	 */
	@Override
	public Dictionary reduceCorpus(List<Label> labels, HashMap<Label, Text> readData,
			Dictionary corpus, int noWords) {
		HashMap<Label,List<String>> reduced = fSelector.selectFeatures(labels, readData, corpus, noWords);
		corpus = fSelector.reduceCorpus(reduced, corpus);
		return corpus;
	}
}

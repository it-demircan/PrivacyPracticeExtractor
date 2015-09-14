	package engine.preprocessing;

import java.util.HashMap;
import java.util.List;

import services.Logger;

import model.*;

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

	public HashMap<Sentence, Vector> preProcessText(String text) throws Exception {
		Text processingText = tokenizer.tokenizeToText(text);
		stemmer.stemm(processingText);
		stopWordRemover.markStopWords(processingText);
		HashMap<Sentence, Vector> mapping = vectorizer
				.mapToVector(processingText);
		return mapping;
	}

	public Text processToText(String text) {
		Text processingText = tokenizer.tokenizeToText(text);
		stemmer.stemm(processingText);
		stopWordRemover.markStopWords(processingText);
		return processingText;
	}

	public HashMap<Sentence, Vector> processToVector(Text text) throws Exception {
		HashMap<Sentence, Vector> mapping = vectorizer.mapToVector(text);
		return mapping;
	}
	
	public void updateCorpus(Dictionary corpus){
		vectorizer.injectCorpus(corpus);
	}

	@Override
	public Dictionary reduceCorpus(List<Label> labels, HashMap<Label, Text> readData,
			Dictionary corpus, int noWords) {
		HashMap<Label,List<String>> reduced = fSelector.selectFeatures(labels, readData, corpus, noWords);
		corpus = fSelector.reduceCorpus(reduced, corpus);
		return corpus;
	}
}

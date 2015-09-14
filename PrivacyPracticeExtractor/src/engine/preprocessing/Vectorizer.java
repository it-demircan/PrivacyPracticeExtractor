package engine.preprocessing;

import java.util.HashMap;

import model.*;

/**
 * Maps a sentence into a vector space according to the bag of words model.
 * @author Muhammed Demircan
 *
 */
public class Vectorizer implements IVectorizer{
	Dictionary dictionary;
	
	public void injectCorpus(Dictionary corpus){
		this.dictionary = corpus;
	}
	
	/**
	 * Mapping a sentence into its correspondending vector
	 */
	@Override
	public Vector mapToVector(Sentence sen) throws Exception {
		if(dictionary == null || dictionary.getSize() == 0)
			throw new Exception("Corpus not injected into Vectorizer");
		Vector mapping = new Vector(dictionary.getSize());
		int pos = -1;
		boolean[] visited = new boolean[dictionary.getSize()];
		for(Word word: sen.getWords()){
			pos = dictionary.getPositionOfElement(word.getProcessingValue());
			if(pos > -1 && pos < dictionary.getSize()){
				//mapping.setValue(pos, mapping.getValue(pos)+1.0);
				//TF-IDF
				if(!visited[pos]){
					mapping.setValue(pos,computeWeighting(sen,word));
					visited[pos] = true;
				}
			}
		}
		return mapping;
	}
	
	/**
	 * computes the tf-idf weight for a word in a sentence.
	 * @return tf-idf weight
	 */
	private double computeWeighting(Sentence sen, Word word){
		double weight = 0.0;
		
		//inverse document frequency
		double idf = 0.0;
		
		//term frequency
		double occur = 0.0;
		//Count occurrece in sentence - 
		for(Word nextWord : sen.getWords()){
			if(nextWord.getProcessingValue().equals(word.getProcessingValue()))
				occur++;
		}
		
		double occurenceInSentence = dictionary.getOccurrenceOfElement(word.getProcessingValue());
		double numberOfSentences = dictionary.getNumberOfSentences();
		idf = Math.log10(numberOfSentences/occurenceInSentence);
		weight = idf*occur;
		return weight;
	}

	/**
	 * Maps a whole document object into a set of sentence vector mappings.
	 */
	@Override
	public HashMap<Sentence, Vector> mapToVector(Text text) throws Exception {
		if(dictionary == null || dictionary.getSize() == 0)
			throw new Exception("Corpus not injected into Vectorizer");
		HashMap<Sentence, Vector> mapping = new HashMap<Sentence,Vector>();
		
		for(Sentence sen: text.getSentences()){
			mapping.put(sen, mapToVector(sen));
		}
		return mapping;
	}

}

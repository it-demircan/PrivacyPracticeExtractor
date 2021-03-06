package model;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents a sentence build up from words
 * @author Muhammed Demircan
 *
 */
public class Sentence {
	List<Word> words;
	int position; //Position in Text
	Label correct, predicted;

	public Sentence(int position) {
		words = new LinkedList<Word>();
		this.position = position;
	}

	/**
	 * Checks if a word is element of this sentence
	 */
	public boolean containsWord(String value){
		for(int i = 0; i < words.size();i++)
			if(words.get(i).getProcessingValue().equals(value))
				return true;
		return false;
	}

	public void appendWord(Word word) {
		words.add(word);
	}

	public List<Word> getWords() {
		return words;
	}

	public void setWords(List<Word> words) {
		this.words = words;
	}
	
	public void setLabel(Label correct, Label predicted){
		this.correct = correct;
		this.predicted = predicted;
	}
	
	public Label getCorrectLabel(){
		return correct;
	}
	
	public Label getPredictedLabel(){
		return predicted;
	}

	@Override
	public String toString(){
		String result = "";
		for(Word word : words)
			result += " "+word.getValue();
		return result;
	}
	
	/**
	 * Get the sentence as a string in different formats (e. g. stemmed sentence)
	 */
	public String toString(WordType type){
		String result = "";
		for(Word word : words){
			switch(type){
			case Raw:
				result += " "+word.getValue();
				break;
			case Lemma:
				result += " "+word.getLemma();
				break;
			case Stem:
				result += " "+word.getStem();
				break;
			case Complete:
				result += "["+word.getValue()+";L:"+word.getLemma()+";S:"+word.getStem()+";PT:"+word.getPosTag()+";isStopWord:"+word.isStopWord()+";P:"+word.getPosition()+"] ";
			case PosTagged:
				result += " "+word.getValue()+"/"+word.getPosTag();
			}	
		}
		return result;
	}
}

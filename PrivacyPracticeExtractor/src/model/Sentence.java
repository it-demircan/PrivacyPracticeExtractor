package model;

import java.util.LinkedList;
import java.util.List;

public class Sentence {
	List<Word> words;
	int position; //Position in Text

	public Sentence(int position) {
		words = new LinkedList<Word>();
		this.position = position;
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

	public String ToString(WordType type){
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
				result += "["+word.getValue()+";"+word.getLemma()+";"+word.getStem()+";"+word.getPosTag()+";"+word.getPosition()+"] ";
			}	
		}
		return result;
	}
}
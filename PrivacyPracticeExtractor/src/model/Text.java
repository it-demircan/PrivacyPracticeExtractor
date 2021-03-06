package model;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents a document containing sentence (instances)
 * @author Muhammed Demircan
 *
 */
public class Text {
	List<Sentence> sentences;
	
	public Text(){
		sentences = new LinkedList<Sentence>();
	}
	
	public void addSentence(Sentence toAdd){
		sentences.add(toAdd);
	}
	
	public List<Sentence> getSentences() {
		return sentences;
	}

	public void setSentences(List<Sentence> sentences) {
		this.sentences = sentences;
	}
	
	public String toString(WordType type){
		String text = "";
		for(Sentence sen : sentences){
			text += sen.toString(type) + System.getProperty("line.separator");
		}
		return text;
	}
}

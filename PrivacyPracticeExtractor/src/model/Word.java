package model;

/**
 * Models a single word with its original value, stem, posTag and a stopword mark.
 * @author Muhammed Demircan
 *
 */
public class Word {
	String value, stem, lemma, posTag;
	int position;
	boolean stopWord;

	public Word(int position){
		value = "";
		stem = "";
		lemma = "";
		posTag = "";
		this.position = position;
	}
	
	public Word(int position, String posTag){
		value = "";
		stem = "";
		lemma = "";
		this.posTag = posTag;
		this.position = position;
	}
	
	public String getProcessingValue(){
		return stem;
	}
	public boolean isStopWord() {
		return stopWord;
	}

	public void setStopWord(boolean stopWord) {
		this.stopWord = stopWord;
	}
	
	public int getPosition() {
		return position;
	}
	
	public void setPosition(int position) {
		this.position = position;
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getStem() {
		return stem;
	}

	public void setStem(String stem) {
		this.stem = stem;
	}

	public String getLemma() {
		return lemma;
	}

	public void setLemma(String lemma) {
		this.lemma = lemma;
	}

	public String getPosTag() {
		return posTag;
	}

	public void setPosTag(String posTag) {
		this.posTag = posTag;
	}
}

package engine.preprocessing;

import java.util.HashMap;

import model.*;

public class Vectorizer implements IVectorizer{
	Dictionary dictionary;
	
	public Vectorizer(Dictionary dictionary){
		this.dictionary = dictionary;
	}
	
	
	@Override
	public Vector mapToVector(Sentence sen) {
		Vector mapping = new Vector(dictionary.getSize());
		int pos = -1;
		for(Word word: sen.getWords()){
			pos = dictionary.getPositionOfElement(word.getProcessingValue());
			if(pos > -1){
				mapping.setValue(pos, mapping.getValue(pos)+1.0);
			}
		}
		return mapping;
	}

	@Override
	public HashMap<Sentence, Vector> mapToVector(Text text) {
		// TODO Auto-generated method stub
		return null;
	}

}

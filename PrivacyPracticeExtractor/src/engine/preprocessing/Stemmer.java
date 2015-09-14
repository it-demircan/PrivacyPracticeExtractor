package engine.preprocessing;
import model.*;

/**
 * Stemmer 
 * @author Muhammed Demircan
 *
 */
public class Stemmer implements IStemmer {

	/**
	 * Stems a word according to the underlying stemmer.
	 */
	public String stemm(String word) {
		PorterStemmer s = new PorterStemmer();
		char[] splittedWord = word.toCharArray();
		for(int i = 0; i < splittedWord.length;i++)
			if(Character.isLetter(splittedWord[i]))
				splittedWord[i] = Character.toLowerCase(splittedWord[i]);
		s.add(splittedWord, splittedWord.length);
		s.stem();
		return s.toString();
	}
	
	/**
	 * Stems all words in a document object.
	 */
	public void stemm(Text processingText){
		for(Sentence nextSentence : processingText.getSentences()){
			for(Word nextWord : nextSentence.getWords()){
				nextWord.setStem(stemm(nextWord.getValue()));
			}
		}
	}
}

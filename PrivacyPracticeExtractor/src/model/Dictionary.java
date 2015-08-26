package model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/*
 * This class represents the corpus
 */
public class Dictionary {
	List<CorpusElement> elements;
	int size; 			   //Number of unique words
	int numberOfSentences; //Number of "Documents", which are used to generate this corpus.
	
	public Dictionary(){
		elements = new LinkedList<CorpusElement>();
	}
	
	/**
	 * Expands the dictionary/corpus using the passed text object. 
	 * @param text - Text object with sentences
	 */
	public void populate(Text text){
		HashMap<String, Boolean> occurrence = new HashMap<String, Boolean>();
		for(Sentence sen: text.getSentences()){
			occurrence.clear();
			for(Word word: sen.getWords())
				if(!word.isStopWord())
					if(this.addElement(word.getProcessingValue())){ //Try to add to the corpus
						if(!occurrence.containsKey(word.getProcessingValue())) //Update the number of sentences, in which this word occurs.
							occurrence.put(word.getProcessingValue(), false);
					}
					else{
						//Element is initally inserted - mark it, so we don't increase the frequency twice.
						occurrence.put(word.getProcessingValue(), true);
					}
		
			//Update the occurrence of each word, which occurs in any other 
			//sentence but its initial sentence.
			Iterator it = occurrence.entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry pair = (Map.Entry)it.next();
		        if(!(boolean)pair.getValue())
		        	increaseOccurrence(getPositionOfElement(""+pair.getKey()));
		        it.remove();
		    }
		    
		    //Increase the number of sentences in the corpus
		    numberOfSentences++;
		}
	}
	
	/**
	 * Adds a new word to the dictionary/corpus
	 * @param element the word
	 * @return returns true, if the corpus already contains the word
	 */
	public boolean addElement(String element){
		boolean exist = false;
		for(int i = 0; i < elements.size();i++)
			if(elements.get(i).value.equals(element))
				exist = true;
		if(!exist)
			elements.add(new CorpusElement(element));
		size++;
		return exist;
	}
	
	/**
	 * Increase (+1) the occurrence of a word at the passed position 
	 * @param pos - Position of the word in dictionary
	 */
	public void increaseOccurrence(int pos){
		elements.get(pos).increaseOccurrence();
	}
	
	/**
	 * Retrieves the position of an word in the dictionary.
	 * @param element - The word
	 * @return Position in dictionary
	 */
	public int getPositionOfElement(String element){
		int pos = -1;
		for(int i = 0; i < elements.size();i++)
			if(elements.get(i).value.equals(element))
				pos = i;
		return pos;
	}
	
	/**
	 * Return the occurrence of an word
	 * @param element - the word
	 * @return - occurrence in all sentences
	 */
	public int getOccurrenceOfElement(String element){
		int occurrence = 0;
		int pos = getPositionOfElement(element);
		if(pos>-1)
			occurrence = elements.get(pos).occurrenceInSentence;
		return occurrence;
	}
	
	public void clearList(){
		elements.clear();
		size = 0;
	}
	
	public int getSize(){
		return size;
	}
	
	public int getNumberOfSentences()
	{
		return this.numberOfSentences;
	}
	
	@Override
	public String toString(){
		String res = "Dictionary Size = " + this.size + ". Containing:[";
		for(int i = 0; i< elements.size();i++){
			res += elements.get(i)+";";
		}
		res += "]";
		return res;
	}
	
	/*
	 * Maps a Entry in a corpus, 
	 * providing information about its occurrence in all sentences.
	 */
	private class CorpusElement{
		public String value;			 
		public int occurrenceInSentence; //Occurrence in all used sentence
		
		public CorpusElement(String value){
			this.value = value;
			occurrenceInSentence = 1;
		}
		
		public void increaseOccurrence(){
			occurrenceInSentence++;
		}
		
		@Override
		public String toString(){
			return "["+value+"-"+""+occurrenceInSentence+"]";
			
		}
	}
}

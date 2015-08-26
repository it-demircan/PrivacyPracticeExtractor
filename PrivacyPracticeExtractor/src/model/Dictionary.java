package model;

import java.util.LinkedList;
import java.util.List;

public class Dictionary {
	List<String> elements;
	int size;
	
	public Dictionary(){
		elements = new LinkedList<String>();
	}
	
	public void populate(Text text){
		for(Sentence sen: text.getSentences()){
			for(Word word: sen.getWords())
				if(!word.isStopWord())
					this.addElement(word.getProcessingValue());
		}
	}
	
	public void addElement(String element){
		boolean exist = false;
		for(int i = 0; i < elements.size();i++)
			if(elements.get(i).equals(element))
				exist = true;
		if(!exist)
			elements.add(element);
		size++;
	}
	
	public int getPositionOfElement(String element){
		int pos = -1;
		for(int i = 0; i < elements.size();i++)
			if(elements.get(i).equals(element))
				pos = i;
		return pos;
	}
	
	public void clearList(){
		elements.clear();
		size = 0;
	}
	
	public int getSize(){
		return size;
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
}

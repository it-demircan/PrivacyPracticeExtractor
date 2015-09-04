package engine.preprocessing;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import services.ITextReader;

import model.*;

public class FeatureSelector {
	List<Label> labels;
	HashMap<Label, Text> readData;
	Dictionary corpus;

	public HashMap<Label, List<String>> selectFeatures(List<Label> labels,HashMap<Label, Text> readData,Dictionary corpus, int noWords){
		HashMap<Label, List<String>> mapping = new HashMap<Label,List<String>>();
		for(int i = 1; i < labels.size();i++){
			mapping.put(labels.get(i), computeTopRankedWords(labels.get(i),noWords));
		}
		return mapping;
	}

	private List<String> computeTopRankedWords(Label recentLabel, int noWords) {

		HashMap<String, Double> rankedWords = new HashMap<String, Double>();
		List<String> result = new LinkedList<String>();
		int counter =0;
		double ranking;
		for (int i = 0; i < corpus.getSize(); i++) {
			ranking = computeInformationGain(corpus.getValue(i), recentLabel);

			rankedWords.put(corpus.getValue(i), ranking);
		}

		TreeMap<String, Double> sortedMap = SortByValue(rankedWords);
		for (Entry<String, Double> entry : sortedMap.entrySet()){
			if(counter < noWords){
				result.add(entry.getKey());
				counter++;
			}
		}
		
		return result;
	}

	/**
	 * Compute the information gain for an word for a label/class
	 * 
	 * @return the information gain
	 */
	private double computeInformationGain(String word, Label recentLabel) {
		int N00 = 0, N01 = 0, N10 = 0, N11 = 0;

		for (int i = 1; i < labels.size(); i++) {
			if (labels.get(i).compareTo(recentLabel) != 0)
				for (Sentence sen : readData.get(labels.get(i)).getSentences())
					if (sen.containsWord(word))
						N10++;
		}

		for (Sentence sen : readData.get(recentLabel).getSentences())
			if (sen.containsWord(word))
				N11++;

		for (int i = 1; i < labels.size(); i++) {
			if (labels.get(i).compareTo(recentLabel) != 0)
				for (Sentence sen : readData.get(labels.get(i)).getSentences())
					if (!sen.containsWord(word))
						N00++;
		}

		for (Sentence sen : readData.get(recentLabel).getSentences())
			if (!sen.containsWord(word))
				N01++;

		int N = N11 + N01 + N10 + N00;

		double termOne = (double) N11
				/ (double) N
				* Math.log((double) (N * N11)
						/ (double) ((N11 + N10) * (N11 + N10))) / Math.log(2);
		double termTwo = (double) N01
				/ (double) N
				* Math.log((double) (N * N01)
						/ (double) ((N01 + N00) * (N01 + N11))) / Math.log(2);
		double termThree = (double) N10
				/ (double) N
				* Math.log((double) (N * N10)
						/ (double) ((N11 + N10) * (N00 + N10))) / Math.log(2);
		double termFour = (double) N00
				/ (double) N
				* Math.log((double) (N * N00)
						/ (double) ((N00 + N10) * (N00 + N10))) / Math.log(2);
		return termOne + termTwo + termThree + termFour;
	}
	
	public static TreeMap<String, Double> SortByValue(
			HashMap<String, Double> map) {
		ValueComparator vc = new ValueComparator(map);
		TreeMap<String, Double> sortedMap = new TreeMap<String, Double>(vc);
		sortedMap.putAll(map);
		return sortedMap;
	}
}

class ValueComparator implements Comparator<String> {

	Map<String, Double> map;

	public ValueComparator(Map<String, Double> base) {
		this.map = base;
	}

	public int compare(String a, String b) {
		if (map.get(a) >= map.get(b)) {
			return -1;
		} else {
			return 1;
		} // returning 0 would merge keys
	}
}

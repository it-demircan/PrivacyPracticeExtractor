package engine.preprocessing;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import services.ITextReader;

import model.*;

/**
 * This class implements the "information gain" feature selector
 * @author Muhammed Demircan
 */
public class FeatureSelector implements IFeatureSelector{
	List<Label> labels;
	HashMap<Label, Text> readData;
	Dictionary corpus;

	/**
	 * Compute the k- top ranked features in a corpus a all classes(labels)
	 * @param labels - all labels
	 * @param readData - all data for each label
	 * @param corpus - recent corpus
	 * @param noWords - top "noWords" will be selected
	 * @return Mapping between class/label and top words in corpus
	 */
	public HashMap<Label, List<String>> selectFeatures(List<Label> labels,
			HashMap<Label, Text> readData, Dictionary corpus, int noWords) {
		HashMap<Label, List<String>> mapping = new HashMap<Label, List<String>>();
		this.corpus = corpus;
		this.labels = labels;
		this.readData = readData;

		for (int i = 1; i < labels.size(); i++) {
			mapping.put(labels.get(i),
					computeTopRankedWords(labels.get(i), noWords));
		}
		return mapping;
	}
	
	/**
	 * reduces the given corpus by the ranked list mapping for each label
	 * @return reduced corpus
	 */
	public Dictionary reduceCorpus(HashMap<Label,List<String>> rankedWords, Dictionary corpus){
		List<String> union = new LinkedList<String>();
		// Iterate through vectors hashmap
		Iterator it = rankedWords.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			
			for(int i = 0; i < ((List<String>)pair.getValue()).size();i++){
				union.add(((List<String>)pair.getValue()).get(i));
			}
			it.remove();
		}
		corpus.removeEntriesExcept(union);
		return corpus;
	}
	
	/**
	 * computes the top k words for the given label/class
	 * @return List with top k words
	 */
	private List<String> computeTopRankedWords(Label recentLabel, int noWords) {

		HashMap<String, Double> rankedWords = new HashMap<String, Double>();
		List<String> result = new LinkedList<String>();
		int counter = 0;
		double ranking;
		for (int i = 0; i < corpus.getSize(); i++) {
			ranking = computeInformationGain(corpus.getValue(i), recentLabel);

			rankedWords.put(corpus.getValue(i), ranking);
		}

		TreeMap<String, Double> sortedMap = SortByValue(rankedWords);
		
		for (Entry<String, Double> entry : sortedMap.entrySet()) {
			if (counter < noWords) {
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
		int N00 = 1, N01 = 1, N10 = 1, N11 = 1;

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

		/* A bit ugly, but we have to avoid overloads */
		double termOne = ((double) N11
				/ (double) N)
				* (Math.log((double) (N * N11)
						/ (double) ((N11 + N10) * (N11 + N01))) / Math.log(2) ) ;
		double termTwo = (double) N01
				/ (double) N
				* Math.log((double) (N * N01)
						/ (double) ((N01 + N00) * (N01 + N11))) / Math.log(2);
		
		double x = (double) N10
				/ (double) N;
		
		double y = (double) ((double)N * (double)N10);
		double z = (double) ((double)(N11 + N10) * (double)(N00 + N10));
		y = y/z;
		double termThree = x * (Math.log(y) / Math.log(2));
		
		x = (double) N00
				/ (double) N;
		y = (double) ((double)N * (double)N00);
		z = (double) (((double)N00 + (double)N10) * ((double)N00 + (double)N01));
		y = y/z;
		double termFour = x * (Math.log(y) / Math.log(2));
		
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
/**
 * Helper Class for sorting a hashmap
 * @author Muhammed Demircan
 *
 */
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

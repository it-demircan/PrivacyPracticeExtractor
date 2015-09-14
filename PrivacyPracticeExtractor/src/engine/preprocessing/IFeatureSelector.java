package engine.preprocessing;

import java.util.HashMap;
import java.util.List;

import model.Dictionary;
import model.Label;
import model.Text;
/**
 * See FeatureSelector
 */
public interface IFeatureSelector {
	public HashMap<Label, List<String>> selectFeatures(List<Label> labels,
			HashMap<Label, Text> readData, Dictionary corpus, int noWords);
	public Dictionary reduceCorpus(HashMap<Label,List<String>> rankedWords, Dictionary corpus);
}

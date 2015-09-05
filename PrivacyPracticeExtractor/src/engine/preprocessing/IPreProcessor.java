package engine.preprocessing;

import java.util.HashMap;
import java.util.List;

import model.Dictionary;
import model.Label;
import model.Sentence;
import model.Text;
import model.Vector;

public interface IPreProcessor {
	public Text processToText(String text);
	public HashMap<Sentence,Vector> processToVector(Text text) throws Exception;
	public HashMap<Sentence,Vector> preProcessText(String text) throws Exception;
	public void updateCorpus(Dictionary corpus);
	public Dictionary reduceCorpus(List<Label> labels,
			HashMap<Label, Text> readData, Dictionary corpus, int noWords);
	
}

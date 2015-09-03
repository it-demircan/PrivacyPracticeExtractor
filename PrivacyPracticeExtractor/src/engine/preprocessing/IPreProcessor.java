package engine.preprocessing;

import java.util.HashMap;

import model.Dictionary;
import model.Sentence;
import model.Text;
import model.Vector;

public interface IPreProcessor {
	public Text processToText(String text);
	public HashMap<Sentence,Vector> processToVector(Text text) throws Exception;
	public HashMap<Sentence,Vector> preProcessText(String text) throws Exception;
	public void updateCorpus(Dictionary corpus);
}

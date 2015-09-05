package engine.extractor;

import java.util.HashMap;
import java.util.List;

import model.Label;
import model.Sentence;

public interface IExtractor {
	public HashMap<Label, String> extract(HashMap<Label, List<Sentence>> classifiedSentences) throws Exception;
	public void initialize();
}

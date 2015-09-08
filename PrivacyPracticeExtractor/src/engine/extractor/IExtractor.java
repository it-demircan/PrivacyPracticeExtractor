package engine.extractor;

import java.util.HashMap;
import java.util.List;

import model.Label;
import model.Sentence;

public interface IExtractor {
	/**
	 * Extracts/Compress each sentence in the all labels.
	 * @param classifiedSentences - label sentence mapping
	 * @return - Compressed sentence for each label
	 */
	public HashMap<Label, String> extract(HashMap<Label, List<Sentence>> classifiedSentences) throws Exception;
}

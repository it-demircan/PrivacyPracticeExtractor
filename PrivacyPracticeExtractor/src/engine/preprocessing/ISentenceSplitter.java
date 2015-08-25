package engine.preprocessing;

import java.util.List;

public interface ISentenceSplitter {
	public List<String> splitSentences(String text);
}

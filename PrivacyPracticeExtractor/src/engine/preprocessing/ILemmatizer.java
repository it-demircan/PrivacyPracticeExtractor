package engine.preprocessing;

import java.util.List;

public interface ILemmatizer {
	public List<String> lemmatize(String word);
}

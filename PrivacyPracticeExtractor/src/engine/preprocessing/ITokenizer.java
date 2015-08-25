package engine.preprocessing;

import java.util.List;

public interface ITokenizer {
	public List<String> tokenize(String sentence);
}

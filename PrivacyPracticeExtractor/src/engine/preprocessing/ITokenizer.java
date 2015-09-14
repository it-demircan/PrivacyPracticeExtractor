package engine.preprocessing;

import java.util.List;

import model.Text;
/**
 * See TokenizerS
 */
public interface ITokenizer {
	public List<String> tokenize(String sentence);
	public Text tokenizeToText(String text);
}

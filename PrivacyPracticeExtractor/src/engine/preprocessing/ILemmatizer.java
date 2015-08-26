package engine.preprocessing;

import java.util.List;

import model.Text;

public interface ILemmatizer {
	public List<String> lemmatize(String word);
	public Text lemmatizeToText(String word);
}

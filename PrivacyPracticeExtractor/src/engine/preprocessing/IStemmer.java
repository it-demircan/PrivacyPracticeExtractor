package engine.preprocessing;

import model.Text;

public interface IStemmer {
	public String stemm(String word);
	public void stemm(Text processingText);
}

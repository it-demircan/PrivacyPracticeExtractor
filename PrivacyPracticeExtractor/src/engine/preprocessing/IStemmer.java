package engine.preprocessing;

import model.Text;

/**
 * See Stemmer (and PorterStemmer)
 */
public interface IStemmer {
	public String stemm(String word);
	public void stemm(Text processingText);
}

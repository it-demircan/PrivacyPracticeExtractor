package engine.preprocessing;

import model.Text;

/**
 * See StopWordRemover
 */
public interface IStopWordRemover {
	public String removeStopWords(String text);
	public boolean checkStopWord(String text);
	public void markStopWords(Text processingText);
}

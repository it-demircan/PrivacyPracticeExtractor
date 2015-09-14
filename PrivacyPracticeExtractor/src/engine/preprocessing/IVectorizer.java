package engine.preprocessing;

import java.util.HashMap;

import model.*;

/**
 * See Vectorizer
 */
public interface IVectorizer {
	public Vector mapToVector(Sentence sen) throws Exception;
	public HashMap<Sentence, Vector> mapToVector(Text text) throws Exception; 
	public void injectCorpus(Dictionary corpus);
}

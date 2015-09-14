package engine.preprocessing;

import edu.stanford.nlp.trees.Tree;

/**
 * See Parser 
 */
public interface IParser {
	public Tree parse(String toParse);
}

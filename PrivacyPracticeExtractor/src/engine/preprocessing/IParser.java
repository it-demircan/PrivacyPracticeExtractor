package engine.preprocessing;

import edu.stanford.nlp.trees.Tree;

public interface IParser {
	public Tree parse(String toParse);
}

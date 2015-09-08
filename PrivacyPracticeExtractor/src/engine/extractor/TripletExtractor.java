package engine.extractor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import edu.stanford.nlp.trees.Tree;
import engine.preprocessing.IParser;

import model.Label;
import model.Sentence;
import model.Vector;

/**
 * Implementation of an Triplet Extractor, which extracts (Subject, verb, object) relations from 
 * sentences based on a parsed tree structure.
 * The Algorithm is based on the paper:
 * "Triplet Extraction from Sentences" - D.Rusu et al.
 */
public class TripletExtractor implements IExtractor {
	int timeOut; //compression timeout
	IParser parser;

	public TripletExtractor(IParser parser, int compressionTimeOut) {
		this.parser = parser;
		timeOut = compressionTimeOut;
	}

	@Override
	public HashMap<Label, String> extract(
			HashMap<Label, List<Sentence>> classifiedSentences)
			throws Exception {

		HashMap<Label, String> compressed = new HashMap<Label, String>();
		Iterator it = classifiedSentences.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			Label nextLabel = (Label) pair.getKey();

			System.out.println("Summarizing:" + nextLabel.getName());

			String compressedSentence = "";
			for (Sentence nextSentence : classifiedSentences.get(nextLabel)) {
				try {
					//Fixing Label for concurrent thread.
					//Stops calculation after "timeOut" seconds (each sentence).
					final String sentence = nextSentence.toString();
					ExecutorService executorService = Executors.newSingleThreadExecutor();
					compressedSentence += executorService.submit(new Callable<String>(){
						@Override
						public String call() throws Exception {
							// TODO Auto-generated method stub
							return extractTriplet(sentence);
						}	
					}).get(timeOut, TimeUnit.SECONDS);
					
					compressedSentence += "\r\n";
					System.out.println("Working:" + compressedSentence);
				}catch (TimeoutException e) {
		            //TODO log
		            System.out.println("failed - timeout");
		        } catch (Exception err) {
					// controlled catching
					System.out.println("failed");
				}
			}
			compressed.put(nextLabel, compressedSentence);
			// it.remove();
		}
		return compressed;
	}

	/**
	 * Extracts the triplet from a sentence with its attributes.
	 * @param sentence - Sentence to compress
	 * @return - Extracted triplet
	 * @throws Exception - If Extraction fails or is not possible, an exception will be thrown.
	 */
	public String extractTriplet(String sentence) throws Exception {
		String compressedSentence = "";
		Tree parsedSentence = parser.parse(sentence);
		compressedSentence = extractTriplet(parsedSentence);
		return compressedSentence;
	}

	/**
	 * Converts the extraction it into a readable string
	 * @return
	 */
	private String toString(Object[] noun, Object[] verb, Object[] negotiation,
			Object[] object) {
		String compressedSentence = "";
		// generate string - subject
		if (noun != null) {
			compressedSentence += " [";
			for (Tree attr : (List<Tree>) noun[1]) {
				compressedSentence += attr.getLeaves().toString()
						.replace("[", "").replace("]", "")
						+ " ";
			}
			compressedSentence += "] ";
			compressedSentence += ((Tree) noun[0]).getLeaves().toString()
					.replace("[", "").replace("]", "");
		}
		// generate string - verb
		compressedSentence += " [";
		for (Tree attr : (List<Tree>) verb[1]) {
			compressedSentence += attr.getLeaves().toString().replace("[", "")
					.replace("]", "")
					+ " ";
		}
		compressedSentence += "] ";
		compressedSentence += ((Tree) verb[0]).getLeaves().toString()
				.replace("[", "").replace("]", "");

		// generate string - negotiation
		if (negotiation != null) {
			compressedSentence += " [";
			for (Tree attr : (List<Tree>) negotiation[1]) {
				compressedSentence += attr.getLeaves().toString()
						.replace("[", "").replace("]", "")
						+ " ";
			}
			compressedSentence += "] ";
			compressedSentence += ((Tree) negotiation[0]).getLeaves()
					.toString().replace("[", "").replace("]", "");
		}

		// generate string - object
		compressedSentence += " [";
		for (Tree attr : (List<Tree>) object[1]) {
			compressedSentence += attr.getLeaves().toString().replace("[", "")
					.replace("]", "")
					+ " ";
		}
		compressedSentence += "] ";
		compressedSentence += ((Tree) object[0]).getLeaves().toString()
				.replace("[", "").replace("]", "");

		return compressedSentence;
	}

	/**
	 * Extracts the triplet from a parsed sentence (tree structure)
	 * @param parsedSentence
	 * @return triplet or failure
	 * @throws Exception - fails if extraction is impossible
	 */
	private String extractTriplet(Tree parsedSentence) throws Exception {
		Tree root = parsedSentence.getChild(0);
		List<Tree> children;
		//Subtrees
		Tree nounSubTree = null;
		Tree verbSubTree = null;

		// Noun Subtree found
		if (root.getChild(0).label().value().equals(("NP"))) {
			nounSubTree = root.getChild(0);
			children = root.getChildrenAsList();

			// Search for first (children) verb subtree
			for (int i = 1; i < children.size(); i++) {
				if (children.get(i).label().value().equals("VP")) {
					verbSubTree = children.get(i);
					i = children.size();
				}
			}

			Object[] noun = extractSubject(nounSubTree);
			Object[] verb = extractVerb(verbSubTree);
			Object[] negotiation = extractNegation(verbSubTree);
			Object[] object = extractObject(verbSubTree);

			return toString(noun, verb, negotiation, object);
		} else if (root.getChild(0).label().value().equals(("VP"))) { 
			//missing subject - sentence
			verbSubTree = root.getChild(0);
			Object[] verb = extractVerb(verbSubTree);
			Object[] negotiation = extractNegation(verbSubTree);
			Object[] object = extractObject(verbSubTree);
			return toString(null, verb, negotiation, object);
		}

		throw new Exception("Failed to extract triplet.");
	}

	/**
	 * Extracts the first negation in the verb subtree.
	 * 
	 * @param verbSubTree
	 *            Verb subtree
	 * @return first negation (not, n't)
	 */
	private Object[] extractNegation(Tree verbSubTree) {
		for (Tree child : verbSubTree.getChildrenAsList()) {
			if (child.label().value().equals("RB")) {
				return new Object[] { child,
						extractAttributes(child, verbSubTree) };
			}
		}
		return null;
	}

	/**
	 * Searches for the deepest verb in the parsed sentence tree and returns it
	 * with its attributes.
	 * 
	 * @param verbSubTree
	 *            - parsed verb subtree
	 * @return - deepest found verb with its attributes
	 */
	public Object[] extractVerb(Tree verbSubTree) {
		Tree deepestVerb = searchForDeepestVerb(verbSubTree);
		List<Tree> attributes = extractAttributes(deepestVerb, verbSubTree);
		return new Object[] { deepestVerb, attributes };
	}

	/**
	 * Extracts the object of the sentence. It will be searched in the verb
	 * subtree. The first occurrence of a noun will be returned.
	 * 
	 * @param verbSubTree
	 *            - Verb sub tree
	 * @return - object of the sentence with its attributes
	 */
	public Object[] extractObject(Tree verbSubTree) {
		boolean repeat = false;
		List<Tree> siblings = new ArrayList<Tree>();

		// filter for relevant sub trees (Prepositional, Noun, Adjective
		// Phrases)
		do {
			repeat = false;
			for (Tree child : verbSubTree.getChildrenAsList()) {
				if (child.label().value().equals("NP")
						|| child.label().value().equals("PP")
						|| child.label().value().equals("JJ")) {
					siblings.add(child);
				} else if (child.label().value().equals("VP")
						|| child.label().value().equals("ADJP")) {
					verbSubTree = child;
					repeat = true;
					break;
				}
			}
		} while (repeat);

		// Search for the first noun in the subtrees and return it
		for (Tree value : siblings) {
			String val = value.label().value();
			if (val.equals("NP") || val.equals("PP")) {
				for (Tree leaf : value.getLeaves()) {
					leaf = leaf.parent(value);
					if (leaf.label().value().startsWith("N")) {
						List<Tree> attributes = extractAttributes(leaf,
								verbSubTree);
						return new Object[] { leaf, attributes };
					}
				}
			} else {
				for (Tree leaf : value.getLeaves()) {
					leaf = leaf.parent(value);
					if (leaf.label().value().startsWith("J")) {
						List<Tree> attributes = extractAttributes(leaf,
								verbSubTree);
						return new Object[] { leaf, attributes };
					}
				}
			}
		}

		return null;
	}

	/**
	 * Finds pronouns or nouns in nounSubTree
	 * 
	 * @param nounSubTree
	 * @return (pro)noun with attributes
	 */
	private Object[] extractSubject(Tree nounSubTree) {
		Object[] noun = null;

		for (Tree leaf : nounSubTree.getLeaves()) {
			leaf = leaf.parent(nounSubTree);
			// Find Pronoun or nouns
			if (leaf.label().value().startsWith("N")
					|| leaf.label().value().equals("PRP")) {
				List<Tree> attributes = extractAttributes(leaf, nounSubTree);
				noun = new Object[] { leaf, attributes };
			}
		}
		return noun;
	}

	/**
	 * Extracts the attributes/modifiers of each word
	 * 
	 * @param word
	 *            - the word
	 * @param siblings
	 *            - its siblings
	 * @return
	 */
	public List<Tree> extractAttributes(Tree word, Tree root) {
		// Get the siblings of the recent word
		List<Tree> siblings = word.parent(root).getChildrenAsList();
		int index = siblings.indexOf(word);
		siblings.remove(index); // remove the word itself from list

		List<Tree> result = new ArrayList<Tree>();

		if (word.label().value().startsWith("J")) // Adjective
		{
			for (Tree sib : siblings) {
				if (sib.label().value().equals("RB")) {
					result.add(sib);
				}
			}
		} else {
			if (word.label().value().startsWith("N")) {
				for (Tree sib : siblings) {
					if (sib.label().value().equals("DT")
							|| sib.label().value().equals("PRP$")
							|| sib.label().value().equals("POS")
							|| sib.label().value().equals("JJ")
							|| sib.label().value().equals("CD")
							|| sib.label().value().equals("ADJP")
							|| sib.label().value().equals("QP")
							|| sib.label().value().equals("NP")) {
						result.add(sib);
					}
				}
			} else {
				if (word.label().value().startsWith("V")) {
					for (Tree sib : siblings) {
						if (sib.label().value().equals("ADJP")) {
							result.add(sib);
						}
					}
				}
			}
		}
		return result;
	}

	/**
	 * Search for the deepest verb in verbSubTree (deep search)
	 * 
	 * @param verbSubTree
	 *            - Verb Sub Tree
	 * @return Deepest Verb in parsed sentence tree
	 */
	private Tree searchForDeepestVerb(Tree verbSubTree) {
		int max = -1;
		Tree verb = null;
		List<Tree> leaves = verbSubTree.getLeaves();
		for (Tree leaf : leaves) {
			leaf = leaf.parent(verbSubTree);

			if (leaf.label().value().startsWith("V")) {
				int i = verbSubTree.depth(leaf);
				if (i > max) {
					max = verbSubTree.depth(leaf);
					verb = leaf;
				}
			}
		}
		return verb;
	}
}

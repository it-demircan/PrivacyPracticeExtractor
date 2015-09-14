package engine.preprocessing;

import java.util.LinkedList;
import java.util.List;

import model.Sentence;
import model.Text;
import model.Word;

import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

/**
 * (Adapter) Class, which enables to use tokenizer from the Stanford CoreNLP API  
 * @author Muhammed Demircan
 *
 */
public class Tokenizer extends CoreNLP implements ITokenizer {

	public Tokenizer() {
		this.setProperty("tokenize, ssplit, pos");
		pipeline = new StanfordCoreNLP(props);
	}
	
	/**
	 * Tokenize a single sentence into a List of all its words.
	 */
	public List<String> tokenize(String sentence) {
		List<String> words = new LinkedList<String>();
		Annotation document = new Annotation(sentence);
		pipeline.annotate(document);

		// PTBTokenizer
		List<CoreLabel> tokens = document.get(TokensAnnotation.class);

		for (CoreLabel token : tokens) {
			words.add(token.get(TextAnnotation.class));
		}
		return words;
	}

	/**
	 * Tokenize a single sentence into a text (object)/document.
	 */
	public Text tokenizeToText(String text) {
		Text processingText = new Text();
		Annotation document = new Annotation(text);
		pipeline.annotate(document);
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		int sPos = 0; //Sentence Position
		for (CoreMap sentence : sentences) {
			Sentence sen = new Sentence(sPos);
			int wPos = 0; //Word Position
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
				String word = token.get(TextAnnotation.class);
				String posTag = token.get(PartOfSpeechAnnotation.class);
				Word nextWord = new Word(wPos,posTag);
				nextWord.setValue(word);
				sen.appendWord(nextWord);
				wPos++;
			}
			processingText.addSentence(sen);
			sPos++;
		}

		return processingText;
	}
}

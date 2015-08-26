package engine.preprocessing;

import java.util.LinkedList;
import java.util.List;

import model.Sentence;
import model.Text;
import model.Word;

import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class Tokenizer extends CoreNLP implements ITokenizer {

	public Tokenizer() {
		this.setProperty("tokenize, ssplit");
		pipeline = new StanfordCoreNLP(props);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see engine.preprocessing.ITokenizer#tokenize(java.lang.String)
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
				Word nextWord = new Word(wPos);
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

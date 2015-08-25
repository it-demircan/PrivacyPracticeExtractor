package engine.preprocessing;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class SentenceSplitter implements ISentenceSplitter{
	StanfordCoreNLP pipeline;
	
	public SentenceSplitter(){
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit");
		pipeline = new StanfordCoreNLP(props);
	}
	
	public List<String> splitSentences(String text) {
		List<String> sentences = new LinkedList<String>();
		Annotation document = new Annotation(text);
		pipeline.annotate(document);
		List<CoreMap> splittedSentences = document.get(SentencesAnnotation.class);
		for(CoreMap sentence : splittedSentences){
			sentences.add(sentence.get(TextAnnotation.class));
		}	
		return sentences;
	}
}

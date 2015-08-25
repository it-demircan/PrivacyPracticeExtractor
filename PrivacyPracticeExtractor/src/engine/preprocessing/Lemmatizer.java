package engine.preprocessing;

import java.util.LinkedList;
import java.util.List;

import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class Lemmatizer extends CoreNLP implements ILemmatizer{
	
	public Lemmatizer(){
		this.setProperty("tokenize, ssplit, pos, lemma");
		pipeline = new StanfordCoreNLP(props);
	}
	 public List<String> lemmatize(String documentText){
	        List<String> lemmas = new LinkedList<String>();
	        Annotation document = new Annotation(documentText);
	        pipeline.annotate(document);
	        List<CoreMap> sentences = document.get(SentencesAnnotation.class);
	        for(CoreMap sentence: sentences) {
	            for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
	                lemmas.add(token.get(LemmaAnnotation.class));
	            }
	        }
	        return lemmas;
	    }
}

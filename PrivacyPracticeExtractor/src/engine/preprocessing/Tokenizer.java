package engine.preprocessing;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class Tokenizer implements ITokenizer{
	StanfordCoreNLP pipeline;
	
	public Tokenizer(){
		 Properties props = new Properties();
		 props.setProperty("annotators", "tokenize");
		 pipeline = new StanfordCoreNLP(props);
	}
	
	/*
	 * (non-Javadoc)
	 * @see engine.preprocessing.ITokenizer#tokenize(java.lang.String)
	 */
	public List<String> tokenize(String sentence) {
		 List<String> words = new LinkedList<String>();
		 Annotation document = new Annotation(sentence);		 
		 pipeline.annotate(document);
		 
		 //PTBTokenizer 
		 List<CoreLabel> tokens =  document.get(TokensAnnotation.class);
		 
		 for(CoreLabel token : tokens){
			 words.add(token.get(TextAnnotation.class));
		 }		 
		 return words;
	}

}

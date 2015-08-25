package engine.preprocessing;

import java.util.Properties;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public abstract class CoreNLP {
	StanfordCoreNLP pipeline;
	Properties props;
	
	protected void setProperty(String property){
		props = new Properties();
		props.setProperty("annotators",property);
	}
}

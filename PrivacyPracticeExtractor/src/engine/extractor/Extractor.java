package engine.extractor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Label;
import model.Sentence;
import scala.collection.Iterator;
import scala.collection.Seq;
import edu.knowitall.openie.Argument;
import edu.knowitall.openie.Instance;
import edu.knowitall.openie.OpenIE;
import edu.knowitall.tool.parse.ClearParser;
import edu.knowitall.tool.postag.ClearPostagger;
import edu.knowitall.tool.srl.ClearSrl;
import edu.knowitall.tool.tokenize.ClearTokenizer;

public class Extractor implements IExtractor{
	OpenIE openIE;	
	boolean initialized;
	
	public void initialize(){
		if(!initialized){
			openIE = new OpenIE(new ClearParser(new ClearPostagger(
	                new ClearTokenizer())),
	                new ClearSrl(), false);
			initialized = true;
		}
	}
	
	public HashMap<Label, String> extract(HashMap<Label, List<Sentence>> classifiedSentences) throws Exception {
		if(!initialized)
			throw new Exception("Extractor has to be initalized first.");
		
		HashMap<Label, String> summarization = new HashMap<Label,String>();
		
		java.util.Iterator it =  classifiedSentences.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			Label recentLabel = (Label)pair.getKey();
			List<Sentence> sentences = (List<Sentence>)pair.getValue();
			String sum = "";
			for(Sentence sen : sentences){
				sum += extractFromSentence(sen.toString()) +"\n";
			}
			summarization.put(recentLabel, sum);
			it.remove();
		}
		
		return summarization;
	}
	
	private String extractFromSentence(String sentence){
		Seq<Instance> extractions = openIE.extract(sentence);
		double maxCon = 0.0;
		String res = "";
		Iterator<Instance> iterator = extractions.iterator();
        while (iterator.hasNext()) {
            Instance inst = iterator.next();
            StringBuilder sb = new StringBuilder();
            sb.append(inst.confidence())
                .append('\t')
                .append(inst.extr().arg1().text())
                .append('\t')
                .append(inst.extr().rel().text())
                .append('\t');

            Iterator<Argument> argIter = inst.extr().arg2s().iterator();
            while (argIter.hasNext()) {
                Argument arg = argIter.next();
                sb.append(arg.text()).append("; ");
            }
            
            if(inst.confidence() > maxCon)
            	res = sb.toString();
        }
        
        return res;
	}
}

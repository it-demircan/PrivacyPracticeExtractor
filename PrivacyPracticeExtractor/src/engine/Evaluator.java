package engine;

import java.util.HashMap;
import java.util.List;

import model.*;

public class Evaluator {
	HashMap<Label, Tree<Label>> labelMapping;
	List<Label> labels;
	HashMap<Label, Integer> labelDocumentCounter;
	int noSentences; // No. Sentences(Documents) in used corpus

	int cumulativeTreeInducedError; // Cumulative tree induced error
	int evaluatedSentence; // No. of predicted labels which are tested

	HashMap<Label, Integer> truePositiv;
	HashMap<Label, Integer> falsePositiv;
	HashMap<Label, Integer> trueNegativ;
	HashMap<Label, Integer> falseNegativ;

	public Evaluator(List<Label> labels, HashMap<Label, Tree<Label>> lMapping,
			HashMap<Label, Integer> labelDocumentCounter, int noSentence) {
		labelMapping = lMapping;
		this.labels = labels;
		this.labelDocumentCounter = labelDocumentCounter;
		this.noSentences = noSentence;

		truePositiv = new HashMap<Label, Integer>();
		falsePositiv = new HashMap<Label, Integer>();
		trueNegativ = new HashMap<Label, Integer>();
		falseNegativ = new HashMap<Label, Integer>();
	}
	
	public void evaluate(List<Sentence> classifiedSentences){
		computePrecisionRecall(classifiedSentences);
		computeTreeInducedError(classifiedSentences);
	}

	private void computePrecisionRecall(List<Sentence> classifiedSentences) {
		/* ************************/
		/* Count False/True Rates */
		/* ************************/
		for (int i = 1; i < labels.size(); i++) {
			int counter = 0;
			// Calculate True Positive
			for (int k = 0; k < classifiedSentences.size(); k++) {
				Label predicted = classifiedSentences.get(k)
						.getPredictedLabel();
				Label correct = classifiedSentences.get(k).getCorrectLabel();
				if (predicted.compareTo(labels.get(i)) == 0
						&& correct.compareTo(labels.get(i)) == 0) {
					counter++;
				}
			}
			truePositiv.put(labels.get(i), counter);
			
			// Calculate False Positive
			counter = 0;		
			for (int k = 0; k < classifiedSentences.size(); k++) {
				Label predicted = classifiedSentences.get(k)
						.getPredictedLabel();
				Label correct = classifiedSentences.get(k).getCorrectLabel();
				if (predicted.compareTo(labels.get(i)) == 0
						&& correct.compareTo(labels.get(i)) != 0) {
					counter++;
				}
			}
			falsePositiv.put(labels.get(i), counter);
			
			// Calculate True Negative
			counter = 0;		
			for (int k = 0; k < classifiedSentences.size(); k++) {
				Label predicted = classifiedSentences.get(k)
						.getPredictedLabel();
				Label correct = classifiedSentences.get(k).getCorrectLabel();
				if (predicted.compareTo(labels.get(i)) != 0
						&& correct.compareTo(labels.get(i)) != 0) {
					counter++;
				}
			}
			trueNegativ.put(labels.get(i), counter);
			
			// Calculate True Negative
			counter = 0;		
			for (int k = 0; k < classifiedSentences.size(); k++) {
				Label predicted = classifiedSentences.get(k)
						.getPredictedLabel();
				Label correct = classifiedSentences.get(k).getCorrectLabel();
				if (predicted.compareTo(labels.get(i)) != 0
						&& correct.compareTo(labels.get(i)) == 0) {
					counter++;
				}
			}
			falseNegativ.put(labels.get(i), counter);
		}
		
		/* ******************************/
		/* Compute Precision and Recall */
		/* ******************************/
		
		double precision = 0.0, recall = 0.0;
		double fScore, beta;
		double temp ;
		
		for(int i = 1; i < labels.size();i++){			
			//precision
			temp = (double)truePositiv.get(labels.get(i))/ (double)(1.0+truePositiv.get(labels.get(i))+ falsePositiv.get(labels.get(i)));
			precision += (((double)labelDocumentCounter.get(labels.get(i)) /(double)noSentences) * temp);
			if(Double.isNaN(precision)){
				System.out.println("At:"+i);
				System.out.println("X"+(double)truePositiv.get(labels.get(i)));
				System.out.println("Y"+falsePositiv.get(labels.get(i)));
			}
			
			//recall
			temp = (double)truePositiv.get(labels.get(i))/ (double)(1.0+truePositiv.get(labels.get(i))+ falseNegativ.get(labels.get(i)));
			recall += (((double)labelDocumentCounter.get(labels.get(i)) /(double)noSentences) * temp);
		}
		
		
		// compute f-score
		beta = 1.0;
		fScore = (1.0+Math.pow(beta, 2.0)) * (precision*recall)/(Math.pow(beta, 2.0)*precision+recall);
		System.out.println("Recall:"+recall);
		System.out.println("Precision:"+precision);
		System.out.println("FScore:"+fScore);
		
	}

	private double computeTreeInducedError(List<Sentence> classifiedSentences) {
		cumulativeTreeInducedError = 0;
		evaluatedSentence = 0;
		for(int i = 0; i < classifiedSentences.size();i++){
			Sentence sen = classifiedSentences.get(i);
			Tree<Label> predictedTree = labelMapping.get(sen.getPredictedLabel());
			Tree<Label> correctTree = labelMapping.get(sen.getCorrectLabel());
			cumulativeTreeInducedError += Tree.computeDistance(predictedTree,
					correctTree);
			evaluatedSentence++;
		}
		System.out.println("TIE:"+(double)cumulativeTreeInducedError/(double)evaluatedSentence);
		return (double)cumulativeTreeInducedError/(double)evaluatedSentence;
	}
}

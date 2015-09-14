package engine;

import java.util.HashMap;
import java.util.List;

import services.Logger;

import model.*;

/**
 * This Class is to evaluate the classification and extraction task.
 * @author Muhammed Demircan
 *
 */
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
		Logger.info("##### Calculate Precision and Recall ####");
		computePrecisionRecall(classifiedSentences);
		Logger.info("##### Calculate Tree Induced Error #####");
		computeTreeInducedError(classifiedSentences);
		Logger.info("##### Count Absolute Correctness #####");
		countAbsoluteCorrectness(classifiedSentences);
	}

	/**
	 * counts the absolute correctness of classification
	 * @param classifiedSentences
	 */
	private void countAbsoluteCorrectness(List<Sentence> classifiedSentences){
		int correctLabeling = 0, wrongLabeling = 0;
		int[] wrongerCounter = new int[labels.size()];
		for(int i = 0;i<classifiedSentences.size();i++){
			if(classifiedSentences.get(i).getCorrectLabel().compareTo(classifiedSentences.get(i).getPredictedLabel()) == 0)
				correctLabeling++;
			else{
				wrongLabeling++;
				for(int k = 0; k < labels.size();k++){
					if(classifiedSentences.get(i).getPredictedLabel().compareTo(labels.get(k))==0)
						wrongerCounter[k]++;
				}
			}
		}
		Logger.info("###################################");
		Logger.info("Correct Labeling:"+correctLabeling);
		Logger.info("Wrong Labeling: "+wrongLabeling);
		
		Logger.info("Mostly frequent wrong prediction into:");
		for(int i = 0; i < wrongerCounter.length;i++){
			System.out.println(labels.get(i).getName()+":"+wrongerCounter[i]);
		}
	}
	
	/**
	 * Compute the precision and recall for classified sentences
	 */
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
			
			//recall
			temp = (double)truePositiv.get(labels.get(i))/ (double)(1.0+truePositiv.get(labels.get(i))+ falseNegativ.get(labels.get(i)));
			recall += (((double)labelDocumentCounter.get(labels.get(i)) /(double)noSentences) * temp);
		}
		
		
		// compute f-score
		beta = 1.0;
		fScore = (1.0+Math.pow(beta, 2.0)) * (precision*recall)/(Math.pow(beta, 2.0)*precision+recall);
		
		Logger.info("Recall: "+recall);
		Logger.info("Precision: "+precision);
		Logger.info("FScore: "+fScore);
		
	}

	/**
	 * computes the tree induced error for a list of classified sentences
	 * @return tree induced error
	 */
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
		Logger.info("TreeInducedError:"+(double)cumulativeTreeInducedError/(double)evaluatedSentence);
		return (double)cumulativeTreeInducedError/(double)evaluatedSentence;
	}
}

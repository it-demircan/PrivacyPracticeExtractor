package engine.classifier;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import model.Label;
import model.Tree;
import model.Vector;

/**
 * Implementation of the "Hieron" Algorithm, which is an hierarchical classifier
 * 
 * @author Muhammed Demircan
 * 
 */
public class Hieron implements IClassifier {
	HashMap<Label, Tree<Label>> labelMapping;
	List<Label> labels;
	boolean reqDataSet;
	
	
	HashMap<Label,List<Vector>> changedFeatures;

	public Hieron() {
		reqDataSet = false;
		changedFeatures = new HashMap<Label,List<Vector>>();
	}

	/**
	 * Trains the algorithm with a given training set based on (hierarchical)
	 * structure given by policy root node.
	 */
	public List<Label> train(HashMap<Vector, Label> trainingSet,
			Tree<Label> policyRoot) throws Exception {
		if (!reqDataSet) {
			throw new Exception("You must first set required Data to Hieron!");
		}
		boolean labelsInit = false;
		
		//init save vector fields
		for(int i = 1; i< labels.size();i++){
			if(changedFeatures.get(labels.get(i)) == null)
				changedFeatures.put(labels.get(i), new LinkedList<Vector>());
		}

		Iterator it = trainingSet.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			Vector nextVector = (Vector) pair.getKey();
			Label nextLabel = (Label) pair.getValue();

			if (!labelsInit) {
				for (int i = 0; i < labels.size(); i++) {
					labels.get(i).init(nextVector.getDimension());
				}
			}
			this.update(nextVector, nextLabel, predictLabel(nextVector));
			
			//Storing vector for average calculation (optional)
			for(int i = 1; i< labels.size();i++){
				changedFeatures.get(labels.get(i)).add(labels.get(i).getFeature());
			}
					
			it.remove(); // avoids a ConcurrentModificationException
		}
		
		//compute average
		for(int i = 1; i< labels.size();i++){
			List<Vector> storedVectors = changedFeatures.get(labels.get(i));
			Vector result = new Vector(storedVectors.get(1).getDimension());
			for(int k = 0 ; k < storedVectors.size();k++)
			{
				result = Vector.add(result,storedVectors.get(k));
			}
			
			labels.get(i).setVector(Vector.multiplyScalar((1.0/((double)storedVectors.size())), result));
		}
		
		return labels;
	}

	/**
	 * Predicts the label/class for an instance vector
	 */
	public Label predictLabel(Vector instance) throws Exception {
		if (!reqDataSet) {
			throw new Exception("You must first set required Data to Hieron! ");
		}
		Label prediction = labels.get(1);
		double value = Vector.computeInnerProduct(
				this.computePrototype(labels.get(1)), instance);

		for (int i = 1; i < labels.size(); i++) {
			if (Vector.computeInnerProduct(
					this.computePrototype(labels.get(i)), instance) > value) {
				value = Vector.computeInnerProduct(
						this.computePrototype(labels.get(i)), instance);
				prediction = labels.get(i);
			}
		}
		return prediction;
	}

	/**
	 * Updates the feature vectors of all nodes(labels) on symmetric difference
	 * set of P(correctLabel) and P(predictedLabel)
	 * 
	 * @param instance
	 *            - Instance vector, which should be classified
	 * @param correctLabel
	 *            - the correct label/class of the instance vector
	 * @param predictedLabel
	 *            - the predicted label of the instance vector
	 * @throws Exception
	 */
	private void update(Vector instance, Label correctLabel,
			Label predictedLabel) throws Exception {
		double sufferLoss = this.computeSufferLoss(instance, correctLabel,
				predictedLabel);
		double distance = Tree.computeDistance(labelMapping.get(correctLabel),
				labelMapping.get(predictedLabel));
		double norm = Vector.computeInnerProduct(instance, instance);
		//Skip zero vector and don't update correct labeling
		if (norm > 0.0 && distance > 0.0) {

			double lagrangeMultiplier = sufferLoss / (distance * norm);

			// Change feature vector to correct label
			List<Tree> subPath = Tree.getExcludingPath(
					labelMapping.get(correctLabel),
					labelMapping.get(predictedLabel));

			for (Tree tree : subPath) {
				Label label = (Label) tree.getData();
				label.setVector(Vector.add(label.getFeature(),
						Vector.multiplyScalar(lagrangeMultiplier, instance)));

				if (Double.isNaN((label.getFeature().getValue(0)))) {
					System.out.println("Hallo");
				}
			}

			subPath.clear();

			// Change feature vector to predicted label
			subPath = Tree.getExcludingPath(labelMapping.get(predictedLabel),
					labelMapping.get(correctLabel));
			for (Tree tree : subPath) {
				Label label = (Label) tree.getData();
				label.setVector(Vector.add(label.getFeature(), Vector
						.multiplyScalar((-1.0) * lagrangeMultiplier, instance)));

				if (Double.isNaN((label.getFeature().getValue(0)))) {
					System.out.println("Hallo");
				}
			}
		}
	}

	/**
	 * Compute the prototype vector for an label/class
	 * 
	 * @return Prototype vector
	 * @throws Exception
	 */
	private Vector computePrototype(Label recentLabel) throws Exception {
		Tree<Label> node = labelMapping.get(recentLabel);
		Vector prototype = new Vector(recentLabel.getFeature().getDimension());

		while (node != null) {
			prototype = Vector.add(prototype, node.getData().getFeature());
			node = node.getParent();
		}

		return prototype;
	}

	/**
	 * Computes the suffer loss between the predicted and correct label based on
	 * the distance between two labels.
	 * 
	 * @param instance
	 *            - instance vector representing data to classify
	 * @param correctLabel
	 *            - the correct label of the instance vector
	 * @param predictedLabel
	 *            - the predicted label of the instance vector
	 * @return - suffer loss (based on convex hingeloss function)
	 * @throws Exception
	 */
	private double computeSufferLoss(Vector instance, Label correctLabel,
			Label predictedLabel) throws Exception {
		double res = 0.0;

		Vector pPrototype = computePrototype(predictedLabel);
		Vector cPrototype = computePrototype(correctLabel);

		double distance = Tree.computeDistance(labelMapping.get(correctLabel),
				labelMapping.get(predictedLabel));
		distance = Math.sqrt(distance);

		res = Vector.computeInnerProduct(pPrototype, instance)
				- Vector.computeInnerProduct(cPrototype, instance) + distance;

		return (res > 0 ? res : 0.0);
	}

	/**
	 * This Data is needed for all calculations and have to be set before first
	 * usage.
	 * 
	 * @param lMapping
	 *            - Mapping between label and tree node
	 * @param labels
	 *            - All labels/classes
	 */
	public void setRequiredData(HashMap<Label, Tree<Label>> lMapping,
			List<Label> labels) {
		labelMapping = lMapping;
		this.labels = labels;
		reqDataSet = true;
	}
}

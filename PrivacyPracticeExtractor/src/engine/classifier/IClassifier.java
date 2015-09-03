package engine.classifier;

import java.util.HashMap;
import java.util.List;

import model.Label;
import model.Tree;
import model.Vector;

public interface IClassifier {
	public List<Label> train(HashMap<Vector,Label> trainingSet, Tree<Label> policyRoot) throws Exception;
	public Label predictLabel(Vector instance) throws Exception;
	public void setRequiredData(HashMap<Label, Tree<Label>> lMapping, List<Label> labels);
}

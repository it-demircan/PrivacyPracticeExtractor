package model;

import java.util.LinkedList;
import java.util.List;

/**
 * Implements a generic tree structure, whose items are comparable.
 * Contains static method for distance calculation.
 * @author Muhammed Demircan
 */
public class Tree<T extends Comparable<T>> implements Comparable<Tree> {

	/* Static Methods */
	public static int computeDistance(Tree nodeOne, Tree nodeTwo) {
		int length;
		Tree jumper = getFirstEqualAnchestor(nodeOne, nodeTwo);
		length = nodeOne.distanceToRoot() + nodeTwo.distanceToRoot() - 2
				* jumper.distanceToRoot();
		return length;
	}

	/**
	 * Compute the nodes to "mainNode" excluding all common node on the path to "excludingNode"
	 * @return P(mainNode)\P(excludingNode)
	 */
	public static List<Tree> getExcludingPath(Tree mainNode, Tree excludingNode) {
		Tree anchestor = getFirstEqualAnchestor(mainNode, excludingNode);
		return getRootedSubPath(anchestor, mainNode);
	}

	private static List<Tree> getRootedSubPath(Tree startPoint, Tree endPoint) {
		Tree jumper = endPoint;
		List<Tree> subPath = new LinkedList<Tree>();
		
		if(startPoint.compareTo(endPoint) == 0)
			return subPath;

		do {
			subPath.add(jumper);
			jumper = jumper.parent;
		} while (jumper != null && jumper.compareTo(startPoint) != 0);
		return subPath;
	}

	private static Tree getFirstEqualAnchestor(Tree nodeOne, Tree nodeTwo) {
		Tree anchestor = null;
		Tree jumper = nodeOne;
		Tree secondJumper = nodeTwo;

		while (jumper != null) {
			secondJumper = nodeTwo;

			while (secondJumper != null) {
				if (jumper.compareTo(secondJumper) != 0) {
					secondJumper = secondJumper.parent;
				} else {
					anchestor = secondJumper;
					secondJumper = null;
					jumper = null;
				}
			}
			if (jumper != null)
				jumper = jumper.parent;
		}

		return anchestor;
	}

	/* (non static) Class Implementation */
	T data;
	Tree<T> parent;
	List<Tree<T>> children;

	public Tree(T data) {
		this.data = data;
		this.children = new LinkedList<Tree<T>>();
	}

	public Tree<T> addChild(T child) {
		Tree<T> childNode = new Tree<T>(child);
		childNode.parent = this;
		this.children.add(childNode);
		return childNode;
	}

	public int distanceToRoot() {
		int length = 0;
		Tree<T> node = this;
		while (node.parent != null) {
			length++;
			node = node.parent;
		}

		return length;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public Tree<T> getParent() {
		return parent;
	}

	public void setParent(Tree<T> parent) {
		this.parent = parent;
	}

	public List<Tree<T>> getChildren() {
		return children;
	}

	public void setChildren(List<Tree<T>> children) {
		this.children = children;
	}

	@Override
	public int compareTo(Tree arg0) {
		return this.data.compareTo((T) arg0.data);
	}
}

package model;
import java.util.LinkedList;
import java.util.List;

public class Tree<T extends Comparable<T>> implements Comparable<Tree> {
	
	/*                  Static Methods                         */
	public static int computeDistance(Tree nodeOne, Tree nodeTwo){
		int length;
		Tree jumper;
		Tree deeperNode, shorterNode;
		boolean subPath = false;
		
		if(nodeOne.distanceToRoot()> nodeTwo.distanceToRoot()){
			deeperNode = nodeOne;
			shorterNode = nodeTwo;
		}
		else{
			deeperNode = nodeTwo;
			shorterNode = nodeOne;
		}
		
		//Check if the edges of the shorter node is subset of the deeper node edges. 
		
		jumper = deeperNode;
		while(jumper != null){
			
			if(jumper.compareTo(shorterNode) != 0){
				jumper = jumper.parent;
			}else{
				subPath = true;
				jumper = null;//Exit
			}
		}
		
		if(subPath){
			length = deeperNode.distanceToRoot() - shorterNode.distanceToRoot();
		}else{
			length = deeperNode.distanceToRoot() + shorterNode.distanceToRoot();
		}
		
		return length;
	}
	
	/*                (non static) Class Implementation                         */	
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
	
	public int distanceToRoot(){
		int length = 0;
		Tree<T> node = this;
		while(node.parent != null){
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

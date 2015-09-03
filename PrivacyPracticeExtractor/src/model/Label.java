package model;

import java.io.Serializable;

public class Label implements Comparable<Label>, Serializable{
	private static final long serialVersionUID = -2938249652667559326L;

	/*  static implementation */
	public static String getLabelPath(Tree<Label> labelNode){
		String res = "";
		
		if(labelNode.parent == null)
			return res;
		else
			return getLabelPath(labelNode.parent) + "\\" +labelNode.getData().getName();
	}
	
	/* *************************  */
	/*  non static implementation */
	/* *************************  */
	String name;
	
	//equals w^{v}
	Vector feature;
	boolean isInit;
	
	public Label(String name){
		this.name = name;
		isInit = false;
	}
	
	public Vector getFeature(){
		return feature;
	}
	public void setVector(Vector feature){
		this.feature = feature;
	}
	
	public void init(int dimension){
		if(!isInit){
			feature = new Vector(dimension);
			isInit = true;
		}
	}
	
	public String getName(){
		return name;
	}
	
	@Override
	public int compareTo(Label arg0) {
		if(this.name.equals(arg0.name))
			return 0;
		else 
			return 1;
	}

}

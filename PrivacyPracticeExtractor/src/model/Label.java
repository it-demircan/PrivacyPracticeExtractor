package model;

public class Label implements Comparable<Label>{
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

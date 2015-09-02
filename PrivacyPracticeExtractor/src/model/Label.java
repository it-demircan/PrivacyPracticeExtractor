package model;

public class Label implements Comparable<Label>{
	String name;
	
	public Label(String name){
		this.name = name;
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

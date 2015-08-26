package model;

public class Vector {
	double[] values;
	
	public Vector(int N){
		values = new double[N];
	}
	
	public void setValue(int pos,double value){
		values[pos] = value;
	}
	
	public double getValue(int pos){
		return values[pos];
	}
	
	@Override
	public String toString(){
		String res = "[";
		
		for(int i = 0;i < values.length;i++){
			res+= values[i]+";";
		}
		res+= "]";
		return res;
	}
}

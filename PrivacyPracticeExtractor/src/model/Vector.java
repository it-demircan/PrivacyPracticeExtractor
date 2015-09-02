package model;

public class Vector {
	/* static methods */
	/**
	 * Vector addition
	 * @param vectors List of Vectors
	 * @return Summation over all vectors
	 * @throws Exception
	 */
	public static Vector add(Vector... vectors) throws Exception {
		if (vectors.length == 0) {
			throw new Exception("No Vectors to add.");
		}
		int dim = vectors[0].getDimension();
		Vector res = new Vector(dim);
		for (Vector vec : vectors) {
			for (int i = 0; i < dim; i++)
				res.setValue(i, res.getValue(i) + vec.getValue(i));
		}
		return res;
	}
	
	
	/**
	 * Compute the inner product of two vectors
	 * @return inner product
	 */
	public static double computeInnerProduct(Vector a, Vector b){
		int dim = a.getDimension();
		double res = 0.0;
		
		for(int i = 0; i <dim; i++){
			res += a.getValue(i)*b.getValue(i);
		}
		return res;
	}
	
	/**
	 * Multiplies a vector with a scalar
	 */
	public static Vector multiplyScalar(double scalar, Vector vector){
		Vector res = new Vector(vector.getDimension());
		for(int i = 0; i< vector.getDimension();i++){
			res.setValue(i, vector.getValue(i)*scalar);
		}
		return res;
	}
	/* non static implementation */
	double[] values;

	public Vector(int N) {
		values = new double[N];
	}

	public void setValue(int pos, double value) {
		values[pos] = value;
	}

	public double getValue(int pos) {
		return values[pos];
	}

	public int getDimension() {
		return values.length;
	}

	@Override
	public String toString() {
		String res = "[";

		for (int i = 0; i < values.length; i++) {
			res += values[i] + ";";
		}
		res += "]";
		return res;
	}
}

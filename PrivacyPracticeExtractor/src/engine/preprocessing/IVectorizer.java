package engine.preprocessing;

import java.util.HashMap;

import model.*;

public interface IVectorizer {
	public Vector mapToVector(Sentence sen);
	public HashMap<Sentence,Vector> mapToVector(Text text); 
}

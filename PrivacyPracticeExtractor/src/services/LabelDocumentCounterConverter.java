package services;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import model.Dictionary;
import model.Label;

public class LabelDocumentCounterConverter {
	public static HashMap<Label,Integer> read(String counterFilePath) throws IOException, ClassNotFoundException {
		HashMap<Label,Integer>  counter;
		FileInputStream fis = new FileInputStream(counterFilePath);
		ObjectInputStream ois = new ObjectInputStream(fis);
		counter = (HashMap<Label,Integer> ) ois.readObject();
		ois.close();
		return counter;
	}

	public static void write(HashMap<Label,Integer> counter, String counterFilePath) throws IOException {
		FileOutputStream fos = new FileOutputStream(
				counterFilePath);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(counter);
		oos.flush();
		oos.close();
	}
}

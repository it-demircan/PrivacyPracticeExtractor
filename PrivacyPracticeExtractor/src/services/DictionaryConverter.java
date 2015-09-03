package services;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import model.*;

public class DictionaryConverter {
	public static Dictionary read(String corpusFilePath) throws IOException, ClassNotFoundException {
		Dictionary corpus;
		FileInputStream fis = new FileInputStream(corpusFilePath);
		ObjectInputStream ois = new ObjectInputStream(fis);
		corpus = (Dictionary) ois.readObject();
		ois.close();
		return corpus;
	}

	public static void write(Dictionary corpus, String corpusFilePath) throws IOException {
		FileOutputStream fos = new FileOutputStream(
				corpusFilePath);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(corpus);
		oos.flush();
		oos.close();
	}
}

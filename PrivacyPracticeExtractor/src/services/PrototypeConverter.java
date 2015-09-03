package services;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import model.*;

public class PrototypeConverter {
	public static List<Label> read(String prototypeFilePath) throws IOException, ClassNotFoundException {
		List<Label> labels;
		FileInputStream fis = new FileInputStream(prototypeFilePath);
		ObjectInputStream ois = new ObjectInputStream(fis);
		labels = (List<Label>) ois.readObject();
		ois.close();
		return labels;
	}

	public static void write(List<Label> labels, String labelFilePath) throws IOException {
		FileOutputStream fos = new FileOutputStream(
				labelFilePath);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(labels);
		oos.flush();
		oos.close();
	}
}

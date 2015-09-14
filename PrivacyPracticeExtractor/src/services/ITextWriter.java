package services;

import java.io.IOException;

public interface ITextWriter {
	public void write(String text, String filePath) throws IOException;
}

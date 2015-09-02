package services;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TextWriter implements ITextWriter{
	@Override
	public void write(String text, String filePath) throws IOException {
		  Path path = Paths.get(filePath);
		    try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)){
		        writer.write(text);
		    }
	}
	
}

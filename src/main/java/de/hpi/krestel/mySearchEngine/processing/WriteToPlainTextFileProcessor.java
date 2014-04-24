package de.hpi.krestel.mySearchEngine.processing;

import java.io.*;
import java.util.List;

public class WriteToPlainTextFileProcessor extends AbstractProcessor {

	PrintWriter writer;
	public WriteToPlainTextFileProcessor() {
		try {
			writer = new PrintWriter(new BufferedWriter(new FileWriter("data/plain-texts.txt")));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	@Override
	public List<String> process(List<String> input) {
		writer.print(input.get(0));
		return input;
	}

	public void flush() {
		writer.close();
	}
}

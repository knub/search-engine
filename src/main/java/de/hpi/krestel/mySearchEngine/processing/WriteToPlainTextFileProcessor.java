package de.hpi.krestel.mySearchEngine.processing;

import edu.stanford.nlp.ling.CoreLabel;

import java.io.*;
import java.util.List;

public class WriteToPlainTextFileProcessor extends Processor {

	PrintWriter writer;
	public WriteToPlainTextFileProcessor() {
		try {
			writer = new PrintWriter(new BufferedWriter(new FileWriter("data/plain-texts.txt")));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	@Override
	public List<CoreLabel> process(List<CoreLabel> input) {
		writer.print(input.get(0).value());
		return input;
	}

	public void finished() {
		writer.close();
	}
}

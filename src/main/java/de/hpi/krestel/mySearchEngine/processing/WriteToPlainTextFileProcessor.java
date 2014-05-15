package de.hpi.krestel.mySearchEngine.processing;

import de.hpi.krestel.mySearchEngine.searching.SnippetReader;
import edu.stanford.nlp.ling.CoreLabel;

import java.io.*;
import java.util.List;

public class WriteToPlainTextFileProcessor extends Processor {

	public final static String PLAIN_TEXT_FILE = "data/plain-texts.txt";
	public final static String PLAIN_TEXT_OFFSETS_FILE = "data/plain-texts-lengths.txt";

	final String NULL_STRING = new String(new byte[SnippetReader.SNIPPET_LENGTH]);

	PrintWriter textWriter;
	DataOutputStream byteCountWriter;
	long accByteCount = 0;

	// Taken from this answer: http://stackoverflow.com/questions/19852460/get-size-of-string-w-encoding-in-bytes-without-converting-to-byte
	class CountingOutputStream extends OutputStream {
		int byteCount;

		@Override public void write(int b) {
			++byteCount;
		}
		@Override public void write(byte[] b) {
		byteCount += b.length;
		}
		@Override public void write(byte[] b, int offset, int len) {
			byteCount += len;
		}

		public int getByteCount() {
			return byteCount;
		}
	}

	public WriteToPlainTextFileProcessor() {
		try {
			textWriter = new PrintWriter(new File(PLAIN_TEXT_FILE), "UTF-8");
			byteCountWriter = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(PLAIN_TEXT_OFFSETS_FILE)));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	@Override
	public List<CoreLabel> process(List<CoreLabel> input) {
		try {
			String text = input.get(0).value();
			int byteCount = getByteCount(text);
			byteCountWriter.writeLong(accByteCount);

			accByteCount += byteCount;
			textWriter.print(text);
			// write SNIPPET_LENGTH null bytes after each text, so we can show the surrounding of a hit without running
			// into another file
			textWriter.print(NULL_STRING);
			accByteCount += SnippetReader.SNIPPET_LENGTH;
			return input;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public int getByteCount(String text) {
		try {
			CountingOutputStream cos = new CountingOutputStream();
			Writer writer = new OutputStreamWriter(cos, "UTF-8");
			writer.write(text);
			writer.flush();
			return cos.getByteCount();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void finished() {
		try {
			textWriter.close();
			byteCountWriter.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}

package de.hpi.krestel.mySearchEngine.searching;

import de.hpi.krestel.mySearchEngine.processing.WriteToPlainTextFileProcessor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class SnippetReader {

	public static final int SNIPPET_LENGTH = 100;
	RandomAccessFile texts;
	RandomAccessFile offsets;

	public SnippetReader() {
		try {
			texts = new RandomAccessFile(WriteToPlainTextFileProcessor.PLAIN_TEXT_FILE, "r");
			offsets = new RandomAccessFile(WriteToPlainTextFileProcessor.PLAIN_TEXT_OFFSETS_FILE, "r");
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public String readSnippet(int docId, int inFileOffset, int length) {
		try {
			// one long is 8 bytes, so we have to multiply by 8
			offsets.seek(docId * 8);
			long offset = offsets.readLong();
//			texts.seek(offset + inFileOffset);
			texts.seek(Math.max(offset + inFileOffset - SNIPPET_LENGTH / 2, 0));
			byte[] text = new byte[SNIPPET_LENGTH * 2];
			texts.read(text);
			String snippet = new String(text);
			snippet = snippet.replace("\n", " ").replace("\0", "");
			return snippet.substring(0, Math.min(SNIPPET_LENGTH, snippet.length()));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}
}

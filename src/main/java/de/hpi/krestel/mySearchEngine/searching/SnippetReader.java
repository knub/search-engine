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
			long seekStart = Math.max(offset + inFileOffset - SNIPPET_LENGTH / 2, 0);
			int searchBeforeLength = (int) (offset + inFileOffset - seekStart);
			texts.seek(seekStart);

			byte[] text = new byte[SNIPPET_LENGTH * 2];
			texts.read(text);
			String snippet = new String(text);
			snippet = snippet.replace("\n", " ").replace("\0", "");
			int snippetlength =  snippet.length();
			System.out.println(searchBeforeLength);
			snippet = snippet.substring(0, Math.min(searchBeforeLength, snippetlength)) +
					"\033[4m" + snippet.substring(Math.min(searchBeforeLength, snippetlength), Math.min(searchBeforeLength + length, snippetlength)) + "\033[0;m" +
					snippet.substring(Math.min(searchBeforeLength + length, snippetlength), snippetlength);
			return snippet.substring(0, Math.min(SNIPPET_LENGTH, snippet.length()));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}
}

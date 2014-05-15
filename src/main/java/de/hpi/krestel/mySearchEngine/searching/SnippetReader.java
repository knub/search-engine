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


			long beforeSeekStart = Math.max(offset + inFileOffset - SNIPPET_LENGTH / 2, 0);
			texts.seek(beforeSeekStart);
			byte[] beforeBytes = new byte[SNIPPET_LENGTH / 2];
			texts.read(beforeBytes);
			String beforeText = new String(beforeBytes).replace("\n", "").replace("\0", "");


			long middleSeekStart = offset + inFileOffset;
			texts.seek(middleSeekStart);
			byte[] middleBytes = new byte[length];
			texts.read(middleBytes);
			String middleText = new String(middleBytes);

			long afterSeekStart = offset + inFileOffset + length;
			texts.seek(afterSeekStart);
			byte[] afterBytes = new byte[SNIPPET_LENGTH];
			texts.read(afterBytes);
			String afterText = new String(afterBytes).replace("\n", "").replace("\0", "");



			String underlineSequence = "\033[4m";
			String stopSequence = "\033[0;m";
			String snippet = beforeText + underlineSequence + middleText + stopSequence + afterText;
			return snippet.substring(0, Math.min(SNIPPET_LENGTH, snippet.length()));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}
}

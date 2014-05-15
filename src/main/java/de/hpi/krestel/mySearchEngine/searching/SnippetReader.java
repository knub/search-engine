package de.hpi.krestel.mySearchEngine.searching;

import de.hpi.krestel.mySearchEngine.processing.WriteToPlainTextFileProcessor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SnippetReader {

	public static final int DEFAULT_SNIPPET_LENGTH = 150;
	private final int maxSnippetLength;
	RandomAccessFile texts;
	RandomAccessFile offsets;

	String endFoundSequence   = "\033[0;m"; // underline stop

	public SnippetReader() {
		this(DEFAULT_SNIPPET_LENGTH);
	}

	public SnippetReader(int maxSnippetLength) {
		try {
			texts = new RandomAccessFile(WriteToPlainTextFileProcessor.PLAIN_TEXT_FILE, "r");
			offsets = new RandomAccessFile(WriteToPlainTextFileProcessor.PLAIN_TEXT_OFFSETS_FILE, "r");
			this.maxSnippetLength = maxSnippetLength;
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public String readSnippet(int docId, int inFileOffset, int length) {
		try {
			// one long is 8 bytes, so we have to multiply by 8
			offsets.seek(docId * 8);
			long offset = offsets.readLong();


			long beforeSeekStart = offset + inFileOffset - maxSnippetLength / 2;
			int beforeReadCount = maxSnippetLength / 2;
			if (beforeSeekStart < 0) {
				beforeReadCount += beforeSeekStart;
				beforeSeekStart = 0;
			}
			texts.seek(beforeSeekStart);
			byte[] beforeBytes = new byte[beforeReadCount];
			texts.read(beforeBytes);
			String beforeText = new String(beforeBytes).replace("\n", "");
			int nullByteIndex = beforeText.indexOf("\0");
			if (nullByteIndex != -1) {
				beforeText = beforeText.substring(nullByteIndex);
			}
			int startHit = beforeText.length();


			long middleSeekStart = offset + inFileOffset;
			texts.seek(middleSeekStart);
			byte[] middleBytes = new byte[length];
			texts.read(middleBytes);
			String middleText = new String(middleBytes);

			long afterSeekStart = offset + inFileOffset + length;
			texts.seek(afterSeekStart);
			byte[] afterBytes = new byte[maxSnippetLength];
			texts.read(afterBytes);
			String afterText = new String(afterBytes).replace("\n", "");
			nullByteIndex = afterText.indexOf("\0");
			if (nullByteIndex != -1) {
				afterText = afterText.substring(0, nullByteIndex);
			}

			String snippet = beforeText + startFoundSequence + middleText + endFoundSequence + afterText;
			snippet = snippet.substring(0, Math.min(maxSnippetLength, snippet.length()));

			int firstSpace = -1;
			int lastSpace = snippet.length();
			Pattern pattern = Pattern.compile("[\\s  ]");
			Matcher matcher = pattern.matcher(snippet);
			while (matcher.find()) {
				if (firstSpace == -1)
					firstSpace = matcher.start();
				lastSpace = matcher.start();
			}
			firstSpace = firstSpace > startHit ?  0 : firstSpace;
			snippet = snippet.substring(firstSpace, lastSpace);
			return snippet;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	/*
	 * Getters and Setters
	 */
	public String getStartFoundSequence() {
		return startFoundSequence;
	}

	public String getEndFoundSequence() {
		return endFoundSequence;
	}

	String startFoundSequence = "\033[4m";  // underline

	public void setEndFoundSequence(String endFoundSequence) {
		this.endFoundSequence = endFoundSequence;
	}

	public void setStartFoundSequence(String startFoundSequence) {
		this.startFoundSequence = startFoundSequence;
	}
}

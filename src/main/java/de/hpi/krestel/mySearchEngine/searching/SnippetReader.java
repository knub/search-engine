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

//	String startFoundSequence = "\033[4m";  // underline
//	String endFoundSequence   = "\033[0;m"; // underline stop
	String startFoundSequence = "##";  // underline
	String endFoundSequence   = "##"; // underline stop

	public SnippetReader(String directory) {
		this(DEFAULT_SNIPPET_LENGTH, directory);
	}

	public SnippetReader(int maxSnippetLength, String directory) {
		try {
            texts = new RandomAccessFile(directory + WriteToPlainTextFileProcessor.PLAIN_TEXT_FILE, "r");
			offsets = new RandomAccessFile(directory + WriteToPlainTextFileProcessor.PLAIN_TEXT_OFFSETS_FILE, "r");
			this.maxSnippetLength = maxSnippetLength;
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

    public String readSnippet(int docId, int inFileOffset, int wordLength)
    {
        int surroundingLetters = (maxSnippetLength - wordLength) / 2;
        int beforeReadCount = (surroundingLetters < inFileOffset) ? surroundingLetters : inFileOffset;
        int afterReadCount = maxSnippetLength - wordLength - beforeReadCount;

        String[] textParts = readParts(docId, inFileOffset, wordLength, beforeReadCount, afterReadCount);
        String beforeText = textParts[0];
        String middleText = textParts[1];
        String afterText  = textParts[2];

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
        firstSpace = firstSpace > beforeText.length() ?  0 : firstSpace;
        snippet = snippet.substring(firstSpace, lastSpace);
        return snippet
                .replace("'''", "")
                .replace("''", "")
                .replace("======", "")
                .replace("=====", "")
                .replace("====", "")
                .replace("===", "")
                .replace("==", "")
                .replace("<u>", "")
                .replace("</u>", "")
                .replace("**", "")
                .replace("[[", "")
                .replace("]]", "")
                .replace("{{", "")
                .replace("}}", "");
    }

    public String[] readParts(int docId, int inFileOffset, int wordLength, int beforeReadCount, int afterReadCount)
    {
        String beforeText, middleText, afterText;
        try {
            // one long is 8 bytes, so we have to multiply by 8
            offsets.seek(docId * 8);
            long docStartOffset = offsets.readLong();

            long beforeSeekStart = docStartOffset + inFileOffset - beforeReadCount;
            texts.seek(beforeSeekStart);

            beforeText = readFromTexts(beforeReadCount, true);
            middleText = readFromTexts(wordLength, false);
            afterText = readFromTexts(afterReadCount, true);
        } catch (IOException e) {
            System.out.println("Cannot read a PlainText file (plain-texts.txt or plain-text-lengths.txt");
            throw new RuntimeException(e);
        }
        return new String[]{beforeText, middleText, afterText};
    }

    private String readFromTexts(int byteCount, boolean trim) throws IOException
    {
        byte[] beforeBytes = new byte[byteCount];
        texts.read(beforeBytes);
        String text = new String(beforeBytes).replace("\n", "");
        if (trim) {
            int nullByteIndex = text.indexOf("\0");
            if (nullByteIndex != -1) {
                text = text.substring(nullByteIndex + 1);
            }
        }
        return text;
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

	public void setEndFoundSequence(String endFoundSequence) {
		this.endFoundSequence = endFoundSequence;
	}

	public void setStartFoundSequence(String startFoundSequence) {
		this.startFoundSequence = startFoundSequence;
	}
}

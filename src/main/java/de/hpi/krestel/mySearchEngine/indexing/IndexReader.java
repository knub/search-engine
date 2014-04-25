package de.hpi.krestel.mySearchEngine.indexing;

import de.hpi.krestel.mySearchEngine.domain.DocumentEntry;
import de.hpi.krestel.mySearchEngine.domain.OccurrenceMap;
import de.hpi.krestel.mySearchEngine.domain.WordMap;
import de.hpi.krestel.mySearchEngine.util.stream.Bit23Reader;
import de.hpi.krestel.mySearchEngine.util.stream.BitInputStream;
import de.hpi.krestel.mySearchEngine.util.stream.EliasGammaReader;
import gnu.trove.list.array.TByteArrayList;

import javax.swing.text.Document;
import java.io.*;
import java.util.Map;

public class IndexReader {

	private BitInputStream bis;
	private Bit23Reader bit23Reader;
	private EliasGammaReader eliasGammaReader;

	public IndexReader(String fileName) {
		try {
			initializeReader(fileName);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	private void initializeReader(String fileName) throws FileNotFoundException {
		bis = new BitInputStream(new FileInputStream(fileName));
		bit23Reader = new Bit23Reader(bis);
		eliasGammaReader = new EliasGammaReader(bis);
	}

	public WordMap read() {
		try {
			String word = readWord();
			OccurrenceMap occurrenceMap = readOccurenceMap();

			WordMap wordMap = new WordMap();
			wordMap.put(word, occurrenceMap);
			return wordMap;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private OccurrenceMap readOccurenceMap() throws IOException {
		OccurrenceMap occurrenceMap = new OccurrenceMap();
		int documentCount = eliasGammaReader.read();
		for (int i = 1; i <= documentCount; i++) {
			int documentId    = bit23Reader.read();
			System.out.println("docid" + documentId);
			DocumentEntry docEntry = new DocumentEntry();
			occurrenceMap.put(documentId, docEntry);
			int occurCount = eliasGammaReader.read();
		}
		return occurrenceMap;
	}

	private String readWord() throws IOException {
		TByteArrayList wordBytes = new TByteArrayList();
		byte currentByte = (byte) bis.read();
		while (currentByte != 0) {
			wordBytes.add(currentByte);
			currentByte = (byte) bis.read();
		}
		String word = new String(wordBytes.toArray());
		System.out.println("Word: " + word);
		return  word;
	}
}

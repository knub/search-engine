package de.hpi.krestel.mySearchEngine.indexing;

import de.hpi.krestel.mySearchEngine.domain.DocumentEntry;
import de.hpi.krestel.mySearchEngine.domain.OccurrenceMap;
import de.hpi.krestel.mySearchEngine.domain.WordMap;
import de.hpi.krestel.mySearchEngine.util.stream.Bit23Reader;
import de.hpi.krestel.mySearchEngine.util.stream.BitInputStream;
import de.hpi.krestel.mySearchEngine.util.stream.EliasDeltaReader;
import de.hpi.krestel.mySearchEngine.util.stream.EliasGammaReader;
import gnu.trove.list.array.TByteArrayList;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class IndexReader {

	private BitInputStream bis;
	private Bit23Reader bit23Reader;
	private EliasGammaReader eliasGammaReader;
	private EliasDeltaReader eliasDeltaReader;

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
		eliasDeltaReader = new EliasDeltaReader(bis);
	}

	public WordMap read() {
		try {
			String word = readWord();
			// check for end of file
			if (word == null)
				return null;
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
			DocumentEntry docEntry = readDocumentEntry();
			occurrenceMap.put(documentId, docEntry);
		}
		return occurrenceMap;
	}

	private DocumentEntry readDocumentEntry() throws IOException {
		int occurCount = eliasGammaReader.read();
		DocumentEntry docEntry = new DocumentEntry();
		int lastPos = 0;
		int lastOffset = 0;
		for (int i = 0; i < occurCount; i++) {
			int currentPos = eliasDeltaReader.read();
			if (i == 0)
				lastPos = currentPos;
			else
				lastPos = lastPos + currentPos;
			docEntry.positions.add(lastPos);

			int currentOffset = eliasDeltaReader.read();
			if (i == 0)
				lastOffset = currentOffset;
			else
				lastOffset = lastOffset + currentOffset;
			docEntry.offsets.add(lastOffset);

			docEntry.lengths.add(eliasGammaReader.read());
		}
		return docEntry;
	}

	private String readWord() throws IOException {
		TByteArrayList wordBytes = new TByteArrayList();
		byte currentByte = (byte) bis.read();
		if (currentByte == -1)
			return null;
		while (currentByte != 0) {
			wordBytes.add(currentByte);
			currentByte = (byte) bis.read();
		}
		String word = new String(wordBytes.toArray());
		return  word;
	}
}

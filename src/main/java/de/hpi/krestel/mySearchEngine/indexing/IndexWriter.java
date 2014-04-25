package de.hpi.krestel.mySearchEngine.indexing;

import de.hpi.krestel.mySearchEngine.domain.DocumentEntry;
import de.hpi.krestel.mySearchEngine.domain.OccurrenceMap;
import de.hpi.krestel.mySearchEngine.domain.WordMap;
import de.hpi.krestel.mySearchEngine.util.stream.Bit23Writer;
import de.hpi.krestel.mySearchEngine.util.stream.BitOutputStream;
import de.hpi.krestel.mySearchEngine.util.stream.EliasGammaWriter;

import java.io.*;
import java.util.Arrays;
import java.util.Map;

public class IndexWriter {

	// the base stream for all other streams
	private BitOutputStream bos = null;

	// for elias gamma ints
	private EliasGammaWriter eliasGammaWriter = null;
	// for strings
	private PrintStream ps = null;
	// for 23-bits
	private Bit23Writer bit23writer = null;

	// counter for naming the index files
	private static int indexCounter = 0;

	public IndexWriter() {
		try {
			String fileName  = nextFileName();
			bos              = new BitOutputStream(new FileOutputStream(fileName));
			bit23writer      = new Bit23Writer(bos);
			eliasGammaWriter = new EliasGammaWriter(bos);
			ps               = new PrintStream(bos);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public void write(WordMap partIndex) {
		try {
			int i = 0;
			for (Map.Entry<String, OccurrenceMap> entry : partIndex.entrySet()) {
				System.out.println(entry.getKey() + "==>" + entry.getValue().toString());
				writeIndexWord(entry.getKey());
				writeOccurrenceMap(entry.getValue());
				if (i == 0) break;
				i++;
			}
			bos.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void writeIndexWord(String indexWord) throws IOException {
		ps.print(indexWord);
		bos.write(new byte[]{0});
		System.out.println("word: " + indexWord);
	}

	private void writeOccurrenceMap(OccurrenceMap occurrenceMap) throws IOException {
		int[] documentIds = occurrenceMap.keys();
		Arrays.sort(documentIds);

		System.out.println("length: " + documentIds.length);
		eliasGammaWriter.write(documentIds.length);
		for (int documentId : documentIds) {
			System.out.println("id:" + documentId);
			bit23writer.write(documentId);
			writeDocumentEntry(occurrenceMap.get(documentId));
		}
	}

	private void writeDocumentEntry(DocumentEntry documentEntry) throws IOException {
		System.out.println("size: " + documentEntry.size());
		eliasGammaWriter.write(documentEntry.size());
	}

	private String nextFileName() {
		return String.format("data/index%04d", ++indexCounter);
	}

	public String getFileName() {
		return String.format("data/index%04d", indexCounter);
	}
}

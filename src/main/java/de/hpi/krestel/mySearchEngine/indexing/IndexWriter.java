package de.hpi.krestel.mySearchEngine.indexing;

import de.hpi.krestel.mySearchEngine.domain.DocumentEntry;
import de.hpi.krestel.mySearchEngine.domain.OccurrenceMap;
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

	public void write(Map<String, OccurrenceMap> partIndex) {
		try {
			int i = 0;
			for (Map.Entry<String, OccurrenceMap> entry : partIndex.entrySet()) {
				writeIndexWord(entry.getKey());
				writeDocumentEntries(entry.getValue());
				if (i == 4) break;
				i++;
			}
			bos.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	private void writeDocumentEntries(OccurrenceMap occurrenceMap) throws IOException {
		int[] keys = occurrenceMap.keys();
		Arrays.sort(keys);

		for (int key : keys) {
			bit23writer.write(key);
			writeDocumentEntry(occurrenceMap.get(key));
		}

		System.out.println("Document-ID: " + keys[0]);
	}

	private void writeDocumentEntry(DocumentEntry documentEntry) throws IOException {
		eliasGammaWriter.write(documentEntry.size());

	}

	private void writeIndexWord(String indexWord) throws IOException {
		ps.print(indexWord);
		bos.write(new byte[] { 0 });
		System.out.println("Word: " + indexWord);
	}

	private String nextFileName() {
		return String.format("data/index%04d", indexCounter++);
	}
}

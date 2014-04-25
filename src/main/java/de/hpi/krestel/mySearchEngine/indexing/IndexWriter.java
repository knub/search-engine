package de.hpi.krestel.mySearchEngine.indexing;

import de.hpi.krestel.mySearchEngine.domain.DocumentEntry;
import de.hpi.krestel.mySearchEngine.domain.OccurrenceMap;
import de.hpi.krestel.mySearchEngine.domain.WordMap;
import de.hpi.krestel.mySearchEngine.util.stream.*;

import java.io.*;
import java.util.Arrays;
import java.util.Map;

public class IndexWriter {

	// the base stream for all other streams
	private BitOutputStream bos = null;

	// for elias gamma ints
	private EliasGammaWriter eliasGammaWriter = null;
	// for elias delta ints
	private EliasDeltaWriter eliasDeltaWriter = null;
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
			eliasDeltaWriter = new EliasDeltaWriter(bos);
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
				if (++i == 2) break;
			}
			bos.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void writeIndexWord(String indexWord) throws IOException {
		ps.print(indexWord);
		bos.write(new byte[]{0});
	}

	private void writeOccurrenceMap(OccurrenceMap occurrenceMap) throws IOException {
		int[] documentIds = occurrenceMap.keys();
		Arrays.sort(documentIds);

		eliasGammaWriter.write(documentIds.length);
		for (int documentId : documentIds) {
			bit23writer.write(documentId);
			writeDocumentEntry(occurrenceMap.get(documentId));
		}
	}

	private void writeDocumentEntry(DocumentEntry documentEntry) throws IOException {
		eliasGammaWriter.write(documentEntry.size());
		int lastPos = 0;
		int lastOffset = 0;
		for (int i = 0; i < documentEntry.size(); i++) {
			// yes, this could be refactored, e.g. to a subclass of elias-delta, which
			// automatically uses delta encoding
			int currentPosition = documentEntry.positions.get(i);
			if (i == 0)
				eliasDeltaWriter.write(currentPosition);
			else
				eliasDeltaWriter.write(currentPosition - lastPos);
			lastPos = currentPosition;

			int currentOffset = documentEntry.offsets.get(i);
			if (i == 0)
				eliasDeltaWriter.write(currentOffset);
			else
				eliasDeltaWriter.write(currentOffset - lastOffset);
			lastOffset = currentOffset;

			eliasGammaWriter.write(documentEntry.lengths.get(i));
		}
	}

	private String nextFileName() {
		return String.format("data/index%04d", ++indexCounter);
	}

	public String getFileName() {
		return String.format("data/index%04d", indexCounter);
	}
}

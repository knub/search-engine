package de.hpi.krestel.mySearchEngine.indexing;

import de.hpi.krestel.mySearchEngine.domain.DocumentEntry;
import de.hpi.krestel.mySearchEngine.domain.OccurrenceMap;
import de.hpi.krestel.mySearchEngine.domain.SeekList;
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

    private SeekList seekList = new SeekList();
    private boolean fillSeekList = false;

	private String indexString;
	private final String directory;

	boolean closed = true;

	public IndexWriter() {
		this("index_first_five");
	}
	public IndexWriter(String indexString) {
		this(indexString, "data");
	}
    public IndexWriter(String indexString, String directory) {
	    this(indexString, directory, false);
    }
    public IndexWriter(String indexString, String directory, boolean fillSeekList) {
	    this.indexString = indexString;
	    this.directory = directory;
        this.fillSeekList = fillSeekList;
    }

	public void write(WordMap partIndex) {
		try {
			if (closed)
				intializeStreams();

			for (Map.Entry<String, OccurrenceMap> entry : partIndex.entrySet()) {
				// save byte count to store in seek list later
				long byteCount = bos.getByteCount();
				writeIndexWord(entry.getKey());
				writeOccurrenceMap(entry.getValue());
                if (fillSeekList) {
                    seekList.put(entry.getKey(), byteCount);
                }
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void intializeStreams() throws FileNotFoundException {
		String fileName  = nextFileName();
		bos              = new BitOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)));
		bit23writer      = new Bit23Writer(bos);
		eliasGammaWriter = new EliasGammaWriter(bos);
		eliasDeltaWriter = new EliasDeltaWriter(bos);
        try {
            ps               = new PrintStream(bos, false, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            System.out.println(e.getLocalizedMessage());
            throw new RuntimeException("This machine does NOT support UTF-8. Ha.");
        }
		closed = false;
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
			if (i == 0) {
				eliasDeltaWriter.write(currentPosition + 1);
			}
			else
				eliasDeltaWriter.write(currentPosition - lastPos + 1);
			lastPos = currentPosition;

			int currentOffset = documentEntry.offsets.get(i);
			if (i == 0) {
				eliasDeltaWriter.write(currentOffset + 1);
			}
			else
				eliasDeltaWriter.write(currentOffset - lastOffset + 1);
			lastOffset = currentOffset;

			eliasGammaWriter.write(documentEntry.lengths.get(i));
		}
	}

	private String nextFileName() {
		indexCounter++;
		return getFileName();
	}

	public String getFileName() {
		return String.format(directory + "/" + indexString + "%04d", indexCounter);
	}

	public void close() {
		try {
			closed = true;
			bos.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

    public SeekList getSeekList() {
        return seekList;
    }
}

package de.hpi.krestel.mySearchEngine.indexing;

import de.hpi.krestel.mySearchEngine.domain.DocumentEntry;
import de.hpi.krestel.mySearchEngine.domain.OccurrenceMap;
import de.hpi.krestel.mySearchEngine.domain.SeekList;
import de.hpi.krestel.mySearchEngine.domain.WordMap;
import de.hpi.krestel.mySearchEngine.util.stream.*;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class IndexWriter
{

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
	private static Map<String, Integer> indexCounter = new HashMap<String, Integer>();

    private SeekList seekList = new SeekList();
    private boolean fillSeekList = false;

	private String indexString;
	private final String directory;

	boolean closed = true;

	public IndexWriter()
    {
		this("data");
	}

    public IndexWriter(String directory)
    {
		this(directory, "index");
	}

    public IndexWriter(String directory, String indexString)
    {
	    this(directory, indexString, false);
    }

    public IndexWriter(String directory, String indexString, boolean fillSeekList)
    {
	    this.directory = directory;
	    this.indexString = indexString;
        this.fillSeekList = fillSeekList;

        if (!this.indexCounter.containsKey(indexString)) {
            this.indexCounter.put(indexString, 0);
        }
    }

	public void write(WordMap partIndex)
    {
		try {
			if (this.closed) {
                this.initializeStreams();
            }

			for (Map.Entry<String, OccurrenceMap> entry : partIndex.entrySet()) {
				// save byte count to store in seek list later
				long byteCount = this.bos.getByteCount();
				this.writeIndexWord(entry.getKey());
				this.writeOccurrenceMap(entry.getValue());

                if (this.fillSeekList) {
	                this.seekList.put(entry.getKey(), byteCount);
                }
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void initializeStreams() throws FileNotFoundException
    {
		String fileName  = nextFileName();
		this.bos              = new BitOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)));
		this.bit23writer      = new Bit23Writer(this.bos);
		this.eliasGammaWriter = new EliasGammaWriter(this.bos);
		this.eliasDeltaWriter = new EliasDeltaWriter(this.bos);

        try {
            this.ps = new PrintStream(this.bos, false, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            System.out.println(e.getLocalizedMessage());
            throw new RuntimeException("This machine does NOT support UTF-8. Ha.");
        }

		this.closed = false;
	}

	private void writeIndexWord(String indexWord) throws IOException
    {
		this.ps.print(indexWord);
		this.bos.write(new byte[]{0});
	}

	private void writeOccurrenceMap(OccurrenceMap occurrenceMap) throws IOException
    {
		int[] documentIds = occurrenceMap.keys();
		Arrays.sort(documentIds);

		this.eliasGammaWriter.write(documentIds.length);
		for (int documentId : documentIds) {
			this.bit23writer.write(documentId);
			this.writeDocumentEntry(occurrenceMap.get(documentId));
		}
	}

	private void writeDocumentEntry(DocumentEntry documentEntry) throws IOException
    {
		this.eliasGammaWriter.write(documentEntry.size());
		int lastPos = 0;
		int lastOffset = 0;

        for (int i = 0; i < documentEntry.size(); i++) {
			// yes, this could be refactored, e.g. to a subclass of elias-delta, which
			// automatically uses delta encoding
			int currentPosition = documentEntry.positions.get(i);
			if (i == 0) {
				this.eliasDeltaWriter.write(currentPosition + 1);
			} else {
                this.eliasDeltaWriter.write(currentPosition - lastPos + 1);
            }
			lastPos = currentPosition;

			int currentOffset = documentEntry.offsets.get(i);
			if (i == 0) {
				this.eliasDeltaWriter.write(currentOffset + 1);
			} else {
                this.eliasDeltaWriter.write(currentOffset - lastOffset + 1);
            }
			lastOffset = currentOffset;

			this.eliasGammaWriter.write(documentEntry.lengths.get(i));
		}
	}

	private String nextFileName()
    {
		this.indexCounter.put(this.indexString, this.indexCounter.get(this.indexString) + 1);
		return this.getFileName();
	}

	public String getFileName()
    {
		return String.format(
                directory + "/" + indexString + "%04d", this.indexCounter.get(indexString)
        );
	}

	public void close()
    {
		try {
			this.bos.close();
            this.closed = true;
        } catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

    public void setSeekList(SeekList seekList)
    {
        this.seekList = seekList;
    }

    public SeekList getSeekList()
    {
        return seekList;
    }
}

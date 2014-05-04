package de.hpi.krestel.mySearchEngine.searching;

import de.hpi.krestel.mySearchEngine.domain.OccurrenceMap;
import de.hpi.krestel.mySearchEngine.domain.SeekList;
import de.hpi.krestel.mySearchEngine.domain.WordMap;
import de.hpi.krestel.mySearchEngine.indexing.IndexReader;
import de.hpi.krestel.mySearchEngine.util.stream.RandomAccessInputStream;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

public class IndexSearcher {

    private SeekList seekList;
    private IndexReader indexReader;
    private RandomAccessInputStream randomAccessInputStream;

    public OccurrenceMap search(String token) {
	    Long offset = seekList.get(token);
	    if (offset != null) {
            try {
                randomAccessInputStream.seek(offset);
            } catch (IOException e) {
                System.out.println("Couldn't set seek position at index. Hmpf:");
                System.out.println(e.getLocalizedMessage());
                throw new RuntimeException(e);
            }
	        WordMap wordMap = indexReader.read();
	        OccurrenceMap occurrenceMap = wordMap.firstEntry().getValue();
		    return occurrenceMap;

        } else {
            return new OccurrenceMap();
        }
    }

    public void setIndexFilename(String indexFilename) {
        try {
            randomAccessInputStream = new RandomAccessInputStream(indexFilename);
        } catch (FileNotFoundException e) {
            System.out.println("The index file does not exist. Hmpf:");
            System.out.println(e.getLocalizedMessage());
            throw new RuntimeException(e);
        }
        indexReader = new IndexReader(randomAccessInputStream);
    }

    public void setSeekList(SeekList seekList) {
        this.seekList = seekList;
    }

	public SeekList getSeekList() {
		return this.seekList;
	}
}

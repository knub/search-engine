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

    public ResultSet search(String token) {
	    long[] keys = seekList.values();
	    Arrays.sort(keys);

	    if (seekList.containsKey(token)) {
            try {
                randomAccessInputStream.seek(seekList.get(token));
            } catch (IOException e) {
                System.out.println("Couldn't set seek position at index. Hmpf:");
                System.out.println(e.getLocalizedMessage());
                throw new RuntimeException(e);
            }
	        WordMap wordMap = indexReader.read();
	        OccurrenceMap occurrenceMap = wordMap.firstEntry().getValue();

            return new ResultSet(occurrenceMap.keySet());
        } else {
            return new ResultSet();
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
}

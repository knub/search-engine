package de.hpi.krestel.mySearchEngine.indexing;

import de.hpi.krestel.mySearchEngine.domain.OccurrenceMap;
import de.hpi.krestel.mySearchEngine.domain.WordMap;

import java.util.*;

public class Merger {

    private Map<IndexReader, WordMap> readers;
    private IndexWriter writer;

    public Merger(List<IndexReader> input, IndexWriter output) {
        this.readers = this.createReaderMap(input);
        this.writer = output;
    }

    private Map<IndexReader, WordMap> createReaderMap(List<IndexReader> input) {
        Map<IndexReader, WordMap> readerMap = new HashMap<IndexReader, WordMap>(input.size());

        for (IndexReader reader : input) {
            readerMap.put(reader, reader.read());
        }

        return readerMap;
    }

    public void merge() throws Exception {
        while ( ! readers.isEmpty()) {
            this.mergeReaderWords();
        }
	    writer.close();
    }

    private void writeWordsFromFirstReader() {
        for (Map.Entry<IndexReader, WordMap> entry : this.readers.entrySet()) {
            IndexReader reader = entry.getKey();
            WordMap curMap = entry.getValue();

            if (curMap == null) {
                curMap = reader.read();
                this.readers.put(reader, curMap);
            }
        }
    }

    private void mergeReaderWords() throws Exception {

	    // the map we use for merging during each iteration
	    WordMap mergeMap = new WordMap();
        while (!this.readers.isEmpty()) {
	        // determine the minimal word
	        String minWord = Collections.min(this.readers.values()).firstKey();

	        // iterate through all readers and merge those with the same word
	        Iterator<Map.Entry<IndexReader, WordMap>> iterator = this.readers.entrySet().iterator();
	        while (iterator.hasNext()) {
		        Map.Entry<IndexReader, WordMap> entry = iterator.next();
                WordMap curMap = entry.getValue();
                String curWord = curMap.firstEntry().getKey();

		        if (minWord.equals(curWord)) {
			        mergeMap.merge(curMap);
			        this.fetchNextWord(entry.getKey(), iterator);
		        }
            }

	        // clear the mergeMap to prepare for the next iteration
	        mergeMap.clear();
        }
    }

    private void fetchNextWord(IndexReader reader, Iterator<Map.Entry<IndexReader, WordMap>> iterator) {
        WordMap nextWord = reader.read();

        if (nextWord == null) {
	        iterator.remove();
        } else {
            this.readers.put(reader, nextWord);
        }
    }

}

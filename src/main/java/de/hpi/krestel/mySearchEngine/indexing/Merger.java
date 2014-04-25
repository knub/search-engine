package de.hpi.krestel.mySearchEngine.indexing;

import de.hpi.krestel.mySearchEngine.domain.OccurrenceMap;
import de.hpi.krestel.mySearchEngine.domain.WordMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        String mergeWord = null;
        WordMap mergeMap = new WordMap();

        while ( ! this.readers.isEmpty()) {
            for (Map.Entry<IndexReader, WordMap> entry : this.readers.entrySet()) {
                IndexReader reader = entry.getKey();
                WordMap curMap = entry.getValue();

                Map.Entry<String, OccurrenceMap> wordEntry = curMap.firstEntry();
                String curWord = wordEntry.getKey();

                if (mergeWord == null) {
                    mergeWord = curWord;
                    this.fetchNextWord(reader);
                }

                if (mergeWord == curWord) {
                    mergeMap.merge(curMap);
                }
            }
        }
    }

    private void fetchNextWord(IndexReader reader) {
        WordMap nextWord = reader.read();

        if (nextWord == null) {
            this.readers.remove(reader);
        } else {
            this.readers.put(reader, nextWord);
        }
    }

}

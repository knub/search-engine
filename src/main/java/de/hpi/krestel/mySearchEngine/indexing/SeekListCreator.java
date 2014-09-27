package de.hpi.krestel.mySearchEngine.indexing;

import de.hpi.krestel.mySearchEngine.domain.SeekList;
import de.hpi.krestel.mySearchEngine.domain.WordMap;

public class SeekListCreator
{
    private IndexReader reader;

    public SeekListCreator(IndexReader reader)
    {
        this.reader = reader;
    }

    public SeekList createSeekList()
    {
        SeekList seekList = new SeekList();
        long curOffset = this.reader.getCurrentOffset();

        // For every word in the index, store the word and the index offset in the seek list
        while (true) {
            WordMap current = this.reader.read();
            if (current == null) {
                break;
            }

            seekList.put(current.getWord(), curOffset);
            curOffset = this.reader.getCurrentOffset();
        }

        return seekList;
    }
}

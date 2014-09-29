package de.hpi.krestel.mySearchEngine.indexing;

import de.hpi.krestel.mySearchEngine.domain.SeekList;
import de.hpi.krestel.mySearchEngine.domain.WordMap;

import java.io.FileWriter;
import java.io.IOException;

public class SeekListCreator
{
    private IndexReader reader;

    private FileWriter fileWriter;

    public SeekListCreator(IndexReader reader, String filename)
    {
        this.reader = reader;
        
        try {
            this.fileWriter = new FileWriter(filename);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("cannot open seek list file for writing");
        }
    }

    public void createSeekList()
    {
        long curOffset = this.reader.getCurrentOffset();

        // For every word in the index, store the word and the index offset in the seek list
        while (true) {
            WordMap current = this.reader.read();

            if (current == null) {
                break;
            }

            this.writeToFile(current.getWord(), curOffset);

            curOffset = this.reader.getCurrentOffset();
        }
    }

    public void writeToFile(String word, long offset)
    {
        try {
            this.fileWriter.write("" + offset + " " + word + "\n");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("cannot write to seek list file");
        }
    }
}

package de.hpi.krestel.mySearchEngine.indexing;

import de.hpi.krestel.mySearchEngine.domain.SeekList;
import de.hpi.krestel.mySearchEngine.domain.WordMap;

import java.io.FileWriter;
import java.io.IOException;

public class SeekListCreator {
    private IndexReader reader;

    private FileWriter fileWriter;

    public SeekListCreator(IndexReader reader, String filename) {
        this.reader = reader;

        try {
            this.fileWriter = new FileWriter(filename);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("cannot open seek list file for writing");
        }
    }

    public void createSeekList() {
        long curOffset = this.reader.getCurrentOffset();
        short round = 0;

        // For every word in the index, store the word and the index offset in the seek list
        while (true) {
            WordMap current = this.reader.read();

            if (current == null) {
                break;
            }

            // just save every forth word
            if (round == 0) {
                round = 3;
                continue;
            }
            round -= 1;

            this.writeToFile(sanitizeWord(current.getWord()), curOffset);

            curOffset = this.reader.getCurrentOffset();
        }

        try {
            this.fileWriter.close();
        } catch (IOException e) {
            System.out.println("Cannot close documents file... anyway.");
        }
    }

    private String sanitizeWord(String originalWord)
    {
        if (originalWord.contains("\n")) {
            originalWord.replaceAll("\n", "");
        }
        return originalWord;
    }

    private void writeToFile(String word, long offset)
    {
        try {
            this.fileWriter.write("" + offset + " " + word + "\n");
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("cannot write to seek list file");
        }
    }
}

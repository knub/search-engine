package de.hpi.krestel.mySearchEngine.indexing;

import de.hpi.krestel.mySearchEngine.domain.SeekList;
import de.hpi.krestel.mySearchEngine.domain.WordMap;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class SeekListCreator {
    private IndexReader reader;

    private Writer fileWriter;

    public SeekListCreator(IndexReader reader, String filename) {
        this.reader = reader;

        try {
            this.fileWriter = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(filename), StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("cannot open seek list file for writing");
        }
    }

    public void createSeekList() {
        long curOffset = this.reader.getCurrentOffset();
        short round = 0;  // first token should be in the index

        // For every word in the index, store the word and the index offset in the seek list
        while (true) {
            WordMap current = this.reader.read();

            if (current == null) {
                break;
            }

            // just save every forth word
            if (round != 0) {
                round -= 1;
                curOffset = this.reader.getCurrentOffset();
            } else {
                round = 3;
                this.writeToFile(sanitizeWord(current.getWord()), curOffset);
            }
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

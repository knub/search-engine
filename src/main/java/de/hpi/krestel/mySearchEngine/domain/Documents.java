package de.hpi.krestel.mySearchEngine.domain;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

// this class stores a list of titles and lengths, each for one document
public class Documents
{
    private List<String> titles;
    private TIntList lengths;
    private long cumulatedLength;
    private boolean writeFileMode = false;
    private FileWriter fileWriter;
    private int count = 0;

    public static Documents readFromFile(String filename)
    {
        Documents documents = new Documents();
        FileReader fileReader;

        //open file
        try {
            fileReader = new FileReader(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("cannot open documents file");
        }
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        // read each line; parse length and title; add them to lists
        String line;
        String[] splitted;
        int length;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                // line has format "12length34 this is da title"
                splitted = line.split(" ", 2);
                documents.add(splitted[1], Integer.valueOf(splitted[0]));
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("cannot read from documents file");
        }

        // close file
        try {
            fileReader.close();
        } catch (IOException e) {
            System.out.println("Cannot close documents file... anyway.");
        }

        return documents;
    }

    public Documents()
    {
        // for reading mode
        this.titles = new ArrayList<String>();
        this.lengths = new TIntArrayList();
        this.cumulatedLength = 0;
    }

    public Documents(String filename)
    {
        // for inserting mode
        this.writeFileMode = true;

        // open writer
        try {
            this.fileWriter = new FileWriter(filename);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("cannot open documents file");
        }
    }

    public void add(String title, int length)
    {
        this.count += 1;
        if (this.writeFileMode) {
            // format line to "12length34 this is the title"
            try {
                this.fileWriter.write("" + length + " " + title + "\n");
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("cannot write to documents file");
            }
        } else {
            this.titles.add(title);
            this.lengths.add(length);
            this.cumulatedLength += length;
        }
    }

    public void finalize()
    {
        try {
            this.fileWriter.close();
        } catch (IOException e) {
            System.out.println("Cannot close documents file... anyway.");
        }
    }

    public String getTitle(int id)
    {
        return this.titles.get(id);
    }

    public int getLength(int id)
    {
        return this.lengths.get(id);
    }

    public long getCumulatedLength()
    {
        return this.cumulatedLength;
    }

    public int getCount()
    {
        return this.count;
    }

    public long getAverageLength()
    {
        return this.cumulatedLength / this.count;
    }
}

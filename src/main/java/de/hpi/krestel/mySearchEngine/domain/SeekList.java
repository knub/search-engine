package de.hpi.krestel.mySearchEngine.domain;

import java.io.*;
import java.util.TreeMap;

public class SeekList extends TreeMap<String, Long> implements Serializable
{

    public static SeekList createFromFile(String filename) {
        SeekList seekList = new SeekList();
        FileReader fileReader;

        //open file
        try {
            fileReader = new FileReader(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("cannot open seekList file");
            return null;
        }
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        // read each line; parse offest and word; add them to map
        String line;
        String[] splitted;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                // line has format "12offset34 word"
                splitted = line.split(" ", 2);
                seekList.put(splitted[1], Long.valueOf(splitted[0]));
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("cannot read from seekList file");
            return null;
        }

        // close file
        try {
            fileReader.close();
        } catch (IOException e) {
            System.out.println("Cannot close seekList file... anyway.");
        }

        return seekList;
    }

}

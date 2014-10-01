package de.hpi.krestel.mySearchEngine.domain;

import gnu.trove.list.TLongList;
import gnu.trove.list.array.TLongArrayList;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class SeekList extends TreeMap<String, Long>
{
    public static SeekList createFromFile(String filename) {
        SeekList seekList = new SeekList();
        BufferedReader bufferedReader;

        //open file
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(filename), StandardCharsets.UTF_8));
        } catch (FileNotFoundException e) {
            System.out.println("cannot open seekList file - it probably does not exist");
            return null;
        }

        // read each line; parse offest and word; add them to map
        String line;
        String[] splitted;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                if(line.length() == 0) continue;
                // line has format "12offset34 word"
                try {
                    splitted = line.split(" ", 2);
                    seekList.put(splitted[1], new Long(splitted[0]));
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.print("splitting failed for: ");
                    System.out.println(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("cannot read from seekList file");
            return null;
        }

        // close file
        try {
            bufferedReader.close();
        } catch (IOException e) {
            System.out.println("Cannot close seekList file... anyway.");
        }

        return seekList;
    }
}

package de.hpi.krestel.mySearchEngine.util.stream;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

public class RandomAccessInputStream extends InputStream {

    private RandomAccessFile randomAccessFile;

    public RandomAccessInputStream(String filename) throws FileNotFoundException{
        randomAccessFile = new RandomAccessFile(filename, "r");
    }

    public void seek(long pos) throws IOException {
        randomAccessFile.seek(pos);
    }

    @Override
    public int read() throws IOException {
        return randomAccessFile.read();
    }
}

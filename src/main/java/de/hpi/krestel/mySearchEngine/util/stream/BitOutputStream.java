package de.hpi.krestel.mySearchEngine.util.stream;

import java.io.IOException;
import java.io.OutputStream;

public class BitOutputStream {

    private OutputStream output;
    private int curByte;
    private int pos;

    public BitOutputStream(OutputStream stream) {
        this.output = stream;
        this.pos = 0;
        this.curByte = 0;
    }

    public void writeBit(int bit) throws IOException {
        if (this.pos == 0) {
            this.curByte = 0;
        }

        if (bit == 1) {
            // Do some bit magic
            int mask = (int) Math.pow(2, 7 - pos);
            this.curByte |= mask;
        }

        this.pos = (this.pos + 1) % 8;

        if (this.pos == 0) {
            this.output.write(this.curByte);
        }
    }

    public void flush() throws IOException {
        if (this.pos > 0) {
            this.output.write(this.curByte);
            this.curByte = 0;
            this.pos = 0;
        }
    }

    public void close() throws IOException {
        this.flush();
        this.output.close();
    }

}

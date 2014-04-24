package de.hpi.krestel.mySearchEngine.util.stream;

import java.io.IOException;
import java.io.InputStream;

public class BitInputStream {

    private InputStream input;
    private int pos;
    private int buffer;

    public BitInputStream(InputStream stream) {
        this.input = stream;
        this.pos = 0;
        this.buffer = 0;
    }

    public int readBit() throws IOException {
        if (this.pos == 0) {
            this.buffer = this.input.read();
        }

        boolean isOne = ((this.buffer & (1 << (7 - this.pos))) != 0);
        int bit = (isOne) ? 1 : 0;

        this.pos = (this.pos + 1) % 8;
        return bit;
    }

}
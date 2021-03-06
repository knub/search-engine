package de.hpi.krestel.mySearchEngine.util.stream;

import java.io.IOException;
import java.io.InputStream;

public class EliasGammaReader extends InputStream {

    private BitInputStream input;

    public EliasGammaReader(BitInputStream stream) {
        this.input = stream;
    }

    /**
     * Reads the next byte of data from the input stream. The value byte is
     * returned as an <code>int</code> in the range <code>0</code> to
     * <code>255</code>. If no byte is available because the end of the stream
     * has been reached, the value <code>-1</code> is returned. This method
     * blocks until input data is available, the end of the stream is detected,
     * or an exception is thrown.
     * <p/>
     * <p> A subclass must provide an implementation of this method.
     *
     * @return the next byte of data, or <code>-1</code> if the end of the
     * stream is reached.
     * @throws java.io.IOException if an I/O error occurs.
     */
    @Override
    public int read() throws IOException {
        int numBits = 0;
        while (this.input.readBit() == 1) {
            numBits++;
        }

        int value = 1;
        for (int curBit = 0; curBit < numBits; curBit++) {
            value = (value << 1) + this.input.readBit();
        }

        return value;
    }

}

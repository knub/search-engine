package de.hpi.krestel.mySearchEngine.util.stream;

import java.io.IOException;
import java.io.InputStream;

public class BitInputStream extends InputStream {

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
	        if (this.buffer == -1)
		        throw new RuntimeException("Houston, we have a problem. This should never be the end of the file.");
        }

        boolean isOne = ((this.buffer & (1 << (7 - this.pos))) != 0);
        int bit = (isOne) ? 1 : 0;

        this.pos = (this.pos + 1) % 8;
        return bit;
    }

	/**
	 * Only called by non-bit-reader.
	 * @return The read byte.
	 * @throws IOException
	 */
	@Override
	public int read() throws IOException {
		this.pos = 0;
		return input.read();
	}
}

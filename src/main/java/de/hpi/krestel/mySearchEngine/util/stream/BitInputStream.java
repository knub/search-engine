package de.hpi.krestel.mySearchEngine.util.stream;

import java.io.IOException;
import java.io.InputStream;

public class BitInputStream extends InputStream {

    private InputStream input;
    private int pos;
    private int buffer;
    private long offset;

    public BitInputStream(InputStream stream) {
        this.input = stream;
        this.pos = 0;
        this.buffer = 0;
        this.offset = 0;
    }

    public int readBit() throws IOException {
        if (this.pos == 0) {
            this.buffer = this.readNext();
	        if (this.buffer == -1) {
                throw new RuntimeException("End of file reached. No bit to read.");
            }
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
		return this.readNext();
	}

    private int readNext() throws IOException
    {
        this.offset++;
        return this.input.read();
    }

    public long getCurrentOffset()
    {
        return this.offset;
    }
}

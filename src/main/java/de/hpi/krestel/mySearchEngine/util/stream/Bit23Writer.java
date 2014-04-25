package de.hpi.krestel.mySearchEngine.util.stream;

import java.io.IOException;
import java.io.OutputStream;

public class Bit23Writer extends OutputStream {

	private final BitOutputStream bos;

	public Bit23Writer(BitOutputStream bos) {
		this.bos = bos;
	}
	@Override
	public void write(int number) throws IOException {
		for (int i = 22; i >= 0; i--) {
			bos.writeBit(getBit(number, i));
		}
	}

	int getBit(int n, int k) {
		return (n >> k) & 1;
	}
}

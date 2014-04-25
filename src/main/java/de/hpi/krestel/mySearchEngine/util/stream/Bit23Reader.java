package de.hpi.krestel.mySearchEngine.util.stream;

import java.io.IOException;
import java.io.InputStream;

public class Bit23Reader extends InputStream {

	private final BitInputStream bis;

	public Bit23Reader(BitInputStream bis) {
		this.bis = bis;
	}

	@Override
	public int read() throws IOException {
		int number = 0;
		for (int i = 1; i <= 23; i++) {
			int readBit = bis.readBit();
			number = number * 2 + readBit;
		}
		return number;
	}
}

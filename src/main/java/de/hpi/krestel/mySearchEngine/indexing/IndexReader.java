package de.hpi.krestel.mySearchEngine.indexing;

import de.hpi.krestel.mySearchEngine.util.stream.Bit23Reader;
import de.hpi.krestel.mySearchEngine.util.stream.BitInputStream;
import gnu.trove.list.array.TByteArrayList;

import java.io.*;

public class IndexReader {

	public void read(String fileName) {
		try {
			FileInputStream fis = new FileInputStream(fileName);
			BitInputStream bis = new BitInputStream(fis);
			InputStreamReader isr = new InputStreamReader(bis);
			Bit23Reader bit23Reader = new Bit23Reader(bis);
			for (int i = 0; i < 5; i++) {
				TByteArrayList wordBytes = new TByteArrayList();
				byte currentByte = (byte) bis.read();
				while (currentByte != 0) {
					wordBytes.add(currentByte);
					currentByte = (byte) bis.read();
				}
				System.out.println("Word: " + new String(wordBytes.toArray()));
				System.out.println("Document-ID: " + bit23Reader.read());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

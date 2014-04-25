package de.hpi.krestel.mySearchEngine.indexing;

import de.hpi.krestel.mySearchEngine.domain.OccurrenceMap;
import de.hpi.krestel.mySearchEngine.util.stream.Bit23Writer;
import de.hpi.krestel.mySearchEngine.util.stream.BitOutputStream;

import java.io.*;
import java.util.Arrays;
import java.util.Map;

public class IndexWriter {

	private static int indexCounter = 0;
	public String write(Map<String, OccurrenceMap> partIndex) {
		try {
			String fileName = nextFileName();
			BitOutputStream bos = new BitOutputStream(new FileOutputStream(fileName));
			Bit23Writer bit23writer = new Bit23Writer(bos);
			PrintStream ps = new PrintStream(bos);
			int i = 0;
			for (Map.Entry<String, OccurrenceMap> entry : partIndex.entrySet()) {
				ps.print(entry.getKey());
				System.out.println("Word: " + entry.getKey());
				bos.write(new byte[] { 0 });
				OccurrenceMap occurrenceMap = entry.getValue();
				int[] keys = occurrenceMap.keys();
				Arrays.sort(keys);
				bit23writer.write(keys[0]);
				System.out.println("Document-ID: " + keys[0]);
				if (i == 4) break;
				i++;
			}
			bos.close();
			bit23writer.close();
			ps.close();


			return fileName;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	private String nextFileName() {
		return String.format("data/index%04d", indexCounter++);
	}
}

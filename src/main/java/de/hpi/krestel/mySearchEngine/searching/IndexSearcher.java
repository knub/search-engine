package de.hpi.krestel.mySearchEngine.searching;

import de.hpi.krestel.mySearchEngine.domain.DocumentEntry;
import de.hpi.krestel.mySearchEngine.domain.OccurrenceMap;
import de.hpi.krestel.mySearchEngine.domain.SeekList;
import de.hpi.krestel.mySearchEngine.domain.WordMap;
import de.hpi.krestel.mySearchEngine.indexing.IndexReader;
import de.hpi.krestel.mySearchEngine.util.stream.RandomAccessInputStream;
import gnu.trove.procedure.TIntObjectProcedure;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

public class IndexSearcher {

	private SeekList seekList;
	private IndexReader indexReader;
	private RandomAccessInputStream randomAccessInputStream;
	private static final double k1 = 1.2;
	private static final double k2 = 100;
	private static final double b  = 0.75;

	public OccurrenceMap search(String token) {
		return this.search(token, 1);
	}
	public OccurrenceMap search(String token, int occurenceInQuery) {
		Long offset = seekList.get(token);
		if (offset != null) {
			try {
				randomAccessInputStream.seek(offset);
			} catch (IOException e) {
				System.out.println("Couldn't set seek position at index. Hmpf:");
				System.out.println(e.getLocalizedMessage());
				throw new RuntimeException(e);
			}
			WordMap wordMap = indexReader.read();
			OccurrenceMap occurrenceMap = wordMap.firstEntry().getValue();
			final long N = seekList.getDocumentCount();
			final long ni = occurrenceMap.size();
			final double avgdl = seekList.getAverageDocumentLength();
			final long qfi = occurenceInQuery;
			occurrenceMap.forEachEntry(new TIntObjectProcedure<DocumentEntry>() {
				@Override
				public boolean execute(int docId, DocumentEntry docEntry) {
					long dl = seekList.getDocLengths().get(docId);
					long fi = docEntry.size();
//					System.out.println("Doc-ID: " + docId);
					docEntry.setRank(calculateRank(N, ni, dl, avgdl, fi, qfi));
					return true;
				}
			});
			return occurrenceMap;
		} else {
			return new OccurrenceMap();
		}
	}

	public static double calculateRank(long N, long ni, long dl, double avgdl, long fi, long qfi) {
//		System.out.println(String.format("N: %d, ni: %d, dl: %d, avgdl: %f, fi: %d, qfi: %d", N, ni, dl, avgdl, fi, qfi));
		double K = k1 * (1 - b + b * ((double) dl) / avgdl);
		return Math.log((N - ni + 0.5) / (ni + 0.5)) * (k1 + 1) * fi / (K + fi) * (k2 + 1) * qfi / (k2 + qfi);
	}

	public void setIndexFilename(String indexFilename) {
		try {
			randomAccessInputStream = new RandomAccessInputStream(indexFilename);
		} catch (FileNotFoundException e) {
			System.out.println("The index file does not exist. Hmpf:");
			System.out.println(e.getLocalizedMessage());
			throw new RuntimeException(e);
		}
		indexReader = new IndexReader(randomAccessInputStream);
	}

	public void setSeekList(SeekList seekList) {
		this.seekList = seekList;
	}

	public SeekList getSeekList() {
		return this.seekList;
	}
}

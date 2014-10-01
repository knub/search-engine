package de.hpi.krestel.mySearchEngine.searching;

import de.hpi.krestel.mySearchEngine.domain.*;
import de.hpi.krestel.mySearchEngine.indexing.IndexReader;
import de.hpi.krestel.mySearchEngine.util.stream.RandomAccessInputStream;
import gnu.trove.procedure.TIntObjectProcedure;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

public class IndexSearcher
{
	private SeekList seekList;
    private Documents documents;
    private IndexReader indexReader;
	private RandomAccessInputStream randomAccessInputStream;
	private static final double k1 = 1.2;
	private static final double k2 = 100;
	private static final double b  = 0.75;

	public OccurrenceMap search(String token)
    {
		return this.search(token, 1);
	}

	public OccurrenceMap search(String token, int occurrenceInQuery)
    {
        OccurrenceMap occurrenceMap = findOccurrenceMapFor(token);
        if (occurrenceMap == null) {
            // found nothing
            return new OccurrenceMap();
        }

        final long N = this.documents.getCount();
        final long ni = occurrenceMap.size();
        final double avgdl = this.documents.getAverageLength();
        final long qfi = occurrenceInQuery;
        occurrenceMap.forEachEntry(new TIntObjectProcedure<DocumentEntry>() {
            @Override
            public boolean execute(int docId, DocumentEntry docEntry) {
                long dl = documents.getLength(docId);
                long fi = docEntry.size();
//					System.out.println("Doc-ID: " + docId);
                docEntry.setRank(calculateRank(N, ni, dl, avgdl, fi, qfi));
                return true;
            }
        });
        return occurrenceMap;
	}

    private OccurrenceMap findOccurrenceMapFor(String token)
    {
        // find nearest index offset for this token
        Map.Entry<String, Long> baseEntry = seekList.floorEntry(token);
        if (baseEntry == null) {
            return null;
        }
        long baseOffset = baseEntry.getValue().longValue();

        // set stream offset to this position
        try {
            this.randomAccessInputStream.seek(baseOffset);
        } catch (IOException e) {
            System.out.println("Couldn't set seek position at index. Hmpf:");
            System.out.println(e.getLocalizedMessage());
            throw new RuntimeException(e);
        }

        // read WordMaps until either a) the token is found,
        // or b) a word "greater" than the token is returned (--> token is not in the index)
        WordMap wordMap;
        String foundWord;
        int compared;
        do {
            // read words from index until
            wordMap = this.indexReader.read();
            foundWord = wordMap.firstEntry().getKey();
            compared = token.compareTo(foundWord);
        } while (compared > 0);

        if (compared == 0) {
            return wordMap.firstEntry().getValue();
        } else {
            // when the token is not found, return null
            return null;
        }
    }

	public static double calculateRank(long N, long ni, long dl, double avgdl, long fi, long qfi)
    {
//		System.out.println(String.format("N: %d, ni: %d, dl: %d, avgdl: %f, fi: %d, qfi: %d", N, ni, dl, avgdl, fi, qfi));
		double K = k1 * (1 - b + b * ((double) dl) / avgdl);
		return Math.log((N - ni + 0.5) / (ni + 0.5)) * (k1 + 1) * fi / (K + fi) * (k2 + 1) * qfi / (k2 + qfi);
	}

	public void setIndexFilename(String indexFilename)
    {
		try {
			this.randomAccessInputStream = new RandomAccessInputStream(indexFilename);
		} catch (FileNotFoundException e) {
			System.out.println("The index file does not exist. Hmpf:");
			System.out.println(e.getLocalizedMessage());
			throw new RuntimeException(e);
		}
		indexReader = new IndexReader(this.randomAccessInputStream);
	}

	public void setSeekList(SeekList seekList)
    {
		this.seekList = seekList;
	}

	public SeekList getSeekList()
    {
		return this.seekList;
	}

    public void setDocuments(Documents documents)
    {
        this.documents = documents;
    }

}

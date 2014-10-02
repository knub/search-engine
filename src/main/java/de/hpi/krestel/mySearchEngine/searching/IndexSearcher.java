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
	private static final double k1 = 1.6;
	private static final double k2 = 100;
	private static final double b  = 0.85;

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
        return calculateRanks(occurrenceMap, occurrenceInQuery);
	}

    public OccurrenceMap searchPrefixed(String prefix)
    {
        return this.searchPrefixed(prefix, 1);
    }

    public OccurrenceMap searchPrefixed(String prefix, int occurrenceInQuery)
    {
        OccurrenceMap occurrenceMap = findOccurrenceMapForPrefixed(prefix);
        if (occurrenceMap == null) {
            // found nothing
            return new OccurrenceMap();
        }
        return calculateRanks(occurrenceMap, occurrenceInQuery);
    }

    private OccurrenceMap calculateRanks(OccurrenceMap occurrenceMap, int occurrenceInQuery)
    {
        final long docCount = this.documents.getCount();
        final long wordDocCount = occurrenceMap.size();
        final double avgDocLength = this.documents.getAverageLength();
        final long queryWordCount = occurrenceInQuery;
        occurrenceMap.forEachEntry(new TIntObjectProcedure<DocumentEntry>() {
            @Override
            public boolean execute(int docId, DocumentEntry docEntry) {
                long docLength = documents.getLength(docId);
                long documentWordCount = docEntry.size();
                docEntry.setRank(calculateRank(
                        docCount,
                        wordDocCount,
                        docLength,
                        avgDocLength,
                        documentWordCount,
                        queryWordCount
                ));
                return true;
            }
        });
        return occurrenceMap;
    }

    private boolean setInputStreamOffset(String token)
    {
        // find nearest index offset for this token
        Map.Entry<String, Long> baseEntry = seekList.floorEntry(token);
        if (baseEntry == null) {
            return false;
        }
        long baseOffset = baseEntry.getValue();

        // set stream offset to this position
        try {
            this.randomAccessInputStream.seek(baseOffset);
        } catch (IOException e) {
            System.out.println("Couldn't set seek position at index. Hmpf:");
            System.out.println(e.getLocalizedMessage());
            throw new RuntimeException(e);
        }
        return true;
    }

    private OccurrenceMap findOccurrenceMapFor(String token)
    {
        if (!setInputStreamOffset(token))
            return null;

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

    private OccurrenceMap findOccurrenceMapForPrefixed(String prefix)
    {
        if (!setInputStreamOffset(prefix))
            return null;

        // skip WordMaps while they are smaller than the prefix
        WordMap wordMap;
        String foundWord;
        do {
            // read words from index
            wordMap = this.indexReader.read();
            foundWord = wordMap.firstEntry().getKey();
        } while (prefix.compareTo(foundWord) > 0);

        // read WordMaps until they don't start with the prefix anymore
        OccurrenceMap occurrenceMap = new OccurrenceMap();
        while (foundWord.startsWith(prefix)) {
            occurrenceMap.merge(wordMap.firstEntry().getValue());

            wordMap = this.indexReader.read();
            foundWord = wordMap.firstEntry().getKey();
        }

        return occurrenceMap;
    }

	public static double calculateRank(
            long docCount,
            long wordDocCount,
            long docLength,
            double avgDocLength,
            long documentWordCount,
            long queryWordCount
    ) {
		double K = k1 * ((1 - b) + (b * (((double) docLength) / avgDocLength)));

        double termConstant = ((k1 + 1) * documentWordCount) / (K + documentWordCount);
        double queryConstant = ((k2 + 1) * queryWordCount) / (k2 + queryWordCount);
        double rank = Math.log((docCount - wordDocCount + 0.5) / (wordDocCount + 0.5));

        return rank * termConstant * queryConstant;
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

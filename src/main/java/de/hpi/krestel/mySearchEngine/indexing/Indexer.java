package de.hpi.krestel.mySearchEngine.indexing;

import de.hpi.krestel.mySearchEngine.domain.DocumentEntry;
import de.hpi.krestel.mySearchEngine.domain.OccurrenceMap;
import de.hpi.krestel.mySearchEngine.domain.WordMap;
import de.hpi.krestel.mySearchEngine.processing.Pipeline;
import de.hpi.krestel.mySearchEngine.xml.TextCompletedListener;
import de.hpi.krestel.mySearchEngine.xml.WikipediaReader;
import edu.stanford.nlp.ling.CoreLabel;

import java.util.List;

public class Indexer implements TextCompletedListener {

	private final Pipeline preprocessingPipeline = Pipeline.createPreprocessingPipeline();
    private final WordMap partIndex = new WordMap();
    private int documentId = 0;
	long startTime;

    public Indexer(String directory) { }

	public void run() {
		WikipediaReader reader = new WikipediaReader();
		reader.addTextCompletedListener(this);
		reader.readWikiFile();
		preprocessingPipeline.finished();
	}

	@Override
	public void onTextCompleted(String text) {
		List<CoreLabel> labels = preprocessingPipeline.start(text);
		indexText(labels);
		documentId++;
		if (documentId % 100 == 0) {
			long usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
			long freeMemory = Runtime.getRuntime().maxMemory() - usedMemory;
//			if (freeMemory / 1024 / 1024 < 1400) {
				exchangePartIndex();
//			}
			System.out.println("Free: " + freeMemory / 1024 / 1024);
			if (startTime != 0)
				System.out.println((System.currentTimeMillis() - startTime) + " ms");
			startTime = System.currentTimeMillis();
		}
	}

	public void indexText(List<CoreLabel> labels) {
        for (int position = 0; position < labels.size(); position++) {
            CoreLabel label = labels.get(position);
            if (partIndex.containsKey(label.value())) {
                OccurrenceMap occurrenceMap = partIndex.get(label.value());
                if (occurrenceMap.containsKey(documentId)) {
                    DocumentEntry documentEntry = occurrenceMap.get(documentId);
                    documentEntry.positions.add(position);
                    documentEntry.offsets.add(label.beginPosition());
                    documentEntry.lengths.add(label.endPosition() - label.beginPosition());
                } else {
                    DocumentEntry documentEntry = new DocumentEntry(position, label.beginPosition(),
                            label.endPosition() - label.beginPosition());
                    occurrenceMap.put(documentId, documentEntry);
                }
            } else {
                DocumentEntry documentEntry = new DocumentEntry(position, label.beginPosition(),
                        label.endPosition() - label.beginPosition());
                OccurrenceMap occurrenceMap = new OccurrenceMap();
                occurrenceMap.put(documentId, documentEntry);
                partIndex.put(label.value(), occurrenceMap);
            }
        }
	}

    public void exchangePartIndex() {
	    IndexWriter indexWriter = new IndexWriter();
	    indexWriter.write(partIndex);
	    indexWriter.close();
	    partIndex.clear();
	    System.gc();
	    System.out.println("================================================================================");

	    IndexReader indexReader = new IndexReader(indexWriter.getFileName());

	    WordMap wordMap = indexReader.read();
	    while  (wordMap != null) {
		    System.out.println(wordMap);
		    wordMap = indexReader.read();
	    }
	    System.exit(0);
    }
}

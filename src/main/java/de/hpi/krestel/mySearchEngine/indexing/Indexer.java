package de.hpi.krestel.mySearchEngine.indexing;

import de.hpi.krestel.mySearchEngine.domain.DocumentEntry;
import de.hpi.krestel.mySearchEngine.domain.OccurrenceMap;
import de.hpi.krestel.mySearchEngine.domain.SeekList;
import de.hpi.krestel.mySearchEngine.domain.WordMap;
import de.hpi.krestel.mySearchEngine.processing.Pipeline;
import de.hpi.krestel.mySearchEngine.xml.TextCompletedListener;
import de.hpi.krestel.mySearchEngine.xml.WikipediaReader;
import edu.stanford.nlp.ling.CoreLabel;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Indexer implements TextCompletedListener {

	private final Pipeline preprocessingPipeline = Pipeline.createPreprocessingPipeline();
    private final WordMap partIndex = new WordMap();
    private final List<String> partIndexFileNames = new ArrayList<String>();
	private final String directory;
	private int documentId = 0;
    private String indexFilename;
    private SeekList seekList;
	long docCount;
	long cumulatedDocLength;
	long startTime;

    public Indexer(String directory) {
	    this.directory = directory;
    }

	public void run() {
		System.out.println("INDEXING");
		docCount = 0;
		cumulatedDocLength = 0;
		WikipediaReader reader = new WikipediaReader();
		reader.addTextCompletedListener(this);
		reader.readWikiFile();
		writePartIndex();
		preprocessingPipeline.finished();
		triggerMergingProcess();
		writeSeekList();
	}

	private void writeSeekList() {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(directory + "/seek_list"));
			seekList.setAverageDocumentLength(cumulatedDocLength / docCount);
			oos.writeObject(seekList);
			oos.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void onTextCompleted(String text, String title) {
		List<CoreLabel> labels = preprocessingPipeline.start(title + "\0" + text);
		docCount += 1;
		cumulatedDocLength += labels.size();
		System.out.println("Title: " + title + ", Document-ID: " + documentId);
		indexText(labels);
		documentId++;
		long usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		long freeMemory = Runtime.getRuntime().maxMemory() - usedMemory;
		if (documentId % 100 == 0) {
			if (startTime != 0)
			System.out.println("Dokument-ID: " + documentId + ". Last 100 took: " + (System.currentTimeMillis() - startTime) + " ms");
			startTime = System.currentTimeMillis();
		}
		if (freeMemory / 1024 / 1024 < 400) {
			writePartIndex();
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

    public void writePartIndex() {
        IndexWriter indexWriter = new IndexWriter(directory);
        indexWriter.write(partIndex);
        indexWriter.close();
        partIndexFileNames.add(indexWriter.getFileName());
        partIndex.clear();
        System.gc();
    }

    public void triggerMergingProcess() {
        List<IndexReader> indexReaders = new ArrayList<IndexReader>(preprocessingPipeline.size());
        for (String partIndexFileName : partIndexFileNames) {
            indexReaders.add(new IndexReader(partIndexFileName));
        }

        IndexWriter indexWriter = new IndexWriter(directory, "final_index", true);
        IndexMerger indexMerger = new IndexMerger(indexReaders, indexWriter);
        try {
            indexMerger.merge();
        }
        catch (Exception e) {
            System.out.println("Something bad happened at merging time:");
            System.out.println(e.getLocalizedMessage());
        }
        indexFilename = indexWriter.getFileName();
        seekList = indexWriter.getSeekList();
    }

    public String getIndexFilename() {
        return indexFilename;
    }

    public SeekList getSeekList() {
        return seekList;
    }
}

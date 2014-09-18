package de.hpi.krestel.mySearchEngine.indexing;

import de.hpi.krestel.mySearchEngine.domain.DocumentEntry;
import de.hpi.krestel.mySearchEngine.domain.OccurrenceMap;
import de.hpi.krestel.mySearchEngine.domain.SeekList;
import de.hpi.krestel.mySearchEngine.domain.WordMap;
import de.hpi.krestel.mySearchEngine.processing.Pipeline;
import de.hpi.krestel.mySearchEngine.xml.TextCompletedListener;
import de.hpi.krestel.mySearchEngine.xml.WikipediaReader;
import edu.stanford.nlp.ling.CoreLabel;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Indexer implements TextCompletedListener
{

	private final Pipeline preprocessingPipeline = Pipeline.createPreprocessingPipeline();
    private final WordMap partIndex = new WordMap();
    private final List<String> partIndexFileNames = new ArrayList<String>();
	private final String directory;
	private int documentId = 0;
    private String indexFilename;
    private SeekList seekList;
    private List<String> titleMap;
	TIntIntMap docLengths = new TIntIntHashMap();
	long docCount;
	long cumulatedDocLength;
	long startTime;

	boolean createLinkConnections = false;

	private Map<String, StringBuilder> links = new HashMap<String, StringBuilder>();

    public Indexer(String directory)
    {
	    this.directory = directory;
        this.titleMap = new ArrayList<String>();
    }

	public void run()
    {
		System.out.println("INDEXING");

		docCount = 0;
		cumulatedDocLength = 0;

        // Set up the parser and make sure we're notified every time a
		WikipediaReader reader = new WikipediaReader();
		reader.addTextCompletedListener(this);

        // Parse the Wiki entries and write the partial indexes
        reader.readWikiFile();
		this.writePartIndex();

        // Do some cleanup in the processing pipeline
		this.preprocessingPipeline.finished();

        // Merge the partial indices, write out seek list and list of links
        this.triggerMergingProcess();
		this.writeSeekList();
		this.writeLinkList();
	}

    /**
     * Write the seek list to a file.
     */
	private void writeSeekList()
    {
        seekList.setTitleMap(this.titleMap);
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(directory + "/seek_list"));
			seekList.setAverageDocumentLength(cumulatedDocLength / docCount);
			oos.writeObject(seekList);
			oos.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

    /**
     * Write the list of links to a file.
     */
	private void writeLinkList()
    {
		try {
			PrintWriter printWriter = new PrintWriter(directory + "/links");
			for (Map.Entry<String, StringBuilder> key : links.entrySet())
				printWriter.println(key.getKey() + "\t" + key.getValue().toString());
			printWriter.close();
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

    /**
     * Handle a parsed wiki document.
     *
     * @param text
     * @param title
     */
	@Override
	public void onTextCompleted(String text, String title)
    {
        // Parse the links, too
		if (this.createLinkConnections) {
			Pattern pattern = Pattern.compile("\\[\\[(.*?)\\]\\]");
			Matcher matcher = pattern.matcher(text);
			while (matcher.find()) {
				String s = matcher.group(1);
				String anchorText = "";
				String destination = "";
				if (s.contains("|")) {
					String[] splits = s.split("\\|");
					destination = splits[0];
                    try {
                        anchorText = splits[1];
                    } catch (Exception e) {
                        System.out.println("I'm dumb.");
                        anchorText = splits[0];
                    }
				} else {
					destination = s;
					anchorText = s;
				}
				StringBuilder sb = links.get(destination);
				String newLink = "\0" + title + "\0" + anchorText;
				if (sb == null) {
					StringBuilder builder = new StringBuilder();
					builder.append(newLink);
					links.put(destination, builder);
				} else
					sb.append(newLink);
			}
		}

        // Tokenize and preprocess the document
		List<CoreLabel> labels = preprocessingPipeline.start(text);


//		System.out.println("Title: " + title + ", Document-ID: " + documentId);
        // Index the tests
        this.indexText(labels);
        this.titleMap.add(title);

        this.watchMemory();

        // Gather some statistics
        this.docCount += 1;
        this.documentId += 1;
        this.cumulatedDocLength += labels.size();
        this.docLengths.put(this.documentId, labels.size());
    }

    protected void watchMemory()
    {
        // Gather memory info
        long usedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long freeMemory = Runtime.getRuntime().maxMemory() - usedMemory;

        // Log statistics
        if (this.documentId % 1000 == 0) {
            long now = System.currentTimeMillis();
            if (this.documentId > 0) {
                System.out.println("Dokument-ID: " + this.documentId + ". Last 1000 took: " + (now - this.startTime) + " ms");
            }
            this.startTime = now;
        }

        // Run the garbage collector if necessary
        if (freeMemory / 1024 / 1024 < 400) {
            // Dump the current partial index
            this.writePartIndex();

            System.out.print("Garbage collect ..");
            for (int i = 0; i < 10; i++) {
                System.gc();
            }
            System.out.println("Done.");
        }
    }

	public void indexText(List<CoreLabel> labels)
    {
        // Run through the results...
        for (int position = 0; position < labels.size(); position++) {
            CoreLabel label = labels.get(position);

            OccurrenceMap occurrenceMap;
            DocumentEntry documentEntry;

            // Fetch or create the occurrence map
            if (this.partIndex.containsKey(label.value())) {
                occurrenceMap = this.partIndex.get(label.value());
            } else {
                occurrenceMap = new OccurrenceMap();
                this.partIndex.put(label.value(), occurrenceMap);
            }

            // Fetch or create the document entry
            if (occurrenceMap.containsKey(this.documentId)) {
                documentEntry = occurrenceMap.get(this.documentId);
            } else {
                documentEntry = new DocumentEntry();
                occurrenceMap.put(this.documentId, documentEntry);
            }

            // And finally, store the current position
            documentEntry.positions.add(position);
            documentEntry.offsets.add(label.beginPosition());
            documentEntry.lengths.add(label.endPosition() - label.beginPosition());
        }
	}

    /**
     * Write the current part index.
     */
    public void writePartIndex()
    {
        // Write out the current part index to a file
        IndexWriter indexWriter = new IndexWriter(this.directory);
        indexWriter.write(this.partIndex);
        indexWriter.close();

        // Store the filename
        this.partIndexFileNames.add(indexWriter.getFileName());

        // Reset the part index so that we're ready for the next round
        this.partIndex.clear();
        System.gc();
    }

    public void triggerMergingProcess()
    {
        // Create reader instances for all our part indices
        List<IndexReader> indexReaders = new ArrayList<IndexReader>(preprocessingPipeline.size());
        for (String partIndexFileName : partIndexFileNames) {
            indexReaders.add(new IndexReader(partIndexFileName));
        }

        // Our merger needs access to a writer and all of our readers
        IndexWriter indexWriter = new IndexWriter(directory, "final_index", true);
        IndexMerger indexMerger = new IndexMerger(indexReaders, indexWriter);

        // Run the merge!
        try {
            indexMerger.merge();
        }
        catch (Exception e) {
            System.out.println("Something bad happened at merging time:");
            System.out.println(e.getLocalizedMessage());
        }

        this.indexFilename = indexWriter.getFileName();

        // Extract the seek list
        this.seekList = indexWriter.getSeekList();
	    this.seekList.setDocLengths(this.docLengths);
	    this.seekList.setDocumentCount(this.docCount);
    }

    public String getIndexFilename()
    {
        return this.indexFilename;
    }

    public SeekList getSeekList()
    {
        return this.seekList;
    }
}

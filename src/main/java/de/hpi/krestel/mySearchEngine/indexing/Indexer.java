package de.hpi.krestel.mySearchEngine.indexing;

import de.hpi.krestel.mySearchEngine.domain.*;
import de.hpi.krestel.mySearchEngine.processing.Pipeline;
import de.hpi.krestel.mySearchEngine.xml.DocumentReaderInterface;
import de.hpi.krestel.mySearchEngine.xml.DocumentReaderListener;
import edu.stanford.nlp.ling.CoreLabel;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Indexer implements DocumentReaderListener
{

	private Pipeline preprocessingPipeline = Pipeline.createPreprocessingPipeline();
    private final WordMap partIndex = new WordMap();
    private final List<String> partIndexFileNames = new ArrayList<String>();
	private final String directory;
	private int documentId = 0;
    private Documents documents;
	long startTime;

	boolean createLinkConnections = false;

    private DocumentReaderInterface reader;
    private Map<String, StringBuilder> links = new HashMap<String, StringBuilder>();

    public Indexer(String directory, DocumentReaderInterface reader)
    {
	    this.directory = directory;
        this.reader = reader;
        documents = new Documents(directory + "/documents");
    }

	public void run()
    {
		System.out.println("INDEXING");
        // Set up the parser and make sure we're notified every time a new document is found
		this.reader.addListener(this);

        // Parse the entries and write the partial indexes
        this.reader.startReading();
		this.writePartIndex();

        System.out.println("FINISHED WRITING PART INDICES");
        System.out.println("Number of documents: " + documents.getCount());
        System.out.println("Cumulated length of documents: " + documents.getCumulatedLength());

        // Do some cleanup in the processing pipeline
		this.preprocessingPipeline.finished();
        this.preprocessingPipeline = null;
        documents.finalize();
        documents = null;
        collectGarbage();

        // Write out seek list
        System.out.print("Writing LinkList... ");
        this.writeLinkList();
        this.links = null;
        System.out.println("Done.");

        // Merge the partial indices and list of links
        this.triggerMergingProcess();
	}

    /**
     * Write the seek list to a file.
     */
	private void writeSeekList(SeekList seekList)
    {
        // TODO implement this in the seeklist class by incremental writing.
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(directory + "/seek_list"));
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
	public void documentParsed(String text, String title)
    {
        // Parse the links, too
		if (this.createLinkConnections) {
            this.parseLinks(text, title);
		}

        // Tokenize and preprocess the document
		List<CoreLabel> labels = preprocessingPipeline.start(text);

//		System.out.println("Title: " + title + ", Document-ID: " + documentId);
        // Index the tests
        this.indexText(labels);
        // two infos about the document
        documents.add(title, labels.size());
        this.documentId += 1;

        this.watchMemory();

    }

    private void parseLinks(String text, String title)
    {
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

            collectGarbage();
        }
    }

    protected void collectGarbage()
    {
        System.out.print("Garbage collect ..");
        for (int i = 0; i < 10; i++) {
            System.gc();
        }
        System.out.println("Done.");
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
        List<IndexReader> indexReaders = new ArrayList<IndexReader>(partIndexFileNames.size());
        for (String partIndexFileName : partIndexFileNames) {
            indexReaders.add(new IndexReader(partIndexFileName));
        }

        //TODO create empty Seeklist here, set filename, handle ist to indexwriter

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

        // Extract and write SeekList
        // TODO this will be exchanged with seekList.finalize()
        System.out.print("Seek list: Extract... ");
        SeekList seekList = indexWriter.getSeekList();

        System.out.print("done. Write...");
        this.writeSeekList(seekList);
        System.out.println("done.");
    }
}

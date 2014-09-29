package de.hpi.krestel.mySearchEngine.indexing;

import de.hpi.krestel.mySearchEngine.domain.*;
import de.hpi.krestel.mySearchEngine.processing.Pipeline;
import de.hpi.krestel.mySearchEngine.xml.DocumentReaderInterface;
import de.hpi.krestel.mySearchEngine.xml.DocumentReaderListener;
import edu.stanford.nlp.ling.CoreLabel;

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
    
    private void announce(String msg)
    {
        System.out.println(msg);
    }

	public void run()
    {
        // Recreate the final index if necessary
        if (! new File(this.directory + "/final_index0001").isFile()) {
            this.announce("INDEXING");

            // Set up the parser and make sure we're notified every time a new document is found
            this.reader.addListener(this);

            // Parse the entries and write the partial indexes
            this.reader.startReading();
            this.writePartIndex();

            this.announce("FINISHED WRITING PART INDICES");
            this.announce("Number of documents: " + documents.getCount());
            this.announce("Cumulated length of documents: " + documents.getCumulatedLength());

            // Do some cleanup in the processing pipeline
            this.preprocessingPipeline.finished();
            this.preprocessingPipeline = null;
            this.documents.finalize();
            this.documents = null;
            this.collectGarbage();

            // Write out seek list
            if (createLinkConnections) {
                this.announce("Writing LinkList... ");
                this.writeLinkList();
                this.links = null;
                this.announce("Done.");
            }

            // Merge the partial indices and list of links
            this.triggerMergingProcess();
        }

        // Extract and write out the seek list
        this.createSeekList();
	}

    /**
     * Create the seek list from the finalized index.
     */
    private void createSeekList()
    {
        this.announce("Seek list: Extract... ");
        SeekListCreator creator = new SeekListCreator(new IndexReader(this.directory + "/final_index0001"));
        SeekList seekList = creator.createSeekList();

        this.announce("done. Write...");
        this.writeSeekList(seekList);
        this.announce("done.");
    }

    /**
     * Write the seek list to a file.
     */
	private void writeSeekList(SeekList seekList)
    {
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

//		this.announce("Title: " + title + ", Document-ID: " + documentId);
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
                    this.announce("I'm dumb.");
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
                this.announce("Dokument-ID: " + this.documentId + ". Last 1000 took: " + (now - this.startTime) + " ms");
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
        this.announce("Garbage collect ..");
        for (int i = 0; i < 10; i++) {
            System.gc();
        }
        this.announce("Done.");
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
        List<IndexReader> indexReaders = new ArrayList<IndexReader>(this.partIndexFileNames.size());
        for (String partIndexFileName : this.partIndexFileNames) {
            indexReaders.add(new IndexReader(partIndexFileName));
        }

        // Our merger needs access to a writer and all of our readers
        IndexWriter indexWriter = new IndexWriter(directory, "final_index");
        IndexMerger indexMerger = new IndexMerger(indexReaders, indexWriter);

        // Run the merge!
        try {
            indexMerger.merge();
        }
        catch (Exception e) {
            this.announce("Something bad happened at merging time:");
            this.announce(e.getLocalizedMessage());
        }
    }
}

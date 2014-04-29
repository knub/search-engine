package de.hpi.krestel.mySearchEngine.searching;

import de.hpi.krestel.mySearchEngine.domain.OccurrenceMap;
import de.hpi.krestel.mySearchEngine.domain.SeekList;
import de.hpi.krestel.mySearchEngine.indexing.IndexReader;
import de.hpi.krestel.mySearchEngine.processing.Pipeline;
import de.hpi.krestel.mySearchEngine.util.stream.RandomAccessInputStream;
import edu.stanford.nlp.ling.CoreLabel;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class Searcher {

    private SeekList seekList;
    private IndexReader indexReader;
    private RandomAccessInputStream randomAccessInputStream;

    public ResultSet search(String query) {
        Pipeline pipeline = Pipeline.createSearchPipeline();
        List<CoreLabel> searchTerms = pipeline.start (query);
        pipeline.finished();
        ResultSet allIds = new ResultSet();

        for (CoreLabel searchTerm : searchTerms) {
            allIds.merge(searchToken(searchTerm.value()));
        }

        return allIds;
    }

    private ResultSet searchToken(String token){
        if (seekList.containsKey(token)) {
            //System.out.println(token + " at Index pos: " + seekList.get(token));
            try {
                randomAccessInputStream.seek(seekList.get(token));
            } catch (IOException e) {
                System.out.println("Couldn't set seek position at index. Hmpf:");
                System.out.println(e.getLocalizedMessage());
                throw new RuntimeException(e);
            }
            OccurrenceMap occurrenceMap = indexReader.read().firstEntry().getValue();

            //System.out.println(token + " ==> " + occurrenceMap);
            return new ResultSet(occurrenceMap.keySet());
        } else {
            return new ResultSet();
        }
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
}

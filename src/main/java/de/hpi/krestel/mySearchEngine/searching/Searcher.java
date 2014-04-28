package de.hpi.krestel.mySearchEngine.searching;

import de.hpi.krestel.mySearchEngine.domain.SeekList;
import de.hpi.krestel.mySearchEngine.processing.Pipeline;
import edu.stanford.nlp.ling.CoreLabel;

import java.util.ArrayList;
import java.util.List;

public class Searcher {

    private String indexFilename;
    private SeekList seekList;

    public ArrayList<String> search(String query) {
        Pipeline pipeline = Pipeline.createSearchPipeline();
        List<CoreLabel> searchTerms = pipeline.start (query);
        ArrayList<String> results = new ArrayList<String>();

        for (CoreLabel searchTerm : searchTerms) {
            results.addAll(searchToken(searchTerm.value()));
        }

        return results;
    }

    private List<String> searchToken(String token) {
        return new ArrayList<String>();
    }

    public String getIndexFilename() {
        return indexFilename;
    }

    public void setIndexFilename(String indexFilename) {
        this.indexFilename = indexFilename;
    }

    public SeekList getSeekList() {
        return seekList;
    }

    public void setSeekList(SeekList seekList) {
        this.seekList = seekList;
    }
}

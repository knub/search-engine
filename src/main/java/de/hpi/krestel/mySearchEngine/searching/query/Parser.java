package de.hpi.krestel.mySearchEngine.searching.query;

import de.hpi.krestel.mySearchEngine.processing.Pipeline;
import de.hpi.krestel.mySearchEngine.searching.query.operators.Word;

public class Parser {

    private Pipeline pipeline;

    public Parser(Pipeline preprocessing) {
        this.pipeline = preprocessing;
    }

    public Operator parse(String query) {
        return new Word(query);
    }

}

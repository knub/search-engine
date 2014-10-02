package de.hpi.krestel.mySearchEngine.searching.query.operators;

import de.hpi.krestel.mySearchEngine.domain.DocumentEntry;
import de.hpi.krestel.mySearchEngine.domain.OccurrenceMap;
import de.hpi.krestel.mySearchEngine.searching.IndexSearcher;
import de.hpi.krestel.mySearchEngine.searching.query.Operator;
import de.hpi.krestel.mySearchEngine.searching.query.QueryException;
import de.hpi.krestel.mySearchEngine.searching.query.UnaryOperator;
import gnu.trove.procedure.TIntObjectProcedure;

public class LinkTo extends UnaryOperator implements Operator
{
    private String page;
    private String processed;

    public LinkTo(String page, String processed)
    {
        this.page = page;
        this.processed = processed;
    }

    public String getPage()
    {
        return this.page;
    }

    @Override
    public Operator pushOnto(Operator operator) throws QueryException
    {
        if (operator == null) return this;

        return operator.pushLinkTo(this);
    }

    @Override
    public OccurrenceMap evaluate(IndexSearcher searcher)
    {
        OccurrenceMap links = searcher.search("linkto:" + this.page);
        final OccurrenceMap texts = searcher.search(this.processed.split(" ")[0]);

        links.forEachEntry(new TIntObjectProcedure<DocumentEntry>() {
            @Override
            public boolean execute(int docId, DocumentEntry linkDoc) {
                if (texts.containsKey(docId)) {
                    try {
                        DocumentEntry textDoc = texts.get(docId);
                        linkDoc.positions.set(0, textDoc.positions.get(0));
                        linkDoc.offsets.set(0, textDoc.offsets.get(0));
                    } catch (Exception e) {}
                }
                return true;
            }
        });

        return links;
    }
}

package de.hpi.krestel.mySearchEngine.searching.query.operators;

import de.hpi.krestel.mySearchEngine.domain.DocumentEntry;
import de.hpi.krestel.mySearchEngine.domain.OccurrenceMap;
import de.hpi.krestel.mySearchEngine.searching.IndexSearcher;
import de.hpi.krestel.mySearchEngine.searching.SnippetReader;
import de.hpi.krestel.mySearchEngine.searching.query.Operator;
import de.hpi.krestel.mySearchEngine.searching.query.QueryException;
import de.hpi.krestel.mySearchEngine.searching.query.UnaryOperator;
import gnu.trove.procedure.TIntObjectProcedure;

public class LinkTo extends UnaryOperator implements Operator
{
    private String page;
    private SnippetReader snippetReader = null;

    public LinkTo(String page)
    {
        this.page = page;
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
        // find names of the page
        final OccurrenceMap occurrenceMap =  searcher.search(page);

        if (snippetReader == null) {
            createSnippetReader(searcher.getDirectory());
        }

        // filter documents which don't have links to it
        System.out.println("evaluating link to " + page);
        System.out.println("old map: " + occurrenceMap);
        occurrenceMap.forEachEntry(new TIntObjectProcedure<DocumentEntry>() {
            @Override
            public boolean execute(int docId, DocumentEntry docEntry) {
                boolean isLink;
                for (int i = 0; i < docEntry.size(); i++) {
                    isLink = checkIfLink(docId, docEntry.offsets.get(i), docEntry.lengths.get(i));
                    if (isLink) {
                        return true;
                    }
                }
                System.out.println("no link found for docId, remove it: " + docId);
                occurrenceMap.remove(docId);
                return true;
            }
        });
        System.out.println("new map: " + occurrenceMap);

        return occurrenceMap;
    }

    private boolean checkIfLink(int docId, int inFileOffset, int wordLength)
    {
        // something is treated as a link, if there are two box brackets before or after it
        // read two characters before and after occurrence
        String[] plainTexts = snippetReader.readParts(docId, inFileOffset, wordLength, 10, 10);
        System.out.println("link?!: " + plainTexts[0] + plainTexts[1] + plainTexts[2]);
        // check if one of them is double box brackets
        return plainTexts[0].equals("[[") || plainTexts[2].equals("]]");
    }

    private void createSnippetReader(String directory)
    {
        snippetReader = new SnippetReader(directory);
    }
}

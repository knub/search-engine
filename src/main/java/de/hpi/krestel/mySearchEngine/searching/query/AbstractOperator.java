package de.hpi.krestel.mySearchEngine.searching.query;

import de.hpi.krestel.mySearchEngine.domain.DocumentEntry;
import de.hpi.krestel.mySearchEngine.domain.OccurrenceMap;
import de.hpi.krestel.mySearchEngine.searching.IndexSearcher;
import de.hpi.krestel.mySearchEngine.searching.query.operators.*;
import gnu.trove.procedure.TIntObjectProcedure;

abstract public class AbstractOperator implements Operator
{
    @Override
    public Operator pushBinary(BinaryOperator operator)
    {
        throw new QueryException(String.format(
                "Cannot push binary operator on %s.",
                this
        ));
    }

    @Override
    public Operator pushWord(Word operator)
    {
        throw new QueryException(String.format(
                "Cannot push word operator on %s.",
                this
        ));
    }

    @Override
    public Operator pushRankedWord(RankedWord operator)
    {
        throw new QueryException(String.format(
                "Cannot push ranked word operator on %s.",
                this
        ));
    }

    @Override
    public Operator pushPrefixedWord(PrefixedWord operator)
    {
        throw new QueryException(String.format(
                "Cannot push prefixed word operator on %s.",
                this
        ));
    }

    @Override
    public Operator pushPhrase(Phrase operator)
    {
        throw new QueryException(String.format(
                "Cannot push phrase operator on %s.",
                this
        ));
    }

    @Override
    public Operator pushLinkTo(LinkTo operator)
    {
        throw new QueryException(String.format(
                "Cannot push linkto operator on %s.",
                this
        ));
    }

    protected void mergeWithRanks(final OccurrenceMap onto, OccurrenceMap from)
    {
        from.forEachEntry(new TIntObjectProcedure<DocumentEntry>() {
            @Override
            public boolean execute(int docId, DocumentEntry docEntry) {
                DocumentEntry currentDocEntry = onto.get(docId);
					/*
					 * If we want to show the surrounding of the search result, we have to be more
					 * sophisticated here, since this current implementation only stores the first document entry.
					 */
                if (currentDocEntry == null) {
                    onto.put(docId, docEntry);
                } else {
                    currentDocEntry.setRank(currentDocEntry.getRank() + docEntry.getRank());
                }
                return true;
            }
        });
    }

    protected OpExecutor runInThread(Operator op, IndexSearcher searcher)
    {
        OpExecutor exec = new OpExecutor(op, searcher);
        exec.start();
        return exec;
    }
}

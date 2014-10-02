package de.hpi.krestel.mySearchEngine.searching.query;

import de.hpi.krestel.mySearchEngine.processing.Pipeline;
import de.hpi.krestel.mySearchEngine.searching.query.operators.*;

public class QueryParser
{
	private Pipeline pipeline;
    private Operator stack;

	public QueryParser(Pipeline preprocessing)
    {
		this.pipeline = preprocessing;
	}

	public Operator parse(String query) throws QueryException
    {
        this.resetState();
        String[] parts = query.split(" +");

        boolean inBut = false;
        boolean inLinkTo = false;
        boolean inPhrase = false;
        String phrase = "";

        for (String token: parts) {
            token = token.trim().toLowerCase();

            if (token.startsWith("\"")) {
                inPhrase = true;
                phrase = token.substring(1);
            } else if (token.endsWith("\"")) {
                inPhrase = false;
                phrase += " " + token.substring(0, token.length() - 1);
                this.handlePhraseToken(phrase);
                phrase = "";
            } else {
                if (inPhrase) {
                    phrase += " " + token;
                } else {
                    if (inBut) {
                        inBut = false;
                        if (token.equals("not")) {
                            this.handleToken("butnot");
                            continue;
                        } else {
                            this.handleToken("but");
                        }
                    } else if (inLinkTo) {
                        inLinkTo = false;

                        this.handleLinkToToken(token);
                        continue;
                    } else if (token.equals("but")) {
                        inBut = true;
                        continue;
                    } else if (token.equals("linkto")) {
                        inLinkTo = true;
                        continue;
                    }
                    this.handleToken(token);
                }
            }
        }

        return this.stack;
	}

	private void resetState()
    {
        this.stack = null;
	}

	private void handleToken(String token)
    {
        Operator op = null;
        if (token.equals("and")) {
            // Do nothing here
		} else if (token.equals("or")) {
            op = new Or();
		} else if (token.equals("butnot")) {
            op = new ButNot();
		} else if (token.endsWith("*")) {
            op = this.createPrefixedWord(token);
		} else {
            op = this.createWord(token);
		}

        if (op != null) {
            this.stack = op.pushOnto(this.stack);
        }
	}

	private void handlePhraseToken(String phrase)
    {
        Phrase op = new Phrase(phrase.split(" "));

        this.stack = op.pushOnto(this.stack);
	}

    private void handleLinkToToken(String token)
    {
        token = pipeline.processForQuery(token);
        LinkTo op = new LinkTo(token);

        this.stack = op.pushOnto(this.stack);
    }

    private Word createWord(String token)
    {
        String processedToken = pipeline.processForQuery(token);
        if (processedToken != null) {
            return new Word(processedToken);
        } else {
            // token was a stopword or something.
            //TODO: should this realy be done tis way? can't be found anyway, probably. do prefixsearch or something?!
            return new Word(token);
        }
    }

    private PrefixedWord createPrefixedWord(String token)
    {
        // Remove star
        token = token.substring(0, token.length() - 1);

        // Run it through the pipeline
        token = pipeline.processForQuery(token);

        return new PrefixedWord(token);
    }
}

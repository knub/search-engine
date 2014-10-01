package de.hpi.krestel.mySearchEngine.searching.query;

import de.hpi.krestel.mySearchEngine.processing.Pipeline;
import de.hpi.krestel.mySearchEngine.searching.query.operators.*;

import java.io.*;
import java.util.ArrayList;

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
        boolean inPhrase = false;
        String phrase = "";

        for (String token: parts) {
            token = token.trim().toLowerCase();

            if (token.startsWith("\"")) {
                inPhrase = true;
            } else if (token.endsWith("\"")) {
                inPhrase = false;
                this.handlePhraseToken(phrase.trim());
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
                    } else if (token.equals("but")) {
                        inBut = true;
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
            op = this.createBinaryOperator("or");
		} else if (token.equals("butnot")) {
            op = this.createBinaryOperator("butnot");
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

    private Operator createBinaryOperator(String type)
    {
        if (type.equals("and")) {
            return new And();
        } else if (type.equals("or")) {
            return new Or();
        } else {
            return new ButNot();
        }
    }

    private Word createWord(String token)
    {
        return new Word(pipeline.processForQuery(token));
    }

    private PrefixedWord createPrefixedWord(String token)
    {
        return new PrefixedWord(token.substring(0, token.length() - 1));
    }
}

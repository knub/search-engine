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
        query = query.replaceAll("(?i)but +not", "butnot");
		StreamTokenizer tokenizer = this.buildTokenizer(query);

		this.resetState();
		try {
			while (tokenizer.nextToken() != StreamTokenizer.TT_EOF) {
                if (tokenizer.ttype == StreamTokenizer.TT_WORD) {
                    this.handleToken(tokenizer.sval);
				} else if (tokenizer.ttype == StreamTokenizer.TT_NUMBER) {
                    this.handleToken(tokenizer.toString());
                } else if (tokenizer.ttype == '"') {
                    this.handlePhraseToken(tokenizer.sval);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

        return this.stack;
	}

	private void resetState()
    {
        this.stack = null;
	}

	private StreamTokenizer buildTokenizer(String query)
    {
		Reader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(query.getBytes())));
		StreamTokenizer tokenizer = new StreamTokenizer(reader);

		tokenizer.eolIsSignificant(false);
		tokenizer.lowerCaseMode(true);
		tokenizer.wordChars('*', '*');

		return tokenizer;
	}

	private void handleToken(String token)
    {
        Operator op = null;
        if (token.toLowerCase().equals("and")) {
            // Do nothing here
		} else if (token.toLowerCase().equals("or")) {
            op = this.createBinaryOperator("or");
		} else if (token.toLowerCase().equals("butnot")) {
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
        if (this.stack == null) {
            return new Word(type);
        } else if (this.stack instanceof BinaryOperator) {
            BinaryOperator binary = (BinaryOperator) this.stack;
            if (! binary.hasRight()) {
                return new Word(type);
            }
        }

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

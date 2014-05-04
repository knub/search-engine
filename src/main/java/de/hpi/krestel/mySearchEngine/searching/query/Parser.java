package de.hpi.krestel.mySearchEngine.searching.query;

import de.hpi.krestel.mySearchEngine.processing.Pipeline;
import de.hpi.krestel.mySearchEngine.searching.query.operators.*;
import edu.stanford.nlp.ling.CoreLabel;

import java.io.*;
import java.util.List;

public class Parser {

    private Pipeline pipeline;
    private String state;
    private Operator leftStash;
    private Operator rightStash;
    private String binaryOp;

    public Parser(Pipeline preprocessing) {
        this.pipeline = preprocessing;
    }

    public Operator parse(String query) {
        StreamTokenizer tokenizer = this.buildTokenizer(query);

        this.resetState();
        try {
            while (tokenizer.nextToken() != StreamTokenizer.TT_EOF) {

                if (tokenizer.ttype == StreamTokenizer.TT_WORD) {
                    this.handleWordToken(pipeline.processForQuery(tokenizer.sval));
                } else if (tokenizer.ttype == '"') {
                    this.handlePhraseToken(pipeline.processPhraseForQuery(tokenizer.sval));
                }

            }
        } catch (IOException e) {
            // Should not happen. We're not doing real IO here.
	        throw new RuntimeException(e);
        }

        return this.createOperator();
    }

    private void resetState() {
        this.state = "left";
        this.leftStash = null;
        this.rightStash = null;
        this.binaryOp = "";
    }

    private StreamTokenizer buildTokenizer(String query) {
        Reader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(query.getBytes())));
        StreamTokenizer tokenizer = new StreamTokenizer(reader);

        tokenizer.eolIsSignificant(false);
        tokenizer.lowerCaseMode(true);
        tokenizer.wordChars('*', '*');

        return tokenizer;
    }

    private Operator createOperator() {
        if (this.state.equals("operator")) {
            return this.leftStash;
        } else { /* TODO: SHould this be  if (this.state.equals("done")) ??? */
            return this.createBinaryOperator();
        }
    }

    private Operator createBinaryOperator() {
        if (this.binaryOp.equals("and")) {
            return new And(this.leftStash, this.rightStash);
        } else if (this.binaryOp.equals("or")) {
            return new Or(this.leftStash, this.rightStash);
        } else {
            return new ButNot(this.leftStash, this.rightStash);
        }
    }

    private void handleWordToken(String word) {
        if (word.equals("and")) {
            this.handleBinaryOp("and");
        } else if (word.equals("or")) {
            this.handleBinaryOp("or");
        } else if (word.equals("but")) {
            this.handleBut();
        } else if (word.equals("not")) {
            this.handleNot();
        } else if (word.endsWith("*")) {
            this.handleOperand(new PrefixedWord(word.substring(0, word.length() - 1)));
        } else {
            this.handleOperand(new Word(word));
        }

        return;
    }

    private void handlePhraseToken(String[] phrase) {
        this.handleOperand(new Phrase(phrase));
    }

    private void handleBinaryOp(String type) {
        if (this.state.equals("operator")) {
            this.binaryOp = type;
            this.state = "right";
        } else {
            this.handleOperand(new Word(type));
        }
    }

    private void handleBut() {
        if (this.state.equals("operator")) {
            this.state = "but";
        } else {
            this.handleOperand(new Word("but"));
        }
    }

    private void handleNot() {
        if (this.state.equals("but")) {
            this.binaryOp = "butnot";
            this.state = "right";
        } else {
            this.handleOperand(new Word("not"));
        }
    }

    private void handleOperand(Operator op) {
        if (this.state.equals("left")) {
            this.leftStash = op;
            this.state = "operator";
        } else if (this.state.equals("right")) {
            this.rightStash = op;
            this.state = "done";
        }

        // TODO: Throw exception!
    }

}

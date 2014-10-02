package de.hpi.krestel.mySearchEngine;

import de.hpi.krestel.mySearchEngine.processing.Pipeline;
import de.hpi.krestel.mySearchEngine.searching.query.Operator;
import de.hpi.krestel.mySearchEngine.searching.query.QueryParser;
import de.hpi.krestel.mySearchEngine.searching.query.operators.*;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.Arrays;

public class QueryParserTest extends TestCase {

    /**
     * Creates the test case.
     *
     * @param testName name of the test case
     */
    public QueryParserTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(QueryParserTest.class);
    }

    public void testParsing() {
        QueryParser queryParser = new QueryParser(new Pipeline());

        Operator op, left, right;
        String[] words;

        op = queryParser.parse("\"eine lange Phrase\"");
        assertTrue(op instanceof Phrase);
        assertEquals("eine lange phrase", ((Phrase) op).getPhrase());

        op = queryParser.parse("Italien AND Pizza");
        assertTrue(op instanceof RankedWord);
        words = ((RankedWord) op).getWords();
        assertTrue(Arrays.asList(words).contains("italien"));
        assertTrue(Arrays.asList(words).contains("pizza"));

        op = queryParser.parse("prefix* AND attribut");
        assertTrue(op instanceof And);
        right = ((And) op).getRight();
        left = ((And) op).getLeft();
        assertEquals("attribut", ((Word) right).getWord());
        assertEquals("prefix", ((PrefixedWord) left).getPrefix());

        op = queryParser.parse("komplizierte wörter sammlung");
        assertTrue(op instanceof RankedWord);
        words = ((RankedWord) op).getWords();
        assertTrue(Arrays.asList(words).contains("komplizierte"));
        assertTrue(Arrays.asList(words).contains("wörter"));
        assertTrue(Arrays.asList(words).contains("sammlung"));

        op = queryParser.parse("apfel AND birne BUT NOT obst");
        assertTrue(op instanceof ButNot);
        right = ((ButNot) op).getRight();
        assertTrue(right instanceof Word);
        left = ((ButNot) op).getLeft();
        assertTrue(left instanceof RankedWord);
        assertEquals("obst", ((Word) right).getWord());
        words = ((RankedWord) left).getWords();
        assertTrue(Arrays.asList(words).contains("apfel"));
        assertTrue(Arrays.asList(words).contains("birne"));

        op = queryParser.parse("Dr. No");
        assertTrue(op instanceof RankedWord);
        words = ((RankedWord) op).getWords();
        assertTrue(Arrays.asList(words).contains("dr."));
        assertTrue(Arrays.asList(words).contains("no"));

        op = queryParser.parse("ICE BUT NOT T");
        assertTrue(op instanceof ButNot);
        right = ((ButNot) op).getRight();
        assertTrue(right instanceof Word);
        left = ((ButNot) op).getLeft();
        assertTrue(left instanceof Word);
        assertEquals("t", ((Word) right).getWord());
        assertEquals("ice", ((Word) left).getWord());

        op = queryParser.parse("08/15");
        assertTrue(op instanceof Word);
        assertEquals("08/15", ((Word) op).getWord());

        op = queryParser.parse("LINKTO flughafen");
        assertTrue(op instanceof Word);
        assertEquals("linkto:flughafen", ((Word) op).getWord());
    }

}

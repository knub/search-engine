package de.hpi.krestel.mySearchEngine;

import de.hpi.krestel.mySearchEngine.processing.Pipeline;
import de.hpi.krestel.mySearchEngine.searching.query.Operator;
import de.hpi.krestel.mySearchEngine.searching.query.QueryParser;
import de.hpi.krestel.mySearchEngine.searching.query.operators.*;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

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

        op = queryParser.parse("\"eine lange Phrase\"");
        assertTrue(op instanceof Phrase);

        op = queryParser.parse("Italien AND Pizza");
        assertTrue(op instanceof RankedWord);

        op = queryParser.parse("prefix* AND attribut");
        assertTrue(op instanceof And);

        op = queryParser.parse("komplizierte wörter sammlung");
        assertTrue(op instanceof RankedWord);

        op = queryParser.parse("apfel AND birne BUT NOT obst");
        assertTrue(op instanceof ButNot);
        right = ((ButNot) op).getRight();
        assertTrue(right instanceof Word);
        left = ((ButNot) op).getLeft();
        assertTrue(left instanceof RankedWord);

        op = queryParser.parse("Dr. No");
        assertTrue(op instanceof RankedWord);

        op = queryParser.parse("ICE BUT NOT T");
        assertTrue(op instanceof ButNot);

        op = queryParser.parse("08/15");
        System.out.println(op);
        System.out.println(((Word) op).getWord());
        assertTrue(op instanceof Word);
    }

}

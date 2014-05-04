package de.hpi.krestel.mySearchEngine;

import de.hpi.krestel.mySearchEngine.processing.Pipeline;
import de.hpi.krestel.mySearchEngine.searching.query.Operator;
import de.hpi.krestel.mySearchEngine.searching.query.QueryParser;
import de.hpi.krestel.mySearchEngine.searching.query.operators.And;
import de.hpi.krestel.mySearchEngine.searching.query.operators.ButNot;
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

        Operator op;

        op = queryParser.parse("Haus AND Baum*");
        assertTrue(op instanceof And);

        op = queryParser.parse("baum BUT NOT fish");
        assertTrue(op instanceof ButNot);;
    }

}

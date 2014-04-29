package de.hpi.krestel.mySearchEngine;

import de.hpi.krestel.mySearchEngine.processing.Pipeline;
import de.hpi.krestel.mySearchEngine.searching.query.Operator;
import de.hpi.krestel.mySearchEngine.searching.query.Parser;
import de.hpi.krestel.mySearchEngine.searching.query.operators.And;
import de.hpi.krestel.mySearchEngine.searching.query.operators.ButNot;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ParserTest extends TestCase {

    /**
     * Creates the test case.
     *
     * @param testName name of the test case
     */
    public ParserTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(ParserTest.class);
    }

    public void testParsing() {
        Parser parser = new Parser(new Pipeline());

        Operator op;

        op = parser.parse("Haus AND Baum*");
        assertTrue(op instanceof And);

        op = parser.parse("baum BUT NOT fish");
        assertTrue(op instanceof ButNot);;
    }

}

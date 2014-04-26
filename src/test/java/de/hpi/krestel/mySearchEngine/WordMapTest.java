package de.hpi.krestel.mySearchEngine;

import de.hpi.krestel.mySearchEngine.domain.DocumentEntry;
import de.hpi.krestel.mySearchEngine.domain.OccurrenceMap;
import de.hpi.krestel.mySearchEngine.domain.WordMap;
import gnu.trove.set.TIntSet;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class WordMapTest extends TestCase {

    /**
     * Creates the test case.
     *
     * @param testName name of the test case
     */
    public WordMapTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(WordMapTest.class);
    }

    public void testMerging() throws Exception {
        WordMap map1 = new WordMap();
        WordMap map2 = new WordMap();

        OccurrenceMap occur1 = new OccurrenceMap();
        OccurrenceMap occur2 = new OccurrenceMap();

        occur1.put(1, new DocumentEntry());
        occur1.put(2, new DocumentEntry());
        occur1.put(3, new DocumentEntry());

        occur2.put(4, new DocumentEntry());
        occur2.put(5, new DocumentEntry());
        occur2.put(6, new DocumentEntry());

        map1.put("word", occur1);
        map2.put("word", occur2);

        map1.partIndexMerge(map2);

        assertEquals(6, occur1.size());
        TIntSet keys = occur1.keySet();

        assertTrue(keys.contains(1));
        assertTrue(keys.contains(2));
        assertTrue(keys.contains(3));
        assertTrue(keys.contains(4));
        assertTrue(keys.contains(5));
        assertTrue(keys.contains(6));
        
        assertTrue(map2.isEmpty());
        assertTrue(occur2.isEmpty());
    }

    public void testMergingDifferentKeysThrowsException() {
        try {
            WordMap map1 = new WordMap();
            WordMap map2 = new WordMap();

            map1.put("word1", new OccurrenceMap());
            map2.put("word2", new OccurrenceMap());

            map1.partIndexMerge(map2);

            this.fail("Did not catch expected exception.");
        } catch (Exception e) {}
    }

    public void testMergingOccurrenceMapsWithNonDistinctDocumentKeysThrowsException() {
        try {
            WordMap map1 = new WordMap();
            WordMap map2 = new WordMap();

            OccurrenceMap occur1 = new OccurrenceMap();
            OccurrenceMap occur2 = new OccurrenceMap();

            occur1.put(1, new DocumentEntry());
            occur1.put(2, new DocumentEntry());
            occur1.put(3, new DocumentEntry());

            occur2.put(1, new DocumentEntry());
            occur2.put(2, new DocumentEntry());
            occur2.put(3, new DocumentEntry());

            map1.put("word", occur1);
            map2.put("word", occur2);

            map1.partIndexMerge(map2);

            this.fail("Did not catch expected exception.");
        } catch (Exception e) {}
    }

}

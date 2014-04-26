package de.hpi.krestel.mySearchEngine;

import de.hpi.krestel.mySearchEngine.domain.DocumentEntry;
import de.hpi.krestel.mySearchEngine.domain.OccurrenceMap;
import de.hpi.krestel.mySearchEngine.domain.WordMap;
import de.hpi.krestel.mySearchEngine.indexing.IndexReader;
import de.hpi.krestel.mySearchEngine.indexing.IndexWriter;
import de.hpi.krestel.mySearchEngine.indexing.Merger;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MergeTest extends TestCase {

	public MergeTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(MergeTest.class);
	}

	public void testBasicMerge() throws Exception {
		WordMap wm1 = new WordMap();
		wm1.put("aaa", new OccurrenceMap() {{
			int i = 1;
			put(1, new DocumentEntry(i++, i++, i++));
			put(3, new DocumentEntry(i++, i++, i++));
			put(4, new DocumentEntry(i++, i++, i++));
			put(6, new DocumentEntry(i++, i++, i++));
		}});
		wm1.put("bbb", new OccurrenceMap() {{
			int i = 1;
			put(3, new DocumentEntry(i++, i++, i++));
			put(4, new DocumentEntry(i++, i++, i++));
			put(5, new DocumentEntry(i++, i++, i++));
			put(6, new DocumentEntry(i++, i++, i++));
		}});
		wm1.put("ccc", new OccurrenceMap() {{
			int i = 1;
			put(3, new DocumentEntry(i++, i++, i++));
			put(4, new DocumentEntry(i++, i++, i++));
			put(5, new DocumentEntry(i++, i++, i++));
			put(6, new DocumentEntry(i++, i++, i++));
		}});
		WordMap wm2 = new WordMap();
		wm2.put("aaa", new OccurrenceMap() {{
			int i = 1;
			put(11, new DocumentEntry(i++, i++, i++));
			put(13, new DocumentEntry(i++, i++, i++));
			put(14, new DocumentEntry(i++, i++, i++));
			put(16, new DocumentEntry(i++, i++, i++));
		}});
		wm2.put("bbb", new OccurrenceMap() {{
			int i = 1;
			put(13, new DocumentEntry(i++, i++, i++));
			put(14, new DocumentEntry(i++, i++, i++));
			put(15, new DocumentEntry(i++, i++, i++));
			put(16, new DocumentEntry(i++, i++, i++));
		}});
		wm2.put("ddd", new OccurrenceMap() {{
			int i = 1;
			put(23, new DocumentEntry(i++, i++, i++));
			put(24, new DocumentEntry(i++, i++, i++));
			put(25, new DocumentEntry(i++, i++, i++));
			put(26, new DocumentEntry(i++, i++, i++));
		}});
		WordMap wm3 = new WordMap();
		wm3.put("aaa", new OccurrenceMap() {{
			int i = 1;
			put(21, new DocumentEntry(i++, i++, i++));
			put(23, new DocumentEntry(i++, i++, i++));
			put(24, new DocumentEntry(i++, i++, i++));
			put(26, new DocumentEntry(i++, i++, i++));
		}});
		wm3.put("ddd", new OccurrenceMap() {{
			int i = 1;
			put(33, new DocumentEntry(i++, i++, i++));
			put(34, new DocumentEntry(i++, i++, i++));
			put(35, new DocumentEntry(i++, i++, i++));
			put(36, new DocumentEntry(i++, i++, i++));
		}});
		wm3.put("eee", new OccurrenceMap() {{
			int i = 1;
			put(43, new DocumentEntry(i++, i++, i++));
			put(44, new DocumentEntry(i++, i++, i++));
			put(45, new DocumentEntry(i++, i++, i++));
			put(46, new DocumentEntry(i++, i++, i++));
		}});

		IndexWriter writer = new IndexWriter("testindex");
		writer.write(wm1);
		writer.close();
		writer.write(wm2);
		writer.close();
		writer.write(wm3);
		writer.close();

		Merger merger = new Merger(Arrays.asList(
				new IndexReader("data/testindex0001"),
				new IndexReader("data/testindex0002"),
				new IndexReader("data/testindex0003")
		), new IndexWriter("data/testoutput"));

		merger.merge();

		WordMap finalWordMap = new WordMap();
		IndexReader indexReader = new IndexReader("data/testoutput");
		WordMap tmpWordMap = indexReader.read();
		while (tmpWordMap != null) {
			finalWordMap.merge(tmpWordMap);
		}

		assertEquals(12, finalWordMap.get("aaa").size());
		assertEquals( 8, finalWordMap.get("bbb").size());
		assertEquals( 4, finalWordMap.get("ccc").size());
		assertEquals( 8, finalWordMap.get("ddd").size());
		assertEquals( 4, finalWordMap.get("eee").size());
		Set<Integer> set1 = new HashSet<Integer>(Arrays.asList(1, 3, 4, 6, 11, 13, 14, 16, 21, 23, 24, 26));
		Set <Integer> set2 = new HashSet<Integer>();
		for (int key : finalWordMap.get("aaa").keys())
			set2.add(key);
		assertEquals(set1, set2);
	}
}

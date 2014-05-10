package de.hpi.krestel.mySearchEngine;

import de.hpi.krestel.mySearchEngine.searching.IndexSearcher;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class BM25Test extends TestCase {

	public BM25Test(String testName) {
		super(testName);
	}

	public static Test suite() {
		return new TestSuite(BM25Test.class);
	}

	public void testRankingFunction() {
		double rankPresident = IndexSearcher.calculateRank(500000, 40000, 9, 10, 15, 1);
		System.out.println(rankPresident);
		assertEquals(rankPresident, 5.002, 0.01);
		double rankLincoln = IndexSearcher.calculateRank(500000, 300, 9, 10, 25, 1);
		System.out.println(rankLincoln);
		assertEquals(rankLincoln, 15.6223, 0.01);
	}
}

package de.hpi.krestel.mySearchEngine;

import de.hpi.krestel.mySearchEngine.util.stream.BitInputStream;
import de.hpi.krestel.mySearchEngine.util.stream.BitOutputStream;
import de.hpi.krestel.mySearchEngine.util.stream.EliasGammaReader;
import de.hpi.krestel.mySearchEngine.util.stream.EliasGammaWriter;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Unit test for simple App.
 */
public class EliasTest extends TestCase {

	private final String ELIAS_GAMMA_FILE_NAME = "elias-gamma-test.result";
	/**
	 * Creates the test case.
	 *
	 * @param testName name of the test case
	 */
	public EliasTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(EliasTest.class);
	}

	/**
	 * Rigourous Test :-).
	 */
	public void testEliasGamma() throws IOException {
		EliasGammaWriter egw = new EliasGammaWriter(new BitOutputStream(new FileOutputStream(ELIAS_GAMMA_FILE_NAME)));
		// 1
		egw.write(1);
		// 001 10
		egw.write(6);
		// 0000000001 111111111
		egw.write(1023);
		// 001 10
		egw.write(6);
		egw.close();

		EliasGammaReader egr = new EliasGammaReader(new BitInputStream(new FileInputStream(ELIAS_GAMMA_FILE_NAME)));
		assertEquals(1, egr.read());
		assertEquals(6, egr.read());
		assertEquals(1023, egr.read());
		assertEquals(6, egr.read());
		egr.close();
	}
}

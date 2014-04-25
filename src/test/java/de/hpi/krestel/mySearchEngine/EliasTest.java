package de.hpi.krestel.mySearchEngine;

import de.hpi.krestel.mySearchEngine.util.stream.*;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.*;

/**
 * Unit test for simple App.
 */
public class EliasTest extends TestCase {

	private final String ELIAS_GAMMA_FILE_NAME = "elias-gamma-test.result";
    private final String ELIAS_DELTA_FILE_NAME = "elias-delta-test.result";

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
	 * Test basic read and write for Elias Gamma encoding.
	 */
	public void testEliasGamma() throws IOException {
		EliasGammaWriter egw = new EliasGammaWriter(new BitOutputStream(new FileOutputStream(ELIAS_GAMMA_FILE_NAME)));
		// 0
		egw.write(1);
		// 110 10
		egw.write(6);
		// 1111111110 111111111
		egw.write(1023);
		// 110 10
		egw.write(6);
		egw.close();

        this.dumpFile(ELIAS_GAMMA_FILE_NAME);

		EliasGammaReader egr = new EliasGammaReader(new BitInputStream(new FileInputStream(ELIAS_GAMMA_FILE_NAME)));
		assertEquals(1, egr.read());
		assertEquals(6, egr.read());
		assertEquals(1023, egr.read());
		assertEquals(6, egr.read());
		egr.close();
	}

    /**
     * Test basic read and write for Elias Delta encoding.
     */
    public void testEliasDelta() throws IOException {
        EliasDeltaWriter edw = new EliasDeltaWriter(new BitOutputStream(new FileOutputStream(ELIAS_DELTA_FILE_NAME)));
        // 0
        edw.write(1);
        // 10 1 10
        edw.write(6);
        // 1110 010 111111111
        edw.write(1023);
        // 10 1 10
        edw.write(6);
        edw.close();

        this.dumpFile(ELIAS_DELTA_FILE_NAME);

        EliasDeltaReader edr = new EliasDeltaReader(new BitInputStream(new FileInputStream(ELIAS_DELTA_FILE_NAME)));
        assertEquals(1, edr.read());
        assertEquals(6, edr.read());
        assertEquals(1023, edr.read());
        assertEquals(6, edr.read());
        edr.close();
    }

    private void dumpFile(String filename) throws IOException {
        DataInputStream reader = new DataInputStream(new FileInputStream(filename));
        System.out.println("Dumping file " + filename);
        try {
            while (true) {
                byte in = reader.readByte();
                String in2 = String.format("%8s", Integer.toBinaryString(in & 0xFF)).replace(' ', '0');
                System.out.println(in2);
            }
        } catch (EOFException e) {
            // ...
        } finally {
            reader.close();
        }
    }

}

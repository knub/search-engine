package de.hpi.krestel.mySearchEngine.util.stream;

import java.io.IOException;
import java.io.OutputStream;

public class EliasDeltaWriter extends OutputStream {

    private BitOutputStream output;
    private EliasGammaWriter gamma;

    public EliasDeltaWriter(BitOutputStream stream) {
        this.output = stream;
        this.gamma = new EliasGammaWriter(stream);
    }

    /**
     * Writes the specified byte to this output stream. The general
     * contract for <code>write</code> is that one byte is written
     * to the output stream. The byte to be written is the eight
     * low-order bits of the argument <code>b</code>. The 24
     * high-order bits of <code>b</code> are ignored.
     * <p/>
     * Subclasses of <code>OutputStream</code> must provide an
     * implementation for this method.
     *
     * @param b the <code>byte</code>.
     * @throws java.io.IOException if an I/O error occurs. In particular,
     *                             an <code>IOException</code> may be thrown if the
     *                             output stream has been closed.
     */
    @Override
    public void write(int b) throws IOException {
	    if (b == 0)
		    throw new RuntimeException("Do not try to encode a zero!");
        int length = (int) Math.floor(Math.log(b) / Math.log(2));
        this.writeGamma(length + 1);

        int value = (~(1 << length)) & b;

        this.writeValue(value, length);
    }

    private void writeValue(int value, int length) throws IOException {
        if (length > 0) {
            this.writeValue(value / 2, length - 1);
            this.output.writeBit(value % 2);
        }
    }

    /**
     * Flushes this output stream and forces any buffered output bytes
     * to be written out. The general contract of <code>flush</code> is
     * that calling it is an indication that, if any bytes previously
     * written have been buffered by the implementation of the output
     * stream, such bytes should immediately be written to their
     * intended destination.
     * <p/>
     * If the intended destination of this stream is an abstraction provided by
     * the underlying operating system, for example a file, then flushing the
     * stream guarantees only that bytes previously written to the stream are
     * passed to the operating system for writing; it does not guarantee that
     * they are actually written to a physical device such as a disk drive.
     * <p/>
     * The <code>flush</code> method of <code>OutputStream</code> does nothing.
     *
     * @throws java.io.IOException if an I/O error occurs.
     */
    @Override
    public void flush() throws IOException {
        this.output.flush();
    }

    /**
     * Closes this output stream and releases any system resources
     * associated with this stream. The general contract of <code>close</code>
     * is that it closes the output stream. A closed stream cannot perform
     * output operations and cannot be reopened.
     * <p/>
     * The <code>close</code> method of <code>OutputStream</code> does nothing.
     *
     * @throws java.io.IOException if an I/O error occurs.
     */
    @Override
    public void close() throws IOException {
        this.output.close();
        super.close();
    }

    private void writeGamma(int b) throws IOException {
        this.gamma.write(b);
    }

}

package propra.imageconverter.utils.streams.bytes;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Objects;

/**
 * Outputstream, der einen anderen als Basis nimmt, aber die zu schreibenden
 * Bytes zählt.
 *
 * @author marvin
 *
 */
public class CounterOutputStream extends OutputStream {

	private final OutputStream out;

	private BigInteger counter;

	public CounterOutputStream(final OutputStream out) {
		this.out = Objects.requireNonNull(out, "out");
		this.counter = BigInteger.ZERO;
	}

	@Override
	public void write(final int b) throws IOException {
		this.out.write(b);
		this.counter = this.counter.add(BigInteger.ONE);
	}

	@Override
	public void write(final byte[] b, final int off, final int len) throws IOException {
		this.out.write(b, off, len);
		this.counter = this.counter.add(BigInteger.valueOf(len));
	}

	@Override
	public void flush() throws IOException {
		super.flush();
		this.out.flush();
	}

	@Override
	public void close() throws IOException {
		super.close();
		this.out.close();
	}

	public BigInteger getActualCounter() {
		return this.counter;
	}
}

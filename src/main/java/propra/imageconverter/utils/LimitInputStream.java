package propra.imageconverter.utils;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Objects;

/**
 * Inputstream, der einen anderen als Basis nimmt, aber die zu lesenden Bytes
 * auf ein Maximum begrenzt.
 * 
 * @author marvin
 *
 */
public class LimitInputStream extends InputStream {

	private final InputStream in;
	private final BigInteger maxLength;

	private BigInteger counter;

	public LimitInputStream(InputStream in, BigInteger maxLength) {
		this.in = in;
		Objects.requireNonNull(in, "in");
		this.maxLength = maxLength;
		this.counter = BigInteger.ZERO;
	}

	public LimitInputStream(InputStream in, long maxLength) {
		this(in, BigInteger.valueOf(maxLength));
	}

	@Override
	public int read() throws IOException {
		if (this.counter.compareTo(this.maxLength) >= 0) {
			return -1;
		}

		final int readByte = this.in.read();
		this.counter = this.counter.add(BigInteger.ONE);

		return readByte;
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		final int read = this.in.read(b, off, len);

		this.counter = this.counter.add(BigInteger.valueOf(read));
		return read;
	}

	@Override
	public int available() throws IOException {

		final int available = this.maxLength.subtract(this.counter).min(BigInteger.valueOf(this.in.available()))
				.min(BigInteger.valueOf(Integer.MAX_VALUE)).intValueExact();

		return available;
	}

	@Override
	public synchronized void mark(int readlimit) {
	}

	@Override
	public synchronized void reset() throws IOException {
		throw new IOException("mark/reset not supported");
	}

	@Override
	public boolean markSupported() {
		return false;
	}

}

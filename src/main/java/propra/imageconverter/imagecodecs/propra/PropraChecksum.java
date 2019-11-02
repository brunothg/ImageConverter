package propra.imageconverter.imagecodecs.propra;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Hilfsklasse zum Berechnen und Überprüfen der Checksumme
 *
 * @author marvin
 *
 */
public class PropraChecksum {

	private static final int X = 65513;

	long a = 0;
	long b = 1;
	long prevB = 0;

	long i = 0;

	public void pushByte(final int data) {
		this.a = this.a + ((this.i + 1) + data);
		this.prevB = this.b;
		this.b = (this.prevB + this.a) % X;

		this.i++;
	}

	public long getActualChecksum() {
		final long aEnd = this.a % X;
		final long bEnd = this.b;
		final long p = (aEnd * ((long) Math.pow(2, 16))) + bEnd;
		return p;
	}

	public static class PropraChecksumInputStream extends InputStream {

		private final InputStream in;
		private final PropraChecksum checksum = new PropraChecksum();

		public PropraChecksumInputStream(final InputStream in) {
			super();
			this.in = in;
		}

		@Override
		public int read() throws IOException {
			final int data = this.in.read();
			if (data < 0) {
				return data;
			}

			this.checksum.pushByte(data);
			return data;
		}

		public long getActualChecksum() {
			return this.checksum.getActualChecksum();
		}

		/**
		 * Schließt ebenfalls den übergebenen InputStream
		 */
		@Override
		public void close() throws IOException {
			super.close();
			this.in.close();
		}

		/**
		 * Liest alle bytes ohne etwas damit zu tun.
		 *
		 * @throws IOException
		 */
		public void consumeAllBytes() throws IOException {
			while (this.read() >= 0) {
				continue;
			}
		}

	}

	public static class PropraChecksumOutputStream extends OutputStream {

		private final OutputStream out;
		private final PropraChecksum checksum = new PropraChecksum();

		public PropraChecksumOutputStream(final OutputStream out) {
			super();
			this.out = out;
		}

		@Override
		public void write(int data) throws IOException {
			data = data & 0xFF;

			this.out.write(data);
			this.checksum.pushByte(data);
		}

		public long getActualChecksum() {
			return this.checksum.getActualChecksum();
		}

		/**
		 * Schließt ebenfalls den übergebenen OutputStream
		 */
		@Override
		public void close() throws IOException {
			super.close();
			this.out.close();
		}

	}

	/**
	 * Berechnet die Checksumme (siehe Aufgabenstellung) für ein Byte-Array
	 *
	 *
	 * @deprecated besser direkt die Streamvariante nutzen z.B.
	 *             {@link PropraChecksumInputStream} oder
	 *             {@link PropraChecksumOutputStream}
	 * @param data Die zu grunde liegenden Daten
	 * @return Die Checksumme
	 */
	@Deprecated
	public static long calculateChecksum(final byte[] data) {
		final PropraChecksumInputStream in = new PropraChecksumInputStream(new ByteArrayInputStream(data));

		try {
			in.consumeAllBytes();
			in.close();
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}

		return in.getActualChecksum();
	}

}

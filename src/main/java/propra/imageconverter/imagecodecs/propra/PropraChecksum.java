package propra.imageconverter.imagecodecs.propra;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Hilfsklasse zum Berechnen und Überprüfen der Checksumme (teils inspiriert
 * durch Review der ersten Abgabe)
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

	public void pushByte(int data) {
		a = a + ((i + 1) + data);
		prevB = b;
		b = (prevB + a) % X;

		i++;
	}

	public long getActualChecksum() {
		long aEnd = a % X;
		long bEnd = b;
		final long p = (aEnd * ((long) Math.pow(2, 16))) + bEnd;
		return p;
	}

	public static class PropraChecksumInputStream extends InputStream {

		private InputStream in;
		private PropraChecksum checksum = new PropraChecksum();

		public PropraChecksumInputStream(InputStream in) {
			super();
			this.in = in;
		}

		@Override
		public int read() throws IOException {
			int data = in.read();
			if (data < 0) {
				return data;
			}

			checksum.pushByte(data);
			return data;
		}

		public long getActualChecksum() {
			return checksum.getActualChecksum();
		}

		/**
		 * Schließt ebenfalls den übergebenen InputStream
		 */
		@Override
		public void close() throws IOException {
			super.close();
			in.close();
		}

		/**
		 * Liest alle bytes ohne etwas damit zu tun.
		 * 
		 * @throws IOException
		 */
		public void consumeAllBytes() throws IOException {
			while (read() >= 0) {
				continue;
			}
		}

	}

	public static class PropraChecksumOutputStream extends OutputStream {

		private OutputStream out;
		private PropraChecksum checksum = new PropraChecksum();

		public PropraChecksumOutputStream(OutputStream out) {
			super();
			this.out = out;
		}

		@Override
		public void write(int data) throws IOException {
			data = data & 0xFF;
			
			out.write(data);
			checksum.pushByte(data);
		}

		public long getActualChecksum() {
			return checksum.getActualChecksum();
		}

		/**
		 * Schließt ebenfalls den übergebenen OutputStream
		 */
		@Override
		public void close() throws IOException {
			super.close();
			out.close();
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
	public static long calculateChecksum(byte[] data) {
		PropraChecksumInputStream in = new PropraChecksumInputStream(new ByteArrayInputStream(data));

		try {
			in.consumeAllBytes();
			in.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return in.getActualChecksum();
	}

}

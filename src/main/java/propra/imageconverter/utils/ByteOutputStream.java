package propra.imageconverter.utils;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Objects;

/**
 * Hilfsklasse zum Schreiben von byte basierten Daten. Die angegebene ByteOrder
 * wird nicht auf die geerbten Methoden des {@link OutputStream} angewendet.
 *
 * @author marvin
 *
 */
public class ByteOutputStream extends OutputStream implements Closeable {

	private final OutputStream out;
	private ByteOrder byteOrder;

	/**
	 * Erstellt einen {@link ByteOutputStream} mit {@link ByteOrder#nativeOrder()}
	 *
	 * @param out {@link OutputStream} aus dem gelesen wird
	 */
	public ByteOutputStream(OutputStream out) {
		super();
		this.out = Objects.requireNonNull(out, "out");
		this.setByteOrder(ByteOrder.nativeOrder());
	}

	/**
	 * Erstellt einen {@link ByteOutputStream} mit entsprechender initialen
	 * {@link ByteOrder}
	 *
	 * @param out       {@link OutputStream} aus dem gelesen wird
	 * @param byteOrder Initiale {@link ByteOrder}
	 */
	public ByteOutputStream(OutputStream out, ByteOrder byteOrder) {
		this(out);
		this.setByteOrder(byteOrder);
	}

	/**
	 * Schreibt einen String mit aktueller Byte-Order.
	 *
	 * @param string  Der String zum Schreiben
	 * @param charset Das verwendete Charset
	 * @throws IOException
	 */
	public void writeOrderedString(String string, Charset charset) throws IOException {
		this.writeOrderedBytes(string.getBytes(charset));
	}

	/**
	 * Schreibt eine vorzeichenlose 16Bit (2byte) Zahl. Dies entspricht einenm
	 * unsigend short in Java.
	 *
	 * @param unsignedShort Vorzeichenlose 16Bit (2byte) Zahl
	 * @throws IOException
	 */
	public void writeOrderedUnsinedShort(int unsignedShort) throws IOException {
		final byte[] unsignedShortBytes = { (byte) (unsignedShort >> 8), (byte) unsignedShort }; // Nur die unteren 2
		this.writeOrderedBytes(unsignedShortBytes);
	}

	/**
	 * Schreibt eine vorzeichenlose 32Bit (4byte) Zahl. Dies entspricht einenm
	 * unsigend int in Java.
	 *
	 * @param unsignedInt Vorzeichenlose 32Bit (4byte) Zahl
	 * @throws IOException
	 */
	public void writeOrderedUnsinedInt(long unsignedInt) throws IOException {
		final byte[] unsignedIntBytes = { (byte) (unsignedInt >> 24), (byte) (unsignedInt >> 16),
				(byte) (unsignedInt >> 8), (byte) unsignedInt }; // Nur die unteren 4
		this.writeOrderedBytes(unsignedIntBytes);
	}

	/**
	 * Schreibt ein vorzeichenloses byte (8bit). Alias für {@link #write(int)}.
	 *
	 * @throws IOException
	 */
	public void writeUnsignedByte(int unsignedByte) throws IOException {
		this.out.write(unsignedByte);
	}
//
//	/**
//	 * Liest eine vorzeichenlose Zahl
//	 *
//	 * @param length Anzahl der Bytes
//	 * @return Die vorzeichenlose Zahl
//	 * @throws IOException
//	 */
//	public BigInteger readOrderedUnsinedNumber(int length) throws IOException {
//		final byte[] bytes = this.readOrderedBytes(length);
//		if (bytes.length != length) {
//			throw new IOException("Nicht genügend bytes vorhanden");
//		}
//
//		return new BigInteger(1, bytes);
//	}

	/**
	 * Schreibt das angegebene Byte-Array
	 *
	 * @param bytes Die Bytes zum Schreiben
	 * @throws IOException
	 */
	public void writeOrderedBytes(byte[] bytes) throws IOException {
		final byte[] bytesToWrite = Arrays.copyOf(bytes, bytes.length);

		if (this.getByteOrder() == ByteOrder.LITTLE_ENDIAN) {
			reverse(bytesToWrite);
		}

		this.out.write(bytesToWrite);
	}

	/**
	 * Dreht ein byte array um.
	 *
	 * @param bytes Byte-Array, das umgedreht werden soll
	 */
	protected static void reverse(byte[] bytes) {
		final int length = bytes.length;

		for (int i = 0; i < (length / 2); i++) {
			final byte temp = bytes[i];
			bytes[i] = bytes[length - i - 1];
			bytes[length - i - 1] = temp;
		}
	}

	/**
	 * @return the byteOrder
	 */
	public ByteOrder getByteOrder() {
		return this.byteOrder;
	}

	/**
	 * @param byteOrder the byteOrder to set
	 */
	public void setByteOrder(ByteOrder byteOrder) {
		this.byteOrder = byteOrder;
	}

	@Override
	public void write(int unsignedByte) throws IOException {
		this.out.write(unsignedByte);
	}

	@Override
	public void flush() throws IOException {
		this.out.flush();
	}

	@Override
	public void close() throws IOException {
		this.out.close();
	}

}

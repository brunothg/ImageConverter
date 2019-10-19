package propra.imageconverter.utils;

import java.io.Closeable;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Objects;

/**
 * Hilfsklasse zum Lesen von byte basierten Daten. Die angegebene ByteOrder wird
 * nicht auf die geerbten Methoden des {@link InputStream} angewendet.
 *
 * @author marvin
 *
 */
public class ByteInputStream extends FilterInputStream implements Closeable {

	private ByteOrder byteOrder;

	/**
	 * Erstellt einen {@link ByteInputStream} mit {@link ByteOrder#nativeOrder()}
	 *
	 * @param in {@link InputStream} aus dem gelesen wird
	 */
	public ByteInputStream(InputStream in) {
		super(in);
		Objects.requireNonNull(in, "in");
		this.setByteOrder(ByteOrder.nativeOrder());
	}

	/**
	 * Erstellt einen {@link ByteInputStream} mit entsprechender initialen
	 * {@link ByteOrder}
	 *
	 * @param in        {@link InputStream} aus dem gelesen wird
	 * @param byteOrder Initiale {@link ByteOrder}
	 */
	public ByteInputStream(InputStream in, ByteOrder byteOrder) {
		this(in);
		this.setByteOrder(byteOrder);
	}

	/**
	 * Liest einen String mit aktueller Byte-Order.
	 *
	 * @param length  Maximale Anzahl an Bytes zum lesen
	 * @param charset Das verwendete Charset
	 * @return Den gelesenen String
	 * @throws IOException
	 */
	public String readOrderedString(int length, Charset charset) throws IOException {
		final byte[] bytes = this.readOrderedBytes(length);
		if (bytes.length != length) {
			throw new IOException("Nicht genügend bytes vorhanden");
		}

		return new String(bytes, charset);
	}

	/**
	 * Liest eine vorzeichenlose 16Bit (2byte) Zahl. Dies entspricht einenm unsigend
	 * short in Java.
	 *
	 * @return Vorzeichenlose 16Bit (2byte) Zahl
	 * @throws IOException
	 */
	public int readOrderedUnsignedShort() throws IOException {
		final byte[] bytes = this.readOrderedBytes(2);
		if (bytes.length != 2) {
			throw new IOException("Nicht genügend bytes vorhanden");
		}

		return ByteBuffer.wrap(bytes).getShort() & 0xffff;
	}

	/**
	 * Liest eine vorzeichenlose 32Bit (4byte) Zahl. Dies entspricht einenm unsigend
	 * int in Java.
	 *
	 * @return Vorzeichenlose 32Bit (4byte) Zahl
	 * @throws IOException
	 */
	public long readOrderedUnsignedInt() throws IOException {
		final byte[] bytes = this.readOrderedBytes(4);
		if (bytes.length != 4) {
			throw new IOException("Nicht genügend bytes vorhanden");
		}

		return ByteBuffer.wrap(bytes).getInt() & 0xffffffffL;
	}

	/**
	 * Liest ein vorzeichenloses byte (8bit). Alias für {@link #read()}.
	 *
	 * @return Vorzeichenloses byte
	 * @throws IOException
	 */
	public int readUnsignedByte() throws IOException {
		final int read = this.in.read();
		if (read < 0) {
			throw new IOException("Nicht genügend bytes vorhanden");
		}

		return read;
	}

	/**
	 * Liest eine vorzeichenlose Zahl
	 *
	 * @param length Anzahl der Bytes
	 * @return Die vorzeichenlose Zahl
	 * @throws IOException
	 */
	public BigInteger readOrderedUnsignedNumber(int length) throws IOException {
		final byte[] bytes = this.readOrderedBytes(length);
		if (bytes.length != length) {
			throw new IOException("Nicht genügend bytes vorhanden");
		}

		return new BigInteger(1, bytes);
	}

	/**
	 * Liest die angegebene Anzahl Bytes (oder weniger, wenn {@link InputStream}
	 * keine bytes mehr hat).
	 *
	 * @param length Maximale Anzahl an Bytes zum lesen
	 * @return Byte-Array mit den gelesenen Bytes in der aktuellen Byte.Reihenfolge
	 * @throws IOException
	 */
	public byte[] readOrderedBytes(int length) throws IOException {

		final byte[] buffer = new byte[length];
		final int read = this.in.read(buffer);
		if (read < 0) {
			throw new IOException("Nicht genügend bytes vorhanden");
		}

		final byte[] bytes = Arrays.copyOf(buffer, read);
		if (this.getByteOrder() == ByteOrder.LITTLE_ENDIAN) {
			reverse(bytes);
		}

		return bytes;
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

}

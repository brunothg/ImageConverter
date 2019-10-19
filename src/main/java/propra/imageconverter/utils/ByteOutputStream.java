package propra.imageconverter.utils;

import java.io.Closeable;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
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
public class ByteOutputStream extends FilterOutputStream implements Closeable {

	private ByteOrder byteOrder;

	/**
	 * Erstellt einen {@link ByteOutputStream} mit {@link ByteOrder#nativeOrder()}
	 *
	 * @param out {@link OutputStream} aus dem gelesen wird
	 */
	public ByteOutputStream(OutputStream out) {
		super(out);
		Objects.requireNonNull(out, "out");
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
	 * @param length  Anzahl der Bytes, die geschrieben werden
	 * @throws IOException
	 */
	public void writeOrderedString(String string, Charset charset, int length) throws IOException {
		this.writeOrderedBytes(Arrays.copyOf(string.getBytes(charset), length));
	}

	/**
	 * Schreibt eine vorzeichenlose 16Bit (2byte) Zahl. Dies entspricht einenm
	 * unsigend short in Java.
	 *
	 * @param unsignedShort Vorzeichenlose 16Bit (2byte) Zahl
	 * @throws IOException
	 */
	public void writeOrderedUnsignedShort(int unsignedShort) throws IOException {
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
	public void writeOrderedUnsignedInt(long unsignedInt) throws IOException {
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

	/**
	 * Schreibt eine vorzeichenlose Zahl
	 *
	 * @param number Vorzeichenlose Zahl
	 * @param length Anzahl der Bytes oder null, wenn egal
	 * @return Anzahl Bytes, die geschrieben wurden
	 * @throws IOException
	 */
	public int writeOrderedUnsignedNumber(BigInteger number, Integer length) throws IOException {
		byte[] unsignedNumberBytes = number.toByteArray();
		number.clearBit(0);
		final int bytesWithoutSignBits = (int) Math.ceil(number.bitLength() / 8.0);
		unsignedNumberBytes = Arrays.copyOfRange(unsignedNumberBytes, unsignedNumberBytes.length - bytesWithoutSignBits,
				unsignedNumberBytes.length); // Entferne Vorzeichen-Byte(s)

		// Byte-Anzahl prüfen und ggf. Anpassen (vergrößern)
		if (length != null) {
			if (unsignedNumberBytes.length < length) {
				reverse(unsignedNumberBytes);
				unsignedNumberBytes = Arrays.copyOf(unsignedNumberBytes, length);
				reverse(unsignedNumberBytes);
			}

			if (unsignedNumberBytes.length != length) {
				throw new IOException("Number " + number.toString() + " can not be stored with " + length + "bytes");
			}
		}

		this.writeOrderedBytes(unsignedNumberBytes);
		return unsignedNumberBytes.length;
	}

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

}

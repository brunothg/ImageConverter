package propra.imageconverter.utils.streams.bytes;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * OutputStream, der in einen anderen Outputstream einzelne Bits schreiben kann.
 * Nur vollständige Bytes werden sofort geschrieben. Deswegen ist es wichtig den
 * Stream zu schließen bzw. das schreiben der Bits zu erzwingen.
 *
 * @author marvin
 *
 */
public class BitOutputStream extends OutputStream {

	private final OutputStream out;

	private final List<Short> bitBuffer = new ArrayList<>(8);

	public BitOutputStream(final OutputStream out) {
		this.out = Objects.requireNonNull(out, "out");
	}

	/**
	 * Schreibt ein bit (0 oder 1)
	 *
	 * @param bit
	 * @throws IOException
	 */
	public void writeBit(final short bit) throws IOException {
		final short bitValue = (short) (bit & 1);
		this.bitBuffer.add(bitValue);

		while (this.bitBuffer.size() >= 8) {
			int byteValue = 0;
			for (int i = 0; i < 8; i++) {
				final short outBit = this.bitBuffer.remove(0);
				byteValue = byteValue << 1;
				byteValue = byteValue | (outBit & 1);
			}

			this.out.write(byteValue);
		}
	}

	@Override
	public void write(final int byteValue) throws IOException {
		// Byte in bits zerlegen
		for (int i = 7; i >= 0; i--) {
			final short bit = (short) ((byteValue >>> (i)) & 1);
			this.writeBit(bit);
		}
	}

	/**
	 * Erzwingt das schreiben ggf. vorgehaltener bits (weil kein vollständiges Byte
	 * vorhanden ist). Fehlende Bits werden mit 0 aufgefüllt
	 *
	 * @throws IOException
	 */
	public void flushBitBuffer() throws IOException {
		while (!this.bitBuffer.isEmpty()) {
			int byteValue = 0;
			for (int i = 0; i < 8; i++) {
				final short outBit = (!this.bitBuffer.isEmpty()) ? this.bitBuffer.remove(0) : 0;
				byteValue = byteValue << 1;
				byteValue = byteValue | (outBit & 1);
			}

			this.out.write(byteValue);
		}
	}

	@Override
	public void flush() throws IOException {
		this.out.flush();
	}

	@Override
	public void close() throws IOException {
		this.flushBitBuffer();
		this.out.close();
	}
}

package propra.imageconverter.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

/**
 * InputStream, der aus einem anderen Inputstream einzelne Bits lesen kann.
 *
 * @author marvin
 *
 */
public class BitInputStream extends InputStream {

	private final InputStream in;

	private final Queue<Short> bitBuffer = new LinkedList<>();
	private boolean eof = false;

	public BitInputStream(final InputStream in) {
		this.in = Objects.requireNonNull(in, "in");
	}

	/**
	 * Liest ein Bit (0 oder 1)
	 *
	 * @return das gelesene Bit oder -1, wenn der Stream zu Ende ist
	 * @throws IOException
	 */
	public short readBit() throws IOException {
		// Restbits vorhanden
		if (!this.bitBuffer.isEmpty()) {
			return this.bitBuffer.poll();
		}

		// EOF bereits erreicht oder versuche neues byte zu lesen
		final int byteValue;
		if (this.eof) {
			return -1;
		} else {
			byteValue = this.in.read();
			if (byteValue == -1) {
				this.eof = true;
				return -1;
			}
		}

		// Byte in bits zerlegen
		for (int i = 7; i >= 0; i--) {
			final short bit = (short) ((byteValue >>> (i)) & 1);
			this.bitBuffer.add(bit);
		}

		// Bit zurückgeben
		return this.bitBuffer.poll();
	}

	/**
	 * Liest ein ganzes byte. Wenn kein ganzes byte mehr vorhanden ist, werden die
	 * fehlenden Bits (die unteren) mit 0 gefüllt
	 *
	 * @return ein ganzes byte bzw. -1, wenn der Stream zu Ende ist
	 */
	@Override
	public int read() throws IOException {
		int byteValue = 0;

		for (int i = 0; i < 8; i++) {
			short bit = this.readBit();
			if (bit <= -1) {
				if (i <= 0) {
					byteValue = -1;
					break;
				}
				bit = 0;
			}

			byteValue = byteValue << 1;
			byteValue = byteValue | (bit & 1);
		}

		return byteValue;
	}

}

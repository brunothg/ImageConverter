package propra.imageconverter.basen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import propra.imageconverter.utils.MathUtils;

/**
 * InputStream zum Lesen eines Base-N- kodierten Inputstreams
 *
 * @author marvin
 *
 */
public class BaseNInputStream extends InputStream {

	private final char[] alphabet;
	private final long bitCountPerChar;
	private final BufferedReader in;

	/**
	 * Das Alphabet wir automatisch ermittelt (erste Zeile des Streams)
	 *
	 * @param in
	 */
	public BaseNInputStream(final InputStream in) {
		this(in, null);
	}

	public BaseNInputStream(final InputStream in, final char[] alphabet) {
		this(in, alphabet, StandardCharsets.UTF_8);
	}

	public BaseNInputStream(final InputStream in, final char[] alphabet, final Charset charset) {
		this.in = new BufferedReader(new InputStreamReader(in, charset), 1024);

		if (alphabet != null) {
			this.alphabet = alphabet;
		} else {
			try {
				this.alphabet = this.in.readLine().toCharArray();
			} catch (final IOException e) {
				throw new RuntimeException("Alphabet konnte nicht ermittelt werden", e);
			}
		}

		this.bitCountPerChar = MathUtils.log2(this.alphabet.length);
	}

	@Override
	public int read() throws IOException {
		// TODO BaseN-read
		final char c = (char) this.in.read();

		return -1;
	}

	@Override
	public void close() throws IOException {
		this.in.close();
	}
}

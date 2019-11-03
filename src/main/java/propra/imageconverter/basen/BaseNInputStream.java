package propra.imageconverter.basen;

import java.io.InputStream;

/**
 * InputStream zum Lesen eines Base-N- kodierten Inputstreams
 *
 * @author marvin
 *
 */
public class BaseNInputStream {

	/**
	 * Das Alphabet wir automatisch ermittelt (erste Zeile des Streams)
	 *
	 * @param in
	 */
	public BaseNInputStream(final InputStream in) {
		this(in, null);
	}

	public BaseNInputStream(final InputStream in, final char[] alphabet) {
	}
}

package propra.imageconverter.basen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Queue;

import propra.imageconverter.utils.ArrayUtils;
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

    private final Queue<Byte> bytes = new LinkedList<>();
    private final int bitBuffer = 0;
    private final int bitBufferSize = 0;

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
	if (alphabet.length > 256) {
	    throw new RuntimeException("Alphabet ist zu lang. Max 256 Zeichen: " + alphabet.length);
	}
	if (ArrayUtils.hasDuplicates(alphabet)) {
	    throw new RuntimeException("Alphabet enthält doppelte Zeichen: " + new String(alphabet));
	}

	this.bitCountPerChar = MathUtils.log2(this.alphabet.length);
    }

    @Override
    public int read() throws IOException {
	// TODO BaseN-read

	// Versuche mindestens ein byte zu lesen
	while (this.bytes.isEmpty()) {
	    final char c = (char) this.in.read();
	    final int index = getAlphabetIndex(c);

	    break;
	}

	// Versuche byte zurückzugeben
	if (this.bytes.isEmpty()) {
	    return -1;
	} else {
	    return this.bytes.poll();
	}
    }

    /**
     * Gibt den Index des Zeichens im Alphabet
     * 
     * @param c
     * @return Den Index oder -1, wenn nicht gefunden
     */
    private int getAlphabetIndex(char c) {
	for (int i = 0; i < this.alphabet.length; i++) {
	    if (this.alphabet[i] == c) {
		return i;
	    }
	}
	return -1;
    }

    @Override
    public void close() throws IOException {
	this.in.close();
    }
}

package propra.imageconverter.basen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

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
    private final int bitCountPerChar;
    private final BufferedReader in;

    private int bitBuffer = 0;
    private int bitBufferSize = 0;
    private boolean eof = false;

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

	this.bitCountPerChar = (int) MathUtils.log2(this.alphabet.length);
    }

    @Override
    public int read() throws IOException {
	// TODO BaseN-read
	if (this.eof) {
	    return -1;
	}

	// Falls noch ein volles byte vorhanden ist
	if (this.bitBufferSize >= 8) {
	    final int returnValue = this.bitBuffer & 0xFF;

	    this.bitBufferSize = 0;
	    this.bitBuffer = 0;

	    return returnValue;
	}

	// Sonst lese bis ein volles byte da ist
	Integer returnValue = null;
	while (returnValue == null) {
	    final int read = this.in.read();
	    if (read == -1) {
		this.eof = true;
		return -1;
	    }

	    final char c = (char) read;
	    final int cValue = getAlphabetIndex(c);

	    for (int i = this.bitCountPerChar - 1; i >= 0; i--) {
		final boolean charBit = MathUtils.getBit(cValue, i);

		this.bitBuffer = MathUtils.setBit(this.bitBuffer, 7 - this.bitBufferSize, charBit);
		this.bitBufferSize++;

		if (this.bitBufferSize >= 8) {
		    returnValue = (this.bitBuffer & 0xFF);

		    this.bitBufferSize = 0;
		    this.bitBuffer = 0;
		}
	    }
	}

	System.out.println(returnValue + " - " + ((char) (int) returnValue));
	return returnValue;
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

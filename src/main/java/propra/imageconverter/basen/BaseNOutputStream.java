package propra.imageconverter.basen;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import propra.imageconverter.utils.ArrayUtils;
import propra.imageconverter.utils.MathUtils;

/**
 * OutputStream zum Lesen eines Base-N- kodierten Outputstreams
 *
 * @author marvin
 *
 */
public class BaseNOutputStream extends OutputStream {

    private final Writer out;
    private final char[] alphabet;
    private final int bitCountPerChar;

    private int bitBuffer = 0;
    private int bitBufferSize = 0;
    private boolean eof = false;

    public BaseNOutputStream(final OutputStream out, final char[] alphabet, final boolean writeAlphabet) {
	this(out, alphabet, writeAlphabet, StandardCharsets.UTF_8);
    }

    public BaseNOutputStream(final OutputStream out, final char[] alphabet, final boolean writeAlphabet,
	    final Charset charset) {
	if (alphabet.length > 256) {
	    throw new RuntimeException("Alphabet ist zu lang. Max 256 Zeichen: " + alphabet.length);
	}
	if (ArrayUtils.hasDuplicates(alphabet)) {
	    throw new RuntimeException("Alphabet enthält doppelte Zeichen: " + new String(alphabet));
	}

	this.alphabet = alphabet;
	this.out = new OutputStreamWriter(out, charset);

	this.bitCountPerChar = (int) MathUtils.log2(this.alphabet.length);

	if (writeAlphabet) {
	    try {
		this.out.write(new String(alphabet) + "\n");
	    } catch (final IOException e) {
		throw new RuntimeException("Alphabet konnte nicht geschrieben werden", e);
	    }
	}
    }

    @Override
    public void write(int b) throws IOException {
	if (this.eof) {
	    throw new IOException("Stream already closed");
	}
	b = b & 0xFF;
	// TODO BaseN-write

	for (int i = 7; i >= 0; i--) {
	    final boolean charBit = MathUtils.getBit(b, i);

	    this.bitBuffer = MathUtils.setBit(this.bitBuffer, (this.bitCountPerChar - 1) - this.bitBufferSize, charBit);
	    this.bitBufferSize++;

	    if (this.bitBufferSize >= this.bitCountPerChar) {
		final int cValue = this.bitBuffer;
		final char c = this.alphabet[cValue];

		this.bitBufferSize = 0;
		this.bitBuffer = 0;

		this.out.write(c);
	    }
	}
    }

    /**
     * Beendet den Datenstrom. Schreibt ggf. verbleibende Restbytes.
     * 
     * @throws IOException
     */
    public void eof() throws IOException {
	if (this.bitBufferSize > 0) {
	    final int cValue = this.bitBuffer;
	    final char c = this.alphabet[cValue];

	    this.out.write(c);
	}
	this.eof = true;
    }

    /**
     * Schließt diesen Stream und den übergebenen. Schreibt ggf. Restbytes.
     */
    public void close() throws IOException {
	eof();
	this.out.close();
    }

}

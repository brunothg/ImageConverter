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
    private final long bitCountPerChar;

    private final int bitBuffer = 0;
    private final int bitBufferSize = 0;

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

	this.bitCountPerChar = MathUtils.log2(this.alphabet.length);

	if (writeAlphabet) {
	    try {
		this.out.write(new String(alphabet) + "\n");
	    } catch (final IOException e) {
		throw new RuntimeException("Alphabet konnte nicht geschrieben werden", e);
	    }
	}
    }

    @Override
    public void write(final int b) throws IOException {
	// TODO BaseN-write

    }

    @Override
    public void close() throws IOException {
	this.out.close();
    }

}

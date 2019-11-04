package propra.imageconverter.basen;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

public class BaseNOutputStreamTest {

    private static final char[] ALPHABET_BASE64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
	    .toCharArray();
    private static final char[] ALPHABET_BASE32 = "0123456789ABCDEFGHIJKLMNOPQRSTUV".toCharArray();
    private static final char[] ALPHABET_BASE16 = "0123456789ABCDEF".toCharArray();
    private static final char[] ALPHABET_BASE2 = "01".toCharArray();

    @Test
    public void testBase32() throws Exception {
	assertEquals("CO", encode("f", ALPHABET_BASE32));
	assertEquals("CPNG", encode("fo", ALPHABET_BASE32));
	assertEquals("CCPNMU", encode("foo", ALPHABET_BASE32));
	assertEquals("CPNMUOG", encode("foob", ALPHABET_BASE32));
	assertEquals("CPNMUOJ1", encode("fooba", ALPHABET_BASE32));
	assertEquals("CPNMUOJ1E8", encode("foobar", ALPHABET_BASE32));
    }

    @Test
    public void testBase2() throws Exception {
	assertEquals("01100110", encode("f", ALPHABET_BASE2));
	assertEquals("0110011001101111", encode("fo", ALPHABET_BASE2));
	assertEquals("011001100110111101101111", encode("foo", ALPHABET_BASE2));
	assertEquals("01100110011011110110111101100010", encode("foob", ALPHABET_BASE2));
	assertEquals("0110011001101111011011110110001001100001", encode("fooba", ALPHABET_BASE2));
	assertEquals("011001100110111101101111011000100110000101110010", encode("foobar", ALPHABET_BASE2));
    }

    @Test
    public void testBase16() throws Exception {
	assertEquals("66", encode("f", ALPHABET_BASE16));
	assertEquals("666F", encode("fo", ALPHABET_BASE16));
	assertEquals("666F6F", encode("foo", ALPHABET_BASE16));
	assertEquals("666F6F62", encode("foob", ALPHABET_BASE16));
	assertEquals("666F6F6261", encode("fooba", ALPHABET_BASE16));
	assertEquals("666F6F626172", encode("foobar", ALPHABET_BASE16));
    }

    @Test
    public void testBase64() throws Exception {
	assertEquals("Zg", encode("f", ALPHABET_BASE64));
	assertEquals("Zm8", encode("fo", ALPHABET_BASE64));
	assertEquals("Zm9v", encode("foo", ALPHABET_BASE64));
	assertEquals("Zm9vYg", encode("foob", ALPHABET_BASE64));
	assertEquals("Zm9vYmE", encode("fooba", ALPHABET_BASE64));
	assertEquals("Zm9vYmFy", encode("foobar", ALPHABET_BASE64));
    }

    private static String encode(final String string, final char[] alphabet) throws IOException {
	final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	final BaseNOutputStream baseNOutputStream = new BaseNOutputStream(byteArrayOutputStream, alphabet, false);
	baseNOutputStream.write(string.getBytes(StandardCharsets.UTF_8));
	baseNOutputStream.close();

	final String encodedString = new String(byteArrayOutputStream.toByteArray(), StandardCharsets.UTF_8);
	return encodedString;
    }
}

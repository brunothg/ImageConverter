package propra.imageconverter.basen;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

public class BaseNInputStreamTest {

    private static final char[] ALPHABET_BASE64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
	    .toCharArray();
    private static final char[] ALPHABET_BASE32 = "0123456789ABCDEFGHIJKLMNOPQRSTUV".toCharArray();
    private static final char[] ALPHABET_BASE16 = "0123456789ABCDEF".toCharArray();
    private static final char[] ALPHABET_BASE2 = "01".toCharArray();

    @Test
    public void testBase32() throws Exception {
	assertEquals("f", decode("CO", ALPHABET_BASE32));
	assertEquals("fo", decode("CPNG", ALPHABET_BASE32));
	assertEquals("foo", decode("CPNMU", ALPHABET_BASE32));
	assertEquals("foob", decode("CPNMUOG", ALPHABET_BASE32));
	assertEquals("fooba", decode("CPNMUOJ1", ALPHABET_BASE32));
	assertEquals("foobar", decode("CPNMUOJ1E8", ALPHABET_BASE32));
    }

    @Test
    public void testBase2() throws Exception {
	assertEquals("f", decode("01100110", ALPHABET_BASE2));
	assertEquals("fo", decode("0110011001101111", ALPHABET_BASE2));
	assertEquals("foo", decode("011001100110111101101111", ALPHABET_BASE2));
	assertEquals("foob", decode("01100110011011110110111101100010", ALPHABET_BASE2));
	assertEquals("fooba", decode("0110011001101111011011110110001001100001", ALPHABET_BASE2));
	assertEquals("foobar", decode("011001100110111101101111011000100110000101110010", ALPHABET_BASE2));
    }

    @Test
    public void testBase16() throws Exception {
	assertEquals("f", decode("66", ALPHABET_BASE16));
	assertEquals("fo", decode("666F", ALPHABET_BASE16));
	assertEquals("foo", decode("666F6F", ALPHABET_BASE16));
	assertEquals("foob", decode("666F6F62", ALPHABET_BASE16));
	assertEquals("fooba", decode("666F6F6261", ALPHABET_BASE16));
	assertEquals("foobar", decode("666F6F626172", ALPHABET_BASE16));
    }

    @Test
    public void testBase64() throws Exception {
	assertEquals("f", decode("Zg", ALPHABET_BASE64));
	assertEquals("fo", decode("Zm8", ALPHABET_BASE64));
	assertEquals("foo", decode("Zm9v", ALPHABET_BASE64));
	assertEquals("foob", decode("Zm9vYg", ALPHABET_BASE64));
	assertEquals("fooba", decode("Zm9vYmE", ALPHABET_BASE64));
	assertEquals("foobar", decode("Zm9vYmFy", ALPHABET_BASE64));
    }

    private static String decode(final String encodedString, final char[] alphabet) throws IOException {
	final BaseNInputStream baseNInputStream = new BaseNInputStream(
		new ByteArrayInputStream(encodedString.getBytes(StandardCharsets.UTF_8)), alphabet);
	final String string = new String(baseNInputStream.readAllBytes(), StandardCharsets.UTF_8);
	baseNInputStream.close();

	return string;
    }
}

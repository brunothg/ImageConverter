package propra.imageconverter;

import static org.junit.Assert.*;

import org.junit.Test;

public class CliParametersTest {

	private static final String[] ARGUMENTS = new String[] { "--input=../KE1_TestBilder/test_01_uncompressed.tga",
			"--output=../KE1_Konvertiert/test_02.propra", "--encode-base-32", "--decode-base-32", "--encode-base-n=0987abc4321xyz56" };

	@Test
	public void testGetInputParameter() throws Exception {
		final CliParameters parameters = new CliParameters();
		parameters.parse(CliParametersTest.ARGUMENTS);

		assertEquals("../KE1_TestBilder/test_01_uncompressed.tga", parameters.getInputFileString());
		assertEquals("tga", parameters.getInputFileExtension());
		assertNotNull(parameters.getInputFile());
	}

	@Test
	public void testGetOutputParameter() throws Exception {
		final CliParameters parameters = new CliParameters();
		parameters.parse(CliParametersTest.ARGUMENTS);

		assertEquals("../KE1_Konvertiert/test_02.propra", parameters.getOutputFileString());
		assertEquals("propra", parameters.getOutputFileExtension());
		assertNotNull(parameters.getOutputFile());
	}
	
	@Test
	public void testBase32() throws Exception {
		final CliParameters parameters = new CliParameters();
		parameters.parse(CliParametersTest.ARGUMENTS);

		assertEquals(true, parameters.isBase32Encode());
		assertEquals(true, parameters.isBase32Decode());
	}
	
	@Test
	public void testBaseN() throws Exception {
		final CliParameters parameters = new CliParameters();
		parameters.parse(CliParametersTest.ARGUMENTS);

		assertEquals(true, parameters.isBaseNEncode());
		assertArrayEquals(new char[] {'0','9','8','7','a','b','c','4','3','2','1','x','y','z','5','6'}, parameters.getBaseNEncodeAlphabet());
	}
}

package propra.imageconverter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class CliParametersTest {

	private static final String[] ARGUMENTS = new String[] { "--input=../KE1_TestBilder/test_01_uncompressed.tga",
			"--output=../KE1_Konvertiert/test_02.propra" };

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
}

package propra.imageconverter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import propra.imageconverter.basen.BaseNInputStream;
import propra.imageconverter.basen.BaseNOutputStream;
import propra.imageconverter.imagecodecs.ConversionException;
import propra.imageconverter.imagecodecs.ImageCodec;
import propra.imageconverter.imagecodecs.InternalImage;
import propra.imageconverter.imagecodecs.internal.JpgCodec;
import propra.imageconverter.imagecodecs.internal.PngCodec;
import propra.imageconverter.imagecodecs.propra.PropraCodec;
import propra.imageconverter.imagecodecs.tga.TgaCodec;
import propra.imageconverter.utils.ReturnCodeWatcher;

/**
 * Kommandozeilenprogramm zum Konvertieren von Bildern.
 *
 *
 * @author marvin
 *
 */
public class ImageConverter {

	private static final ImageCodec[] IMAGE_CODECS = new ImageCodec[] { new TgaCodec(), new PropraCodec(),
			new PngCodec(), new JpgCodec() };

	private static final char[] BASE32_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUV".toCharArray();
	private static final String BASE32_EXTENSION = ".base-32";
	private static final String BASEN_EXTENSION = ".base-n";

	/**
	 * Programm-Einsteigspunkt
	 *
	 * @param args Kommandozeilen-Argumente
	 */
	public static void main(final String[] args) {

		// Exceptions überwachen und Programm starten
		ReturnCodeWatcher.watch(() -> {
			ImageConverter.cmd(args);
			return null;
		});

	}

	/**
	 * Abarbeitung der Kommandozeilen-Argumente
	 *
	 * @param args Kommandozeilen-Argumente
	 * @throws Exception Wenn ein Fehler auftritt
	 */
	private static void cmd(final String[] args) throws Exception {
		final CliParameters parameters = new CliParameters();
		parameters.parse(args);

		if (parameters.isBase32Encode()) {
			encodeBaseN(parameters, BASE32_ALPHABET, BASE32_EXTENSION);
		} else if (parameters.isBase32Decode()) {
			decodeBaseN(parameters, BASE32_ALPHABET, BASE32_EXTENSION);
		} else if (parameters.isBaseNDecode()) {
			decodeBaseN(parameters, null, BASEN_EXTENSION);
		} else if (parameters.isBaseNEncode()) {
			encodeBaseN(parameters, parameters.getBaseNEncodeAlphabet(), BASEN_EXTENSION);
		} else /* Bildkonvertierung */ {
			convertImage(parameters);
		}

	}

	private static void decodeBaseN(final CliParameters parameters, final char[] alphabet, final String extension)
			throws IOException {
		final Path inputFile = parameters.getInputFile();
		if ((inputFile == null)) {
			throw new ParameterException("Input-File wurde nicht angegeben");
		}
		final String inputFileName = inputFile.getFileName().toString();
		if (!inputFileName.endsWith(extension)) {
			throw new ParameterException("Input-File ist nicht vom richtigen typ");
		}

		final Path outputFile = inputFile.getParent()
				.resolve(inputFileName.substring(0, inputFileName.length() - extension.length()));

		final BaseNInputStream baseNInputStream = new BaseNInputStream(
				new BufferedInputStream(Files.newInputStream(inputFile), 1024), alphabet);
		final BufferedOutputStream out = new BufferedOutputStream(Files.newOutputStream(outputFile), 1024);

		int read;
		while ((read = baseNInputStream.read()) >= 0) {
			out.write(read);
		}

		try {
			baseNInputStream.close();
			out.close();
		} catch (final IOException e) {
		}
	}

	private static void encodeBaseN(final CliParameters parameters, final char[] alphabet, final String extension)
			throws IOException {
		final Path inputFile = parameters.getInputFile();
		if (inputFile == null) {
			throw new ParameterException("Input-File wurde nicht angegeben");
		}
		final String inputFileName = inputFile.getFileName().toString();
		final Path outputFile = inputFile.getParent().resolve(inputFileName + "" + extension);

		final BufferedInputStream in = new BufferedInputStream(Files.newInputStream(inputFile), 1024);
		final BaseNOutputStream baseNOutputStream = new BaseNOutputStream(
				new BufferedOutputStream(Files.newOutputStream(outputFile), 1024), alphabet);

		int read;
		while ((read = in.read()) >= 0) {
			baseNOutputStream.write(read);
		}

		try {
			in.close();
			baseNOutputStream.close();
		} catch (final IOException e) {
		}
	}

	private static void convertImage(final CliParameters parameters) throws ConversionException, IOException {
		final ImageCodec inputCodec = getCodec(parameters.getInputFileExtension());
		final ImageCodec outputCodec = getCodec(parameters.getOutputFileExtension());
		setOutputProperties(parameters, outputCodec);

		final InternalImage image = inputCodec.readImage(Files.newInputStream(parameters.getInputFile()));
		outputCodec.writeImage(image, Files.newOutputStream(parameters.getOutputFile()));
		image.close();
	}

	/**
	 * Setzt Eigenschaften des Output-Codecs
	 *
	 * @param parameters Die {@link CliParameters} aus denen die Eigenschaften
	 *                   gewonnen werden
	 * @param codec      Der Codec, dessen Eigenschaften gesetzt werden soll
	 */
	private static void setOutputProperties(final CliParameters parameters, final ImageCodec codec) {
		final String compression = parameters.getCompression();
		if (compression != null) {
			codec.setCodecProperty(ImageCodec.PROPERTY_COMPRESSION, compression);
		}
	}

	/**
	 * Gibt den passenden {@link ImageCodec} zu einer Dateierweiterung.
	 *
	 * @param fileExtension Die Dateierweiterung zu der ein Codec gesucht wird
	 * @return Der {@link ImageCodec} zur Dateierweiterung
	 * @throws ConversionException Wenn kein passender Codec vorhanden ist
	 */
	private static ImageCodec getCodec(final String fileExtension) throws ConversionException {

		for (final ImageCodec imageCodec : IMAGE_CODECS) {
			if (imageCodec.getFileExtension().equalsIgnoreCase(fileExtension)) {
				return imageCodec;
			}
		}

		throw new ConversionException("Kein passenden Codec für '" + fileExtension + "' gefunden.");
	}

}

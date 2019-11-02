package propra.imageconverter;

import java.nio.file.Files;

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

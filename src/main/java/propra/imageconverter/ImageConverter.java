package propra.imageconverter;

import propra.imageconverter.codecs.ImageCodec;
import propra.imageconverter.utils.ReturnCodeWatcher;

/**
 * Kommandozeilenprogramm zum Konvertieren von Bildern.
 *
 *
 * @author marvin
 *
 */
public class ImageConverter {

	private static final ImageCodec[] CODECS = new ImageCodec[] {};

	/**
	 * Programm-Einsteigspunkt
	 *
	 * @param args Kommandozeilen-Argumente
	 */
	public static void main(String[] args) {

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
	private static void cmd(String[] args) throws Exception {
		final CliParameters parameters = new CliParameters();
		parameters.parse(args);

	}

}

package propra.imageconverter.imagecodecs;

import propra.imageconverter.utils.ReturnCodeWatcher.ReturnCode;

/**
 * Allgemeine Exception-Klasse für Fehler in der Konvertierung.
 *
 * @author marvin
 *
 */
@ReturnCode(ReturnCode.IMAGE_CONVERTION_ERROR)
public class ConversionException extends Exception {
	private static final long serialVersionUID = 1L;

	public ConversionException() {
		super();
	}

	public ConversionException(final String message) {
		super(message);
	}

	public ConversionException(final Throwable cause) {
		super(cause);
	}

	public ConversionException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public ConversionException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}

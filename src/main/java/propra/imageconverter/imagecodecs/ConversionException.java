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

	public ConversionException(String message) {
		super(message);
	}

	public ConversionException(Throwable cause) {
		super(cause);
	}

	public ConversionException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConversionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}

package propra.imageconverter;

/**
 * Exception für fehlerhafte Parameter
 *
 * @author marvin
 *
 */
public class ParameterException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ParameterException() {
		super();
	}

	public ParameterException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ParameterException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public ParameterException(final String message) {
		super(message);
	}

	public ParameterException(final Throwable cause) {
		super(cause);
	}

}

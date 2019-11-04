package propra.imageconverter.utils;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.concurrent.Callable;

/**
 * Führt Code aus und überwacht die Exceptions. Beendet das Programm ggf. mit
 * passendem Return-Code.
 *
 * @author marvin
 *
 */
public class ReturnCodeWatcher {

    /**
     * Nur statische Funktionen. Instanzen sollen nicht erstellt werden.
     */
    private ReturnCodeWatcher() {
    }

    /**
     * Führt einen Code aus und beendet das Programm, wenn eine Exception empfangen
     * wird. Der Return-Code wird versucht aus einer {@link ReturnCode} Annotation
     * zu lesen. Ansonsten wird {@link ReturnCode#DEFAULT_ERROR} benutzt.
     *
     * @param function Auszuführender Code
     */
    public static void watch(final Callable<Void> function) {
	try {
	    function.call();
	} catch (final Throwable t) {

	    // Fehlernachricht ausgeben
	    System.err.println(t.getClass().getSimpleName() + ": " + t.getMessage());
	    t.printStackTrace();

	    // Programm mit Fehlercode beenden
	    final ReturnCode returnCode = t.getClass().getAnnotation(ReturnCode.class);
	    if (returnCode != null) {
		System.exit(returnCode.value());
	    } else {
		System.exit(ReturnCode.DEFAULT_ERROR);
	    }
	}
    }

    /**
     * Annotation zum Kennzeichnen des Fehlercodes einer Exception
     *
     * @author marvin
     *
     */
    @Retention(RUNTIME)
    @Target(TYPE)
    public static @interface ReturnCode {
	public static final int DEFAULT_ERROR = 1;
	public static final int IMAGE_CONVERTION_ERROR = 123;

	int value() default ReturnCode.DEFAULT_ERROR;
    }

}

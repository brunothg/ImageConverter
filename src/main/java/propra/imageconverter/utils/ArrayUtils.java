package propra.imageconverter.utils;

/**
 * Hilfsfunktionen für Arrays
 * 
 * @author marvin
 *
 */
public class ArrayUtils {

    public static boolean hasDuplicates(char[] arr) {

	for (int i = 0; i < arr.length; i++) {
	    final int c = arr[i];

	    for (int j = i + 1; j < arr.length; j++) {
		if (arr[j] == c) {
		    return true;
		}
	    }
	}

	return false;
    }
}

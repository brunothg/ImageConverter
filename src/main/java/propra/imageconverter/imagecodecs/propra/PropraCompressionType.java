package propra.imageconverter.imagecodecs.propra;

import java.lang.reflect.InvocationTargetException;

import propra.imageconverter.imagecodecs.propra.compression.PropraCompression;
import propra.imageconverter.imagecodecs.propra.compression.PropraNoCompression;
import propra.imageconverter.imagecodecs.propra.compression.PropraRleCompression;
import propra.imageconverter.imagecodecs.propra.compression.PropraHuffmanCompression;

/**
 * Kompressionstyp eines ProPra Bildes
 *
 * @author marvin
 *
 */
public enum PropraCompressionType {
    None(0, "uncompressed", PropraNoCompression.class), Rle(1, "rle", PropraRleCompression.class),
    Huffman(2, "huffman", PropraHuffmanCompression.class), Auto(2, "auto", PropraHuffmanCompression.class);

    /**
     * Id des Kompressionstyps nach der Spezifikation
     */
    private int id;

    /**
     * Id/Name des Bildtyps für die Konsole
     */
    private final String cliName;

    private Class<? extends PropraCompression> compressionClass;

    private PropraCompressionType(final int id, final String cliName,
	    final Class<? extends PropraCompression> compressionClass) {
	this.id = id;
	this.cliName = cliName;
	this.compressionClass = compressionClass;
    }

    public int getId() {
	return this.id;
    }

    public String getCliName() {
	return this.cliName;
    }

    public Class<? extends PropraCompression> getCompressionClass() {
	return this.compressionClass;
    }

    public PropraCompression createCompressionInstance() {
	try {
	    return this.getCompressionClass().getDeclaredConstructor().newInstance();
	} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
		| NoSuchMethodException | SecurityException e) {
	    throw new RuntimeException(e);
	}
    }

    /**
     * Gibt den Kompressionstyp zu einer Id
     *
     * @param id Kompressions Id
     * @return {@link PropraCompressionType} oder null, wenn nicht vorhanden
     */
    public static PropraCompressionType fromId(final int id) {
	for (final PropraCompressionType type : PropraCompressionType.values()) {
	    if (type.getId() == id) {
		return type;
	    }
	}

	return null;
    }

    /**
     * Gibt den Kompressionstyp zu einer Cli-Id
     *
     * @param id Kompressions Cli-Id
     * @return {@link PropraCompressionType} oder null, wenn nicht vorhanden
     */
    public static PropraCompressionType fromCliId(final String id) {
	for (final PropraCompressionType type : PropraCompressionType.values()) {
	    if (type.getCliName().equalsIgnoreCase(id)) {
		return type;
	    }
	}

	return null;
    }
}

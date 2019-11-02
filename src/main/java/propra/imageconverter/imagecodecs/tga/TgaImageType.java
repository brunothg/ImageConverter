package propra.imageconverter.imagecodecs.tga;

import java.lang.reflect.InvocationTargetException;

import propra.imageconverter.imagecodecs.tga.compression.TgaCompression;
import propra.imageconverter.imagecodecs.tga.compression.TgaRgbCompression;
import propra.imageconverter.imagecodecs.tga.compression.TgaRleCompression;

/**
 * Tga-Bildtyp
 *
 * @author marvin
 *
 */
public enum TgaImageType {

	/**
	 * RGB (24 oder 32 Bit) unkomprimiert
	 */
	Rgb(2, "uncompressed", TgaRgbCompression.class),

	/**
	 * RGB (24 Bit) lauflängenkodiert
	 */
	Rle(10, "rle", TgaRleCompression.class);

	/**
	 * Id des Bildtyp nach der Spezifikation
	 */
	private int id;

	/**
	 * Id/Name des Bildtyps für die Konsole
	 */
	private final String cliName;

	private Class<? extends TgaCompression> compressionClass;

	private TgaImageType(final int id, final String cliName, final Class<? extends TgaCompression> compressionClass) {
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

	public Class<? extends TgaCompression> getCompressionClass() {
		return this.compressionClass;
	}

	public TgaCompression createCompressionInstance() {
		try {
			return this.getCompressionClass().getDeclaredConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Gibt den Bildtyp zu einer Id
	 *
	 * @param id Bildtyp Id
	 * @return {@link TgaImageType} oder null, wenn nicht vorhanden
	 */
	public static TgaImageType fromId(final int id) {
		for (final TgaImageType type : TgaImageType.values()) {
			if (type.getId() == id) {
				return type;
			}
		}

		return null;
	}

	/**
	 * Gibt den Bildtyp zu einem CLI-Namen
	 *
	 * @param id Bildtyp CLI-Id
	 * @return {@link TgaImageType} oder null, wenn nicht vorhanden
	 */
	public static TgaImageType fromCliId(final String id) {
		for (final TgaImageType type : TgaImageType.values()) {
			if (type.getCliName().equalsIgnoreCase(id)) {
				return type;
			}
		}

		return null;
	}
}

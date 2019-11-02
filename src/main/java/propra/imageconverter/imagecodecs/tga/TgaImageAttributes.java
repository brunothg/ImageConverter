package propra.imageconverter.imagecodecs.tga;

/**
 * Repräsentation des Bild-Attribute-Bytes
 *
 * @author marvin
 *
 */
public class TgaImageAttributes {

	private int attributeBitCount = 0;
	private HorizontalOrigin horizontalOrigin = HorizontalOrigin.Left;
	private VerticalOrigin verticalOrigin = VerticalOrigin.Bottom;

	/**
	 * @return the attributeBitCount
	 */
	public int getAttributeBitCount() {
		return this.attributeBitCount;
	}

	/**
	 * Es werden nur die unteren 4 bit beachtet
	 * 
	 * @param attributeBitCount the attributeBitCount to set
	 */
	public void setAttributeBitCount(int attributeBitCount) {
		this.attributeBitCount = attributeBitCount & 0B00001111;
	}

	/**
	 * @return the horizontalOrigin
	 */
	public HorizontalOrigin getHorizontalOrigin() {
		return this.horizontalOrigin;
	}

	/**
	 * @param horizontalOrigin the horizontalOrigin to set
	 */
	public void setHorizontalOrigin(HorizontalOrigin horizontalOrigin) {
		this.horizontalOrigin = horizontalOrigin;
	}

	/**
	 * @return the verticalOrigin
	 */
	public VerticalOrigin getVerticalOrigin() {
		return this.verticalOrigin;
	}

	/**
	 * @param verticalOrigin the verticalOrigin to set
	 */
	public void setVerticalOrigin(VerticalOrigin verticalOrigin) {
		this.verticalOrigin = verticalOrigin;
	}

	@Override
	public String toString() {
		return "ImageAttributes [attributeBitCount=" + this.attributeBitCount + ", horizontalOrigin="
				+ this.horizontalOrigin + ", verticalOrigin=" + this.verticalOrigin + "]";
	}

	public static TgaImageAttributes fromByte(int imageAttributeByte) {
		imageAttributeByte = imageAttributeByte & 0xff;

		final int attributeBits = imageAttributeByte & 0B00001111;
		final boolean horizontalOriginBit = ((imageAttributeByte & 0B00010000) >> 4) == 1;
		final boolean verticalOriginBit = ((imageAttributeByte & 0B00100000) >> 5) == 1;

		final TgaImageAttributes attributes = new TgaImageAttributes();
		attributes.setAttributeBitCount(attributeBits);
		attributes.setHorizontalOrigin((horizontalOriginBit) ? HorizontalOrigin.Right : HorizontalOrigin.Left);
		attributes.setVerticalOrigin((verticalOriginBit) ? VerticalOrigin.Top : VerticalOrigin.Bottom);

		return attributes;
	}

	public static int toByte(TgaImageAttributes attributes) {
		int attributeBits = 0;

		attributeBits = attributeBits | attributes.getAttributeBitCount();
		attributeBits = attributeBits | (((attributes.getHorizontalOrigin() == HorizontalOrigin.Right) ? 1 : 0) << 4);
		attributeBits = attributeBits | (((attributes.getVerticalOrigin() == VerticalOrigin.Top) ? 1 : 0) << 5);

		return attributeBits;
	}

	public static enum HorizontalOrigin {
		Left, Right;
	}

	public static enum VerticalOrigin {
		Top, Bottom;
	}
}

package propra.imageconverter.imagecodecs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Objects;

public class InternalMemoryImage implements InternalImage {
	/**
	 * Interne Pixeldatendarstellung
	 */
	private final BufferedImage pixelData;
	private final Dimension size;

	public InternalMemoryImage(final Dimension size) {
		this.size = Objects.requireNonNull(size, "size");
		this.pixelData = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);
	}

	public InternalMemoryImage(final BufferedImage pixelData) {
		this.size = new Dimension(pixelData.getWidth(), pixelData.getHeight());
		this.pixelData = pixelData;
	}

	@Override
	public BufferedImage getPixelData() {
		return this.pixelData;
	}

	@Override
	public void setPixel(final Point p, final Color c) {
		this.pixelData.setRGB(p.x, p.y, c.getRGB());
	}

	@Override
	public Color getPixel(final Point p) {
		return new Color(this.pixelData.getRGB(p.x, p.y));
	}

	@Override
	public Dimension getSize() {
		return this.size;
	}

	@Override
	public void close() {
	}

}

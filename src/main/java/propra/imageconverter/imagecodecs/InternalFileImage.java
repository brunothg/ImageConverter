package propra.imageconverter.imagecodecs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class InternalFileImage implements InternalImage {

	private final Dimension size;
	private final Path tempFile;
	private final RandomAccessFile raFile;

	public InternalFileImage(final Dimension size) throws IOException {
		this.size = Objects.requireNonNull(size, "size");

		this.tempFile = Files.createTempFile("propra", "internal");
		try {
			this.raFile = new RandomAccessFile(this.tempFile.toFile(), "rw");
		} catch (final IOException e) {
			Files.deleteIfExists(this.tempFile);
			throw e;
		}
		try {
			this.raFile.setLength(size.width * size.height * 3);
		} catch (final IOException e) {
			this.raFile.close();
			Files.deleteIfExists(this.tempFile);
			throw e;
		}

	}

	@Override
	public BufferedImage getPixelData() {
		final Dimension size = this.getSize();
		final BufferedImage bufferedImage = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);

		for (int y = 0; y < size.height; y++) {
			for (int x = 0; x < size.width; x++) {
				bufferedImage.setRGB(x, y, this.getPixel(new Point(x, y)).getRGB());
			}
		}

		return bufferedImage;
	}

	@Override
	public void setPixel(final Point p, final Color c) {
		if (!this.isValidPoint(p)) {
			throw new RuntimeException("Illegale Koordinate: " + p);
		}

		try {
			this.gotoPosition(p);
			this.raFile.write(new byte[] { (byte) c.getRed(), (byte) c.getGreen(), (byte) c.getBlue() });
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setPixels(final Point p, final Color... cs) {
		if (!this.isValidPoint(p)) {
			throw new RuntimeException("Illegale Koordinate: " + p);
		}

		try {
			this.gotoPosition(p);

			final byte[] bytes = new byte[cs.length * 3];
			for (int i = 0; i < cs.length; i++) {
				bytes[(i * 3) + 0] = (byte) cs[i].getRed();
				bytes[(i * 3) + 1] = (byte) cs[i].getGreen();
				bytes[(i * 3) + 2] = (byte) cs[i].getBlue();
			}

			this.raFile.write(bytes);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Color getPixel(final Point p) {
		if (!this.isValidPoint(p)) {
			throw new RuntimeException("Illegale Koordinate: " + p);
		}

		try {
			this.gotoPosition(p);

			final byte[] colorBytes = new byte[3];
			this.raFile.readFully(colorBytes);

			final int r = Byte.toUnsignedInt(colorBytes[0]);
			final int g = Byte.toUnsignedInt(colorBytes[1]);
			final int b = Byte.toUnsignedInt(colorBytes[2]);

			return new Color(r, g, b);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Color[] getPixels(final Point p, final int count) {
		if (!this.isValidPoint(p)) {
			throw new RuntimeException("Illegale Koordinate: " + p);
		}

		try {
			this.gotoPosition(p);
			final Color[] colors = new Color[count];

			final byte[] colorBytes = new byte[count * 3];
			this.raFile.readFully(colorBytes);

			for (int i = 0; i < colors.length; i++) {
				final int r = Byte.toUnsignedInt(colorBytes[(i * 3) + 0]);
				final int g = Byte.toUnsignedInt(colorBytes[(i * 3) + 1]);
				final int b = Byte.toUnsignedInt(colorBytes[(i * 3) + 2]);
				colors[i] = new Color(r, g, b);
			}

			return colors;
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}

	}

	private void gotoPosition(final Point p) throws IOException {
		final long pos = ((p.y * p.x) + p.x)/* Index */ * 3;
		this.raFile.seek(pos);
	}

	@Override
	public Dimension getSize() {
		return this.size;
	}

	private boolean isValidPoint(final Point p) {
		return ((p.x >= 0) && (p.x < this.getSize().width)) && ((p.y >= 0) && (p.y < this.getSize().height));
	}

	@Override
	public void close() {
		try {
			this.raFile.close();
		} catch (final IOException e) {
		}
		try {
			Files.deleteIfExists(this.tempFile);
		} catch (final IOException e) {
		}
	}

}

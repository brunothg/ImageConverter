package propra.imageconverter.imagecodecs;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

/**
 * Dateigepufferetes {@link InternalImage}. Der Puffer arbeitet nur effizient,
 * wenn von oben links nach unten rechts gelesen/geschrieben und nicht häufig
 * zwischen lesen/schreiben gewechselt wird.
 *
 * Es wird ein temporäres File über einen {@link FileChannel} genutzt. Damit
 * beschränkt sich die maximale Dateigröße auf den verfügbaren
 * Festplattenspeicher.
 *
 * @author marvin
 *
 */
public class InternalFileImage implements InternalImage {

	private final Dimension size;
	private final Path tempFile;
	private final FileChannel raFile;

	private final ByteBuffer writeBuffer;
	private Point writeBufferStart = null;

	private final ByteBuffer readBuffer;
	private Point readBufferStart = null;

	public InternalFileImage(final Dimension size) throws IOException {
		this.size = Objects.requireNonNull(size, "size");

		this.tempFile = Files.createTempFile("propra", "internal");
		try {
			this.raFile = FileChannel.open(this.tempFile, StandardOpenOption.CREATE, StandardOpenOption.READ,
					StandardOpenOption.WRITE, StandardOpenOption.DELETE_ON_CLOSE);
		} catch (final IOException e) {
			Files.deleteIfExists(this.tempFile);
			throw e;
		}
		try {
			long fileSize = size.width * size.height * 3;
			this.raFile.position(0);
			while (fileSize > 0) {
				final int byteSize = (int) Math.min(1024, fileSize);
				this.raFile.write(ByteBuffer.allocate(byteSize));
				fileSize -= byteSize;
			}

		} catch (final IOException e) {
			this.raFile.close();
			Files.deleteIfExists(this.tempFile);
			throw e;
		}

		this.writeBuffer = ByteBuffer.allocateDirect(1024 * 3);
		this.readBuffer = ByteBuffer.allocateDirect(1024 * 3);
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

		// Ist Koordinate neu oder ein direkter Nachfolger des zuletzt in den Puffer
		// geschriebenen Pixels
		final boolean validNextBufferPos = (this.writeBufferStart == null)
				|| ((this.pointToIndex(this.writeBufferStart) + this.writeBuffer.position()) == this.pointToIndex(p));

		// Kein gültiger Pixel für den Puffer oder Puffer zu klein
		if (!validNextBufferPos || ((this.writeBuffer.capacity() - this.writeBuffer.position()) < 3)) {
			this.flushPixel();
		}

		// Neuer Puffer Start
		if (this.writeBufferStart == null) {
			this.writeBuffer.clear();
			this.writeBufferStart = new Point(p.x, p.y);
		}

		// Schreibe Pixel in den Puffer
		this.writeBuffer.put(new byte[] { (byte) c.getRed(), (byte) c.getGreen(), (byte) c.getBlue() });
	}

	/**
	 * Schreibe gepufferte Pixel in die Datei
	 */
	private void flushPixel() {
		if (this.writeBuffer.position() <= 0) {
			return;
		}

		this.writeBuffer.limit(this.writeBuffer.position());
		try {
			final long index = this.pointToIndex(this.writeBufferStart);
			this.raFile.position(index);

			this.writeBuffer.position(0);
			this.raFile.write(this.writeBuffer);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
		this.writeBuffer.clear();
		this.writeBufferStart = null;
	}

	@Override
	public Color getPixel(final Point p) {
		if (!this.isValidPoint(p)) {
			throw new RuntimeException("Illegale Koordinate: " + p);
		}
		this.flushPixel();

		// Versuche Pixel aus dem Puffer zu lesen
		if (this.readBufferStart != null) {
			final long bufferStartIndex = this.pointToIndex(this.readBufferStart);
			final long pIndex = this.pointToIndex(p);
			final long bufferIndex = pIndex - bufferStartIndex;

			if ((bufferIndex >= 0) && (bufferIndex <= (this.readBuffer.position() - 3))) {
				final int r = Byte.toUnsignedInt(this.readBuffer.get((int) bufferIndex));
				final int g = Byte.toUnsignedInt(this.readBuffer.get((int) bufferIndex + 1));
				final int b = Byte.toUnsignedInt(this.readBuffer.get((int) bufferIndex + 2));

				return new Color(r, g, b);
			}
		}

		// Lese neue Pixel (inkl. gesuchtem) ein
		try {
			do {
				this.readBuffer.clear();
				this.readBufferStart = new Point(p.x, p.y);
				this.raFile.position(this.pointToIndex(p));
			} while (this.raFile.read(this.readBuffer) <= 0);

			final int r = Byte.toUnsignedInt(this.readBuffer.get(0));
			final int g = Byte.toUnsignedInt(this.readBuffer.get(1));
			final int b = Byte.toUnsignedInt(this.readBuffer.get(2));

			return new Color(r, g, b);
		} catch (final IOException e) {
			this.readBuffer.clear();
			this.readBufferStart = null;
			throw new RuntimeException(e);
		}
	}

	/**
	 * Übersetzt eine Pixel-Koordinate in einen Datei-Index (wie bei einem
	 * byte-Array)
	 *
	 * @param p
	 * @return
	 */
	private long pointToIndex(final Point p) {
		final long pos = ((p.y * this.getSize().width) + p.x)/* Index */ * 3;
		return pos;
	}

	@Override
	public Dimension getSize() {
		return this.size;
	}

	/**
	 * Testet, ob die Koordinate innerhalb der Bildgröße ( {@link #getSize()} )
	 * liegt.
	 *
	 * @param p
	 * @return
	 */
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

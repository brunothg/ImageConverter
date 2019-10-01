package propra.imageconverter.codecs.propra;

import java.io.InputStream;
import java.io.OutputStream;

import propra.imageconverter.codecs.ConversionException;
import propra.imageconverter.codecs.ImageCodec;
import propra.imageconverter.codecs.InternalImage;

public class PropraCodec implements ImageCodec {

	private static final String FILE_EXTENSION = "propra";

	@Override
	public InternalImage readImage(InputStream in) throws ConversionException {
		// TODO readImage
		return null;
	}

	@Override
	public void writeImage(InternalImage image, OutputStream out) throws ConversionException {
		// TODO writeImage

	}

	@Override
	public String getFileExtension() {
		return FILE_EXTENSION;
	}

}

package propra.imageconverter.gui;

import java.awt.BorderLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import propra.imageconverter.imagecodecs.ConversionException;
import propra.imageconverter.imagecodecs.ImageCodec;
import propra.imageconverter.imagecodecs.InternalImage;
import propra.imageconverter.imagecodecs.InternalMemoryImage;

public class ImageConverterFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	private final List<ImageCodec> supportedCodecs = new LinkedList<>();

	private JLabel lblImage;

	private BufferedImage pixelData;

	public ImageConverterFrame() {
		super();

		this.build();
	}

	private void build() {
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.setTitle("ImageConverter");
		this.setSize(800, 600);
		this.setLocationRelativeTo(null);
		this.getContentPane().setLayout(new BorderLayout());

		this.createMenu();
		this.createImageView();
	}

	private void createImageView() {
		final JScrollPane sp = new JScrollPane();
		this.getContentPane().add(sp, BorderLayout.CENTER);

		this.lblImage = new JLabel();
		sp.setViewportView(this.lblImage);

		new DropTarget(this.lblImage, new DropTargetAdapter() {
			@Override
			public void drop(DropTargetDropEvent dtde) {
				try {
					final Transferable tr = dtde.getTransferable();
					final DataFlavor[] flavors = tr.getTransferDataFlavors();
					final ArrayList<File> fileNames = new ArrayList<>();
					for (int i = 0; i < flavors.length; i++) {
						if (flavors[i].isFlavorJavaFileListType()) {
							dtde.acceptDrop(dtde.getDropAction());
							@SuppressWarnings("unchecked")
							final java.util.List<File> files = (java.util.List<File>) tr.getTransferData(flavors[i]);
							for (int k = 0; k < files.size(); k++) {
								fileNames.add(files.get(k));
							}

							if (fileNames.size() > 0) {
								ImageConverterFrame.this.openImage(fileNames.get(0).toPath());
							}

							dtde.dropComplete(true);
						}
					}
					return;
				} catch (final Throwable t) {
					t.printStackTrace();
				}
				dtde.rejectDrop();
			}
		});
	}

	private void createMenu() {
		final JMenuBar menuBar = new JMenuBar();
		this.setJMenuBar(menuBar);

		final JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		final JMenuItem mntmOpen = new JMenuItem("Open");
		mntmOpen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ImageConverterFrame.this.openFile();
			}
		});
		mnFile.add(mntmOpen);

		final JMenuItem mntmSave = new JMenuItem("Save");
		mntmSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ImageConverterFrame.this.saveFile();
			}
		});
		mnFile.add(mntmSave);

		final JSlider zoomSlider = new JSlider();
		zoomSlider.setMinimum(1);
		zoomSlider.setMaximum(100);
		zoomSlider.setValue(100);
		menuBar.add(zoomSlider);
		zoomSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				final int zoom = zoomSlider.getValue();
				final double zoomLevel = zoom / 100.0;

				ImageConverterFrame.this.setZoomLevel(zoomLevel);
			}
		});
	}

	private void setZoomLevel(double zoomLevel) {
		zoomLevel = Math.max(0.1, Math.min(zoomLevel, 100.0));

		BufferedImage after = new BufferedImage(this.pixelData.getWidth(), this.pixelData.getHeight(),
				BufferedImage.TYPE_INT_ARGB);
		final AffineTransform at = new AffineTransform();
		at.scale(zoomLevel, zoomLevel);
		final AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
		after = scaleOp.filter(this.pixelData, after);

		this.lblImage.setIcon(new ImageIcon(after));
	}

	protected void saveFile() {
		final JFileChooser jf = new JFileChooser();
		jf.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		jf.setFileFilter(new FileFilter() {
			@Override
			public String getDescription() {
				final List<ImageCodec> codecs = ImageConverterFrame.this.getSupportedCodecs();
				String desc = "Images (";
				for (final ImageCodec codec : codecs) {
					desc += "*." + codec.getFileExtension() + ", ";
				}
				desc = desc.substring(0, desc.length() - 2);
				desc += ")";
				return desc;
			}

			@Override
			public boolean accept(File f) {
				if (f.isDirectory()) {
					return true;
				}

				final List<ImageCodec> codecs = ImageConverterFrame.this.getSupportedCodecs();
				for (final ImageCodec codec : codecs) {
					if (f.getName().endsWith("." + codec.getFileExtension())) {
						return true;
					}
				}
				return false;
			}
		});
		final int result = jf.showSaveDialog(this);

		if (result == JFileChooser.APPROVE_OPTION) {
			this.saveImage(jf.getSelectedFile().toPath());
		}
	}

	private void saveImage(Path path) {
		if (this.pixelData == null) {
			return;
		}

		final List<ImageCodec> codecs = ImageConverterFrame.this.getSupportedCodecs();
		for (final ImageCodec codec : codecs) {
			if (path.getFileName().toString().endsWith("." + codec.getFileExtension())) {
				final InternalImage image = new InternalMemoryImage(this.pixelData);
				try {
					codec.writeImage(image, Files.newOutputStream(path));
				} catch (ConversionException | IOException e) {
					JOptionPane.showMessageDialog(this, e.getMessage(), "Could not save image",
							JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
				}
				return;
			}
		}

		JOptionPane.showMessageDialog(this, "No image codec found", "Could not save image", JOptionPane.ERROR_MESSAGE);
	}

	protected void openFile() {
		final JFileChooser jf = new JFileChooser();
		jf.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		jf.setFileFilter(new FileFilter() {
			@Override
			public String getDescription() {
				final List<ImageCodec> codecs = ImageConverterFrame.this.getSupportedCodecs();
				String desc = "Images (";
				for (final ImageCodec codec : codecs) {
					desc += "*." + codec.getFileExtension() + ", ";
				}
				desc = desc.substring(0, desc.length() - 2);
				desc += ")";
				return desc;
			}

			@Override
			public boolean accept(File f) {
				if (f.isDirectory()) {
					return true;
				}

				final List<ImageCodec> codecs = ImageConverterFrame.this.getSupportedCodecs();
				for (final ImageCodec codec : codecs) {
					if (f.getName().endsWith("." + codec.getFileExtension())) {
						return true;
					}
				}
				return false;
			}
		});
		final int result = jf.showOpenDialog(this);

		if (result == JFileChooser.APPROVE_OPTION) {
			this.openImage(jf.getSelectedFile().toPath());
		}
	}

	protected void openImage(Path path) {
		final List<ImageCodec> codecs = ImageConverterFrame.this.getSupportedCodecs();
		for (final ImageCodec codec : codecs) {
			if (path.getFileName().toString().endsWith("." + codec.getFileExtension())) {
				try {
					final InternalImage internalImage = codec.readImage(Files.newInputStream(path));
					this.pixelData = internalImage.getPixelData();

					this.lblImage.setIcon(new ImageIcon(this.pixelData));
				} catch (ConversionException | IOException e) {
					JOptionPane.showMessageDialog(this, e.getMessage(), "Could not open image",
							JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
				}
				return;
			}
		}

		JOptionPane.showMessageDialog(this, "No image codec found", "Could not open image", JOptionPane.ERROR_MESSAGE);
	}

	public List<ImageCodec> getSupportedCodecs() {
		return Collections.unmodifiableList(this.supportedCodecs);
	}

	public void setSupportedCodecs(List<ImageCodec> supportedCodecs) {
		this.supportedCodecs.clear();
		this.supportedCodecs.addAll(supportedCodecs);
	}
}

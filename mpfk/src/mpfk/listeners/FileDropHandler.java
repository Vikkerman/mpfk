package mpfk.listeners;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.TransferHandler;

import mpfk.createGUI;
/**
 * Drag and drop handler class
 * 
 * based on: ABika https://stackoverflow.com/a/39415436
 */
public final class FileDropHandler extends TransferHandler {
	private static final long serialVersionUID = 1L;

	@Override
	public boolean canImport(TransferHandler.TransferSupport support) {
		for (DataFlavor flavor : support.getDataFlavors()) {
			if (flavor.isFlavorJavaFileListType()) {
				return true;
			}
		}
		return false;
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean importData(TransferHandler.TransferSupport support) {
		if (!this.canImport(support))
			return false;

		List<File> files;
		try {
			files = (List<File>) support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
		} catch (UnsupportedFlavorException | IOException ex) {
			// should never happen (or JDK is buggy)
			return false;
		}

		createGUI.searchPanel.clearMovieList();
		List<File> filesDropped = new ArrayList<File>();
		for (File file : files) {
			if (createGUI.searchPanel.movieFile(file)) {
				filesDropped.add(file);
			} else {
				createGUI.searchPanel.addToMovieDirfileDir(file.getAbsolutePath());
			}
		}
		createGUI.searchPanel.setNewMovieList(filesDropped);
		return true;
	}
}

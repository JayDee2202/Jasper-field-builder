package jasperfieldbuilder.util;

import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.File;
import java.io.FileReader;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

public class GuiHelper {
	
	private GuiHelper() {}

	/**
	 * Activates drag'n'drop for the textarea.<br>
	 * The dragged file will be read and written into the textarea.<br>
	 * If multiple files are dropped the first file will be read.
	 * 
	 * @param textArea
	 */
	public static void enableDragAndDrop(final JTextArea textArea) {
		DropTarget target = new DropTarget(textArea, new DropTargetListener() {

			@Override
			public void dragEnter(DropTargetDragEvent e) {
				// Not needed
			}

			@Override
			public void dragExit(DropTargetEvent e) {
				// Not needed
			}

			@Override
			public void dragOver(DropTargetDragEvent e) {
				// Not needed
			}

			@Override
			public void dropActionChanged(DropTargetDragEvent e) {
				// Not needed
			}

			@Override
			public void drop(DropTargetDropEvent e) {
				try {
					FileReader in;

					e.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
					java.util.List<?> list = (java.util.List<?>) e.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
					File file = (File) list.get(0);
					in = new FileReader(file);
					textArea.read(in, null);

					in.close();
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					PJFLogger logger = (PJFLogger) PJFLogger.getLogger(PJFLogger.class.getName());
					logger.log(Level.SEVERE, "Drag'n'Drop-Error", ex);
				}
			}
		});
		target.setActive(true);
	}
}

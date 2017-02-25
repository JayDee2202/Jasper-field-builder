package jasperfieldbuilder;

import java.io.IOException;
import java.util.logging.Level;

import javax.swing.JOptionPane;

import jasperfieldbuilder.util.PJFLogger;

/**
 *	Main class 
 * 
 * @author JayDee
 */
public class MainGUI {
	
	private MainGUI() {}

		public static void main(String[] args) {
			ProcessJasperFile window = new ProcessJasperFile();
			try {
				window.createGUI();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, e.getMessage(), "Global error", JOptionPane.ERROR_MESSAGE);
				PJFLogger logger = (PJFLogger) PJFLogger.getLogger(PJFLogger.class.getName());
				logger.log(Level.SEVERE, "Global error", e);
			}
		}
}

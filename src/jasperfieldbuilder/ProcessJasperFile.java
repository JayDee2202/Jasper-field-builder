package jasperfieldbuilder;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import jasperfieldbuilder.util.Conventions;
import jasperfieldbuilder.util.CopyPaste;
import jasperfieldbuilder.util.FormattingUtils;
import jasperfieldbuilder.util.GuiHelper;
import jasperfieldbuilder.util.PJFLogger;
import jasperfieldbuilder.util.ProcessConstants;
import jasperfieldbuilder.util.Visibility;
import layout.TableLayout;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

/**
 * Builds out of a JRXML-file the constants for java sourcecode.
 * 
 * @author jaydee
 */
public class ProcessJasperFile extends JFrame {
	private static final long serialVersionUID = 3595239616870652107L;

	private JFileChooser importFile = new JFileChooser();
	private JLabel fileNameLabel = new JLabel(ProcessConstants.NO_FILE_CHOOSEN);
	private int importFileStatus = -1;
	private JCheckBox chooseFileCheckbox = new JCheckBox(ProcessConstants.USE_FILE);
	private JCheckBox copyPasteCheckbox = new JCheckBox(ProcessConstants.COPY_TO_CLIPBOARD);
	private JRadioButton conventionsJava = new JRadioButton("Java");
	private JRadioButton conventionsJavaStatic = new JRadioButton("Java (" + ProcessConstants.PREFIX_STATIC + ")");
	private JRadioButton visibilityPrivate = new JRadioButton(ProcessConstants.PREFIX_PRIVATE);
	private JRadioButton visibilityProtected = new JRadioButton(ProcessConstants.PREFIX_PROTECTED);
	private JTextArea textArea = new JTextArea(5, 10);


	public ProcessJasperFile() {
		super();
	}

	/**
	 * Erstellt die GUI
	 * @throws IOException 
	 */
	protected void createGUI() throws IOException {
		double[][] size = {
				{ TableLayout.FILL, 2, 100,100, 2 }, // X
				{ TableLayout.PREFERRED, TableLayout.FILL, 50, 25, 25, 5, 25, 25, 25, 25, 50 } // Y
			};
		JFrame mainframe = new JFrame();
		mainframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainframe.setLayout(new TableLayout(size));
		mainframe.setTitle(ProcessConstants.PROGRAM_TITLE);

		// Set icon
		try {
			BufferedImage frameIcon = ImageIO.read(ProcessJasperFile.class.getResource("/img/Settings_gears_64.png"));
			mainframe.setIconImage(frameIcon);
		} catch (IOException e) {
			PJFLogger logger = (PJFLogger) PJFLogger.getLogger(PJFLogger.class.getName());
			logger.log(Level.SEVERE, "The GUI-Icon couldn't be loaded.", e);
		}

		/*
		 * >> GUI <<
		 * Left
		 */
		mainframe.add(new JScrollPane(textArea), "0,0,0,9");
		JButton processButton = new JButton(ProcessConstants.PROCESS);
		mainframe.add(processButton, "0,10,3,10");
		GuiHelper.enableDragAndDrop(textArea);


		/*
		 * Right
		 */
		mainframe.add(new JLabel(ProcessConstants.LOAD_INSTRUCTIONS), "2,0,3,0");

		// Load file
		mainframe.add(fileNameLabel, "2,1,3,1");
		JButton openFileButton = new JButton(ProcessConstants.LOAD_FILE);
		mainframe.add(openFileButton, "2,2,3,2");

		// Checkboxes
		mainframe.add(chooseFileCheckbox, "2,3,3,3");
		copyPasteCheckbox.setSelected(true);
		mainframe.add(copyPasteCheckbox, "2,4,3,4");
		
		// Heading for checkboxes
		mainframe.add(new JLabel(ProcessConstants.ACCESS_UNDERLINED), "2,6,2,6");
		mainframe.add(new JLabel(ProcessConstants.CONVENTIONS_UNDERLINED), "3,6,3,6");

		// Radio buttons private/protected
		visibilityPrivate.setSelected(true);
		ButtonGroup visibitilyGroup = new ButtonGroup();
		visibitilyGroup.add(visibilityProtected);
		visibitilyGroup.add(visibilityPrivate);
		mainframe.add(visibilityPrivate, "2,7,2,7");
		mainframe.add(visibilityProtected, "2,8,2,8");
		
		// Radio buttons
		conventionsJavaStatic.setSelected(true);
		ButtonGroup conventionsGroup = new ButtonGroup();
		conventionsGroup.add(conventionsJavaStatic);
		conventionsGroup.add(conventionsJava);
		mainframe.add(conventionsJavaStatic, "3,7,3,7");
		mainframe.add(conventionsJava, "3,8,3,8");

		// process
		JRootPane rootPane = SwingUtilities.getRootPane(processButton);
		rootPane.setDefaultButton(processButton);


		// FileFilter: load only .jrxml
		importFile.setFileFilter(new FileFilter() {

			@Override
			public String getDescription() {
				return null;
			}

			@Override
			public boolean accept(File f) {
				return f.isDirectory() || f.getName().toLowerCase().endsWith(".jrxml");
			}
		});

		// Action: load file
		openFileButton.addActionListener(e -> {
			importFileStatus = importFile.showOpenDialog(null);

			if (importFileStatus == JFileChooser.APPROVE_OPTION) {
				fileNameLabel.setText(importFile.getSelectedFile().getName());
				chooseFileCheckbox.setSelected(true);
			}
		});

		// Action: process
		processButton.addActionListener(new ProcessListener());

		// visible
		mainframe.setSize(720, 480);
		mainframe.setLocationRelativeTo(null);
		mainframe.setVisible(true);
	}

	
	public class ProcessListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				Visibility visibility = visibilityPrivate.isSelected() ? Visibility.PRIVATE : Visibility.PUBLIC;
				Conventions convention = conventionsJava.isSelected() ? Conventions.NONSTATIC : Conventions.STATIC;
				CopyPaste copy = copyPasteCheckbox.isSelected() ? CopyPaste.COPY : CopyPaste.NONE;
				
				FormattingUtils formatter = new FormattingUtils(visibility, convention, copy);
				
				if (chooseFileCheckbox.isSelected()) {
					if (importFileStatus == JFileChooser.APPROVE_OPTION) {
						JasperDesign jasperDesign = JRXmlLoader.load(importFile.getSelectedFile());
						textArea.setText(formatter.processJasperDesign(jasperDesign.getParametersList(), jasperDesign.getFieldsList()));
					} else {
						JOptionPane.showMessageDialog(null, ProcessConstants.ERROR_NO_FILE, ProcessConstants.INFORMATION, JOptionPane.INFORMATION_MESSAGE);
					}
				} else {
					if (textArea.getText().length() > 1) {
						textArea.setText(formatter.processRawXml(textArea.getText()));
					} else {
						JOptionPane.showMessageDialog(null, ProcessConstants.ERROR_NO_TEXT, ProcessConstants.INFORMATION, JOptionPane.INFORMATION_MESSAGE);
					}
				}
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(null, e1.getMessage(), ProcessConstants.ERROR_PROCESSING, JOptionPane.ERROR_MESSAGE);
				PJFLogger logger = (PJFLogger) PJFLogger.getLogger(PJFLogger.class.getName());
				logger.log(Level.SEVERE, ProcessConstants.ERROR_PROCESSING, e1);
			}
		}

	}

}

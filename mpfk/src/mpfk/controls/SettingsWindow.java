package mpfk.controls;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.Dialog;

import javax.swing.Box;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import mpfk.createGUI;
import mpfk.ui.ColoredMenuBar;
import mpfk.util.LoadSettings;
/**
 * Tabbed JDialog for settings and so maybe later..
 * 
 * @author Vikker
 *
 */
public class SettingsWindow extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	private static JDialog settingsDialog;
	private static JTabbedPane tabbedPane = new JTabbedPane();
	private static JPanel settingsCard = new JPanel() {
		private static final long serialVersionUID = 1L;

		public Dimension getPreferredSize() {
			Dimension size = super.getPreferredSize();
			size.width += EXTRAWINDOWWIDTH;
			return size;
		}
	};
	private static int activeTabIndex = 0;
	
	private final static String MENUBARCOLORINACTIVE = "#63B2B6";
	private final static String MENUBARCOLORACTIVE = "#228388";
	private final static String PATHSETTINGSPANEL = "Structure";
	private final static int EXTRAWINDOWWIDTH = 100;

	private static String mainDir = new LoadSettings().getSettings("mainDir");
	private static String movieDir = new LoadSettings().getSettings("movieDir");

	private JButton mainButton, movieButton;
	private JTextField mainTextField, movieTextField;
	private JFileChooser chooser = null;
	private ColoredMenuBar menuBar;
	private static Point pointS = new Point();

	public SettingsWindow(int aIndex) {
		activeTabIndex = aIndex;
		setUI();
		settingsDialog = new JDialog(createGUI.frame, "Settings");
		settingsDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		createGUI.menuBar.setColor(MENUBARCOLORINACTIVE);

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		createSettingsPanel();

		tabbedPane.addTab(PATHSETTINGSPANEL, settingsCard);
		tabbedPane.setSelectedIndex(activeTabIndex);

		menuBar = new ColoredMenuBar();
		menuBar.setColor(MENUBARCOLORACTIVE);
		menuBar.add(Box.createHorizontalGlue());
		menuBar.addCloseXButton(settingsDialog);

		settingsDialog.setJMenuBar(menuBar);
		settingsDialog.getContentPane().add(tabbedPane, BorderLayout.CENTER);
		settingsDialog.setUndecorated(true);
		settingsDialog.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				pointS.x = e.getX();
				pointS.y = e.getY();
			}
		});
		settingsDialog.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				Point p = settingsDialog.getLocation();
				settingsDialog.setLocation(p.x + e.getX() - pointS.x, p.y + e.getY() - pointS.y);
			}
		});
		settingsDialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		settingsDialog.pack();
		settingsDialog.setLocationRelativeTo(null);
		settingsDialog.setResizable(false);
		settingsDialog.setVisible(true);
	}

	public void createSettingsPanel() {
		settingsCard.setBorder(new CompoundBorder(new TitledBorder("Settings"), new EmptyBorder(12, 0, 12, 0)));
		GroupLayout settingsLayout = new GroupLayout(settingsCard);
		settingsCard.setLayout(settingsLayout);
		settingsLayout.setAutoCreateGaps(true);
		settingsLayout.setAutoCreateContainerGaps(true);

		JLabel mainLabel = new JLabel("MPFK folder: ");
		JLabel tempLabel = new JLabel("Movie folder: ");

		mainButton = new JButton("Change");

		movieButton = new JButton("Change");
		movieButton.addActionListener(this);

		mainButton.addActionListener(this);
		mainButton.setFocusable(false);
		movieButton.setFocusable(false);

		mainTextField = new JTextField(mainDir.replace("//", "\\"), 40);
		mainTextField.setEditable(false);

		movieTextField = new JTextField(movieDir.replace("//", "\\"), 40);
		movieTextField.setEditable(false);

		settingsLayout.setHorizontalGroup(settingsLayout.createSequentialGroup()
				.addGroup(settingsLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(mainLabel)
						.addComponent(tempLabel))
				.addGroup(settingsLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(mainTextField)
						.addComponent(movieTextField))
				.addGroup(settingsLayout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(mainButton)
						.addComponent(movieButton)));
		settingsLayout.setVerticalGroup(settingsLayout.createSequentialGroup()
				.addGroup(settingsLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(mainLabel)
						.addComponent(mainTextField).addComponent(mainButton))
				.addGroup(settingsLayout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(tempLabel)
						.addComponent(movieTextField).addComponent(movieButton)));
	}
	
	private void setUI() {
		UIManager.put("FileChooser.openDialogTitleText", "Path");
		UIManager.put("FileChooser.openButtonText", "Save");
		UIManager.put("FileChooser.cancelButtonText", "Cancel");
	}
	
	private boolean setUpChooser(String dirStr) {
		chooser = new JFileChooser();
		chooser.setCurrentDirectory(new java.io.File(dirStr));
		SwingUtilities.updateComponentTreeUI(chooser);
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setAcceptAllFileFilterUsed(false);
		
		return chooser.showOpenDialog(createGUI.frame) == JFileChooser.APPROVE_OPTION;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == mainButton) {
			if (setUpChooser(mainDir)) {
				new LoadSettings();
				mainDir = LoadSettings.changeSettings(chooser.getSelectedFile().toString(), "mainDir");
				mainTextField.setText(mainDir.replace("//", "\\"));
			}
		} else if (e.getSource() == movieButton) {
			if (setUpChooser(movieDir)) {
				new LoadSettings();
				movieDir = LoadSettings.changeSettings(chooser.getSelectedFile().toString(), "movieDir");
				movieTextField.setText(movieDir.replace("//", "\\"));
			}
		}
		settingsDialog.pack();
		settingsDialog.revalidate();
		settingsDialog.repaint();
	}
}
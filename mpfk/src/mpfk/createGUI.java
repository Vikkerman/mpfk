package mpfk;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;

import mpfk.controls.Overlay;
import mpfk.controls.SettingsWindow;
import mpfk.listeners.CustomKeyListener;
import mpfk.listeners.CustomMouseListenerRightMouseButton;
import mpfk.listeners.FileDropHandler;
import mpfk.ui.ColoredMenuBar;
import mpfk.util.LoadSettings;
import mpfk.util.MovieIconPanel;
//import mpfk.util.Snapshots4;
import mpfk.util.VlcjLoader3;
//import mpfk.util.VlcjLoader4;
/**
 * Media Player for kids main class.
 * 
 * @author Vikker
 *
 */
public class createGUI {
	private final static String MENUBARCOLORACTIVE = "#228388";
	public static JFrame frame;
	public static JPanel moviePanel;
	public static MovieIconPanel searchPanel;
	public static ColoredMenuBar menuBar;

	private static Point point = new Point();

	public static VlcjLoader3 emp = new VlcjLoader3();
//	public static VlcjLoader4 emp = new VlcjLoader4();
	public static Canvas movieCanvas;

	public static Overlay overlay;

	private static Object lock = new Object();
	private static SettingsWindow settingsWindows = null;

	public createGUI() {
		frame = new JFrame("vlcMoviePlayer");
		
		new LoadSettings();
		createMenuBar();
		createContentPanel();

		frame.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				point.x = e.getX();
				point.y = e.getY();
			}
		});
		frame.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent e) {
				overlay.suspendTimer();
				Point p = frame.getLocation();
				frame.setLocation(p.x + e.getX() - point.x, p.y + e.getY() - point.y);
			}
		});
		KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		manager.addKeyEventDispatcher(new CustomKeyListener());
		frame.setBackground(Color.BLACK);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setVisible(true);

		setVLC();

		new Thread() {
			public void run() {
				overlay.window().setVisible(false);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (!overlay.window().isVisible()) {
					overlay.window().setVisible(true);
				}
			}
		}.start();
	}

	private void createMenuBar() {
		setLookAndFeel();
		
		menuBar = new ColoredMenuBar();
		menuBar.setColor(MENUBARCOLORACTIVE);
		menuBar.addSettingsMenu();
		menuBar.addExitMenu();
		menuBar.add(Box.createHorizontalGlue());
		menuBar.addSizeButton();
		menuBar.addCloseXButton();

		frame.setJMenuBar(menuBar);
		frame.setUndecorated(true);
	}
	
	private void setLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void createContentPanel() {
		createMoviePanel();
		createSearchPanel();
	}

	private void createMoviePanel() {
		moviePanel = new JPanel();
		moviePanel.setLayout(new BorderLayout());
		movieCanvas = new Canvas();
		movieCanvas.setBackground(Color.BLACK);
		movieCanvas.addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if (e.getWheelRotation() == 1) {
					emp.decreaseVolume();
					overlay.resetTimer();
				}
				if (e.getWheelRotation() == -1) {
					emp.increaseVolume();
					overlay.resetTimer();
				}
			}
			
		});
		moviePanel.add(movieCanvas);
		moviePanel.setTransferHandler(new FileDropHandler());
		movieCanvas.addMouseListener(new CustomMouseListenerRightMouseButton());
		frame.getContentPane().add(moviePanel, BorderLayout.CENTER);
	}
	
	private void createSearchPanel() {
		searchPanel = new MovieIconPanel();
		frame.getContentPane().add(searchPanel.getSrollPane(), BorderLayout.EAST);
	}

	public static void settingsMenu() {
		String oldDir = new LoadSettings().getSettings("movieDir");
		settingsWindows = new SettingsWindow(0);
		Thread t = new Thread() {
			public void run() {
				synchronized (lock) {
					while (settingsWindows.isVisible()) {
						try {
							lock.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					if (!oldDir.equals(new LoadSettings().getSettings("movieDir"))) {
						searchPanel.clearListsAndLoadSetupDir();
						searchPanel.setNewMovieList();
					}
				}
			}
		};
		t.start();

		settingsWindows.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent arg0) {
				synchronized (lock) {
					lock.notify();
				}
			}

		});

		try {
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void setVLC() {
		emp.setUp(frame, movieCanvas);
		emp.setEventListener();

		if (searchPanel.getIconList().size() > 0) {
			searchPanel.getIconList().get(searchPanel.getCurrentMovie()).focusOn();
			searchPanel.getIconList().get(searchPanel.getCurrentMovie()).focusOn();
		}
		
		overlay = new Overlay(frame);
		playFile();
	}

	public static void playFile() {
		if (emp.isSetted() && !searchPanel.movieListisEmpty()) {
			String file = searchPanel.getMovieListItem(searchPanel.getCurrentMovie()).getAbsolutePath();
			searchPanel.setActivateIcon();
			emp.prepare(file);
			emp.play();
			searchPanel.setPreviousMovie(searchPanel.getCurrentMovie());
			emp.setVolume(overlay.getVolume());
			new Thread() {
				public void run() {
					overlay.setVolumeButtons();
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					overlay.setVolumeButtons();
				}
			}.start();
		}
	}
}
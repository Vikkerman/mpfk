package mpfk;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

import mpfk.controls.Overlay;
import mpfk.controls.SettingsWindow;
import mpfk.listeners.CustomKeyListener;
import mpfk.listeners.CustomMouseListenerRightMouseButton;
import mpfk.listeners.FileDropHandler;
import mpfk.ui.ColoredMenuBar;
import mpfk.ui.ScrollBarUI;
import mpfk.util.IconCreatorThread;
import mpfk.util.LoadSettings;
import mpfk.util.MovieIcon;
import mpfk.util.Snapshots3;
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
	private static final String FILESEPARATOR = File.separator;
	private final static String MENUBARCOLORACTIVE = "#228388";
	public static JFrame frame;
	public static JPanel moviePanel, searchPanel;
	public static JScrollPane searchScrollPane;
	public static ColoredMenuBar menuBar;
	public static List<MovieIcon> labels = new ArrayList<MovieIcon>();
	public static int previousMovie = 0, currentMovie = 0;
	private static Point point = new Point();

	public static List<String> fileDir = new ArrayList<>();
	public static List<File> listOfMine = new ArrayList<>();

	public static VlcjLoader3 emp = new VlcjLoader3();
//	public static VlcjLoader4 emp = new VlcjLoader4();
	public static Canvas movieCanvas;

	public static Overlay overlay;

	private static ImageIcon[] imageArray;
	private static String[] imageNames;
	private static ImageIcon defaultIcon = null;

	private static Object lock = new Object();
	private static SettingsWindow settingsWindows = null;
	public static IconCreatorThread pictureLoaderThread = new IconCreatorThread();

	public createGUI() {
		defaultIcon = new ImageIcon((getClass().getResource("/images/movie.jpg")));
		frame = new JFrame("vlcMoviePlayer");

		createMenuBar();
		createContentPanel();
		fileDir.clear();
		fileDir.add(new LoadSettings().getSettings("movieDir"));
		listOfMine.clear();
		loadMovieList();
		setMovieList();

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
//		setLookAndFeel();
		setCustomLookAndFeel();
		
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
	
	public static void list() {
        UIDefaults defaults = UIManager.getDefaults();
        System.out.println(defaults.size()+ " properties defined !");
        String[ ] colName = {"Key", "Value"};
        String[ ][ ] rowData = new String[ defaults.size() ][ 2 ];
        int i = 0;
        for(Enumeration e = defaults.keys(); e.hasMoreElements(); i++){
            Object key = e.nextElement();
            rowData[ i ] [ 0 ] = key.toString();
            rowData[ i ] [ 1 ] = ""+defaults.get(key);
            System.out.println(rowData[i][0]+" ,, "+rowData[i][1]);
        }
        JFrame f = new JFrame("UIManager properties default values");
        JTable t = new JTable(rowData, colName);
        f.setContentPane(new JScrollPane(t));
        //f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.pack();
        f.setVisible(true);
    }
	
	private void setCustomLookAndFeel() {
		list();
		
		UIManager.put("Menu.selectionBackground", new Color(0, 0, 200, 20));
		
		
		
	}

	private void createContentPanel() {
		createMoviePanel();
		createSearchPanel();
	}

	public static void loadMovieList() {
		loadMovieList(null);
	}

	public static void loadMovieList(List<File> fileList) {
		for (String dirs : fileDir) {
			listFilesFrom(dirs, listOfMine);
		}
		if (fileList != null) {
			listOfMine.addAll(fileList);
		}
	}

	public static void setMovieList() {
		labels.clear();
		searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.Y_AXIS));

		File dir = new File(System.getProperty("user.dir") + FILESEPARATOR + "covers" + FILESEPARATOR);
		dir.mkdirs();

		File[] files = dir.listFiles((d, name) -> name.endsWith(".png"));

		imageArray = new ImageIcon[files.length];
		imageNames = new String[files.length];

		for (int i = 0; i < files.length; i++) {
			try {
				imageArray[i] = new ImageIcon(ImageIO.read(files[i]));
				imageNames[i] = files[i].getName();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		for (int i = 0; i < (5 > listOfMine.size() ? listOfMine.size() : 5); i++) {
			if (!listOfMine.get(i).getName().toString().isEmpty()) {
				createIcons(i);
				Dimension currMaxSize = searchPanel.getMaximumSize();
				searchPanel.setPreferredSize(new Dimension(170, currMaxSize.height));
				searchPanel.revalidate();
			}
		}

		pictureLoaderThread = new IconCreatorThread() { // probably listOfMine reseted during initialization, so should
														// stop this when listOfMine reseted (settings window, etc..)
			public void run() {
				for (int i = 5; i < listOfMine.size(); i++) {
					if (!shouldRun) {
						break;
					}
					if (!listOfMine.get(i).getName().toString().isEmpty()) {
						createIcons(i);
						Dimension currMaxSize = searchPanel.getMaximumSize();
						searchPanel.setPreferredSize(new Dimension(170, currMaxSize.height));
						searchPanel.revalidate();
					}
				}
				try {
					if (shouldRun) {
						new Snapshots3(listOfMine);
//						new Snapshots4(listOfMine);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		pictureLoaderThread.start();

		Dimension currMaxSize = searchPanel.getMaximumSize();
		searchPanel.setPreferredSize(new Dimension(170, currMaxSize.height));
	}

	public static void setNewMovieList() {
		setNewMovieList(null);
	}

	public static void setNewMovieList(List<File> filesDropped) {
		while (pictureLoaderThread.isAlive()) {
			pictureLoaderThread.stopThread();
		}
		clearMovieList();
		loadMovieList(filesDropped);
		setMovieList();
		currentMovie = 0;
		playFile();
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
		searchPanel = new JPanel();
		searchScrollPane = new JScrollPane(searchPanel);
		searchScrollPane.getVerticalScrollBar().setUnitIncrement(150);
		searchScrollPane.getVerticalScrollBar().setUI(new ScrollBarUI());
		searchScrollPane.getVerticalScrollBar().setBackground(Color.BLACK);
		searchScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		searchScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		searchPanel.setBorder(BorderFactory.createStrokeBorder(new BasicStroke(3.0f)));
		searchPanel.setOpaque(true);
		searchPanel.setBackground(Color.BLACK);
		frame.getContentPane().add(searchScrollPane, BorderLayout.EAST);
	}

	protected static void clearMovieList() {
		searchPanel.removeAll();
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
						listOfMine.clear();
						fileDir.clear();
						fileDir.add(new LoadSettings().getSettings("movieDir"));
						setNewMovieList();
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

	private static void createIcons(int i) {
		String iconString = listOfMine.get(i).getName().toString();
		labels.add(new MovieIcon(iconString, i, ifThereIsAnImage(listOfMine.get(i).getAbsolutePath().toString())));
		labels.get(i).addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentMovie = ((MovieIcon) e.getSource()).getIndex();
				labels.get(previousMovie).focusOff();
				labels.get(currentMovie).focusOn();
				previousMovie = currentMovie;
				playFile();
			}
		});
		searchPanel.add(labels.get(i));
		JLabel label = new JLabel(iconString);
		label.setForeground(Color.white);
		searchPanel.add(label);
		// searchPanel.add(Box.createRigidArea(new Dimension(0, 20)));
	}

	private static ImageIcon ifThereIsAnImage(String string) {
		string = string.replaceAll("\\\\", "_").replaceAll(":", "");
		for (int i = 0; i < imageNames.length; i++) {
			if (imageNames[i].contains(string)) {
				return imageArray[i];
			}
		}

		return defaultIcon;
	}

	public static void listFilesFrom(String directoryName, List<File> files) {
		File directory = new File(directoryName);

		// Get all files from a directory.
		File[] fileList = directory.listFiles();

		if (fileList != null) {
			for (File file : fileList) {
				if (file.isFile() && movieFile(file)) {
					files.add(file);
				} else if (file.isDirectory()) {
					String lastDirName = file.getName();
					if (!lastDirName.contains("sample")) {
						listFilesFrom(file.getAbsolutePath(), files);
					}
				}
			}
		}
	}

	private void setVLC() {
		emp.setUp(frame, movieCanvas);
//		emp.setUp(frame, movieCanvas);
		emp.setEventListener();

		if (labels.size() > 0) {
			labels.get(currentMovie).focusOn();
		}
		overlay = new Overlay(frame);
		playFile();
	}

	public static void playFile() {
		if (emp.isSetted()) {
			String file = listOfMine.get(currentMovie).getAbsolutePath();

			emp.prepare(file);
			emp.play();
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

	public static boolean movieFile(File file) {
		String name = file.getName();
		if (name.toLowerCase().endsWith(".avi") || name.toLowerCase().endsWith(".mpg")
				|| name.toLowerCase().endsWith(".mp4") || name.toLowerCase().endsWith(".mkv")) {
			return true;
		}
		return false;
	}

	public static void clearPlayList() {
		pictureLoaderThread.stopThread();
		emp.stop();
		searchPanel.removeAll();
		listOfMine.clear();
		fileDir.clear();
		new Thread() {
			public void run() {
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				searchPanel.removeAll();
			}
		}.start();
		Dimension currMaxSize = searchPanel.getMaximumSize();
		searchPanel.setPreferredSize(new Dimension(170, currMaxSize.height));
		searchPanel.revalidate();
	}
}
package vlcmovie;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.windows.Win32FullScreenStrategy;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;
import uk.co.caprica.vlcj.runtime.windows.WindowsRuntimeUtil;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.InputStream;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.TmdbMovies.MovieMethod;
import info.movito.themoviedbapi.model.tv.TvSeries;

public class createGUI {
	private final String FILESEPARATOR = File.separator;
	public static JFrame frame;
	public static JPanel moviePanel;
	public static JPanel searchPanel;
	public static List<MovieIcon> labels = new ArrayList<MovieIcon>();
	public static int previousMovie = 0, currentMovie = 0;
	public static JScrollPane searchScrollPane;
	public static ColoredMenuBar menuBar;
	private JMenu exitMenu, settingsMenu;
	private ColoredMenu closeMenu;
	private JMenuItem exitMi, settingsMi;
	private static Point point = new Point();
	
	private static String fileDir = new LoadSettings().getSettings("movieDir");
	public static List<File> listOfMine = new ArrayList<>();
	private MediaPlayerFactory mpf;
	public static EmbeddedMediaPlayer emp;
	public static Canvas movieCanvas;
	
	public static Overlay overlay;
	public static OverlayPanel topPanel = new OverlayPanel();
	public static OverlayPanel bottomPanel = new OverlayPanel();
	private static TransparentButton buttonPlay;
	private static TransparentButton[] buttonVolume = new TransparentButton[10];
	private static JButton buttonStop;
	public static JProgressBar seekerProgressBar;
	public static MouseMotionTimer mouseMotionTimer;
	
	private static ImageIcon[] imageIcons = new ImageIcon[5];
	private static ImageIcon[] imageArray;
	private static String[] imageNames;
	public static int volume = 50;
	private final ImageIcon defaultIcon =  new ImageIcon((getClass().getResource("/images/movie.jpg")));
	
	public static final String GOOGLE_SEARCH_URL = "https://www.google.com/search";
	
	public static Color hex2Rgb(String colorStr) {
		return new Color(Integer.valueOf(colorStr.substring(1, 3), 16), Integer.valueOf(colorStr.substring(3, 5), 16), Integer.valueOf(colorStr.substring(5, 7), 16));
	}
	
	public createGUI() {
		frame = new JFrame("vlcMoviePlayer");
		
		createMenuBar();
		createContentPanel();
		setMovieList();
		
		frame.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
		    	point.x = e.getX();
		    	point.y = e.getY();
			}
		});
		frame.addMouseMotionListener(new MouseMotionAdapter() {
	    	public void mouseDragged(MouseEvent e) {
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
        frame.setResizable(true);
        frame.setVisible(true);
        
        setVLC();
        new Thread() {
        	public void run() {
        		overlay.setVisible(false);
        		try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
        		overlay.setVisible(true);
        	}
        }.start();
	}
	
	private void createMenuBar() {
		try {
	        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	    }catch(Exception ex) {
	        ex.printStackTrace();
	    }
		
		menuBar = new ColoredMenuBar();
		menuBar.setColor(hex2Rgb("#228388"));
		
		settingsMenu = new JMenu("Settings");
		settingsMenu.setForeground(Color.WHITE);
		settingsMi = new JMenuItem("Settings");
		settingsMi.setMnemonic(KeyEvent.VK_B);
		settingsMi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, ActionEvent.ALT_MASK));
		settingsMi.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	new SettingsWindow(0);
            }
	    });
		
		exitMenu = new JMenu("Exit");
		exitMenu.setForeground(Color.WHITE);
		exitMi = new JMenuItem("Exit");
		exitMi.setMnemonic(KeyEvent.VK_K);
		exitMi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K, ActionEvent.ALT_MASK));
		exitMi.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	System.exit(0);
            }
	    });
		exitMi.setMnemonic(KeyEvent.VK_K);
		exitMi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K, ActionEvent.ALT_MASK));
		
		closeMenu = new ColoredMenu("Exit");
		closeMenu.setForeground(Color.WHITE);
		closeMenu.setColor(hex2Rgb("#c75050"));
		closeMenu.addMouseListener(new MouseListener() {
			boolean isSelected = false;

			@Override
			public void mouseClicked(MouseEvent arg0) {}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				closeMenu.setColor(hex2Rgb("#e04343"));
				isSelected = true;
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				closeMenu.setColor(hex2Rgb("#c75050"));
				isSelected = false;
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				if (isSelected) {
					System.exit(0);
				}
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {}
			
		});
		menuBar.add(settingsMenu);
		menuBar.add(exitMenu);
		menuBar.add(Box.createHorizontalGlue());
		menuBar.add(closeMenu);

		exitMenu.add(exitMi);
		settingsMenu.add(settingsMi);
		frame.setJMenuBar(menuBar);
		frame.setUndecorated(true);
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
		moviePanel.add(movieCanvas);
		frame.getContentPane().add(moviePanel, BorderLayout.CENTER);
	}
	
	private void createSearchPanel() {
		searchPanel = new JPanel();
		searchScrollPane = new JScrollPane(searchPanel);
		searchScrollPane.getVerticalScrollBar().setUnitIncrement(150);
		searchScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		searchPanel.setBorder(BorderFactory.createStrokeBorder(new BasicStroke(3.0f)));
		searchPanel.setOpaque(true);
		searchPanel.setBackground(Color.BLACK);
		frame.getContentPane().add(searchScrollPane, BorderLayout.EAST);
	}

	private void setMovieList() {
        listFilesFrom(fileDir, listOfMine);
        
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.Y_AXIS));

        File dir = new File(System.getProperty("user.dir") + FILESEPARATOR + "covers" + FILESEPARATOR);
        if (!dir.exists()) {
        	dir.mkdirs();
        }
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
			}
		}
        
        Thread pictureLoaderThread = new Thread() {
        	public void run() {
        		for (int i = 5; i < listOfMine.size(); i++) {
        			if (!listOfMine.get(i).getName().toString().isEmpty()) {
        				createIcons(i);
        				Dimension currMaxSize = searchPanel.getMaximumSize();
        				searchPanel.setPreferredSize(new Dimension(170, currMaxSize.height));
						searchPanel.revalidate();
        			}
        		}
        		try {
					new Snapshots(listOfMine);
				} catch (Exception e) {
					e.printStackTrace();
				}
        	}
        };
        
        pictureLoaderThread.start();
        
        Dimension currMaxSize = searchPanel.getMaximumSize();
		searchPanel.setPreferredSize(new Dimension(170, currMaxSize.height));
	}
	
	private void createIcons(int i) {
		labels.add(new MovieIcon(listOfMine.get(i).getName().toString(), i, ifThereIsAnImage(listOfMine.get(i).getAbsolutePath().toString())));
		labels.get(i).addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentMovie = Integer.parseInt(((AbstractButton) e.getSource()).getName());
				String fileString = listOfMine.get(currentMovie).getAbsolutePath();
				labels.get(previousMovie).focusOff();
				labels.get(currentMovie).focusOn();
				previousMovie = currentMovie;
				playFile(fileString);
			}
		});
		searchPanel.add(labels.get(i));
		JLabel label = new JLabel(listOfMine.get(i).getName().toString());
		label.setForeground(Color.white);
		searchPanel.add(label);
//		searchPanel.add(Box.createRigidArea(new Dimension(0, 20)));
	}

	private ImageIcon ifThereIsAnImage(String string) {
		string = string.replaceAll("\\\\", "_").replaceAll(":", "");
		for (int i = 0; i < imageNames.length; i++) {
			if (imageNames[i].contains(string)) {
				return imageArray[i];
			}
		}
		
		return defaultIcon;
	}

	public void listFilesFrom(String directoryName, List<File> files) {
		File directory = new File(directoryName);

		// Get all files from a directory.
		File[] fList = directory.listFiles();
		
		if (fList != null) {
			for (File file : fList) {
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
		if (RuntimeUtil.isWindows()) {
			NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(),WindowsRuntimeUtil.getVlcInstallDir());
		} else {
			NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(),"C:" + FILESEPARATOR + "Program Files" + FILESEPARATOR + "VideoLAN" + FILESEPARATOR + "VLC");
		}
		Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);

		String[] args = {"--video-filter=deinterlace"};// --direct3d11-hw-blending, --vout=gl
		
		mpf = new MediaPlayerFactory(args);
		
		emp = mpf.newEmbeddedMediaPlayer(new Win32FullScreenStrategy(frame));
		emp.setVideoSurface(mpf.newVideoSurface(movieCanvas));
//		emp.toggleFullScreen();
		emp.setEnableMouseInputHandling(false);
		emp.setEnableKeyInputHandling(false);
	
		labels.get(currentMovie).focusOn();
		String fileString = listOfMine.get(currentMovie).getAbsolutePath();
		playFile(fileString);
		setOverlay();
		
		emp.addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
            @Override
            public void muted(MediaPlayer mediaPlayer, boolean muted) {
            	System.out.println("muted");
            	setVolumeButtons();
            }

            @Override
            public void volumeChanged(MediaPlayer mediaPlayer, float volume) {
            	System.out.println("volume changed: " + emp.getVolume());
            	setVolumeButtons();
            }
        });
	}
	
	public static void playFile(String file) {
		emp.prepareMedia(file);
		emp.play();
		emp.setVolume(volume);
	}
	
	private void setOverlay() {
		overlay = new Overlay(frame);
		overlay.setLayout(new BorderLayout());
		
		seekerProgressBar = new JProgressBar();
		seekerProgressBar.setValue(1);
		seekerProgressBar.setMaximum(1000);
		seekerProgressBar.setOpaque(false);
		seekerProgressBar.setBorderPainted(false);
		seekerProgressBar.setUI(new TransparentSlider());
		Thread progressBarThread = new Thread() {
			public void run(){
				while (true) {
					float progress = (float) (emp.getPosition()*1000.0);
					seekerProgressBar.setValue(Math.round(progress));
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		progressBarThread.start();
		seekerProgressBar.addMouseListener(new CustomMouseListener());
		seekerProgressBar.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
            	int borderWidth = 7;
        		Point pMouse = e.getPoint();
        		int wSeeker = createGUI.seekerProgressBar.getWidth() - (2 * borderWidth);
        		int progressSet = (int) Math.round((pMouse.x - borderWidth)/(wSeeker/1000.0));
        		createGUI.seekerProgressBar.setValue(progressSet);
        		createGUI.emp.setPosition((float) progressSet / 1000);
            }
        });
		
		imageIcons[0] = new ImageIcon((getClass().getResource("/images/play.png")));
		imageIcons[1] = new ImageIcon((getClass().getResource("/images/pause.png")));
		imageIcons[2] = new ImageIcon((getClass().getResource("/images/stop.png")));
		imageIcons[3] = new ImageIcon((getClass().getResource("/images/volumeon.png")));
		imageIcons[4] = new ImageIcon((getClass().getResource("/images/volumeoff.png")));
		
		for (int i = 0; i < imageIcons.length; i ++) {
		    Image image = imageIcons[i].getImage();
		    Image newimg = image.getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH);
		    imageIcons[i] = new ImageIcon(newimg);
		}
		buttonPlay = new TransparentButton(imageIcons[0], imageIcons[1]);
		
		buttonPlay.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	if (buttonPlay.getState()) {
		    		buttonPlay.switchIco();
		    		System.out.println("1:"+buttonPlay.getState());
		    		emp.play();
		    	} else {
		    		buttonPlay.switchIco();
		    		System.out.println("2:"+buttonPlay.getState());
		    		emp.pause();
		    	}
		    }
		});

		buttonStop = setButton(buttonStop, imageIcons[2]);
		
		buttonStop.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	emp.stop();
		    }
		});
		
		ButtonGroup group = new ButtonGroup();
		group.add(buttonPlay);
		group.add(buttonStop);
		
		for (int i = 0; i < buttonVolume.length; i++) {
			buttonVolume[i] = new TransparentButton(imageIcons[3], imageIcons[4], 20, i+1);
		}
		
		ButtonGroup groupVolume = new ButtonGroup();
		for (int i = 0; i < buttonVolume.length; i++) {
			groupVolume.add(buttonVolume[i]);
			buttonVolume[i].addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					volume = ((TransparentButton) e.getSource()).getIndex() * 10;
					emp.setVolume(volume);
					setVolumeButtons();
				}
				
			});
		}
		
		topPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
//		topPanel.setBackground(new Color(0, 0, 0, 5));
		for (int i = 0; i < buttonVolume.length; i++) {
			topPanel.add(buttonVolume[i]);
		};
		setVolumeButtons();
		bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		bottomPanel.add(buttonPlay);
		bottomPanel.add(buttonStop);
		bottomPanel.add(seekerProgressBar);
        
		mouseMotionTimer = new MouseMotionTimer();
		mouseMotionTimer.startTimer();
		
		
		overlay.add(topPanel, BorderLayout.NORTH);
		overlay.add(bottomPanel, BorderLayout.SOUTH);
		
		emp.setOverlay(overlay);
		emp.enableOverlay(true);
	}

	public static void setVolumeButtons() {
		for (int i = 0; i < buttonVolume.length; i++) {
			if (emp.getVolume() >= (i + 1) * 10) {
				buttonVolume[i].setOn();
			} else {
				buttonVolume[i].setOff();
			}
		}
	}

	private JButton setButton(JButton thisButton, ImageIcon img) {
		thisButton = new JButton(img);
		thisButton.setBackground(new Color(0, 0, 0, 0));
		thisButton.setContentAreaFilled(false);
		
		thisButton.setPreferredSize(new Dimension(50, 50));
		thisButton.setMaximumSize(new Dimension(50, 50));
		thisButton.setMinimumSize(new Dimension(50, 50));
		thisButton.setFocusPainted(false);
		return thisButton;
	}

	private boolean movieFile(File file) {
		String name = file.getName();
    	if (name.toLowerCase().endsWith(".avi") ||
    		name.toLowerCase().endsWith(".mpg") ||
    		name.toLowerCase().endsWith(".mp4") ||
    		name.toLowerCase().endsWith(".mkv")) {
    		return true;
    	}
		return false;
	}
}
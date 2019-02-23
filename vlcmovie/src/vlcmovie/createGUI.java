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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
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
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.windows.Win32FullScreenStrategy;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;
import uk.co.caprica.vlcj.runtime.windows.WindowsRuntimeUtil;
import vlcmovie.controls.Overlay;
import vlcmovie.controls.OverlayPanel;
import vlcmovie.controls.SettingsWindow;
import vlcmovie.controls.TransparentButton;
import vlcmovie.controls.TransparentSlider;
import vlcmovie.listeners.CustomKeyListener;
import vlcmovie.listeners.CustomMouseListener;
import vlcmovie.listeners.CustomMouseListenerRightMouseButton;
import vlcmovie.listeners.FileDropHandler;
import vlcmovie.listeners.MouseMotionTimer;
import vlcmovie.ui.ColoredMenuBar;
import vlcmovie.ui.ScrollBarUI;
import vlcmovie.util.IconCreatorThread;
import vlcmovie.util.LoadSettings;
import vlcmovie.util.MovieIcon;
import vlcmovie.util.Snapshots;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;

public class createGUI {
	private static final  String FILESEPARATOR = File.separator;
	private static final  String[] VLC_ARGS = {"--video-filter=deinterlace"};// --direct3d11-hw-blending, --vout=gl
	public static JFrame frame;
	public static JPanel moviePanel, searchPanel;
	public static JScrollPane searchScrollPane;
	public static ColoredMenuBar menuBar;
	public static List<MovieIcon> labels = new ArrayList<MovieIcon>();
	public static int previousMovie = 0, currentMovie = 0;
	private static Point point = new Point();
	
	public static List<String> fileDir = new ArrayList<>();
	public static List<File> listOfMine = new ArrayList<>();
	
	private MediaPlayerFactory mpf;
	public static EmbeddedMediaPlayer emp;
	public static Canvas movieCanvas;
	
	public static Overlay overlay;
	private static OverlayPanel topPanel = new OverlayPanel();
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
	private static ImageIcon defaultIcon = null;
		
	private static Object lock = new Object();
	private static SettingsWindow settingsWindows = null;
	public static IconCreatorThread pictureLoaderThread;
	
	public createGUI() {
		defaultIcon =  new ImageIcon((getClass().getResource("/images/movie.jpg")));
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
        		if (!overlay.isVisible()) {
        			overlay.setVisible(true);
        		}
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
		menuBar.setColor("#228388");
		menuBar.addSettingsMenu();
		menuBar.addExitMenu();
		menuBar.add(Box.createHorizontalGlue());
		menuBar.addCloseXButton();

		frame.setJMenuBar(menuBar);
		frame.setUndecorated(true);
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
        
        while (listOfMine.size() < 1) {
        	reloadMoviList();
        }
        if (fileList != null) {
        	listOfMine.addAll(fileList);
        }
	}
	
	public static void setMovieList() {
        labels.clear();
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
				Dimension currMaxSize = searchPanel.getMaximumSize();
				searchPanel.setPreferredSize(new Dimension(170, currMaxSize.height));
				searchPanel.revalidate();
			}
		}
        
        pictureLoaderThread = new IconCreatorThread() { // probably listOfMine reseted during initialization, so should stop this when listOfMine reseted (settings window, etc..)
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
        				new Snapshots(listOfMine);
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
		if (pictureLoaderThread.isAlive()) {
			pictureLoaderThread.stopThread();
		}
		clearMovieList();
    	loadMovieList();
    	setMovieList();
    	currentMovie = 0;
    	String fileString = listOfMine.get(currentMovie).getAbsolutePath();
		playFile(fileString);
	}
	
	public static void setNewMovieList(List<File> filesDropped) {
		while (pictureLoaderThread.isAlive()) {
			pictureLoaderThread.stopThread();
		}
		clearMovieList();
		loadMovieList(filesDropped);
    	setMovieList();
    	currentMovie = 0;
    	String fileString = listOfMine.get(currentMovie).getAbsolutePath();
		playFile(fileString);
	}
	
	private void createMoviePanel() {
		moviePanel = new JPanel();
		moviePanel.setLayout(new BorderLayout());
		movieCanvas = new Canvas();
		movieCanvas.setBackground(Color.BLACK);
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
	
	public static void reloadMoviList() {
		String oldDir = new LoadSettings().getSettings("movieDir");
    	settingsWindows = new SettingsWindow(0);
    	Thread t = new Thread() {
            public void run() {
                synchronized(lock) {
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
				currentMovie = Integer.parseInt(((AbstractButton) e.getSource()).getName());
				String fileString = listOfMine.get(currentMovie).getAbsolutePath();
				labels.get(previousMovie).focusOff();
				labels.get(currentMovie).focusOn();
				previousMovie = currentMovie;
				playFile(fileString);
			}
		});
		searchPanel.add(labels.get(i));
		JLabel label = new JLabel(iconString);
		label.setForeground(Color.white);
		searchPanel.add(label);
//		searchPanel.add(Box.createRigidArea(new Dimension(0, 20)));
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
		if (RuntimeUtil.isWindows()) {
			NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(),WindowsRuntimeUtil.getVlcInstallDir());
		} else {
			NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(),"C:" + FILESEPARATOR + "Program Files" + FILESEPARATOR + "VideoLAN" + FILESEPARATOR + "VLC");
		}
		Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
		
		mpf = new MediaPlayerFactory(VLC_ARGS);
		
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
            	setVolumeButtons();
            }

            @Override
            public void volumeChanged(MediaPlayer mediaPlayer, float volume) {
            	setVolumeButtons();
            }
        });
	}
	
	public static void playFile(String file) {
		emp.prepareMedia(file);
		emp.play();
		emp.setVolume(volume);
		new Thread() {
			public void run() {
				setVolumeButtons();
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				setVolumeButtons();
			}
		}.start();
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
            	if (SwingUtilities.isLeftMouseButton(e)) {
            		int borderWidth = 7;
            		Point pMouse = e.getPoint();
            		int wSeeker = createGUI.seekerProgressBar.getWidth() - (2 * borderWidth);
            		int progressSet = (int) Math.round((pMouse.x - borderWidth)/(wSeeker/1000.0));
            		createGUI.seekerProgressBar.setValue(progressSet);
            		createGUI.emp.setPosition((float) progressSet / 1000);
            	}
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
		buttonPlay.addMouseListener(new CustomMouseListener());
		buttonPlay.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	if (listOfMine.size() > 0) {
			    	if (!emp.isSeekable()) {
			    		buttonPlay.setOff();
			    		String fileString = listOfMine.get(currentMovie).getAbsolutePath();
			    		playFile(fileString);
			    	} else if (!emp.isPlaying()) {
			    		buttonPlay.setOff();
			    		emp.play();
			    	} else {
			    		buttonPlay.setOn();
			    		emp.pause();
			    	}
		    	}
		    }
		});

		buttonStop = setButton(buttonStop, imageIcons[2]);
		buttonStop.addMouseListener(new CustomMouseListener());
		buttonStop.addActionListener(new ActionListener() {
		    @Override
		    public void actionPerformed(ActionEvent e) {
		    	buttonPlay.setOn();
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
			if (buttonVolume[i] == null) {
				break;
			}
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

	public static boolean movieFile(File file) {
		String name = file.getName();
    	if (name.toLowerCase().endsWith(".avi") ||
    		name.toLowerCase().endsWith(".mpg") ||
    		name.toLowerCase().endsWith(".mp4") ||
    		name.toLowerCase().endsWith(".mkv")) {
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
package mpfk.util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import mpfk.createGUI;
import mpfk.ui.ScrollBarUI;

public class MovieIconPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static JScrollPane searchScrollPane;
	
	private static final String FILESEPARATOR = File.separator;
	private int previousMovie;
	private int currentMovie;
	private static IconCreatorThread iconCreatorThread;
	private static List<String> movieDir = new ArrayList<>();  // renamed fileDir
	private static List<File> movieList = new ArrayList<>();  //renamed listOfMine
	private static List<MovieIcon> movieIcons = new ArrayList<MovieIcon>();
	private static List<FocusableLabel> labels = new ArrayList<FocusableLabel>();
	private static ImageIcon[] imageArray;
	private static String[] imageNames;
	private static ImageIcon defaultIcon = null;
	
	public MovieIconPanel() {
		previousMovie = 0;
		currentMovie = 0;
		iconCreatorThread = new IconCreatorThread();
		movieDir.add(new LoadSettings().getSettings("movieDir"));
		defaultIcon = new ImageIcon((getClass().getResource("/images/movie.jpg")));
		
		setBorder(BorderFactory.createStrokeBorder(new BasicStroke(3.0f)));
		setOpaque(true);
		setBackground(Color.BLACK);
		
		setScrollPane();
		setNewMovieList();
	}
	
	private void setScrollPane() {
		searchScrollPane = new JScrollPane(this);
		searchScrollPane.getVerticalScrollBar().setUnitIncrement(150);
		searchScrollPane.getVerticalScrollBar().setUI(new ScrollBarUI());
		searchScrollPane.getVerticalScrollBar().setBackground(Color.BLACK);
		searchScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		searchScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
	}

	public void setNewMovieList() {
		setNewMovieList(null);
	}
	
	public void setNewMovieList(List<File> filesDropped) {
		stopIconCreatorThread();
		stopCurrentMedia();
		clearIconPanel();
		loadMovieList(filesDropped);
		setMovieList();
		previousMovie = currentMovie = 0;
		createGUI.playFile(); // rename or reprogramming later
	}
	
	private void stopIconCreatorThread() {
		if (iconCreatorThread.isAlive()) {
			iconCreatorThread.stopThread();
		}
	}
	
	private void stopCurrentMedia() {
		if (createGUI.emp.isSetted()) {
			createGUI.emp.stop();
		}
	}

	private void clearIconPanel() {
		getPanel().removeAll();
	}

	private void loadMovieList(List<File> fileList) {
		for (String dirs : movieDir) {
			listFilesFrom(dirs, movieList);
		}
		if (fileList != null) {
			movieList.addAll(fileList);
		}
	}
	
	private void listFilesFrom(String directoryName, List<File> files) {
		if (directoryName != null) {
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
	}
	
	public boolean movieFile(File file) {
		String name = file.getName();
		if (name.toLowerCase().endsWith(".avi") ||
			name.toLowerCase().endsWith(".mpg") ||
			name.toLowerCase().endsWith(".mp4") ||
			name.toLowerCase().endsWith(".mkv")) {
			return true;
		}
		return false;
	}
	
	private void setMovieList() {
		movieIcons.clear();
		labels.clear();
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

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

		for (int i = 0; i < (5 > movieList.size() ? movieList.size() : 5); i++) {
			if (!movieList.get(i).getName().toString().isEmpty()) {
				createMovieIcons(i);
				Dimension currMaxSize = getPanelMaximumSize();
				setPanelPreferredSize(new Dimension(170, currMaxSize.height));
				revalidatePanel();
			}
		}

		iconCreatorThread = new IconCreatorThread() { // probably movieList reseted during initialization, so should
														// stop this when movieList reseted (settings window, etc..)
			public void run() {
				for (int i = 5; i < movieList.size(); i++) {
					if (!shouldRun) {
						break;
					}
					if (!movieList.get(i).getName().toString().isEmpty()) {
						createMovieIcons(i);
						Dimension currMaxSize = getPanel().getMaximumSize();
						getPanel().setPreferredSize(new Dimension(170, currMaxSize.height));
						getPanel().revalidate();
					}
				}
				getPanel().add(Box.createRigidArea(new Dimension(0, 20)));
				Dimension currMaxSize = getPanelMaximumSize();
				setPanelPreferredSize(new Dimension(170, currMaxSize.height));
				revalidatePanel();
				try {
					if (shouldRun) {
						new Snapshots3(movieList);
//						new Snapshots4(movieList);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		iconCreatorThread.start();

		Dimension currMaxSize = getPanelMaximumSize();
		setPanelPreferredSize(new Dimension(170, currMaxSize.height));
	}
	
	protected void revalidatePanel() {
		this.revalidate();
	}

	protected void setPanelPreferredSize(Dimension dim) {
		this.setPreferredSize(dim);
	}

	private Dimension getPanelMaximumSize() {
		return this.getMaximumSize();
	}
	
	private void createMovieIcons(int i) {
		String iconString = movieList.get(i).getName().toString();
		movieIcons.add(new MovieIcon(iconString, i, ifThereIsAnImage(movieList.get(i).getAbsolutePath().toString())));
		FocusableLabel label = new FocusableLabel(iconString);
		label.setForeground(Color.white);
		labels.add(label);
		movieIcons.get(i).addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentMovie = ((MovieIcon) e.getSource()).getIndex();
				setActivateIcon();
				previousMovie = currentMovie;
				createGUI.playFile();
			}
		});
		this.add(movieIcons.get(i));
		this.add(labels.get(i));
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
	
	public void clearPlayList() {
		iconCreatorThread.stopThread();
		createGUI.emp.stop();
		clearIconPanel();
		movieList.clear();
		movieDir.clear();
		new Thread() {
			public void run() {
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				clearIconPanel();
			}
		}.start();
		Dimension currMaxSize = getPanel().getMaximumSize();
		getPanel().setPreferredSize(new Dimension(170, currMaxSize.height));
		getPanel().revalidate();
	}
	
	public void setActivateIcon() {
		movieIcons.get(previousMovie).focusOff();
		movieIcons.get(currentMovie).focusOn();
		labels.get(previousMovie).focusOff();
		labels.get(currentMovie).focusOn();
		revalidate();
	}
		
	private JPanel getPanel() {
		return this;
	}

	public JScrollPane getSrollPane() {
		return searchScrollPane;
	}

	public void clearListsAndLoadSetupDir() {
		movieList.clear();
		movieDir.clear();
		movieDir.add(new LoadSettings().getSettings("movieDir"));
	}

	public List<MovieIcon> getIconList() {
		return movieIcons;
	}

	public int getCurrentMovie() {
		return currentMovie;
	}

	public boolean movieListisEmpty() {
		return movieList.isEmpty();
	}

	public File getMovieListItem(int index) {
		return movieList.get(index);
	}

	public void setPreviousMovie(int index) {
		previousMovie = index;	
	}

	public void setIconList(int i, File snapshotFile) {
		movieIcons.get(i).setImage(snapshotFile);
	}

	public void nextCurrentMovie() {
		currentMovie++;
	}
	
	public void pervCurrentMovie() {
		currentMovie--;
	}

	public int getMovieListSize() {
		return movieList.size();
	}

	public void setCurrentMovie(int index) {
		currentMovie = index;
	}

	public void setActiveIcons() {
		movieIcons.get(previousMovie).focusOff();
		movieIcons.get(currentMovie).focusOn();
		previousMovie = currentMovie;
	}

	public void setScrollPaneViewPosition(Point point) {
		searchScrollPane.getViewport().setViewPosition(point);
	}

	public void clearMovieList() {
		movieList.clear();
	}

	public void addToMovieDirfileDir(String dir) {
		movieDir.add(dir);
	}

}

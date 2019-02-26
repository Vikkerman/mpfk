package mpfk.controls;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import mpfk.createGUI;
import mpfk.listeners.CustomMouseListener;
import mpfk.listeners.MouseMotionTimer;

public class Overlay {
	private static OverlayWindow overlayWindow;
	private static OverlayPanel topPanel = new OverlayPanel();
	private static OverlayPanel bottomPanel = new OverlayPanel();
	public JProgressBar seekerBar;

	private MouseMotionTimer mouseMotionTimer;
	private LoadAndScaleImages imageIcons = new LoadAndScaleImages();
	private TransparentButton buttonPlay;
	private JButton buttonStop;
	private TransparentButton buttonMute;
	private static TransparentButton[] buttonVolume;
	private int volume = 50;

	public Component window() {
		return overlayWindow;
	}

	public int getVolume() {
		return volume;
	}

	public void setVolume(int value) {
		volume += value;
	}

	public void repaint() {
		overlayWindow.repaint();
	}

	public void repaintBottomPanel() {
		bottomPanel.repaint();
	}

	public Overlay(JFrame parent) {
		overlayWindow = new OverlayWindow(parent);
		overlayWindow.setLayout(new BorderLayout());

		buttonMute = playMute();
		buttonVolume = volumeButtons();

		ButtonGroup groupVolume = new ButtonGroup();
		groupVolume.add(buttonMute);
		for (int i = 0; i < buttonVolume.length; i++) {
			groupVolume.add(buttonVolume[i]);
		}

		topPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		topPanel.add(buttonMute);
		for (int i = 0; i < buttonVolume.length; i++) {
			topPanel.add(buttonVolume[i]);
		}
		;

		buttonMute.setOn();
		setVolumeButtons();

		buttonPlay = playButton();
		buttonStop = stopButton();
		seekerBar = new Seeker().getSeeker();

		ButtonGroup group = new ButtonGroup();
		group.add(buttonPlay);
		group.add(buttonStop);

		bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		bottomPanel.add(buttonPlay);
		bottomPanel.add(buttonStop);
		bottomPanel.add(seekerBar);

		mouseMotionTimer = new MouseMotionTimer();
		mouseMotionTimer.startTimer();

		overlayWindow.add(topPanel, BorderLayout.NORTH);
		overlayWindow.add(bottomPanel, BorderLayout.SOUTH);

		createGUI.emp.overlay().set(overlayWindow);
		createGUI.emp.overlay().enable(true);

	}

	private TransparentButton[] volumeButtons() {
		TransparentButton[] initVolume = new TransparentButton[10];
		for (int i = 0; i < initVolume.length; i++) {
			initVolume[i] = new TransparentButton(imageIcons.getImage(3), imageIcons.getImage(4), 20, i + 1);
			initVolume[i].addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					volume = ((TransparentButton) e.getSource()).getIndex() * 10;
					createGUI.emp.audio().setVolume(volume);
					setVolumeButtons();
				}

			});
		}

		return initVolume;
	}

	public void setVolumeButtons() {
		for (int i = 0; i < buttonVolume.length; i++) {
			if (buttonVolume[i] == null) {
				break;
			}
			if (createGUI.emp.audio().volume() >= (i + 1) * 10) {
				buttonVolume[i].setOn();
			} else {
				buttonVolume[i].setOff();
			}
		}
	}

	private TransparentButton playMute() {
		TransparentButton initMute = new TransparentButton(imageIcons.getImage(5), imageIcons.getImage(6), 20, 0);
		initMute.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (createGUI.emp.audio().volume() == 0) {
					createGUI.emp.audio().setVolume(volume);
					initMute.setOn();
				} else {
					createGUI.emp.audio().setVolume(0);
					initMute.setOff();
				}
				setVolumeButtons();
			}

		});

		return initMute;
	}

	private TransparentButton playButton() {
		TransparentButton initPlay = new TransparentButton(imageIcons.getImage(0), imageIcons.getImage(1));
		initPlay.addMouseListener(new CustomMouseListener());
		initPlay.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (createGUI.listOfMine.size() > 0) {
					if (!createGUI.emp.status().isSeekable()) {
						initPlay.setOff();
						String fileString = createGUI.listOfMine.get(createGUI.currentMovie).getAbsolutePath();
						createGUI.playFile(fileString);
					} else if (!createGUI.emp.status().isPlaying()) {
						initPlay.setOff();
						createGUI.emp.controls().play();
					} else {
						initPlay.setOn();
						createGUI.emp.controls().pause();
					}
				}
			}
		});

		return initPlay;
	}

	private JButton stopButton() {
		JButton initStop = new JButton(imageIcons.getImage(2));
		initStop.setBackground(new Color(0, 0, 0, 0));
		initStop.setContentAreaFilled(false);

		initStop.setPreferredSize(new Dimension(50, 50));
		initStop.setMaximumSize(new Dimension(50, 50));
		initStop.setMinimumSize(new Dimension(50, 50));
		initStop.setFocusPainted(false);
		initStop.addMouseListener(new CustomMouseListener());
		initStop.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				buttonPlay.setOn();
				createGUI.emp.controls().stop();
			}
		});
		return initStop;
	}

	private class LoadAndScaleImages {
		ImageIcon[] iIcons = new ImageIcon[7];

		public LoadAndScaleImages() {
			loadImages();
		}

		public ImageIcon getImage(int i) {
			return iIcons[i];
		}

		private void loadImages() {
			iIcons[0] = new ImageIcon((getClass().getResource("/images/play.png")));
			iIcons[1] = new ImageIcon((getClass().getResource("/images/pause.png")));
			iIcons[2] = new ImageIcon((getClass().getResource("/images/stop.png")));
			iIcons[3] = new ImageIcon((getClass().getResource("/images/volumeon.png")));
			iIcons[4] = new ImageIcon((getClass().getResource("/images/volumeoff.png")));
			iIcons[5] = new ImageIcon((getClass().getResource("/images/volumeunmuted.png")));
			iIcons[6] = new ImageIcon((getClass().getResource("/images/volumemuted.png")));

			for (int i = 0; i < iIcons.length; i++) {
				Image image = iIcons[i].getImage();
				Image newimg = image.getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH);
				iIcons[i] = new ImageIcon(newimg);
			}
		}
	}

	public class Seeker extends JProgressBar {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private final int OUTERBORDER = 7;
		private final int INNERBORDER = OUTERBORDER * 2;
		private final float SCALE = (float) 1000.0;

		private JProgressBar seeker;

		public Seeker() {
			seeker = new JProgressBar();
			seeker.setValue(1);
			seeker.setMaximum((int) SCALE);
			seeker.setOpaque(false);
			seeker.setBorderPainted(false);
			seeker.setUI(new TransparentSlider());
			seeker.addMouseListener(new CustomMouseListener());
			seeker.addMouseMotionListener(new MouseMotionAdapter() {
				@Override
				public void mouseDragged(MouseEvent e) {
					if (SwingUtilities.isLeftMouseButton(e)) {
						Point pMouse = e.getPoint();
						int wSeeker = seeker.getWidth() - INNERBORDER;
						int progressSet = (int) Math.round((pMouse.x - OUTERBORDER) / (wSeeker / SCALE));
						seeker.setValue(progressSet);
						createGUI.emp.controls().setPosition((float) progressSet / SCALE);
					}
				}
			});

			new Thread() {
				public void run() {
					while (true) {
						float progress = (float) (createGUI.emp.status().position() * SCALE);
						seeker.setValue(Math.round(progress));
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}.start();
		}

		public JProgressBar getSeeker() {
			return seeker;
		}
	}
}

package mpfk.listeners;

import java.awt.KeyEventDispatcher;
import java.awt.event.KeyEvent;

import mpfk.createGUI;
/**
 * Simple KeyListener for handling Key events
 * 
 * @author Vikker
 *
 */
public class CustomKeyListener implements KeyEventDispatcher {
	@Override
	public boolean dispatchKeyEvent(KeyEvent e) {
		if (e.getID() == KeyEvent.KEY_RELEASED)
			return false;
		int key = e.getKeyCode();

		if (key == KeyEvent.VK_LEFT) {
			if (createGUI.overlay.getVolume() >= 10) {
				createGUI.overlay.setVolume(-10);
			}
			createGUI.emp.setVolume(createGUI.overlay.getVolume());
			MouseMotionTimer.resetTimer();
		}

		if (key == KeyEvent.VK_RIGHT) {
			if (createGUI.overlay.getVolume() <= 90) {
				createGUI.overlay.setVolume(10);
			}
			createGUI.emp.setVolume(createGUI.overlay.getVolume());
			MouseMotionTimer.resetTimer();
		}

		// Mute - 'M' and 'm' keys
		if (key == 77 || key == 109) {
			if (createGUI.emp.getVolume() == 0) {
				createGUI.emp.setVolume(createGUI.overlay.getVolume());
			} else {
				createGUI.emp.setVolume(0);
			}
			MouseMotionTimer.resetTimer();
		}

		// Clear PlayList - 'C' and 'c' keys
		if (key == 67 || key == 99) {
			createGUI.searchPanel.clearPlayList();
			MouseMotionTimer.resetTimer();
		}

		if (key == KeyEvent.VK_DOWN) {
			if (createGUI.searchPanel.getCurrentMovie() < createGUI.searchPanel.getMovieListSize() - 1) {
				createGUI.searchPanel.nextCurrentMovie();
			} else {
				createGUI.searchPanel.setCurrentMovie(0);
			}
			createGUI.searchPanel.setActiveIcons();

			createGUI.playFile();

			int yCoor = createGUI.searchPanel.getComponent(createGUI.searchPanel.getCurrentMovie() * 2).getY() - 30;
			createGUI.searchPanel.setScrollPaneViewPosition(new java.awt.Point(0, yCoor));
			MouseMotionTimer.resetTimer();
		}

		if (key == KeyEvent.VK_UP) {
			if (createGUI.searchPanel.getCurrentMovie() > 1) {
				createGUI.searchPanel.pervCurrentMovie();
			} else {
				createGUI.searchPanel.setCurrentMovie(createGUI.searchPanel.getMovieListSize() - 1);
			}
			createGUI.searchPanel.setActiveIcons();

			createGUI.playFile();

			int yCoor = createGUI.searchPanel.getComponent(createGUI.searchPanel.getCurrentMovie() * 2).getY() - 30;
			createGUI.searchPanel.setScrollPaneViewPosition(new java.awt.Point(0, yCoor));
			MouseMotionTimer.resetTimer();
		}
		return false;
	}
}

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
			createGUI.emp.audio().setVolume(createGUI.overlay.getVolume());
			MouseMotionTimer.resetTimer();
		}

		if (key == KeyEvent.VK_RIGHT) {
			if (createGUI.overlay.getVolume() <= 90) {
				createGUI.overlay.setVolume(10);
			}
			createGUI.emp.audio().setVolume(createGUI.overlay.getVolume());
			MouseMotionTimer.resetTimer();
		}

		// Mute - 'M' and 'm' keys
		if (key == 77 || key == 109) {
			if (createGUI.emp.audio().volume() == 0) {
				createGUI.emp.audio().setVolume(createGUI.overlay.getVolume());
			} else {
				createGUI.emp.audio().setVolume(0);
			}
			MouseMotionTimer.resetTimer();
		}

		// Clear PlayList - 'C' and 'c' keys
		if (key == 67 || key == 99) {
			createGUI.clearPlayList();
			MouseMotionTimer.resetTimer();
		}

		if (key == KeyEvent.VK_DOWN) {
			if (createGUI.currentMovie < createGUI.listOfMine.size() - 1) {
				createGUI.currentMovie++;
			} else {
				createGUI.currentMovie = 0;
			}
			createGUI.labels.get(createGUI.previousMovie).focusOff();
			createGUI.labels.get(createGUI.currentMovie).focusOn();
			createGUI.previousMovie = createGUI.currentMovie;

			String fileString = createGUI.listOfMine.get(createGUI.currentMovie).getAbsolutePath();
			createGUI.playFile(fileString);

			int yCoor = createGUI.searchPanel.getComponent(createGUI.currentMovie * 2).getY() - 30;
			createGUI.searchScrollPane.getViewport().setViewPosition(new java.awt.Point(0, yCoor));
			MouseMotionTimer.resetTimer();
		}

		if (key == KeyEvent.VK_UP) {
			if (createGUI.currentMovie > 1) {
				createGUI.currentMovie--;
			} else {
				createGUI.currentMovie = createGUI.listOfMine.size() - 1;
			}
			createGUI.labels.get(createGUI.previousMovie).focusOff();
			createGUI.labels.get(createGUI.currentMovie).focusOn();
			createGUI.previousMovie = createGUI.currentMovie;

			String fileString = createGUI.listOfMine.get(createGUI.currentMovie).getAbsolutePath();
			createGUI.playFile(fileString);

			int yCoor = createGUI.searchPanel.getComponent(createGUI.currentMovie * 2).getY() - 30;
			createGUI.searchScrollPane.getViewport().setViewPosition(new java.awt.Point(0, yCoor));
			MouseMotionTimer.resetTimer();
		}
		return false;
	}
}

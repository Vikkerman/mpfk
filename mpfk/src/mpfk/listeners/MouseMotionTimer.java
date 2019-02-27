package mpfk.listeners;

import java.awt.MouseInfo;
import java.awt.Point;

import mpfk.createGUI;
/**
 * This class for handling mouse actions. If mouse moves, than reset timer.
 * After 3 seconds without interactions overlay should set visible(false).
 * If mouse moves or clicking over frame, overlay should set visible(true).
 * Or if it's still visible, then reset timer.
 * 
 * @author Vikker
 *
 */
public class MouseMotionTimer {
	private static int restTime = 0;
	private static boolean suspended = false;
	private static Point mouseLastPosition = MouseInfo.getPointerInfo().getLocation();

	public static void resetTimer() {
		restTime = 0;
	}
	
	public static void suspend() {
		if (!suspended) {
			suspended = true;
			new Thread() {
				public void run() {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					createGUI.overlay.setOverlayChanges();
					back();
				}
			}.start();
		}
	}
	
	public static void back() {
		suspended = false;
	}

	public void startTimer() {
		new Thread() {
			public void run() {
				
				while (true) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					if (!suspended) {
						Point newPosition = MouseInfo.getPointerInfo().getLocation();

						if (mouseLastPosition.equals(newPosition) || (createGUI.movieCanvas.getMousePosition() == null)) {
							restTime++;
						} else {
							restTime = 0;
						}

						mouseLastPosition = newPosition;

						if (restTime >= 30) {
							if (createGUI.overlay.window().isVisible() && createGUI.frame.isActive())
								createGUI.overlay.window().setVisible(false);
						} else {
							if (!createGUI.overlay.window().isVisible())
								createGUI.overlay.window().setVisible(true);
						}
					} else {
						createGUI.overlay.window().setVisible(false);
					}
					
				}
			}
		}.start();
	}
}

package mpfk.listeners;

import java.awt.MouseInfo;
import java.awt.Point;

import mpfk.createGUI;

public class MouseMotionTimer {
	private static int restTime = 0;
	private static Point mouseLastPosition = MouseInfo.getPointerInfo().getLocation();

	public static void resetTimer() {
		restTime = 0;
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
					Point newPosition = MouseInfo.getPointerInfo().getLocation();

					if (mouseLastPosition.equals(newPosition)) {
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
				}
			}
		}.start();
	}
}

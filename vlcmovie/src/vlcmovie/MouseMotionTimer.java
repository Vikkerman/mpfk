package vlcmovie;

import java.awt.MouseInfo;
import java.awt.Point;

public class MouseMotionTimer {
	private static int restTime = 0;
	private static Point mouseLastPosition = MouseInfo.getPointerInfo().getLocation();
	
	public static void resetTimer() {
		restTime = 0;
	}

	void startTimer() {
		new Thread() {
			public void run(){
				while (true) {
					Point newPosition = MouseInfo.getPointerInfo().getLocation();
					
					if (mouseLastPosition.equals(newPosition)) {
						restTime++;
					} else {
						restTime = 0;
					}
					
					mouseLastPosition = newPosition;
					
					if (restTime >= 30) {
						createGUI.overlay.setVisible(false);
					} else {
						createGUI.overlay.setVisible(true);
					}
					
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
	}
}

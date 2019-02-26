package mpfk.util;
/**
 * Simple thread extension for easier handling.
 * 
 * @author Vikker
 *
 */
public class IconCreatorThread extends Thread {
	protected boolean shouldRun;

	public IconCreatorThread() {
		shouldRun = true;
	}

	public void startThread() {
		shouldRun = true;
		run();
	}

	public void stopThread() {
		shouldRun = false;
	}
}

package mpfk.controls;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Window;
import com.sun.jna.platform.WindowUtils;
/**
 * Transparent Window for overlaying video canvas.
 * 
 * @author Vikker
 *
 */
public class OverlayWindow extends Window {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OverlayWindow(Window owner) {
		super(owner, WindowUtils.getAlphaCompatibleGraphicsConfiguration());
		setBackground(new Color(0, 0, 0, 0));
	}

	@Override
	public void paint(Graphics g) {
		super.repaint();
		super.paint(g);
	}
}
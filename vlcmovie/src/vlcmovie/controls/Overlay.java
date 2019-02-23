package vlcmovie.controls;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Window;
import com.sun.jna.platform.WindowUtils;

public class Overlay extends Window {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Overlay(Window owner) {
		super(owner, WindowUtils.getAlphaCompatibleGraphicsConfiguration());
		setBackground(new Color(0, 0, 0, 0));
	}

	@Override
	public void paint(Graphics g) {
		super.repaint();
		super.paint(g);
	}
}
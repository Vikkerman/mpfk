package mpfk.controls;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;

public class OverlayPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	public OverlayPanel() {
		setBackground(new Color(0, 0, 0, 0));
	}

	@Override
	public void paint(Graphics g) {
		super.repaint();
		super.paint(g);
	}
}
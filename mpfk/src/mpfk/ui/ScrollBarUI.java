package mpfk.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicScrollBarUI;
/**
 * Custom ScrollBar
 * 
 * @author Vikker
 *
 */
public class ScrollBarUI extends BasicScrollBarUI {
	@Override
	protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
		c.setBackground(Color.BLACK);
	}
}
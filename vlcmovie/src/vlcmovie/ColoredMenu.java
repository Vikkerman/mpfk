package vlcmovie;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JMenu;

/**
 * Coloring the JMenu
 * @author Vikker
 *
 */
public class ColoredMenu extends JMenu {
	private static final long serialVersionUID = 1L;
	Color bgColor = Color.WHITE;
	Color textColor = Color.WHITE;
	Font font = new Font("Arial", Font.BOLD, 12);
	String s = "x";

    public ColoredMenu(String string) {
    	this.setText(string);
	}
    
	public void setColor(Color color) {
        bgColor = color;
    }
	
	public void setTextColor(Color color) {
        textColor = color;
    }
	
	public void setFont(Font fontThis) {
		font = fontThis;
    }
	
	public void setChar(String string) {
		s = string;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(bgColor);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.setColor(textColor);
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        int x = getWidth()/2 - fm.stringWidth(s)/2;
        int y = fm.getHeight()-1;
        g2d.drawString(s, x, y);
    }
}
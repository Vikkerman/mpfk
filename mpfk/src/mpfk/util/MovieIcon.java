package mpfk.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingConstants;

/**
 * 
 * @author Vikker
 *
 */
public class MovieIcon extends JButton {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MovieIcon(String name, int index, ImageIcon img) {
		setPreferredSize(new Dimension(150, 150));
		setMinimumSize(new Dimension(150, 150));
		setMaximumSize(new Dimension(150, 150));
		setBackground(new Color(0, 0, 0, 5));
		setBorderPainted(false);
		setContentAreaFilled(false);
		setFocusPainted(false);
		setName("" + index);
		setHorizontalAlignment(SwingConstants.CENTER);
		setHorizontalTextPosition(SwingConstants.CENTER);

		Image image = img.getImage();
		int imageW = img.getIconWidth();
		int imageH = img.getIconHeight();
		int scale = 0;
		if (imageW > imageH) {
			scale = Math.round((140 * imageH) / imageW);
			Image newimg = image.getScaledInstance(140, scale, java.awt.Image.SCALE_SMOOTH);
			img = new ImageIcon(newimg);
		} else {
			scale = Math.round((140 * imageW) / imageH);
			Image newimg = image.getScaledInstance(scale, 140, java.awt.Image.SCALE_SMOOTH);
			img = new ImageIcon(newimg);
		}

		setIcon(img);
	}

	public void focusOn() {
		this.setBackground(new Color(255, 255, 255));
		this.setContentAreaFilled(true);
		repaint();
	}

	public void focusOff() {
		this.setBackground(new Color(0, 0, 0));
		this.setContentAreaFilled(false);
		repaint();
	}

	public void setImage(File snapshotFile) {
		try {
			ImageIcon img = new ImageIcon(ImageIO.read(snapshotFile));
			Image image = img.getImage();
			int imageW = img.getIconWidth();
			int imageH = img.getIconHeight();
			int scale = 0;
			if (imageW > imageH) {
				scale = Math.round((140 * imageH) / imageW);
				Image newimg = image.getScaledInstance(140, scale, java.awt.Image.SCALE_SMOOTH);
				img = new ImageIcon(newimg);
			} else {
				scale = Math.round((140 * imageW) / imageH);
				Image newimg = image.getScaledInstance(scale, 140, java.awt.Image.SCALE_SMOOTH);
				img = new ImageIcon(newimg);
			}

			setIcon(img);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

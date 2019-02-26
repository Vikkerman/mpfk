package mpfk.controls;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JButton;
/**
 * Transparent JButton class where can change the button icons on clicking.
 * Something like JToggleButton, but it's working backwards.
 * 
 * @author Vikker
 *
 */
public class TransparentButton extends JButton {
	private static final long serialVersionUID = 1L;

	private ImageIcon icoA;
	private ImageIcon icoP;
	private int index = 0;

	public TransparentButton() {
		this(null, null);
	}

	public TransparentButton(ImageIcon ico) {
		this(ico, null);
	}

	public TransparentButton(ImageIcon icoDeselected, ImageIcon icoSelected) {
		setBackground(new Color(0, 0, 0, 5));
		setContentAreaFilled(false);
		setPreferredSize(new Dimension(50, 50));
		setMaximumSize(new Dimension(50, 50));
		setMinimumSize(new Dimension(50, 50));
		setFocusPainted(false);

		icoA = icoDeselected;
		icoP = icoSelected;

		setOn();
		setIcon(icoSelected);
	}
	
	public TransparentButton(ImageIcon icoDeselected, ImageIcon icoSelected, int width) {
		this(icoDeselected, icoSelected, width, 0);
	}

	public TransparentButton(ImageIcon icoDeselected, ImageIcon icoSelected, int width, int indX) {
		index = indX;
		setBackground(new Color(0, 0, 0, 5));
		setContentAreaFilled(false);
		setPreferredSize(new Dimension(width, 50));
		setMaximumSize(new Dimension(width, 50));
		setMinimumSize(new Dimension(width, 50));
		setFocusPainted(false);

		icoA = icoDeselected;
		icoP = icoSelected;

		Image image = icoA.getImage();
		Image newimg = image.getScaledInstance(width, 50, java.awt.Image.SCALE_SMOOTH);
		icoA = new ImageIcon(newimg);

		image = icoP.getImage();
		newimg = image.getScaledInstance(width, 50, java.awt.Image.SCALE_SMOOTH);
		icoP = new ImageIcon(newimg);

		setOn();
		setIcon(icoSelected);
	}

	public int getIndex() {
		return index;
	}

	public void setOn() {
		setIcon(icoA);
	}

	public void setOff() {
		setIcon(icoP);
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g.create();

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		g2d.setBackground(new Color(0, 0, 0, 5));
		g2d.clearRect(0, 0, (int) screenSize.getWidth(), (int) screenSize.getHeight());

		super.paint(g2d);
	}
}
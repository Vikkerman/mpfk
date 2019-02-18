package vlcmovie;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JToggleButton;

public class TransparentToggleButton extends JToggleButton {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ImageIcon icoA;
	private ImageIcon icoP;
	private boolean state = false;

	public TransparentToggleButton() {
		this(null, null);
	}

	public TransparentToggleButton(ImageIcon ico) {
		this(ico, null);
	}

	public TransparentToggleButton(ImageIcon icoDeselected, ImageIcon icoSelected) {
		setBackground(new Color(0, 0, 0, 5));
		setContentAreaFilled(false);
		setPreferredSize(new Dimension(50, 50));
		setMaximumSize(new Dimension(50, 50));
		setMinimumSize(new Dimension(50, 50));
		setFocusPainted(false);

		icoA = icoDeselected;
		icoP = icoSelected;

		setUp();
		setIcon(icoSelected);
		setSelectedIcon(icoDeselected);
	}
	
	public TransparentToggleButton(ImageIcon icoDeselected, ImageIcon icoSelected, int width) {
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
		
		setUp();
		setIcon(icoSelected);
		setSelectedIcon(icoDeselected);
	}

	public void setUp() {
		setIcon(icoA);
		setSelectedIcon(icoP);
	}

	public void switchIco() {
		ImageIcon imc = icoA;
		icoA = icoP;
		icoP = imc;
		state = !state;
		setUp();
	}
	
	public void setOn() {
		setIcon(icoA);
		setSelectedIcon(icoP);
	}
	
	public void setOff() {
		setIcon(icoP);
		setSelectedIcon(icoA);
	}

	public boolean getState() {
		return state;
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g.create();

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		g2d.setBackground(new Color(0, 0, 0, 0));
		g2d.clearRect(0, 0, (int) screenSize.getWidth(), (int) screenSize.getHeight());
		
		super.paint(g2d);
	}
}
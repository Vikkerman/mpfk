package vlcmovie;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class TransparentButton extends JButton {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ImageIcon icoA;
	private ImageIcon icoP;
	private boolean state = false;
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

		setUp();
		setIcon(icoSelected);
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
		
		setUp();
		setIcon(icoSelected);
	}
	
	public int getIndex() {
		return index;
	}

	public void setUp() {
		setIcon(icoA);
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
	}
	
	public void setOff() {
		setIcon(icoP);
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
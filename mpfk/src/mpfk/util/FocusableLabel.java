package mpfk.util;

import javax.swing.JLabel;

public class FocusableLabel extends JLabel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String text = "";

	public FocusableLabel(String textString) {
		text = textString;
		setText(textString);
	}
	
	public void focusOn() {
		this.setText("<html><b>" + text + "</b></html>");
	}

	public void focusOff() {
		this.setText(text);
	}

}

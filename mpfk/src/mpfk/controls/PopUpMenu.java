package mpfk.controls;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import mpfk.createGUI;
/**
 * Simple JPopupMenu
 * 
 * @author Vikker
 *
 */
public class PopUpMenu extends JPopupMenu {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	JMenuItem clearItem, settingsItem;

	public PopUpMenu() {
		settingsItem = new JMenuItem("Settings");
		settingsItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				createGUI.reloadMoviList();
			}
		});

		clearItem = new JMenuItem("Clear playlist");
		clearItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				createGUI.clearPlayList();
			}
		});

		add(settingsItem);
		add(clearItem);
	}
}

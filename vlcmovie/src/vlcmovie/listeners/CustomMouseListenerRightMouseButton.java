package vlcmovie.listeners;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.SwingUtilities;

import vlcmovie.controls.PopUpMenu;

public class CustomMouseListenerRightMouseButton  implements MouseListener {

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (SwingUtilities.isRightMouseButton(e)) {
			// Presser Gabor
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (SwingUtilities.isRightMouseButton(e)) {
			PopUpMenu pm = new PopUpMenu();
			pm.show(e.getComponent(), e.getX(), e.getY());
		}
	}
}

package vlcmovie;

public class vlcmovie {
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new createGUI();
			}
		});
	}
}
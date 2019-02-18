package vlcmovie;

import java.awt.KeyEventDispatcher;
import java.awt.event.KeyEvent;

public class CustomKeyListener implements KeyEventDispatcher {
        @Override
        public boolean dispatchKeyEvent(KeyEvent e) {
        	if(e.getID() == KeyEvent.KEY_RELEASED) return false;
        	int key = e.getKeyCode();

		    if (key == KeyEvent.VK_LEFT) {
		    	if (createGUI.volume >= 10) {
		    		createGUI.volume -= 10;
		    	}
		    	createGUI.emp.setVolume(createGUI.volume);
		    	MouseMotionTimer.resetTimer();
		    }

		    if (key == KeyEvent.VK_RIGHT) {
		    	if (createGUI.volume <= 90) {
		    		createGUI.volume += 10;
		    	}
		    	createGUI.emp.setVolume(createGUI.volume);
		    	MouseMotionTimer.resetTimer();
		    }
		    
		    if (key == 77 || key == 109) {
		    	if (createGUI.emp.getVolume() == 0) {
		    		createGUI.emp.setVolume(createGUI.volume);
		    	} else {
		    		createGUI.emp.setVolume(0);
		    	}
		    	MouseMotionTimer.resetTimer();
		    }

		    if (key == KeyEvent.VK_DOWN) {
		    	if (createGUI.currentMovie < createGUI.listOfMine.size() - 1) {
		    		createGUI.currentMovie++;
		    	} else {
		    		createGUI.currentMovie = 0;
		    	}
		    	createGUI.labels.get(createGUI.previousMovie).focusOff();
		    	createGUI.labels.get(createGUI.currentMovie).focusOn();
		    	createGUI.previousMovie = createGUI.currentMovie;
		    	
				String fileString = createGUI.listOfMine.get(createGUI.currentMovie).getAbsolutePath();
				createGUI.playFile(fileString);
				
				int yCoor = createGUI.searchPanel.getComponent(createGUI.currentMovie * 2).getY() - 30;
//				yCoor += createGUI.searchPanel.getBounds().height/2;
//				System.out.println("le: " + yCoor);
				createGUI.searchScrollPane.getViewport().setViewPosition(new java.awt.Point(0, yCoor));
				MouseMotionTimer.resetTimer();
		    }

		    if (key == KeyEvent.VK_UP) {
		    	if (createGUI.currentMovie > 1) {
		    		createGUI.currentMovie--;
		    	} else {
		    		createGUI.currentMovie = createGUI.listOfMine.size() - 1;
		    	}
		    	createGUI.labels.get(createGUI.previousMovie).focusOff();
		    	createGUI.labels.get(createGUI.currentMovie).focusOn();
		    	createGUI.previousMovie = createGUI.currentMovie;
		    	
				String fileString = createGUI.listOfMine.get(createGUI.currentMovie).getAbsolutePath();
				createGUI.playFile(fileString);

				int yCoor = createGUI.searchPanel.getComponent(createGUI.currentMovie * 2).getY() - 30;
//				System.out.println("fel: " + yCoor);
				createGUI.searchScrollPane.getViewport().setViewPosition(new java.awt.Point(0, yCoor));
				MouseMotionTimer.resetTimer();
		    }
            return false;
        }
    }

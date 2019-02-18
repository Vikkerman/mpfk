package vlcmovie;

import java.io.File;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;

/**
 * A simple application to generate a single thumbnail image from a media file.
 * <p>
 * This application shows how to implement the "vlc-thumb" sample (written in "C") that is part of
 * the vlc code-base.
 * <p>
 * The original "C" implementation is available in the vlc code-base here:
 *
 * <pre>
 *   /vlc/doc/libvlc/vlc-thumb.c
 * </pre>
 *
 * This implementation tries to stick as closely to the original "C" implementation as possible, but
 * uses a different synchronisation technique.
 * <p>
 * Since this is a test, the implementation is not very tolerant to errors.
 */
public class Snapshots {

    private static final String[] VLC_ARGS = {
        "--intf", "dummy",          /* no interface */
        "--vout", "dummy",          /* we don't want video (output) */
        "--no-audio",               /* we don't want audio (decoding) */
        "--no-snapshot-preview",    /* no blending in dummy vout */
    };

    private static final float VLC_THUMBNAIL_POSITION = 30.0f / 100.0f;

    public Snapshots(List<File> listOfMine) throws Exception {
    	
    	for (int i = 0; i < listOfMine.size(); i++) {
    		String fileString = listOfMine.get(i).getAbsolutePath().toString();
	        String mrl = fileString;
	        fileString = fileString.replaceAll("\\\\", "_").replaceAll(":", "");
	        System.out.println(fileString);
	        
	        int imageWidth = Integer.parseInt("150");
	        File snapshotFile = new File(System.getProperty("user.dir") + "\\covers\\(" + fileString + ").png");
	        if (!snapshotFile.exists()) {
		        snapshotFile.getParentFile().mkdirs();
		
		        MediaPlayerFactory factory = new MediaPlayerFactory(VLC_ARGS);
		        MediaPlayer mediaPlayer = factory.newEmbeddedMediaPlayer();
		
		        final CountDownLatch inPositionLatch = new CountDownLatch(1);
		        final CountDownLatch snapshotTakenLatch = new CountDownLatch(1);
		
		        mediaPlayer.addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
		
		            @Override
		            public void positionChanged(MediaPlayer mediaPlayer, float newPosition) {
		                if(newPosition >= VLC_THUMBNAIL_POSITION * 0.9f) { /* 90% margin */
		                    inPositionLatch.countDown();
		                }
		            }
		
		            @Override
		            public void snapshotTaken(MediaPlayer mediaPlayer, String filename) {
		                System.out.println("snapshotTaken(filename=" + filename + ")");
		                snapshotTakenLatch.countDown();
		            }
		            
		        });
		
		        if (mediaPlayer.startMedia(mrl)) {
		            mediaPlayer.setPosition(VLC_THUMBNAIL_POSITION);
		            inPositionLatch.await(); // Might wait forever if error
		
		            mediaPlayer.saveSnapshot(snapshotFile, imageWidth, 0);
		            snapshotTakenLatch.await(); // Might wait forever if error
		            
		            createGUI.labels.get(i).setImage(snapshotFile);
		
		            mediaPlayer.pause();
		            mediaPlayer.stop();
		        }
		
		        mediaPlayer.release();
		        factory.release();
	        }
        }
    }
}

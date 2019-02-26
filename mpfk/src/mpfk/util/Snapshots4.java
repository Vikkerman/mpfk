package mpfk.util;

import java.io.File;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import mpfk.createGUI;
//import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
//import uk.co.caprica.vlcj.player.base.MediaPlayer;
//import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
/**
 * Snapshot taker class
 * 
 * based on:
 * https://github.com/caprica/vlcj/blob/master/src/test/java/uk/co/caprica/vlcj/test/thumbs/ThumbsTest.java
 */
public class Snapshots4 {

	private static final String[] VLC_ARGS = { "--intf", "dummy", /* no interface */
			"--vout", "dummy", /* we don't want video (output) */
			"--no-audio", /* we don't want audio (decoding) */
			"--no-snapshot-preview", /* no blending in dummy vout */
	};
	
	private static final float VLC_THUMBNAIL_POSITION = 30.0f / 100.0f;

	public Snapshots4(List<File> listOfMine) throws Exception {

		for (int i = 0; i < listOfMine.size(); i++) {
			String fileString = listOfMine.get(i).getAbsolutePath().toString();
			String mrl = fileString;
			fileString = fileString.replaceAll("\\\\", "_").replaceAll(":", "");

			int imageWidth = Integer.parseInt("150");
			File snapshotFile = new File(System.getProperty("user.dir") + "\\covers\\(" + fileString + ").png");
			if (!snapshotFile.exists()) {
				snapshotFile.getParentFile().mkdirs();

//				MediaPlayerFactory factory = new MediaPlayerFactory(VLC_ARGS);
//				MediaPlayer mediaPlayer = factory.mediaPlayers().newEmbeddedMediaPlayer();

				final CountDownLatch inPositionLatch = new CountDownLatch(1);
				final CountDownLatch snapshotTakenLatch = new CountDownLatch(1);

//				mediaPlayer.events().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {

//					@Override
//					public void positionChanged(MediaPlayer mediaPlayer, float newPosition) {
//						if (newPosition >= VLC_THUMBNAIL_POSITION * 0.9f) { /* 90% margin */
//							inPositionLatch.countDown();
//						}
//					}

//					@Override
//					public void snapshotTaken(MediaPlayer mediaPlayer, String filename) {
//						System.out.println("snapshotTaken(filename=" + filename + ")");
//						snapshotTakenLatch.countDown();
//					}

//				});

/*				if (mediaPlayer.media().start(mrl)) {
					mediaPlayer.controls().setPosition(VLC_THUMBNAIL_POSITION);
					inPositionLatch.await(); // Might wait forever if error

					mediaPlayer.snapshots().save(snapshotFile, imageWidth, 0);
					snapshotTakenLatch.await(); // Might wait forever if error

					createGUI.labels.get(i).setImage(snapshotFile);

					mediaPlayer.controls().pause();
					mediaPlayer.controls().stop();
				}

				mediaPlayer.release();
				factory.release();*/
			}
		}
	}
}

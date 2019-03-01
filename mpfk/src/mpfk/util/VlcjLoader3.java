package mpfk.util;

import java.awt.Canvas;
import java.io.File;

import javax.swing.JFrame;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;

import mpfk.createGUI;
import mpfk.controls.OverlayWindow;
import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.windows.Win32FullScreenStrategy;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;
import uk.co.caprica.vlcj.runtime.windows.WindowsRuntimeUtil;

public class VlcjLoader3 {
	public EmbeddedMediaPlayer embeddedMediaPlayer = null;

	private static final String FILESEPARATOR = File.separator;
	
	private static final String[] VLC_ARGS = { "--video-filter=deinterlace" };// --direct3d11-hw-blending, --vout=gl
	private static boolean setted = false;
	
	public  VlcjLoader3() {
		setted = false;
	}
	
	public void setUp(JFrame jFrame, Canvas canvas) {
		setted = true;
		loadLIBvlc();
		
		MediaPlayerFactory mediaPlayerFactory = new MediaPlayerFactory(VLC_ARGS);
		
		embeddedMediaPlayer = mediaPlayerFactory.newEmbeddedMediaPlayer(new Win32FullScreenStrategy(jFrame));
		embeddedMediaPlayer.setVideoSurface(mediaPlayerFactory.newVideoSurface(canvas));
//		embeddedMediaPlayer.toggleFullScreen();
		embeddedMediaPlayer.setEnableMouseInputHandling(false);
		embeddedMediaPlayer.setEnableKeyInputHandling(false);
	}
	
	public void setEventListener() {
		embeddedMediaPlayer.addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
			@Override
			public void muted(MediaPlayer mediaPlayer, boolean muted) {
				createGUI.overlay.setVolumeButtons();
			}

			@Override
			public void volumeChanged(MediaPlayer mediaPlayer, float volume) {
				createGUI.overlay.setVolumeButtons();
			}
		});
	}
	
	@SuppressWarnings("deprecation")
	private void loadLIBvlc() {
		if (RuntimeUtil.isWindows()) {
			NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(),WindowsRuntimeUtil.getVlcInstallDir());
		} else {
			NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(),"C:" + FILESEPARATOR + "Program Files" + FILESEPARATOR + "VideoLAN" + FILESEPARATOR + "VLC");
		}
		Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
	}
	
	public boolean isSetted() {
		return setted;
	}
	
	public void prepare(String fileString) {
		embeddedMediaPlayer.prepareMedia(fileString);
	}
	
	public void play() {
		embeddedMediaPlayer.play();
	}
	
	public void stop() {
		embeddedMediaPlayer.stop();
	}
	
	public void pause() {
		embeddedMediaPlayer.pause();
	}
	
	public void setVolume(int newVolume) {
		embeddedMediaPlayer.setVolume(newVolume);
	}
	
	public int getVolume() {
		return embeddedMediaPlayer.getVolume();
	}
	
	public void setOverlay(OverlayWindow overlayWindow) {
		embeddedMediaPlayer.setOverlay(overlayWindow);
		embeddedMediaPlayer.enableOverlay(true);
	}
	
	public boolean isSeekable() {
		return embeddedMediaPlayer.isSeekable();
	}
	
	public boolean isPlaying() {
		return embeddedMediaPlayer.isPlaying();
	}
	
	public void setPosition(float value) {
		if (isSeekable()) {
			embeddedMediaPlayer.setPosition(value);
		}
	}
	
	public float getPosition() {
		return embeddedMediaPlayer.getPosition();
	}

	public void increaseVolume() {
		int volume = getVolume();
		if (getVolume() <= 90) {
			setVolume(volume + 10);
		}
	}
	
	public void decreaseVolume() {
		int volume = getVolume();
		if (getVolume() >= 10) {
			setVolume(volume - 10);
		}
	}

	public void release() {
		embeddedMediaPlayer.release();
	}
	
}

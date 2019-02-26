package mpfk.util;

import java.awt.Canvas;
import javax.swing.JFrame;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;

import mpfk.createGUI;
import mpfk.controls.OverlayWindow;
import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.binding.RuntimeUtil;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.fullscreen.windows.Win32FullScreenStrategy;

public class VlcjLoader4 {	
	public EmbeddedMediaPlayer embeddedMediaPlayer = null;
	
	private static final String[] VLC_ARGS = { "--video-filter=deinterlace" };// --direct3d11-hw-blending, --vout=gl
	
	public VlcjLoader4(JFrame jFrame, Canvas canvas) {
		loadLIBvlc();
		
		MediaPlayerFactory mediaPlayerFactory = new MediaPlayerFactory(VLC_ARGS);
		
		embeddedMediaPlayer = mediaPlayerFactory.mediaPlayers().newEmbeddedMediaPlayer();
		embeddedMediaPlayer.fullScreen().strategy(new Win32FullScreenStrategy(jFrame));
		embeddedMediaPlayer.videoSurface().set(mediaPlayerFactory.videoSurfaces().newVideoSurface(canvas));
//		embeddedMediaPlayer.toggleFullScreen();
		embeddedMediaPlayer.input().enableMouseInputHandling(false);
		embeddedMediaPlayer.input().enableKeyInputHandling(false);
	}
	
	public void setEventListener() {
		embeddedMediaPlayer.events().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
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
	
	private void loadLIBvlc() {
		if (RuntimeUtil.isWindows()) {
			NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), Advapi32Util
					.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\VideoLAN\\VLC", "InstallDir"));
		} else if (RuntimeUtil.isMac()) {
			NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), "/Applications/VLC.app/Contents/MacOS/lib");
		} else if (RuntimeUtil.isNix()) { // later
			String[] DIRECTORIES = { 
					"/usr/lib/x86_64-linux-gnu",
					"/usr/lib64",
					"/usr/local/lib64",
					"/usr/lib/i386-linux-gnu",
					"/usr/lib",
					"/usr/local/lib" };
			NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), DIRECTORIES[0]);
		}
		Native.load(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
	}
	
	public void prepare(String fileString) {
		embeddedMediaPlayer.media().prepare(fileString);
	}
	
	public void play() {
		embeddedMediaPlayer.controls().play();
	}
	
	public void stop() {
		embeddedMediaPlayer.controls().stop();
	}
	
	public void pause() {
		embeddedMediaPlayer.controls().pause();
	}
	
	public void setVolume(int newVolume) {
		embeddedMediaPlayer.audio().setVolume(newVolume);
	}
	
	public int getVolume() {
		return embeddedMediaPlayer.audio().volume();
	}
	
	public void setOverlay(OverlayWindow overlayWindow) {
		embeddedMediaPlayer.overlay().set(overlayWindow);
		embeddedMediaPlayer.overlay().enable(true);
	}
	
	public boolean isSeekable() {
		return embeddedMediaPlayer.status().isSeekable();
	}
	
	public boolean isPlaying() {
		return embeddedMediaPlayer.status().isPlaying();
	}
	
	public void setPosition(float value) {
		embeddedMediaPlayer.controls().setPosition(value);
	}
	
	public float getPosition() {
		return embeddedMediaPlayer.status().position();
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
	
}

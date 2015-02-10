/*	Music Service singleton
 * 
 */
package pilestudios.musicplayer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.parse.ParseObject;

import pilestudios.really.R;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class MusicService implements OnCompletionListener,
		OnAudioFocusChangeListener {
	// needed variable
	private static MusicService mInstance = null;
	private static AudioManager mAudioManager;
	MediaPlayer mp;
	int duration = 0;
	String currentSong;
	boolean onRepeat = false;
	public boolean isPlaying = false;
	String songTitle;
	ParseObject songObject;

	public MusicService(Context context) {
		// controls the focus of the music
		mAudioManager = (AudioManager) context
				.getSystemService(context.AUDIO_SERVICE);
		mAudioManager.requestAudioFocus(MusicService.mInstance,
				AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
	}

	// returns the music instance
	public static synchronized MusicService getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new MusicService(context);
		}

		return mInstance;

	}

	// sets the song object
	public void setSetObject(ParseObject object) {
		songObject = object;
	}

	// returns the song object
	public ParseObject getSongObject() {
		return songObject;
	}

	// stops the song
	public void stopSong() {
		isPlaying = false;
		mp.stop();
		mp.release();
	}

	// sets the song
	public void setSong(String song, String songTitle, boolean Repeat,
			int length) {
		mp = new MediaPlayer();
		mp.setOnCompletionListener(this);

		onRepeat = Repeat;
		this.songTitle = songTitle;
		duration = length;
		currentSong = song;
		isPlaying = true;
		playSong();
	}

	// returns the current music
	public String getCurrentMusic() {
		return currentSong;
	}

	// puts in play all the data needed to play song successfully
	public void playSong(String songPath, String songTitle, boolean Repeat) {
		mp = new MediaPlayer();

		onRepeat = Repeat;
		this.songTitle = songTitle;
		currentSong = songPath;
		isPlaying = true;
		playSong();
	}

	// sets the current song
	public void setCurrentSong(String song) {
		currentSong = song;
	}

	// sets up mediaplayer
	public void playSong() {
		try {

			mp.reset();
			mp.setDataSource(currentSong);
			mp.setAudioStreamType(AudioManager.STREAM_MUSIC);

			mp.prepareAsync();
			
			mp.setOnPreparedListener(new OnPreparedListener(){

				@Override
				public void onPrepared(MediaPlayer mp) {
					// TODO Auto-generated method stub
					mp.start();
					
				}
				
			});
			
			mp.setOnCompletionListener(this);

		} catch (IOException ex) {
			String s = ex.toString();
			ex.printStackTrace();
		}
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub
		if (onRepeat == true) {
			duration = 0;
			playSong();
		} else {
			mp.release();
			isPlaying = false;
			// finish();

			System.exit(0);
		}
	}
	

	@Override
	public void onAudioFocusChange(int focusChange) {
		// TODO Auto-generated method stub
		if (focusChange <= 0) {
			// LOSS -> PAUSE
			if (isPlaying != false) {
				mp.pause();
			}
		} else {
			// GAIN -> PLAY
			// mp.start();
			if (isPlaying != false) {
				mp.start();
			}
		}
	}

}

package pilestudios.musicplayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import com.parse.ParseObject;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.util.Log;
import android.webkit.WebChromeClient.CustomViewCallback;
import android.widget.Toast;

public class playlistService implements OnCompletionListener {
	//required variables
	private static playlistService mInstance = null;
	private String selectedPlaylist;
	private ArrayList<Playlist> playlistContent;
	ArrayList<Playlist> usedRandomNumbers;

	private Playlist selectedSong;
	int selectedIndex;
	int randomIndex;
	String songTitle;

	public boolean isPlaylistPlaying = false;
	boolean isRepeat = false;
	boolean isShuffle = false;
	ParseObject songObject;
	private boolean playRandom;

	static MediaPlayer mp;

	public static synchronized playlistService getInstance() {
		if (mInstance == null) {
			mInstance = new playlistService();
		}

		return mInstance;

	}

	// stops the song
	public void stopSong() {
		isPlaylistPlaying = false;
		mp.stop();
		mp.release();
		mp = new MediaPlayer();
	}

	//sets the song object
	public void setSongObject(ParseObject object) {
		songObject = object;
	}

	//returns the song object
	public ParseObject getSongObject() {
		return songObject;
	}

	//gets randomSongs
	public ArrayList<Playlist> getRandomSongs() {
		usedRandomNumbers = new ArrayList();
		usedRandomNumbers.addAll(getPlaylistContent());
		Collections.shuffle(usedRandomNumbers);
		return usedRandomNumbers;
	}

	public void playSong(int i, boolean repeat, boolean shuffle, String songName) {

		// usedRandomNumbers = randomSongs;

		randomIndex = i;
		selectedIndex = i;
		mp = new MediaPlayer();
		songTitle = songName;
		isRepeat = repeat;
		isShuffle = shuffle;
		isPlaylistPlaying = true;
		mp.reset();

		try {
			mp.setDataSource(playlistContent.get(i).getSongUrl());
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		mp.setAudioStreamType(AudioManager.STREAM_MUSIC);

		mp.prepareAsync();

		mp.setOnPreparedListener(new OnPreparedListener() {

			@Override
			public void onPrepared(MediaPlayer mp) {
				// TODO Auto-generated method stub
				mp.start();

			}

		});

		mp.setOnCompletionListener(this);

	}

	public void setPlaylist(String name) {
		selectedPlaylist = name;
	}

	public String getSelectedPlaylist() {
		return selectedPlaylist;
	}

	public void setSelectedIndex(int index) {
		selectedIndex = index;
	}

	public int getSelectedIndex() {
		return selectedIndex;
	}

	public void setPlaylistContent(ArrayList<Playlist> content) {
		playlistContent = content;
	}

	public ArrayList<Playlist> getPlaylistContent() {
		return playlistContent;
	}

	public void setSelectedSong(Playlist song) {
		selectedSong = song;
	}

	public Playlist getSelectedSong() {
		return selectedSong;
	}

	@Override
	public void onCompletion(MediaPlayer arg0) {
		// TODO Auto-generated method stub
		if (selectedIndex < (getPlaylistContent().size() - 1)) {
			// important
			stopSong();

			playSong(selectedIndex, isRepeat, isShuffle, getPlaylistContent()
					.get(selectedIndex).getSongTitle());
			selectedIndex = selectedIndex + 1;
			// usedRandomNumbers.add(currentSongIndex);
		} else {

			stopSong();

			// play first song
			if (isRepeat == true) {
				playSong(0, isRepeat, isShuffle, getPlaylistContent().get(0)
						.getSongTitle());
				selectedIndex = 1;
			} else {
				mp.release();
			}

		}

	}
}

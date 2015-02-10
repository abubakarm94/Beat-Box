/* this activity plays the songs
 * 
 */
package pilestudios.musicplayer;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import pilestudios.really.R;
import pilestudios.really.startApplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MusicPlayer extends Activity implements OnCompletionListener,
		SeekBar.OnSeekBarChangeListener {

	// need variables
	private ImageButton btnPlay;
	private ImageButton btnForward;
	private ImageButton btnBackward;
	private ImageButton btnNext;
	private ImageButton btnPrevious;
	private ImageButton btnPlaylist;
	private Button btnRepeat;
	private Button btnShuffle;
	private SeekBar songProgressBar;
	private TextView songTitleLabel;
	private TextView songCurrentDurationLabel;
	private TextView songTotalDurationLabel;
	// Media Player
	private MediaPlayer mp;
	// Handler to update UI timer, progress bar etc,.
	private Handler mHandler = new Handler();;
	private Utilities utils;
	private int seekForwardTime = 5000; // 5000 milliseconds
	private int seekBackwardTime = 5000; // 5000 milliseconds
	private int currentSongIndex = 0;
	private boolean isShuffle = false;
	private boolean isRepeat = false;

	private boolean isPause = false;
	private String currentSong;
	private String songTitle;
	private String songlocation;
	private String songExtension;
	MusicService s;
	private TextView artistTitleLabel;
	private String artistName;
	private SharedPreferences preferences;
	private Editor editor;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

		setContentView(R.layout.activity_playerlayout);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.home_status);

		// All player buttons
		btnPlay = (ImageButton) findViewById(R.id.btnPlay);
		btnForward = (ImageButton) findViewById(R.id.btnForward);
		btnBackward = (ImageButton) findViewById(R.id.btnBackward);
		btnNext = (ImageButton) findViewById(R.id.btnNext);
		btnPrevious = (ImageButton) findViewById(R.id.btnPrevious);
		btnRepeat = (Button) findViewById(R.id.btnRepeat);
		btnShuffle = (Button) findViewById(R.id.btnShuffle);
		songProgressBar = (SeekBar) findViewById(R.id.songProgressBar);
		songTitleLabel = (TextView) findViewById(R.id.songTitle);
		songCurrentDurationLabel = (TextView) findViewById(R.id.songCurrentDurationLabel);
		songTotalDurationLabel = (TextView) findViewById(R.id.songTotalDurationLabel);
		artistTitleLabel = (TextView) findViewById(R.id.artistName);
		TextView backButton = (TextView) findViewById(R.id.statusbar_back);

		// go back when back button is clicked
		backButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();

			}

		});
		
		//stops the music coming from playlistMusicPlayer. if any
		playlistService listService = playlistService.getInstance();
		if(listService.isPlaylistPlaying == true){
		listService.stopSong();
		}

		// instantiates music service
		s = s.getInstance(this);

		// alert dialog for adding song to play list
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);

		// shared preferences that will contain the saved data
		preferences = getSharedPreferences("temp",
				getApplicationContext().MODE_PRIVATE);

		// starts an editor on preferences
		editor = preferences.edit();

		// sets the nowplaying text to add to
		TextView addButton = (TextView) findViewById(R.id.nowPlaying);
		addButton.setText("Add to");

		// the add button will allow songs to be added to playlist
		addButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// if a playlist has been creates
				if (preferences.contains("playlist_name")) {

					// list of playlistnames
					ArrayList<String> listNames = new ArrayList<String>(
							preferences.getStringSet("playlist_name", null));

					// start alert dialog of playlist names
					if (listNames != null) {
						final CharSequence[] items = listNames
								.toArray(new CharSequence[listNames.size()]);

						builder.setTitle("Select a playlist");
						builder.setItems(items,
								new DialogInterface.OnClickListener() {
								
									//needed variables
									private String selectedPlaylist;
									private ParseObject selectedSong;
									private Playlist playlist;

									public void onClick(DialogInterface dialog,
											int item) {
										
										//stores the selected playlist
										selectedPlaylist = items[item]
												.toString();
										
										
										//gets the object of the current song
										selectedSong = s.getSongObject();

										if (selectedSong != null) {

											//starts the playlist  parse object
											playlist = new Playlist();
											
											//saves the song to playlist parse object
											playlist.setSong(selectedSong);
											
											//saves the playlist name to playlist parse object
											playlist.setPlaylistName(selectedPlaylist);
											
											//saves the user  to playlist parse object
											playlist.setUser(ParseUser
													.getCurrentUser());
											
											//saves everything parse database online
											playlist.saveInBackground(new SaveCallback() {

												@Override
												public void done(
														ParseException arg0) {
													// TODO Auto-generated
													// method stub

													

												}

											});
										}
									}
								});
						AlertDialog alert = builder.create();
						alert.show();

					}
				} else {
					Toast.makeText(getApplicationContext(),
							"No playlist has been created", 3).show();
				}

			}

		});

		//instantiate the album art imageview
		ImageView albumArt = (ImageView) findViewById(R.id.songArt);

		//instantiate the utities
		utils = new Utilities();

		// starts onSeekbarChangeListener 
		songProgressBar.setOnSeekBarChangeListener(this); // Important

		
		//starts the global singleton
		startApplication helo = (startApplication) getApplication();

		// gets song
		currentSong = helo.getSong();

		//gets artist name
		artistName = helo.getArtistName();

		// getting song extension
		songExtension = helo.getSongExtension();
		//Toast.makeText(getApplicationContext(), songExtension, 6).show();

		// GETTING SONG TITLE
		songTitle = helo.getSongTitle();

		//gets albumart and converts it to drawable. and sets it imageview
		BitmapDrawable ob = new BitmapDrawable(getResources(),
				helo.getAlbumArt());
		albumArt.setBackground(ob);



		/**
		 * Play button click event plays a song and changes button to pause
		 * image pauses a song and changes button to play image
		 * */
		btnPlay.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// check for already playing
				if (mp.isPlaying()) {
					if (mp != null) {
						mp.pause();
						// Changing button image to play button
						isPause = true;
						btnPlay.setImageResource(R.drawable.ic_play);
					}
				} else {
					// Resume song
					if (mp != null) {
						mp.start();
						// Changing button image to pause button
						isPause = false;
						btnPlay.setImageResource(R.drawable.ic_pause);
					}
				}

			}
		});

		/**
		 * Forward button click event Forwards song specified seconds
		 * */
		btnForward.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// get current song position
				int currentPosition = mp.getCurrentPosition();
				// check if seekForward time is lesser than song duration
				if (currentPosition + seekForwardTime <= mp.getDuration()) {
					// forward song
					mp.seekTo(currentPosition + seekForwardTime);
				} else {
					// forward to end position
					mp.seekTo(mp.getDuration());
				}
			}
		});

		/**
		 * Backward button click event Backward song to specified seconds
		 * */
		btnBackward.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// get current song position
				int currentPosition = mp.getCurrentPosition();
				// check if seekBackward time is greater than 0 sec
				if (currentPosition - seekBackwardTime >= 0) {
					// forward song
					mp.seekTo(currentPosition - seekBackwardTime);
				} else {
					// backward to starting position
					mp.seekTo(0);
				}

			}
		});

		/**
		 * Next button click event Plays next song by taking currentSongIndex +
		 * 1
		 * */
		/*
		 * btnNext.setOnClickListener(new View.OnClickListener() {
		 * 
		 * @Override public void onClick(View arg0) { // check if next song is
		 * there or not if(currentSongIndex < (songsList.size() - 1)){
		 * playSong(currentSongIndex + 1); currentSongIndex = currentSongIndex +
		 * 1; }else{ // play first song playSong(0); currentSongIndex = 0; }
		 * 
		 * } });
		 */
		

		/**
		 * Back button click event Plays previous song by currentSongIndex - 1
		 * */
		/*
		 * btnPrevious.setOnClickListener(new View.OnClickListener() {
		 * 
		 * @Override public void onClick(View arg0) { if(currentSongIndex > 0){
		 * playSong(currentSongIndex - 1); currentSongIndex = currentSongIndex -
		 * 1; }else{ // play last song playSong(songsList.size() - 1);
		 * currentSongIndex = songsList.size() - 1; }
		 * 
		 * } });
		 */
		
		//sets the states of the repeat and shuffle button from the shared preferences
		setRepeatandShuffle();

		/**
		 * Button Click event for Repeat button Enables repeat flag to true
		 * */
		btnRepeat.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
			
				if (isRepeat) {
					isRepeat = false;
					Toast.makeText(getApplicationContext(), "Repeat is OFF",
							Toast.LENGTH_SHORT).show();
					btnRepeat.setBackground(getResources().getDrawable(
							R.drawable.green_button_pressed));
					btnRepeat.setTextColor(Color.parseColor("#FF3262"));
					editor.putBoolean("repeatState", isRepeat);
					editor.commit();

				} else {
					// make repeat to true
					isRepeat = true;
					Toast.makeText(getApplicationContext(), "Repeat is ON",
							Toast.LENGTH_SHORT).show();
					// make shuffle to false
					// isShuffle = false;
					btnRepeat.setBackground(getResources().getDrawable(
							R.drawable.green_button_depressed));
					btnRepeat.setTextColor(Color.parseColor("#ffffff"));
					editor.putBoolean("repeatState", isRepeat);
					editor.commit();

				}
			}
		});
		

		/**
		 * Button Click event for Shuffle button Enables shuffle flag to true
		 * */

		btnShuffle.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (isShuffle) {
					isShuffle = false;
					Toast.makeText(getApplicationContext(), "Shuffle is OFF",
							Toast.LENGTH_SHORT).show();
					btnShuffle.setBackground(getResources().getDrawable(
							R.drawable.green_button_pressed));
					btnShuffle.setTextColor(Color.parseColor("#FF3262"));
					editor.putBoolean("shuffleState", isShuffle);
					editor.commit();

				} else { // make repeat to true
					isShuffle = true;
					Toast.makeText(getApplicationContext(), "Shuffle is ON",
							Toast.LENGTH_SHORT).show(); // make shuffle to false
														// isRepeat =false;
					btnShuffle.setBackground(getResources().getDrawable(
							R.drawable.green_button_depressed));
					btnShuffle.setTextColor(Color.parseColor("#ffffff"));
					editor.putBoolean("shuffleState", isShuffle);
					editor.commit();

				}
			}
		});


	}

	// sets the state of the repeat and shuffle button
	private void setRepeatandShuffle() {
		if (preferences.contains("shuffleState")) {

		} else {
			editor.putBoolean("shuffleState", false);
			editor.commit();
		}

		if (preferences.contains("repeatState")) {

		} else {
			editor.putBoolean("shuffleState", false);
			editor.commit();
		}
		isRepeat = preferences.getBoolean("repeatState", false);
		isShuffle = preferences.getBoolean("shuffleState", false);

		if (isRepeat) {
			btnRepeat.setBackground(getResources().getDrawable(
					R.drawable.green_button_depressed));
			btnRepeat.setTextColor(Color.parseColor("#ffffff"));

		}

		if (isShuffle) {
			btnShuffle.setBackground(getResources().getDrawable(
					R.drawable.green_button_depressed));
			btnShuffle.setTextColor(Color.parseColor("#ffffff"));

		}

	}
	
	//if the selected song if already been played 
	private void continueSong() {
		// TODO Auto-generated method stub
		//sets the mediaplayer from this activity to the one from its service
		mp = s.mp;
		
		//change the media player
		btnPlay.setImageResource(R.drawable.ic_pause);
		
		//sets the song title and artist name
		songTitleLabel.setText(songTitle);
		artistTitleLabel.setText(artistName);
		
		//sets oncompletion listener to the mediaplaer
		//mp.setOnCompletionListener(this); // Important
		
	
		
		songProgressBar.setProgress(0);
		songProgressBar.setMax(100);
		// Updating progress bar
		updateProgressBar();

		
	}

	//when the back button is pressed
	public void onBackPressed() {
		// moveTaskToBack (true);

		//remove the seekbar runnable callbacks
		if(mp.isPlaying() == true){

		mHandler.removeCallbacks(mUpdateTimeTask); //must. its important

		//finish this activity
		finish();
		}else{
			Toast.makeText(getApplicationContext(), "Please wait until the song finishes loading", 3).show();

		}
	}

	// after the activity has finished displaying its contents
	@Override
	public void onStart() {
		super.onStart();
		if (s.isPlaying == false) {
			playMp3(currentSong, songTitle);
		} else {
			if (s.songTitle.equals(songTitle)) {
				continueSong();
				// s.stopSong();
			} else {
				s.stopSong();
				playMp3(currentSong, songTitle);

			}
		}

		if (s.mp.isPlaying() == false) {
			btnPlay.setImageResource(R.drawable.ic_play);

		}
	}
	
	
	//gets the album art. if the album art doesn't exist. set a preset on
	public Bitmap getArt(String path) {
		Bitmap songImage = null;
		MediaMetadataRetriever metaRetriver;
		metaRetriver = new MediaMetadataRetriever();
		metaRetriver.setDataSource(path);
		
		byte[] artBytes = metaRetriver.getEmbeddedPicture();
		
		if (artBytes != null) {
			Bitmap bitmap = BitmapFactory.decodeByteArray(artBytes, 0,
					artBytes.length);

			return bitmap;
		} else {

			Bitmap imgArt = BitmapFactory.decodeResource(
					getApplicationContext().getResources(),
					R.drawable.no_cover_art);
			return imgArt;
		}
	}

	/**
	 * Receiving song index from playlist view and play the song
	 * */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// if(resultCode == 100){
		// currentSongIndex = data.getExtras().getInt("songIndex");
		// play selected song
		// playSong(currentSongIndex);
		// }

	}

	
	//plays the song
	private void playMp3(String song, final String songTitle) {
		
		//starts preparing the song from music service
		s.playSong(currentSong, songTitle, isRepeat);
		
		//sets this mediaplayer to the media player from the music service
		mp = s.mp;

		//sets on prepared listener from the media player
		mp.setOnPreparedListener(new OnPreparedListener() {

			@Override
			public void onPrepared(MediaPlayer mp) {
				// TODO Auto-generated method stub
				
				//sets artist name to artist title label
				artistTitleLabel.setText(artistName);

				//sets the song title
				songTitleLabel.setText(songTitle);

				btnPlay.setImageResource(R.drawable.ic_pause);

				mp.start();
				// set Progress bar values
				songProgressBar.setProgress(0);
				songProgressBar.setMax(100);
				// Updating progress bar
				updateProgressBar();
				

			}

		});
		
		//sets on completion listener
	//	mp.setOnCompletionListener(this); // Important
		mp.setOnCompletionListener(this); 
	}

	/**
	 * Update timer on seekbar
	 * */
	public void updateProgressBar() {
		mHandler.postDelayed(mUpdateTimeTask, 100);
	}

	/**
	 * Background Runnable thread
	 * */
	private Runnable mUpdateTimeTask = new Runnable() {
		public void run() {
			long totalDuration = s.mp.getDuration();
			long currentDuration = s.mp.getCurrentPosition();

			// Displaying Total Duration time
			songTotalDurationLabel.setText(""
					+ utils.milliSecondsToTimer(totalDuration));
			// Displaying time completed playing
			songCurrentDurationLabel.setText(""
					+ utils.milliSecondsToTimer(currentDuration));

			// Updating progress bar
			int progress = (int) (utils.getProgressPercentage(currentDuration,
					totalDuration));
			// Log.d("Progress", ""+progress);
			songProgressBar.setProgress(progress);

			// Running this thread after 100 milliseconds
			mHandler.postDelayed(this, 100);
		}
	};

	/**
     *
     * */
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromTouch) {

	}

	/**
	 * When user starts moving the progress handler
	 * */
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// remove message Handler from updating progress bar
		mHandler.removeCallbacks(mUpdateTimeTask);
	}

	/**
	 * When user stops moving the progress hanlder
	 * */
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		mHandler.removeCallbacks(mUpdateTimeTask);
		int totalDuration = mp.getDuration();
		int currentPosition = utils.progressToTimer(seekBar.getProgress(),
				totalDuration);

		// forward or backward to certain seconds
		mp.seekTo(currentPosition);

		// update timer progress again
		updateProgressBar();
	}

	/**
	 * On Song Playing completed if repeat is ON play same song again if shuffle
	 * is ON play random song
	 * */
	@Override
	public void onCompletion(MediaPlayer arg0) {


		if (isRepeat == true) {
			Toast.makeText(getApplicationContext(), "repat", 4).show();
			mp.seekTo(0);
			mp.start();
			//playMp3(currentSong, songTitle);
		} else {
			mHandler.removeCallbacks(mUpdateTimeTask); //must. its important
s.stopSong();
			//mp.stop();
			mp.release();
			finish();
		}
		// go back to previous screen temparirly
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// mp.release();
	}

}
package pilestudios.musicplayer;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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

public class playlistMusicPlayer extends Activity implements
		OnCompletionListener, SeekBar.OnSeekBarChangeListener {

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
	// private SongsManager songManager;
	private Utilities utils;
	private int seekForwardTime = 5000; // 5000 milliseconds
	private int seekBackwardTime = 5000; // 5000 milliseconds
	private int currentSongIndex = 0;
	private boolean isShuffle = false;
	private boolean isRepeat = false;

	private boolean isPause = false;
	private String currentSong;
	private String songTitle;
	private FileDescriptor songlocation;
	private String songExtension;

	// allows to user to go back if the song is done loading
	private boolean canGoBack = false;
	// private ArrayList<HashMap<String, String>> songsList = new
	// ArrayList<HashMap<String, String>>();
	MusicService s;

	playlistService songService;

	ArrayList<Playlist> songsList;
	private ImageView albumArt;
	private ArrayList<Playlist> usedRandomNumbers;
	private TextView artistTitleLabel;
	private SharedPreferences preferences;
	private Editor editor;
	private int randomIndex;
	private boolean playRandom;

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
		artistTitleLabel = (TextView) findViewById(R.id.artistName);

		songCurrentDurationLabel = (TextView) findViewById(R.id.songCurrentDurationLabel);
		songTotalDurationLabel = (TextView) findViewById(R.id.songTotalDurationLabel);

		TextView back = (TextView) findViewById(R.id.statusbar_back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mHandler.removeCallbacks(mUpdateTimeTask); // must. its

				finish();

			}

		});

		s = s.getInstance(this);
		if (s.isPlaying == true) {
			Log.i("playlistMusicPlayer", "single player stopped");
			s.stopSong();
		}

		albumArt = (ImageView) findViewById(R.id.songArt);

		// handles playiing music on back pressed
		// s = s.getInstance(this);

		// Mediaplayer
		mp = new MediaPlayer();
		// mp.setOnCompletionListener(this);

		// playlist handler
		songService = songService.getInstance();

		usedRandomNumbers = new ArrayList<Playlist>();

		utils = new Utilities();

		// Listeners
		songProgressBar.setOnSeekBarChangeListener(this); // Important
		// mp.setOnCompletionListener(this); // Important

		// Getting all songs list
		// songsList = songManager.getPlayList();

		// By default play first song
		// playSong(0);

		// start song
		// Bundle extras = getIntent().getExtras();
		// byte[] currentSong = extras.getByteArray("song");
		// String songTitle = extras.getString("songTitle");
		// if(currentSong != null){
		//
		// Toast.makeText(getApplicationContext(), "we made it", 5).show();
		// }
		// start where we get the globals from
		// startApplication helo =(startApplication)getApplication();

		// gets songlist
		songsList = songService.getPlaylistContent();

		// GETTING song
		// currentSong = songService.getSelectedSong().getSongUrl();

		// getting song extension
		// songExtension = helo.getSongExtension();

		// sets play button to opposition if song is playing
		if (songService.mp != null) {
			if (songService.mp.isPlaying() == false) {
				btnPlay.setImageResource(R.drawable.ic_play);

			}
		}

		// shared preferences that will contain the saved data
		preferences = getSharedPreferences("temp",
				getApplicationContext().MODE_PRIVATE);

		// starts an editor on preferences
		editor = preferences.edit();

		usedRandomNumbers = new ArrayList<Playlist>();

		// sets the states of the repeat and shuffle button from the shared
		// preferences
		setRepeatandShuffle();

		// GETTING SONG TITLE
		// songTitle = songService.getSelectedSong().getSongTitle();
		currentSongIndex = songService.getSelectedIndex();
		// / setUpRandom();

		// if a song from playlist is already playing in the background
		if (songService.isPlaylistPlaying == true) {

			// mHandler.removeCallbacks(mUpdateTimeTask); // must. its
			// important
			// Toast.makeText(getApplicationContext(), "playlisti", 4).show();
			if (songsList.get(currentSongIndex).getSongTitle()
					.equals(songService.songTitle)) {
				continueSong(currentSongIndex);
				Toast.makeText(getApplicationContext(), "continue", 4).show();
			} else {
				// songService.stopSong();
				songService.mp.stop();
				playSong(currentSongIndex);
				Toast.makeText(getApplicationContext(), "don't continue", 4)
						.show();
			}

		} else { // if not song from playlist is playing in the backgroudn
					// if shuffle is enabled
			/*
			 * if(isShuffle == true){ //playRandomSongs(0); not this because i
			 * want the song that is clicked on to play first
			 * playSong(currentSongIndex);
			 * 
			 * }
			 */
			playSong(currentSongIndex);

		}

		TextView nowPlaying = (TextView) findViewById(R.id.nowPlaying);
		nowPlaying.setText("Add to");

		final AlertDialog.Builder builder = new AlertDialog.Builder(this);

		nowPlaying.setOnClickListener(new OnClickListener() {

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

									// needed variables
									private String selectedPlaylist;
									private ParseObject selectedSong;
									private Playlist playlist;

									public void onClick(DialogInterface dialog,
											int item) {

										// stores the selected playlist
										selectedPlaylist = items[item]
												.toString();

										// gets the object of the current song
										selectedSong = songService
												.getSongObject();

										if (selectedSong != null) {

											// starts the playlist parse object
											playlist = new Playlist();

											// saves the song to playlist parse
											// object
											playlist.setSong(selectedSong);

											// saves the playlist name to
											// playlist parse object
											playlist.setPlaylistName(selectedPlaylist);

											// saves the user to playlist parse
											// object
											playlist.setUser(ParseUser
													.getCurrentUser());

											// saves everything parse database
											// online
											playlist.saveInBackground(new SaveCallback() {

												@Override
												public void done(
														ParseException arg0) {
													// TODO Auto-generated
													// method stub
													Toast.makeText(
															getApplicationContext(),
															"saved", 4).show();

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

		// usedRandomNumbers.add(currentSongIndex);
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

		btnPrevious.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (currentSongIndex <= 0) {
					mHandler.removeCallbacks(mUpdateTimeTask); // must. its
					// important
					songService.mp.stop();
					finish();
				} else {
					mHandler.removeCallbacks(mUpdateTimeTask); // must. its
					// important
					songService.mp.stop();
					currentSongIndex = currentSongIndex - 1;
					playSong(currentSongIndex);
				}

			}
		});

		/**
		 * Next button click event Plays next song by taking currentSongIndex +
		 * 1
		 * */
		btnNext.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// check if next song is there or not
				// songService.stopSong();

				if (currentSongIndex < (songsList.size() - 1)) {
					mHandler.removeCallbacks(mUpdateTimeTask); // must. its
																// important
					songService.stopSong();

					playSong(currentSongIndex + 1);
					currentSongIndex = currentSongIndex + 1;
					// usedRandomNumbers.add(currentSongIndex);
				} else {
					mHandler.removeCallbacks(mUpdateTimeTask); // must. its
																// important
					songService.stopSong();

					// play first song
					if (isRepeat) {
						playSong(0);
						currentSongIndex = 0;
					} else {
						mHandler.removeCallbacks(mUpdateTimeTask); // must. its

						finish();
					}

					/*
					 * if (isShuffle != true) { if (currentSongIndex <
					 * (songsList.size() - 1)) {
					 * mHandler.removeCallbacks(mUpdateTimeTask); // must. its
					 * // important songService.stopSong();
					 * 
					 * playSong(currentSongIndex + 1); currentSongIndex =
					 * currentSongIndex + 1; //
					 * usedRandomNumbers.add(currentSongIndex); } else {
					 * mHandler.removeCallbacks(mUpdateTimeTask); // must. its
					 * // important songService.stopSong();
					 * 
					 * // play first song if (isRepeat) { playSong(0);
					 * currentSongIndex = 0; } else { finish(); } //
					 * usedRandomNumbers.clear(); //
					 * usedRandomNumbers.add(currentSongIndex);
					 * 
					 * }
					 */

					// Toast.makeText(getApplicationContext(), "true",
					// 3).show();

					/*
					 * if ((randomIndex) < (usedRandomNumbers.size())) {
					 * mHandler.removeCallbacks(mUpdateTimeTask); // must. //
					 * its// // important
					 * Toast.makeText(getApplicationContext(), "shuffle", 3)
					 * .show(); songService.stopSong();
					 * 
					 * // Toast.makeText(getApplicationContext(), //
					 * usedRandomNumbers.get(randomIndex).getSongTitle(), //
					 * 3).show(); playRandomSongs(randomIndex);
					 * 
					 * //playSong(randomIndex); randomIndex += 1;
					 * 
					 * } else { if (isRepeat) {
					 * mHandler.removeCallbacks(mUpdateTimeTask); // must.
					 * 
					 * songService.stopSong();
					 * 
					 * randomIndex = 0; playRandomSongs(randomIndex);
					 * 
					 * } else { mHandler.removeCallbacks(mUpdateTimeTask);
					 * songService.stopSong(); songService.usedRandomNumbers =
					 * null;
					 * 
					 * mp.release(); finish();
					 * 
					 * // mp.release();
					 * 
					 * } }
					 */

				}
			}
		});

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

				Toast.makeText(getApplicationContext(),
						"This feature will be available in the next update",
						Toast.LENGTH_LONG).show();
				// shuffle feature is coming out in the next version
				/*
				 * if (isShuffle) { isShuffle = false;
				 * Toast.makeText(getApplicationContext(), "Shuffle is OFF",
				 * Toast.LENGTH_SHORT).show();
				 * btnShuffle.setBackground(getResources().getDrawable(
				 * R.drawable.green_button_pressed));
				 * btnShuffle.setTextColor(Color.parseColor("#FF3262"));
				 * editor.putBoolean("shuffleState", isShuffle);
				 * editor.commit();
				 * 
				 * } else { // make repeat to true isShuffle = true;
				 * 
				 * usedRandomNumbers=(songService.getRandomSongs());
				 * Collections.shuffle(usedRandomNumbers); //currentSongIndex =
				 * 0; randomIndex = currentSongIndex;
				 * 
				 * Toast.makeText(getApplicationContext(), "Shuffle is ON",
				 * Toast.LENGTH_SHORT).show(); // make shuffle to false //
				 * isRepeat =false;
				 * btnShuffle.setBackground(getResources().getDrawable(
				 * R.drawable.green_button_depressed));
				 * btnShuffle.setTextColor(Color.parseColor("#ffffff"));
				 * editor.putBoolean("shuffleState", isShuffle);
				 * editor.commit();
				 * 
				 * }
				 */
			}
		});

		/**
		 * Button Click event for Play list click event Launches list activity
		 * which displays list of songs
		 * */
		/*
		 * btnPlaylist.setOnClickListener(new View.OnClickListener() {
		 * 
		 * @Override public void onClick(View arg0) { Intent i = new
		 * Intent(getApplicationContext(), PlayListActivity.class);
		 * startActivityForResult(i, 100); } });
		 */

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
			// Toast.makeText(getApplicationContext(), "repeat", 4).show();

		}

	}

	public void setUpRandom() {

		usedRandomNumbers = (songService.getRandomSongs());
		// Collections.shuffle(usedRandomNumbers);
		// currentSongIndex = 0;
		randomIndex = 0;
	}

	public void onBackPressed() {
		// moveTaskToBack (true);
		if (mp.isPlaying() == true) {
			canGoBack = true;
		}

		if (canGoBack == true) {
			mHandler.removeCallbacks(mUpdateTimeTask);

			finish();
		} else {
			Toast.makeText(getApplicationContext(),
					"Please wait until the song finishes loading", 3).show();
		}

	}

	public Bitmap getArt(String path) {
		Bitmap songImage = null;
		MediaMetadataRetriever metaRetriver;
		metaRetriver = new MediaMetadataRetriever();
		metaRetriver.setDataSource(path);
		byte[] artBytes = metaRetriver.getEmbeddedPicture();
		if (artBytes != null) {
			Bitmap bitmap = BitmapFactory.decodeByteArray(artBytes, 0,
					artBytes.length);

			// imgArt.setImageBitmap(bm);
			return bitmap;
		} else {
			// imgArt.setImageDrawable(getResources().getDrawable(R.drawable.no_cover_art));
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

	private void continueSong(int index) {
		// TODO Auto-generated method stub

		String artistName, songTitle;

		// currentSongIndex = index;

		// Displaying Song title
		songTitle = songsList.get(index).getSongTitle();

		// display artist title
		artistName = songsList.get(index).getSongAuthor();

		// GETTING ALBUM ART
		byte[] art = null;
		try {
			art = songsList.get(index).getImage();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Bitmap bmp = BitmapFactory.decodeByteArray(art, 0, art.length);
		// albumArt.setImageBitmap(helo.getAlbumArt());
		BitmapDrawable ob = new BitmapDrawable(getResources(), bmp);
		albumArt.setBackground(ob);

		mp = songService.mp;

		songTitleLabel.setText(songTitle);
		artistTitleLabel.setText(artistName);

		// Changing Button Image to pause image
		btnPlay.setImageResource(R.drawable.ic_pause);

		// set Progress bar values
		songProgressBar.setProgress(0);
		songProgressBar.setMax(100);

		// Updating progress bar
		updateProgressBar();

	}

	/**
	 * Function to play a song
	 * 
	 * @param songIndex
	 *            - index of song
	 * */

	public void playSong(final int songIndex) {
		// Play song
		try {

			btnPlay.setEnabled(false);
			btnNext.setEnabled(false);
			btnPrevious.setEnabled(false);
			btnForward.setEnabled(false);
			btnBackward.setEnabled(false);

			// GETTING ALBUM ART
			byte[] art = null;

			final String artistName;
			final String songTitle;

			// Displaying Song title
			songTitle = songsList.get(songIndex).getSongTitle();

			// display artist title
			artistName = songsList.get(songIndex).getSongAuthor();

			artistTitleLabel.setText("Loading...");
			songTitleLabel.setText("Loading...");

			// GETTING ALBUM ART

			try {
				art = songsList.get(songIndex).getImage();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Bitmap bmp = BitmapFactory.decodeByteArray(art, 0, art.length);
			// albumArt.setImageBitmap(helo.getAlbumArt());
			BitmapDrawable ob = new BitmapDrawable(getResources(), bmp);
			albumArt.setBackground(ob);

			songService.playSong((songIndex), isRepeat, isShuffle, songTitle);

			// mp = s.mp;
			mp = songService.mp;

			mp.setOnPreparedListener(new OnPreparedListener() {

				@Override
				public void onPrepared(MediaPlayer arg0) {
					// TODO Auto-generated method stub

					canGoBack = true;

					songTitleLabel.setText(songTitle);

					artistTitleLabel.setText(artistName);

					// Changing Button Image to pause image
					btnPlay.setImageResource(R.drawable.ic_pause);

					btnPlay.setEnabled(true);
					btnNext.setEnabled(true);
					btnPrevious.setEnabled(true);
					btnForward.setEnabled(true);
					btnBackward.setEnabled(true);

					// set Progress bar values
					songProgressBar.setProgress(0);
					songProgressBar.setMax(100);

					// Updating progress bar
					updateProgressBar();
					mp.start();

				}

			});

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}

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
			long totalDuration = mp.getDuration();
			long currentDuration = mp.getCurrentPosition();

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
		if (currentSongIndex < (songsList.size() - 1)) {
			mHandler.removeCallbacks(mUpdateTimeTask); // must. its
														// important
			songService.stopSong();

			playSong(currentSongIndex + 1);
			currentSongIndex = currentSongIndex + 1;
			// usedRandomNumbers.add(currentSongIndex);
		} else {
			mHandler.removeCallbacks(mUpdateTimeTask); // must. its
														// important
			songService.stopSong();

			// play first song
			if (isRepeat == true) {
				playSong(0);
				currentSongIndex = 0;
			} else {
				mHandler.removeCallbacks(mUpdateTimeTask); // must. its

				finish();
			}

		}

		// go back to previous screen temparirly
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// mp.release();
	}

}
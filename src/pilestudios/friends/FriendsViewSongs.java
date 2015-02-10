/*This activity shows all the songs a  friend has
 * 
 */
package pilestudios.friends;

import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import pilestudios.musicplayer.MusicPlayer;
import pilestudios.musicplayer.MusicService;
import pilestudios.musicplayer.PlaylistContent;
import pilestudios.musicplayer.PlaylistSelector;
import pilestudios.musicplayer.playlistMusicPlayer;
import pilestudios.musicplayer.playlistService;
import pilestudios.really.HomeScreen;
import pilestudios.really.LoginActivity;
import pilestudios.really.NewPhotoActivity;
import pilestudios.really.R;
import pilestudios.really.R.id;
import pilestudios.really.R.layout;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FriendsViewSongs extends ListActivity implements OnClickListener {

	// textview for now playing
	private TextView nowPlaying;

	// music service instance
	private MusicService s;

	// adapter that returns the query for friend's songs
	private FriendsViewAdapter friendsSongsAdapter;

	// listview that will hold the friend's songs
	ListView lv;

	private playlistService tempStorage;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

		setContentView(R.layout.activity_friends_viewsong);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.home_status);

		// instantiates the music service class
		s = MusicService.getInstance(this);

		// changes the title bar text to friend
		TextView statusBarTitle = (TextView) findViewById(R.id.statusbar_title);
		statusBarTitle.setText("Friend");

		// starts the music player when now playing is clicked
		nowPlaying = (TextView) findViewById(R.id.nowPlaying);
		nowPlaying.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(tempStorage.isPlaylistPlaying == true){
					Intent i = new Intent(FriendsViewSongs.this,playlistMusicPlayer.class);
					startActivity(i);
				}else{
					Intent a = new Intent(FriendsViewSongs.this,MusicPlayer.class);
					startActivity(a);
				}
			}

		});

		TextView upload = (TextView) findViewById(R.id.upload);
		upload.setOnClickListener(this);

		TextView logout = (TextView) findViewById(R.id.logout_button);
		logout.setOnClickListener(this);

		TextView refresh = (TextView) findViewById(R.id.refresh);
		refresh.setOnClickListener(this);

		TextView followers = (TextView) findViewById(R.id.followers);
		followers.setOnClickListener(this);

		TextView playlist = (TextView) findViewById(R.id.playlist_button);
		playlist.setOnClickListener(this);
		
		
		// if music is not playing don't show now playing else show it
        tempStorage = playlistService.getInstance();
		 if(tempStorage.isPlaylistPlaying == true || s.isPlaying == true){
				nowPlaying.setVisibility(View.VISIBLE);
			}else{
				nowPlaying.setVisibility(View.GONE);
			}

		// instantiates songs listview
		lv = getListView();

		// gets the friends songs query and sets it to listview
		friendsSongsAdapter = new FriendsViewAdapter(this);
		lv.setAdapter(friendsSongsAdapter);

		// go back when back is clicked
		TextView back = (TextView) findViewById(R.id.statusbar_back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();

			}

		});

	}
	private void onLogoutButtonClicked() {
		// close this user's session
		ParseFacebookUtils.getSession().closeAndClearTokenInformation();
		// Log the user out
		ParseUser.logOut();
		// Go to the login view
		Intent intent = new Intent(this, LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		finish();
	}
	private void updateHomeList() {

		Intent i = new Intent(this, HomeScreen.class);
		startActivity(i);
	}


	private void newPhoto() {
		Intent i = new Intent(this, NewPhotoActivity.class);
		startActivityForResult(i, 0);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.upload:
			newPhoto();
			break;
		case R.id.refresh:
			updateHomeList();
			break;
		case R.id.logout_button:
			onLogoutButtonClicked();

			break;

		case R.id.playlist_button:

			Intent a = new Intent(this, PlaylistSelector.class);
			startActivity(a);
			break;
		case R.id.followers:
			Intent i = new Intent(this, friends.class);
			startActivity(i);
			break;
		}
	}

	@Override
	public void onResume() {
		super.onResume();

			 if(tempStorage.isPlaylistPlaying == true || s.isPlaying == true){
					nowPlaying.setVisibility(View.VISIBLE);
				}else{
					nowPlaying.setVisibility(View.GONE);
				}

	}

}

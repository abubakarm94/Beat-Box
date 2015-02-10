/*	This activity shows all the friends of a user 
 * 
 */
package pilestudios.friends;

import java.util.ArrayList;
import java.util.List;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.GraphUser;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import pilestudios.musicplayer.MusicPlayer;
import pilestudios.musicplayer.MusicService;
import pilestudios.musicplayer.PlaylistContent;
import pilestudios.musicplayer.PlaylistSelector;
import pilestudios.musicplayer.playlistMusicPlayer;
import pilestudios.musicplayer.playlistService;
import pilestudios.really.HomeListActivity;
import pilestudios.really.HomeScreen;
import pilestudios.really.LoginActivity;
import pilestudios.really.NewPhotoActivity;
import pilestudios.really.R;
import pilestudios.really.startApplication;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class friends extends ListActivity implements OnClickListener{

	//MusicService class instant
	private MusicService s;
	
	//now playing text at top status bar
	private TextView nowPlaying;
	
	//listview of friends
	private ListView friendsList;
	
	//returns the query of friends to the listview
	private friendsAdapter friends;

	private playlistService tempStorage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

		setContentView(R.layout.activity_friends);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.home_status);
		


		//queries all the friends a user has and sets it to listview
		friendsList = getListView();
		 friends = new friendsAdapter(friends.this);
		friendsList.setAdapter(friends);
		
		//hides the back button on the titlebar
		 TextView back = (TextView)findViewById(R.id.statusbar_back);
		 back.setVisibility(View.GONE);
		
		 //instance of the music service object
		 s = MusicService.getInstance(this);
		 
		 //sets the title bar banner to followers
		 TextView statusBarTitle = (TextView) findViewById(R.id.statusbar_title);
		 statusBarTitle.setText("Followers");
		 
		 //instantiates now playing text on titlebar
		 nowPlaying = (TextView)findViewById(R.id.nowPlaying);
		 nowPlaying.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(tempStorage.isPlaylistPlaying == true){
					Intent i = new Intent(friends.this,playlistMusicPlayer.class);
					startActivity(i);
				}else{
					Intent a = new Intent(friends.this,MusicPlayer.class);
					startActivity(a);
				}
			}
			 
		 });
		 
		 	//hides or shows now playing text depending whether if a song is playing
	        tempStorage = playlistService.getInstance();

		 if(tempStorage.isPlaylistPlaying == true || s.isPlaying == true){
				nowPlaying.setVisibility(View.VISIBLE);
			}else{
				nowPlaying.setVisibility(View.GONE);
			}

			//list on friends sets on item click listener
			friendsList.setOnItemClickListener(new OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> parent, View v,
						int position, long id) {
					// TODO Auto-generated method stub
					
					//gets the name of user
					String name = friends.getItem(position).getFromUser().getString("displayName");
				
					//a singleton of friends
					friendsSingleton s = friendsSingleton.getInstance();
					
					//passes the clicked friend to friends singleton
					s.setUser(friends.getItem(position).getFromUser());
					
					//starts an activity that shows the content of the clicked friend
					Intent i = new Intent(friends.this,FriendsViewSongs.class);
					startActivity(i);
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

		
	}
	
	@Override
	public void onResume(){
		super.onResume();

	//hides or shows now playing text depending whether if a song is playing. after the activity resumes

		 if(tempStorage.isPlaylistPlaying == true || s.isPlaying == true){
				nowPlaying.setVisibility(View.VISIBLE);
			}else{
				nowPlaying.setVisibility(View.GONE);
			}
		
		
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
	

	


}

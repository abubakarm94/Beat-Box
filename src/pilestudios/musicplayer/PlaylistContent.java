/* This activity shows the content of playlist adapter
 * 
 */
package pilestudios.musicplayer;

import java.util.ArrayList;

import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseUser;

import pilestudios.beatbox.more.more;
import pilestudios.friends.friends;
import pilestudios.really.HomeScreen;
import pilestudios.really.LoginActivity;
import pilestudios.really.NewSongActivity;
import pilestudios.really.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class PlaylistContent extends ListActivity implements OnClickListener {
	//needed variables
	ListView listNotes;
	TextView newNote;
	playlistAdapter list;
	private playlistService tempStorage;
	private TextView nowPlaying;
	private MusicService singlePlayer;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_display_playlist);

		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.home_status);
		
	
		//query the content in a playlist
		list =  new playlistAdapter(this);
		listNotes = getListView();
		listNotes.setAdapter(list);
		
		
		
		TextView back = (TextView) findViewById(R.id.statusbar_back);
		back.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
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

		
		
        tempStorage = playlistService.getInstance();
         singlePlayer = MusicService.getInstance(this);
        
		TextView title = (TextView) findViewById(R.id.statusbar_title);
	title.setText(tempStorage.getSelectedPlaylist());
	
	 nowPlaying = (TextView) findViewById(R.id.nowPlaying);

		
		if(tempStorage.isPlaylistPlaying == true || singlePlayer.isPlaying == true){
			nowPlaying.setVisibility(View.VISIBLE);
		}else{
			nowPlaying.setVisibility(View.GONE);
		}
		
		nowPlaying.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(tempStorage.isPlaylistPlaying == true){
					Intent i = new Intent(PlaylistContent.this,playlistMusicPlayer.class);
					startActivity(i);
				}else{
					Intent a = new Intent(PlaylistContent.this,MusicPlayer.class);
					startActivity(a);
				}
			}
			
		});
		
		//array of playlist content. song, songtitle etc
		final ArrayList<Playlist> playlistContent = new ArrayList<Playlist>();
		
		//instance of the playlist service
		final playlistService playlistSource= playlistService.getInstance();
		
		//collects all the song in the playlist and puts it in an array
		listNotes.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position,
					long id) {
				// TODO Auto-generated method stub
				for (int i=0;i<list.getCount();i++){
					playlistContent.add(list.getItem(i));
				
					//	Toast.makeText(getApplicationContext(),i+" and "+position, 3).show();

					
				}
				playlistSource.setPlaylistContent(playlistContent);
				playlistSource.setSelectedSong(list.getItem(position));
				playlistSource.setSongObject(list.getItem(position).getSong());
				playlistSource.setSelectedIndex(position);

				Intent i = new Intent(PlaylistContent.this,playlistMusicPlayer.class);
				startActivity(i);
			}
			
		});
		final Builder alert = new AlertDialog.Builder(this);

		listNotes.setOnItemLongClickListener(new OnItemLongClickListener(){

			@Override
			public boolean onItemLongClick(final AdapterView<?> parent2, final View view2,
					final int position2, long id) {
				// TODO Auto-generated method stub
				
				
				
				alert.setTitle("Delete ");
				alert.setMessage("Are you sure? This is permanent");
				alert.setPositiveButton("Yes",new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						ParseObject song = list.getItem(position2).getObject();
						song.deleteInBackground(new DeleteCallback(){

							@Override
							public void done(ParseException arg0) {
								// TODO Auto-generated method stub
								parent2.removeViewInLayout(view2);
								((BaseAdapter) listNotes.getAdapter()).notifyDataSetChanged();
								setListAdapter(list);
							}
							
						});
					}
					
				});
				
				alert.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int whichButton) {
								// Canceled.
							}
						});

				alert.show();
				
				return true;
			}
			
			
		});
	}

	

	protected void onDestory() {
		super.onDestroy();
	}

	@Override
	protected void onResume(){
		super.onResume();
		
		if(tempStorage.isPlaylistPlaying == true || singlePlayer.isPlaying == true){
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
		Intent i = new Intent(this, NewSongActivity.class);
		startActivityForResult(i, 0);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		
		case R.id.upload:
			newPhoto();
			break;
		case R.id.refresh:
			updateHomeList();
			break;
		case R.id.logout_button:
			//onLogoutButtonClicked();
			Intent b = new Intent(this, more.class);
			startActivity(b);
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

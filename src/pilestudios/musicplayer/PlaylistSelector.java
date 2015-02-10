/*
 * this activity shows the playlist names and allows user the select one
 */
package pilestudios.musicplayer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import pilestudios.really.R;
import pilestudios.really.startApplication;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class PlaylistSelector extends Activity implements OnClickListener {
	//needed variable
	ListView playlistView;
	playlistAdapter myDb;
	ArrayList<String> listNames, tempList;

	SharedPreferences preferences;
	protected String newListTitle;
	private TextView nowPlaying;
	private MusicService singlePlayer;
	private playlistService tempStorage;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

		setContentView(R.layout.activity_playlist);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.home_status);

		//list of playlist names
		playlistView = (ListView) findViewById(R.id.play_list);
		
		listNames = new ArrayList();

		//playlist service
        tempStorage = playlistService.getInstance();

		//create playlist
		TextView create = (TextView) findViewById(R.id.create_playlist);
		create.setOnClickListener(this);
		
		//shared preferences 
		preferences = getSharedPreferences("temp",
				getApplicationContext().MODE_PRIVATE);
		
		//sets the contents in playlist
		setList();

		//when a playlist is click
		playlistView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position,
					long id) {
				// TODO Auto-generated method stub
				String temp = parent.getItemAtPosition(position).toString();
				tempStorage.setPlaylist(temp);
				Intent i = new Intent(PlaylistSelector.this, PlaylistContent.class);
				startActivity(i);

			}

		});
		
		
		TextView title = (TextView) findViewById(R.id.statusbar_title);
		title.setText("Playlist");
		
		TextView back = (TextView) findViewById(R.id.statusbar_back);
		back.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
			
		});
		
		
         singlePlayer = MusicService.getInstance(this);
		
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
						Intent i = new Intent(PlaylistSelector.this,playlistMusicPlayer.class);
						startActivity(i);
					}else{
						Intent a = new Intent(PlaylistSelector.this,MusicPlayer.class);
						startActivity(a);
					}
				}
				
			});
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

	protected void onDestory() {
		super.onDestroy();
	}
	




	public void setList() {
		// String name=preferences.getString("name",null);
		if (preferences.contains("playlist_name")) {
			listNames = new ArrayList<String>(preferences.getStringSet(
					"playlist_name", null));
			// tempList= (ArrayList<String>)
			// preferences.getStringSet("playlist_name", null);
			if (listNames != null) {
				ArrayAdapter adapter = new ArrayAdapter<String>(
						getApplicationContext(), R.layout.playlist_item,
						R.id.playlist_title, listNames);
				playlistView.setAdapter(adapter);
			} else {
				Toast.makeText(getApplicationContext(), "temp", 1).show();

			}
		}

	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		// Toast.makeText(getApplicationContext(), "temp", 1).show();

		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("New Playlist");
		alert.setMessage("Enter a name for this playlist");

		// Set an EditText view to get user input
		final EditText input = new EditText(this);
		alert.setView(input);

		alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				Editor editor = preferences.edit();

				newListTitle = input.getText().toString();
				if(listNames.contains(newListTitle)){
					Toast.makeText(getApplicationContext(), "A playlist already exists by that title", 3).show();
				}else{
				listNames.add(newListTitle);
				Set<String> set = new HashSet<String>();
				set.addAll(listNames);

				editor.putStringSet("playlist_name", set);
				editor.commit();
				}

				// Do something with value!
			}
		});

		alert.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// Canceled.
					}
				});

		alert.show();

		// myDb.insertRow(null, "temp");
	}

	public void askNewPlaylistTitle() {

	}

}

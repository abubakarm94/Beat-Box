/*
 * this activity shows the playlist names and allows user the select one
 */
package pilestudios.musicplayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseUser;

import pilestudios.friends.friends;
import pilestudios.really.HomeScreen;
import pilestudios.really.LoginActivity;
import pilestudios.really.NewPhotoActivity;
import pilestudios.really.R;
import pilestudios.really.startApplication;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
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
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class PlaylistSelector extends Activity implements OnClickListener {
	// needed variable
	ListView playlistView;
	playlistAdapter myDb;
	ArrayList<String> listNames, tempList;

	SharedPreferences preferences;
	protected String newListTitle;
	private TextView nowPlaying;
	private MusicService singlePlayer;
	private playlistService tempStorage;

	private Editor editor;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

		setContentView(R.layout.activity_playlist);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.home_status);

		// list of playlist names
		playlistView = (ListView) findViewById(R.id.play_list);

		listNames = new ArrayList();

		// playlist service
		tempStorage = playlistService.getInstance();

		// create playlist
		TextView create = (TextView) findViewById(R.id.create_playlist);
		create.setOnClickListener(this);

		// shared preferences
		preferences = getSharedPreferences("temp",
				getApplicationContext().MODE_PRIVATE);

		// where the names of the playlist are saved
		editor = preferences.edit();

		// sets the contents in playlist
		setList();
		final Builder alert = new AlertDialog.Builder(this);

		playlistView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(final AdapterView<?> parent,
					final View view, final int position, long id) {
				// TODO Auto-generated method stub

				alert.setTitle("Delete "
						+ parent.getItemAtPosition(position).toString());
				alert.setMessage("Are you sure? This is permanent");
				alert.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								// TODO Auto-generated method stub
								parent.removeViewInLayout(view);
								((BaseAdapter) playlistView.getAdapter())
										.notifyDataSetChanged();
								listNames.remove(parent.getItemAtPosition(
										position).toString());
								// after deleted save new playlist
								Set<String> set = new HashSet<String>();
								set.addAll(listNames);

								editor.putStringSet("playlist_name", set);
								editor.commit();
								setList();

							}

						});

				alert.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								// Canceled.
							}
						});

				alert.show();
				return true;
			}

		});

		// when a playlist is click
		playlistView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				// TODO Auto-generated method stub
				String temp = parent.getItemAtPosition(position).toString();
				tempStorage.setPlaylist(temp);
				Intent i = new Intent(PlaylistSelector.this,
						PlaylistContent.class);
				startActivity(i);

			}

		});

		TextView title = (TextView) findViewById(R.id.statusbar_title);
		title.setText("Playlist");

		TextView back = (TextView) findViewById(R.id.statusbar_back);
		 back.setVisibility(View.GONE);

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

		singlePlayer = MusicService.getInstance(this);

		nowPlaying = (TextView) findViewById(R.id.nowPlaying);

		if (tempStorage.isPlaylistPlaying == true
				|| singlePlayer.isPlaying == true) {
			nowPlaying.setVisibility(View.VISIBLE);
		} else {
			nowPlaying.setVisibility(View.GONE);
		}

		nowPlaying.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (tempStorage.isPlaylistPlaying == true) {
					Intent i = new Intent(PlaylistSelector.this,
							playlistMusicPlayer.class);
					startActivity(i);
				} else {
					Intent a = new Intent(PlaylistSelector.this,
							MusicPlayer.class);
					startActivity(a);
				}
			}

		});
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (tempStorage.isPlaylistPlaying == true
				|| singlePlayer.isPlaying == true) {
			nowPlaying.setVisibility(View.VISIBLE);
		} else {
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
				Collections.sort(listNames);

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
	public void onClick(View v) {
		// TODO Auto-generated method stub
		// Toast.makeText(getApplicationContext(), "temp", 1).show();

		switch (v.getId()) {

		case R.id.create_playlist:

			AlertDialog.Builder alert = new AlertDialog.Builder(this);

			alert.setTitle("New Playlist");
			alert.setMessage("Enter a name for this playlist");

			// Set an EditText view to get user input
			final EditText input = new EditText(this);
			alert.setView(input);

			alert.setPositiveButton("Save",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog,
								int whichButton) {

							newListTitle = input.getText().toString();
							if (listNames.contains(newListTitle)) {
								Toast.makeText(
										getApplicationContext(),
										"A playlist already exists by that title",
										3).show();
							} else {
								listNames.add(newListTitle);
								Set<String> set = new HashSet<String>();
								set.addAll(listNames);

								editor.putStringSet("playlist_name", set);
								editor.commit();

								setList();
							}

							// Do something with value!
						}
					});

			alert.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int whichButton) {
							// Canceled.
						}
					});

			alert.show();
			break;
			
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

		// myDb.insertRow(null, "temp");
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

	public void askNewPlaylistTitle() {

	}

}

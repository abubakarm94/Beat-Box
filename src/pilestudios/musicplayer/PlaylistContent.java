/* This activity shows the content of playlist adapter
 * 
 */
package pilestudios.musicplayer;

import java.util.ArrayList;

import pilestudios.really.R;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class PlaylistContent extends ListActivity {
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

}

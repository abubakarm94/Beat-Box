package pilestudios.really;

//sets up a tab host for the user's newfeed and the user profile page

import pilestudios.musicplayer.MusicPlayer;
import pilestudios.musicplayer.MusicService;
import pilestudios.musicplayer.PlaylistContent;
import pilestudios.musicplayer.playlistMusicPlayer;
import pilestudios.musicplayer.playlistService;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TextView;

public class HomeScreen extends TabActivity {
	private TabHost mTabHost;
	private TextView nowPlaying;
	private MusicService s;
	private playlistService tempStorage;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

		setContentView(R.layout.activity_home_tab);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.home_status);

		Resources res = getResources();
		TabHost mTabHost = getTabHost();
		TabHost.TabSpec spec;
		Intent intent;

		intent = new Intent(this, HomeListActivity.class);
		spec = mTabHost.newTabSpec("News Feed").setIndicator("News Feed")

		.setContent(intent);

		mTabHost.addTab(spec);

		intent = new Intent(this, ProfileListActivity.class);
		spec = mTabHost.newTabSpec("Profile").setIndicator("Profile")
				.setContent(intent);
		mTabHost.addTab(spec);

		
		s = MusicService.getInstance(this);

		TextView back = (TextView) findViewById(R.id.statusbar_back);
		back.setVisibility(View.GONE);

		nowPlaying = (TextView) findViewById(R.id.nowPlaying);
		nowPlaying.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (tempStorage.isPlaylistPlaying == true) {
					Intent i = new Intent(HomeScreen.this,
							playlistMusicPlayer.class);
					startActivity(i);
				} else {
					Intent a = new Intent(HomeScreen.this, MusicPlayer.class);
					startActivity(a);
				}
			}

		});

		tempStorage = playlistService.getInstance();

		if (tempStorage.isPlaylistPlaying == true || s.isPlaying == true) {
			nowPlaying.setVisibility(View.VISIBLE);
		} else {
			nowPlaying.setVisibility(View.GONE);
		}

	}

	@Override
	public void onResume() {
		super.onResume();

		if (tempStorage.isPlaylistPlaying == true || s.isPlaying == true) {
			nowPlaying.setVisibility(View.VISIBLE);
		} else {
			nowPlaying.setVisibility(View.GONE);
		}

	}

}
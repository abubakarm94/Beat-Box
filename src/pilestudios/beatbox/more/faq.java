//This class acts as the Frequently asked question activity
package pilestudios.beatbox.more;

import pilestudios.musicplayer.MusicPlayer;
import pilestudios.musicplayer.MusicService;
import pilestudios.musicplayer.playlistMusicPlayer;
import pilestudios.musicplayer.playlistService;
import pilestudios.really.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class faq extends Activity {

	private playlistService tempStorage;
	TextView nowPlaying, back;
	MusicService s;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

		setContentView(R.layout.activity_faq);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.home_status);

		s = MusicService.getInstance(this);

		TextView back = (TextView) findViewById(R.id.statusbar_back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}

		});

		tempStorage = playlistService.getInstance();

		nowPlaying = (TextView) findViewById(R.id.nowPlaying);
		nowPlaying.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (tempStorage.isPlaylistPlaying == true) {
					Intent i = new Intent(faq.this, playlistMusicPlayer.class);
					startActivity(i);
				} else {
					Intent a = new Intent(faq.this, MusicPlayer.class);
					startActivity(a);
				}
			}

		});

		if (tempStorage.isPlaylistPlaying == true || s.isPlaying == true) {
			nowPlaying.setVisibility(View.VISIBLE);
		} else {
			nowPlaying.setVisibility(View.GONE);
		}

		TextView faqText = (TextView) findViewById(R.id.faqText);
		String sourceString = "<b>What is Music Box?</b>"
				+ " <br> Music Box is an that lets you create a playlist and stream songs from your friend's music collection. <br>"
				+ "  <br><b> Can I upload songs that my friends would not have access to?</b>"
				+ "  <br> Yes, your friends do not have songs uploaded as a private upload. Simply click on upload and then private upload."
				+ " <br><br><b>How do I download my songs to my device?</b>"
				+ " <br>  Go to your profile and then click on the download icon on the song you would like to upload."
				+ "<br><br><b> Can I download my friends songs?</b>"
				+ " <br> Unfortunately no, Music Box was not created to support piracy."
				+ "<br><br><b>How do I delete a song I uploaded?</b>"
				+ "  <br> Simply long click the song you would like to delete"
				+

				" <br><br><b> I have a problem that is not listed here, who should I contact for help?</b>"
				+ "  <br> Email us at admin@pilestudios.com and we would be glad to help.";
		faqText.setText(Html.fromHtml(sourceString));

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

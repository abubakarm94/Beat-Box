/*
 * returns the query for the clicked playlist name
 */
package pilestudios.musicplayer;

import java.util.Arrays;

import pilestudios.really.Photo;
import pilestudios.really.R;
import pilestudios.really.startApplication;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

// TO USE:
// Change the package (at top) to match your project.
// Search for "TODO", and make the appropriate changes.
public class playlistAdapter extends ParseQueryAdapter<Playlist> {

	public playlistAdapter(final Context context) {
		super(context, new playlistAdapter.QueryFactory<Playlist>() {
			public ParseQuery<Playlist> create() {

				// instantiate for the playlist service
				playlistService s = playlistService.getInstance();

				// gets the parse user
				ParseUser user = ParseUser.getCurrentUser();

				// gets songs from the playlist
				ParseQuery<Playlist> songs = new ParseQuery<Playlist>(
						"Playlist");
				songs.whereMatches("playlistName", s.getSelectedPlaylist());
				songs.whereExists("song");
				songs.whereEqualTo("user", user);
				songs.include("song");

				
				return songs;
			

			}

		});
	}
	
	

	// sets up the view based on query
	@Override
	public View getItemView(final Playlist photo, View v, ViewGroup parent) {

		if (v == null) {

			v = View.inflate(getContext(), R.layout.row_item, null);
		}

		super.getItemView(photo, v, parent);
		
		

		

		if (photo.get("song") != null) {
			// gets parse user
			ParseUser user = ParseUser.getCurrentUser();

			// sets songtitle
			TextView songTitle = (TextView) v.findViewById(R.id.songTitle);
			songTitle.setText((String) photo.getSongTitle());

			// sets song artist name
			TextView songAuthor = (TextView) v.findViewById(R.id.songAuthor);
			songAuthor.setText((String) photo.getSongAuthor());

			// Set up the actual photo
			ImageView thumbnail = (ImageView) v
					.findViewById(R.id.playlist_thumbnail);

			byte[] photoFile = null;
			try {
				photoFile = photo.getThumbnail();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Bitmap helo = BitmapFactory.decodeByteArray(photoFile, 0,
					photoFile.length);
			if (helo != null) {
				thumbnail.setImageBitmap(helo);
			}
		}

		return v;

	}

}

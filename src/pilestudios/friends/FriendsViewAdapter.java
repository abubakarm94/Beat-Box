/* This activity queries parse for all a friend's songs
 * 
 */

package pilestudios.friends;

import java.util.ArrayList;
import java.util.Arrays;

import pilestudios.musicplayer.MusicPlayer;
import pilestudios.musicplayer.MusicService;
import pilestudios.musicplayer.Playlist;
import pilestudios.really.Photo;
import pilestudios.really.R;
import pilestudios.really.startApplication;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
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
import com.parse.SaveCallback;

public class FriendsViewAdapter extends ParseQueryAdapter<Photo> {
	// mediaplayer Instance
	private MediaPlayer mediaPlayer = new MediaPlayer();

	// friends singleton this gets and sets parseUser friend
	private static friendsSingleton s;

	public FriendsViewAdapter(Context context) {
		super(context, new ParseQueryAdapter.QueryFactory<Photo>() {

			public ParseQuery<Photo> create() {

				// instantiates friends singelton variable
				s = friendsSingleton.getInstance();

				// Get the current user's songs
				ParseQuery<Photo> query = new ParseQuery<Photo>("Photo");
				query.whereEqualTo("user", s.getUser());
				query.whereExists("image");
				query.whereEqualTo("visibility",true);

				query.include("user");
				query.orderByDescending("createdAt");

				return query;
			}
		});

	}

	// uses the query and sets the results to listview elements
	@Override
	public View getItemView(final Photo photo, View v, ViewGroup parent) {

		if (v == null) {
			v = View.inflate(getContext(), R.layout.home_list_item, null);
		}

		super.getItemView(photo, v, parent);
		
		//gets the friend and saves it to a variable
		ParseUser user = photo.getUser();

		// Set up the username
		TextView usernameView = (TextView) v.findViewById(R.id.user_name);
		usernameView.setText((String) s.getUser().get("displayName")
				+ " uploaded");
		
		//sets up the functionality for the play button
		//this plays the song in listview
		final TextView addToPlaylist = (TextView)v.findViewById(R.id.addTo);
		addToPlaylist.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				SharedPreferences preferences = getContext().getSharedPreferences("temp",
						getContext().getApplicationContext().MODE_PRIVATE);
				if (preferences.contains("playlist_name")) {
					ArrayList<String> listNames = new ArrayList<String>(
							preferences.getStringSet("playlist_name", null));
					// tempList= (ArrayList<String>)
					// preferences.getStringSet("playlist_name", null);
					final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

					if (listNames != null) {
						final CharSequence[] items = listNames
								.toArray(new CharSequence[listNames.size()]);

						// final CharSequence[] items = {"Foo", "Bar", "Baz"};

						builder.setTitle("Make your selection");
						builder.setItems(items,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int item) {
										// Do something with the selection
										String selectedPlaylist = items[item]
												.toString();
										Toast.makeText(getContext().getApplicationContext(),
												selectedPlaylist, 5).show();
									
											ParseObject selectedSong = photo.getObject();
													
									

										if (selectedSong != null) {
										//	openDatabase();
											// myDb.deleteAll();
										
										//	 Log.i("HomeList", myDb.);
											//myDb.deleteAll();
											// Log.i("database",myDb.);
										//	closeDataBase();
											//Log.i("finished saving", "done");
											Playlist playlist = new Playlist();
											playlist.setSong(selectedSong);
											playlist.setPlaylistName(selectedPlaylist);
											playlist.setUser(ParseUser.getCurrentUser());
											playlist.saveInBackground( new SaveCallback(){

												@Override
												public void done(
														ParseException arg0) {
													// TODO Auto-generated method stub
													
													Toast.makeText(getContext().getApplicationContext(), "saved", 3).show();

												}
												
											});
										}
									}
								});
						AlertDialog alert = builder.create();
						alert.show();

					}
				}else{
					Toast.makeText(getContext().getApplicationContext(), "No playlist has been created", 3).show();
				}

			}
		});
		
		
		final Button play = (Button) v.findViewById(R.id.play);
		play.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO

				//starts the music player on click
				Intent i = new Intent(v.getContext(), MusicPlayer.class);

				
				//instantites music service and sets it to variable s
				MusicService s = MusicService.getInstance(getContext());
				
				//instantiates the general whole application singleton
				startApplication currentSong = (startApplication) v
						.getContext().getApplicationContext();
				try {
					//gets the song url
					String temp = photo.getSongUrl();
					
					//gets the song title
					String extend = photo.getSong().getName();
					
					//gets the song extension
					currentSong.setExtension(extend.substring(extend.length() - 3));
					
					//save the song url to singleton
					currentSong.setSong(temp);
					
					//gets the album art and save it to singleton
					currentSong.setAlbumArt(getArt(photo.getImage().getData()));
					
					//gets the song artist name and saves it to singleton
					currentSong.setArtistName(photo.getArtistName());
					
					//saves the song object to music service instance
					s.setSetObject(photo.getObject());

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				//get the song title and saves it to singleton
				currentSong.setSongTitle(photo.getSongTitle());

				v.getContext().startActivity(i);

			}

		});

		//gets and sets the song title and album artist name
		TextView songTitle = (TextView) v.findViewById(R.id.song_title);
		songTitle
				.setText(photo.getSongTitle() + " by " + photo.getArtistName());

		// Set up the actual album art
		ParseImageView anypicPhotoView = (ParseImageView) v
				.findViewById(R.id.photo);
		ParseFile photoFile = photo.getThumbnail();
		if (photoFile != null) {
			anypicPhotoView.setParseFile(photoFile);
			anypicPhotoView.loadInBackground(new GetDataCallback() {
				@Override
				public void done(byte[] data, ParseException e) {
					// nothing to do
					// Log.i(startApplication.TAG, "8. Image view loaded");
				}
			});
		} else { // Clear ParseImageView if an object doesn't have a photo
			anypicPhotoView.setImageResource(android.R.color.transparent);
		}

		return v;

	}

	//converts the album art array of bytes to bitmap and returns it. if the album art is null 
	//returns a preset no cover art image
	public Bitmap getArt(byte[] artBytes) {
		Bitmap songImage = null;

		if (artBytes != null) {
			Bitmap bitmap = BitmapFactory.decodeByteArray(artBytes, 0,
					artBytes.length);

			// imgArt.setImageBitmap(bm);
			return bitmap;
		} else {
			// imgArt.setImageDrawable(getResources().getDrawable(R.drawable.no_cover_art));
			Bitmap imgArt = BitmapFactory.decodeResource(getContext()
					.getResources(), R.drawable.no_cover_art);
			return imgArt;
		}
	}

}
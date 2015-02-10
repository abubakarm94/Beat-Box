package pilestudios.really;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.Deflater;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import pilestudios.musicplayer.MusicPlayer;
import pilestudios.musicplayer.MusicService;
import pilestudios.musicplayer.Playlist;
import pilestudios.really.R;

/*
 * The HomeViewAdapter is an extension of ParseQueryAdapter
 * that has a custom layout for Anypic photos in the home
 * list view.
 */

public class HomeViewAdapter extends ParseQueryAdapter<Photo> {
	private MediaPlayer mediaPlayer = new MediaPlayer();

	public HomeViewAdapter(Context context) {
		super(context, new ParseQueryAdapter.QueryFactory<Photo>() {
			public ParseQuery<Photo> create() {
				
				// First, query for the friends whom the current user follows
				ParseQuery<pilestudios.really.Activity> followingActivitiesQuery = new ParseQuery<pilestudios.really.Activity>("Activity");
				followingActivitiesQuery.setLimit(4);
				followingActivitiesQuery.whereMatches("type", "follow");
				
				followingActivitiesQuery.whereEqualTo("fromUser", ParseUser.getCurrentUser());
				
				// Get the photos from the Users returned in the previous query
				ParseQuery<Photo> photosFromFollowedUsersQuery = new ParseQuery<Photo>("Photo");
				photosFromFollowedUsersQuery.whereMatchesKeyInQuery("user", "toUser", followingActivitiesQuery);
				photosFromFollowedUsersQuery.whereExists("image");
				photosFromFollowedUsersQuery.whereEqualTo("visibility",true);

				
				// Get the current user's photos
				ParseQuery<Photo> photosFromCurrentUserQuery = new ParseQuery<Photo>("Photo");
				photosFromCurrentUserQuery.whereEqualTo("user", ParseUser.getCurrentUser());
				photosFromCurrentUserQuery.whereExists("image");
				//photosFromCurrentUserQuery.setLimit(10);
				//photosFromCurrentUserQuery.whereEqualTo("visibility", true);

				// We create a final compound query that will find all of the photos that were
			    // taken by the user's friends or by the user
				ParseQuery<Photo> query = ParseQuery.or(Arrays.asList( photosFromFollowedUsersQuery, photosFromCurrentUserQuery ));
				
				
				query.include("user");
				query.orderByDescending("createdAt");
			
				//query.setLimit(0); not working yet

				return query;
			}
		});
	

	}

	/**
	 * This class is overridden to provide a custom view for each item in the 
	 * Home List View. It sets the user's profile picture, their user name,
	 * and then displays the actual photo.
	 * 
	 * See home_list_item.xml for the layout file
	 * 
	 * @see com.parse.ParseQueryAdapter#getItemView(com.parse.ParseObject, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getItemView(final Photo photo, View v, ViewGroup parent) {

		if (v == null) {
			v = View.inflate(getContext(), R.layout.home_list_item, null);
		}

		super.getItemView(photo, v, parent);
		
		ParseUser user = photo.getUser();
/*
		// Set up the user's profile picture
		ParseImageView fbPhotoView = (ParseImageView) v.findViewById(R.id.user_thumbnail);
		
		ParseFile thumbnailFile = user.getParseFile("profilePictureSmall");
		if (thumbnailFile != null) {
			fbPhotoView.setParseFile(thumbnailFile);
			fbPhotoView.loadInBackground(new GetDataCallback() {
				@Override
				public void done(byte[] data, ParseException e) {
					// nothing to do
					//Log.i(startApplication.TAG, "7. Thumbnail view loaded");
				}
			});
		} else { // Clear ParseImageView if an object doesn't have a photo
	        fbPhotoView.setImageResource(android.R.color.transparent);
	    }*/

		// Set up the username
		TextView usernameView = (TextView) v.findViewById(R.id.user_name);
		usernameView.setText((String) user.get("displayName")+" uploaded");
		
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
	                     //TODO
			
	        	Intent i = new Intent(v.getContext(),MusicPlayer.class);
	        	
	       
	        /*	try {
					i.putExtra("song", photo.getSong().getData());
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Toast.makeText(v.getContext(), "failed", 4).show();
				}*/
	        	MusicService s = MusicService.getInstance(getContext());
	        	startApplication currentSong = (startApplication) v.getContext().getApplicationContext();
	        	try {
	        		String temp = photo.getSongUrl();
	        		String extend= photo.getSong().getName(); 
	        		currentSong.setExtension(extend.substring(extend.length() - 3));
					currentSong.setSong(temp);
					currentSong.setAlbumArt(getArt(photo.getImage().getData()));
		        	currentSong.setArtistName(photo.getArtistName());
		        	s.setSetObject(photo.getObject());
					
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        	
	        	
	    /*    	try {
					currentSong.setAlbumArt(photo.getImage().getData());
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
	        	
	        	currentSong.setSongTitle(photo.getSongTitle());

	        	
	        	v.getContext().startActivity(i);
	        	
	                }
	 
	            });
		
		//set up song title =
		TextView songTitle = (TextView) v.findViewById(R.id.song_title);
		songTitle.setText(photo.getSongTitle() + " by "+photo.getArtistName());
		
		// Set up the actual photo
		ParseImageView anypicPhotoView = (ParseImageView) v.findViewById(R.id.photo);
		ParseFile photoFile = photo.getThumbnail();
		
		// TODO (future) - get image bitmap, then set the image view with setImageBitmap()
		// we can use the decodeBitmap tricks to reduce the size to save memory
		
		if (photoFile != null) {
			anypicPhotoView.setParseFile(photoFile);
			anypicPhotoView.loadInBackground(new GetDataCallback() {
				@Override
				public void done(byte[] data, ParseException e) {
					// nothing to do
					//Log.i(startApplication.TAG, "8. Image view loaded");
				}
			});
		} else { // Clear ParseImageView if an object doesn't have a photo
	        anypicPhotoView.setImageResource(android.R.color.transparent);
	    }
		

//		final ImageView iv=anypicPhotoView;
//		ViewTreeObserver vto = iv.getViewTreeObserver();
//		vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//			public boolean onPreDraw() {
//				Log.i(startApplication.TAG, "*** Photo height: " + iv.getMeasuredHeight() + " width: " + iv.getMeasuredWidth());
//				return true;
//			}
//		});
		return v;
	}
	
	public Bitmap getArt(byte[] artBytes){
    	Bitmap songImage=null;
    	
    	 if(artBytes != null)
    	    {
    		 Bitmap bitmap = BitmapFactory.decodeByteArray(artBytes, 0, artBytes.length);

    	      
    	        //imgArt.setImageBitmap(bm);
    	        return bitmap;
    	    }
    	    else
    	    {
    	       // imgArt.setImageDrawable(getResources().getDrawable(R.drawable.no_cover_art));
    	        Bitmap imgArt = BitmapFactory.decodeResource(getContext().getResources(),
    	                R.drawable.no_cover_art);
    	        return imgArt;
    	    }
    }
		   
	private FileDescriptor getFileDescriptor(byte[] mp3SoundByteArray) {
		
	    try {
	        // create temp file that will hold byte array
	        File tempMp3 = File.createTempFile("kurchina", "mp3", getContext().getCacheDir());
	        tempMp3.deleteOnExit();
	        FileOutputStream fos = new FileOutputStream(tempMp3);
	        fos.write(mp3SoundByteArray);
	        fos.close();

	        // Tried reusing instance of media player
	        // but that resulted in system crashes...  
	       

	        // Tried passing path directly, but kept getting 
	        // "Prepare failed.: status=0x1"
	        // so using file descriptor instead
	        FileInputStream fis = new FileInputStream(tempMp3);
	        
	        return fis.getFD();
	    } catch (IOException ex) {
	        String s = ex.toString();
	        ex.printStackTrace();
	    }
	    return null;
	}
	

}

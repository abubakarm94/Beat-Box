package pilestudios.really;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.util.Log;
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

import pilestudios.musicplayer.MusicPlayer;
import pilestudios.musicplayer.MusicService;
import pilestudios.musicplayer.Playlist;
import pilestudios.really.R;

/*
 * The UserViewAdapter is an extension of ParseQueryAdapter
 * that has a custom layout for Anypic photos for the current user
 */

public class UserViewAdapter extends ParseQueryAdapter<Photo> {

	public UserViewAdapter(Context context) {
		super(context, new ParseQueryAdapter.QueryFactory<Photo>() {
			public ParseQuery<Photo> create() {

				// Get the current user's photos
				ParseQuery<Photo> photosFromCurrentUserQuery = new ParseQuery<Photo>(
						"Photo");
				photosFromCurrentUserQuery.whereEqualTo("user",
						ParseUser.getCurrentUser());
				photosFromCurrentUserQuery.whereExists("thumbnail");

				photosFromCurrentUserQuery.include("user");
				photosFromCurrentUserQuery.orderByDescending("createdAt");

				return photosFromCurrentUserQuery;
			}
		});
	}

	/**
	 * This class is overridden to provide a custom view for each item in the
	 * User's List View. It sets the user's profile picture, the user name, and
	 * then displays the actual photo.
	 * 
	 * See user_list_item.xml for the layout file
	 * 
	 * @see com.parse.ParseQueryAdapter#getItemView(com.parse.ParseObject,
	 *      android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getItemView(final Photo photo, View v, ViewGroup parent) {

		if (v == null) {
			v = View.inflate(getContext(), R.layout.profile_list_item, null);
		}

		super.getItemView(photo, v, parent);
		ParseUser user = photo.getUser();

		/*
		 * // Set up the user's profile picture ParseImageView fbPhotoView =
		 * (ParseImageView) v.findViewById(R.id.user_thumbnail);
		 * 
		 * ParseFile thumbnailFile = user.getParseFile("profilePictureSmall");
		 * if (thumbnailFile != null) { fbPhotoView.setParseFile(thumbnailFile);
		 * fbPhotoView.loadInBackground(new GetDataCallback() {
		 * 
		 * @Override public void done(byte[] data, ParseException e) { //
		 * nothing to do //Log.i(startApplication.TAG,
		 * "7. Thumbnail view loaded"); } }); } else { // Clear ParseImageView
		 * if an object doesn't have a photo
		 * fbPhotoView.setImageResource(android.R.color.transparent); }
		 */
		
		String name = (String) user.get("displayName");
		
		

		// Set up the username
		TextView usernameView = (TextView) v.findViewById(R.id.user_name);
		usernameView.setText(name);

		// set up song title 
		TextView songTitle = (TextView) v.findViewById(R.id.song_title);
		songTitle.setText(photo.getSongTitle()+" by "+photo.getArtistName());
		
		
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
		
		Button download = (Button) v.findViewById(R.id.download);
		download.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String songUrl = photo.getSongTitle();
				String songExtension = photo.getSong().getName();
				
				  Toast.makeText(getContext().getApplicationContext(), "Song Downloaded!", Toast.LENGTH_LONG).show();
				try {
					getFileDescriptor(photo.getSong().getData(),songUrl,songExtension.substring(songExtension.length()-3));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		});
		
		TextView play = (TextView) v.findViewById(R.id.playSong);
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
				/*if(play.getText().equals("PLAY")){
					//Toast.makeText(getApplicationContext(), "error", 6).show();
					mediaPlayer.stop();
					try {
						playMp3(photo.getSong().getData());
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					play.setText("STOP");
					}else{
						mediaPlayer.stop();
						play.setText("PLAY");
					}*/

	                }
	 
	            });
		

		// Set up the actual photo
		ParseImageView anypicPhotoView = (ParseImageView) v
				.findViewById(R.id.photo);
		ParseFile photoFile = photo.getThumbnail();

		// TODO (future) - get image bitmap, then set the image view with
		// setImageBitmap()
		// we can use the decodeBitmap tricks to reduce the size to save memory

		if (photoFile != null) {
			anypicPhotoView.setParseFile(photoFile);
			anypicPhotoView.loadInBackground(new GetDataCallback() {
				@Override
				public void done(byte[] data, ParseException e) {
					// nothing to do
				}
			});
		} else { // Clear ParseImageView if an object doesn't have a photo
			anypicPhotoView.setImageResource(android.R.color.transparent);
		}

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
		   
	private FileDescriptor getFileDescriptor(byte[] mp3SoundByteArray,String songName, String songExtension) {
		
	    try {
	        // create temp file that will hold byte array
	    	File dir = new File("/sdcard/beatbox/");
	    	dir.mkdir();
	    	
	    	File temp = new File("/sdcard/beatbox/",songName+"."+songExtension);
	       // File tempMp3 = File.createTempFile("kurchina", "mp3", getContext().getCacheDir());
	       // tempMp3.deleteOnExit();
	    	
	    	
	        FileOutputStream fos = new FileOutputStream(temp);
	        fos.write(mp3SoundByteArray);
	        fos.close();

	        // Tried reusing instance of media player
	        // but that resulted in system crashes...  
	       

	        // Tried passing path directly, but kept getting 
	        // "Prepare failed.: status=0x1"
	        // so using file descriptor instead
	        FileInputStream fis = new FileInputStream(temp);
	        
	        return fis.getFD();
	    } catch (IOException ex) {
	        String s = ex.toString();
	        ex.printStackTrace();
	    }
	    return null;
	}
	
	
	
	
}




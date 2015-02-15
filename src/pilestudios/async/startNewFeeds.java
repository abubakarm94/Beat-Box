/* This activity is launched by loginActivity 
 * It starts the Homescreen after 2 secs
 * 
 */

package pilestudios.async;

import java.util.Arrays;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import pilestudios.friends.FriendsViewSongs;
import pilestudios.musicplayer.Playlist;
import pilestudios.musicplayer.playlistService;
import pilestudios.really.HomeListActivity;
import pilestudios.really.HomeScreen;
import pilestudios.really.Photo;
import pilestudios.really.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;
import android.widget.Toast;

public class startNewFeeds extends Activity implements Runnable {
	private final int WAIT_TIME = 25000;
	ProgressBar progress;
	private ParseUser user;
	private Photo song;
    private Number usedSize,totalSpace, uploadCount;
	private globalData gData;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_startnewfeed);

		// progress bar

		if(isNetworkAvailable() == false){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

		// set title
		alertDialogBuilder.setTitle("Connection error");

		// set dialog message
		alertDialogBuilder
				.setMessage("Unable to connect with the server. Check your internet connection and try again")
				.setCancelable(false)
				.setPositiveButton("Exit and Try again",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// if this button is clicked, close
								// current activity
								// MainActivity.this.finish();
								finish();
							}
						});
				

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
		
		}else{
			 user = ParseUser.getCurrentUser();
			  song = new Photo();

			  gData = globalData.getInstance(this);
			  getData();
			
			  
			//  setNewsFeedQuery();
			  
			  setPlaylistQuery();
			  
		Thread mThread = new Thread(this);

		mThread.start();
		
		}
		
	}

	public void run() {
		try {
			Thread.sleep(2000);
			// progress.incrementProgressBy(10);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			startActivity(new Intent(this, HomeScreen.class));

			finish();
		}
	}
	
	

	// checks if there is an internet connection available
	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}
	
	public void setPlaylistQuery(){
		// instantiate for the playlist service
		playlistService s = playlistService.getInstance();

		// gets the parse user
		ParseUser user = ParseUser.getCurrentUser();

		// gets songs from the playlist
		ParseQuery<Playlist> songs = new ParseQuery<Playlist>(
				"Playlist");
		songs.whereEqualTo("user", user);

		songs.whereExists("song");
		
		songs.include("objectId");

		songs.include("song");
		gData.setPlaylistQuery(songs);
	}
	
	
	
	public void getData(){
		 user.fetchInBackground(new GetCallback<ParseObject>() {

				public void done(ParseObject object, ParseException e) {
		            if (e == null) {
		                ParseUser hello = (ParseUser) object;
		                 usedSize = hello.getNumber("usedSpace");
		        		if(usedSize == null){
		        			usedSize=0;
		        		//user.put("usedSpace", 0);
		        		song.setUsedSpace(0);
		        	user.put("usedSpace", 0);
					user.saveInBackground();	
		        		
		        	
		        		}
		        		
						totalSpace = hello.getNumber("totalSpace");
						if(totalSpace == null){
							//totalSpace =250;
							user.put("totalSpace", 100);
							user.saveInBackground();
							//song.setTotalSpace(0);
							totalSpace =100;

						}
						
						uploadCount = hello.getNumber("uploadCount");
						if(uploadCount == null){
							user.put("uploadCount", 0);
							user.saveInBackground();
							uploadCount = 0;
						}
						
						
						gData.setTotalSpace(totalSpace.intValue());
						gData.setUsedSpace(usedSize.intValue());
						gData.setUploadCount(uploadCount.intValue());
						// Toast.makeText(getApplicationContext(), ""+usedSize+totalSpace, 3).show();
	
		        		

		                // Do stuff with currUser
		            } else {
		            } 
		            
		        }});
	}

}

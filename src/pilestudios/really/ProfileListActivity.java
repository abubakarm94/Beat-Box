package pilestudios.really;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import pilestudios.async.globalData;
import pilestudios.beatbox.more.more;
import pilestudios.friends.friends;
import pilestudios.musicplayer.Playlist;
import pilestudios.musicplayer.PlaylistSelector;
import pilestudios.musicplayer.playlistAdapter;
import pilestudios.really.R;

public class ProfileListActivity extends ListActivity implements OnClickListener {
	ListView lv;
	private UserViewAdapter mUserViewAdapter;
	private List<ParseObject> objects;

	byte[] clickedSong;
	private MediaPlayer mediaPlayer;
	private SharedPreferences preferences;
	private playlistAdapter myDb;
	protected String selectedPlaylist;
	ParseObject selectedSong;
	
	private Playlist playlist;
	
	private final int permittedSpace = 250;
	private TextView usedSpace;
	private Number usedSize;
	private globalData gData;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_profile_list);
		
		lv = getListView();
		mUserViewAdapter = new UserViewAdapter(ProfileListActivity.this);

		preferences = getSharedPreferences("temp",
				getApplicationContext().MODE_PRIVATE);

		setListAdapter(mUserViewAdapter);
		
		final ParseUser user= ParseUser.getCurrentUser();
	//	Photo song = new Photo();
		//
		//displaying profile name
		TextView profilename = (TextView)findViewById(R.id.profile_name);
		profilename.setText(user.getString("displayName"));
		
		//displaying profile picture
		ImageView profilePic = (ImageView) findViewById(R.id.profile_picture);
		byte[] art = null;
		try {
			art = user.getParseFile("profilePictureSmall").getData();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Bitmap bmp = BitmapFactory.decodeByteArray(art, 0, art.length);
		// albumArt.setImageBitmap(helo.getAlbumArt());
		BitmapDrawable ob = new BitmapDrawable(getResources(), bmp);
		profilePic.setBackground(ob);
		
		final Photo photo = new Photo();
		
		//display the space the user is currently using
		 usedSpace = (TextView)findViewById(R.id.used_space);
		 
		  gData = globalData.getInstance(this);
 		usedSpace.setText(gData.getUsedSpace()+"Mb use out of "+gData.getTotalSpace()+"Mb");

		
		
		final Builder alert = new AlertDialog.Builder(this);
		
		final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        switch (which){
		        case DialogInterface.BUTTON_POSITIVE:
		            //Yes button clicked
		            break;

		        case DialogInterface.BUTTON_NEGATIVE:
		            //No button clicked
		            break;
		        }
		    }
		};

	lv.setOnItemLongClickListener(new OnItemLongClickListener(){

		;

		@Override
		public boolean onItemLongClick( final AdapterView<?> parent, final View view,
				final int position, long id) {
			// TODO Auto-generated method stub


			alert.setTitle("Delete "+mUserViewAdapter.getItem(position).getSongTitle());
			alert.setMessage("Are you sure? This is permanent");

			// Set an EditText view to get user input
		

			alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					final int songSize = mUserViewAdapter.getItem(position).getSongSize().intValue();
					
					ParseObject song =mUserViewAdapter.getItem(position).getObject();
					 song.deleteInBackground(new DeleteCallback(){

						@Override
						public void done(ParseException e) {
							// TODO Auto-generated method stub
							parent.removeViewInLayout(view);
						((BaseAdapter) lv.getAdapter()).notifyDataSetChanged();
						setListAdapter(mUserViewAdapter);
						
							
						//	final int currentSize = ParseUser.getCurrentUser().getNumber("usedSpace").intValue()-songSize;
						final int currentSize = gData.getUsedSpace()-songSize;
						
							final int uploadCount = gData.getUploadCount()-1;
							ParseUser.getCurrentUser().put("usedSpace", currentSize);
							ParseUser.getCurrentUser().put("uploadCount", uploadCount);
							ParseUser.getCurrentUser().saveInBackground(new SaveCallback(){

								@Override
								public void done(ParseException e) {
									// TODO Auto-generated method stub
									usedSpace.setText(currentSize+"Mb used out of "+gData.getTotalSpace()+"Mb");
								//	if(e == null)
								//	Toast.makeText(getApplicationContext(), "Success", 4).show();

								}
								
							});
						

						}
						 
					 });

				

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

			return false;
		}
		
	});

	/*
		final Button newsFeed = (Button) findViewById(R.id.newfeed);
		final Button profile = (Button) findViewById(R.id.profile_button);

		newsFeed.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// Toast.makeText(getApplicationContext(), "hello", 6).show();
				updateHomeList();
				newsFeed.setBackgroundResource(R.drawable.news_button);
				profile.setBackgroundResource(R.drawable.profile_notpressed);
				profile.setTextColor(Color.parseColor("#FF7559"));
				newsFeed.setTextColor(Color.WHITE);
				newsFeed.setGravity(Gravity.CENTER_VERTICAL);

			}

		});

		profile.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showUser();
				newsFeed.setBackgroundResource(R.drawable.profile_notpressed);
				profile.setBackgroundResource(R.drawable.newsfeed_notpressed);
				profile.setTextColor(Color.WHITE);
				newsFeed.setTextColor(Color.parseColor("#FF7559"));
				newsFeed.setGravity(Gravity.CENTER_VERTICAL);
				// FF2B54

			}

		});*/

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

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				// Photo clicked == parent.getItemAtPosition(position)
				// Toast.makeText(getApplicationContext(),
				// "Item clicked: " +
				// parent.getItemAtPosition(position).getClass().getName(),
				// Toast.LENGTH_SHORT).show();

				try {
					clickedSong = mUserViewAdapter.getItem(position).getSong()
							.getData();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});

		// Fetch Facebook user info if the session is active
		Session session = ParseFacebookUtils.getSession();
		if (session != null && session.isOpened()) {
			makeMeRequest();
		}

	}

	protected void onDestory() {
		super.onDestroy();
	}

	

	private void playMp3(byte[] mp3SoundByteArray) {
		try {
			// create temp file that will hold byte array
			File tempMp3 = File
					.createTempFile("kurchina", "mp3", getCacheDir());
			tempMp3.deleteOnExit();
			FileOutputStream fos = new FileOutputStream(tempMp3);
			fos.write(mp3SoundByteArray);
			fos.close();

			// Tried reusing instance of media player
			// but that resulted in system crashes...
			mediaPlayer = new MediaPlayer();

			// Tried passing path directly, but kept getting
			// "Prepare failed.: status=0x1"
			// so using file descriptor instead
			FileInputStream fis = new FileInputStream(tempMp3);
			mediaPlayer.setDataSource(fis.getFD());

			mediaPlayer.prepare();
			mediaPlayer.start();
		} catch (IOException ex) {
			String s = ex.toString();
			ex.printStackTrace();
		}
	}

	@Override
	public void onResume() {
		super.onResume();

		// Log.i(startApplication.TAG, "Entered HomeListActivity onResume()");

		ParseUser currentUser = ParseUser.getCurrentUser();
		if (currentUser != null) {
			// Check if the user is currently logged
			// and show any cached content

		} else {
			// If the user is not logged in, go to the
			// activity showing the login view.
			startLoginActivity();
		}
	}



	/*
	 * Posting pictures and refreshing the list will be controlled from the
	 * Action Bar.
	 */


	private void updateHomeList() {

	/*	usedSpace.setText(usedSize.intValue()+"Mb used out of "+permittedSpace+"Mb");
		mUserViewAdapter.loadObjects();
		setListAdapter(mUserViewAdapter);*/
		Intent  i = new Intent(this, HomeScreen.class);
		startActivity(i);
		finish();
	}

	private void showUser() {
		mUserViewAdapter.loadObjects();
		setListAdapter(mUserViewAdapter);
	}

	private void newPhoto() {
		Intent i = new Intent(this, NewPhotoActivity.class);
		startActivityForResult(i, 0);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			// If a new post has been added, update
			// the list of posts
			updateHomeList();
		}
	}

	/**
	 * Requesting and setting user data. Essentially, this is the User
	 * constructor
	 */
	private void makeMeRequest() {
		Request request = Request.newMeRequest(ParseFacebookUtils.getSession(),
				new Request.GraphUserCallback() {
					@Override
					public void onCompleted(GraphUser user, Response response) {
						if (user != null) {
							// get the relevant data using the GraphAPI
							// and store them as fields in our ParseUser

							/*
							 * User Model
							 * 
							 * displayName : String email : string
							 * profilePictureMedium : File profilePictureSmall :
							 * File facebookId : String facebookFriends : Array
							 * channel : String
							 * userAlreadyAutoFollowedFacebookFriends : boolean
							 */
							ParseUser currentUser = ParseUser.getCurrentUser();
							currentUser.put("facebookId", user.getId());
							currentUser.put("displayName", user.getName());
							// currentUser.put("profilePictureSmall", );
							byte[] hello = compressAndConvertImageToByteFrom(getProfilePic(
									user, currentUser));

							ParseFile saveImageFile = new ParseFile(
									"profileImage.jpg", hello);
							currentUser.put("profilePictureSmall",
									saveImageFile);

							currentUser.saveInBackground();

							// Make another facebook request to auto follow all
							// of
							// the current user's facebook friends who are using
							// Anypic
							if (currentUser
									.get("userAlreadyAutoFollowedFacebookFriends") != null
									&& ((Boolean) currentUser
											.get("userAlreadyAutoFollowedFacebookFriends"))) {
								// do nothing
								Log.i(startApplication.TAG,
										"Already followed facebook friends");
								// autoFollowFacebookFriendsRequest(); //might
								// be causing problems cause i didn't originally
								// have it there

							} else {
								autoFollowFacebookFriendsRequest();
							}
							// Associate the device with a user
							ParseInstallation installation = ParseInstallation
									.getCurrentInstallation();
							installation.put("user", currentUser);
							installation.saveInBackground();

							// handle errors accessing data from facebook
						} else if (response.getError() != null) {
							if ((response.getError().getCategory() == FacebookRequestError.Category.AUTHENTICATION_RETRY)
									|| (response.getError().getCategory() == FacebookRequestError.Category.AUTHENTICATION_REOPEN_SESSION)) {
								Log.i(startApplication.TAG,
										"The facebook session was invalidated.");
								onLogoutButtonClicked();
							} else {
								Log.i(startApplication.TAG,
										"Some other error: "
												+ response.getError()
														.getErrorMessage());
							}
						}
					}
				});
		request.executeAsync();

	}

	public byte[] compressAndConvertImageToByteFrom(Bitmap imageBitmap) {

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		if (imageBitmap == null) {
			Log.i("is null", "contains nothing!!!!!!");
		}
		imageBitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
		return stream.toByteArray();

	}

	public Bitmap getProfilePic(GraphUser user, ParseUser currentUser) {

		Bitmap userImage = null;

		URL img_value = null;
		try {
			img_value = new URL("https://graph.facebook.com/" + user.getId()
					+ "/picture?type=large");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();

			StrictMode.setThreadPolicy(policy);
			Bitmap mIcon1 = BitmapFactory.decodeStream(img_value
					.openConnection().getInputStream());

			userImage = mIcon1;

			// mIcon1=null;
			Log.i("STATUS", "image retrieved from facebook");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return userImage;
	}

	/**
	 * This function performs a request to the Facebook Graph API, which finds
	 * all the friends of the current ParseUser and checks if any of them
	 * currently use Anypic. If so, then it automatically follows those friends
	 * on Anypic, by creating new Activity relationships.
	 */
	private void autoFollowFacebookFriendsRequest() {
		Request request = Request.newMyFriendsRequest(
				ParseFacebookUtils.getSession(),
				new Request.GraphUserListCallback() {
					@Override
					public void onCompleted(List<GraphUser> friendList,
							Response response) {
						if (friendList != null) {
							List<String> ids = toIdsList(friendList);

							// Select * From Users Where User.facebookID is
							// contained in
							// the list of IDs of users returned from the
							// GraphApi
							ParseQuery<ParseUser> friendsQuery = ParseUser
									.getQuery();
							friendsQuery.whereContainedIn("facebookId", ids);
							friendsQuery
									.findInBackground(new FindCallback<ParseUser>() {
										@Override
										public void done(
												List<ParseUser> objects,
												ParseException e) {
											if (e == null && objects != null) {
												// friendsQuery successful,
												// follow these users
												ParseUser currentUser = ParseUser
														.getCurrentUser();
												for (ParseUser friend : objects) {
													pilestudios.really.Activity followActivity = new pilestudios.really.Activity();
													followActivity
															.setFromUser(currentUser);
													followActivity
															.setToUser(friend);
													followActivity
															.setType("follow");

													followActivity
															.saveEventually();

													// set the user to follow
													// you too MIGHT HAVE
													// PROBLEMS cause it wasn't
													// there beofre
													followActivity
															.setFromUser(friend);
													followActivity
															.setToUser(currentUser);
													followActivity
															.setType("follow");
													followActivity
															.saveEventually();

												}
												currentUser
														.put("userAlreadyAutoFollowedFacebookFriends",
																true);
												currentUser.saveInBackground();
											} else {
												// friendsQuery failed
												Log.i(startApplication.TAG,
														"Query to find facebook friends in Parse failed");
											}
										}
									}); // end findInBackground()

							// handle errors from facebook
						} else if (response.getError() != null) {
							if ((response.getError().getCategory() == FacebookRequestError.Category.AUTHENTICATION_RETRY)
									|| (response.getError().getCategory() == FacebookRequestError.Category.AUTHENTICATION_REOPEN_SESSION)) {
								Log.i(startApplication.TAG,
										"The facebook session was invalidated.");
								onLogoutButtonClicked();
							} else {
								Log.i(startApplication.TAG,
										"Some other error: "
												+ response.getError()
														.getErrorMessage());
							}
						}

					}
				});// end GraphUserListCallback
		request.executeAsync();
	}

	// Take a list of Facebook GraphUsers and return a list of their IDs
	private List<String> toIdsList(List<GraphUser> fbUsers) {
		List<String> ids = new ArrayList<String>();

		for (GraphUser user : fbUsers) {
			ids.add(user.getId());
		}
		return ids;
	}

	private void onLogoutButtonClicked() {
		// close this user's session
		ParseFacebookUtils.getSession().closeAndClearTokenInformation();
		// Log the user out
		ParseUser.logOut();
		// Go to the login view
		startLoginActivity();
	}

	private void startLoginActivity() {
		Intent intent = new Intent(this, LoginActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		finish();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.upload:
			newPhoto();
			break;
		case R.id.refresh:
			updateHomeList();
			break;
		case R.id.logout_button:
			//onLogoutButtonClicked();
			Intent b = new Intent(this, more.class);
			startActivity(b);
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

	}

}

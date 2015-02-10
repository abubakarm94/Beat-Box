package pilestudios.really;

import java.util.ArrayList;
import java.util.List;

import android.app.Application;
import android.graphics.Bitmap;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.PushService;

import pilestudios.musicplayer.Playlist;
import pilestudios.really.R;

public class startApplication extends Application {

	static final String TAG = "Panagram";

	@Override
	public void onCreate() {
		super.onCreate();

		/*
		 * In this tutorial, we'll subclass ParseObjects for convenience to
		 * create and modify Photo objects.
		 * 
		 * Also, we'll use an Activity class to keep track of the relationships
		 * of ParseUsers with each other and Photos. Every time a user follows,
		 * likes or comments, a new activity is created to represent the
		 * relationship.
		 */
		ParseObject.registerSubclass(Photo.class);
		ParseObject.registerSubclass(Activity.class);
		ParseObject.registerSubclass(Playlist.class);

//		Parse.enableLocalDatastore(this);
		/*
		 * Fill in this section with your Parse credentials
		 */
		Parse.initialize(this, "KOeI8CM7z0yeH5ZBa7MoEhG5YQjGipkkBO6oFAUV",
				"U7jVsWxDzgOeqFSZCEwzutlbIWhVhiUWJ9ld0Us2");

		// Set your Facebook App Id in strings.xml
		ParseFacebookUtils.initialize(getString(R.string.app_id));

		/*
		 * For more information on app security and Parse ACL:
		 * https://www.parse.com/docs/android_guide#security-recommendations
		 */
		ParseACL defaultACL = new ParseACL();

		/*
		 * If you would like all objects to be private by default, remove this
		 * line
		 */
		defaultACL.setPublicReadAccess(true);

		/*
		 * Default ACL is public read access, and user read/write access
		 */
		ParseACL.setDefaultACL(defaultACL, true);

		/*
		 * Register for push notifications.
		 */
		PushService.setDefaultPushCallback(this, LoginActivity.class);
		ParseInstallation.getCurrentInstallation().saveInBackground();
	}

	private String currentSong;
	private String songTitle = "";
	private Bitmap albumArt;
    List<String> friendsList;
    private String songExtension;
    private String artistName;
    


	public String getSong() {
		return currentSong;
	}
	public void setExtension(String extend){
		songExtension = extend;
	}

public String getSongExtension(){
	return songExtension;
}
	public Bitmap getAlbumArt(){
		return albumArt;
	}
	public void setAlbumArt(Bitmap art){
		albumArt = art;
	}
	public void setSong(String song) {
		currentSong = song;
	}

	public String getSongTitle() {
		return songTitle;
	}
	
	public void setArtistName(String song) {
		artistName = song;
	}

	public String getArtistName() {
		return artistName;
	}
	
	public List<String> getFriends(){
		
		return friendsList;
	}
	public void setFriendsList(List<String> friendsList2){
		friendsList = friendsList2;
	}

	public void setSongTitle(String title) {
		songTitle = title;
	}

}

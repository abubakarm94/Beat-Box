package pilestudios.async;

import com.parse.ParseQuery;

import pilestudios.musicplayer.MusicService;
import pilestudios.musicplayer.Playlist;
import pilestudios.really.Photo;
import android.content.Context;

public class globalData {

	private static globalData gInstance = null;
	
	private int usedSpace, totalSpace, uploadCount;
	private ParseQuery<Playlist> playlistSongs;
	ParseQuery<Photo> newsFeedQuery;
	
	
	public globalData(Context context) {
		// TODO Auto-generated constructor stub
	}


	public static synchronized globalData getInstance(Context context) {
		if (gInstance == null) {
			gInstance = new globalData(context);
		}

		return gInstance;

	}
	
	//sets the songs in the playlist
	public void setPlaylistQuery(ParseQuery<Playlist> query){
		playlistSongs = query;
	}
	
	//returns the songs in the playlist
	public ParseQuery<Playlist> getPlaylistQuery(){
		return playlistSongs;
	}
	
	//sets the songs in the newsfeed
		public void setNewsFeedQuery(ParseQuery<Photo> query){
			newsFeedQuery = query;
		}
		
		//returns the songs in the playlist
		public ParseQuery<Photo> getNewsFeedQuery(){
			return newsFeedQuery;
		}
	
	//returns the amount of space the user has available
	public int getUsedSpace(){
		return usedSpace;
	}
	
	//returns the number  of songs uploaded
	public int getUploadCount(){
		return uploadCount;
	}
	
	//sets the number of songs uploaded
	public void setUploadCount(int amount){
		 uploadCount = amount;
	}
	
	//sets the amount of space the user has used
	public void setUsedSpace(int amount){
		usedSpace = amount;
	}
	
	//returns the total amount of space the user has available
	public int getTotalSpace(){
		return totalSpace;
	}
	
	//sets the amount of space the user has available 
	public void setTotalSpace(int amount){
		totalSpace = amount;
	}
	
}

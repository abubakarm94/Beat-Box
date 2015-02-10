/*
 * Playlist parse object
 */

package pilestudios.musicplayer;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

/*
 * An extension of ParseObject that makes
 * it more convenient to access information
 * about a given Photo 
 */

@ParseClassName("Playlist")
public class Playlist extends ParseObject {

	public Playlist() {
		// A default constructor is required.
	}

	//get the playlist name
	public String getPlaylistName() {

		return getString("playlistName");
	}

	//sets the playlist name
	public void setPlaylistName(String name) {
		put("playlistName", name);
	}

	//gets parse user
	public ParseUser getUser() {
		return getParseUser("user");
	}

	//sets parse user
	public void setUser(ParseUser user) {
		put("user", user);
	}

	//gets the song title
	public String getSongTitle() {
		return getSong().getString("songTitle");
	}

	//gets the artist name
	public String getSongAuthor() {
		return getSong().getString("artistName");
	}
	
	//gets the song thumbnail
	public byte[] getThumbnail() throws ParseException{
		return getSong().getParseFile("thumbnail").getData();
	}
	
	//gets the song album art
	public byte[] getImage() throws ParseException{
		return getSong().getParseFile("image").getData();
	}

	//gets the song object
	public ParseObject getSong() {
		return getParseObject("song");
	}
	
	//gets the song url
	public String getSongUrl(){
		return getSong().getParseFile("song").getUrl();
	}

	//sets the song object
	public void setSong(ParseObject selectedSong) {
		put("song", selectedSong);
	}

}

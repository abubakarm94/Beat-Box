package pilestudios.really;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
//This activity controls the meta data of a song uploaded
//sets up the artistname, spaceused,songtitle, songfile, etc

/*
 * An extension of ParseObject that makes
 * it more convenient to access information
 * about a given Song 
 */

@ParseClassName("Photo")
public class Photo extends ParseObject {

	public Photo() {
		// A default constructor is required.
	}
	public String getArtistName(){
		
		return getString("artistName");
	}
	public ParseObject getObject(){
		return this;
	}
	
	//sets the total space the user has
	public void setTotalSpace(Number amount){
		put("totalSpace", amount);
	}
	
	public Number getTotalSpace(){
		return ParseUser.getCurrentUser().getNumber("totalSpace");
	}
	
	public Number getUploadCount(){
		return ParseUser.getCurrentUser().getNumber("uploadCount");
	}
	public void setUploadCount(Number count){
		ParseUser.getCurrentUser().put("uploadCount", count);
	}
	//returns the total space the user is currently using
	public Number getUsedSpace(){
		return ParseUser.getCurrentUser().getNumber("usedSpace");
	}

	//sets the total space the user is currently using
	public void setUsedSpace(Number amount){
		ParseUser.getCurrentUser().put("usedSpace", amount);
	}
	public void setSongSize(Number size){
		put("songSize", size);
	}
	public Number getSongSize(){
		return getNumber("songSize");
	}
	public void setArtistName(String name){
		put("artistName",name);
	}
	public void setVisiblity(boolean status){
		put("visibility", status);
	}
	public boolean getVisiblity(){
		return getBoolean("visibility");
	}
	
	public ParseFile getImage() {
		return getParseFile("image");
	}

	public void setImage(ParseFile file) {
		put("image", file);
	}
	
	public ParseFile getSong() {
		return getParseFile("song");
	}
	
	public String getSongTitle(){
		return getString("songTitle");
	}
	
	public void setSongTitle(String title){
		put("songTitle", title);
	}

	public void setSong(ParseFile file) {
		put("song", file);
	}

	public ParseUser getUser() {
		return getParseUser("user");
	}

	public void setUser(ParseUser user) {
		put("user", user);
	}
	
	public String getSongUrl(){
		return getSong().getUrl();
		
	}

	public ParseFile getThumbnail() {
		return getParseFile("thumbnail");
	}

	public void setThumbnail(ParseFile file) {
		put("thumbnail", file);
	}

}

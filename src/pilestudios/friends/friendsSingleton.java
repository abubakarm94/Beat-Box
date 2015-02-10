/* Friend singleton sets parse user and returns parse user
 * 
 */
package pilestudios.friends;

import com.parse.ParseUser;

import pilestudios.musicplayer.MusicService;

public class friendsSingleton {
	
	//the instance of this class
	private static friendsSingleton frInstance = null;
	
	//variable of parse user
	private ParseUser user;
	
	//returns a synchronized class instance
	public static synchronized friendsSingleton getInstance(){
		if (frInstance == null){
			frInstance = new friendsSingleton();
		}
		return frInstance;
		
	}
	
	//return parse user
	public ParseUser getUser(){
		return user;
	}
	
	//sets the parse user 
	public void setUser(ParseUser person){
		this.user = person;
	}
	
}

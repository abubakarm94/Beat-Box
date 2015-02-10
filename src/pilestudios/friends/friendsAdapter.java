/* This activity queries parse for all of a users friends
 * 
 */
package pilestudios.friends;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.Deflater;

import android.content.Context;
import android.content.Intent;
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

import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.GraphUser;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import pilestudios.musicplayer.MusicPlayer;
import pilestudios.really.Activity;
import pilestudios.really.Photo;
import pilestudios.really.R;
import pilestudios.really.startApplication;


public class friendsAdapter extends ParseQueryAdapter<Activity> {

	@SuppressWarnings("unchecked")
	public friendsAdapter(final Context context) {
		super(context, new ParseQueryAdapter.QueryFactory<Activity>() {
			public ParseQuery<Activity> create() {

				//gets the current user and saves it to a variable
				ParseUser user = ParseUser.getCurrentUser();

				//gets the friends by finding out if the user if toUser in Activity
				ParseQuery<pilestudios.really.Activity> friends = new ParseQuery<pilestudios.really.Activity>(
						"Activity");
				friends.whereEqualTo("toUser", user);
				friends.include("fromUser");
				friends.orderByDescending("createdAt");

				
				return friends;

			}
		});

	}
	
	
	//uses the query and sets the results to listview elements
	@Override
	public View getItemView(Activity photo, View v, ViewGroup parent) {

		if (v == null) {
			v = View.inflate(getContext(), R.layout.friends_list_item, null);
		}
		super.getItemView(photo, v, parent);
		
		//gets the friend and saves it to a variable
		ParseUser user = photo.getFromUser();
		

		//gets the friends display name and sets it to a textview 
		TextView usernameView = (TextView) v.findViewById(R.id.displayName);
		usernameView.setText(user.getString("displayName"));
		
		//gets how many songs a friend has uploaded and sets it to a textview 
		TextView uploadCount = (TextView) v.findViewById(R.id.uploadCount);
		uploadCount.setText(user.getNumber("uploadCount")+" songs");

		//get the friends profile picture
		ImageView profilePicture = (ImageView) v.findViewById(R.id.photo);
		byte[] photoFile = null;
		try {
			photoFile = user.getParseFile("profilePictureSmall").getData();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//converts the byte array photo to bitmap and sets the profile picture
		Bitmap helo = BitmapFactory.decodeByteArray(photoFile, 0,
				photoFile.length);
		if (helo != null) {
			profilePicture.setImageBitmap(helo);
		}

		return v;
	}

}
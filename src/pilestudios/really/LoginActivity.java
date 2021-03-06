package pilestudios.really;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.GraphUser;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.ParseException;
import com.parse.LogInCallback;

import pilestudios.async.startNewFeeds;
import pilestudios.really.R;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.os.Build;

public class LoginActivity extends Activity {

	private Button loginButton;
	private Dialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

		setContentView(R.layout.activity_login);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.home_status);

		Parse.initialize(this, "YOUR_APPLICATION_ID",
				"YOUR_CLIENT_KEY");

		// Set your Facebook App Id in strings.xml
		ParseFacebookUtils.initialize(("YOUR FACEBOOK APP ID"));

		TextView back = (TextView) findViewById(R.id.statusbar_back);
		back.setVisibility(View.GONE);

		TextView nowPlaying = (TextView) findViewById(R.id.nowPlaying);
		nowPlaying.setVisibility(View.GONE);


		loginButton = (Button) findViewById(R.id.loginButton);
		loginButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.i(startApplication.TAG, "Login button clicked");
				onLoginButtonClicked();
			}
		});

		// Check if there is a currently logged in user
		// and they are linked to a Facebook account.
		ParseUser currentUser = ParseUser.getCurrentUser();
		if ((currentUser != null) && ParseFacebookUtils.isLinked(currentUser)) {
			// Go to the main photo list view activity
			getFriends();
			showHomeListActivity();
		}

		// For push notifications
		ParseAnalytics.trackAppOpened(getIntent());
	}

	public void getFriends() {
		Request.executeMyFriendsRequestAsync(ParseFacebookUtils.getSession(),
				new Request.GraphUserListCallback() {

					@Override
					public void onCompleted(List<GraphUser> users,
							Response response) {
						// TODO Auto-generated method stub
						if (users != null) {
							List<String> friendsList = new ArrayList<String>();
							for (GraphUser user : users) {
								friendsList.add(user.getId());
							}

							startApplication globalStatus = (startApplication) getApplicationContext();
							globalStatus.setFriendsList(friendsList);

						}
					}
				});
	}

	private void onLoginButtonClicked() {
		LoginActivity.this.progressDialog = ProgressDialog.show(
				LoginActivity.this, "", "Logging in...", true);
		List<String> permissions = Arrays.asList("public_profile",
				"user_about_me", "user_friends");
		ParseFacebookUtils.logIn(permissions, this, new LogInCallback() {
			@Override
			public void done(ParseUser user, ParseException err) {
				LoginActivity.this.progressDialog.dismiss();
				if (user == null) {

					Log.i(startApplication.TAG,
							"Uh oh. The user cancelled the Facebook login.");
				} else if (user.isNew()) {
					Log.i(startApplication.TAG,
							"User signed up and logged in through Facebook!");
					showHomeListActivity();
				} else {
					Log.i(startApplication.TAG,
							"User logged in through Facebook!");
					showHomeListActivity();
				}
			}
		});
	}

	/**
	 * Used to provide "single sign-on" for users who don't have the Facebook
	 * app installed
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
	}

	private void showHomeListActivity() {
		// Log.i(startApplication.TAG, "entered showHomeListActivity");
		Intent intent = new Intent(this, startNewFeeds.class);
		startActivity(intent);
		finish(); // This closes the login screen so it's not on the back stack
	}

	/***************************************************************************/

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_login,
					container, false);
			return rootView;
		}
	}

}

//This activity is used to log off, get more usage space, launch the frequently asked question
package pilestudios.beatbox.more;

import com.android.vending.billing.IInAppBillingService;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.payment.helper.IabHelper;
import com.payment.helper.IabResult;
import com.payment.helper.Purchase;
import com.payment.helper.Inventory;

import pilestudios.async.globalData;
import pilestudios.musicplayer.MusicPlayer;
import pilestudios.musicplayer.MusicService;
import pilestudios.musicplayer.playlistMusicPlayer;
import pilestudios.musicplayer.playlistService;
import pilestudios.really.HomeScreen;
import pilestudios.really.LoginActivity;
import pilestudios.really.R;
import android.app.Activity;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class more extends Activity implements OnClickListener {

	private MusicService s;
	private playlistService tempStorage;
	private TextView nowPlaying, logout, faq, buyCredit;

	private IabHelper mHelper;
	private String buyId = "extra_gigabyte";
	// android.test.purchased
	private boolean mBillingServiceReady;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

		setContentView(R.layout.activity_more);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.home_status);
		
		//start the music service instance
		s = MusicService.getInstance(this);

		//hide the back button on the status bar
		TextView back = (TextView) findViewById(R.id.statusbar_back);
		back.setVisibility(View.GONE);
		
		//start the playlist service instance
		tempStorage = playlistService.getInstance();

		//if any song if playing from the music service or playlist service and launch accordingly
		nowPlaying = (TextView) findViewById(R.id.nowPlaying);
		nowPlaying.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (tempStorage.isPlaylistPlaying == true) {
					Intent i = new Intent(more.this, playlistMusicPlayer.class);
					startActivity(i);
				} else {
					Intent a = new Intent(more.this, MusicPlayer.class);
					startActivity(a);
				}
			}

		});

		//if any song if playing from the music service or playlist service show the now playing service
		if (tempStorage.isPlaylistPlaying == true || s.isPlaying == true) {
			nowPlaying.setVisibility(View.VISIBLE);
		} else {
			nowPlaying.setVisibility(View.GONE);
		}

		//this textview is used as button to lauch the faq class, log out and also get more space
		faq = (TextView) findViewById(R.id.faq);
		logout = (TextView) findViewById(R.id.logout);
		buyCredit = (TextView) findViewById(R.id.buyButton);

		faq.setOnClickListener(this);
		logout.setOnClickListener(this);
		buyCredit.setOnClickListener(this);

	//	initialiseBilling();
	}

	@Override
	public void onResume() {
		super.onResume();

		if (tempStorage.isPlaylistPlaying == true || s.isPlaying == true) {
			nowPlaying.setVisibility(View.VISIBLE);
		} else {
			nowPlaying.setVisibility(View.GONE);
		}
		initialiseBilling();

	}

	private void initialiseBilling() {
		if (mHelper != null) {

			return;
		}

		// Create the helper, passing it our context and the public key to
		// verify signatures with
		mHelper = new IabHelper(
				this,
				"YOUR BILLING PUBLIC KEY");

		mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {

			@Override
			public void onIabSetupFinished(IabResult result) {

				// Have we been disposed of in the meantime? If so, quit.
				if (mHelper == null) {
					Toast.makeText(more.this, "set up finihed",
							Toast.LENGTH_LONG).show();
					return;
				}

				if (!result.isSuccess()) {
					// Oh noes, there was a problem.
					Log.i("Problem setting up in-app billing: "
							+ result.getMessage().toString(), "");
					Toast.makeText(more.this, "problem with billing",
							Toast.LENGTH_LONG).show();
					return;
				}

				// IAB is fully set up.
				// Toast.makeText(more.this, "Thank you for upgrading!",
				// Toast.LENGTH_LONG).show();
				mBillingServiceReady = true;
				mHelper.queryInventoryAsync(iabInventoryListener());
				// Custom function to update UI reflecting their inventory
				// updateInventoryUI();
			}

		});

	}

	// consume all the purchased item at this point
	private IabHelper.QueryInventoryFinishedListener iabInventoryListener() {
		return new IabHelper.QueryInventoryFinishedListener() {
			@Override
			public void onQueryInventoryFinished(IabResult result,
					Inventory inventory) {
				// Have we been disposed of in the meantime? If so, quit.
				if (mHelper == null) {
					return;
				}

				// Something went wrong
				if (!result.isSuccess()) {

					return;
				}

				if (inventory.hasPurchase(buyId)) {
					// isItemEnable= true;
					mHelper.consumeAsync(inventory.getPurchase(buyId), null);
				} else {
					// isItemEnable = false;
				}

				// Do your checks here...

				// Do we have the premium upgrade?
				// Purchase purchasePro = inventory.getPurchase(G.SKU_PRO); //
				// Where G.SKU_PRO is your product ID (eg. permanent.ad_removal)
				// G.settings.isPro = (purchasePro != null &&
				// G.verifyDeveloperPayload(purchasePro));

				// After checking inventory, re-jig stuff which the user can
				// access now
				// that we've determined what they've purchased
				// G.initialiseStuff();
			}
		};
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (mHelper == null) {
			return;
		}

		// Pass on the activity result to the helper for handling
		if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
			// not handled, so handle it ourselves (here's where you'd
			// perform any handling of activity results not related to in-app
			// billing...
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	/**
	 * Very important
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();

		if (mHelper != null) {
			mHelper.dispose();
			mHelper = null;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.logout:
			onLogoutButtonClicked();
			break;

		case R.id.faq:

			Intent i = new Intent(this, faq.class);
			startActivity(i);
			break;

		case R.id.buyButton:
			// Toast.makeText(getApplicationContext(), "faq", 4).show();
			if (!mBillingServiceReady) {
				Toast.makeText(
						more.this,
						"Purchase requires Google Play Store (billing) on your Android.",
						Toast.LENGTH_LONG).show();
				return;
			}

			// String payload = generatePayloadForSKU(G.SKU_PRO); // This is
			// based
			// off your own
			// implementation.

			mHelper.launchPurchaseFlow(more.this, buyId, 10001,
					mPurchaseFinishedListener,
					"bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ");
			break;

		}

	}

	private IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {

		@Override
		public void onIabPurchaseFinished(IabResult result, Purchase purchase) {

			// if we were disposed of in the meantime, quit.

			if (mHelper == null) {

				return;

			}

			// Don't complain if cancelling

			if (result.getResponse() == IabHelper.IABHELPER_USER_CANCELLED) {

				return;

			}

			if (!result.isSuccess()) {

				// complain("Error purchasing: " + result.getMessage());
				Toast.makeText(more.this,
						"Error purchasing: contact the developer",
						Toast.LENGTH_LONG).show();
				return;

			}

			/*
			 * if (!G.verifyDeveloperPayload(purchase)) {
			 * 
			 * complain("Error purchasing. Authenticity verification failed.");
			 * 
			 * return;
			 * 
			 * }
			 */

			// Purchase was success! Update accordingly
			if (purchase.getSku().equals(buyId)) {

				Toast.makeText(more.this, "Thank you for your purchase!",
						Toast.LENGTH_LONG).show();
				globalData gData = globalData.getInstance(more.this);
				int temp = gData.getTotalSpace();
				gData.setTotalSpace(temp + 1024);
				ParseUser.getCurrentUser().put("totalSpace", temp + 1024);
				ParseUser.getCurrentUser().saveInBackground();

				// G.settings.isPro = true;

				// G.initialiseStuff();

				// Update the UI to reflect their latest purchase

				// updateInventoryUI();
			}

		}

	};

}

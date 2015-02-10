/* This activity is launched by loginActivity 
 * It starts the Homescreen after 2 secs
 * 
 */

package pilestudios.async;

import pilestudios.friends.FriendsViewSongs;
import pilestudios.really.HomeListActivity;
import pilestudios.really.HomeScreen;
import pilestudios.really.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

public class startNewFeeds extends Activity implements Runnable{
    private final int WAIT_TIME = 25000;
    ProgressBar progress;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_startnewfeed);
		
	
		//progress bar
		progress =(ProgressBar) findViewById(R.id.progressBar);

		Thread mThread = new Thread(this);

        mThread.start();
	      }
	

	 public void run() 
	    {
	        try
	        {
	            Thread.sleep(2000);
		      //  progress.incrementProgressBy(10);

	        }
	        catch (Exception e) 
	        {
	            e.printStackTrace();
	        }
	        finally
	        {
	            startActivity(new Intent(this, HomeScreen.class));

	            finish();
	        }
	    }
		        
	
	
		   
	}
	
	



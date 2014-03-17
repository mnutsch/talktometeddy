package com.talkingteddy;

import Library.DatabaseHandler;
import Library.UserFunctions;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.GoogleAnalytics;

import com.talkingteddy.R;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Locale;

public class TalkingTeddyActivity extends Activity implements OnInitListener {

	private long longitem = 0; // used by Google Analytics

	private ImageButton heartSpeakButton = null;

	private static TextToSpeech tts;

	private static Context context;

	private static DownloadAndProcessXML downloadAndProcessXML;

	private static final int RESULT_SPEECH = 1;
	
	private UserFunctions userFunctions;
	
	public static void GenerateOutput(String returnedText) {
		Helper.startTTS(returnedText, tts, context);
	}

	/**
	 * Called when the activity is first created. This is where we'll hook up
	 * our views in XML layout files to our application.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Helper.previousScenario = "998";
		Helper.context = this.getApplicationContext();
		super.onCreate(savedInstanceState);
		
		if (BuildConfig.DEBUG) {
			GoogleAnalytics googleAnalytics = GoogleAnalytics
					.getInstance(getApplicationContext());
			googleAnalytics.setAppOptOut(true);
		}
		
		/**
         * Dashboard Screen for the application
         * */       
        // Check login status in database
        userFunctions = new UserFunctions();
        if(!userFunctions.isUserLoggedIn(getApplicationContext())){
        	
        	 // user is not logged in show login screen
            Intent login = new Intent(getApplicationContext(), LoginActivity.class);
            login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(login);
            // Closing dashboard screen
            finish();
        }       
        else{
        	// user already logged in show databoard
			setContentView(R.layout.speech);
	
			tts = new TextToSpeech(this, this);
			tts.setSpeechRate(0.95f);
			context = this;
			
			EasyTracker.getInstance().setContext(context);
	
			heartSpeakButton = (ImageButton) findViewById(R.id.heartSpeakButton);
			heartSpeakButton.setOnClickListener(new View.OnClickListener() {
	
				@Override
				public void onClick(View v) {
					PerformRecongnition();
				}
			});
        }
	}

	/**
	 * Called when Google Speech-to-text returns us recognition result.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case RESULT_SPEECH: {
			if (resultCode == RESULT_OK && null != data) {

				ArrayList<String> text = data
						.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

				matchTaskWithSpeech(text.get(0).toString());
			}
			break;
		}

		}
	}

	@Override
	public void onInit(int status) {
		// TODO Auto-generated method stub

		if (status == TextToSpeech.SUCCESS) {

			int result = tts.setLanguage(Locale.US);

			if (result == TextToSpeech.LANG_MISSING_DATA
					|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
				Toast.makeText(context, "Language not supported",
						Toast.LENGTH_LONG).show();
				Log.e("TTS", "Language is not supported");
			}
			
			ExecuteServerCall("DummyText");

		} else {
			Log.e("TTS", "Initilization Failed");
		}

	}

	@Override
	public void onStop() {
		super.onStop();
		EasyTracker.getInstance().activityStop(this); // Add this method.
	}

	@Override
	public void onDestroy() {
		/**
		 * Properly close the TTS when the activity is destroyed.
		 */
		if (tts != null) {
			tts.stop();
			tts.shutdown();
			Log.d("TTS", "TTS Destroyed");
		}

		super.onDestroy();

	}
	
	// Initiating Menu XML file (menu.xml)
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.layout.menu, menu);
        return true;
    }
    
    /**
     * Event Handling for Individual menu item selected
     * Identify single menu item by it's id
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
         
        switch (item.getItemId())
        {
        case R.id.menu_logout:
            // Single menu item is selected do something
        	UserFunctions userFunction = new UserFunctions();
        	if (userFunction.logoutUser(getApplicationContext()))
        	{
            // Ex: launching new activity/screen or show alert message
        		Toast.makeText(TalkingTeddyActivity.this, "User logged out.", Toast.LENGTH_SHORT).show();
        		// Launch Dashboard Screen
                Intent loginActivity = new Intent(getApplicationContext(), LoginActivity.class);
                 
                // Close all views before launching Dashboard
                loginActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(loginActivity);
                 
                // Close Login Screen
                finish();
        	}
        	else
        	{
        		Toast.makeText(TalkingTeddyActivity.this, "There was some error in logging user out. Try again.", Toast.LENGTH_SHORT).show();
        	}
            return true;

        default:
            return super.onOptionsItemSelected(item);
        }
    }    
	
	/**
	 * Matches the text with one of the tasks, and start the TTS with matched
	 * task.
	 * 
	 * @param speechText
	 *            recognized speech text used to match task for talk-back
	 */
	private void matchTaskWithSpeech(String speechText) {
		// report data to google analytics
		EasyTracker.getTracker().sendEvent("internal", "general",
				"speech_to_text_recognition_successful", longitem); // Google
																	// Analytics
																	// event
		EasyTracker.getTracker().sendEvent("conversation", "user_input",
				speechText, longitem); // Google Analytics event

		Helper.showToast(speechText, context);
		// And then perform a search on a website using the text.
		String query = URLEncoder.encode(speechText);
		ExecuteServerCall(query);
		
	}
	
	private void ExecuteServerCall(String speechText)
	{
		String recognitionURL = Helper.getRecognitionURL(speechText);
		Log.v("Url for API2",recognitionURL);
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			Helper.showToast("Thinking...", context);
			downloadAndProcessXML = new DownloadAndProcessXML();
			downloadAndProcessXML.execute(recognitionURL, speechText);
			
		} else {
			Helper.startTTS(
					"Teddy needs internet connection to work properly.", tts,
					context);
		}
	}
	
	private void PerformRecongnition()
	{
		Helper.stopTTS(tts, downloadAndProcessXML);
		EasyTracker.getTracker().sendEvent("ui-action", "button_press",
				"speak_button", longitem);
		Intent intent = new Intent(
				RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");

		try {
			startActivityForResult(intent, RESULT_SPEECH);
		} catch (ActivityNotFoundException a) {
			Helper.showToast(
					"Oops! Your device doesn't support Speech to Text",
					context);
		}
	}
}
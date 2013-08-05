package com.talkingteddy;

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
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.GoogleAnalytics;

import com.talkingteddy.R;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TalkingTeddyActivity extends Activity implements OnInitListener {

	private long longitem = 0; // used by Google Analytics

	private ImageButton heartSpeakButton = null;

	private static TextToSpeech tts;

	private static Context context;
	
	private static TaskDiscriminator taskDiscriminator;

	private static DownloadAndProcessXML downloadAndProcessXML;

	private static final int RESULT_SPEECH = 1;
	
	public static void GenerateOutput(ResponseDigest respDigest) {
		System.out.println("in generateOutput");
		Task task = taskDiscriminator.getTask(respDigest);
		Helper.startTTS(task.getRandomSpeechAnswer(), tts, context);
	}

	/**
	 * Called when the activity is first created. This is where we'll hook up
	 * our views in XML layout files to our application.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		if (BuildConfig.DEBUG) {
			GoogleAnalytics googleAnalytics = GoogleAnalytics
					.getInstance(getApplicationContext());
			googleAnalytics.setAppOptOut(true);
		}

		super.onCreate(savedInstanceState);

		setContentView(R.layout.speech);

		tts = new TextToSpeech(this, this);
		tts.setSpeechRate(0.9f);
		context = this;
		taskDiscriminator = new TaskDiscriminator(context);
		
		EasyTracker.getInstance().setContext(context);

		heartSpeakButton = (ImageButton) findViewById(R.id.heartSpeakButton);
		heartSpeakButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				PerformRecongnition();
			}
		});
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
			Helper.greet(tts, context);

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
		List<Task> tasks = taskDiscriminator.enumeratedTasks();
		
		String recognitionURL = Helper.getRecognitionURL(tasks, query);

		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			Helper.showToast("Thnking...", context);
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
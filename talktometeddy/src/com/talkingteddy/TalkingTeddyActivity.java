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

	protected static final int RESULT_SPEECH = 1;

	long longitem = 0; // used by Google Analytics

	private ImageButton heartSpeakButton = null;

	private TextToSpeech tts;

	private String apikey = "962b2d2b8e72dc6771bca613d49b46fb";

	private DownloadAndProcessXML downloadAndProcessXML;

	private Context context;
	
	private TaskDiscriminator taskDiscriminator;

	// strings for Teddy
	private String greeting1 = "Hey Kiddoe! Press my belly and talk to me.";
	private String greeting2 = "Hey Buddy! Press my belly and talk to me.";
	private String greeting3 = "Hello! Press my belly and talk to me.";

	private String task1Q_encoded = "prime+colors";
	private String task1Q_decoded = "prime colors";
	private String task1A1 = "The prime colors are red, blue and yellow.";
	private String task1A2 = "Combining the colors yellow and blue makes green.";
	private String task1A3 = "Mixing the colors red and blue makes purple.";

	private String task2Q_encoded = "teach+count+numbers";
	private String task2Q_decoded = "teach count numbers";
	private String task2A1 = "Let's count to ten. One, two, three, four, five, six, seven, eight, nine, ten.";

	private String task3Q_encoded = "sounds+do+animals+make";
	private String task3Q_decoded = "sounds do animals make";
	private String task3A1 = "Let's talk about the sounds that animals make. The cow says Moo. The pig says Oink Oink. The cat says Meow. The dog says Woof Woof. The rooster says cockle doodle doo.";
	private String task3A2 = "Let's talk about the sounds that animals make. The cat says Meow. The dog says Woof Woof. The cow says Moo. The pig says Oink Oink. The rooster says cockle doodle doo.";
	private String task3A3 = "Let's talk about the sounds that animals make. The rooster says cockle doodle doo. The cat says Meow. The dog says Woof Woof. The cow says Moo. The pig says Oink Oink.";

	private String task4Q_encoded = "sing+me+a+song";
	private String task4Q_decoded = "sing me a song";
	private String task4A1 = "Let's sing a song. Twinkle twinkle little star. How I wonder what you are.";
	private String task4A2 = "Let's sing a song. London bridge is falling down, falling down, falling down";
	private String task4A3 = "Let's sing a song. You are my sunshine, my only sunshine. You make me happy when skies are gray.";
	private String task4A4 = "Let's sing a song. Humpty Dumpty sat on a wall, Humpty Dumpty had a great fall. All the king's horses and all the king's men. Couldn't put Humpty together again";

	private String task5Q_encoded = "teach+me+alphabet";
	private String task5Q_decoded = "teach me alphabet";
	private String task5A1 = "Let's learn the alphabet. A for apple, B for ball, C for cat.";
	private String task5A2 = "Let's learn the alphabet. D for dog, E for elephant, F for frog.";
	private String task5A3 = "Let's learn the alphabet. G for goat, H is for happy, I is for iguana.";

	private String task6Q_encoded = "Hi+Talking+Teddy";
	private String task6Q_decoded = "Hi Talking Teddy";
	private String task6A1 = "Hey Kiddoe! Would you like to hear a joke or a song?";
	private String task6A2 = "Hey there! What would you like to learn today?";
	private String task6A3 = "Hello! I can make animal sounds.";

	private String task7Q_encoded = "what+do+you+know";
	private String task7Q_decoded = "what do you know";
	private String task7A = "Teddy knows a little about colors, numbers, animal sounds, the alphabet, funny jokes and songs.";

	private String task8Q_encoded = "tell+me+a+funny+joke";
	private String task8Q_decoded = "tell me a funny joke";
	private String task8A1 = "Why do bees have sticky hair? Because they use honeycombs!";
	private String task8B1 = "Why is six afraid of seven? Because seven eight nine!";
	private String task8C1 = "What do you call a pig that knows karate? Pork chops!";

	private String task9Q_encoded = "what+is+your+name";
	private String task9Q_decoded = "what is your name";
	private String task9A = "My name is Talking Teddy!";

	private String task10Q_encoded = "how+old+are+you";
	private String task10Q_decoded = "how old are you";
	private String task10A = "I'm just a couple years older than you!";

	private String task11Q_encoded = "who+is+your+best+friend";
	private String task11Q_decoded = "who is  your best friend";
	private String task11A = "You are my best friend!";

	private String task12Q_encoded = "where+are+you+from";
	private String task12Q_decoded = "where are you from";
	private String task12A = "I was born in Seattle, Washington.";

	private String task13Q_encoded = "favorite+color";
	private String task13Q_decoded = "favorite color";
	private String task13A = "I like a little bit of everything, so rainbow is my favorite color.";

	private String task14Q_encoded = "can+we+be+friends";
	private String task14Q_decoded = "can we be friends";
	private String task14A = "Of course. I'm already your friend!";

	private String task15Q_encoded = "i+love+you+teddy";
	private String task15Q_decoded = "i love you teddy";
	private String task15A = "I love you too. You are my best friend!";

	private String task16Q_encoded = "pretty+good+awesome";
	private String task16Q_decoded = "pretty good awesome";
	private String task16A1 = "Thanks! I'm working towards that.";
	private String task16B1 = "I certainly hope you think so.";
	private String task16C1 = "You're not so bad yourself.";

	private String task17Q_encoded = "stupid+dumb";
	private String task17Q_decoded = "stupid dumb";
	private String task17A = "Hey, be nice. I've feelings you know!";

	private String fallback1 = "I didn't understand you! Please say that again.";
	private String fallback2 = "Will you please say that again?";
	private String fallback3 = "I didn't understand you! What did you say?";

	public static void GenerateOutput(ResponseDigest respDigest) {
		System.out.println("in generateOutput");
	}

	/**
	 * Called when the activity is first created. This is where we'll hook up
	 * our views in XML layout files to our application.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		/*
		if (BuildConfig.DEBUG) {
			GoogleAnalytics googleAnalytics = GoogleAnalytics
					.getInstance(getApplicationContext());
			googleAnalytics.setAppOptOut(true);
		}*/

		super.onCreate(savedInstanceState);

		setContentView(R.layout.speech);

		tts = new TextToSpeech(this, this);
		context = this;
		taskDiscriminator = new TaskDiscriminator(context);
		
		EasyTracker.getInstance().setContext(context);

		heartSpeakButton = (ImageButton) findViewById(R.id.heartSpeakButton);
		heartSpeakButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
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
}
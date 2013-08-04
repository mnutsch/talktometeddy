package com.talkingteddy;

import java.util.List;
import java.util.Random;

import com.google.analytics.tracking.android.EasyTracker;

import android.content.Context;
import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

public class Helper {
	private static final int TOAST_DURATION = Toast.LENGTH_SHORT;
	
	// strings for Teddy
	private static String greeting1 = "Hey Kiddoe! Press my belly and talk to me.";
	private static String greeting2 = "Hey Buddy! Press my belly and talk to me.";
	private static String greeting3 = "Hello! Press my belly and talk to me.";
	
	private static long longitem = 0; // used by Google Analytics
	
	/**
	 * Speaks the given text. If text is empty, speak "You haven't typed text".
	 * 
	 * @param textToSpeak
	 *            the text to be spoken.
	 */
	public static void startTTS(String textToSpeak,TextToSpeech tts, Context context) {
		if (textToSpeak.length() == 0) {
			tts.speak("Teddy don't have anything to say!",
					TextToSpeech.QUEUE_FLUSH, null);
		} else {
			tts.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null);
			showToast(textToSpeak, context);
		}
	}

	/**
	 * Speaks out a randomized greeting.
	 */
	public static void greet(TextToSpeech tts, Context context) {
		// Make Text to Speech request that will speak out a greeting.
		Random r = new Random();
		int i1 = r.nextInt(4 - 1) + 1;

		String greeting = "";

		if (i1 == 1) {
			greeting = greeting1;
		} else if (i1 == 2) {
			greeting = greeting2;
		} else if (i1 == 3) {
			greeting = greeting3;
		}

		startTTS(greeting, tts, context);
	}
	
	/** Make use of the recognition text in this app. **/

	/**
	 * Stops any Text to Speech in progress.
	 */
	public static void stopTTS(TextToSpeech tts, DownloadAndProcessXML downloadAndProcessXML) {
		tts.stop();
		if (downloadAndProcessXML != null
				&& (downloadAndProcessXML.getStatus() == AsyncTask.Status.PENDING || downloadAndProcessXML
						.getStatus() == AsyncTask.Status.RUNNING)) {
			downloadAndProcessXML.cancel(true);
		}
	}
	
	/**
	 * Shows a toast containing specified text.
	 * 
	 * @param message
	 *            text as toast content
	 */
	public static void showToast(String message, Context context) {
		Toast toast = Toast.makeText(context, message, TOAST_DURATION);
		toast.show();
	}
	
	/**
	 * Report usage to google analytics
	 * 
	 * @param matchedPrompt
	 *            , phrase that was matched.
	 */
	public static void logUserData(String matchedPrompt, ResponseDigest respDigest) {
		 EasyTracker.getTracker().sendEvent("conversation", "user_statement",
				 respDigest.getMatchingPrompt()+"_"+respDigest.getActualPrompt(), longitem); //Google Analytics event
	}
	
	public static String getRecognitionURL(List<Task> tasks, String query){
		String urlFormat = "http://www.sentencerecognition.com/sentencerecognition070313.php?input=%s&key=962b2d2b8e72dc6771bca613d49b46fb%s}";
		
		int index = 1;
		
		String taskStringFormat = "&sentence%s=%s";
		String encodedTasksString = "";
		
		for( Task task : tasks)
		{
			encodedTasksString = encodedTasksString + String.format(taskStringFormat, index++, task.getEncodedQuestion());
		}
		
		return String.format(urlFormat, query, encodedTasksString);
		
	}
	
}

package com.talkingteddy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.google.analytics.tracking.android.EasyTracker;

import android.os.AsyncTask;
import android.util.Log;

/**
 * An asynchronous task that handles requests to the speech-matching API.
 * <p/>
 * Uses AsyncTask to create a task away from the main UI thread. This task takes
 * a URL string and uses it to create an HttpUrlConnection. Once the connection
 * has been established, the AsyncTask downloads the contents of the webpage as
 * an InputStream. Finally, the InputStream is converted into a string, which is
 * displayed in the UI by the AsyncTask's onPostExecute method.
 */
public class DownloadAndProcessXML extends AsyncTask<String, Void, String> {

	// Connection timeouts for HTTP connections
	private static final int CONNECTION_READ_TIMEOUT_MILLIS = 10000;
	private static final int CONNECTION_CONNECT_TIMEOUT_MILLIS = 15000;
	private long longitem = 0; // used by Google Analytics

	// variable specific to sentence recognition API
	private ResponseDigest responseDigest;
	
	@Override
	protected String doInBackground(String... params) {
		this.responseDigest = new ResponseDigest();
		this.responseDigest.setActualPrompt(params[1]);
		// params comes from the execute() call: params[0] is the url.
		try {
			return downloadUrl(params[0]);
		} catch (IOException e) {
			return "Unable to retrieve xml content. URL may be invalid.";
		}
	}

	// onPostExecute displays the results of the AsyncTask.
	@Override
	protected void onPostExecute(String result) {
		try {
			parseXML(result);
			 EasyTracker.getTracker().sendEvent("internal", "general",
			 "sentencerecognition_api_connection_successful", longitem);
			TalkingTeddyActivity.GenerateOutput(this.responseDigest);
		} catch (XmlPullParserException e) {
			 EasyTracker.getTracker().sendEvent("error", "general",
			 "sentencerecognition_api_url_error_XML_Parser_Exception",
			 longitem);
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			 EasyTracker.getTracker().sendEvent("error", "general",
			 "sentencerecognition_api_url_error_IOException", longitem);
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Returns string for web page content located at specified url.
	 * 
	 * @param myurl
	 *            the web page url for which content string is returned
	 * @return string for web page content located at specified url
	 * @throws IOException
	 */
	private String downloadUrl(String myurl) throws IOException {
		InputStream is = null;
		// Only display the first 500 characters of the retrieved
		// web page content.

		try {
			URL url = new URL(myurl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(CONNECTION_READ_TIMEOUT_MILLIS);
			conn.setConnectTimeout(CONNECTION_CONNECT_TIMEOUT_MILLIS);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			// Starts the query
			conn.connect();
			is = conn.getInputStream();

			// Converts the InputStream into a string
			String contentAsString = convertStreamToString(is);
			return contentAsString;

			// Makes sure that the InputStream is closed after the app is
			// finished using it.
		} finally {
			if (is != null) {
				is.close();
			}
		}
	}

	/**
	 * Returns a string converted from a stream.
	 * 
	 * @param is
	 *            the input stream used to convert to a string
	 * @return string converted from the input stream
	 * @throws IOException
	 */
	private String convertStreamToString(InputStream is) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line;

		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}

		is.close();

		return sb.toString();
	}

	// parse XML
	private void parseXML(String xmlInput) throws XmlPullParserException,
			IOException {
		int lastNameWasMatchingPrompt = 0;
		int lastNameWasMatchingPromptScore = 0;

		String name;

		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

		factory.setNamespaceAware(true);
		XmlPullParser xpp = factory.newPullParser();

		xpp.setInput(new StringReader(xmlInput));

		int eventType = xpp.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {

			if (eventType == XmlPullParser.START_DOCUMENT) {
				// System.out.println("Start document");
			} else if (eventType == XmlPullParser.END_DOCUMENT) {
				// System.out.println("End document");
			} else if (eventType == XmlPullParser.START_TAG) {
				// System.out.println("Start tag "+xpp.getName());
				// matchingPrompt = matchingPrompt + xpp.getName() + "***\n";
				name = xpp.getName();
				if (name.equalsIgnoreCase("matching_prompt")) {
					lastNameWasMatchingPrompt = 1;
					// matchingPrompt = matchingPrompt +
					// "Matching Prompt Found!\n";
				}
				if (name.equalsIgnoreCase("matching_prompt_score")) {
					lastNameWasMatchingPromptScore = 1;
					// matchingPrompt = matchingPrompt +
					// "Matching Prompt Score Found!\n";
				}
			} else if (eventType == XmlPullParser.END_TAG) {
				// System.out.println("End tag "+xpp.getName());
			} else if (eventType == XmlPullParser.TEXT) {
				// System.out.println("Text "+xpp.getText());
				// matchingPrompt = matchingPrompt + xpp.getName() + ": " +
				// xpp.getText() + "\n";
				if (lastNameWasMatchingPrompt == 1) {
					this.responseDigest.setMatchingPrompt(xpp.getText());
					lastNameWasMatchingPrompt = 0;
				}
				if (lastNameWasMatchingPromptScore == 1) {
					this.responseDigest.setMatchingPromptScore(Double
							.parseDouble(xpp.getText()));
					lastNameWasMatchingPromptScore = 0;
				}
			}

			try {
				eventType = xpp.next();
			} catch (XmlPullParserException e) {
				Log.e("TTS", "Application Error Occured");
			}
		}
	}
}

/*
Licensed by AT&T under 'Software Development Kit Tools Agreement' 2012.
TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION: http://developer.att.com/sdk_agreement/
Copyright 2012 AT&T Intellectual Property. All rights reserved. 
For more information contact developer.support@att.com http://developer.att.com
*/
package example.simplespeech;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import example.simplespeech.AudioPlayer;
import example.simplespeech.TTSRequest;
import example.simplespeech.TTSClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageButton;

import com.att.android.speech.ATTSpeechActivity;
import com.example.talktometeddy.R;

/**
 * SimpleSpeech is a very basic demonstration of using the ATTSpeechKit 
 * to do voice recognition.  It is designed to introduce a developer to making 
 * a new application that uses the AT&T SpeechKit Android library.  
 * It also documents some of the more basic Android methods for those developers 
 * that are new to Android as well.
 * 
 * As with all apps that use the ATTSpeechActivity, make sure the manifest file 
 * includes a reference to the activity:
 *         <activity android:name="com.att.android.speech.ATTSpeechActivity"
 *          android:theme="@android:style/Theme.Translucent.NoTitleBar" />
**/
public class SimpleSpeechActivityDemo extends Activity {
    private Button speakButton = null;
    private ImageButton heartSpeakButton = null;
    private TextView resultView = null;
    private WebView webView = null;
    private String oauthToken = null;
    private TTSClient ttsClient = null;
    private AudioPlayer audioPlayer = null;
    
    // strings for Teddy
    private String greeting = "Hey Kid dough! Press my heart and talk to me.";
    private String task1Q_encoded = "prime+colors";
    private String task1Q_decoded = "prime colors";
    private String task1A = "The prime colors are red, blue, yellow.";
    
    private String task2Q_encoded = "teach+count+numbers";
    private String task2Q_decoded = "teach count numbers";
    private String task2A = "Let's count to ten. One, two, three, four, five, six, seven, eight, nine, ten.";
   
    private String task3Q_encoded = "sounds+do+animals+make";
    private String task3Q_decoded = "sounds do animals make";
    private String task3A = "Let's talk about the sounds that animals make. The cow says Moo. The cat says Meow. The dog says Woof Woof. The pig says Oink Oink.";
    
    private String task4Q_encoded = "sing+me+a+song";
    private String task4Q_decoded = "sing me a song";
    private String task4A = "Let's sing a song. Twinkle twinkle little star. How I wonder what you are.";
    
    private String task5Q_encoded = "teach+me+alphabet";
    private String task5Q_decoded = "teach me alphabet";
    private String task5A = "Let's learn the alphabet. Ei for apple, B for ball, C for cat, D for dog, E for elephant, F for frog, G for goat. Your turn, I'm tired now!";
    private String tiredstr = "I'm tired now!";
    
    private String task6Q_encoded = "hi+teddy";
    private String task6Q_decoded = "hi teddy";
    private String task6A = "Hello kid!";
    
    private String task7Q_encoded = "what+things+do+you+know";
    private String task7Q_decoded = "what things do you know";
    private String task7A = "I know colors, numbers, animal sounds, the alphabet and a song.";
    
    private String fallback = "I didn't understand you! Please say again.";
    
    //global variables specific to sentence recognition API
  	static String matchingprompt;
  	static String matchingpromptscore;
    
    /** 
     * Called when the activity is first created.  This is where we'll hook up 
     * our views in XML layout files to our application.
    **/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // First, we specify which layout resource we'll be using.
        setContentView(R.layout.speech);
        
     // A simple UI-less player for the TTS audio.
        audioPlayer = new AudioPlayer(this);
        
        // This is the Speak button that the user presses to start a speech
        // interaction.
        speakButton = (Button)findViewById(R.id.speak_button);
        speakButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startSpeechActivity();
            }
        });
        
        heartSpeakButton = (ImageButton)findViewById(R.id.heartSpeakButton);
        heartSpeakButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	stopTTS();
                startSpeechActivity();
            }
        });
        
        // This will show the recognized text.
        resultView = (TextView)findViewById(R.id.result);
        
        // This will show a website receiving the recognized text.
        webView = (WebView)findViewById(R.id.webview);
        configureWebView();
    }
    
    /** 
     * Called when the activity is coming to the foreground.
     * This is where we will fetch a fresh OAuth token.
    **/
    @Override
    protected void onStart() {
        super.onStart();
        
        // Fetch the OAuth credentials.  
        validateOAuth();
    }
    
    
    /**
     * Stops any Text to Speech in progress.
    **/
    private void
    stopTTS()
    {
        if (ttsClient != null)
            ttsClient.cancel = true;
        audioPlayer.stop();
    }

    /**
     * The request code is required to use the startActivityForResult method. 
     * It allows you to identify multiple requests from activities when they 
     * finish their work.  In this example, we only ever have a single child 
     * activity active, which we identify by an arbitrary constant.
    **/
    private static final int SPEECH_REQUEST_CODE = 42;
    
    /** 
     * Called by the Speak button in the sample activity.
     * Starts the SpeechKit activity that listens to the microphone and returns
     * the recognized text.
     */
    private void startSpeechActivity() {
        // The ATTSpeechKit uses its own activity to do speech recognition.  
        // We're going to call that by creating an Intent.
        // The intent takes two parameters -- the activity we're calling FROM 
        // and the class of the activity we want to call TO.
        Intent recognizerIntent = new Intent(this, ATTSpeechActivity.class);
        
        // Next, we'll put in some basic parameters.
        // First is the Request URL.  This is the URL of the speech recognition 
        // service that you were given during onboarding.
        recognizerIntent.putExtra(ATTSpeechActivity.EXTRA_RECOGNITION_URL, 
                SpeechConfig.serviceUrl());
        
        // Specify the speech context for this app.
        recognizerIntent.putExtra(ATTSpeechActivity.EXTRA_SPEECH_CONTEXT, 
                "WebSearch");
        
        // Set the OAuth token that was fetched in the background.
        recognizerIntent.putExtra(ATTSpeechActivity.EXTRA_BEARER_AUTH_TOKEN, 
                oauthToken);

        // Add extra arguments for speech recognition.
        // The parameter is the name of the current screen within this app.
        recognizerIntent.putExtra(ATTSpeechActivity.EXTRA_XARGS, 
                "ClientScreen=main");

        // Finally we have all the information needed to start the speech activity.  
        startActivityForResult(recognizerIntent, SPEECH_REQUEST_CODE);
        Log.v("SimpleSpeech", "Starting speech interaction");
    }
    
    /**
     * Since we use another activity to do the speech recognition, the results 
     * are sent back to our project as an "Activity Result". 
     * This method collects those results and do further processing.
    **/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == SPEECH_REQUEST_CODE) {
            // The result code indicates whether or not the child activity was 
            // successful performing its task. 
            // In SpeechKit, if the speech processing occurred without errors, 
            // it returns the android.app.Activity.RESULT_OK result.
            if (resultCode == RESULT_OK) {
                // The results are sent back as intent "extras" and can be 
                // retrieved in a number of ways.  The simplest is to get a list 
                // of strings representing different possible results.
                ArrayList<String> textList = 
                        resultData.getStringArrayListExtra(ATTSpeechActivity.EXTRA_RESULT_TEXT_STRINGS);
                String resultText = null;
                if (textList != null && textList.size() > 0) {
                    // There may be multiple results, but this example will only use
                    // the first one, which is the most likely.
                    resultText = textList.get(0);
                }
                if (resultText != null && resultText.length() > 0) {
                    // This is where your app will process the recognized text.
                    Log.v("SimpleSpeech", "Recognized "+textList.size()+" hypotheses.");
                    handleRecognition(resultText);
                }
                else {
                    // The speech service did not recognize what was spoken.
                    Log.v("SimpleSpeech", "Recognized no hypotheses.");
                    alert("Didn't recognize speech", "Please try again.");
                }
            }
            else if (resultCode == RESULT_CANCELED) {
                // The user canceled the speech interaction.
                // This can happen through several mechanisms:
                // pressing a cancel button in the speech UI;
                // pressing the back button; starting another activity;
                // or locking the screen.
                
                // In all these situations, the user was instrumental
                // in canceling, so there is no need to put up a UI alerting 
                // the user to the fact.
                Log.v("SimpleSpeech", "User canceled.");
            }
            else {
                // Any other value for the result code means an error has occurred.
                // A message to help the programmer diagnose the issue is  
                // returned as a string extra under the ERROR_MESSAGE key.
                String errorMessage = (resultData != null)
                        ? resultData.getStringExtra(ATTSpeechActivity.EXTRA_RESULT_ERROR_MESSAGE)
                            : "(no explanation)";
                Log.v("SimpleSpeech", "Recognition error #"+resultCode+": "+errorMessage);
                alert("Speech Error", "Please try again later.");
            }
        }
    }

    /** Make use of the recognition text in this app. **/
    private void handleRecognition(String resultText) {
        // In this example, we set display the text in the result view
    	
    	String mystring = "";
		String displaystring;
    	
        resultView.setText("Teddy heard \""+resultText+"\"");
        // And then perform a search on a website using the text.
        String query = URLEncoder.encode(resultText);
        String myurl = "http://www.sentencerecognition.com/sentencerecognition.php?input="+query+"" +
        		"&sentence1="+this.task1Q_encoded+"" +
        		"&sentence2="+this.task2Q_encoded+
        		"&sentence3="+this.task3Q_encoded+
        		"&sentence4="+this.task4Q_encoded+
        		"&sentence5="+this.task5Q_encoded+
        		"&sentence6="+this.task6Q_encoded+
        		"&sentence7="+this.task7Q_encoded;
        
        //getting HTTP
		
        URL url = null;
		try {
			url = new URL(myurl);
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	
        try {
			mystring = convertStreamToString(url.openStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		  //parsing XML
		  try {
			parseXML(mystring);
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  displaystring = "Matching Prompt: " + matchingprompt + "\n" + "Score: " + matchingpromptscore;
        
		double promptScore = Double.parseDouble(matchingpromptscore);
		
		if(promptScore < 35)
		{
			//webView.loadData("I didn't understand you!", "text/html", "UTF-8");
			this.startTTS(this.fallback);
		}
		else if(matchingprompt.compareTo(this.task1Q_decoded) == 0)
		{
			//webView.loadData(this.task1Q_decoded, "text/html", "UTF-8");
			this.startTTS(this.task1A);
		}
		else if(matchingprompt.compareTo(this.task2Q_decoded) == 0)
		{
			//webView.loadData(this.task2Q_decoded, "text/html", "UTF-8");
			this.startTTS(this.task2A);
		}
		else if(matchingprompt.compareTo(this.task3Q_decoded) == 0)
		{
			//webView.loadData(this.task3Q_decoded, "text/html", "UTF-8");
			this.startTTS(this.task3A);
		}
		else if(matchingprompt.compareTo(this.task4Q_decoded) == 0)
		{
			//webView.loadData(this.task4Q_decoded, "text/html", "UTF-8");
			this.startTTS(this.task4A);
		}
		else if(matchingprompt.compareTo(this.task5Q_decoded) == 0)
		{
			//webView.loadData(this.task5Q_decoded, "text/html", "UTF-8");
			this.startTTS(this.task5A);
		}
		else if(matchingprompt.compareTo(this.task6Q_decoded) == 0)
		{
			//webView.loadData(this.task6Q_decoded, "text/html", "UTF-8");
			this.startTTS(this.task6A);
		}
		else if(matchingprompt.compareTo(this.task7Q_decoded) == 0)
		{
			//webView.loadData(this.task7Q_decoded, "text/html", "UTF-8");
			this.startTTS(this.task7A);
		}
	        
    }
    

    
    /**
     * Start a TTS request to speak the argument.
    **/
    private void startTTS(String textToSpeak)
    {
        TTSRequest tts = TTSRequest.forService(SpeechConfig.ttsUrl(), oauthToken);
        ttsClient = new TTSClient(this.audioPlayer);
        tts.postTextWithVoice(textToSpeak, "mike", ttsClient);
    }
    
    /** Configure the webview that displays websites with the recognition text. **/
    private void configureWebView() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false; // Let the webview display the URL
            }
        });
    }
    
    /**
     * Start an asynchronous OAuth credential check. 
     * Disables the Speak button until the check is complete.
    **/
    private void validateOAuth() {
        SpeechAuth auth = 
            SpeechAuth.forService(SpeechConfig.oauthUrl(), SpeechConfig.oauthScope(), 
                SpeechConfig.oauthKey(), SpeechConfig.oauthSecret());
        auth.fetchTo(new OAuthResponseListener());
        speakButton.setText(R.string.speak_wait);
        speakButton.setEnabled(false);
    }
    
    /**
     * Handle the result of an asynchronous OAuth check.
    **/
    private class OAuthResponseListener implements SpeechAuth.Client {
        public void 
        handleResponse(String token, Exception error)
        {
            if (token != null) {
                oauthToken = token;
                readyForSpeech();
                speakButton.setText(R.string.speak);
                speakButton.setEnabled(true);
            }
            else {
                Log.v("SimpleSpeech", "OAuth error: "+error);
                // There was either a network error or authentication error.
                // Show alert for the latter.
                alert("Speech Unavailable", 
                    "This app was rejected by the speech service.  Contact the developer for an update.");
            }
        }
    }
    
    /**
     * When the app is authenticated with the Speech API, 
     * enable the interface and speak out a greeting.
    **/
    private void 
    readyForSpeech() 
    {
        // Make Text to Speech request that will speak out a greeting.
        startTTS(this.greeting);
    }
    
    private void alert(String header, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
            .setTitle(header)
            .setCancelable(true)
            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        AlertDialog alert = builder.create();
        alert.show();
    }
    
  //get HTTP
  	public static String convertStreamToString(InputStream is) throws Exception {
  	    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
  	    StringBuilder sb = new StringBuilder();
  	    String line = null;

  	    while ((line = reader.readLine()) != null) {
  	        sb.append(line);
  	    }

  	    is.close();

  	    return sb.toString();
  	}
    
  //parse XML
  	public static void parseXML (String xmlinput)
  	         throws XmlPullParserException, IOException
  	     {
  			 int lastnamewasmatchingprompt;
  			 int lastnamewasmatchingpromptscore;
  			 lastnamewasmatchingprompt = 0;
  			 lastnamewasmatchingpromptscore = 0;
  			 String comparisontext;
  			 String name;
  			 
  	         XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
  	         factory.setNamespaceAware(true);
  	         XmlPullParser xpp = factory.newPullParser();

  	         xpp.setInput(new StringReader (xmlinput));
  	                  
  	        
  	         int eventType = xpp.getEventType();
  	         while (eventType != XmlPullParser.END_DOCUMENT) {
  	          
  	        	
  	          
  	        	if(eventType == XmlPullParser.START_DOCUMENT) {
  	              //System.out.println("Start document");
  	          } else if(eventType == XmlPullParser.END_DOCUMENT) {
  	              //System.out.println("End document");
  	          } else if(eventType == XmlPullParser.START_TAG) {
  	              //System.out.println("Start tag "+xpp.getName());
  	        	  //matchingprompt = matchingprompt + xpp.getName() + "***\n";
  	        	  name = xpp.getName();
  	        	  if (name.equalsIgnoreCase("matching_prompt"))
  	        	  {
  	        		  lastnamewasmatchingprompt = 1;
  	        		  //matchingprompt = matchingprompt + "Matching Prompt Found!\n";
  	        	  }
  	        	  if (name.equalsIgnoreCase("matching_prompt_score"))
  	        	  {
  	        		  lastnamewasmatchingpromptscore = 1;
  	        		  //matchingprompt = matchingprompt + "Matching Prompt Score Found!\n";
  	        	  }
  	          } else if(eventType == XmlPullParser.END_TAG) {
  	              //System.out.println("End tag "+xpp.getName());
  	          } else if(eventType == XmlPullParser.TEXT) {
  	              //System.out.println("Text "+xpp.getText());
  	        	  //matchingprompt = matchingprompt + xpp.getName() + ": " + xpp.getText() + "\n";
  	        	  if(lastnamewasmatchingprompt == 1)
  	        	  {
  	        		  matchingprompt = xpp.getText();
  	        		  lastnamewasmatchingprompt = 0;
  	        	  }
  	        	  if(lastnamewasmatchingpromptscore == 1)
  	        	  {
  	        		  matchingpromptscore = xpp.getText();
  	        		  lastnamewasmatchingpromptscore = 0;
  	        	  }
  	        	  
  	          }
  	          eventType = xpp.next();
  	         }
  	     
  	         
  	     }
}

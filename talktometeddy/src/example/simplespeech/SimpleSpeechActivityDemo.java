package example.simplespeech;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.talktometeddy.R;
import com.google.analytics.tracking.android.EasyTracker;


public class SimpleSpeechActivityDemo extends Activity implements OnInitListener {
	
    protected static final int RESULT_SPEECH = 1;
    private ImageButton heartSpeakButton = null;
    
    private TextToSpeech tts;
    private String apikey = "962b2d2b8e72dc6771bca613d49b46fb";
    
    // strings for Teddy
    private String greeting1 = "Hey Kido! Press my belly and talk to me.";
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
    
    private String task5Q_encoded = "teach+me+alphabet";
    private String task5Q_decoded = "teach me alphabet";
    private String task5A1 = "Let's learn the alphabet. A for apple, B for ball, C for cat.";
    private String task5A2 = "Let's learn the alphabet. D for dog, E for elephant, F for frog.";
    private String task5A3 = "Let's learn the alphabet. G for goat, H is for happy, I is for iguana.";
    
    private String task6Q_encoded = "Hi+Talking+Teddy";
    private String task6Q_decoded = "Hi Talking Teddy";
    private String task6A1 = "Hey Kido! Press my belly and talk to me.";
    private String task6A2 = "Hey there! Press my belly and talk to me.";
    private String task6A3 = "Hello! Press my belly and talk to me.";
    
    private String task7Q_encoded = "what+do+you+know";
    private String task7Q_decoded = "what do you know";
    private String task7A = "I know a about colors, numbers, animal sounds, the alphabet, funny jokes and songs.";
    
    private String task8Q_encoded = "tell+me+a+funny+joke";
    private String task8Q_decoded = "tell me a funny joke";
    private String task8A1 = "Why do bees have sticky hair? Because they use honeycombs!";
    private String task8B1 = "Why is six afraid of seven? Because seven eight nine!";
    private String task8C1 = "What do call pig that knows karate? Pork chops!";
    
    private String task9Q_encoded = "what+is+your+name";
    private String task9Q_decoded = "what is your name";
    private String task9A = "My name is Talking Teddy!";
    
    private String task10Q_encoded = "how+old+are+you";
    private String task10Q_decoded = "how old are you";
    private String task10A = "I'm just couple years older than you!";

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
    
    private String fallback1 = "I didn't understand you! Please say that again.";
    private String fallback2 = "Will you please say that again?";
    private String fallback3 = "I didn't understand you! What did you say?";
    
    //global variables specific to sentence recognition API
  	public static String matchingPrompt;
  	public static String matchingPromptScore;
  	
  	
  	private static final int TOAST_DURATION = Toast.LENGTH_SHORT;

  	
    
    /** 
     * Called when the activity is first created.  This is where we'll hook up 
     * our views in XML layout files to our application.
    **/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.speech);
        

        tts = new TextToSpeech(this, this);        
        heartSpeakButton = (ImageButton)findViewById(R.id.heartSpeakButton);
        heartSpeakButton.setOnClickListener(new View.OnClickListener() {
            
            @Override
			public void onClick(View v) {
            	stopTTS();
				Intent intent = new Intent(
						RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

				intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");

				try {
					startActivityForResult(intent, RESULT_SPEECH);
				} catch (ActivityNotFoundException a) {
				    showToast("Ops! Your device doesn't support Speech to Text");
				}
			}
        });

    }
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case RESULT_SPEECH: {
			if (resultCode == RESULT_OK && null != data) {

				ArrayList<String> text = data
						.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				
				handleRecognition(text.get(0).toString());
			}
			break;
		}

		}
	}
    
    /** 
     * Called when the activity is coming to the foreground.
     * This is where we will fetch a fresh OAuth token.
    **/
    @Override
    protected void onStart() {
        super.onStart();
        
        EasyTracker.getInstance().activityStart(this);//start Google Analytics API
        
        readyForSpeech();
    }
    
    
    /**
     * Stops any Text to Speech in progress.
    **/
    private void stopTTS()
    {
    	tts.stop();
    }

    

    /** Make use of the recognition text in this app. **/
    private void handleRecognition(String speechText) {
        // In this example, we set display the text in the result view
    	
		showToast(speechText);
        // And then perform a search on a website using the text.
        String query = URLEncoder.encode(speechText);
        String recognitionURL = "http://www.sentencerecognition.com/sentencerecognition070313.php?input="+query+"&key="+apikey+
        		"&sentence1="+this.task1Q_encoded+"" +
        		"&sentence2="+this.task2Q_encoded+
        		"&sentence3="+this.task3Q_encoded+
        		"&sentence4="+this.task4Q_encoded+
        		"&sentence5="+this.task5Q_encoded+
        		"&sentence6="+this.task6Q_encoded+
        		"&sentence7="+this.task7Q_encoded+
        		"&sentence8="+this.task8Q_encoded+
        		"&sentence9="+this.task9Q_encoded+
        		"&sentence10="+this.task10Q_encoded+
        		"&sentence11="+this.task11Q_encoded+
        		"&sentence12="+this.task12Q_encoded+
        		"&sentence13="+this.task13Q_encoded+
        		"&sentence14="+this.task14Q_encoded+
        		"&sentence15="+this.task15Q_encoded;
        
        //getting HTTP
		
		// Gets the URL from the UI's text field.
        String stringUrl = recognitionURL;
        ConnectivityManager connMgr = (ConnectivityManager) 
            getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadWebpageTask().execute(stringUrl);
        } else {
        	showToast("No network connection available.");
        }
		
    }
    
	 // Reads an InputStream and converts it to a String.
	    public String readIt(InputStream is) throws IOException, UnsupportedEncodingException {
	    	BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	  	    StringBuilder sb = new StringBuilder();
	  	    String line = null;

	  	    while ((line = reader.readLine()) != null) {
	  	        sb.append(line);
	  	    }

	  	    is.close();

	  	    return sb.toString();
	    }
    
	 // Given a URL, establishes an HttpUrlConnection and retrieves
	 // the web page content as a InputStream, which it returns as
	 // a string.
	 private String downloadUrl(String myurl) throws IOException {
	     InputStream is = null;
	     // Only display the first 500 characters of the retrieved
	     // web page content.

	     try {
	         URL url = new URL(myurl);
	         HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	         conn.setReadTimeout(10000 /* milliseconds */);
	         conn.setConnectTimeout(15000 /* milliseconds */);
	         conn.setRequestMethod("GET");
	         conn.setDoInput(true);
	         // Starts the query
	         conn.connect();
	         is = conn.getInputStream();
	
	         // Convert the InputStream into a string
	         String contentAsString = readIt(is);
	         return contentAsString;
	         
	     // Makes sure that the InputStream is closed after the app is
	     // finished using it.
	     } finally {
	         if (is != null) {
	             is.close();
	         } 
	     }
	 }
    
    // Uses AsyncTask to create a task away from the main UI thread. This task takes a 
    // URL string and uses it to create an HttpUrlConnection. Once the connection
    // has been established, the AsyncTask downloads the contents of the webpage as
    // an InputStream. Finally, the InputStream is converted into a string, which is
    // displayed in the UI by the AsyncTask's onPostExecute method.
    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
       @Override
       protected String doInBackground(String... urls) {
             
           // params comes from the execute() call: params[0] is the url.
           try {
               return downloadUrl(urls[0]);
           } catch (IOException e) {
               return "Unable to retrieve web page. URL may be invalid.";
           }
       }
       // onPostExecute displays the results of the AsyncTask.
       @Override
       protected void onPostExecute(String result) {
    	   try {
			parseXML(result);
			generateOutput();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      }
   }
    
    private void generateOutput()
    {
    	double promptScore = Double.parseDouble(matchingPromptScore);
		if(promptScore < 35)
		{
			Random r = new Random();
	    	int i1=r.nextInt(4-1) + 1;
	    	
	    	if(i1 == 1)
	    	{
	    		this.startTTS(this.fallback1);
	    	}
	    	else if(i1 == 2)
	    	{
	    		this.startTTS(this.fallback2);
	    	}
	    	if(i1 == 3)
	    	{
	    		this.startTTS(this.fallback3);
	    	}
	    					
		}
		else if(matchingPrompt.compareTo(this.task1Q_decoded) == 0)
		{
			Random r = new Random();
	    	int i1=r.nextInt(4-1) + 1;
	    	
	    	if(i1 == 1)
	    	{
	    		this.startTTS(this.task1A1);
	    	}
	    	else if(i1 == 2)
	    	{
	    		this.startTTS(this.task1A2);
	    	}
	    	if(i1 == 3)
	    	{
	    		this.startTTS(this.task1A3);
	    	}
			
		}
		else if(matchingPrompt.compareTo(this.task2Q_decoded) == 0)
		{
			this.startTTS(this.task2A1);
	    	
		}
		else if(matchingPrompt.compareTo(this.task3Q_decoded) == 0)
		{
			Random r = new Random();
	    	int i1=r.nextInt(4-1) + 1;
	    	
	    	if(i1 == 1)
	    	{
	    		this.startTTS(this.task3A1);
	    	}
	    	else if(i1 == 2)
	    	{
	    		this.startTTS(this.task3A2);
	    	}
	    	if(i1 == 3)
	    	{
	    		this.startTTS(this.task3A3);
	    	}
			
		}
		else if(matchingPrompt.compareTo(this.task4Q_decoded) == 0)
		{
			Random r = new Random();
	    	int i1=r.nextInt(4-1) + 1;
	    	
	    	if(i1 == 1)
	    	{
	    		this.startTTS(this.task4A1);
	    	}
	    	else if(i1 == 2)
	    	{
	    		this.startTTS(this.task4A2);
	    	}
	    	if(i1 == 3)
	    	{
	    		this.startTTS(this.task4A3);
	    	}
			
		}
		else if(matchingPrompt.compareTo(this.task5Q_decoded) == 0)
		{
			Random r = new Random();
	    	int i1=r.nextInt(4-1) + 1;
	    	
	    	if(i1 == 1)
	    	{
	    		this.startTTS(this.task5A1);
	    	}
	    	else if(i1 == 2)
	    	{
	    		this.startTTS(this.task5A2);
	    	}
	    	if(i1 == 3)
	    	{
	    		this.startTTS(this.task5A3);
	    	}
			
		}
		else if(matchingPrompt.compareTo(this.task6Q_decoded) == 0)
		{
			//webView.loadData(this.task6Q_decoded, "text/html", "UTF-8");
			Random r = new Random();
	    	int i1=r.nextInt(4-1) + 1;
	    	
	    	if(i1 == 1)
	    	{
	    		this.startTTS(this.task6A1);
	    	}
	    	else if(i1 == 2)
	    	{
	    		this.startTTS(this.task6A2);
	    	}
	    	if(i1 == 3)
	    	{
	    		this.startTTS(this.task6A3);
	    	}
			
		}
		else if(matchingPrompt.compareTo(this.task7Q_decoded) == 0)
		{
			this.startTTS(this.task7A);
		}
		else if(matchingPrompt.compareTo(this.task8Q_decoded) == 0)
		{
			Random r = new Random();
	    	int i1=r.nextInt(4-1) + 1;
	    	
	    	if(i1 == 1)
	    	{
	    		this.startTTS(this.task8A1);
	    	}
	    	else if(i1 == 2)
	    	{
	    		this.startTTS(this.task8B1);
	    	}
	    	if(i1 == 3)
	    	{
	    		this.startTTS(this.task8C1);
	    	}
			
		}
		else if(matchingPrompt.compareTo(this.task9Q_decoded) == 0)
		{
			this.startTTS(this.task9A);
	    	
		}
		else if(matchingPrompt.compareTo(this.task10Q_decoded) == 0)
		{
			this.startTTS(this.task10A);
	    	
		}
		else if(matchingPrompt.compareTo(this.task11Q_decoded) == 0)
		{
			this.startTTS(this.task11A);
	    	
		}
		else if(matchingPrompt.compareTo(this.task12Q_decoded) == 0)
		{
			this.startTTS(this.task12A);
	    	
		}
		else if(matchingPrompt.compareTo(this.task13Q_decoded) == 0)
		{
			this.startTTS(this.task13A);
	    	
		}
		else if(matchingPrompt.compareTo(this.task14Q_decoded) == 0)
		{
			this.startTTS(this.task14A);
	    	
		}
		else if(matchingPrompt.compareTo(this.task15Q_decoded) == 0)
		{
			this.startTTS(this.task15A);
	    	
		}
		else
		{
			this.startTTS(this.task7A);
		}
	}
    
    /**
     * Start a TTS request to speak the argument.
    **/
    private void startTTS(String textToSpeak)
    {   
		if (textToSpeak.length() == 0) {
			tts.speak("You haven't typed text", TextToSpeech.QUEUE_FLUSH, null);
		} else {
			tts.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null);
			showToast(textToSpeak);
		}
    }

    /**
     * When the app is authenticated with the Speech API, 
     * enable the interface and speak out a greeting.
    **/
    private void readyForSpeech()
    {
        // Make Text to Speech request that will speak out a greeting.
    	Random r = new Random();
    	int i1=r.nextInt(4-1) + 1;

        String greeting = "";
    	
    	if(i1 == 1)
    	{
    		greeting = this.greeting1;
    	}
    	else if(i1 == 2)
    	{
    		greeting = this.greeting2;
    	}
    	if(i1 == 3)
    	{
    		greeting = this.greeting3;
    	}

        
        startTTS(greeting);
        showToast(greeting);
    }
  	
  //parse XML
  	public static void parseXML (String xmlinput)
  	         throws XmlPullParserException, IOException
  	     {
  			 int lastnamewasmatchingprompt;
  			 int lastnamewasmatchingpromptscore;
  			 lastnamewasmatchingprompt = 0;
  			 lastnamewasmatchingpromptscore = 0;
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
  	        	  //matchingPrompt = matchingPrompt + xpp.getName() + "***\n";
  	        	  name = xpp.getName();
  	        	  if (name.equalsIgnoreCase("matching_prompt"))
  	        	  {
  	        		  lastnamewasmatchingprompt = 1;
  	        		  //matchingPrompt = matchingPrompt + "Matching Prompt Found!\n";
  	        	  }
  	        	  if (name.equalsIgnoreCase("matching_prompt_score"))
  	        	  {
  	        		  lastnamewasmatchingpromptscore = 1;
  	        		  //matchingPrompt = matchingPrompt + "Matching Prompt Score Found!\n";
  	        	  }
  	          } else if(eventType == XmlPullParser.END_TAG) {
  	              //System.out.println("End tag "+xpp.getName());
  	          } else if(eventType == XmlPullParser.TEXT) {
  	              //System.out.println("Text "+xpp.getText());
  	        	  //matchingPrompt = matchingPrompt + xpp.getName() + ": " + xpp.getText() + "\n";
  	        	  if(lastnamewasmatchingprompt == 1)
  	        	  {
  	        		  matchingPrompt = xpp.getText();
  	        		  lastnamewasmatchingprompt = 0;
  	        	  }
  	        	  if(lastnamewasmatchingpromptscore == 1)
  	        	  {
  	        		  matchingPromptScore = xpp.getText();
  	        		  lastnamewasmatchingpromptscore = 0;
  	        	  }
  	        	  
  	          }
  	          eventType = xpp.next();
  	         }
  	         
  	     }

	@Override
	public void onInit(int status) {
		// TODO Auto-generated method stub
		
		if (status == TextToSpeech.SUCCESS) {

			int result = tts.setLanguage(Locale.US);
			
			if (result == TextToSpeech.LANG_MISSING_DATA
					|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
				Toast.makeText(this, "Language not supported", Toast.LENGTH_LONG).show();
				Log.e("TTS", "Language is not supported");
			}
	        readyForSpeech();

		} else {
			Log.e("TTS", "Initilization Failed");
		}
		
	}
	
	@Override
	  public void onStop() {
	    super.onStop();
	    EasyTracker.getInstance().activityStop(this); // Add this method.
	}
	

    private void showToast(String message) {
        Toast toast = Toast.makeText(this, message, TOAST_DURATION);
        toast.show();
    }


}

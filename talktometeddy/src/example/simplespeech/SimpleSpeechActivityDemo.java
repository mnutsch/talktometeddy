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
import java.util.Locale;
import java.util.Random;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.talktometeddy.R;


public class SimpleSpeechActivityDemo extends Activity implements OnInitListener {
	
    protected static final int RESULT_SPEECH = 1;
    private ImageButton heartSpeakButton = null;
    private TextView resultView = null;
    private WebView webView = null;
    
    private TextToSpeech tts;
    
    private String apikey = "962b2d2b8e72dc6771bca613d49b46fb";
    
    // strings for Teddy
    private String greeting1 = "Hey Kid dough! Press my heart and talk to me.";
    private String greeting2 = "Hey there! Press my heart and talk to me.";
    private String greeting3 = "Hello! Press my heart and talk to me.";
    private String task1Q_encoded = "prime+colors";
    private String task1Q_decoded = "prime colors";
    private String task1A1 = "The prime colors are red, blue and yellow.";
    private String task1A2 = "Combining the colors yellow and blue makes green.";
    private String task1A3 = "Mixing the colors red and blue makes purple.";
    
    private String task2Q_encoded = "teach+count+numbers";
    private String task2Q_decoded = "teach count numbers";
    private String task2A1 = "Let's count to ten. One, two, three, four, five, six, seven, eight, nine, ten.";
    private String task2A2 = "Let's count by twos. Two, four, six, eight, ten.";
    private String task2A3 = "Let's count by prime numbers. One, two, three, five, seven, eleven, thirteen, seventeen, nineteen, twenty three.";
    
    private String task3Q_encoded = "sounds+do+animals+make";
    private String task3Q_decoded = "sounds do animals make";
    private String task3A1 = "Let's talk about the sounds that animals make. The cow says Moo. The pig says Oink Oink.";
    private String task3A2 = "Let's talk about the sounds that animals make. The cat says Meow. The dog says Woof Woof.";
    private String task3A3 = "Let's talk about the sounds that animals make. The rooster says cockle doodle doo.";
    
    private String task4Q_encoded = "sing+me+a+song";
    private String task4Q_decoded = "sing me a song";
    private String task4A1 = "Let's sing a song. Twinkle twinkle little star. How I wonder what you are.";
    private String task4A2 = "Let's sing a song. London bridge is falling down, falling down, falling down";
    private String task4A3 = "Let's sing a song. You are my sunshine, my only sunshine. You make me happy when skies are gray.";
    
    private String task5Q_encoded = "teach+me+alphabet";
    private String task5Q_decoded = "teach me alphabet";
    private String task5A1 = "Let's learn the alphabet. Ei for apple, B for ball, C for cat.";
    private String task5A2 = "Let's learn the alphabet. D for dog, E for elephant, F for frog.";
    private String task5A3 = "Let's learn the alphabet. G for goat, H is for happy, eye is for iguana.";
    
    private String task6Q_encoded = "how+are+you";
    private String task6Q_decoded = "how are you";
    private String task6A1 = "I'm great, thanks for asking!";
    private String task6A2 = "I'm happy! Today is a fun day!";
    private String task6A3 = "Yawn... I'm sleepy. Let's take a nap.";
    
    private String task7Q_encoded = "what+things+do+you+know";
    private String task7Q_decoded = "what things do you know";
    private String task7A = "I know colors, numbers, animal sounds, the alphabet and a song.";
    
    private String fallback1 = "I didn't understand you! Please say that again.";
    private String fallback2 = "Will you please say that again?";
    private String fallback3 = "I didn't understand you! What did you say?";
    
    //global variables specific to sentence recognition API
  	static String matchingprompt;
  	static String matchingpromptscore;
  	
  	
  	int duration = Toast.LENGTH_SHORT;

  	
    
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
					Toast t = Toast.makeText(getApplicationContext(),
							"Ops! Your device doesn't support Speech to Text",
							Toast.LENGTH_SHORT);
					t.show();
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
        readyForSpeech();

    }
    
    
    /**
     * Stops any Text to Speech in progress.
    **/
    private void
    stopTTS()
    {
    	tts.stop();
    }

    

    /** Make use of the recognition text in this app. **/
    private void handleRecognition(String resultText) {
        // In this example, we set display the text in the result view
    	
    	String mystring = "";
		String displaystring;
		Toast toast = Toast.makeText(this, resultText, duration);
	  	toast.show();
        // And then perform a search on a website using the text.
        String query = URLEncoder.encode(resultText);
        String myurl = "http://www.sentencerecognition.com/sentencerecognition070313.php?input="+query+"&key="+apikey+
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
        
		try {
			double promptScore = Double.parseDouble(matchingpromptscore);
			if(promptScore < 35)
			{
				//webView.loadData("I didn't understand you!", "text/html", "UTF-8");
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
			else if(matchingprompt.compareTo(this.task1Q_decoded) == 0)
			{
				//webView.loadData(this.task1Q_decoded, "text/html", "UTF-8");
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
			else if(matchingprompt.compareTo(this.task2Q_decoded) == 0)
			{
				//webView.loadData(this.task2Q_decoded, "text/html", "UTF-8");
				Random r = new Random();
		    	int i1=r.nextInt(4-1) + 1;
		    	
		    	if(i1 == 1)
		    	{
		    		this.startTTS(this.task2A1);
		    	}
		    	else if(i1 == 2)
		    	{
		    		this.startTTS(this.task2A2);
		    	}
		    	if(i1 == 3)
		    	{
		    		this.startTTS(this.task2A3);
		    	}
				
			}
			else if(matchingprompt.compareTo(this.task3Q_decoded) == 0)
			{
				//webView.loadData(this.task3Q_decoded, "text/html", "UTF-8");
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
			else if(matchingprompt.compareTo(this.task4Q_decoded) == 0)
			{
				//webView.loadData(this.task4Q_decoded, "text/html", "UTF-8");
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
			else if(matchingprompt.compareTo(this.task5Q_decoded) == 0)
			{
				//webView.loadData(this.task5Q_decoded, "text/html", "UTF-8");
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
			else if(matchingprompt.compareTo(this.task6Q_decoded) == 0)
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
			else if(matchingprompt.compareTo(this.task7Q_decoded) == 0)
			{
				//webView.loadData(this.task7Q_decoded, "text/html", "UTF-8");
				this.startTTS(this.task7A);
			}
		}
		catch (Exception e){
			Log.v("SimpleSpeech", "Matching Prompt Score in Exception Handler = ["+ matchingpromptscore + "]");
			this.startTTS(this.fallback1);
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
		}
    }
    
//    /** Configure the webview that displays websites with the recognition text. **/
//    private void configureWebView() {
//        webView.getSettings().setJavaScriptEnabled(true);
//        webView.setWebViewClient(new WebViewClient() {
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                return false; // Let the webview display the URL
//            }
//        });
//    }
    
    
    /**
     * When the app is authenticated with the Speech API, 
     * enable the interface and speak out a greeting.
    **/
    private void 
    readyForSpeech() 
    {
        // Make Text to Speech request that will speak out a greeting.
    	Random r = new Random();
    	int i1=r.nextInt(4-1) + 1;
    	
    	if(i1 == 1)
    	{
    		startTTS(this.greeting1);
    	}
    	else if(i1 == 2)
    	{
    		startTTS(this.greeting2);
    	}
    	if(i1 == 3)
    	{
    		startTTS(this.greeting3);
    	}
    	
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
}

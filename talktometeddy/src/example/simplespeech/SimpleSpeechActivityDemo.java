package example.simplespeech;

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
import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.GoogleAnalytics;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import com.google.code.chatterbotapi.*;

public class SimpleSpeechActivityDemo extends Activity implements OnInitListener {

    // Connection timeouts for HTTP connections
    public static final int CONNECTION_READ_TIMEOUT_MILLIS = 10000;
    public static final int CONNECTION_CONNECT_TIMEOUT_MILLIS = 15000;

    protected static final int RESULT_SPEECH = 1;
    private static final int TOAST_DURATION = Toast.LENGTH_SHORT;

    //global variables specific to sentence recognition API
    public static String matchingPrompt;
    public static String matchingPromptScore;
    public static String actualPrompt;
    
    long longitem = 0; //used by Google Analytics
    
    private ImageButton heartSpeakButton = null;

    private TextToSpeech tts;
    
    private ChatterBotFactory chatterBotFactory;
    
    private ChatterBot bot;
    private ChatterBotSession botSession;

    private String apikey = "962b2d2b8e72dc6771bca613d49b46fb";

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

    //parse XML
    public static void parseXML(String xmlInput)
            throws XmlPullParserException, IOException {
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
                //System.out.println("Start document");
            } else if (eventType == XmlPullParser.END_DOCUMENT) {
                //System.out.println("End document");
            } else if (eventType == XmlPullParser.START_TAG) {
                //System.out.println("Start tag "+xpp.getName());
                //matchingPrompt = matchingPrompt + xpp.getName() + "***\n";
                name = xpp.getName();
                if (name.equalsIgnoreCase("matching_prompt")) {
                    lastNameWasMatchingPrompt = 1;
                    //matchingPrompt = matchingPrompt + "Matching Prompt Found!\n";
                }
                if (name.equalsIgnoreCase("matching_prompt_score")) {
                    lastNameWasMatchingPromptScore = 1;
                    //matchingPrompt = matchingPrompt + "Matching Prompt Score Found!\n";
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                //System.out.println("End tag "+xpp.getName());
            } else if (eventType == XmlPullParser.TEXT) {
                //System.out.println("Text "+xpp.getText());
                //matchingPrompt = matchingPrompt + xpp.getName() + ": " + xpp.getText() + "\n";
                if (lastNameWasMatchingPrompt == 1) {
                    matchingPrompt = xpp.getText();
                    lastNameWasMatchingPrompt = 0;
                }
                if (lastNameWasMatchingPromptScore == 1) {
                    matchingPromptScore = xpp.getText();
                    lastNameWasMatchingPromptScore = 0;
                }

            }

            try{
            	eventType = xpp.next();
            }
            catch (XmlPullParserException e){
            	Log.e("TTS", "Application Error Occured");              
            }
        }
    }

    /**
     * Called when the activity is first created.  This is where we'll hook up
     * our views in XML layout files to our application.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.speech);
        
        if (BuildConfig.DEBUG) {
			GoogleAnalytics googleAnalytics = GoogleAnalytics
					.getInstance(getApplicationContext());
			googleAnalytics.setAppOptOut(true);
		}


        tts = new TextToSpeech(this, this);
        chatterBotFactory = new ChatterBotFactory();
        try {
			 bot = chatterBotFactory.create(ChatterBotType.CLEVERBOT);
			 botSession = bot.createSession();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        heartSpeakButton = (ImageButton) findViewById(R.id.heartSpeakButton);
        heartSpeakButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                stopTTS();
                //EasyTracker.getTracker().sendEvent("ui-action", "button_press", "speak_button", longitem);
                Intent intent = new Intent(
                        RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");

                try {
                    startActivityForResult(intent, RESULT_SPEECH);
                } catch (ActivityNotFoundException a) {
                    showToast("Oops! Your device doesn't support Speech to Text");
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
     * Called when the activity is coming to the foreground.
     * This is also when we initialize the tracker.
     */
    @Override
    protected void onStart() {
        super.onStart();

        ////EasyTracker.getInstance().activityStart(this);//start Google Analytics API
    }


    /** Make use of the recognition text in this app. **/

    /**
     * Stops any Text to Speech in progress.
     */
    private void stopTTS() {
        tts.stop();
    }

    /**
     * Matches the text with one of the tasks, and
     * start the TTS with matched task.
     *
     * @param speechText recognized speech text used to match task for talk-back
     */
    private void matchTaskWithSpeech(String speechText) {
        //report data to google analytics
    	//EasyTracker.getTracker().sendEvent("internal", "general", "speech_to_text_recognition_successful", longitem); //Google Analytics event
    	//EasyTracker.getTracker().sendEvent("conversation", "user_input", speechText, longitem); //Google Analytics event
    	actualPrompt = speechText;
    	
    	showToast(speechText);
        // And then perform a search on a website using the text.
        String query = URLEncoder.encode(speechText);
        String recognitionURL = "http://www.sentencerecognition.com/sentencerecognition070313.php?input=" + query + "&key=" + apikey +
                "&sentence1=" + this.task1Q_encoded + "" +
                "&sentence2=" + this.task2Q_encoded +
                "&sentence3=" + this.task3Q_encoded +
                "&sentence4=" + this.task4Q_encoded +
                "&sentence5=" + this.task5Q_encoded +
                "&sentence6=" + this.task6Q_encoded +
                "&sentence7=" + this.task7Q_encoded +
                "&sentence8=" + this.task8Q_encoded +
                "&sentence9=" + this.task9Q_encoded +
                "&sentence10=" + this.task10Q_encoded +
                "&sentence11=" + this.task11Q_encoded +
                "&sentence12=" + this.task12Q_encoded +
                "&sentence13=" + this.task13Q_encoded +
                "&sentence14=" + this.task14Q_encoded +
                "&sentence15=" + this.task15Q_encoded +
                "&sentence16=" + this.task16Q_encoded +
                "&sentence17=" + this.task17Q_encoded;

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
        	showToast("Thinking...");
            new DownloadWebpageTask().execute(speechText);
        	
        } else {
            this.startTTS("Teddy needs internet connection to work properly.");
        }

    }

    /**
     * Returns a string converted from a stream.
     *
     * @param is the input stream used to convert to a string
     * @return string converted from the input stream
     * @throws IOException
     */
    public String convertStreamToString(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        is.close();

        return sb.toString();
    }

    /**
     * Returns string for web page content located at specified url.
     *
     * @param myurl the web page url for which content string is returned
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
     * Speaks the output based on speech matching result.
     */
    private void generateOutput() {
        double promptScore = Double.parseDouble(matchingPromptScore);
        if (promptScore < 35) {
        	
            Random r = new Random();
            int i1 = r.nextInt(4 - 1) + 1;

            if (i1 == 1) {
                this.startTTS(this.fallback1);
            } else if (i1 == 2) {
                this.startTTS(this.fallback2);
            }
            else if (i1 == 3) {
                this.startTTS(this.fallback3);
            }
            logUserData("(unrecognized)");
        } else if (matchingPrompt.compareTo(this.task1Q_decoded) == 0) {
            Random r = new Random();
            int i1 = r.nextInt(4 - 1) + 1;

            if (i1 == 1) {
                this.startTTS(this.task1A1);
            } else if (i1 == 2) {
                this.startTTS(this.task1A2);
            }
            if (i1 == 3) {
                this.startTTS(this.task1A3);
            }
            logUserData(this.task1Q_decoded);
        } else if (matchingPrompt.compareTo(this.task2Q_decoded) == 0) {
            this.startTTS(this.task2A1);
            logUserData(this.task2Q_decoded);
        } else if (matchingPrompt.compareTo(this.task3Q_decoded) == 0) {
            Random r = new Random();
            int i1 = r.nextInt(4 - 1) + 1;

            if (i1 == 1) {
                this.startTTS(this.task3A1);
            } else if (i1 == 2) {
                this.startTTS(this.task3A2);
            }
            else if (i1 == 3) {
                this.startTTS(this.task3A3);
            }
            logUserData(this.task3Q_decoded);
            
        } else if (matchingPrompt.compareTo(this.task4Q_decoded) == 0) {
            Random r = new Random();
            int i1 = r.nextInt(5 - 1) + 1;

            if (i1 == 1) {
                this.startTTS(this.task4A1);
            } 
            else if (i1 == 2) {
                this.startTTS(this.task4A2);
            }
            else if (i1 == 3) {
                this.startTTS(this.task4A3);
            }
            else if (i1 == 4) {
                this.startTTS(this.task4A4);
            }
            logUserData(this.task4Q_decoded);
           
        } else if (matchingPrompt.compareTo(this.task5Q_decoded) == 0) {
            Random r = new Random();
            int i1 = r.nextInt(4 - 1) + 1;

            if (i1 == 1) {
                this.startTTS(this.task5A1);
            } else if (i1 == 2) {
                this.startTTS(this.task5A2);
            } else if (i1 == 3) {
                this.startTTS(this.task5A3);
            }
            logUserData(this.task5Q_decoded);
            
        } else if (matchingPrompt.compareTo(this.task6Q_decoded) == 0) {
            //webView.loadData(this.task6Q_decoded, "text/html", "UTF-8");
            Random r = new Random();
            int i1 = r.nextInt(4 - 1) + 1;

            if (i1 == 1) {
                this.startTTS(this.task6A1);
            } else if (i1 == 2) {
                this.startTTS(this.task6A2);
            } else if (i1 == 3) {
                this.startTTS(this.task6A3);
            }
            logUserData(this.task6Q_decoded);
            
        } else if (matchingPrompt.compareTo(this.task7Q_decoded) == 0) {
            this.startTTS(this.task7A);
            logUserData(this.task7Q_decoded);
        } else if (matchingPrompt.compareTo(this.task8Q_decoded) == 0) {
            Random r = new Random();
            int i1 = r.nextInt(4 - 1) + 1;

            if (i1 == 1) {
                this.startTTS(this.task8A1);
            } else if (i1 == 2) {
                this.startTTS(this.task8B1);
            }
            else if (i1 == 3) {
                this.startTTS(this.task8C1);
            }
            logUserData(this.task8Q_decoded);
        } else if (matchingPrompt.compareTo(this.task9Q_decoded) == 0) {
            this.startTTS(this.task9A);
            logUserData(this.task9Q_decoded);
            
        } else if (matchingPrompt.compareTo(this.task10Q_decoded) == 0) {
            this.startTTS(this.task10A);
            logUserData(this.task10Q_decoded);

        } else if (matchingPrompt.compareTo(this.task11Q_decoded) == 0) {
            this.startTTS(this.task11A);
            logUserData(this.task11Q_decoded);

        } else if (matchingPrompt.compareTo(this.task12Q_decoded) == 0) {
            this.startTTS(this.task12A);
            logUserData(this.task12Q_decoded);

        } else if (matchingPrompt.compareTo(this.task13Q_decoded) == 0) {
            this.startTTS(this.task13A);
            logUserData(this.task13Q_decoded);

        } else if (matchingPrompt.compareTo(this.task14Q_decoded) == 0) {
            this.startTTS(this.task14A);
            logUserData(this.task14Q_decoded);

        } else if (matchingPrompt.compareTo(this.task15Q_decoded) == 0) {
            this.startTTS(this.task15A);
            logUserData(this.task15Q_decoded);
        } else if (matchingPrompt.compareTo(this.task16Q_decoded) == 0) {
            Random r = new Random();
            int i1 = r.nextInt(4 - 1) + 1;

            if (i1 == 1) {
                this.startTTS(this.task16A1);
            } else if (i1 == 2) {
                this.startTTS(this.task16B1);
            }
            else if (i1 == 3) {
                this.startTTS(this.task16C1);
            }
            logUserData(this.task16Q_decoded);

        } else if (matchingPrompt.compareTo(this.task17Q_decoded) == 0) {
            this.startTTS(this.task17A);
            logUserData(this.task17Q_decoded);

        } else {
            this.startTTS(this.task7A);
            logUserData("what do you know (default)");
        }
    }
    
    /**
     * Report usage to google analytics
     * @param matchedPrompt, phrase that was matched.
     */
    private void logUserData(String matchedPrompt)
    {
    	//EasyTracker.getTracker().sendEvent("conversation", "user_statement", matchedPrompt+"_"+actualPrompt, longitem); //Google Analytics event
    }

    /**
     * Speaks the given text.
     * If text is empty, speak "You haven't typed text".
     * @param textToSpeak the text to be spoken.
     */
    private void startTTS(String textToSpeak) {
        if (textToSpeak.length() == 0) {
            tts.speak("Teddy don't have anything to say!", TextToSpeech.QUEUE_FLUSH, null);
        } else {
            tts.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null);
            showToast(textToSpeak);
        }
    }

    /**
     * Speaks out a randomized greeting.
     */
    private void greet() {
        // Make Text to Speech request that will speak out a greeting.
        Random r = new Random();
        int i1 = r.nextInt(4 - 1) + 1;

        String greeting = "";

        if (i1 == 1) {
            greeting = this.greeting1;
        } else if (i1 == 2) {
            greeting = this.greeting2;
        } else if (i1 == 3) {
            greeting = this.greeting3;
        }

        startTTS(greeting);
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
            greet();

        } else {
            Log.e("TTS", "Initilization Failed");
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        //EasyTracker.getInstance().activityStop(this); // Add this method.
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
     * Shows a toast containing specified text.
     *
     * @param message text as toast content
     */
    private void showToast(String message) {
        Toast toast = Toast.makeText(this, message, TOAST_DURATION);
        toast.show();
    }

    /**
     * An asynchronous task that handles requests to the speech-matching API.
     * <p/>
     * Uses AsyncTask to create a task away from the main UI thread. This task takes a
     * URL string and uses it to create an HttpUrlConnection. Once the connection
     * has been established, the AsyncTask downloads the contents of the webpage as
     * an InputStream. Finally, the InputStream is converted into a string, which is
     * displayed in the UI by the AsyncTask's onPostExecute method.
     */
    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... urls) {

            try {
				return botSession.think(urls[0]);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				return "Unable to retrieve web page. URL may be invalid.";
			}
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
        	startTTS(result);
            /*
        	try {
                parseXML(result);
                //EasyTracker.getTracker().sendEvent("internal", "general", "sentencerecognition_api_connection_successful", longitem);
                generateOutput();
            } catch (XmlPullParserException e) {
            	//EasyTracker.getTracker().sendEvent("error", "general", "sentencerecognition_api_url_error_XML_Parser_Exception", longitem);
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
            	//EasyTracker.getTracker().sendEvent("error", "general", "sentencerecognition_api_url_error_IOException", longitem);
                // TODO Auto-generated catch block
                e.printStackTrace();
            }*/
        }
    }


}
package example.simplespeech;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import com.example.talktometeddy.R;

/**
 * Created with IntelliJ IDEA.
 * User: liang3404814
 * Date: 6/29/13
 * Time: 3:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class StartScreenActivity extends Activity{


    /**
     * Called when the activity is first created.  This is where we'll hook up
     * our views in XML layout files to our application.
     **/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // First, we specify which layout resource we'll be using.
        setContentView(R.layout.launch_screen);

        // Fetch the OAuth credentials.
        //validateOAuth();
        Handler handler = new Handler();

        // run a thread after 2 seconds to start the home screen
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                // make sure we close the splash screen so the user won't come back when it presses back key


                //if (!mIsBackButtonPressed) {
                // start the home screen if the back button wasn't pressed already
                Intent intent = new Intent(StartScreenActivity.this, SimpleSpeechActivityDemo.class);
                StartScreenActivity.this.startActivity(intent);

                finish();
                //}

            }
        }, 5000); // time in milliseconds (1 second = 1000 milliseconds) until the run() method will be called
        //startActivity(new Intent(StartScreenActivity.this, SimpleSpeechActivityDemo.class));

        // A simple UI-less player for the TTS audio.
        //audioPlayer = new AudioPlayer(this);

        /*// This is the Speak button that the user presses to start a speech
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
        configureWebView();*/
    }

}

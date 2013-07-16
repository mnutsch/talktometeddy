package example.simplespeech;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * Created with IntelliJ IDEA.
 * User: liang3404814
 * Date: 6/29/13
 * Time: 3:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class StartScreenActivity extends Activity {


    /**
     * Called when the activity is first created.  This is where we'll hook up
     * our views in XML layout files to our application.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // First, we specify which layout resource we'll be using.
        setContentView(R.layout.launch_screen);

        Handler handler = new Handler();

        // run a thread after 1 second to start the home screen
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                // make sure we close the splash screen so the user won't come back when it presses back key
                Intent intent = new Intent(StartScreenActivity.this, SimpleSpeechActivityDemo.class);
                StartScreenActivity.this.startActivity(intent);

                finish();

            }
        }, 1000); 
    }
}

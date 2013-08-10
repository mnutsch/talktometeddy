package example.simplespeech;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.widget.ImageButton;
import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiSelector;
import com.talkingteddy.TalkingTeddyActivity;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class example.simplespeech.SimpleSpeechActivityDemoTest \
 * example.simplespeech.tests/android.test.InstrumentationTestRunner
 */
public class SimpleSpeechActivityDemoTest extends ActivityInstrumentationTestCase2<TalkingTeddyActivity> {

    /**
     * Resources to be tested are listed here:
     */

    Activity talkingTeddy;
    ImageButton teddyButton;



    /**
     * Initializes the testing unit.
     */
    public SimpleSpeechActivityDemoTest() {
        super(SimpleSpeechActivityDemo.class);
    }

    protected void setUp() throws Exception {
        super.setUp();

        setActivityInitialTouchMode(false); // turns off touch mode to allow UI interaction testing
        talkingTeddy = getActivity();
        teddyButton = (ImageButton) talkingTeddy.findViewById(R.id.heartSpeakButton);
    }

    public void testPreconditions() {
        assertTrue(talkingTeddy != null);

    }

    public void testTeddyButton() throws Exception {
        assertTrue(teddyButton != null);
        assertTrue(teddyButton.hasOnClickListeners());
    }

}

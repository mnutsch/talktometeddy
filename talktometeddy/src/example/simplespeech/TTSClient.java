package example.simplespeech;

import android.util.Log;

/**
 * This callback object will get the TTS responses.
 **/
public class TTSClient implements TTSRequest.Client
{

    AudioPlayer audioPlayer = null;

    public TTSClient(AudioPlayer audioPlayer) {
        this.audioPlayer = audioPlayer;
    }


    @Override public void
    handleResponse(byte[] audioData, Exception error)
    {
        if (cancel)
            return;
        if (audioData != null) {
            Log.v("SimpleTTS", "Text to Speech returned " + audioData.length + " of audio.");
            audioPlayer.play(audioData);
        }
        else {
            // The TTS service was not able to generate the audio.
            Log.v("SimpleTTS", "Unable to convert text to speech.", error);
            // Real applications probably shouldn't display an alert.
            //TODO: have some kind of fallback
            //alert(null, "Unable to convert text to speech.");
        }
    }
    /** Set to true to prevent playing. **/
    boolean cancel = false;
}
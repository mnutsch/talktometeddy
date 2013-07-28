package com.talkingteddy;

import android.*;
import android.R;
import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

/**
 * Created with IntelliJ IDEA.
 * User: liang3404814
 * Date: 7/28/13
 * Time: 2:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class SplashVideoActivity extends Activity {

    private VideoView videoView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        videoView = new VideoView(this);
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                SplashVideoActivity.this.finish();
            }
        });

        Uri videoURI = Uri.parse("android.resource://" + getPackageName() + "/" + com.talkingteddy.R.raw.test);
        videoView.setVideoURI(videoURI);
        setContentView(videoView);
        videoView.start();

    }
}
package com.talkingteddy;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.VideoView;

import java.util.Date;
import android.net.Uri;

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

        String videoName = getIntent().getExtras().getString("VIDEO_NAME");


        Uri videoURI = Uri.parse("android.resource://" + getPackageName() + "/raw/" + videoName);
        videoView.setVideoURI(videoURI);
        setContentView(videoView);
        videoView.start();

    }
}
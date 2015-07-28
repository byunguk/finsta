package com.homework.finsta.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.widget.MediaController;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

import com.homework.finsta.R;
import com.homework.finsta.util.Const;

/**
 * Created by kbw815 on 7/27/15.
 */
public class PlayActivity extends Activity {
    private MediaController mMediaController;
    private VideoView mVideoView;
    private String mUrl;
    private ProgressDialog mProgressDialog;

    private MediaPlayer.OnPreparedListener mPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            mProgressDialog.dismiss();
            mp.start();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        mUrl = getIntent().getStringExtra(Const.FIELD_URL);
        bindUIElements();
        setUpListeners();
        setUpVideoView();
    }

    private void bindUIElements()
    {
        mVideoView = (VideoView)findViewById(R.id.play_video_view);
    }

    private void setUpListeners()
    {
        mVideoView.setOnPreparedListener(mPreparedListener);
    }

    private void setUpVideoView()
    {
        mMediaController = new MediaController(this);
        mVideoView.setVideoURI(Uri.parse(mUrl));
        mVideoView.setMediaController(mMediaController);
        mProgressDialog = ProgressDialog.show(this, "", "Loading...", true);
    }
}

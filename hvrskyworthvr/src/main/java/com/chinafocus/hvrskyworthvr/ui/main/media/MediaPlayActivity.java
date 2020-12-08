package com.chinafocus.hvrskyworthvr.ui.main.media;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.chinafocus.hvrskyworthvr.R;

public class MediaPlayActivity extends AppCompatActivity {

    public static final String MEDIA_ID = "media_id";
    public static final String MEDIA_CATEGORY_TAG = "media_category_tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_play);

        Intent intent = getIntent();
        int video_id = intent.getIntExtra(MEDIA_ID, -1);
        String video_tag = intent.getStringExtra(MEDIA_CATEGORY_TAG);

        MediaViewModel mediaViewModel = new ViewModelProvider(this).get(MediaViewModel.class);
        mediaViewModel.getVideoDetailData(video_tag, video_id);
        mediaViewModel.videoDetailMutableLiveData.observe(this, videoDetail -> {
            Log.e("MyLog", " videoDetail >>> " + videoDetail.getTitle());
        });

    }
}
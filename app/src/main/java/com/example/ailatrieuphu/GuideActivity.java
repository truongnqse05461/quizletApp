package com.example.ailatrieuphu;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class GuideActivity extends AppCompatActivity {

    List<TextView> textViewList;
    List<ImageView> imageViewList;

    private TextView question1TextView;
    private TextView question2TextView;
    private TextView question3TextView;
    private TextView question4TextView;
    private TextView question5TextView;
    private TextView question6TextView;
    private TextView question7TextView;
    private TextView question8TextView;
    private TextView question9TextView;
    private TextView question10TextView;
    private TextView question11TextView;
    private TextView question12TextView;
    private TextView question13TextView;
    private TextView question14TextView;
    private TextView question15TextView;
    private ImageView guideCallImageView;
    private ImageView guideFiftyImageView;
    private ImageView guideChangeImageView;

    private MediaManager backgroundSound;
    private MediaManager guideSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        initControls();
    }

    private void initControls() {

        backgroundSound = new MediaManager(GuideActivity.this);
        backgroundSound.openMedia(R.raw.score, true);

        guideSound = new MediaManager(GuideActivity.this);
        guideSound.openMedia(R.raw.guide, false);

        question1TextView = (TextView) findViewById(R.id.ques1);
        question2TextView = (TextView) findViewById(R.id.ques2);
        question3TextView = (TextView) findViewById(R.id.ques3);
        question4TextView = (TextView) findViewById(R.id.ques4);
        question5TextView = (TextView) findViewById(R.id.ques5);
        question6TextView = (TextView) findViewById(R.id.ques6);
        question7TextView = (TextView) findViewById(R.id.ques7);
        question8TextView = (TextView) findViewById(R.id.ques8);
        question9TextView = (TextView) findViewById(R.id.ques9);
        question10TextView = (TextView) findViewById(R.id.ques10);
        question11TextView = (TextView) findViewById(R.id.ques11);
        question12TextView = (TextView) findViewById(R.id.ques12);
        question13TextView = (TextView) findViewById(R.id.ques13);
        question14TextView = (TextView) findViewById(R.id.ques14);
        question15TextView = (TextView) findViewById(R.id.ques15);

        guideCallImageView = (ImageView) findViewById(R.id.guide_call);
        guideFiftyImageView = (ImageView) findViewById(R.id.guide_fifty);
        guideChangeImageView =(ImageView)findViewById(R.id.guide_change);

        textViewList = new ArrayList<>();
        textViewList.add(question1TextView);
        textViewList.add(question2TextView);
        textViewList.add(question3TextView);
        textViewList.add(question4TextView);
        textViewList.add(question5TextView);
        textViewList.add(question6TextView);
        textViewList.add(question7TextView);
        textViewList.add(question8TextView);
        textViewList.add(question9TextView);
        textViewList.add(question10TextView);
        textViewList.add(question11TextView);
        textViewList.add(question12TextView);
        textViewList.add(question13TextView);
        textViewList.add(question14TextView);
        textViewList.add(question15TextView);

        imageViewList = new ArrayList<>();
        imageViewList.add(guideFiftyImageView);
        imageViewList.add(guideCallImageView);
        imageViewList.add(guideChangeImageView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        guideSound.playBackGround();
        backgroundSound.playBackGround();
    }

    @Override
    protected void onPause() {
        super.onPause();
        backgroundSound.pause();
        if (guideSound.isPlaying())
            guideSound.pause();
    }
}

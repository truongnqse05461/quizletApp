package com.example.ailatrieuphu;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ScoreActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<ScoreItem> listScore;
    private CustomAdapter adapter;
    private MediaManager background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        this.loadScore();
        this.initView();
        this.initControls();
    }

    public void initControls() {

        background = new MediaManager(ScoreActivity.this);
        background.openMedia(R.raw.score, true);
    }

    private void loadScore() {
        listView = (ListView) findViewById(R.id.list_score);
        listScore = new ArrayList<>();
        listScore.addAll(StartActivity.scoreHandler.getListScores());
    }

    private void initView() {
        adapter = new CustomAdapter(ScoreActivity.this, R.layout.custom_layout_score, listScore);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        background.playBackGround();
    }

    @Override
    protected void onPause() {
        super.onPause();
        background.pause();
    }
}

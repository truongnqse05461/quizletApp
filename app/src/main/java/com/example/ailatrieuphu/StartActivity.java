package com.example.ailatrieuphu;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

public class StartActivity extends AppCompatActivity implements View.OnClickListener {

    public static ScoreHandler scoreHandler;

    private ToggleButton btnMusic;
    private Button btnPlay;
    private Button btnScore;
    private Button btnGuide;

    private Button btnChangeName;
    public static TextView playerNameTextView;

    private MediaManager backgroundMediaManager;
    private MediaManager startMediaManager;
    private MediaManager soundPoolStartMediaManager;
    public static MediaManager soundPoolMain;

    private RelativeLayout relativeStartLayout;
    private Dialog infoDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        initControls();
        setControls();
    }

    @Override
    protected void onResume() {

        super.onResume();
        backgroundMediaManager.playBackGround();
        if (playerNameTextView.getText().toString().length() <= 0 && loadPlayerName().length() <= 0) {
            playerNameTextView.setText("");
        } else {
            playerNameTextView.setText(loadPlayerName());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        backgroundMediaManager.pause();
    }

    public void initControls() {

        scoreHandler = new ScoreHandler(this);

        relativeStartLayout = (RelativeLayout) findViewById(R.id.start_layout);
        btnMusic = (ToggleButton) findViewById(R.id.btnMusic);
        btnPlay = (Button) findViewById(R.id.btnPlay);
        btnScore = (Button) findViewById(R.id.btnScore);
        btnGuide = (Button) findViewById(R.id.btnGuide);
        btnChangeName = (Button) findViewById(R.id.btnChangeName);
        playerNameTextView = (TextView) findViewById(R.id.player_name);

        backgroundMediaManager = new MediaManager(StartActivity.this);
        backgroundMediaManager.openMedia(R.raw.bgstart, true);

        startMediaManager = new MediaManager(StartActivity.this);
        startMediaManager.openMedia(R.raw.start, false);

        soundPoolStartMediaManager = new MediaManager(StartActivity.this);
        soundPoolStartMediaManager.add("ready", R.raw.ready);
        soundPoolStartMediaManager.add("joker", R.raw.joker);
        soundPoolMain = new MediaManager(StartActivity.this);
    }

    public void addSoundPool() {
        soundPoolMain.add("true1", R.raw.true1);
        soundPoolMain.add("false", R.raw.false1);
        soundPoolMain.add("final_answer2", R.raw.final_answer2);
        soundPoolMain.add("fifty", R.raw.help_fifty);

    }

    public void setControls() {
        btnMusic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    backgroundMediaManager.pause();
                    btnMusic.setBackgroundResource(R.drawable.music_off);
                } else {
                    backgroundMediaManager.playBackGround();
                    btnMusic.setBackgroundResource(R.drawable.music);
                }
            }
        });
        btnPlay.setOnClickListener(this);
        btnGuide.setOnClickListener(this);
        btnScore.setOnClickListener(this);
        btnChangeName.setOnClickListener(this);
    }

    private void toggleControls(boolean willEnable, ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View child = viewGroup.getChildAt(i);
            child.setEnabled(willEnable);
            if (child instanceof ViewGroup) {
                toggleControls(willEnable, (ViewGroup) child);
            }
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.btnPlay:
                toggleControls(false, relativeStartLayout);
                backgroundMediaManager.pause();
                soundPoolStartMediaManager.play("joker");
                new AlertDialog.Builder(StartActivity.this)
                        .setMessage("Bạn đã sẵn sàng chưa?")
                        .setNegativeButton("Sẵn sàng", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int id) {

                                backgroundMediaManager.stop();
                                addSoundPool();
                                toggleControls(true, relativeStartLayout);
                                startActivity(new Intent(StartActivity.this, MainActivity.class));

                            }
                        })
                        .setCancelable(false)
                        .setPositiveButton("Chưa", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                toggleControls(true, relativeStartLayout);
                                backgroundMediaManager.playBackGround();
                            }
                        }).show();

                break;

            case R.id.btnScore:
                toggleControls(false, relativeStartLayout);
                backgroundMediaManager.pause();
                soundPoolStartMediaManager.play("joker");
                startActivity(new Intent(StartActivity.this, ScoreActivity.class));
                toggleControls(true, relativeStartLayout);
                break;

            case R.id.btnGuide:
                toggleControls(false, relativeStartLayout);
                backgroundMediaManager.pause();
                soundPoolStartMediaManager.play("joker");
                startActivity(new Intent(StartActivity.this, GuideActivity.class));
                toggleControls(true, relativeStartLayout);
                break;

            case R.id.btnChangeName:
                final EditText txtName = new EditText(this);
                txtName.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                txtName.setGravity(Gravity.CENTER);
                new AlertDialog.Builder(new ContextThemeWrapper(StartActivity.this, android.R.style.Widget_Material_Light))
                        .setView(txtName)
                        .setTitle("Nhập tên của bạn")
                        .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                playerNameTextView.setText(txtName.getText().toString());
                                savePlayerName(txtName.getText().toString());
                            }
                        })
                        .setCancelable(false)
                        .setPositiveButton("Cancel", null)
                        .show();

                break;

        }
    }

    private void savePlayerName(String name) {
        SharedPreferences preferences = getSharedPreferences("prefName", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("name", name);
        editor.commit();
    }

    private String loadPlayerName() {
        SharedPreferences preferences = getSharedPreferences("prefName", MODE_PRIVATE);
        String name = preferences.getString("name", "");
        return name;
    }
}


package com.example.ailatrieuphu;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
//import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    List<QuestionItem> questionList;
    String currentDate;
    private static final long TIME_FOR_QUESTION = 21000;
    private DatabaseHandler questionHandler;
    private QuestionItem currentQuestion;
    private CountDownTimer timerQuestion;
    private long currentTime;

    // Question board views
    private TextView questionTextView;
    private TextView optionATextView;
    private TextView optionBTextView;
    private TextView optionCTextView;
    private TextView optionDTextView;
    private TextView questionNumberTextView;
    private TextView moneyTextView;
    private TextView timeTextView;
    private List<TextView> listAnswetOptionTextView;

    // Help board views
    private ImageView stopImageView;

    private ImageView helpCallImageView;
    private ImageView disableHelpCallImageView;

    private ImageView help5050ImageView;
    private ImageView disableHelp5050ImageView;

    private ImageView helpChangeQuestionImageView;
    private ImageView disableHelpChangeQuestionImageView;

    //
    private RelativeLayout relativeMainLayout;
    private int indexOfCurrentQuestion = 0;
    private int score = 0;

    // hash map
    private HashMap<String, TextView> mapOptionsTextView;
    private List<TextView> mapFiftyRemainOptionsAfter5050;

    // Media items
    private MediaManager backgroundMainSound;
    private MediaManager soundPool;
    private MediaManager callWaitingSound;
    private MediaManager callProgressSound;
    private MediaManager audienceWaitingSound;
    private ToggleButton btnMusic;

    private boolean hasUse5050 = false;
    private LinearLayout layout1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initControls();
        this.backgroundMainSound.playBackGround();
        loadQuestions();
        displayQuestion();
        this.questionHandler.closeDatabase();
        setControls();
    }

    public void initControls() {

        DateFormat df = new SimpleDateFormat("dd-MMM-yy HH:mm");
        this.currentDate = df.format(Calendar.getInstance().getTime());
        this.relativeMainLayout = (RelativeLayout) findViewById(R.id.main_layout);

        this.questionNumberTextView = (TextView) findViewById(R.id.question_number);
        this.moneyTextView = (TextView) findViewById(R.id.money);
        this.timeTextView = (TextView) findViewById(R.id.time);
        this.questionTextView = (TextView) findViewById(R.id.question);
        this.optionATextView = (TextView) findViewById(R.id.optionA);
        this.optionBTextView = (TextView) findViewById(R.id.optionB);
        this.optionCTextView = (TextView) findViewById(R.id.optionC);
        this.optionDTextView = (TextView) findViewById(R.id.optionD);

        this.listAnswetOptionTextView = new ArrayList<>();
        this.listAnswetOptionTextView.add(optionATextView);
        this.listAnswetOptionTextView.add(optionBTextView);
        this.listAnswetOptionTextView.add(optionCTextView);
        this.listAnswetOptionTextView.add(optionDTextView);

        this.mapOptionsTextView = new HashMap<>();
        this.mapOptionsTextView.put("A", optionATextView);
        this.mapOptionsTextView.put("B", optionBTextView);
        this.mapOptionsTextView.put("C", optionCTextView);
        this.mapOptionsTextView.put("D", optionDTextView);

        this.mapFiftyRemainOptionsAfter5050 = new ArrayList<>();

        this.stopImageView = (ImageView) findViewById(R.id.stop);
        this.helpCallImageView = (ImageView) findViewById(R.id.help_call);
        this.disableHelpCallImageView = (ImageView) findViewById(R.id.turn_off_call);
        this.help5050ImageView = (ImageView) findViewById(R.id.help_50_50);
        this.disableHelp5050ImageView = (ImageView) findViewById(R.id.turn_off_50_50);
        this.helpChangeQuestionImageView = (ImageView) findViewById(R.id.help_change_question);
        this.disableHelpChangeQuestionImageView = (ImageView) findViewById(R.id.turn_off_change);
        this.btnMusic = (ToggleButton) findViewById(R.id.btnMusic);

        this.backgroundMainSound = new MediaManager(MainActivity.this);
        this.backgroundMainSound.openMedia(R.raw.bgmain1, true);
        this.callWaitingSound = new MediaManager(MainActivity.this);
        this.callWaitingSound.openMedia(R.raw.call_waiting, true);
        this.callProgressSound = new MediaManager(MainActivity.this);
        this.callProgressSound.openMedia(R.raw.call_progress, false);
        this.audienceWaitingSound = new MediaManager(MainActivity.this);
        this.audienceWaitingSound.openMedia(R.raw.audience_waiting, false);
        this.soundPool = StartActivity.soundPoolMain;
    }

    public void setControls() {

        this.optionATextView.setOnClickListener(this);
        this.optionBTextView.setOnClickListener(this);
        this.optionCTextView.setOnClickListener(this);
        this.optionDTextView.setOnClickListener(this);

        this.stopImageView.setOnClickListener(this);
        this.helpCallImageView.setOnClickListener(this);
        this.disableHelpCallImageView.setOnClickListener(this);
        this.help5050ImageView.setOnClickListener(this);
        this.disableHelp5050ImageView.setOnClickListener(this);
        this.helpChangeQuestionImageView.setOnClickListener(this);
        this.disableHelpChangeQuestionImageView.setOnClickListener(this);


        this.btnMusic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    backgroundMainSound.pause();
                    btnMusic.setBackgroundResource(R.drawable.music_off);
                } else {
                    backgroundMainSound.playBackGround();
                    btnMusic.setBackgroundResource(R.drawable.music);
                }
            }
        });

        this.btnMusic.setOnClickListener(this);
    }

    public void loadQuestions() {
        this.questionHandler = new DatabaseHandler(this);
        this.questionList = questionHandler.getListQuestions();
        this.currentQuestion = questionList.get(indexOfCurrentQuestion);
    }

    public void displayQuestion() {
        this.indexOfCurrentQuestion++;
        this.setNextQuestion();
        this.questionNumberTextView.setText(" Câu hỏi số " + indexOfCurrentQuestion + " ");
        this.timeHandler(TIME_FOR_QUESTION);
        this.questionTextView.setText(currentQuestion.getQuestionContent());
        this.optionATextView.setText(currentQuestion.getOptionA());
        this.optionBTextView.setText(currentQuestion.getOptionB());
        this.optionCTextView.setText(currentQuestion.getOptionC());
        this.optionDTextView.setText(currentQuestion.getOptionD());
    }

    public void changeQuestion() {
        this.currentQuestion = this.questionList.get(15);
        this.questionTextView.setText(currentQuestion.getQuestionContent());
        this.optionATextView.setText(currentQuestion.getOptionA());
        this.optionBTextView.setText(currentQuestion.getOptionB());
        this.optionCTextView.setText(currentQuestion.getOptionC());
        this.optionDTextView.setText(currentQuestion.getOptionD());
        if (this.hasUse5050)
            showAllOptions();
    }

    private void timeHandler(long timePlay) {

        this.timerQuestion = new CountDownTimer(timePlay, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                timeTextView.setText(millisUntilFinished / 1000 - 1 + "");
                currentTime = millisUntilFinished;
            }

            @Override
            public void onFinish() {
                new AlertDialog.Builder(MainActivity.this).setMessage("Bạn có muốn chơi lại không?")
                        .setPositiveButton("Không", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).setCancelable(false)
                        .setTitle("Hết thời gian!")
                        .setNegativeButton("Có", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                                startActivity(getIntent());
                            }
                        })
                        .show();
            }
        };
        this.timerQuestion.start();
    }

    private void pauseTimer() {
        if (this.currentTime > 0)
            this.timerQuestion.cancel();
    }

    private void resumeTimer() {
        if (this.currentTime > 0)
            timeHandler(this.currentTime);
    }

    public void setNextQuestion() {

        switch (indexOfCurrentQuestion) {

            case 1:
                this.moneyTextView.setText("200");
                this.score = 200;
                break;

            case 2:
                this.moneyTextView.setText("400");
                this.score = 400;
                break;

            case 3:
                this.moneyTextView.setText("600");
                this.score = 600;
                break;

            case 4:
                this.moneyTextView.setText("1.000");
                this.score = 1000;
                break;

            case 5:
                this.moneyTextView.setText("2.000");
                this.score = 2000;
                break;

            case 6:
                this.moneyTextView.setText("3.000");
                this.score = 3000;
                break;

            case 7:
                this.moneyTextView.setText("6.000");
                this.score = 6000;
                break;

            case 8:
                this.moneyTextView.setText("10.000");
                this.score = 10000;
                break;

            case 9:
                this.moneyTextView.setText("14.000");
                this.score = 14000;
                break;

            case 10:
                this.moneyTextView.setText("22.000");
                this.score = 22000;
                break;

            case 11:
                this.moneyTextView.setText("30.000");
                this.score = 30000;
                break;

            case 12:
                this.moneyTextView.setText("40.000");
                this.score = 40000;
                break;

            case 13:
                this.moneyTextView.setText("60.000");
                this.score = 60000;
                break;

            case 14:
                this.moneyTextView.setText("85.000");
                this.score = 85000;
                break;

            case 15:
                this.moneyTextView.setText("150.000");
                this.score = 150000;
                break;
        }
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            // click on 1 out of 4 answer button
            case R.id.optionA:
                answerHandler(optionATextView.getText().toString(), optionATextView);
                break;

            case R.id.optionB:
                answerHandler(optionBTextView.getText().toString(), optionBTextView);
                break;

            case R.id.optionC:
                answerHandler(optionCTextView.getText().toString(), optionCTextView);
                break;

            case R.id.optionD:
                answerHandler(optionDTextView.getText().toString(), optionDTextView);
                break;

            case R.id.stop:
                new AlertDialog.Builder(MainActivity.this).setMessage("Bạn có chắc chắn muốn dừng cuộc chơi?")
                        .setPositiveButton("Dừng", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (indexOfCurrentQuestion >= 5) {
                                    saveScore();
                                }
                                finish();
                            }
                        }).setCancelable(false)
                        .setNegativeButton("Chơi tiếp", null)
                        .show();
                break;

                // click on 50-50 button
            case R.id.help_50_50:

                disableHelp5050ImageView.setVisibility(View.VISIBLE);
                help5050ImageView.setEnabled(false);
                soundPool.play("fifty");
                hasUse5050 = true;

                int count = 0;
                String save = "";
                String alphabet = "ABCD";
                int n = alphabet.length();
                while (count < 2) {
                    Random random = new Random();

                    // randomOption from 0->3
                    String randomOption = String.valueOf(alphabet.charAt(random.nextInt(n)));

                    // if randomOption != answer && randomOption != randomOptionBefore
                    if (!randomOption.equals(currentQuestion.getAnswer()) && !save.equals(randomOption)) {
                        mapOptionsTextView.get(randomOption).setVisibility(View.INVISIBLE);
                        count++;
                        save = randomOption;
                    }
                }

                for (TextView answerOptionTextView : listAnswetOptionTextView) {
                    if (answerOptionTextView.getVisibility() == View.VISIBLE)
                        mapFiftyRemainOptionsAfter5050.add(answerOptionTextView);
                }
                break;


            case R.id.help_call:

                helpCallImageView.setEnabled(false);
                disableHelpCallImageView.setVisibility(View.VISIBLE);

                final Dialog callDialog = new Dialog(MainActivity.this, android.R.style.Theme_Holo_Light_Dialog);
                             callDialog.setContentView(R.layout.custom_layout_call);
                             callDialog.setTitle("Bạn sẽ gọi cho ai?");

                pauseBackground();
                pauseTimer();
//                callWaitingSound.playBackGround();

                final TextView callAnswer = (TextView) callDialog.findViewById(R.id.call_answer);
                final ProgressBar spinner = (ProgressBar) callDialog.findViewById(R.id.spinner);

                final ImageView callDoctor = (ImageView) callDialog.findViewById(R.id.call_doctor);
                final ImageView callTeacher = (ImageView) callDialog.findViewById(R.id.call_teacher);
                final ImageView callFootballer = (ImageView) callDialog.findViewById(R.id.call_footballer);
                final ImageView callLvs = (ImageView) callDialog.findViewById(R.id.call_lvs);

                callDoctor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        callProgressSound.playBackGround();
                        spinner.setVisibility(View.VISIBLE);
                        // disable allView:
                        callDoctor.setEnabled(false);
                        callTeacher.setEnabled(false);
                        callFootballer.setEnabled(false);
                        callLvs.setEnabled(false);
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                spinner.setVisibility(View.GONE);
                                callAnswer.setVisibility(View.VISIBLE);
                                callAnswer.setText("Theo tôi đáp án là " + currentQuestion.getAnswer());
                            }
                        }, 8000);
                    }
                });

                callTeacher.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callProgressSound.playBackGround();
                        spinner.setVisibility(View.VISIBLE);
                        callDoctor.setEnabled(false);
                        callTeacher.setEnabled(false);
                        callFootballer.setEnabled(false);
                        callLvs.setEnabled(false);
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                spinner.setVisibility(View.GONE);
                                callAnswer.setVisibility(View.VISIBLE);
                                callAnswer.setText("Tôi nghĩ đáp án là " + currentQuestion.getAnswer());
                            }
                        }, 8000);
                    }
                });

                callFootballer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callProgressSound.playBackGround();
                        spinner.setVisibility(View.VISIBLE);
                        callDoctor.setEnabled(false);
                        callTeacher.setEnabled(false);
                        callFootballer.setEnabled(false);
                        callLvs.setEnabled(false);
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                spinner.setVisibility(View.GONE);
                                callAnswer.setVisibility(View.VISIBLE);
                                callAnswer.setText("Tôi nghĩ đáp án là A");
                                if (hasUse5050)
                                    callAnswer.setText("Tôi nghĩ đáp án là " + String.valueOf(mapFiftyRemainOptionsAfter5050.get(1).getText().charAt(0)));
                            }
                        }, 8000);
                    }
                });

                callLvs.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        spinner.setVisibility(View.VISIBLE);
                        callDoctor.setEnabled(false);
                        callTeacher.setEnabled(false);
                        callFootballer.setEnabled(false);
                        callLvs.setEnabled(false);
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                spinner.setVisibility(View.GONE);
                                callAnswer.setVisibility(View.VISIBLE);
                                callAnswer.setText("Số điện thoại này tạm thời không liên lạc được!!");
                            }
                        }, 5000);
                    }
                });

                callDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        resumeBackground();
                        callWaitingSound.stop();
                        callWaitingSound.pRelease();
                        callProgressSound.pRelease();
                        resumeTimer();
                    }
                });

                callDialog.show();
                break;

            case R.id.help_change_question:
                disableHelpChangeQuestionImageView.setVisibility(View.VISIBLE);
                helpChangeQuestionImageView.setEnabled(false);

                changeQuestion();
                break;
        }
    }

    public void answerHandler(final String answer, final TextView optionTextView) {

        this.pauseTimer();

        new AlertDialog.Builder(MainActivity.this)
                .setMessage("Câu trả lời cuối cùng của bạn là " + answer.substring(0, 1) + "?")
                .setNegativeButton("Đồng ý", new DialogInterface.OnClickListener() {
                    // on Dong Y:
                    public void onClick(DialogInterface dialog, int id) {
                        pauseTimer();
                        // Câu trả lời được chọn
                        optionTextView.setBackgroundResource(R.drawable.press_question);
                        soundPool.play("final_answer2");
                        toggleControls(false, relativeMainLayout);


                        Runnable chooseAnswer = new Runnable() {
                            @Override
                            public void run() {
                                // Câu trả lời đúng
                                if (checkAnswer(answer)) {
                                    // chua phai la cau hoi cuoi cung
                                    if (indexOfCurrentQuestion < 15) {

                                        soundPool.play("true1");
                                        new CountDownTimer(1500, 100) {
                                            // blinking answer textView
                                            @Override
                                            public void onTick(long millisUntilFinished) {
                                                int time = (int) (millisUntilFinished / 100);
                                                if (time % 2 == 0) {
                                                    optionTextView.setBackgroundResource(R.drawable.press_question);
                                                } else {
                                                    optionTextView.setBackgroundResource(R.drawable.true_question);
                                                }
                                            }

                                            @Override
                                            public void onFinish() {
                                                toggleControls(true, relativeMainLayout);
                                                currentQuestion = questionList.get(indexOfCurrentQuestion);
                                                displayQuestion();
                                                showAllOptions();
                                            }

                                        }.start();

                                    }

                                    // day la cau hoi cuoi cung
                                    else if (indexOfCurrentQuestion == 15) {
                                        saveScore();
                                        new CountDownTimer(10000, 500) {
                                            @Override
                                            public void onTick(long millisUntilFinished) {
                                                int time = (int) (millisUntilFinished / 100);
                                                if (time % 2 == 0) {
                                                    optionTextView.setBackgroundResource(R.drawable.press_question);
                                                } else {
                                                    optionTextView.setBackgroundResource(R.drawable.true_question);
                                                }
                                            }

                                            @Override
                                            public void onFinish() {
                                                toggleControls(true, relativeMainLayout);
                                                new AlertDialog.Builder(MainActivity.this)
                                                        .setMessage("Bạn có muốn chơi lại không?")
                                                        .setPositiveButton("Không", new DialogInterface.OnClickListener() {

                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        finish();
                                                    }

                                                }).setCancelable(false)
                                                        .setTitle("Bạn là Triệu Phú. Xin chúc mừng!")
                                                        .setNegativeButton("Có", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                finish();
                                                                startActivity(getIntent());
                                                            }
                                                        })
                                                        .show();
                                            }
                                        }.start();
                                    }


                                    // Câu trả lời sai
                                } else {

                                    pauseBackground();
                                    soundPool.play("false");

//                                    // luu diem neu qua dc cau hoi so 5
                                    if (indexOfCurrentQuestion > 5) {
                                        saveScore();
                                    }

//                                    saveScore();

                                    new CountDownTimer(1500, 100) {
                                        @Override
                                        public void onTick(long millisUntilFinished) {
                                            int time = (int) (millisUntilFinished / 100);
                                            if (time % 2 == 0) {
                                                optionTextView.setBackgroundResource(R.drawable.press_question);
                                                mapOptionsTextView.get(currentQuestion.getAnswer()).setBackgroundResource(R.drawable.press_question);
                                            } else {
                                                mapOptionsTextView.get(currentQuestion.getAnswer()).setBackgroundResource(R.drawable.true_question);

                                            }
                                        }

                                        @Override
                                        public void onFinish() {
                                            new AlertDialog.Builder(MainActivity.this)
                                                    .setTitle("Game Over")
                                                    .setMessage("Bạn có muốn chơi lại không?")
                                                    .setNegativeButton("Có", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            finish();
                                                            startActivity(getIntent());
                                                        }
                                                    })
                                                    .setCancelable(false)
                                                    .setPositiveButton("Không", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            finish();
                                                        }
                                                    })
                                                    .show();
                                        }
                                    }.start();
                                }
                            }
                        };
                        optionTextView.postDelayed(chooseAnswer, 3000);
//                        if (indexOfCurrentQuestion <= 5)
//                            optionTextView.postDelayed(chooseAnswer, 1000);
//                        else if (indexOfCurrentQuestion > 5)
//                            optionTextView.postDelayed(chooseAnswer, 5500);
                    }
                }).setCancelable(false)
                .setPositiveButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        resumeTimer();
                    }
                })
                .show();

    }

    public boolean checkAnswer(String answer) {
        return answer.substring(0, 1).equals(currentQuestion.getAnswer());
    }

    // show the options after using 50-50
    private void showAllOptions() {

        for (int i = 0; i < listAnswetOptionTextView.size(); i++) {
            listAnswetOptionTextView.get(i).setVisibility(View.VISIBLE);
            listAnswetOptionTextView.get(i).setBackgroundResource(R.drawable.normal_question);
        }

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
    protected void onPause() {
        super.onPause();
        pauseBackground();
        pauseTimer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        resumeBackground();
        resumeTimer();
    }

    private void resumeBackground() {
        backgroundMainSound.playBackGround();
    }

    private void pauseBackground() {
        backgroundMainSound.pause();
    }

    public void saveScore() {
        String playerName = StartActivity.playerNameTextView.getText().toString();
        if (playerName.trim().isEmpty()) {
            StartActivity.scoreHandler.addScore(new ScoreItem("Unknown Player", score, currentDate));
        } else {
            StartActivity.scoreHandler.addScore(new ScoreItem(StartActivity.playerNameTextView.getText().toString(), score, currentDate));
        }
    }
}

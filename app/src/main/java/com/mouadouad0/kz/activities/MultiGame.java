package com.mouadouad0.kz.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mouadouad0.kz.Error;
import com.mouadouad0.kz.MainGame;
import com.mouadouad0.kz.R;
import com.mouadouad0.kz.Shared;

import java.util.Locale;
import java.util.Random;

public class MultiGame extends AppCompatActivity {

    public final static String who_key = "com.mouad0.hp.snake.who_key";

    DatabaseReference my_player, his_player;
    ValueEventListener hisReadyListener, hisRepeatListener;
    String hisReady;

    int line;
    String s;
    int a, b, c, d;
    Button confirm, replay;
    Boolean error = false, played = true, win = false;
    Boolean I_repeat = false, He_repeat = false;
    TextView at1;

    FrameLayout theDimShown;
    RelativeLayout theBoxShown;
    ImageView play_again;

    long Time_start = 120000;
    TextView tv;
    CountDownTimer timer;
    private long left = Time_start;
    MediaPlayer time_finished;

    MainGame main_game_class;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (MultiMode.name == null) {
            Shared.navigateBack(this, MultiMode.class);
            return;
        }

        main_game_class = new MainGame(this);
        setContentView(main_game_class);
        Shared.setUpScreen(this);

        setConfirmButton();
        setTimer();
        Shared.background(this, this);
        Shared.backButton(this, this, MultiMode.class);
        setTextView();

        Intent intent = getIntent();

        play_again = new ImageView(this);

        //SEE WHICH PLAYER
        if (intent.getStringExtra(who_key).equals("create")) {

            my_player = Shared.lobby(MultiMode.name).child("player1");
            his_player = Shared.lobby(MultiMode.name).child("player2");

        } else {

            his_player = Shared.lobby(MultiMode.name).child("player1");
            my_player = Shared.lobby(MultiMode.name).child("player2");
        }

        startAndRandoms();

        //ON CONFIRM ANSWER
        confirm.setOnClickListener(view -> {
            if (!Shared.isOnline(this)) {
                showError(R.drawable.internet_box);
            } else {

                if (!error) { //IF THERE IS NO ERROR

                    if (played) { //IF I PLAYED
                        checkAnswer();
                        if (!error) {
                            //IF I HAD IT RIGHT OR NOT
                            if (win) {
                                my_player.child("ready").setValue("2");
                            } else {
                                my_player.child("ready").setValue("1");
                            }

                            played = false;
                            pause();
                            checkOpponent();

                        }
                    }

                }

            }
        });

        hisReadyListener = his_player.child("ready").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                hisReady = dataSnapshot.getValue(String.class);
                checkOpponent();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //SEE IF WANT TO REPEAT
        hisRepeatListener = his_player.child("repeat").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if ("1".equals(dataSnapshot.getValue(String.class))) {//HE WANTS TO REPLAY
                    He_repeat = true;

                    if (I_repeat) { // I ALREADY WANT TO REPLAY
                        replay();
                    } else if (play_again.getParent() == null) {

                        RelativeLayout.LayoutParams layoutParams4 = new RelativeLayout.LayoutParams(Shared.setX(700), Shared.setY(180));
                        play_again.setBackgroundResource(R.drawable.play_again_box);
                        addContentView(play_again, layoutParams4);
                        play_again.setX(Shared.setX(190));
                        play_again.setY(Shared.setY(505));
                        play_again.setZ(30);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //START TIMER
        startT();

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Shared.navigateBack(MultiGame.this, MultiMode.class);
            }
        });

    }

    private void checkOpponent() {
        if (played) {
            return;
        }

        if ("1".equals(hisReady)) { //IF HE PLAYED AND HAD IT WRONG
            played = true;
            his_player.child("ready").setValue("0");
            reset();

            if (win) {
                showMessage(R.drawable.win_box);
            }

        } else if ("2".equals(hisReady)) { //IF HE PLAYED AND HAD IT RIGHT

            reset();

            if (win) {
                showMessage(R.drawable.draw_box);
            } else {
                showMessage(R.drawable.lost_box);
            }

        }
    }

    public void startAndRandoms() {

        // SEE WHO ENTRED FIRST AND GIVE RANDOMS
        my_player.child("side").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String enter_first = dataSnapshot.getValue(String.class);

                if ("0".equals(enter_first)) {

                    randomGenerator();

                    //SEND RANDOMS
                    his_player.child("rana").setValue(a);
                    his_player.child("ranb").setValue(b);
                    his_player.child("ranc").setValue(c);
                    his_player.child("rand").setValue(d);

                    //I ENTRED FIRST
                    his_player.child("side").setValue("1");

                } else {

                    //RETRIEVE RANDOMS
                    my_player.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            a = intChild(dataSnapshot, "rana");
                            b = intChild(dataSnapshot, "ranb");
                            c = intChild(dataSnapshot, "ranc");
                            d = intChild(dataSnapshot, "rand");
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private static int intChild(DataSnapshot snapshot, String key) {
        Integer value = snapshot.child(key).getValue(Integer.class);
        return value == null ? 0 : value;
    }

    public void randomGenerator() {
        Random rand;
        rand = new Random();

        a = rand.nextInt(10);
        b = rand.nextInt(10);
        c = rand.nextInt(10);
        d = rand.nextInt(10);

        while (b == a) {
            b = rand.nextInt(10);
        }

        while (c == a || c == b) {
            c = rand.nextInt(10);
        }

        while (d == a || d == b || d == c) {
            d = rand.nextInt(10);
        }

    }

    public void setConfirmButton() {

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Shared.setX(400), Shared.setY(150));
        confirm = new Button(this);
        ((Activity) this).addContentView(confirm, layoutParams);
        confirm.setBackgroundResource(R.drawable.check_button);

        float a;
        a = 1080 - 400 - 100;
        confirm.setY(Shared.setY(1400));
        confirm.setX(Shared.setX(a));

    }

    public void setTextView() {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Shared.setX(660), Shared.setY(550));
        at1 = new TextView(this);
        //at1.setBackgroundResource(R.drawable.border);
        Shared.setTextSize(at1, 60);
        at1.setTextColor(Color.WHITE);
        at1.setTypeface(Shared.fredoka);
        at1.setMovementMethod(new ScrollingMovementMethod());
        ((Activity) this).addContentView(at1, layoutParams);
        at1.setY(Shared.setY(50));
        at1.setX(Shared.setX(195));
    }

    public void setTimer() {

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Shared.setX(200), Shared.setY(120));
        tv = new TextView(this);
        Shared.setTextSize(tv, 60);
        tv.setTypeface(Shared.fredoka);
        tv.setTextColor(Color.WHITE);
        ((Activity) this).addContentView(tv, layoutParams);

        tv.setY(Shared.setY(1420));
        tv.setX(Shared.setX(310));
        tv.setZ(10);

    }

    public void startT() {
        timer = (new CountDownTimer(left, 1000) {
            @Override
            public void onTick(long l) {
                left = l;
                update();
            }

            @Override
            public void onFinish() {
            }
        }).start();

    }

    public void update() {
        int min = (int) ((left / 1000) / 60);
        int sec = (int) (left / 1000) % 60;
        String time = String.format(Locale.getDefault(), "%02d:%02d", min, sec);
        tv.setText(time);


        if (min <= 0 && sec <= 1) { //IF TIME FINISHED

            my_player.child("ready").setValue("1");
            line++;

            his_player.child("ready").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if ("2".equals(dataSnapshot.getValue(String.class))) { // SEE IF LOST
                        showMessage(R.drawable.lost_box);
                    } else {
                        his_player.child("ready").setValue("0"); //RESET
                        reset();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }

    public void pause() {
        timer.cancel();

    }

    public void reset() {
        timer.cancel();
        left = Time_start;
        startT();
        if (time_finished != null) {
            time_finished.release();
        }
        time_finished = MediaPlayer.create(this, R.raw.time_start_sound);
        if (time_finished != null) {
            time_finished.start();
        }
    }

    public void checkAnswer() {
        String r1 = main_game_class.getEditText().getText().toString();

        if (r1.isEmpty()) {
            showError(R.drawable.error_box);
        } else if (r1.length() != 4) {
            showError(R.drawable.error_box);
        } else {
            int rep = Integer.parseInt(r1);
            int a1, b1, c1, d1, juste = 0, m_p = 0;

            a1 = rep / 1000;
            b1 = (rep % 1000) / 100;
            c1 = (rep / 10) - 100 * a1 - 10 * b1;
            d1 = rep - 10 * (100 * a1 + 10 * b1 + c1);

            if (a1 == b1 || a1 == c1 || a1 == d1 || b1 == c1 || b1 == d1 || c1 == d1) {
                showError(R.drawable.error_box);
            } else if (a1 == a && b1 == b && c1 == c && d1 == d) {
                line++;
                win = true;

                s = "Correct";
                String st1 = " " + line + "--" + r1 + "       " + s + "\n";

                SpannableString ss = new SpannableString(st1);
                ForegroundColorSpan red = new ForegroundColorSpan(Color.GREEN);
                ss.setSpan(red, 14 + String.valueOf(line).length(), 14 + String.valueOf(line).length() + s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ForegroundColorSpan black = new ForegroundColorSpan(Color.parseColor("#F5A623"));
                ss.setSpan(black, 0, 3 + String.valueOf(line).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                at1.append(ss);
                main_game_class.getEditText().setText("");

            } else {
                if (a1 == a) {
                    juste++;
                } else if (a1 == b || a1 == c || a1 == d) {
                    m_p++;
                }

                if (b1 == b) {
                    juste++;
                } else if (b1 == a || b1 == c || b1 == d) {
                    m_p++;
                }

                if (c1 == c) {
                    juste++;
                } else if (c1 == b || c1 == a || c1 == d) {
                    m_p++;
                }

                if (d1 == d) {
                    juste++;
                } else if (d1 == b || d1 == c || d1 == a) {
                    m_p++;
                }

                if (juste != 0 && m_p != 0) {
                    String j = String.valueOf(juste);
                    String m = String.valueOf(m_p);
                    s = j + "J" + m + "M";
                } else if (juste == 0 && m_p == 0) {
                    s = "----";
                } else if (juste == 0) {
                    String m = String.valueOf(m_p);
                    s = m + "M";
                } else {
                    String j = String.valueOf(juste);
                    s = j + "J";
                }

                line++;

                String st1 = " " + line + "--" + r1 + "       " + s + "\n";


                SpannableString ss = new SpannableString(st1);
                ForegroundColorSpan red = new ForegroundColorSpan(Color.RED);
                ForegroundColorSpan black = new ForegroundColorSpan(Color.parseColor("#F5A623"));
                ss.setSpan(red, 14 + String.valueOf(line).length(), 14 + String.valueOf(line).length() + s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ss.setSpan(black, 0, 3 + String.valueOf(line).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                at1.append(ss);
                main_game_class.getEditText().setText("");
            }
        }

        //SCROLL TO THE LAST OF THE TEXTVIEW
        if (at1.getLayout() != null) {
            final int scrollAmount = at1.getLayout().getLineTop(at1.getLineCount()) - at1.getHeight();
            at1.scrollTo(0, Math.max(scrollAmount, 0));
        }
    }

    public void showError(Integer resource) {

        Error myError = Shared.setError(this, this, resource);
        error = true;

        theBoxShown = myError.messageBox;
        theDimShown = myError.dimLayout;

        myError.okButton.setOnClickListener(view -> {
            theBoxShown.setVisibility(View.GONE);
            theDimShown.setVisibility(View.GONE);
            error = false;
        });

    }

    public void showMessage(Integer resource) {
        pause();

        Error myError = Shared.setError(this, this, resource);
        repeatButton(myError.messageBox);

        theBoxShown = myError.messageBox;
        theDimShown = myError.dimLayout;

    }

    public void repeatButton(RelativeLayout message_box) {

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Shared.setX(250), Shared.setY(100));
        replay = new Button(this);
        replay.setBackgroundResource(R.drawable.again_button);
        message_box.addView(replay, layoutParams);

        replay.setX(Shared.setX(225));
        replay.setY(Shared.setY(300 - 100 - 20));

        replay.setOnClickListener(view -> {
            I_repeat = true;
            my_player.child("repeat").setValue("1");

            if (I_repeat && He_repeat) { //SEE IF HE ALREADY WANTS TO REPLAY
                replay();
            }
        });
    }

    public void replay() {

        Intent intent1 = getIntent();
        Intent intent = new Intent(MultiGame.this, Waiting.class);
        intent.putExtra(who_key, intent1.getStringExtra(who_key));
        startActivity(intent);
        finish();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (main_game_class != null && main_game_class.getEditText() != null) {
            main_game_class.getEditText().setText("");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
        if (time_finished != null) {
            time_finished.release();
            time_finished = null;
        }
        if (hisReadyListener != null) {
            his_player.child("ready").removeEventListener(hisReadyListener);
        }
        if (hisRepeatListener != null) {
            his_player.child("repeat").removeEventListener(hisRepeatListener);
        }
    }
}

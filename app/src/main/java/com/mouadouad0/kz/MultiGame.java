package com.mouadouad0.kz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class MultiGame extends AppCompatActivity {

    public final static String who_key = "com.mouad0.hp.snake.who_key";

    FirebaseDatabase database;
    DatabaseReference my_player, his_player;

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

    ConnectivityManager cm;
    MainGame main_game_class;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        main_game_class = new MainGame(this);
        setContentView(main_game_class);

        setConfirmButton();
        setTimer();
        Shared.background(this, this);
        Shared.backButton(this, this, MultiMode.class);
        setTextView();

        database = FirebaseDatabase.getInstance();
        Intent intent = getIntent();

        play_again = new ImageView(this);

        //SEE WHICH PLAYER
        if (intent.getStringExtra(who_key).equals("create")) {

            my_player = database.getReference(MultiMode.name).child("player1");
            his_player = database.getReference(MultiMode.name).child("player2");

        } else {

            his_player = database.getReference(MultiMode.name).child("player1");
            my_player = database.getReference(MultiMode.name).child("player2");
        }

        startAndRandoms();

        cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;

        //ON CONFIRM ANSWER
        confirm.setOnClickListener(view -> {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork == null) {
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

                            his_player.child("ready").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    if (!played) { //IF I PLAYED

                                        if (Objects.equals(dataSnapshot.getValue(String.class), "1")) { //IF HE PLAYED AND HAD IT WRONG
                                            played = true;
                                            his_player.child("ready").setValue("0");
                                            reset();

                                            if (win) {
                                                showMessage(R.drawable.win_box);
                                            }

                                        } else if (Objects.equals(dataSnapshot.getValue(String.class), "2")) { //IF HE PLAYED AND HAD IT RIGHT

                                            reset();

                                            if (win) {
                                                showMessage(R.drawable.draw_box);
                                            } else {
                                                showMessage(R.drawable.lost_box);
                                            }

                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }
                    }

                }

            }
        });

        //SEE IF WANT TO REPEAT
        his_player.child("repeat").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (Objects.equals(dataSnapshot.getValue(String.class), "1")) {//HE WANTS TO REPLAY
                    He_repeat = true;

                    if (I_repeat) { // I ALREADY WANT TO REPLAY
                        replay();
                    } else {

                        RelativeLayout.LayoutParams layoutParams4 = new RelativeLayout.LayoutParams(Shared.setx(700), Shared.sety(180));
                        play_again.setBackgroundResource(R.drawable.play_again_box);
                        addContentView(play_again, layoutParams4);
                        play_again.setX(Shared.setx(190));
                        play_again.setY(Shared.sety(505));
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


    }

    public void startAndRandoms() {

        // SEE WHO ENTRED FIRST AND GIVE RANDOMS
        my_player.child("side").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String enter_first = dataSnapshot.getValue(String.class);

                assert enter_first != null;
                if (enter_first.equals("0")) {

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
                    my_player.child("rana").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            a = dataSnapshot.getValue(Integer.class);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    my_player.child("ranb").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            b = dataSnapshot.getValue(Integer.class);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    my_player.child("ranc").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            c = dataSnapshot.getValue(Integer.class);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    my_player.child("rand").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            d = dataSnapshot.getValue(Integer.class);
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

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Shared.setx(400), Shared.sety(150));
        confirm = new Button(this);
        ((Activity) this).addContentView(confirm, layoutParams);
        confirm.setBackgroundResource(R.drawable.check_button);

        float a;
        a = 1080 - 400 - 100;
        confirm.setY(Shared.sety(1400));
        confirm.setX(Shared.setx(a));

    }

    public void setTextView() {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Shared.setx(660), Shared.sety(550));
        at1 = new TextView(this);
        //at1.setBackgroundResource(R.drawable.border);
        at1.setTextSize(Shared.seth(20));
        at1.setTextColor(Color.WHITE);
        at1.setMovementMethod(new ScrollingMovementMethod());
        ((Activity) this).addContentView(at1, layoutParams);
        at1.setY(Shared.sety(50));
        at1.setX(Shared.setx(195));
    }

    public void setTimer() {

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Shared.setx(200), Shared.sety(120));
        tv = new TextView(this);
        tv.setTextSize(Shared.seth(20));
        tv.setTypeface(Start.fredoka);
        tv.setTextColor(Color.BLACK);
        ((Activity) this).addContentView(tv, layoutParams);

        tv.setY(Shared.sety(1420));
        tv.setX(Shared.setx(310));
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

                    if (Objects.equals(dataSnapshot.getValue(String.class), "2")) { // SEE IF LOST
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
        time_finished = MediaPlayer.create(this, R.raw.time_start_sound);
        time_finished.start();
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
        final int scrollAmount = at1.getLayout().getLineTop(at1.getLineCount()) - at1.getHeight();
        at1.scrollTo(0, Math.max(scrollAmount, 0));
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

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Shared.setx(250), Shared.sety(100));
        replay = new Button(this);
        replay.setBackgroundResource(R.drawable.again_button);
        message_box.addView(replay, layoutParams);

        replay.setX(Shared.setx(225));
        replay.setY(Shared.sety(300 - 100 - 20));

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
        killActivity();

    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MultiGame.this, MultiMode.class);
        startActivity(intent);
        killActivity();
    }

    private void killActivity() {
        timer.cancel();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        main_game_class.getEditText().setText("");
    }
}

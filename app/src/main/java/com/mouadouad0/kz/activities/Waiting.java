package com.mouadouad0.kz.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mouadouad0.kz.R;
import com.mouadouad0.kz.Shared;

import java.util.HashMap;
import java.util.Map;

public class Waiting extends AppCompatActivity {

    public final static String who_key = "com.mouad0.hp.snake.who_key";
    public final static String SHARED_PREFS = "shared_prefs";
    public final static String stars_sharedPREFS = "lobby";

    DatabaseReference lobby;
    ValueEventListener joinedListener;
    Boolean joined = false;
    Button play;
    TextView lobbyName;
    Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (MultiMode.name == null) {
            Shared.navigateBack(this, MultiMode.class);
            return;
        }

        Shared.setUpScreen(this);
        Shared.background(this, this);
        Shared.banner(this, this);

        final Intent a = getIntent();

        lobby = Shared.lobby(MultiMode.name);

        play = new Button(this);
        lobbyName = new TextView(this);

        backButton();

        //SET THE PLAY BUTTON
        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(Shared.setX(300), Shared.setY(150));
        addContentView(play, layoutParams2);
        play.setBackgroundResource(R.drawable.play_off_button);

        play.setY(Shared.setY(400));
        play.setX(Shared.setX(400));

        //SET THE NAME OF THE LOBBY
        RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(Shared.setX(1080), RelativeLayout.LayoutParams.WRAP_CONTENT);
        addContentView(lobbyName, layoutParams3);
        Shared.setTextSize(lobbyName, 72);
        lobbyName.setGravity(Gravity.CENTER_HORIZONTAL);

        String prefix = getString(R.string.name_lobby);
        String st1 = prefix + MultiMode.name;

        SpannableString ss = new SpannableString(st1);
        ForegroundColorSpan labelColor = new ForegroundColorSpan(Color.WHITE);
        ss.setSpan(labelColor, 0, prefix.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ForegroundColorSpan nameColor = new ForegroundColorSpan(Color.parseColor("#F5A623"));
        ss.setSpan(nameColor, prefix.length(), st1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        lobbyName.setText(ss);
        lobbyName.setY(Shared.setY(800));
        lobbyName.setX(0);
        lobbyName.setTypeface(Shared.fredoka);

        //TO SEE IF CREATED OR JOINED
        if (a.getStringExtra(who_key).equals("create")) {


            Map<String, Object> reset = new HashMap<>();
            reset.put("owner", Shared.uid());
            for (String player : new String[]{"player1", "player2"}) {
                reset.put(player + "/ready", "0");
                reset.put(player + "/side", "0");
                reset.put(player + "/repeat", "0");
            }

            lobby.updateChildren(reset, (databaseError, databaseReference) -> {
                if (databaseError != null) {
                    Shared.navigateBack(this, MultiMode.class);
                } else if (!isFinishing()) {
                    listenForGuest();
                }
            });


        } else {

            lobby.child("guest").setValue(Shared.uid(), (databaseError, databaseReference) -> {
                if (databaseError != null) {
                    Shared.navigateBack(this, MultiMode.class);
                }
            });
            play.setBackgroundResource(R.drawable.play_on_button);

        }

        play.setOnClickListener(view -> {

            if (!Shared.isOnline(this)) {

                Shared.internetError(this, this);
            } else {
                if (a.getStringExtra(who_key).equals("create")) {

                    if (joined) {
                        Intent intent = new Intent(Waiting.this, MultiGame.class);
                        intent.putExtra(who_key, "create");
                        startActivity(intent);
                        finish();
                    }

                } else {
                    Intent intent = new Intent(Waiting.this, MultiGame.class);
                    intent.putExtra(who_key, "join");
                    startActivity(intent);
                    finish();

                }
            }
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                leave();
            }
        });

    }

    private void listenForGuest() {
        joinedListener = lobby.child("guest").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    play.setBackgroundResource(R.drawable.play_on_button);
                    joined = true;

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void backButton() {

        back = new Button(this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Shared.setX(100), Shared.setY(100));
        back.setBackgroundResource(R.drawable.back_button);
        addContentView(back, layoutParams);
        back.setY(Shared.setY(50));
        back.setX(Shared.setX(50));
        back.setZ(30);

        back.setOnClickListener(view -> leave());

    }

    private void leave() {

        //DELETE THE LOBBY IF AM CREATOR
        if (getIntent().getStringExtra(who_key).equals("create")) {

            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
            String last_lobby = sharedPreferences.getString(stars_sharedPREFS, "");
            if (!last_lobby.isEmpty()) {

                Shared.lobby(last_lobby).removeValue();
            }
        }
        Shared.navigateBack(this, MultiMode.class);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (joinedListener != null) {
            lobby.child("guest").removeEventListener(joinedListener);
        }
    }

}

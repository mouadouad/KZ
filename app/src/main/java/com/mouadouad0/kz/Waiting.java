package com.mouadouad0.kz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Waiting extends AppCompatActivity {

    public final static String who_key = "com.mouad0.hp.snake.who_key";
    public final static String SHARED_PREFS = "shared_prefs";
    public final static String stars_sharedPREFS = "lobby";

    FirebaseDatabase database;
    DatabaseReference lobby;
    Boolean joined = false;
    Button play;
    TextView lobbyName;
    Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Shared.background(this, this);
        Shared.banner(this, this);
        
        final Intent a = getIntent();

        database = FirebaseDatabase.getInstance();
        lobby = database.getReference(MultiMode.name);

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
        RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(Shared.setX(600), RelativeLayout.LayoutParams.WRAP_CONTENT);
        addContentView(lobbyName, layoutParams3);
        lobbyName.setTextSize(Shared.setX(24));

        String st1 = getString(R.string.name_lobby) + MultiMode.name;

        SpannableString ss = new SpannableString(st1);
        ForegroundColorSpan red = new ForegroundColorSpan(Color.BLACK);
        ss.setSpan(red, 0, 15, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ForegroundColorSpan black = new ForegroundColorSpan(Color.parseColor("#104AA8"));
        ss.setSpan(black, 15, 15 + MultiMode.name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        lobbyName.setText(ss);
        lobbyName.setY(Shared.setY(800));
        lobbyName.setX(Shared.setX(400));
        lobbyName.setTypeface(Start.fredoka);

        //INTERNET CHECK
        ConnectivityManager cm = (ConnectivityManager)
                this.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        final NetworkInfo activeNetwork = cm.getActiveNetworkInfo();


        //TO SEE IF CREATED OR JOINED
        if (a.getStringExtra(who_key).equals("create")) {


            lobby.child("player1").child("ready").setValue("0");
            lobby.child("player1").child("side").setValue("0");
            lobby.child("player1").child("repeat").setValue("0");
            lobby.child("player2").child("ready").setValue("0");
            lobby.child("player2").child("side").setValue("0");
            lobby.child("player2").child("repeat").setValue("0");


            lobby.child("joined").addValueEventListener(new ValueEventListener() {
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


        } else {

            lobby.child("joined").setValue(1);
            play.setBackgroundResource(R.drawable.play_on_button);

        }

        play.setOnClickListener(view -> {

            if (activeNetwork == null) {

                Shared.internetError(this, this);
            } else {
                if (a.getStringExtra(who_key).equals("create")) {

                    if (joined) {
                        Intent intent = new Intent(Waiting.this, MultiGame.class);
                        intent.putExtra(who_key, "create");
                        startActivity(intent);
                    }

                } else {
                    Intent intent = new Intent(Waiting.this, MultiGame.class);
                    intent.putExtra(who_key, "join");
                    startActivity(intent);

                }
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

        back.setOnClickListener(view -> {

            //DELETE THE LOBBY IF AM CREATOR
            final Intent a = getIntent();
            if (a.getStringExtra(who_key).equals("create")) {

                SharedPreferences sharedPreferences;
                sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                String last_lobby = sharedPreferences.getString(stars_sharedPREFS, "");
                assert last_lobby != null;
                if (!last_lobby.equals("")) {

                    database.getReference(last_lobby).removeValue();
                }
            }
            Intent intent = new Intent(Waiting.this, MultiMode.class);
            startActivity(intent);
        });

    }

    @Override
    public void onBackPressed() {

        //DELETE THE LOBBY IF AM CREATOR
        final Intent a = getIntent();
        if (a.getStringExtra(who_key).equals("create")) {

            SharedPreferences sharedPreferences;
            sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
            String last_lobby = sharedPreferences.getString(stars_sharedPREFS, "");
            assert last_lobby != null;
            if (!last_lobby.equals("")) {

                database.getReference(last_lobby).removeValue();
            }
        }
        Intent intent = new Intent(Waiting.this, MultiMode.class);
        startActivity(intent);

    }

}

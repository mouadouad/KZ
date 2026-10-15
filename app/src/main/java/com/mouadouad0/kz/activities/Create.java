package com.mouadouad0.kz.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mouadouad0.kz.R;
import com.mouadouad0.kz.Shared;

import java.util.Random;

public class Create extends AppCompatActivity {

    public final static String who_key = "com.mouad0.hp.snake.who_key";
    public final static String SHARED_PREFS = "shared_prefs";
    public final static String stars_sharedPREFS = "lobby";

    EditText lobbyName;
    Button confirm, generate;
    DatabaseReference lobby;
    String generated = "";
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Shared.fitSystemBars(this);

        Shared.background(this, this);
        Shared.backButton(this, this, MultiMode.class);
        Shared.banner(this, this);
        buttonsEditText();

        //DELETE LAST LOBBY
        sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String last_lobby = sharedPreferences.getString(stars_sharedPREFS, "");
        assert last_lobby != null;
        if (!last_lobby.equals("")) {

            Shared.lobby(last_lobby).removeValue();
        }

        generate.setOnClickListener(view -> {

            if (!Shared.isOnline(this)) {

                Shared.internetError(this, this);
            } else {
                generate();
            }

        });

        confirm.setOnClickListener(view -> {

            if (!Shared.isOnline(this)) {
                Shared.internetError(this, this);
            } else if (!Shared.isValidLobbyName(lobbyName.getText().toString())) {


                lobbyName.setError("Please choose another lobby");


            } else {

                MultiMode.name = lobbyName.getText().toString();

                lobby = Shared.lobby(MultiMode.name);
                lobby.child("owner").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (!dataSnapshot.exists()) {

                            save();
                            Intent intent = new Intent(Create.this, Waiting.class);
                            intent.putExtra(who_key, "create");
                            startActivity(intent);
                        } else {
                            lobbyName.setError("Lobby already exists");
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

    }

    private void buttonsEditText() {
        //SET BUTTONS AND EDIT TEXT
        lobbyName = new EditText(this);
        confirm = new Button(this);
        generate = new Button(this);

        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(Shared.setX(300), Shared.setY(150));
        addContentView(lobbyName, layoutParams2);

        lobbyName.setY(Shared.setY(200));
        lobbyName.setX(Shared.setX(400));
        lobbyName.setTextColor(Color.WHITE);
        lobbyName.setTypeface(Start.fredoka);
        //int color= Color.parseColor("#171433");

        RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(Shared.setX(300), Shared.setY(150));
        addContentView(confirm, layoutParams3);
        confirm.setBackgroundResource(R.drawable.create_button);

        confirm.setY(Shared.setY(500));
        confirm.setX(Shared.setX(400));

        RelativeLayout.LayoutParams layoutParams4 = new RelativeLayout.LayoutParams(Shared.setX(300), Shared.setY(150));
        addContentView(generate, layoutParams4);
        generate.setBackgroundResource(R.drawable.generate_button);

        generate.setY(Shared.setY(900));
        generate.setX(Shared.setX(400));
    }

    public void save() {

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(stars_sharedPREFS, MultiMode.name);


        editor.apply();

    }

    public void generate() {

        String alphabets = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

        generated = "";

        //RANDOM NAME
        for (int i = 0; i < 6; i++) {

            final Random rand = new Random();
            final int position = rand.nextInt(36);

            generated = String.format("%s%s", generated, alphabets.charAt(position));

        }


        final String candidate = generated;

        lobby = Shared.lobby(candidate);
        lobby.child("owner").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    generate();
                } else {
                    MultiMode.name = candidate;
                    save();

                    Intent intent = new Intent(Create.this, Waiting.class);
                    intent.putExtra(who_key, "create");
                    startActivity(intent);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


}

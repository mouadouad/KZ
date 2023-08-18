package com.mouadouad0.kz;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Join extends AppCompatActivity {

    public final static String who_key = "com.mouad0.hp.snake.who_key";

    EditText lobbyName;
    Button confirm;

    FirebaseDatabase database;
    DatabaseReference lobby;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Shared.background(this, this);
        Shared.backButton(this, this, MultiMode.class);
        Shared.banner(this, this);

        lobbyName =new EditText(this);
        confirm=new Button(this);
        database = FirebaseDatabase.getInstance();

        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(Shared.setX(300),Shared.setY(150));
        addContentView(lobbyName,layoutParams2);

        lobbyName.setY(Shared.setY(200));
        lobbyName.setX(Shared.setX(400));
        lobbyName.setTextColor(Color.WHITE);
        lobbyName.setTypeface(Start.fredoka);

        RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(Shared.setX(300),Shared.setY(150));
        addContentView(confirm,layoutParams3);
        confirm.setBackgroundResource(R.drawable.join_button);

        confirm.setY(Shared.setY(500));
        confirm.setX(Shared.setX(400));

        //INTERNET CHECK
        ConnectivityManager cm = (ConnectivityManager)
                this.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        final NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        confirm.setOnClickListener(view -> {
            if (activeNetwork==null){

                Shared.internetError(this, this);

            }else if (lobbyName.getText().toString().isEmpty()){
                lobbyName.setError("Lobby doesn't exist");
            }else {

                MultiMode.name = lobbyName.getText().toString();

                lobby = database.getReference(MultiMode.name);

                lobby.child("player1").child("ready").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {

                            lobby.child("joined").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    if (!dataSnapshot.exists()) {

                                        Intent intent = new Intent(Join.this, Waiting.class);
                                        intent.putExtra(who_key, "join");
                                        startActivity(intent);
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        } else {
                            lobbyName.setError("Lobby doesn't exist");
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }

}

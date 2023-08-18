package com.mouadouad0.kz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Join extends AppCompatActivity {

    public final static String who_key = "com.mouad0.hp.snake.who_key";

    EditText name_of_lobby;
    Button confirm;

    FirebaseDatabase database;
    DatabaseReference lobby;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Shared.background(this, this);
        Shared.backButton(this, this, MultiMode.class);
        banner();

        name_of_lobby=new EditText(this);
        confirm=new Button(this);
        database = FirebaseDatabase.getInstance();

        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(Shared.setx(300),Shared.sety(150));
        addContentView(name_of_lobby,layoutParams2);

        name_of_lobby.setY(Shared.sety(200));
        name_of_lobby.setX(Shared.setx(400));
        name_of_lobby.setTextColor(Color.WHITE);
        name_of_lobby.setTypeface(Start.fredoka);

        RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(Shared.setx(300),Shared.sety(150));
        addContentView(confirm,layoutParams3);
        confirm.setBackgroundResource(R.drawable.join_button);

        confirm.setY(Shared.sety(500));
        confirm.setX(Shared.setx(400));

        //INTERNET CHECK
        ConnectivityManager cm = (ConnectivityManager)
                this.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        final NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        confirm.setOnClickListener(view -> {
            if (activeNetwork==null){

                Shared.internetError(this, this);

            }else if (name_of_lobby.getText().toString().isEmpty()){
                name_of_lobby.setError("Lobby doesn't exist");
            }else {

                MultiMode.name = name_of_lobby.getText().toString();

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
                            name_of_lobby.setError("Lobby doesn't exist");
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
    }

    public void banner(){
        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId("ca-app-pub-3922358669029120/2831354657");

        RelativeLayout layout=new RelativeLayout(this);
        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams((int) Start.width, (int) Start.height-getStatusBarHeight());
        addContentView(layout,layoutParams1);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layout.addView(adView,layoutParams);

        MobileAds.initialize(this,"ca-app-pub-3922358669029120~3985187056");
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

}

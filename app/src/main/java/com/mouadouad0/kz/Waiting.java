package com.mouadouad0.kz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
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
    TextView name_lobby;
    Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Shared.background(this, this);
        banner();
        
        final Intent a = getIntent();

        database = FirebaseDatabase.getInstance();
        lobby = database.getReference(MultiMode.name);

        play = new Button(this);
        name_lobby = new TextView(this);

        backButton();

        //SET THE PLAY BUTTON
        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(Shared.setx(300), Shared.sety(150));
        addContentView(play, layoutParams2);
        play.setBackgroundResource(R.drawable.play_off_button);

        play.setY(Shared.sety(400));
        play.setX(Shared.setx(400));

        //SET THE NAME OF THE LOBBY
        RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(Shared.setx(600), RelativeLayout.LayoutParams.WRAP_CONTENT);
        addContentView(name_lobby, layoutParams3);
        name_lobby.setTextSize(Shared.setx(24));

        String st1 = getString(R.string.name_lobby) + MultiMode.name;

        SpannableString ss = new SpannableString(st1);
        ForegroundColorSpan red = new ForegroundColorSpan(Color.BLACK);
        ss.setSpan(red, 0, 15, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ForegroundColorSpan black = new ForegroundColorSpan(Color.parseColor("#104AA8"));
        ss.setSpan(black, 15, 15 + MultiMode.name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        name_lobby.setText(ss);
        name_lobby.setY(Shared.sety(800));
        name_lobby.setX(Shared.setx(400));
        name_lobby.setTypeface(Start.fredoka);

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

                internetError();
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
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Shared.setx(100), Shared.sety(100));
        back.setBackgroundResource(R.drawable.back_button);
        addContentView(back, layoutParams);
        back.setY(Shared.sety(50));
        back.setX(Shared.setx(50));
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

    public void internetError() {

        final RelativeLayout message_box;
        Button ok_button;

        final FrameLayout dim_layout;

        //MAKE THE SCREEN DIM
        Resources res = getResources();
        Drawable shape = ResourcesCompat.getDrawable(res, R.drawable.dim, null);
        dim_layout = new FrameLayout(this);
        dim_layout.setForeground(shape);

        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams((int) (Start.width), (int) (Start.height));
        addContentView(dim_layout, layoutParams1);
        dim_layout.getForeground().setAlpha(200);
        dim_layout.setZ(20);


        //SETTING THE ERROR MESSAGE

        message_box = new RelativeLayout(this);
        ok_button = new Button(this);


        //BUTTON
        RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(Shared.setx(200), Shared.sety(100));
        message_box.addView(ok_button, layoutParams3);
        ok_button.setBackgroundResource(R.drawable.okay_button);
        ok_button.setX(Shared.setx(250));
        ok_button.setY(Shared.sety(300 - 100 - 20));


        //BOX
        message_box.setBackgroundResource(R.drawable.internet_box);
        RelativeLayout.LayoutParams layoutParams4 = new RelativeLayout.LayoutParams(Shared.setx(700), Shared.sety(300));
        addContentView(message_box, layoutParams4);
        message_box.setX(Shared.setx((Start.width - 700) / 2));
        message_box.setY(Shared.sety((Start.height - 300) / 2));
        message_box.setZ(30);


        //ONCLICK BUTTON
        ok_button.setOnClickListener(view -> {
            message_box.setVisibility(View.GONE);
            dim_layout.setVisibility(View.GONE);

        });

    }

    public void banner() {
        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId("ca-app-pub-3922358669029120/2831354657");

        RelativeLayout layout = new RelativeLayout(this);
        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams((int) Start.width, (int) Start.height - getStatusBarHeight());
        addContentView(layout, layoutParams1);


        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layout.addView(adView, layoutParams);

        MobileAds.initialize(this, "ca-app-pub-3922358669029120~3985187056");
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

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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
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

import java.util.Random;

public class Create extends AppCompatActivity {

    public final static String who_key = "com.mouad0.hp.snake.who_key";
    public final static String SHARED_PREFS = "shared_prefs";
    public final static String stars_sharedPREFS = "lobby";

    EditText name_of_lobby;
    Button confirm, generate;
    FirebaseDatabase database;
    DatabaseReference lobby;
    String generated = "";
    Button back;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Shared.background(this, this);
        backButton();
        banner();
        buttons_editText();

        //INTERNET CHECK
        ConnectivityManager cm = (ConnectivityManager)
                this.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        final NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        //DELETE LAST LOBBY
        sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String last_lobby = sharedPreferences.getString(stars_sharedPREFS, "");
        assert last_lobby != null;
        if (!last_lobby.equals("")) {

            database.getReference(last_lobby).removeValue();
        }

        generate.setOnClickListener(view -> {

            if (activeNetwork == null) {

                internetError();
            } else {
                generate();
                save();

                Intent intent = new Intent(Create.this, Waiting.class);
                intent.putExtra(who_key, "create");
                startActivity(intent);
            }

        });

        confirm.setOnClickListener(view -> {

            if (activeNetwork == null) {

                internetError();

            } else if (name_of_lobby.getText().toString().contains(" ") || name_of_lobby.getText().toString().isEmpty() || name_of_lobby.getText().toString().length() > 50) {


                name_of_lobby.setError("Please choose another lobby");


            } else {

                MultiMode.name = name_of_lobby.getText().toString();

                lobby = database.getReference(MultiMode.name);
                lobby.child("player1").child("ready").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (!dataSnapshot.exists()) {

                            save();
                            Intent intent = new Intent(Create.this, Waiting.class);
                            intent.putExtra(who_key, "create");
                            startActivity(intent);
                        } else {
                            name_of_lobby.setError("Lobby already exists");
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });


    }

    private void buttons_editText() {
        //SET BUTTONS AND EDIT TEXT
        name_of_lobby = new EditText(this);
        confirm = new Button(this);
        generate = new Button(this);
        database = FirebaseDatabase.getInstance();

        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(Shared.setx(300), Shared.sety(150));
        addContentView(name_of_lobby, layoutParams2);

        name_of_lobby.setY(Shared.sety(200));
        name_of_lobby.setX(Shared.setx(400));
        name_of_lobby.setTextColor(Color.WHITE);
        name_of_lobby.setTypeface(Start.fredoka);
        //int color= Color.parseColor("#171433");

        RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(Shared.setx(300), Shared.sety(150));
        addContentView(confirm, layoutParams3);
        confirm.setBackgroundResource(R.drawable.create_button);

        confirm.setY(Shared.sety(500));
        confirm.setX(Shared.setx(400));

        RelativeLayout.LayoutParams layoutParams4 = new RelativeLayout.LayoutParams(Shared.setx(300), Shared.sety(150));
        addContentView(generate, layoutParams4);
        generate.setBackgroundResource(R.drawable.generate_button);

        generate.setY(Shared.sety(900));
        generate.setX(Shared.setx(400));
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


        MultiMode.name = generated;

        lobby = database.getReference(MultiMode.name);
        lobby.child("player1").child("ready").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (!dataSnapshot.exists()) {

                    generate();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
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

    public void backButton() {

        back = new Button(this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Shared.setx(100), Shared.sety(100));
        back.setBackgroundResource(R.drawable.back_button);
        addContentView(back, layoutParams);
        back.setY(Shared.sety(50));
        back.setX(Shared.setx(50));
        back.setZ(30);

        back.setOnClickListener(view -> {

            Intent intent = new Intent(Create.this, MultiMode.class);
            startActivity(intent);
        });

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
}

package com.mouadouad0.kz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import android.content.Context;
import android.content.Intent;
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

public class Join extends AppCompatActivity {

    public final static String who_key = "com.mouad0.hp.snake.who_key";

    EditText name_of_lobby;
    Button confirm;
    Button back;

    FirebaseDatabase database;
    DatabaseReference lobby;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        background();
        back_button();
        banner();


        name_of_lobby=new EditText(this);
        confirm=new Button(this);
        database = FirebaseDatabase.getInstance();


        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(setx(300),sety(150));
        addContentView(name_of_lobby,layoutParams2);

        name_of_lobby.setY(sety(200));
        name_of_lobby.setX(setx(400));
        name_of_lobby.setTextColor(Color.WHITE);
        name_of_lobby.setTypeface(Start.fredoka);



        RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(setx(300),sety(150));
        addContentView(confirm,layoutParams3);
        confirm.setBackgroundResource(R.drawable.join_button);

        confirm.setY(sety(500));
        confirm.setX(setx(400));

        //INTERNET CHECK
        ConnectivityManager cm = (ConnectivityManager)
                this.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        final NetworkInfo activeNetwork = cm.getActiveNetworkInfo();


        confirm.setOnClickListener(view -> {
            if (activeNetwork==null){

                internet_error();

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

    private void background() {
        //BACKGROUND
        RelativeLayout background=new RelativeLayout(this);
        RelativeLayout.LayoutParams backparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        int back_color= Color.parseColor("#2F2958");
        background.setBackgroundColor(back_color);
        addContentView(background,backparams);
    }

    public int setx(float x){
        int i;

        i= (int) ((x*Start.width)/1080);

        return i;
    }

    public int sety(float x){
        int i;

        i= (int) ((x*Start.height)/1770);

        return i;
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

    public void back_button(){

        back=new Button(this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(setx(100),sety(100));
        back.setBackgroundResource(R.drawable.back_button);
        addContentView(back,layoutParams);
        back.setY(sety(50));
        back.setX(setx(50));
        back.setZ(30);

        back.setOnClickListener(view -> {

            Intent intent=new Intent(Join.this, MultiMode.class);
            startActivity(intent);
        });

    }

    public void internet_error(){

        final RelativeLayout message_box;
        Button ok_button;

        final FrameLayout dim_layout;

        //MAKE THE SCREEN DIM
        Resources res = getResources();
        Drawable shape = ResourcesCompat.getDrawable(res, R.drawable.dim, null);
        dim_layout =new FrameLayout(this);
        dim_layout.setForeground(shape);

        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams((int)(Start.width), (int) (Start.height));
        addContentView(dim_layout,layoutParams1);
        dim_layout.getForeground().setAlpha(200);
        dim_layout.setZ(20);


        //SETTING THE ERROR MESSAGE

        message_box=new RelativeLayout(this);
        ok_button=new Button(this);


        //BUTTON
        RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(setx(200),sety(100));
        message_box.addView(ok_button,layoutParams3);
        ok_button.setBackgroundResource(R.drawable.okay_button);
        ok_button.setX(setx(250));
        ok_button.setY(sety(300-100-20));


        //BOX
        message_box.setBackgroundResource(R.drawable.internet_box);
        RelativeLayout.LayoutParams layoutParams4= new RelativeLayout.LayoutParams(setx(700),sety(300) );
        addContentView(message_box,layoutParams4);
        message_box.setX(setx((Start.width-700)/2));
        message_box.setY(sety((Start.height-300)/2));
        message_box.setZ(30);



        //ONCLICK BUTTON
        ok_button.setOnClickListener(view -> {
            message_box.setVisibility(View.GONE);
            dim_layout.setVisibility(View.GONE);

        });

    }
}

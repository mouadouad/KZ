package com.mouadouad0.kz.activities;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mouadouad0.kz.Digits;
import com.mouadouad0.kz.Error;
import com.mouadouad0.kz.MainGame;
import com.mouadouad0.kz.R;
import com.mouadouad0.kz.Shared;

import java.util.Random;

public class Solo extends AppCompatActivity {

    final static int LINE_LENGTH = 14;
    int lineCounter;
    int correct = 0, misplaced = 0;
    String entry, correction;
    Digits correctAnswer;
    Button confirm, replay;
    Boolean error = false;
    TextView gameText;
    MainGame mainGameClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainGameClass = new MainGame(this);
        setContentView(mainGameClass);

        randomGenerator();
        setConfirmButtons();
        Shared.background(this, this);
        Shared.backButton(this, this, Start.class);
        setGameTextParams();

        confirm.setOnClickListener(view -> {
            if (!error) {
                checkEntry();
            }
        });

        replay.setOnClickListener(view -> {
            if (!error) {
                lineCounter = 0;
                randomGenerator();
                gameText.setText("");
                mainGameClass.getEditText().setText("");
            }
        });

    }

    public void randomGenerator() {
        final Random rand = new Random();
        int a, b, c, d;

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

        correctAnswer = new Digits(a, b, c, d);

        System.out.println(a);
        System.out.println(b);
        System.out.println(c);
        System.out.println(d);
    }

    public void setConfirmButtons() {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Shared.setX(400), Shared.setY(150));

        confirm = new Button(this);
        replay = new Button(this);

        ((Activity) this).addContentView(confirm, layoutParams);
        ((Activity) this).addContentView(replay, layoutParams);

        replay.setBackgroundResource(R.drawable.again_button);
        confirm.setBackgroundResource(R.drawable.check_button);

        float a;
        a = 1080 - 400 - 100;

        confirm.setY(Shared.setY(1400));
        confirm.setX(Shared.setX(a));

        replay.setY(Shared.setY(1400));
        replay.setX(Shared.setX(100));
    }

    public void setGameTextParams() {
        final float a, b;
        a = 195;  //(1080-200*3-30*3)/2;
        b = 200 * 3 + 30 * 3;

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Shared.setX(b), Shared.setY(550));
        gameText = new TextView(this);
        // at1.setBackgroundResource(R.drawable.border);
        gameText.setTextSize(Shared.seth(18));
        gameText.setTextColor(Color.WHITE);
        gameText.setTypeface(Start.fredoka);
        gameText.setMovementMethod(new ScrollingMovementMethod());
        ((Activity) this).addContentView(gameText, layoutParams);
        gameText.setY(Shared.setY(50));
        gameText.setX(Shared.setX(a));
    }

    public void setGameLine(int color) {
        final String gameLine = " " + lineCounter + "--" + entry + "       " + correction + "\n";
        final SpannableString ss = new SpannableString(gameLine);
        ForegroundColorSpan correctionColor = new ForegroundColorSpan(color);

        ss.setSpan(correctionColor, LINE_LENGTH + String.valueOf(lineCounter).length(), LINE_LENGTH +
                String.valueOf(lineCounter).length() + correction.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ForegroundColorSpan black = new ForegroundColorSpan(Color.parseColor("#F5A623"));//104AA8
        ss.setSpan(black, 0, 3 + String.valueOf(lineCounter).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        gameText.append(ss);
        mainGameClass.getEditText().setText("");
    }

    private void answerIsCorrect() {
        lineCounter++;
        correction = "Correct";
        setGameLine(Color.GREEN);
    }

    private void checkDigit(int digit, int digitIndex) {
        if (digit == correctAnswer.getDigit(digitIndex)) {
            correct++;
        } else {
            for (int i = 1; i < 4; i++) {
                if (digit == correctAnswer.getDigit((digitIndex + i) % 4)) {
                    misplaced++;
                }
            }
        }
    }

    private void setCorrectionString() {
        if (correct != 0 && misplaced != 0) {
            String j = String.valueOf(correct);
            String m = String.valueOf(misplaced);
            correction = j + "J" + m + "M";
        } else if (correct == 0 && misplaced == 0) {
            correction = "----";
        } else if (correct == 0) {
            String m = String.valueOf(misplaced);
            correction = m + "M";
        } else {
            String j = String.valueOf(correct);
            correction = j + "J";
        }
    }

    private void correctAnswer(Digits answer) {
        for(int i = 0; i < 4; i++){
            checkDigit(answer.getDigit(i), i);
        }
        setCorrectionString();
        lineCounter++;

        setGameLine(Color.RED);
    }

    private void checkAnswer() {
        final int entryInteger = Integer.parseInt(entry);
        int a1, b1, c1, d1;

        a1 = entryInteger / 1000;
        b1 = (entryInteger % 1000) / 100;
        c1 = (entryInteger / 10) - 100 * a1 - 10 * b1;
        d1 = entryInteger - 10 * (100 * a1 + 10 * b1 + c1);

        final Digits answer = new Digits(a1, b1, c1, d1);

        if (answer.isNotValid()) {
            setError();
        } else if (answer.equals(correctAnswer)) {
            answerIsCorrect();
        } else {
            correctAnswer(answer);
        }
    }

    private void checkEntry() {
        entry = mainGameClass.getEditText().getText().toString();

        if (entry.isEmpty()) {
            setError();
        } else if (entry.length() != 4) {
            setError();
        } else {
           checkAnswer();
            correct = 0; misplaced = 0;
        }
        //SCROLL TO THE LAST OF THE TEXTVIEW
        final int scrollAmount = gameText.getLayout().getLineTop(gameText.getLineCount()) - gameText.getHeight();
        gameText.scrollTo(0, Math.max(scrollAmount, 0));
    }

    public void setError() {
        Error myError = Shared.setError(this, this, R.drawable.error_box);
        error = true;
        myError.okButton.setOnClickListener(view -> {
            myError.messageBox.setVisibility(View.GONE);
            myError.dimLayout.setVisibility(View.GONE);
            error = false;
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mainGameClass.getEditText().setText("");
    }
}

package com.mouadouad0.kz;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import java.util.Random;

public class MainGame {
    final static int LINE_LENGTH = 14;
    private Digits correctAnswer;
    private int correct, misplaced, lineCounter;
    private Boolean error = false;
    private String entry, correction;
    final private MainGameView mainGameView;

    public MainGame(MainGameView mainGameView) {
        this.mainGameView = mainGameView;
        randomGenerator();
    }

    public void reset() {
        lineCounter = 0;
        mainGameView.getGameText().setText("");
        mainGameView.getEditText().setText("");
        randomGenerator();
    }

    private void randomGenerator() {
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

    public void checkEntry() {
        entry = mainGameView.getEditText().getText().toString();

        if (entry.isEmpty()) {
            setError();
        } else if (entry.length() != 4) {
            setError();
        } else {
            checkAnswer();
            correct = 0; misplaced = 0;
        }
        //SCROLL TO THE LAST OF THE TEXTVIEW
        final int scrollAmount = mainGameView.getGameText().getLayout().getLineTop(mainGameView.getGameText().getLineCount()) - mainGameView.getGameText().getHeight();
        mainGameView.getGameText().scrollTo(0, Math.max(scrollAmount, 0));
    }

    private void checkAnswer() {
        final Digits answer = new Digits(entry);

        if (answer.isNotValid()) {
            setError();
        } else if (answer.equals(correctAnswer)) {
            answerIsCorrect();
        } else {
            correctAnswer(answer);
        }
    }

    private void answerIsCorrect() {
        lineCounter++;
        correction = "Correct";
        setGameLine(Color.GREEN);
    }

    private void correctAnswer(Digits answer) {
        for(int i = 0; i < 4; i++){
            checkDigit(answer.getDigit(i), i);
        }
        setCorrectionString();
        lineCounter++;

        setGameLine(Color.RED);
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

    private void setGameLine(int color) {
        final String gameLine = " " + lineCounter + "--" + entry + "       " + correction + "\n";

        mainGameView.getGameText().append(colorLine(gameLine, color));
        mainGameView.getEditText().setText("");
    }

    private SpannableString colorLine(String gameLine, int color){
        final SpannableString ss = new SpannableString(gameLine);
        ForegroundColorSpan correctionColor = new ForegroundColorSpan(color);

        ss.setSpan(correctionColor, LINE_LENGTH + String.valueOf(lineCounter).length(), LINE_LENGTH +
                String.valueOf(lineCounter).length() + correction.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ForegroundColorSpan black = new ForegroundColorSpan(Color.parseColor("#F5A623"));//104AA8
        ss.setSpan(black, 0, 3 + String.valueOf(lineCounter).length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return ss;
    }

    private void setError() {
        Error myError = mainGameView.setError();
        error = true;
        myError.okButton.setOnClickListener(view -> {
            myError.messageBox.setVisibility(View.GONE);
            myError.dimLayout.setVisibility(View.GONE);
            error = false;
        });
    }

    public Boolean isNotError(){
        return !error;
    }

}

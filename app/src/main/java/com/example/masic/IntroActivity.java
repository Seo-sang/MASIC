package com.example.masic;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

public class IntroActivity extends AppCompatActivity {
    TextView Masic;
    TextView makeMusic;
    Typeface typeface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro); //
        Masic = findViewById(R.id.masic);
        typeface = Typeface.createFromAsset(getAssets(),"hsi.ttf");
        makeMusic = findViewById(R.id.makeMusic);
        Masic.setTypeface(typeface);
        //makeMusic.setTypeface(typeface);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), Login.class); //시간이 초과된 이후 Login 화면 띄움
                startActivity(intent);
                finish();
            }
        }, 3000); //3초 뒤 메인 화면으로 넘어감
    }
    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
package com.example.masic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class Login extends AppCompatActivity implements View.OnClickListener {
    EditText mEmailText, mPasswordText;
    Button mLoginBtn, mSignupBtn;
    private FirebaseAuth firebaseAuth;
    TextView Masic;
    Typeface typeface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Masic = findViewById(R.id.Masic);
        typeface = Typeface.createFromAsset(getAssets(),"hsi.ttf");
        Masic.setTypeface(typeface);
        mEmailText = findViewById(R.id.userEmail);
        mPasswordText = findViewById(R.id.password);
        mSignupBtn = findViewById(R.id.signupBtn);
        mLoginBtn = findViewById(R.id.loginBtn);
        firebaseAuth = FirebaseAuth.getInstance();

        mSignupBtn.setOnClickListener(this);
        mLoginBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v==mSignupBtn){
            Intent intent = new Intent(Login.this, JoinActivity.class);
            startActivity(intent);
        }
        else if(v==mLoginBtn){
            final String email = mEmailText.getText().toString().trim();
            final String password = mPasswordText.getText().toString().trim();

            final ProgressDialog mDialog = new ProgressDialog(Login.this);
            mDialog.setMessage("로그인중입니다...");
            mDialog.show();

            firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        mDialog.dismiss();
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        String uid = user.getUid();
                        Intent intent = new Intent(Login.this, MainActivity.class);
                        intent.putExtra("email", email);
                        intent.putExtra("password", password);
                        intent.putExtra("uid", uid); //uid 값으로 사용자 식별함
                        startActivity(intent);
                    }
                    else {
                        mDialog.dismiss();
                        Toast.makeText(Login.this, "이메일 또는 비밀번호가 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public static class PlayList extends AppCompatActivity {

        Button backButton; //뒤로가기 버튼
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_play_list);

            backButton = findViewById(R.id.backButton);

            backButton.setOnClickListener(new View.OnClickListener(){ //뒤로가기 버튼시 플레이화면으로 되돌아감
                @Override
                public void onClick(View view) {
                    finish();
                }
            });

            RecyclerView view = (RecyclerView) findViewById(R.id.playlist_recyclerview);
            view.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            view.setAdapter(new PlayListRecyclerViewAdapter());
        }
    }
}
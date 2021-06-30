package com.example.masic;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JoinActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private static final String TAG = "JoinActivity";
    EditText mEmailText, mPasswordText, mPasswordcheckText, mName, mNickname;
    Button mjoinBtn, mEmailCheckBtn, mNicknameCheckBtn;
    RadioButton manBtn, womanBtn;
    DatePicker mBirthdate;
    int duplicationEmail, duplicationNickname, emailCheck, nicknameCheck;
    String verifiedEmail, verifiedNickname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        firebaseAuth = FirebaseAuth.getInstance();

        mEmailText = findViewById(R.id.email);
        mPasswordText = findViewById(R.id.pw);
        mPasswordcheckText = findViewById(R.id.pwcheck);
        mjoinBtn = findViewById(R.id.joinBtn);
        mEmailCheckBtn = findViewById(R.id.emailCheckBtn);
        mNicknameCheckBtn = findViewById(R.id.nicknameCheckBtn);
        mName = findViewById(R.id.userName);
        manBtn = findViewById(R.id.man);
        womanBtn = findViewById(R.id.woman);
        mBirthdate = findViewById(R.id.datePicker);
        mNickname = findViewById(R.id.nickname);

        mEmailCheckBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                emailCheck=1;
                String email = mEmailText.getText().toString().trim();
                String regex = "^[_a-zA-Z0-9-\\.]+@[\\.a-zA-Z0-9-]+\\.[a-zA-Z]+$";
                Pattern p = Pattern.compile(regex);
                Matcher m = p.matcher(email);

                if(m.matches()==false){
                    Toast.makeText(getApplicationContext(), "올바르지 않은 이메일 형식입니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference reference = database.getReference("Emails");
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    String email = mEmailText.getText().toString().trim();
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Iterator<DataSnapshot> child = snapshot.getChildren().iterator();
                        while(child.hasNext()){
                            if(email.equals(child.next().getValue())){
                                Toast.makeText(JoinActivity.this, "이미 존재하는 이메일입니다.", Toast.LENGTH_SHORT).show();
                                duplicationEmail=1;
                                return;
                            }
                        }
                        duplicationEmail=2;
                        verifiedEmail=email;
                        Toast.makeText(getApplicationContext(), "사용해도 좋은 이메일입니다.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        mNicknameCheckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mNickname.getText().toString().trim().equals("")){
                    Toast.makeText(getApplicationContext(), "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                nicknameCheck=1;
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference reference = database.getReference("Nicknames");
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    String nickname = mNickname.getText().toString().trim();
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Iterator<DataSnapshot> child = snapshot.getChildren().iterator();
                        while(child.hasNext()){
                            if(nickname.equals(child.next().getValue())){
                                Toast.makeText(JoinActivity.this, "이미 존재하는 닉네임입니다.", Toast.LENGTH_SHORT).show();
                                duplicationNickname=1;
                                return;
                            }
                        }
                        duplicationNickname=2;
                        verifiedNickname=nickname;
                        Toast.makeText(getApplicationContext(), "사용해도 좋은 닉네임입니다.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        mjoinBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                String email = mEmailText.getText().toString().trim();
                String pw = mPasswordText.getText().toString().trim();
                String pwcheck = mPasswordcheckText.getText().toString().trim();
                String name = mName.getText().toString().trim();
                String nickname = mNickname.getText().toString().trim();

                if(email==null||pw==null||pwcheck==null||name==null||nickname==null||(!manBtn.isChecked()&&!womanBtn.isChecked())){
                    Toast.makeText(JoinActivity.this, "모든 정보를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }

                else if(!pw.equals(pwcheck)){
                    Toast.makeText(JoinActivity.this, "비밀번호와 비밀번호 확인이 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                }

                else if(emailCheck==0||duplicationEmail!=2||!verifiedEmail.equals(email)){
                    Toast.makeText(JoinActivity.this, "이메일 중복확인을 해주세요.", Toast.LENGTH_SHORT).show();
                }

                else if(nicknameCheck==0||duplicationNickname!=2||!verifiedNickname.equals(nickname)){
                    Toast.makeText(JoinActivity.this, "닉네임 중복확인을 해주세요.", Toast.LENGTH_SHORT).show();
                }

                else{
                    final ProgressDialog mDialog = new ProgressDialog(JoinActivity.this);
                    mDialog.setMessage("가입중입니다...");
                    mDialog.show();
                    firebaseAuth.createUserWithEmailAndPassword(email, pw).addOnCompleteListener(JoinActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                mDialog.dismiss();
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                String uid = user.getUid();
                                String email = user.getEmail();
                                String password = mPasswordText.getText().toString().trim();
                                String name = mName.getText().toString().trim();
                                String yearOfBirth = String.valueOf(mBirthdate.getYear());
                                String monthOfBirth = String.valueOf(mBirthdate.getMonth());
                                String dayOfBirth = String.valueOf(mBirthdate.getDayOfMonth());
                                String nickname = mNickname.getText().toString().trim();

                                HashMap<Object,String> hashMap = new HashMap<>();
                                hashMap.put("uid", uid);
                                hashMap.put("email", email);
                                hashMap.put("password", password);
                                hashMap.put("name", name);
                                if(manBtn.isChecked()){
                                    hashMap.put("gender", "man");
                                }
                                else {
                                    hashMap.put("gender", "woman");
                                }
                                hashMap.put("birthdate", yearOfBirth+"-"+monthOfBirth+"-"+dayOfBirth);
                                hashMap.put("nickname", nickname);

                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference reference = database.getReference("Users");
                                reference.child(uid).setValue(hashMap);

                                DatabaseReference reference2 = database.getReference("Emails");
                                reference2.child(uid).setValue(email);

                                DatabaseReference reference3 = database.getReference("Nicknames");
                                reference3.child(uid).setValue(nickname);

                                Intent intent = new Intent(JoinActivity.this, Login.class);
                                startActivity(intent);
                            }
                        }
                    });
                }
            }
        });
    }



}

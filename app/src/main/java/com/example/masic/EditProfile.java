package com.example.masic;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Iterator;

public class EditProfile extends AppCompatActivity {

    Button completeBtn, imageBtn, cancelBtn, nicknameCheckBtn;
    EditText nicknameEdit, introductionEdit;
    String uid;
    ImageView imageView;
    Uri selectedImageUri; // 내가 선택한 이미지 URI
    String firebaseUri; // URI를 String으로 변환한 값
    String verifiedNickname;
    int imageSelected,duplicationNickname, nicknameCheck;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        Intent intent = getIntent(); //fragment44로부터 uid를 받음
        uid = intent.getStringExtra("sendUid");
        Log.d("인텐트 전달", uid);
        nicknameCheckBtn = findViewById(R.id.profileNicknameCheck);
        completeBtn = findViewById(R.id.completeBtn);
        nicknameEdit = findViewById(R.id.nicknameEdit);
        cancelBtn = findViewById(R.id.cancelBtn);
        introductionEdit = findViewById(R.id.introductionEdit);
        imageBtn = findViewById(R.id.profilePictureEdit);

        nicknameCheckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nicknameEdit.getText().toString().trim().equals("")){
                    Toast.makeText(getApplicationContext(), "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                nicknameCheck=1;
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference reference = database.getReference("Nicknames");
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    String nickname = nicknameEdit.getText().toString().trim();
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Iterator<DataSnapshot> child = snapshot.getChildren().iterator();
                        while(child.hasNext()){
                            if(nickname.equals(child.next().getValue())){
                                Toast.makeText(getApplicationContext(), "이미 존재하는 닉네임입니다.", Toast.LENGTH_SHORT).show();
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
        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //사진 선택
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent. setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                imageView = findViewById(R.id.profilePicture);
                startActivityForResult(intent, 102);

            }
        });
        completeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nickname = nicknameEdit.getText().toString().trim();
                if(nicknameEdit.getText().toString().trim().equals("")){
                    Toast.makeText(getApplicationContext(),"닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(introductionEdit.getText().toString().trim().equals("")){
                    Toast.makeText(getApplicationContext(),"소개를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(imageSelected==0){
                    Toast.makeText(getApplicationContext(),"이미지를 선택해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(nicknameCheck==0||duplicationNickname!=2||!(verifiedNickname.equals(nickname))){
                    Toast.makeText(getApplicationContext(), "닉네임 중복확인을 해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                //갤러리에서 선택한 이미지 저장
                final ProgressDialog mDialog = new ProgressDialog(EditProfile.this);
                mDialog.setMessage("저장중입니다...");
                mDialog.show();

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference("ProfileImage").child(uid);
                UploadTask uploadTask = storageRef.putFile(selectedImageUri);

                // Register observers to listen for when the download is done or if it fails
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                        // ...
                        //Storage에서 url 얻어와서 Database에 이미지 url 저장
                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                firebaseUri=uri.toString();

                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference reference = database.getReference("Users").child(uid).child("profileImage");
                                reference.setValue(firebaseUri);

                                String nickname = nicknameEdit.getText().toString().trim();
                                String introduction = introductionEdit.getText().toString();
                                reference = database.getReference("Users").child(uid).child("nickname");
                                reference.setValue(nickname);
                                reference = database.getReference("Users").child(uid).child("introduction");
                                reference.setValue(introduction);
                                reference = database.getReference("Nicknames").child(uid);
                                reference.setValue(nickname);
                                Intent intent = new Intent();
                                intent.putExtra("newNickname", nickname);
                                intent.putExtra("newIntroduction", introduction);
                                intent.putExtra("newPicture", selectedImageUri.toString());

                                mDialog.dismiss();
                                firebaseUri=null;

                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                    }
                });

            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 102 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            imageSelected = 1;
            imageView.setImageURI(selectedImageUri);
        }
    }
}
package com.example.masic;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends AppCompatActivity implements OnItemClick,OnItemClick2 {

    static final int[] soundID = {
            R.raw.alarm,
            R.raw.chicken,
            R.raw.close_door,
            R.raw.coin,
            R.raw.desk,
            R.raw.drop,
            R.raw.eat,
            R.raw.electric,
            R.raw.empty_can,
            R.raw.enter,
            R.raw.fan,
            R.raw.flip_through_paper,
            R.raw.guitar,
            R.raw.harmony,
            R.raw.knock,
            R.raw.knock_mirror,
            R.raw.light_on,
            R.raw.lighter,
            R.raw.line,
            R.raw.mouse_click,
            R.raw.pencil,
            R.raw.perfume,
            R.raw.plastic_bag,
            R.raw.sabuzak,
            R.raw.scissors,
            R.raw.switch_click,
            R.raw.switch_on,
            R.raw.tv,
            R.raw.whistle,
            R.raw.zipper,
    };

    public static Intent serviceIntent = null;

    Fragment11 fg1; //메인화면
    Fragment22 fg2; //제작화면
    Fragment33 fg3; //플레이화면
    Fragment44 fg4; //프로필화면
    OtherProfile of;
    String uid; //사용자 uid - 희원
    Bundle bundle = new Bundle(); //Fragement에 전달할 번들 생성 - 희원
    String getUid; //CardView에서 받아올 아이템의 uid
    //재생화면을 위한 데이터
    String[] node = new String[Fragment22.gridRowSize * Fragment22.gridColumnSize];
    String title, producer, image;

    private FrameLayout frameLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //사용자 uid 받아옴 - 희원
        Intent intent = getIntent();
        uid=intent.getStringExtra("uid");

        bundle.putString("uid", uid);

        fg1 = new Fragment11();
        fg2 = new Fragment22();
        fg3 = new Fragment33();
        fg4 = new Fragment44();
        of = new OtherProfile();

        fg1.setArguments(bundle); //fg1에 bundle 전달 - 희원
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fg1).commit();

        BottomNavigationView btn = findViewById(R.id.bottom_navigation);
        btn.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() { //하단바 버튼에 따라 Fragment전환
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.tab1:
                        fg1.setArguments(bundle); //fg1에 bundle 전달 - 희원
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, fg1).commit();
                        return true;
                    case R.id.tab2:
                        fg2.setArguments(bundle); //fg2에 bundle 전달 - 희원
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, fg2).commit();
                        return true;
                    case R.id.tab3:
                        fg3.setArguments(bundle); //fg3에 bundle 전달 - 희원
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, fg3).commit();
                        return true;
                    case R.id.tab4:
                        fg4.setArguments(bundle); //fg4에 bundle 전달 - 희원
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, fg4).commit();
                        Thread thread = new Thread() {
                            @Override
                            public void run() {
                                while(true) {
                                    if(fg4.chk == 1) break;//Fragment에서 데이터베이스로부터 데이터 불러온지 확인
                                }
                                bundle.putString("nickname", fg4.getNickname());
                                bundle.putString("introduction", fg4.getIntroduction());
                                final FragmentTransaction ft4 = getSupportFragmentManager().beginTransaction();
                                ft4.detach(fg4); //Fragment뗐다가 다시 붙임
                                ft4.attach(fg4);
                                ft4.commit();
                            }
                        };
                        thread.start();
                        return true;
                }
                return false;
            }
        });

    }

    /*
    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment).commit();
    }*/

    //OnItemClick 인터페이스에서 선언한 함수 (재생화면 띄우기 위해 만듦)
    @Override
    public void onClick(String[] node, String title, String producer, String image) {
        this.node=node;
        this.title=title;
        this.producer=producer;
        this.image=image;

        bundle.putStringArray("node", node);
        bundle.putString("title", title);
        bundle.putString("producer", producer);
        bundle.putString("image", image);
        fg3.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fg3).commit();


    }

    //닉네임을 눌렀을 때 해당 사용자의 프로필을 보기 위한 클릭 리스너
    @Override
    public void onClick2(String uid) {
        bundle.putString("itemUid", uid); //해당 사용자의 uid정보 넘김
        of.setArguments(bundle);
        Log.d("테스트-", uid);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, of).commit();
        Thread thread2 = new Thread() {
            @Override
            public void run() {
                while(true) {
                    if(of.chk == 1) break; //프로필 정보가 데이터베이스로부터 모두 불러왔는지 확인
                }
                bundle.putString("nickname", of.getNickname()); //불러온 정보로 닉네임 설정
                Log.d("테스트", of.getNickname());
                bundle.putString("introduction", of.getIntroduction()); //불러온 정보로 소개 설정
                Log.d("테스트", of.getIntroduction());
                of.setArguments(bundle);
                final FragmentTransaction ot = getSupportFragmentManager().beginTransaction();
                ot.detach(of); //Fragment를 뗐다가 다시 붙임
                ot.attach(of);
                ot.commit();
            }
        };
        thread2.start();
    }
}
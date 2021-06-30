package com.example.masic;

import android.app.Service;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;

public class ServicePlay extends Service {
    static String[] nodeData;
    static boolean play = false;
    static int r = 0; static int c = 0;
    static int[][] nodeInt = new int[Fragment22.gridRowSize][Fragment22.gridColumnSize];
    static int fragment = -1;

    SoundPool sp;
    //최대 음악파일의 개수,  스트림타입,  음질-기본값:0
    //재생할 파일

    PlayThread playThread = null;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            AudioAttributes audioAttributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build();
            sp = new SoundPool.Builder().setMaxStreams(Fragment22.gridRowSize * 2).setAudioAttributes(audioAttributes).build();
        }
        else{
            sp = new SoundPool(Fragment22.gridRowSize * 2, AudioManager.STREAM_MUSIC, 0);
        }
        sp.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                soundPool.play(sampleId, 1f, 1f, 0, 0, 1f);
                //왼쪽-오른쪽볼륨(0.0~1.0), 우선순위, 반복횟수, 재생속도
            }
        });
        for(int i = 0; i< Fragment22.gridRowSize; i++){
            for(int j = 0; j< Fragment22.gridColumnSize; j++){
                nodeInt[i][j] = -1;
            }
        }              //-1로 초기화
        super.onCreate();
    }
    private class PlayThread extends Thread {
        private static final String TAG = "PlayThread";
        Intent intent;
        public PlayThread(Intent intent) {
            this.intent = intent;
        }
        public void run() {
            if(intent.getBooleanExtra("new", true)) {
                Bundle data = intent.getExtras();
                nodeData = data.getStringArray("node");
                fragment = data.getInt("Fragment");
                r = 0;
                c = 0;
                for (int i = 0; i < Fragment22.gridSize; i++) {
                    nodeInt[r][c++] = Integer.parseInt(nodeData[i]);
                    if (c >= Fragment22.gridColumnSize) {
                        c %= Fragment22.gridColumnSize;
                        r++;
                    }
                }
                r = 0;
                c = 0;

                play = true;
                switch(fragment) {// 재생중일 때 ■ 모양 버튼 띄우기
                    case 2:
                        Fragment22.playButton.setText("■");
                        break;
                    case 3:
                        Fragment33.playBtn.setText("■");
                        break;
                    default:
                        break;
                }
                //곡 play
                for (; c < Fragment22.gridColumnSize && play; c++) {      //각 열마다 반복
                    final int[] rowInt = new int[Fragment22.gridRowSize];  //최대 행크기만큼 생성
                    Arrays.fill(rowInt, -1);                //-1로 초기화
                    int count = 0;
                    for (r=0; r < Fragment22.gridRowSize; r++) {     //최대행크기만큼 반복
                        int temp = nodeInt[r][c];
                        if (temp != -1) {                       //-1이 아니면 rowInt에 할당
                            rowInt[count++] = temp;
                        }
                    }

                    for (int t = 0; t < rowInt.length; t++) {
                        if (rowInt[t] <= -1 || rowInt[t] >= MainActivity.soundID.length) { //노드가 없는경우(<0), 노드에 해당하는 곡이 없는경우(>=length)
                            continue;
                        } else {                                 //rowInt값을 통해 재생할 파일 선택
                            sp.load(getApplicationContext(), MainActivity.soundID[rowInt[t]],1);
                        }
                    }
                    try {                                       //다음 열로 넘어가는 경우는 일정박자 후이기 때문에 delay time 설정
                        long temp=0;
                        switch(Fragment22.beat){
                            case 2: case 4:
                                temp = 500;
                                break;
                            case 3:
                                temp = 666;
                                break;
                            default:
                                temp = 1000;
                                break;
                        }
                        TimeUnit.MILLISECONDS.sleep(temp-100);      //박자에 따라 delay(-100 : for문 연산에 걸리는 시간을 0.1초로 가정)
                    } catch (InterruptedException e) {
                        play = false;
                        switch (fragment) {// 재생 중지됐을 때 ▶ 모양 버튼 띄우기
                            case 2:
                                Fragment22.playButton.setText("▶");
                                break;
                            case 3:
                                Fragment33.playBtn.setText("▶");
                                break;
                            default:
                                break;
                        }
                        e.printStackTrace();
                    }
                }
                play = false;
                switch (fragment) {// 재생 중지됐을 때 ▶ 모양 버튼 띄우기
                    case 2:
                        Fragment22.playButton.setText("▶");
                        break;
                    case 3:
                        Fragment33.playBtn.setText("▶");
                        break;
                    default:
                        break;
                }
            }

        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent.getBooleanExtra("new", true)) {
            if(playThread!=null && playThread.isAlive()){
                playThread.interrupt();
            }
            playThread = new PlayThread(intent);
            playThread.start();
        }
        else{
            if(playThread!=null && playThread.isAlive()){
                playThread.interrupt();
            }
            play = false;
            fragment = intent.getExtras().getInt("Fragment");
            switch(fragment) {// 재생 중지됐을 때 ▶ 모양 버튼 띄우기
                case 2:
                    Fragment22.playButton.setText("▶");
                    break;
                case 3:
                    Fragment33.playBtn.setText("▶");
                    break;
                default:
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        if(playThread!=null && playThread.isAlive()){
            playThread.interrupt();
        }
        play = false;
        switch(fragment) {// 재생 중지됐을 때 ▶ 모양 버튼 띄우기
            case 2:
                Fragment22.playButton.setText("▶");
                break;
            case 3:
                Fragment33.playBtn.setText("▶");
                break;
            default:
                break;
        }
        super.onDestroy();
    }
}

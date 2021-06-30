package com.example.masic;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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

import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment22#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment22 extends Fragment {
    static int gridColumnSize = 20;                     //노드 가로최대개수
    static int gridRowSize = 10;                        //노드 세로최대개수
    static int gridSize = gridColumnSize*gridRowSize;   //노드 최대개수
    static int beat = 4;                                //박자
    TextView selectedSoundNode = null;                  //드래그중인 node
    boolean dragAndDrop = false;                        //드래그앤드롭 판단
    SoundPool sp=null;
    Handler handler = new Handler();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    String uid; //MainActivity로부터 받아온 uid 저장할 변수
    String [][] textValue = new String[10][20]; //텍스트뷰의 값 저장할 이차원 배열
    EditText musicTitleText; // 음악 제목
    Button mSaveBtn; // 저장 버튼
    static Button playButton;  //play 버튼
    int musicNum=0; // 음악 번호 매기기 위한 변수
    int check=0; // 데이터베이스의 사용자별 음악개수 세는 것 완료했는지 여부
    View[][] node = new View[10][20]; //playbutton.setonClickListener의 변수를 전역변수로 빼냄 저장버튼 누를 때도 사용해야하기 때문
    ImageButton imageButton; //카메라 모양 버튼(갤러리 접근)
    ImageView imageView; // 이미지뷰에 내가 선택한 이미지 보여줌
    Uri selectedImageUri; // 내가 선택한 이미지 URI
    String firebaseUri; // 파이어베이스의 storage에서 받아온 URI 문자열로 변환한 것
    String nickname;

    public Fragment22() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment22.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment22 newInstance(String param1, String param2) {
        Fragment22 fragment = new Fragment22();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            uid=getArguments().getString("uid"); //MainActivity로부터 uid 받아옴
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //dp to pixcel
        int dp_pixcel = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics());

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_22,container, false);

        //play 버튼 할당
        playButton = (Button)rootView.findViewById(R.id.playButton);
        if(ServicePlay.play){
            playButton.setText("■");
        }
        else {
            playButton.setText("▶");
        }

        //GridView 할당 및 size 저장
        final GridLayout grid = (GridLayout)rootView.findViewById(R.id.gridLayout);
        final int beat = 4;               //박자
        int second = 10;            //전체 길이(초)
        int tempColumnSize;
        //박자에 맞게 그리드뷰 columnsize 설정
        switch(beat){
            case 2: case 4: // 2/4, 4/4 박
                tempColumnSize = second*2;
                break;
            case 3:         // 3/4박
                tempColumnSize = second*3/2;
                break;
            default:
                tempColumnSize = second*1;
                break;
        }

        final int gridColumnSize = tempColumnSize;  //노드 가로최대개수
        final int gridRowSize = 10;                 //노드 세로최대개수
        final int gridSize = gridColumnSize*gridRowSize;//노드 최대개수
        grid.setColumnCount(gridColumnSize); grid.setRowCount(gridRowSize);

        //sound delete
        View.OnClickListener soundDelete = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                v.setBackgroundColor(getResources().getColor(R.color.black));
                ((TextView)v).setText("-1");
                ((TextView)v).setTextColor(getResources().getColor(R.color.invisible));
            }
        };

        //sound drag
        View.OnLongClickListener soundDrag = new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                ClipData data = ClipData.newPlainText("","");
                View.DragShadowBuilder myShadowBuilder = new View.DragShadowBuilder(v);
                v.startDrag(data, myShadowBuilder, v,0);
                selectedSoundNode = (TextView)v;
                dragAndDrop = true;
                return true;
            }
        };

        //sound drop to node
        View.OnDragListener soundDropToNode = new View.OnDragListener(){
            @Override
            public boolean onDrag(View v, DragEvent event) {
                int dragAction = event.getAction();
                TextView soundNode = (TextView)event.getLocalState();
                switch(dragAction){
                    case DragEvent.ACTION_DRAG_ENTERED:
                        if(((TextView)v).getText() == "-1") {
                            v.setBackgroundColor(getResources().getColor(R.color.white));
                        }
                        break;
                    case DragEvent.ACTION_DROP:
                        ((TextView)v).setText((String)soundNode.getTag());
                        ColorDrawable temp = (ColorDrawable)selectedSoundNode.getBackground();
                        int colorCode = temp.getColor();
                        v.setBackgroundColor(colorCode);
                        ((TextView)v).setTextColor(getResources().getColor(R.color.black));
                        selectedSoundNode = null;
                        dragAndDrop = false;
                    case DragEvent.ACTION_DRAG_EXITED:
                        if(dragAndDrop && ((TextView)v).getText() == "-1") {
                            v.setBackgroundColor(getResources().getColor(R.color.black));
                            ((TextView)v).setTextColor(getResources().getColor(R.color.invisible));
                        }
                        break;
                }
                return true;
            }
        };

        TextView[] txView = new TextView[gridSize];
        for(int i=0; i<txView.length - gridColumnSize*0; i++){
            txView[i] = new TextView(rootView.getContext());
            txView[i].setWidth(50*dp_pixcel);
            txView[i].setGravity(Gravity.CENTER);
            txView[i].setText("-1");
            txView[i].setTextSize(15);
            txView[i].setTextColor(getResources().getColor(R.color.invisible));
            txView[i].setBackgroundColor(getResources().getColor(R.color.black));
            txView[i].setTag("node");

            //drop
            txView[i].setOnDragListener(soundDropToNode);
            txView[i].setOnClickListener(soundDelete);

            grid.addView(txView[i]);
            GridLayout.LayoutParams tempTVGrLayoutParams = (GridLayout.LayoutParams)txView[i].getLayoutParams();
            tempTVGrLayoutParams.setMargins(5,0,5,30);

            textValue[i/gridColumnSize][i%gridColumnSize]="-1"; //이차원 배열에 텍스트 값 저장 - 희원
        }

        //sound 미리듣기
        View.OnClickListener soundPreView = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), ServicePlay.class);
                intent.putExtra("sound", Integer.parseInt(v.getTag().toString()));

                PreviewThread preView = new PreviewThread(intent){
                    public void run(){
                        //soundpool 생성
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                            AudioAttributes audioAttributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA)
                                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build();
                            sp = new SoundPool.Builder().setMaxStreams(Fragment22.gridRowSize * 2).setAudioAttributes(audioAttributes).build();
                        }
                        else{
                            sp = new SoundPool(Fragment22.gridRowSize * 2, AudioManager.STREAM_MUSIC, 0);
                        }
                        SoundPool.OnLoadCompleteListener loadPlay = new SoundPool.OnLoadCompleteListener() {
                            @Override
                            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                                int streamID = soundPool.play(sampleId, 1f, 1f, 0, 0, 1f);
                                //왼쪽-오른쪽볼륨(0.0~1.0), 우선순위, 반복횟수, 재생속도
                                try {
                                    TimeUnit.MILLISECONDS.sleep(1000);
                                }
                                catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                finally {
                                    if(sp!=null) {
                                        sp.stop(streamID);
                                        sp.release();
                                        sp = null;

                                    }
                                }
                            }
                        };
                        sp.setOnLoadCompleteListener(loadPlay);
                        //최대 음악파일의 개수,  스트림타입,  음질-기본값:0
                        sp.load(getContext(), MainActivity.soundID[intent.getIntExtra("sound",0)],1);
                    }
                };
                preView.start();
                preView.interrupt();
            }
        };

        //하단 sound source TextView
        LinearLayout soundList = rootView.findViewById(R.id.soundSource);
        TextView[] soundSource = new TextView[soundList.getChildCount()];
        for(int i=0;i<soundSource.length;i++){
            soundSource[i] = (TextView)soundList.getChildAt(i);
            soundSource[i].setOnLongClickListener(soundDrag);
            soundSource[i].setOnClickListener(soundPreView);
        }

        //background play event
        //Flagment11의 카드뷰에서 title, 곡이미지 View에 .setOnClickListener(backgroundPlay); 하면 됨
        //playbutton ServicePlay 클릭 이벤트
        View.OnClickListener backgroundPlay = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //
                //firebase에서 배열 받아오는 코드
                //
                if(MainActivity.serviceIntent == null){MainActivity.serviceIntent = new Intent(getActivity().getApplicationContext(), ServicePlay.class);}
                if (!ServicePlay.play) {
                    Bundle data = new Bundle();
                    String[] nodeData = new String[gridRowSize * gridColumnSize];
                    for (int i = 0; i < nodeData.length; i++) {
                        nodeData[i] = (String) ((TextView) grid.getChildAt(i)).getText();
                    }//노드 데이터를 string으로 변환하는 과정
                    //firebase에서 받아온 배열 데이터를 [0][0],[0][1]...[1][0],[1][1]...[2][0],[2][1]... 순으로 넣으면 됨
                    data.putStringArray("node", nodeData);
                    data.putBoolean("new", true);
                    data.putInt("Fragment", 2);
                    MainActivity.serviceIntent.putExtras(data);
                    getActivity().startService(MainActivity.serviceIntent);
                }
                else{
                    Bundle data = new Bundle();
                    data.putInt("Fragment", 2);
                    data.putBoolean("new", false);
                    MainActivity.serviceIntent.putExtras(data);
                    getActivity().stopService(MainActivity.serviceIntent); // 재생 중지
                    MainActivity.serviceIntent = null;
                }
            }
        };
/*
        //playbutton directPlay 클릭 이벤트
        View.OnClickListener directPlay = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //node[][]에 GridView의 View들 각각 할당
                //View[][] node = new View[gridRowSize][gridColumnSize];
                int r = 0; int c = 0;
                for(int i =0;i<gridSize;i++){
                    View temp = grid.getChildAt(i);
                    if(((TextView)temp).getTag() == "node") {
                        node[r][c++] = temp;
                        if(c>=gridColumnSize) { c%=gridColumnSize; r++; }
                    }
                }
                //곡 play
                for (int i = 0; i < gridColumnSize; i++) {      //각 열마다 반복
                    final int[] rowInt = new int[gridRowSize];  //최대 행크기만큼 생성
                    Arrays.fill(rowInt, -1);                //-1로 초기화
                    int count = 0;
                    for (int j = 0; j < gridRowSize; j++) {     //최대행크기만큼 반복
                        int temp = Integer.parseInt((String) ((TextView) node[j][i]).getText());
                        if (temp != -1) {                       //-1이 아니면 rowInt에 할당
                            rowInt[count++] = temp;
                        }
                    }

                    for (int t = 0; t < rowInt.length; t++) {
                        if (rowInt[t] <= -1 || rowInt[t] >= MainActivity.soundID.length) { //노드가 없는경우(<0), 노드에 해당하는 곡이 없는경우(>=length)
                            continue;
                        } else {                                 //rowInt값을 통해 재생할 파일 선택
                            sp.play(MainActivity.soundID[rowInt[t]], 1, 1, 0, 0, 1f);
                            //왼쪽-오른쪽볼륨(0.0~1.0), 우선순위, 반복횟수, 재생속도
                        }
                    }
                    try {                                       //다음 열로 넘어가는 경우는 일정박자 후이기 때문에 delay time 설정
                        long temp=0;
                        switch(beat){
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
                        e.printStackTrace();
                    }
                }
            }
        };
        */
        //playbutton onClickListener 등록
        playButton.setOnClickListener(backgroundPlay);

        //여기서부터 이미지 등록 기능 - 희원
        imageButton = rootView.findViewById(R.id.galleryBtn);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent. setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, 101);
            }
        });

        imageView = rootView.findViewById(R.id.imageView);

        // 여기서부터 저장 기능 - 희원
        //새로 만든 저장 기능
        // uid 없이 저장할 AllMusic 참조(나중에 전체 곡 불러오기 편하도록)
        FirebaseDatabase database2 = FirebaseDatabase.getInstance();
        DatabaseReference reference2 = database2.getReference("AllMusic");
        reference2.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(check==0){
                    Iterator<DataSnapshot> child = snapshot.getChildren().iterator();
                    while(child.hasNext()){
                        if(child.next().getKey()!=null)musicNum++; // 음악 전체 개수가 몇개인지 세서 번호가 정해짐
                    }
                    check=1;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        FirebaseDatabase database3 = FirebaseDatabase.getInstance();
        DatabaseReference reference3 = database3.getReference("Nicknames").child(uid);
        reference3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                nickname=snapshot.getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mSaveBtn= (Button)rootView.findViewById(R.id.saveBtn);
        musicTitleText = (EditText)rootView.findViewById(R.id.musicTitle);
        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //저장하는중
                final ProgressDialog mDialog = new ProgressDialog(getActivity());
                mDialog.setMessage("저장중입니다...");
                mDialog.show();
                //사용자 uid별 곡 저장할 MyMusic 참조
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference reference = database.getReference("MyMusic").child(uid);
                // uid 없이 저장할 AllMusic 참조(나중에 전체 곡 불러오기 편하도록)
                FirebaseDatabase database2 = FirebaseDatabase.getInstance();
                DatabaseReference reference2 = database2.getReference("AllMusic");

                String title = musicTitleText.getText().toString();

                if(title.equals("")){
                    Toast.makeText(getActivity(), "제목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                else if(selectedImageUri==null){
                    Toast.makeText(getActivity(), "카메라 모양 버튼을 눌러 이미지를 등록해주세요.", Toast.LENGTH_SHORT).show();
                }
                else {
                    while (true){
                        if(check==1)break;
                    }

                    int r = 0; int c = 0;
                    for(int i =0;i<gridSize;i++){
                        View temp = grid.getChildAt(i);
                        if(((TextView)temp).getTag() == "node") {
                            node[r][c++] = temp;
                            if(c>=gridColumnSize) { c%=gridColumnSize; r++; }
                        }
                    }


                    //GridView에 있는 소리별 Text 값 저장
                    int index=0;
                    for(int i=0; i<gridRowSize; i++){
                        for(int j=0; j<gridColumnSize; j++){
                            reference.child(Integer.toString(musicNum)).child("sound").child(Integer.toString(index)).setValue(((TextView)node[i][j]).getText().toString());
                            reference2.child(Integer.toString(musicNum)).child("sound").child(Integer.toString(index)).setValue(((TextView)node[i][j]).getText().toString());
                            index++;
                        }
                    }

                    //음악 제목 저장
                    reference.child(Integer.toString(musicNum)).child("title").setValue(title);
                    reference2.child(Integer.toString(musicNum)).child("title").setValue(title);
                    //닉네임 저장
                    reference.child(Integer.toString(musicNum)).child("nickname").setValue(nickname);
                    reference2.child(Integer.toString(musicNum)).child("nickname").setValue(nickname);
                    //uid 저장
                    reference.child(Integer.toString(musicNum)).child("uid").setValue(uid);
                    reference2.child(Integer.toString(musicNum)).child("uid").setValue(uid);
                    //갤러리에서 선택한 이미지 Storage에 저장
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReference("MusicImageTmp").child(Integer.toString(musicNum));
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
                                    //사용자 uid별 곡 저장할 MyMusic 참조
                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    DatabaseReference reference = database.getReference("MyMusic").child(uid);
                                    // uid 없이 저장할 AllMusic 참조(나중에 전체 곡 불러오기 편하도록)
                                    FirebaseDatabase database2 = FirebaseDatabase.getInstance();
                                    DatabaseReference reference2 = database2.getReference("AllMusic");
                                    reference.child(Integer.toString(musicNum)).child("image").setValue(firebaseUri);
                                    reference2.child(Integer.toString(musicNum)).child("image").setValue(firebaseUri);
                                    mDialog.dismiss();
                                    Toast.makeText(getActivity(),"저장이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                    musicNum=0;
                                    check=0;
                                    firebaseUri=null;
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });

                        }
                    });

                }

            }
        });

        //이전에 만든 저장 기능
        /*FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("MusicSound").child(uid);
        reference.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                musicNum=0;
                check=0;
                Iterator<DataSnapshot> child = snapshot.getChildren().iterator();
                while(child.hasNext()){
                    if(child.next().hasChildren())musicNum++; // 사용자의 음악 개수가 몇개인지 세서 번호가 정해짐
                }
                check=1;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        mSaveBtn= (Button)rootView.findViewById(R.id.saveBtn);
        musicTitleText = (EditText)rootView.findViewById(R.id.musicTitle);
        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = musicTitleText.getText().toString();

                if(title.equals("")){
                    Toast.makeText(getActivity(), "제목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                else if(selectedImageUri==null){
                    Toast.makeText(getActivity(), "카메라 모양 버튼을 눌러 이미지를 등록해주세요.", Toast.LENGTH_SHORT).show();
                }
                else {
                    //GridView에 있는 소리별 Text 값 저장
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference reference = database.getReference("MusicSound").child(uid);

                    while (true){
                        if(check==1)break;
                    }

                    int r = 0; int c = 0;
                    for(int i =0;i<gridSize;i++){
                        View temp = grid.getChildAt(i);
                        if(((TextView)temp).getTag() == "node") {
                            node[r][c++] = temp;
                            if(c>=gridColumnSize) { c%=gridColumnSize; r++; }
                        }
                    }
                    int index=0;
                    for(int i=0; i<gridRowSize; i++){
                        for(int j=0; j<gridColumnSize; j++){
                            reference.child(Integer.toString(musicNum)).child(Integer.toString(index)).setValue(((TextView)node[i][j]).getText().toString());
                            index++;
                        }

                    }

                    //음악 제목 저장
                    reference = database.getReference("MusicTitle").child(uid);
                    reference.child(Integer.toString(musicNum)).setValue(title);

                    //갤러리에서 선택한 이미지 저장
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReference("MusicImage").child(uid).child(Integer.toString(musicNum));
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
                        }
                    });

                    musicNum=0;
                    check=0;
                    Toast.makeText(getActivity(),"저장이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                }

            }
        });*/

        return rootView;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            selectedImageUri = data.getData();
            imageView.setImageURI(selectedImageUri);
        }
    }

    private class PreviewThread extends Thread {
        private static final String TAG = "PlayThread";
        Intent intent;
        public PreviewThread(Intent intent) {
            this.intent = intent;
        }
    }

    /*public String getPath(Uri uri){
        String[] proj={MediaStore.Images.Media.DATA};
        CursorLoader cursorLoader = new CursorLoader(getContext(), uri, proj, null, null, null);
        Cursor cursor=cursorLoader.loadInBackground();
        int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return  cursor.getString(index);
    }*/
}
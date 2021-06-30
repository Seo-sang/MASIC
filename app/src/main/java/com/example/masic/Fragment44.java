package com.example.masic;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Iterator;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment44#newInstance} factory method to
 * create an instance of this fragment.
 */
public class    Fragment44 extends Fragment{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    String uid;
    String[] title=new String[1001];
    String nickname;
    int musicNum=0;
    Button editButton;
    String introduction = "";
    String profileUri = "";
    TextView myNickname;
    TextView myIntroduction;
    ImageView myPicture;
    RecyclerView recyclerView;
    int chk = 0;
    String tmpUrl;
    String tmpNickname;
    String tmpTitle;
    String[] nodeData = new String[Fragment22.gridRowSize * Fragment22.gridColumnSize];

    private FirebaseRecyclerAdapter adapter;
    View rootView;
    public Fragment44() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment44.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment44 newInstance(String param1, String param2) {
        Fragment44 fragment = new Fragment44();
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
            uid=getArguments().getString("uid"); //MainActivity로부터 uid 받아옴 - 희원
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_44,container, false); //Recyclerview를 넣을 rootView
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Users").child(uid);//프로필을 표시할 자신의 정보 데이터베이스 접근
        ref.addValueEventListener(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) { //자신의 닉네임, 소개, 프로필사진 불러오기
                nickname = snapshot.child("nickname").getValue().toString();
                if(snapshot.child("introduction").getValue() != null)
                    introduction = snapshot.child("introduction").getValue().toString();
                if(snapshot.child("profileImage").getValue() != null)
                    profileUri = snapshot.child("profileImage").getValue().toString();
                chk = 1;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        myNickname = rootView.findViewById(R.id.mynickname);
        myNickname.setText(nickname); //자신의 닉네임으로 설정
        myIntroduction = rootView.findViewById(R.id.introduce);
        myIntroduction.setText(introduction); //자신의 소개로 설정
        myPicture = rootView.findViewById(R.id.profilePicture);
        if(!profileUri.equals("")){ //프로필설정
            Picasso.get().load(profileUri).into(myPicture);
        }

        editButton = rootView.findViewById(R.id.editButton); //프로필 수정화면으로 넘어감
        editButton.setOnClickListener(new View.OnClickListener() { //수정버튼 클릭리스너
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditProfile.class);
                intent.putExtra("sendUid", uid);
                Log.d("인텐트 전달", uid);
                startActivityForResult(intent, 1);
            }
        });

        recyclerView = (RecyclerView)rootView.findViewById(R.id.profile_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));//recyclerview연결
        //view.setAdapter(new ProfileRecyclerViewAdapter(uid, musicNum, title, nickname));

        fetch();//recyclerview item을 로드해줄 Firebase연결
        musicNum = 0;
        return rootView;
    }

    private void fetch() {
        Query query = FirebaseDatabase.getInstance().getReference().child("MyMusic").child(uid); //자신의 음악목록 데이터베이스 접근

        FirebaseRecyclerOptions<CardViewItemDTO> options = new FirebaseRecyclerOptions.Builder<CardViewItemDTO>()
                .setQuery(query, new SnapshotParser<CardViewItemDTO>(){

                    @Override
                    public CardViewItemDTO parseSnapshot(DataSnapshot snapshot) {//데이터 로드해서 recyclerview의 item에 추가
                        return new CardViewItemDTO(
                                snapshot.child("image").getValue().toString(),
                                snapshot.child("title").getValue().toString(),
                                snapshot.child("nickname").getValue().toString(),
                                snapshot.getKey(),
                                uid);
                    }
                }).build();

        adapter = new FirebaseRecyclerAdapter<CardViewItemDTO, Fragment44.ViewHolder>(options //recyclerview 어댑터
        ) {
            @NonNull
            @Override
            public Fragment44.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_item, parent, false);
                return new Fragment44.ViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull Fragment44.ViewHolder holder, int position, @NonNull CardViewItemDTO model) {
                holder.setTitle(model.getTitle());
                holder.setNickname(model.getNickname());
                holder.setImage(model.getImage());
                holder.setUserMusicNum(model.getUserMusicNum());

                final String musicTitle=model.getTitle();
                final String userMusicNum=model.getUserMusicNum();
                final String producer=model.getNickname();
                final String image=model.getImage();
                final String tmpUid=model.getUid();
                View.OnClickListener soundLoad = new View.OnClickListener() { //앨범커버, 타이틀의 클릭 리스너
                    @Override
                    public void onClick(View v) {

                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference reference = database.getReference("MyMusic").child(uid).child(userMusicNum).child("sound"); //음악 정보 접근
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                final Iterator<DataSnapshot> child = snapshot.getChildren().iterator();
                                int i=0;
                                while(child.hasNext()){
                                    nodeData[i++]=child.next().getValue().toString();//음악 재생 정보 배열에 저장
                                }
                                ((MainActivity)getActivity()).onClick(nodeData, musicTitle, producer, image); //재생 정보 재생화면으로 넘기고 화면 전환
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        reference = database.getReference("AllMusic").child(userMusicNum);//자신의 플레이리스트에 자동 추가 기능
                        Log.d("넘", userMusicNum);
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {//전체 음악에서 앨범커버, 타이틀, 닉네임 불러오기
                                tmpUrl = snapshot.child("image").getValue().toString();
                                tmpNickname = snapshot.child("nickname").getValue().toString();
                                tmpTitle = snapshot.child("title").getValue().toString();
                                Thread thread = new Thread() {//스레드를 이용하여 자신의 플레이리스트 데이터베이스에 선택한 음약의 정보 저장
                                    public void run() {
                                        FirebaseDatabase database2 = FirebaseDatabase.getInstance();
                                        DatabaseReference reference2 = database2.getReference("Playlist");//Firebase에서 자신의 플레이리스트 접근 후 불러온 정보 저장
                                        reference2.child(uid).child(userMusicNum).child("image").setValue(tmpUrl);
                                        reference2.child(uid).child(userMusicNum).child("nickname").setValue(tmpNickname);
                                        reference2.child(uid).child(userMusicNum).child("title").setValue(tmpTitle);
                                    }
                                };
                                thread.start();
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                    }
                };

                ((ViewHolder)holder).Image.setOnClickListener(soundLoad);
                ((ViewHolder)holder).Title.setOnClickListener(soundLoad);
                ((ViewHolder)holder).Nickname.setOnClickListener(new View.OnClickListener() {//닉네임 클릭 리스너 자기 자신이므로 비활성화
                    @Override
                    public void onClick(View v) {
                    }
                });
                ((ViewHolder)holder).addBtn.setOnClickListener(new View.OnClickListener() { //추가버튼 클릭 리스너
                    @Override
                    public void onClick(View v) {

                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        final DatabaseReference reference = database.getReference("AllMusic").child(userMusicNum);//선택 음악의 데이터베이스 접근
                        Log.d("넘", userMusicNum);
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {//해당 음악 정보 데이터베이스로부터 불러오기
                                tmpUrl = snapshot.child("image").getValue().toString();
                                tmpNickname = snapshot.child("nickname").getValue().toString();
                                tmpTitle = snapshot.child("title").getValue().toString();
                                Thread thread = new Thread() { //스레드를 이용하여 자신의 플레이리스트 데이터베이스에 선택한 음약의 정보 저장
                                    public void run() {
                                        FirebaseDatabase database2 = FirebaseDatabase.getInstance();
                                        DatabaseReference reference2 = database2.getReference("Playlist");//Firebase에서 자신의 플레이리스트 접근 후 불러온 정보 저장
                                        reference2.child(uid).child(userMusicNum).child("image").setValue(tmpUrl);
                                        reference2.child(uid).child(userMusicNum).child("nickname").setValue(tmpNickname);
                                        reference2.child(uid).child(userMusicNum).child("title").setValue(tmpTitle);
                                    }
                                };
                                thread.start();
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                    }
                });
            }

        };
        recyclerView.setAdapter(adapter); //recyclerview adapter 설정
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {//recyclerview의 각 아이템 ViewHolder
        public LinearLayout root;
        public TextView Title;
        public TextView Nickname;
        public ImageView Image;
        public String userMusicNum;
        public TextView addBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.list_root);
            Title = itemView.findViewById(R.id.cardview_title);
            Nickname = itemView.findViewById(R.id.cardview_nickname);
            Image = itemView.findViewById(R.id.cardview_picture);
            addBtn = itemView.findViewById(R.id.addButton);
        }

        public void setTitle(String string) {
            Title.setText(string);
        }

        public void setNickname(String string) {
            Nickname.setText(string);
        }
        public void setImage(String string) {
            Picasso.get().load(string).into(Image);
        }

        public void setUserMusicNum(String string){
            userMusicNum=string;
        }

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {
            if(requestCode == 1) {
                myNickname.setText(data.getStringExtra("newNickname"));
                myIntroduction.setText(data.getStringExtra("newIntroduction"));
                myPicture.setImageURI(Uri.parse(data.getStringExtra("newPicture")));
            }
        }
        else {
            return;
        }
    }
    public String getNickname() {
        return nickname;
    }
    public String getIntroduction() {
        return introduction;
    }
}
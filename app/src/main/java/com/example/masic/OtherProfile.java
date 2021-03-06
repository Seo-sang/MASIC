package com.example.masic;

import android.content.Intent;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OtherProfile#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OtherProfile extends Fragment {

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
    int chk=0;
    int checkNickname=0;
    Button editButton;
    String introduction = "";
    String profileUri = "";
    TextView myNickname;
    TextView myIntroduction;
    ImageView myPicture;
    RecyclerView recyclerView;
    String tmpUrl;
    String tmpNickname;
    String tmpTitle;
    String[] nodeData = new String[Fragment22.gridRowSize * Fragment22.gridColumnSize];

    private FirebaseRecyclerAdapter adapter;
    View rootView;

    public OtherProfile() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OtherProfile.
     */
    // TODO: Rename and change types and number of parameters
    public static OtherProfile newInstance(String param1, String param2) {
        OtherProfile fragment = new OtherProfile();
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
            uid=getArguments().getString("itemUid"); //MainActivity????????? uid ????????? - ??????
            Log.d("?????????-", uid);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        nickname = null;
        introduction = "";
        myPicture = null;
        rootView = inflater.inflate(R.layout.fragment_other_profile,container, false);//Recyclerview??? ?????? rootView

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Users").child(uid);//???????????? ????????? ????????? ?????? ?????????????????? ??????
        ref.addValueEventListener(new ValueEventListener(){ //????????? ?????????, ??????, ??????????????? ????????????
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) { //bundle??? ?????? uid????????? ????????? ????????? ?????? ?????????
                nickname = snapshot.child("nickname").getValue().toString();
                myNickname.setText(nickname); //????????? ??????????????? ??????
                if(snapshot.child("introduction").getValue() != null) {
                    introduction = snapshot.child("introduction").getValue().toString();
                    myIntroduction.setText(introduction); //????????? ??????????????? ??????
                }
                else {
                    myIntroduction.setText("");
                }
                if(snapshot.child("profileImage").getValue() != null) {
                    profileUri = snapshot.child("profileImage").getValue().toString();
                    Picasso.get().load(profileUri).into(myPicture); //???????????????
                }
                else {
                    myPicture.setImageResource(R.drawable.profilepicture);
                }
                chk = 1;
                Log.d("?????????", nickname);
                Log.d("?????????", introduction);
                Log.d("?????????", profileUri);
                chk = 1; //?????? ???????????? ??????
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        myNickname = rootView.findViewById(R.id.mynickname);
        myNickname.setText(nickname); //????????? ??????????????? ??????
        myIntroduction = rootView.findViewById(R.id.introduce);
        myIntroduction.setText(introduction); //????????? ??????????????? ??????
        myPicture = rootView.findViewById(R.id.profilePicture);
        if(!profileUri.equals("")){
            Picasso.get().load(profileUri).into(myPicture); //???????????????
        }


        recyclerView = (RecyclerView)rootView.findViewById(R.id.profile_recyclerView);//recyclerview??????
        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));

        fetch();//recyclerview item??? ???????????? Firebase??????
        musicNum = 0;
        return rootView;
    }

    private void fetch() {
        Query query = FirebaseDatabase.getInstance().getReference().child("MyMusic").child(uid);//????????? ???????????? ?????????????????? ??????

        FirebaseRecyclerOptions<CardViewItemDTO> options = new FirebaseRecyclerOptions.Builder<CardViewItemDTO>()
                .setQuery(query, new SnapshotParser<CardViewItemDTO>(){

                    @Override
                    public CardViewItemDTO parseSnapshot(DataSnapshot snapshot) {//????????? ???????????? recyclerview??? item??? ??????
                        return new CardViewItemDTO(
                                snapshot.child("image").getValue().toString(),
                                snapshot.child("title").getValue().toString(),
                                snapshot.child("nickname").getValue().toString(),
                                snapshot.getKey(),
                                uid);
                    }
                }).build();

        adapter = new FirebaseRecyclerAdapter<CardViewItemDTO, OtherProfile.ViewHolder>(options//recyclerview ?????????
        ) {
            @NonNull
            @Override
            public OtherProfile.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_item, parent, false);
                return new OtherProfile.ViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull OtherProfile.ViewHolder holder, int position, @NonNull CardViewItemDTO model) {
                holder.setTitle(model.getTitle());
                holder.setNickname(model.getNickname());
                holder.setImage(model.getImage());
                holder.setUserMusicNum(model.getUserMusicNum());

                final String musicTitle=model.getTitle();
                final String userMusicNum=model.getUserMusicNum();
                final String producer=model.getNickname();
                final String image=model.getImage();

                View.OnClickListener soundLoad = new View.OnClickListener() { //????????????, ???????????? ?????? ?????????
                    @Override
                    public void onClick(View v) {

                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference reference = database.getReference("MyMusic").child(uid).child(userMusicNum).child("sound");//?????? ?????? ??????
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                final Iterator<DataSnapshot> child = snapshot.getChildren().iterator();
                                int i = 0;
                                while(child.hasNext()){
                                    nodeData[i++]=child.next().getValue().toString();//?????? ?????? ?????? ????????? ??????
                                }
                                ((MainActivity)getActivity()).onClick(nodeData, musicTitle, producer, image); //?????? ?????? ?????????????????? ????????? ?????? ??????
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                };

                ((ViewHolder)holder).Nickname.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {//????????? ?????? ????????? ???????????? ????????? ?????????????????? ????????????

                    }
                });
                ((OtherProfile.ViewHolder)holder).Image.setOnClickListener(soundLoad);
                ((OtherProfile.ViewHolder)holder).Title.setOnClickListener(soundLoad);
                ((OtherProfile.ViewHolder)holder).addBtn.setOnClickListener(new View.OnClickListener() {//???????????? ?????? ?????????
                    @Override
                    public void onClick(View v) {

                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        final DatabaseReference reference = database.getReference("AllMusic").child(userMusicNum);//?????? ????????? ?????????????????? ??????
                        Log.d("???", userMusicNum);
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {//?????? ?????? ?????? ??????????????????????????? ????????????
                                tmpUrl = snapshot.child("image").getValue().toString();
                                tmpNickname = snapshot.child("nickname").getValue().toString();
                                tmpTitle = snapshot.child("title").getValue().toString();
                                chk = 1;
                                Thread thread = new Thread() {//???????????? ???????????? ????????? ?????????????????? ????????????????????? ????????? ????????? ?????? ??????
                                    public void run() {
                                        while(true) {
                                            if(chk == 1) break; //???????????????????????? ????????? ?????? ????????? ??? playlist ????????????????????? ????????????
                                        }
                                        FirebaseDatabase database2 = FirebaseDatabase.getInstance();
                                        DatabaseReference reference2 = database2.getReference("Playlist");//Firebase?????? ????????? ?????????????????? ?????? ??? ????????? ?????? ??????
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
        recyclerView.setAdapter(adapter);//recyclerview adapter ??????

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


    public class ViewHolder extends RecyclerView.ViewHolder {//recyclerview??? ??? ????????? ViewHolder
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

    public String getNickname() {
        return nickname;
    }
    public String getIntroduction() {
        return introduction;
    }
}
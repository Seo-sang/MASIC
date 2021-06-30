package com.example.masic;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import static com.example.masic.Fragment22.gridColumnSize;
import static com.example.masic.Fragment22.gridRowSize;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment11#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment11 extends Fragment{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    String uid;
    String[] userMusicNum=new String[1001];//사용자별 음악 번호 저장(uid와 음악 번호로 음악을 참조해야 하기 때문)
    String[] title=new String[1001];
    String[] nickname = new String[1001];
    String otherImage;
    String otherNick;
    String otherIntro;
    String tmpUid;
    int musicNum=0;
    int checkMusicTitle=0;
    DatabaseReference reference;
    View rootView;
    RecyclerView recyclerview;
    private FirebaseRecyclerAdapter adapter;
    String tmpUrl;
    String tmpNickname;
    String tmpTitle;
    int chk = 0;
    String[] nodeData = new String[gridRowSize * gridColumnSize];

    public Fragment11() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment11.
     */

    // TODO: Rename and change types and number of parameters
    public static Fragment11 newInstance(String param1, String param2) {
        Fragment11 fragment = new Fragment11();
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

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        reference = database.getReference("AllMusic");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_11,container, false);
        recyclerview = (RecyclerView)rootView.findViewById(R.id.main_recyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
        recyclerview.setHasFixedSize(true);
        fetch();

        musicNum = 0;
        return rootView;
    }

    private void fetch() {
        Query query = FirebaseDatabase.getInstance().getReference().child("AllMusic");

        FirebaseRecyclerOptions<CardViewItemDTO> options = new FirebaseRecyclerOptions.Builder<CardViewItemDTO>()
                .setQuery(query, new SnapshotParser<CardViewItemDTO>(){

                    @Override
                    public CardViewItemDTO parseSnapshot(DataSnapshot snapshot) {
                        return new CardViewItemDTO(
                                snapshot.child("image").getValue().toString(),
                                snapshot.child("title").getValue().toString(),
                                snapshot.child("nickname").getValue().toString(),
                                snapshot.getKey(),
                                snapshot.child("uid").getValue().toString());
                    }
                }).build();

        adapter = new FirebaseRecyclerAdapter<CardViewItemDTO, ViewHolder>(options
        ) {
            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_item, parent, false);
                return new ViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final ViewHolder holder, int position, @NonNull CardViewItemDTO model) {
                holder.setTitle(model.getTitle());
                holder.setNickname(model.getNickname());
                holder.setImage(model.getImage());
                holder.setUserMusicNum(model.getUserMusicNum());

                final String musicTitle=model.getTitle();
                final String userMusicNum=model.getUserMusicNum();
                final String producer=model.getNickname();
                final String image=model.getImage();
                final String itemUid = model.getUid();
                View.OnClickListener soundLoad = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference reference = database.getReference("AllMusic").child(userMusicNum).child("sound");
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                final Iterator<DataSnapshot> child = snapshot.getChildren().iterator();
                                int i = 0;
                                while (child.hasNext()) {
                                    nodeData[i++] = child.next().getValue().toString();
                                }
                                ((MainActivity) getActivity()).onClick(nodeData, musicTitle, producer, image);
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                        reference = database.getReference("AllMusic").child(userMusicNum);
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                tmpUrl = snapshot.child("image").getValue().toString();
                                tmpNickname = snapshot.child("nickname").getValue().toString();
                                tmpTitle = snapshot.child("title").getValue().toString();
                                chk = 1;
                                Thread thread = new Thread() {
                                    public void run() {
                                        while(true) {
                                            if(chk == 1) break;
                                        }

                                        FirebaseDatabase database2 = FirebaseDatabase.getInstance();
                                        DatabaseReference reference2 = database2.getReference("Playlist");
                                        reference2.child(uid).child(userMusicNum).child("image").setValue(tmpUrl);
                                        reference2.child(uid).child(userMusicNum).child("nickname").setValue(tmpNickname);
                                        reference2.child(uid).child(userMusicNum).child("title").setValue(tmpTitle);
                                        chk = 0;
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
                ((Fragment11.ViewHolder)holder).Image.setOnClickListener(soundLoad);
                ((Fragment11.ViewHolder)holder).Title.setOnClickListener(soundLoad);
                ((Fragment11.ViewHolder)holder).Nickname.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("테스트", itemUid);
                        ((MainActivity) getActivity()).onClick2(itemUid);
                    }
                });
                ((Fragment11.ViewHolder)holder).addBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        final DatabaseReference reference = database.getReference("AllMusic").child(userMusicNum);
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                tmpUrl = snapshot.child("image").getValue().toString();
                                tmpNickname = snapshot.child("nickname").getValue().toString();
                                tmpTitle = snapshot.child("title").getValue().toString();
                                chk = 1;
                                Thread thread = new Thread() {
                                    public void run() {
                                        while(true) {
                                            if(chk == 1) break;
                                        }

                                        FirebaseDatabase database2 = FirebaseDatabase.getInstance();
                                        DatabaseReference reference2 = database2.getReference("Playlist");
                                        reference2.child(uid).child(userMusicNum).child("image").setValue(tmpUrl);
                                        reference2.child(uid).child(userMusicNum).child("nickname").setValue(tmpNickname);
                                        reference2.child(uid).child(userMusicNum).child("title").setValue(tmpTitle);
                                        chk = 0;
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
        recyclerview.setAdapter(adapter);
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


    public class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout root;
        public TextView Title;
        public TextView Nickname;
        public ImageView Image;
        public String userMusicNum;
        public TextView addBtn;
        public String nowUid;
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

    public int getCheckMusicTitle(){
        return checkMusicTitle;
    }
    public String[] getTitle(){
        return title;
    }
    public String[] getUserMusicNum(){
        return userMusicNum;
    }
    public String[] getNickname(){
        return nickname;
    }
    public int getMusicNum(){
        return musicNum;
    }



}
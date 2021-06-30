package com.example.masic;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

public class PlayList extends AppCompatActivity {

    Button backButton; //뒤로가기 버튼
    private FirebaseRecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private String uid;
    String[] nodeData = new String[Fragment22.gridRowSize * Fragment22.gridColumnSize];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_list);
        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");
        backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener(){ //뒤로가기 버튼시 플레이화면으로 되돌아감
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.playlist_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);
        fetch();
    }

    private void fetch() {
        Query query = FirebaseDatabase.getInstance().getReference().child("Playlist").child(uid);
        FirebaseRecyclerOptions<PlayListItemDTO> options = new FirebaseRecyclerOptions.Builder<PlayListItemDTO>()
                .setQuery(query, new SnapshotParser<PlayListItemDTO>() {
                    @NonNull
                    @Override
                    public PlayListItemDTO parseSnapshot(@NonNull DataSnapshot snapshot) {
                        return new PlayListItemDTO(
                                snapshot.child("image").getValue().toString(),
                                snapshot.child("title").getValue().toString(),
                                snapshot.child("nickname").getValue().toString(),
                                snapshot.getKey());
                    }
                }).build();

        adapter = new FirebaseRecyclerAdapter<PlayListItemDTO, ViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull final ViewHolder holder, int position, @NonNull final PlayListItemDTO model) {
                holder.setTitle(model.getTitle());
                holder.setNickname(model.getNickname());
                holder.setImage(model.getImage());
                holder.setUserMusicNum(model.getUserMusicNum());

                holder.delBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        final DatabaseReference reference = database.getReference("Playlist").child(uid);
                        reference.child(holder.userMusicNum).setValue(null);
                    }
                });

                View.OnClickListener soundLoad = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference reference = database.getReference("AllMusic").child(holder.userMusicNum).child("sound");
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                final Iterator<DataSnapshot> child = snapshot.getChildren().iterator();
                                int i = 0;
                                while (child.hasNext()) {
                                    nodeData[i++] = child.next().getValue().toString();
                                }
                                Intent intent = new Intent();
                                intent.putExtra("node", nodeData);
                                intent.putExtra("title", model.getTitle());
                                intent.putExtra("producer", model.getNickname());
                                intent.putExtra("image", model.getImage());
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                    }
                };

                holder.Image.setOnClickListener(soundLoad);
                holder.Title.setOnClickListener(soundLoad);
                holder.Nickname.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                });
            }

            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.playlist_item, parent, false);

                return new ViewHolder(view);
            }

        };
        recyclerView.setAdapter(adapter);
    }


    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout root;
        public TextView Title;
        public TextView Nickname;
        public ImageView Image;
        public String userMusicNum;
        public TextView delBtn;
        public ViewHolder(View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.list_root);
            Title = itemView.findViewById(R.id.cardview_title);
            Nickname = itemView.findViewById(R.id.cardview_nickname);
            Image = itemView.findViewById(R.id.cardview_picture);
            delBtn = itemView.findViewById(R.id.delButton);
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

}

package com.example.masic;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PlayListRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<PlayListItemDTO> PlayListItemDTOs = new ArrayList<>();
    public PlayListRecyclerViewAdapter() {

/*        PlayListItemDTOs.add(new PlayListItemDTO(R.drawable.albumcover1, "untitled", "nickname1"));
        PlayListItemDTOs.add(new PlayListItemDTO(R.drawable.albumcover2, "secret", "nickname2"));
        PlayListItemDTOs.add(new PlayListItemDTO(R.drawable.albumcover3, "when we were young", "nickname3"));
        PlayListItemDTOs.add(new PlayListItemDTO(R.drawable.albumcover4, "Your man", "nickname4"));

 */
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //XML 세팅
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_item,parent,false);
        return new RowCell(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        //아이템 세팅
//        ((RowCell)holder).imageView.setImageResource(PlayListItemDTOs.get(position).imageview);
        ((RowCell)holder).title.setText(PlayListItemDTOs.get(position).title);
        ((RowCell)holder).nickname.setText(PlayListItemDTOs.get(position).nickname);
    }

    @Override
    public int getItemCount() {
        //이미지 카운터
        return PlayListItemDTOs.size();
    }

    private class RowCell extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView title;
        public TextView nickname;
        public RowCell(View view) {
            super(view);
            imageView = (ImageView)view.findViewById(R.id.cardview_picture);
            title = (TextView)view.findViewById(R.id.cardview_title);
            nickname = (TextView)view.findViewById(R.id.cardview_nickname);
        }
    }
}

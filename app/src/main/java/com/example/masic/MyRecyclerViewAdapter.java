package com.example.masic;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<CardViewItemDTO> cardViewItemDTOs = new ArrayList<>();
/*    public MyRecyclerViewAdapter(String uid, String image[], int musicNum, String title[], String[] nickname, String[] userMusicNum) {
        for(int i=0; i<musicNum; i++){
            cardViewItemDTOs.add(new CardViewItemDTO(image[i], title[i], nickname[i], userMusicNum[i], uid));
        }
    }
*/
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //XML 세팅
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_item,parent,false);
        return new RowCell(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
    //아이템 세팅
        Picasso.get().load(cardViewItemDTOs.get(position).image).into(((RowCell)holder).imageView);
        ((RowCell)holder).title.setText(cardViewItemDTOs.get(position).title);
        ((RowCell)holder).nickname.setText(cardViewItemDTOs.get(position).nickname);
    }

    @Override
    public int getItemCount() {
        //이미지 카운터
        return cardViewItemDTOs.size();
    }

    public class RowCell extends RecyclerView.ViewHolder {
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

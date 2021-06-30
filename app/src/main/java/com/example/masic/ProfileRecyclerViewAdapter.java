package com.example.masic;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ProfileRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    String uid;
    String musicNum;

    String title;
    String userMusicNum;
    OnItemClick mCallback;


    private ArrayList<CardViewItemDTO> cardViewItemDTOs = new ArrayList<>();
 /*   public ProfileRecyclerViewAdapter(String uid, String image[], int musicNum, String title[], String nickname) {

        //프로필은 한 명의 사용자만 해당하므로 userMusicNum(사용자별 곡 번호)랑 musicNum이랑 같음
        for(int i=0; i<musicNum; i++){
            cardViewItemDTOs.add(new CardViewItemDTO(image[i], title[i], nickname, Integer.toString(musicNum), uid));
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
        Picasso.get().load(cardViewItemDTOs.get(position).image).into(((MyRecyclerViewAdapter.RowCell)holder).imageView);
        ((RowCell)holder).title.setText(cardViewItemDTOs.get(position).title);
        ((RowCell)holder).nickname.setText(cardViewItemDTOs.get(position).nickname);
        title = cardViewItemDTOs.get(position).title;
        userMusicNum=cardViewItemDTOs.get(position).userMusicNum;

    }

    @Override
    public int getItemCount() {
        //이미지 카운터
        return cardViewItemDTOs.size();
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

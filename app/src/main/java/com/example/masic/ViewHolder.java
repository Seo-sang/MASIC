package com.example.masic;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

public class ViewHolder extends RecyclerView.ViewHolder {

    View view;
    String userMusicNum;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        view = itemView;
    }

    public void setdetails(Context context, String title, String nickname, String image, String userMusicNum) {
        TextView Title = view.findViewById(R.id.cardview_title);
        TextView Nickname = view.findViewById(R.id.cardview_nickname);
        ImageView imageView = view.findViewById(R.id.cardview_picture);
        this.userMusicNum = userMusicNum;

        Title.setText(title);
        Nickname.setText(nickname);
        Picasso.get().load(image).into(imageView);

        Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
        itemView.startAnimation(animation);
    }
}

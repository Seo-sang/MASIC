package com.example.masic;

public class CardViewItemDTO {
    public String image;
    public String title;
    public String nickname;
    public String userMusicNum; // 곡 번호
    public String uid; // 사용자 uid
    // 카드뷰에서 아이템 참조해서 곡 재생시킬 때 .child(uid).child(userMusicNum)로 참조하세요.


    public CardViewItemDTO(String image, String title, String nickname, String userMusicNum, String uid) {
        this.image = image;
        this.title = title;
        this.nickname = nickname;
        this.userMusicNum = userMusicNum;
        this.uid = uid;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUserMusicNum() {
        return userMusicNum;
    }

    public void setUserMusicNum(String userMusicNum) {
        this.userMusicNum = userMusicNum;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

}

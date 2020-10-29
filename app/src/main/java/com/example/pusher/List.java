
package com.example.pusher;


public class List {
private  int uid;
    private String nickName;

    private int goodCount;
    private String picUri;
    private String description;
    private String title;
    private int picId;
    private String username;

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public void setGoodCount(int goodCount) {
         this.goodCount = goodCount;
     }
     public int getGoodCount() {
         return goodCount;
     }

    public void setPicUri(String picUri) {
         this.picUri = picUri;
     }
     public String getPicUri() {
         return picUri;
     }

    public void setDescription(String description) {
         this.description = description;
     }
     public String getDescription() {
         return description;
     }

    public void setTitle(String title) {
         this.title = title;
     }
     public String getTitle() {
         return title;
     }

    public void setPicId(int picId) {
         this.picId = picId;
     }
     public int getPicId() {
         return picId;
     }

    public void setUsername(String username) {
         this.username = username;
     }
     public String getUsername() {
         return username;
     }

}
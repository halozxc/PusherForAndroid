package com.example.pusher;


import java.util.ArrayList;

public class ImagePage {

    private int code;
    private String msg;
    private com.example.pusher.Data  data;
    public void setCode(int code) {
        this.code = code;
    }
    public int getCode() {
        return code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
    public String getMsg() {
        return msg;
    }

    public void setData(com.example.pusher.Data data) {
        this.data = data;
    }
    public Data getData() {
        return data;
    }

}
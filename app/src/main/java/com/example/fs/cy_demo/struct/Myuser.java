package com.example.fs.cy_demo.struct;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;


public class Myuser extends BmobObject {
    private String user_name;
    private String user_password;
    private BmobFile user_head;
    private String user_phone;
    private int user_age;
    private int post_num;
    private String user_id;
    private String user_sex;
    private String user_qq;
    private String user_wx;
    private String user_head_url;
    private String user_sign;


    public String getUser_sign() {
        return user_sign;
    }

    public void setUser_sign(String user_sign) {
        this.user_sign = user_sign;
    }

    public String getUser_qq() {
        return user_qq;
    }

    public void setUser_qq(String user_qq) {
        this.user_qq = user_qq;
    }

    public String getUser_wx() {
        return user_wx;
    }

    public void setUser_wx(String user_wx) {
        this.user_wx = user_wx;
    }

    public String getUser_head_url() {
        return user_head_url;
    }

    public void setUser_head_url(String user_head_url) {
        this.user_head_url = user_head_url;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_password() {
        return user_password;
    }

    public void setUser_password(String user_password) {
        this.user_password = user_password;
    }

    public BmobFile getUser_head() {
        return user_head;
    }

    public void setUser_head(BmobFile user_head) {
        this.user_head = user_head;
    }

    public String getUser_phone() {
        return user_phone;
    }

    public void setUser_phone(String user_phone) {
        this.user_phone = user_phone;
    }

    public int getUser_age() {
        return user_age;
    }

    public void setUser_age(int user_age) {
        this.user_age = user_age;
    }

    public int getPost_num() {
        return post_num;
    }

    public void setPost_num(int post_num) {
        this.post_num = post_num;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }


    public String getUser_sex() {
        return user_sex;
    }

    public void setUser_sex(String user_sex) {
        this.user_sex = user_sex;
    }
}

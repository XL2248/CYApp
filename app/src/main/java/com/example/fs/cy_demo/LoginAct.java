package com.example.fs.cy_demo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fs.cy_demo.struct.Myuser;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
public class LoginAct extends Activity {
    private EditText et_name,et_password;
    private Button bt_login,bt_reg;
    private String name,password;
    private SharedPreferences sp;
    private Myuser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        Bmob.initialize(this, "e3261c1decdbfe7e562b5d0ba6884245");
        sp=this.getSharedPreferences("user_info", Context.MODE_PRIVATE);
        initview();
        bt_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginAct.this, "注册", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginAct.this,RegisterAct.class));
            }
        });
        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name=et_name.getText().toString();
                password=et_password.getText().toString();
                BmobQuery<Myuser> query=new BmobQuery<Myuser>();
                query.addWhereEqualTo("user_name",name);
                query.findObjects(new FindListener<Myuser>() {
                    @Override
                    public void done(List<Myuser> list, BmobException e) {
                        if (e==null){
                            if (list.size()==0){
                                BmobQuery<Myuser> q=new BmobQuery<Myuser>();
                                q.addWhereEqualTo("user_phone",name);
                                q.findObjects(new FindListener<Myuser>() {
                                    @Override
                                    public void done(List<Myuser> list, BmobException e) {
                                        if (e==null){
                                            if (list.size()==0){
                                                Toast.makeText(LoginAct.this, "无该用户", Toast.LENGTH_SHORT).show();
                                            }
                                            else {
                                                for(Myuser muser:list){
                                                    if (muser.getUser_password().equals(password)){
                                                        user=muser;
                                                        name=user.getUser_name();
                                                        Toast.makeText(LoginAct.this, "登陆成功", Toast.LENGTH_SHORT).show();
                                                        SharedPreferences.Editor editor=sp.edit();
                                                        editor.putBoolean("login",true);
                                                        editor.putString("user_name",name);
                                                        editor.putString("user_password",password);
                                                        editor.commit();
                                                        Bundle bundle=new Bundle();
                                                        bundle.putString("name",name);
                                                        bundle.putInt("number",2);
                                                        Intent intent=new Intent(LoginAct.this,MainAct.class);
                                                        startActivity(intent);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                });
                            }
                            else{
                                for(Myuser muser:list){
                                    if (muser.getUser_password().equals(password)){
                                        user=muser;
                                        name=user.getUser_name();
                                        Toast.makeText(LoginAct.this, "登陆成功", Toast.LENGTH_SHORT).show();
                                        SharedPreferences.Editor editor=sp.edit();
                                        editor.putBoolean("login",true);
                                        editor.putString("user_name",name);
                                        editor.putString("user_password",password);
                                        editor.commit();
                                        Bundle bundle=new Bundle();
                                        bundle.putString("name",name);
                                        bundle.putInt("number",2);
                                        Intent intent=new Intent(LoginAct.this,MainAct.class);
                                        startActivity(intent);
                                    }
                                }
                            }
                        }
                    }
                });
            }
        });


    }

    private void initview() {
        user=new Myuser();
        et_name= (EditText) findViewById(R.id.login_et_name);
        et_password= (EditText) findViewById(R.id.login_et_password);
        bt_login= (Button) findViewById(R.id.login_bt_l);
        bt_reg= (Button) findViewById(R.id.login_bt_r);
    }
}

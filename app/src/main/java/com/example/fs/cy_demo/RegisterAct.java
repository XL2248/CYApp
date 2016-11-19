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
import cn.bmob.v3.listener.SaveListener;

public class RegisterAct extends Activity {
    private EditText et_name,et_password;
    private Button bt_reg;
    private Myuser user;
    private String name,password;
    private SharedPreferences sp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        Bmob.initialize(this, "e3261c1decdbfe7e562b5d0ba6884245");
        sp=this.getSharedPreferences("user_info",Context.MODE_PRIVATE);
        sp.getBoolean("login",false);
        initview();
        bt_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(RegisterAct.this, "点击了注册", Toast.LENGTH_SHORT).show();
                name=et_name.getText().toString();
                password=et_password.getText().toString();
                BmobQuery<Myuser> query=new BmobQuery<Myuser>();
                query.addWhereEqualTo("user_name",name);
                query.findObjects(new FindListener<Myuser>() {
                    @Override
                    public void done(List<Myuser> list, BmobException e) {
                        if (e==null){
                            if (list.size()==0){
                                if (password.length()<6){
                                    Toast.makeText(RegisterAct.this, "密码过短", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    user.setUser_name(name);
                                    user.setUser_password(password);
                                    user.save(new SaveListener<String>() {
                                        @Override
                                        public void done(String s, BmobException e) {
                                            if (e==null){
                                                Toast.makeText(RegisterAct.this, "注册成功", Toast.LENGTH_SHORT).show();
                                                SharedPreferences.Editor editor=sp.edit();
                                                editor.putBoolean("login",true);
                                                editor.putString("user_name",name);
                                                editor.putString("user_password",password);
                                                editor.commit();
                                                Bundle bundle=new Bundle();
                                                bundle.putString("name",name);
                                                bundle.putInt("number",2);
                                                Intent intent=new Intent(RegisterAct.this,MainAct.class);
                                                intent.putExtras(bundle);
                                                startActivity(intent);
                                            }
                                        }
                                    });
                                }
                            }
                            else{
                                Toast.makeText(RegisterAct.this, "该昵称已被占用", Toast.LENGTH_SHORT).show();
                                et_name.setText("");
                            }
                        }
                    }
                });




            }
        });

    }
    private void initview() {
        user=new Myuser();
        et_name= (EditText) findViewById(R.id.reg_et_name);
        et_password= (EditText) findViewById(R.id.reg_et_password);
        bt_reg= (Button) findViewById(R.id.reg_bt);
    }
}

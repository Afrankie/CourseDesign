package com.example.coursedesign;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.net.HttpURLConnection;
import java.util.Timer;
import java.util.TimerTask;

public class MyLoginActivity extends AppCompatActivity {

    EditText username;
    EditText password;
    Button button;
    static Server server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_login);
        username=findViewById(R.id.id);
        password=findViewById(R.id.pw);
        button=findViewById(R.id.login_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final String id_str = username.getText().toString();
                        final String pw_str = password.getText().toString();
                        server=new Server();
                        Utils utils = new Utils();
                        utils.passLoginData(id_str,pw_str);
                        final String string =server.sendPostMessage(utils.getPostData(),"gb2312");
                        if(Server.reponse_code== HttpURLConnection.HTTP_OK){
                            if(!string.contains("top.location.href='/sise/index.jsp'")){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Stuff that updates the UI
                                        Toast.makeText(MyLoginActivity.this,"用户名或密码错误",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }else{
                                final String html = server.getRawCurriculumData("gb2312");
                                server.parseRawCurriculumData(html);
                                server.parseRawCurriculumData2(html);
                                //第几周
                                Timer timer = new Timer(true);
                                TimerTask timerTask = new TimerTask() {
                                    @Override
                                    public void run() {
                                        String nowWeekNumStr = server.getNowWeek(html);
                                        CourseData.weekNumStr=nowWeekNumStr;
                                        String week = "第"+nowWeekNumStr+"周";
                                        CourseData.week=week;
                                    }
                                };
                                timer.schedule(timerTask,0,1000*60*60*12);
                                Intent intent = new Intent(MyLoginActivity.this,IndexActivity.class);
                                startActivity(intent);
                            }
                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // Stuff that updates the UI
                                    Toast.makeText(MyLoginActivity.this,"服务器不可访问",Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }
                }).start();
            }
        });

        Button button = findViewById(R.id.root);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        server=new Server();
                        Utils utils = new Utils();
                        final String string =server.sendPostMessage(utils.getPostData(),"gb2312");
                        server.parseRawCurriculumData(server.getRawCurriculumData("gb2312"));
                        server.parseRawCurriculumData2(server.getRawCurriculumData("gb2312"));
                        Intent intent = new Intent(MyLoginActivity.this,TestActivity.class);
                        startActivity(intent);
                    }
                }).start();
            }
        });
    }
}

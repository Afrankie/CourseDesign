package com.example.coursedesign;

import android.app.AlertDialog;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Random;

public class CourseTableActivity extends AppCompatActivity {
    HashMap<String, String> course_map;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_table);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    course_map = CourseData.map;
                    for (int i = 1; i <= 16; i += 2) {
                        String row_id = "r";
                        String marks = String.valueOf(i) + String.valueOf(i + 1);
                        row_id += marks;
                        Resources resources = getResources();
                        int aapt_id = resources.getIdentifier(row_id, "id", getPackageName());
                        final LinearLayout linearLayout = findViewById(aapt_id);
                        for (int j = 1; j <= 5; j++) {
                            final String course_id = "c" + String.valueOf(marks) + String.valueOf(j);
                            final TextView course_view = (TextView) LayoutInflater.from(CourseTableActivity.this)
                                    .inflate(R.layout.course_layout, linearLayout, false);
                            int aapt_id1 = getResources().getIdentifier(course_id, "id", getPackageName());
                            course_view.setId(aapt_id1);
//                            course_view.setWidth(dip2px(R.dimen.block_width));
                            Boolean course_exist = false;
                            if (course_map.containsKey(course_id)) {
                                course_exist = true;
                                Random random = new Random();
                                int r = random.nextInt(256);
                                int g = random.nextInt(256);
                                int b = random.nextInt(256);
                                course_view.setBackgroundColor(Color.rgb(r, g, b));
                                course_view.getBackground().setAlpha(80);
                                course_view.setText(course_map.get(course_id));
                            }
                            //color
                            course_view.setClickable(true);
                            final Boolean finalCourse_exist = course_exist;
                            course_view.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (finalCourse_exist) {
                                        AlertDialog builder = new AlertDialog.Builder(CourseTableActivity.this).create();
                                        builder.show();
                                        Window win = builder.getWindow();
                                        // 设置弹出对话框的布局
                                        win.setContentView(R.layout.dialog_layout);
                                        win.getDecorView().setBackgroundResource(android.R.color.transparent);
                                        TextView course_view = win.findViewById(R.id.dialog_view);
                                        course_view.setBackground(findViewById(v.getId()).getBackground());
                                        course_view.setText(course_map.get(course_id));
                                        builder.setView(course_view);
                                    }
                                }
                            });
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // Stuff that updates the UI
                                    TextView dayView=null;
                                    int dayOfWeek =IndexActivity.dayOfWeek;
                                    if(dayOfWeek==1){
                                        dayView=findViewById(R.id.w1);
                                    }else if(dayOfWeek==2){
                                        dayView=findViewById(R.id.w2);
                                    }else if (dayOfWeek==3){
                                        dayView=findViewById(R.id.w3);
                                    }else if (dayOfWeek==4){
                                        dayView=findViewById(R.id.w4);
                                    }else if (dayOfWeek==5){
                                        dayView=findViewById(R.id.w5);
                                    }
                                    Random random = new Random();
                                    int r = random.nextInt(256);
                                    int g = random.nextInt(256);
                                    int b = random.nextInt(256);
                                    dayView.setBackgroundColor(Color.rgb(r, g, b));
                                    dayView.getBackground().setAlpha(80);
                                    TextView textView = findViewById(R.id.weekTextView);
                                    textView.setText(CourseData.week);
                                    linearLayout.addView(course_view);
                                }
                            });
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }



}

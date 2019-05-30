package com.example.coursedesign;

import android.os.Build;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class PersonalInfoActivity extends AppCompatActivity {

    Server server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        server = MyLoginActivity.server;
//        String personal_page_html = server.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String post_data = new Utils().getPostData();
                    String server_response = server.sendPostMessage(post_data, "gb2312");
                    final String my_info_page_html = server.getRawPersonalData("gb2312");
                    final LinearLayout linearLayout = findViewById(R.id.table);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Stuff that updates the UI
                            LinearLayout linearLayout1 = new LinearLayout(PersonalInfoActivity.this);
                            TextView textView1 = new TextView(PersonalInfoActivity.this);
                            textView1.setBackgroundResource(R.color.table_header);
                            textView1.setTextSize(25);
                            textView1.setText("基本信息");
                            textView1.setGravity(Gravity.CENTER);
                            textView1.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                            linearLayout1.addView(textView1);
                            linearLayout.addView(linearLayout1, new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
                            ));
                            Document document = Jsoup.parse(my_info_page_html);
                            Element root_element = document.selectFirst("span:contains(学号)");
                            Element tr_element = root_element.parent().parent();
                            fillTableContent(tr_element, linearLayout, 2, 5);
                            LinearLayout linearLayout2 = new LinearLayout(PersonalInfoActivity.this);
                            TextView textView2 = new TextView(PersonalInfoActivity.this);
                            textView2.setBackgroundResource(R.color.table_header);
                            textView2.setTextSize(25);
                            textView2.setText("学分概况");
                            textView2.setGravity(Gravity.CENTER);
                            textView2.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
                            linearLayout2.addView(textView2);
                            linearLayout.addView(linearLayout2, new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
                            ));
                            Element root_element1 = document.selectFirst("b:contains(必修课的总学分)");
                            Element tr_element1 = root_element1.parent().parent().parent().parent();
                            fillTableContent(tr_element1, linearLayout, 5, 2);
                            Element root_element2 = document.selectFirst("b:contains(选修课已修学分)");
                            Element tr_element2=root_element2.parent().parent().parent().parent();
                            fillTableContent(tr_element2,linearLayout,5,2);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public void fillTableContent(Element tr_element, LinearLayout linearLayout, int weight1, int weight2) {
        Elements root_element2_siblings = tr_element.siblingElements();
        int i = 1;
        int j = 1;
        for (Element child_root2 : tr_element.children()) {
            if (!child_root2.text().isEmpty()) {
                if (i % 2 != 0) {
                    i++;
                    fillTextView(child_root2, linearLayout, j, weight1, weight2);
                } else {
                    i++;
                    j++;
                }
            }
        }
        i = 1;
        j = 1;
        for (Element sibling_root2 : root_element2_siblings) {
            Elements child_sibling_root2 = sibling_root2.children();
            for (Element content2 : child_sibling_root2) {
                if (!content2.text().isEmpty()) {
                    if (i % 2 != 0) {
                        i++;
                        fillTextView(content2, linearLayout, j, weight1, weight2);
                    } else {
                        i++;
                        j++;
                    }
                }
            }
        }
    }

    public void fillTextView(Element element, LinearLayout linearLayout, int color_ctrl, int weight1, int weight2) {
        Element element1 = element.nextElementSibling();
//        System.out.println(element1.text());
        LinearLayout linearLayout1 = new LinearLayout(PersonalInfoActivity.this);
        TextView textView1 = new TextView(PersonalInfoActivity.this);
        TextView textView2 = new TextView(PersonalInfoActivity.this);
        textView1.setText(element.text());
//        if (element1 == null) {
//            textView2.setText("");
//        } else {
//
//        }
        textView2.setText(element1.text());
        if (color_ctrl % 2 != 0) {
            textView1.setBackgroundResource(R.color.table_odd);
            textView2.setBackgroundResource(R.color.table_odd);
        } else {
            textView1.setBackgroundResource(R.color.table_even);
            textView2.setBackgroundResource(R.color.table_even);
        }
        textView1.setTextSize(20);
        textView2.setTextSize(20);
        textView1.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, weight1));
        textView2.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, weight2));
        textView1.setGravity(Gravity.RIGHT);
        linearLayout1.addView(textView1);
        linearLayout1.addView(textView2);
        linearLayout.addView(linearLayout1, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ));
    }


}

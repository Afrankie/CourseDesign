package com.example.coursedesign;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ExamActivity extends AppCompatActivity {

    Server server;
    String EXAMTABLE_PATH = "http://class.sise.com.cn:7001/SISEWeb/pub/exam/studentexamAction.do?method=doMain&studentid=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);
        server=MyLoginActivity.server;
        new Thread(new Runnable() {
            @Override
            public void run() {
                EXAMTABLE_PATH+=server.getStudentId("gb2312",2);
                final String data = server.getServerHtmlByPath(EXAMTABLE_PATH,"gb2312");
                Document doc = Jsoup.parse(data);
//                System.out.println(document.text());
                final LinearLayout linearLayout = findViewById(R.id.exam_linearlayout);
                final Element element = doc.selectFirst("th:contains(课程代码)").parent().parent().nextElementSibling();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Stuff that updates the UI
                        fillTable(element,linearLayout,2);
                    }
                });

            }
        }).start();

    }

    public void fillTable(org.jsoup.nodes.Element root, LinearLayout linearLayout,int mode){
         Elements siblings =root.children();
//        System.out.println(root.text());
         String prefix = "";
         if(mode==1){
             prefix="th";
         }else if(mode==2){
             prefix="td";
         }
         int i=1;
         for(org.jsoup.nodes.Element element:siblings){
             LayoutInflater inflater = ExamActivity.this.getLayoutInflater();
             View linearLayout1 = inflater.inflate(R.layout.exam_layout,null);
             String name = element.select(prefix+":nth-child(2)").text();
             String date = element.select(prefix+":nth-child(3)").text();
             String time = element.select(prefix+":nth-child(4)").text();
             String place = element.select(prefix+":nth-child(6)").text();
             String position = element.select(prefix+":nth-child(7)").text();
//             System.out.println("!!!!!!!!!!"+name+date+place+position);
             String time1=time.substring(0,5);
             String time2=time.substring(8,14);
             time=time1+time2;
             TextView textView1 = linearLayout1.findViewById(R.id.name);
             TextView textView2 = linearLayout1.findViewById(R.id.date);
             TextView textView3 = linearLayout1.findViewById(R.id.place);
             TextView textView4 = linearLayout1.findViewById(R.id.position);
             TextView textView5 = linearLayout1.findViewById(R.id.time);
             textView1.setText(name);
             textView2.setText(date);
             textView3.setText(place);
             textView4.setText(position);
             textView5.setText(time);
             if(i%2!=0){
                 linearLayout1.setBackgroundResource(R.color.table_odd);
             }else{
                 linearLayout1.setBackgroundResource(R.color.table_even);
             }
             i++;
             linearLayout.addView(linearLayout1,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
         }
    }
}

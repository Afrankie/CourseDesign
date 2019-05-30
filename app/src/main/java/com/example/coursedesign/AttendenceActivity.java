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

public class AttendenceActivity extends AppCompatActivity {

    Server server;
    String studentid;
    String gzcode;
    String ATTENDENTENCE_PATH =
            "http://class.sise.com.cn:7001/SISEWeb/pub/studentstatus/attendance/studentAttendanceViewAction.do?method=doMain&studentID=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendence);
        new Thread(new Runnable() {
            @Override
            public void run() {
                server=MyLoginActivity.server;
                final LinearLayout linearLayout = findViewById(R.id.attendencelayout);
                studentid=server.getStudentId("gb2312",1);
                gzcode=server.getGzcode("gb2312");
                ATTENDENTENCE_PATH+=studentid+"&gzcode="+gzcode;
                String data = server.getServerHtmlByPath(ATTENDENTENCE_PATH,"gb2312");
                Document doc = Jsoup.parse(data);
                final Element root1 = doc.selectFirst("th:contains(课程编号)").parent().parent();
                final Element root2 = root1.nextElementSibling();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Stuff that updates the UI
                        fillTable(root1,linearLayout,1);
                        fillTable(root2,linearLayout,2);
                    }
                });

            }
        }).start();
    }

    public void fillTable(org.jsoup.nodes.Element root, LinearLayout linearLayout, int mode){
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
            LayoutInflater inflater = AttendenceActivity.this.getLayoutInflater();
            View linearLayout1 = inflater.inflate(R.layout.attendence_layout,null);
            String name = element.select(prefix+":nth-child(2)").text();
            String desc = element.select(prefix+":nth-child(3)").text();
//             System.out.println("!!!!!!!!!!"+name+date+place+position);
            TextView textView1 = linearLayout1.findViewById(R.id.name);
            TextView textView2 = linearLayout1.findViewById(R.id.desc);
            textView1.setText(name);
            textView2.setText(desc);
            if(i%2!=0){
                if(mode==1){
                    linearLayout1.setBackgroundResource(R.color.table_even);
                }else {
                    linearLayout1.setBackgroundResource(R.color.table_odd);
                }
            }else{
                linearLayout1.setBackgroundResource(R.color.table_even);
            }
            i++;
            linearLayout.addView(linearLayout1,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
    }
}

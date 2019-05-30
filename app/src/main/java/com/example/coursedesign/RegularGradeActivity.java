package com.example.coursedesign;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.weigan.loopview.LoopView;
import com.weigan.loopview.OnItemSelectedListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegularGradeActivity extends AppCompatActivity {

    private ExpandableListView myExpandableListView;
    private ExPandableListViewAdapterForR adapter;
    private ArrayList<FatherData> datas;

    Server server;
    String schoolyear;
    String semester;
    ArrayList<String> schoolyears;
    ArrayList<String> semesters;
    Map<Integer, String> map1;
    Map<Integer, String> map2;
    Map<String, Integer> map;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regular_grade);
        initView();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                server=MyLoginActivity.server;
                setData();
                schoolyears = new ArrayList<>();
                semesters = new ArrayList<>();
                map1 = new HashMap<>();
                map2 = new HashMap<>();
                map= new HashMap<>();
                schoolyear = "";
                semester = "";
                final String regular_grade_page_html = server.getServerHtmlByPath("http://class.sise.com.cn:7001/sise/module/commonresult/index.jsp", "gb2312");
//                System.out.println(regular_grade_page_html);
                fillExTextView(getAllRegularGradeData(regular_grade_page_html, "2018", "2"));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Stuff that updates the UI
                        setAdapter();
                    }
                });
                Document document = Jsoup.parse(regular_grade_page_html);
                Element root1 = document.selectFirst("select[name=\"schoolyear\"]");
                Element root2 = document.selectFirst("select[name=\"semester\"]");
                int i = 0;
                for (Element element1 : root1.children()) {
                    String string = element1.text();
                    map1.put(i, string);
                    map.put(string,i);
                    System.out.println(i + "  " + string);
                    schoolyears.add(string);
                    i++;
                }
                i = 0;
                for (Element element2 : root2.children()) {
                    map2.put(i, String.valueOf(i + 1));
                    map.put(String.valueOf(i + 1),i);
                    semesters.add(element2.text());
                    i++;
                }
                schoolyear=root1.selectFirst("option[selected]").text();
                semester=root2.selectFirst("option[selected]").attr("value");
                findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialog(schoolyears, semesters);
                    }
                });
            }
        });
        thread.start();
    }

    private void setAdapter() {
        if (adapter == null) {
            adapter = new ExPandableListViewAdapterForR(RegularGradeActivity.this, datas);
            myExpandableListView.setAdapter(adapter);
//            myExpandableListView.setGroupIndicator(null);
        } else {
            adapter.flashData(datas);
        }
    }

    private void setData() {
        if (datas == null) {
            datas = new ArrayList<>();
        }
    }

    private void initView() {
        myExpandableListView = findViewById(R.id.regular_grade_expandablelist);

    }

    public List<String> getAllRegularGradeData(String html, String schoolyear, String semester) {
        String PATH = "http://class.sise.com.cn:7001/sise/module/commonresult/showdetails.";
        String regex = "jsp\\?([^']+)";
        Matcher matcher = Pattern.compile(regex).matcher(html);
        List<String> list = new ArrayList<>();
        List<String> data = new ArrayList<>();
        while (matcher.find()) {
            list.add(matcher.group());
        }
//        System.out.println(list);
        for (int i = 0; i < list.size(); i++) {
            String url = PATH + list.get(i);
//            System.out.println(url);
            String regular_grade_html = server.getServerHtmlByPath(url, "gb2312");
//            System.out.println("!!!!!!!!!!!!!!!!!!!!-"+regular_grade_html);
            data.add(regular_grade_html);
//            System.out.println(regular_grade_html);
        }
//        System.out.println(data);
        return data;
    }

    public void fillExTextView(List<String> list) {
        for (int i = 0; i < list.size(); i++) {
            String data = list.get(i);
            FatherData fatherData = new FatherData();
            ChildrenData childrenData;
            ArrayList<ChildrenData> itemList = new ArrayList<>();
            Document document = Jsoup.parse(data);
            Element name = document.selectFirst("td[align=\"left\"]");
            String title = name.text();
//            System.out.println(title);
            String tmp[] = title.split(" ");
            title = "";
            int j;
            for (j = 1; j < tmp.length; j++) {
                title += tmp[j] + " ";
            }
            fatherData.setTitle(title);
            Element root1 = document.selectFirst("strong:contains(平时成绩来源)");
            Element table1 = root1.parent().parent().parent().parent();
            Elements sibling = table1.children();
            j = 1;
            for (Element element : sibling) {
                Elements tds = element.children();
                childrenData = new ChildrenData();
//                System.out.println(table1.text());
                childrenData.setRegularName(tds.select("td:nth-child(1)").first().text());
                childrenData.setRegularHighest(tds.select("td:nth-child(3)").first().text());
                childrenData.setRegularGrade(tds.select("td:nth-child(4)").first().text());
                childrenData.setColorCtrlNum(j);
                itemList.add(childrenData);
                j++;
            }
            Element root2 = document.selectFirst("span:contains(总平时成绩)");
            Element table2 = root2.parent().parent();
            Elements tds2 = table2.children();
            ChildrenData childrenData1 = new ChildrenData();
            childrenData1.setRegularName(tds2.select("td:nth-child(1)").first().text());
            childrenData1.setRegularHighest(tds2.select("td:nth-child(3)").first().text());
            childrenData1.setRegularGrade(tds2.select("td:nth-child(4)").first().text());
            childrenData1.setColorCtrlNum(j);
            itemList.add(childrenData1);
            fatherData.setList(itemList);
            datas.add(fatherData);
        }
    }

    private void showDialog(final ArrayList<String> schoolyears, ArrayList<String> semesters) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_view, null);
        dialogBuilder.setView(dialogView);
        LoopView loopView1 = dialogView.findViewById(R.id.loopView1);
        LoopView loopView2 = dialogView.findViewById(R.id.loopView2);
//        System.out.println(map);
//        System.out.println(map.get(schoolyear)+"~~~~~~``"+map.get(semester));
//        System.out.println(schoolyear+"!!!!!!!!!!!!"+semester);

        // 滚动监听
        loopView1.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                schoolyear = map1.get(index);
//                Toast.makeText(RegularGradeActivity.this,schoolyear,Toast.LENGTH_SHORT).show();
            }
        });
        loopView2.setListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                semester = map2.get(index);
//                Toast.makeText(RegularGradeActivity.this,semester,Toast.LENGTH_SHORT).show();
            }
        });
        // 设置原始数据
        loopView1.setItems(schoolyears);
        loopView2.setItems(semesters);
        loopView1.setInitPosition(map.get(schoolyear));
        loopView2.setInitPosition(map.get(semester));
        final AlertDialog alertDialog = dialogBuilder.create();
        Button button = dialogView.findViewById(R.id.dialog_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                change(schoolyear,semester);
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    public void change(final String schoolyear, final String semester) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String PATH = "http://class.sise.com.cn:7001/sise/module/commonresult/index.jsp?";
                PATH += "schoolyear=" + schoolyear + "&semester=" + semester;
                datas.clear();
                final String regular_grade_page_html = server.getServerHtmlByPath(PATH, "gb2312");
//                System.out.println("!!!!!!!!!!!!!!!!!!!!!!"+PATH);
//                System.out.println("~~~~~~~~~~~~~~`"+regular_grade_page_html);
                fillExTextView(getAllRegularGradeData(regular_grade_page_html, schoolyear, semester));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Stuff that updates the UI
                        if(datas.size()==0){
                            Toast.makeText(RegularGradeActivity.this,"无记录！",Toast.LENGTH_SHORT).show();
                        }
                        setAdapter();
                    }
                });
            }
        });
        thread.start();

    }

}

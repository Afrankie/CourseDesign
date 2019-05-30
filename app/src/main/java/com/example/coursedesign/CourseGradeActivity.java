package com.example.coursedesign;

import android.icu.util.RangeValueIterator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ExpandableListView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class CourseGradeActivity extends AppCompatActivity {

    private ExpandableListView myExpandableListView;
    private ExPandableListViewAdapter adapter;
    private ArrayList<FatherData> datas;

    Server server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_grade);
        initView();
        setData();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                server = MyLoginActivity.server;
                final String my_info_page_html = server.getRawPersonalData("gb2312");
                Document document = Jsoup.parse(my_info_page_html);
                Element root_element = document.selectFirst("td:contains(第一学期)");
                Element tr_element = root_element.parent();
                fillExTextView(tr_element);
                Element root_element2 = document.select("th:contains(课程代码)").last();
                Element table_element = root_element2.parent().parent().nextElementSibling();
//                System.out.println(table_element1.nextElementSiblings().text());
                fillExTextView2(table_element);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Stuff that updates the UI
                        setAdapter();
                    }
                });

            }
        });
        thread.start();

    }

    private void setAdapter() {
        if (adapter == null) {
            adapter = new ExPandableListViewAdapter(CourseGradeActivity.this, datas);
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
        myExpandableListView = findViewById(R.id.course_grade_expandablelist);

    }

    public void fillExTextView(Element tr_element){
        Elements sibling_element = tr_element.siblingElements();
        FatherData fatherData=new FatherData();
        ChildrenData childrenData = new ChildrenData();
        ArrayList<ChildrenData> itemList = new ArrayList<>();
        boolean ctrl;
        int color_ctrl_num = 1;
        //必修课
        //表格第一行需要手动填充
        Elements first_child_of_root = tr_element.children();
//        System.out.println(first_child_of_root.text());
        childrenData.setCourseName(first_child_of_root.select("td:nth-child(3)").text()+" ：");
        childrenData.setCourseGrade(" "+first_child_of_root.select("td:nth-child(9)").text());
        childrenData.setColorCtrlNum(color_ctrl_num);
        color_ctrl_num++;
        itemList.add(childrenData);
        fatherData.setList(itemList);
        fatherData.setTitle("第一学期");
        datas.add(fatherData);
        for(Element element:sibling_element){
            ctrl = false;
            Elements child = element.children();
            String title = child.select("td:nth-child(1)").text();
            if(!title.isEmpty()){
                ctrl=true;
                itemList = new ArrayList<>();
                fatherData = new FatherData();
                fatherData.setTitle(title);
            }
            childrenData = new ChildrenData();
            childrenData.setCourseName(child.select("td:nth-child(3)").text()+" ：");
            childrenData.setCourseGrade(" "+child.select("td:nth-child(9)").text());
            childrenData.setColorCtrlNum(color_ctrl_num);
            itemList.add(childrenData);
            fatherData.setList(itemList);
            if(ctrl){
                datas.add(fatherData);
            }
            color_ctrl_num++;
        }
    }
    public void fillExTextView2(Element table_element){
        System.out.println(table_element.text());
        Element tbody_element = table_element.selectFirst("tbody");
        Elements tr_elements = tbody_element.children();
        FatherData fatherData=new FatherData();
        ChildrenData childrenData;
        ArrayList<ChildrenData> itemList = new ArrayList<>();
        fatherData.setTitle("选修课程");
        int color_ctrl_num = 1;
        for(Element tr_element:tr_elements){
            Elements child = tr_element.children();
            childrenData = new ChildrenData();
            childrenData.setCourseName(child.select("td:nth-child(2)").text()+" ：");
            childrenData.setCourseGrade(" "+child.select("td:nth-child(8)").text());
            childrenData.setColorCtrlNum(color_ctrl_num);
            itemList.add(childrenData);
            fatherData.setList(itemList);
            color_ctrl_num++;
        }
        datas.add(fatherData);
    }

}

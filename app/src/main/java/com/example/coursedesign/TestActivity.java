package com.example.coursedesign;

import java.text.SimpleDateFormat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestActivity extends AppCompatActivity {

    ContentAdapter today_adapter;
    ContentAdapter tomo_adapter;
    ContentAdapter emial_adapter;
    ListView mTodayView;
    ListView mTomorrowView;
    ListView mEmailView;
    List<CardMode> today_datas;
    List<CardMode> tomo_datas;
    List<CardMode> emial_datas;
    HashMap<Integer, String> courseTimeMap;
    int firstDayOfWeek;
    List<Integer> today_course_list;
    List<List<String>> course_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        setData();
        initView();
        today_course_list = new ArrayList<>();
        courseTimeMap = new HashMap<>();
        courseTimeMap.put(1, "9:00-10:20");
        courseTimeMap.put(2, "10:40-12:00");
        courseTimeMap.put(4, "14:00-15:20");
        courseTimeMap.put(5, "15:30-17:00");
        courseTimeMap.put(6, "17:00-18:20");
        courseTimeMap.put(7, "19:00-20:20");
        courseTimeMap.put(8, "20:30-21:50");
        firstDayOfWeek = getDayOfWeek();
        course_data = CourseData.courselist;
        int dayOfWeek = getDayOfWeek();
        String nowWeek = CourseData.week;
        nowWeek = nowWeek.substring(1, nowWeek.length() - 1);
        List<String> list = course_data.get(dayOfWeek - 1);
        int start = getNowTimeZone();
        if (start != 0 && start != -1) {
            for (int i = start - 1; i < list.size(); i++) {
                String course_info = list.get(i);
                System.out.println(course_info);
                if (!course_info.equals("")) {
                    if (course_info.contains(nowWeek)) {
                        String result = parseCourseInfoToResult(course_info, nowWeek);
                        String time = courseTimeMap.get(i + 1);
                        String string = time + " " + result;
                        CardMode cardMode = new CardMode();
                        cardMode.setContent(string);
                        today_datas.add(cardMode);
                        today_course_list.add(i + 1);
                    }
                }
            }
        }
        refreshTomoCard(dayOfWeek);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView textView = findViewById(R.id.today_num);
                textView.setText(String.valueOf(today_datas.size()));
                // Stuff that updates the UI
                setAdapter();
                setTomoAdapter();
            }
        });
        Timer today_timer = new Timer(true);
        TimerTask today_task = new TimerTask() {
            public void run() {
                if (today_datas.size() > 0) {
                    int nowTimeZone = getNowTimeZone();
                    int tmp = today_course_list.get(0);
                    if (nowTimeZone > tmp) {
                        today_course_list.remove(0);
                        today_datas.remove(0);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TextView textView = findViewById(R.id.today_num);
                                textView.setText(String.valueOf(today_datas.size()));
                                today_adapter.flashData(today_datas);
                            }
                        });
                    }
                }
//                if(today_datas.size()>0){
//                    today_course_list.remove(0);
//                    today_datas.remove(0);
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            today_adapter.flashData(today_datas);
//                            TextView textView = findViewById(R.id.today_num);
//                            textView.setText(String.valueOf(today_datas.size()));
//                        }
//                    });
//                }
            }
        };
        today_timer.schedule(today_task, 0, 1000 * 60 * 5);
        Timer tomo_timer = new Timer(true);
        TimerTask tomo_task = new TimerTask() {
            @Override
            public void run() {
                int dayOfWeek = getDayOfWeek();
                if (dayOfWeek != firstDayOfWeek) {
                    System.out.println(dayOfWeek + "???" + firstDayOfWeek);
                    firstDayOfWeek = dayOfWeek;
                    tomo_datas.clear();
                    refreshTomoCard(dayOfWeek);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tomo_adapter.flashData(tomo_datas);
                            TextView textView = findViewById(R.id.tomo_num);
                            textView.setText(String.valueOf(tomo_datas.size()));
                        }
                    });
                }
            }
        };
        tomo_timer.schedule(tomo_task, 0, 1000 * 60 * 30);
    }

    private void setAdapter() {
        if (today_adapter == null) {
            today_adapter = new ContentAdapter(TestActivity.this, today_datas);
            mTodayView.setAdapter(today_adapter);
//            myExpandableListView.setGroupIndicator(null);
        } else {
            today_adapter.flashData(today_datas);
        }
    }

    private void setTomoAdapter() {
        if (tomo_adapter == null) {
            tomo_adapter = new ContentAdapter(TestActivity.this, tomo_datas);
            mTomorrowView.setAdapter(tomo_adapter);
//            myExpandableListView.setGroupIndicator(null);
        } else {
            tomo_adapter.flashData(tomo_datas);
        }
    }

    private void setData() {
        if (today_datas == null) {
            today_datas = new ArrayList<>();
        }
        if (tomo_datas == null) {
            tomo_datas = new ArrayList<>();
        }
        if (emial_datas == null) {
            emial_datas = new ArrayList<>();
        }
    }


    private void initView() {
        mTodayView = TestActivity.this.findViewById(R.id.day_listview);
        mTomorrowView = TestActivity.this.findViewById(R.id.tomorrow_listview);
        mEmailView = TestActivity.this.findViewById(R.id.email_listview);
    }

    public static int getDayOfWeek() {
        Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        calendar.setTime(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (dayOfWeek < 0) dayOfWeek = 0;
        return dayOfWeek;
    }

    public void refreshTomoCard(int dayOfWeek) {

        List<String> list = course_data.get(dayOfWeek);
        for (int i = 0; i < list.size(); i++) {
            String nowWeek = CourseData.weekNumStr;
            String course_info = list.get(i);
            if (!course_info.equals("")) {
                System.out.println("^^%^" + nowWeek);
                if (course_info.contains(nowWeek)) {
                    String result = parseCourseInfoToResult(course_info, nowWeek);
                    String time = courseTimeMap.get(i + 1);
                    String string = time + " " + result;
                    System.out.println("^^%@#@#^" + string);
                    CardMode cardMode = new CardMode();
                    cardMode.setContent(string);
                    tomo_datas.add(cardMode);
                }
            }
        }
    }

    public String parseCourseInfoToResult(String course_info, String nowWeek) {
        String result = "";
        String regex = "\\[(.+?)\\]";
        Matcher matcher = Pattern.compile(regex).matcher(course_info);
        List<String> data = new ArrayList<>();
        while (matcher.find()) {
            data.add(matcher.group());
        }
        String tmp = "";
        String place = "";
        String regex1 = ".+?\\(";
        if (data.size() == 1) {
            tmp = course_info;
            place = data.get(0);
        } else {
            int comma_posi = course_info.indexOf(',');
            int num_posi = course_info.indexOf(nowWeek);
            String[] strings = course_info.split(",");
            if (num_posi > comma_posi) {
                tmp = strings[1];
                place = data.get(1);
            } else {
                tmp = strings[0];
                place = data.get(0);
            }
        }
        Matcher matcher1 = Pattern.compile(regex1).matcher(tmp);
        if (matcher1.find()) {
            result = matcher1.group(0);
            result = result.substring(0, result.length() - 1) + " " + place;
        }
        return result;
    }

    public static boolean belongCalendar(Date nowTime, Date beginTime, Date endTime) {
        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);

        Calendar begin = Calendar.getInstance();
        begin.setTime(beginTime);

        Calendar end = Calendar.getInstance();
        end.setTime(endTime);

        if (date.after(begin) && date.before(end)) {
            return true;
        } else {
            return false;
        }
    }

    public int getNowTimeZone() {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");//设置日期格式
        Date now = null;
        Date beginTime = null;
        Date endTime = null;
        courseTimeMap.put(1, "9:00-10:20");
        courseTimeMap.put(2, "10:40-12:00");
        courseTimeMap.put(4, "14:00-15:20");
        courseTimeMap.put(5, "15:30-16:50");
        courseTimeMap.put(6, "17:00-18:20");
        courseTimeMap.put(7, "19:00-20:20");
        courseTimeMap.put(8, "20:30-21:50");
        try {
            now = df.parse(df.format(new Date()));
            if (belongCalendar(now, df.parse("00:00"), df.parse("10:20"))) {
                return 1;
            } else if (belongCalendar(now, df.parse("10:21"), df.parse("12:00"))) {
                return 2;
            } else if (belongCalendar(now, df.parse("12:01"), df.parse("15:20"))) {
                return 4;
            } else if (belongCalendar(now, df.parse("15:21"), df.parse("17:00"))) {
                return 5;
            } else if (belongCalendar(now, df.parse("17:01"), df.parse("18:20"))) {
                return 6;
            } else if (belongCalendar(now, df.parse("18:21"), df.parse("20:20"))) {
                return 7;
            } else if (belongCalendar(now, df.parse("20:21"), df.parse("21:50"))) {
                return 8;
            } else {
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

}

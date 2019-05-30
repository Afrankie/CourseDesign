package com.example.coursedesign;

import java.util.HashMap;
import java.util.List;

public class CourseData {
    public static String week;
    public static String weekNumStr;
    public static String dayOfWeek;
    public static String myname;
    public static List<List<String>> courselist;
    public static HashMap<String,String> map;

    public static List<List<String>> getCourselist() {
        return courselist;
    }

    public static void setCourselist(List<List<String>> courselist) {
        CourseData.courselist = courselist;
    }

    public static String getWeek() {
        return week;
    }

    public static void setWeek(String week) {
        CourseData.week = week;
    }

    public static String getDayOfWeek() {
        return dayOfWeek;
    }

    public static void setDayOfWeek(String dayOfWeek) {
        CourseData.dayOfWeek = dayOfWeek;
    }

    public static String getMyname() {
        return myname;
    }

    public static void setMyname(String myname) {
        CourseData.myname = myname;
    }
}

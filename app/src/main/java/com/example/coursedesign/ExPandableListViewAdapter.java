package com.example.coursedesign;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ExPandableListViewAdapter extends BaseExpandableListAdapter {
    // 定义一个Context
    private Context context;
    // 定义一个LayoutInflater
    private LayoutInflater mInflater;
    // 定义一个List来保存列表数据
    private ArrayList<FatherData> data_list = new ArrayList<>();

    public ExPandableListViewAdapter(Context context, ArrayList<FatherData> data_list) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.data_list = data_list;
    }

    // 刷新数据
    public void flashData(ArrayList<FatherData> datas) {
        this.data_list = datas;
        this.notifyDataSetChanged();
    }


    @Override
    public int getGroupCount() {
        return data_list.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return data_list.get(groupPosition).getList().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return data_list.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return data_list.get(groupPosition).getList().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        HodlerViewFather hodlerViewFather;
        if (convertView == null) {
            hodlerViewFather = new HodlerViewFather();
            convertView = mInflater.inflate(R.layout.activity_course_grade_father, parent, false);
            hodlerViewFather.titlev = (TextView) convertView.findViewById(R.id.course_grade_father_tv);
            // 新建一个TextView对象，用来显示一级标签上的大体描述的信息
            convertView.setTag(hodlerViewFather);
        } else {
            hodlerViewFather = (HodlerViewFather) convertView.getTag();
        }
        /**
         * 设置相应控件的内容
         */
        // 设置标题上的文本信息
        hodlerViewFather.titlev.setText(data_list.get(groupPosition).getTitle());
        // 返回一个布局对象
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        HolderView childrenView;
        if (convertView == null) {
            childrenView = new HolderView();
            // 获取子视图的布局文件
            convertView = mInflater.inflate(R.layout.activity_course_grade_children, parent, false);
            childrenView.nameView =  convertView.findViewById(R.id.course_name);
            childrenView.gradeView = convertView.findViewById(R.id.course_grade);
            // 这个函数是用来将holderview设置标签,相当于缓存在view当中
            convertView.setTag(childrenView);
        } else {
            childrenView = (HolderView) convertView.getTag();
        }

        /**
         * 设置相应控件的内容
         */
        // 设置标题上的文本信息
        childrenView.nameView.setText(data_list.get(groupPosition).getList().get(childPosition).getCourseName());
        // 设置副标题上的文本信息
        childrenView.gradeView.setText(data_list.get(groupPosition).getList().get(childPosition).getCourseGrade());
        //设置背景颜色
        if(data_list.get(groupPosition).getList().get(childPosition).getColorCtrlNum()%2!=0){
            childrenView.nameView.setBackgroundResource(R.color.table_odd);
            childrenView.gradeView.setBackgroundResource(R.color.table_odd);
        }else{
            childrenView.nameView.setBackgroundResource(R.color.table_even);
            childrenView.gradeView.setBackgroundResource(R.color.table_even);
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    private class HolderView {
        public TextView nameView;
        public TextView gradeView;
    }

    private class HodlerViewFather {
        public TextView titlev;
    }
}

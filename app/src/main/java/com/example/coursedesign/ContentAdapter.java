package com.example.coursedesign;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class ContentAdapter extends BaseAdapter {

    private Context context;
    private List<CardMode> list;

    public ContentAdapter(Context context, List<CardMode> list) {
        this.context = context;
        this.list = list;
    }

    public void flashData(List<CardMode> list){
        this.list=list;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHold hold;
        if (convertView == null) {
            hold = new ViewHold();
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.card_item, null);
            convertView.setTag(hold);
        } else {
            hold = (ViewHold) convertView.getTag();
        }

        hold.textView = convertView.findViewById(R.id.content_view);
        hold.textView.setText(list.get(position).getContent());
        return convertView;
    }
    class ViewHold { ;
        public TextView textView;
    }
}

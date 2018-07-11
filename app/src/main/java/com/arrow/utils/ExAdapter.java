package com.arrow.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.arrow.test.R;
import com.arrow.uitest.MainUiActivity;

public class ExAdapter extends BaseExpandableListAdapter{

    private String[] groupData;
    private String[][] childData;

    public ExAdapter(String[] groupData, String[][] childData) {
        this.groupData = groupData;
        this.childData = childData;
    }
    //        获取分组的个数
    @Override
    public int getGroupCount() {
        return groupData.length;
    }
    //        获取指定分组中的子选项的个数
    @Override
    public int getChildrenCount(int i) {
        return childData[i].length;
    }
    //        获取指定的分组数据
    @Override
    public Object getGroup(int i) {
        return groupData.length;
    }
    //        获取指定分组中的指定子选项数据
    @Override
    public Object getChild(int i, int i1) {
        return childData[i][i1];
    }
    //        获取指定分组的ID, 这个ID必须是唯一的
    @Override
    public long getGroupId(int i) {
        return i;
    }
    //        获取子选项的ID, 这个ID必须是唯一的
    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }
    //        分组和子选项是否持有稳定的ID, 就是说底层数据的改变会不会影响到它们。
    @Override
    public boolean hasStableIds() {
        return true;
    }
    //        获取显示指定分组的视图
    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        GroupViewHolder holder;
        if (view == null){
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.ex_item,viewGroup,false);
            holder = new GroupViewHolder();
            holder.groupText = view.findViewById(R.id.item);
            view.setTag(holder);
        }else {
            holder = (GroupViewHolder) view.getTag();
        }
        holder.groupText.setText(groupData[i]);
        return view;
    }
    //        获取显示指定分组中的指定子选项的视图
    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        ChildViewHolder holder;
        if (view == null){
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.ex_item_item,viewGroup,false);
            holder = new ChildViewHolder();
            holder.childText = view.findViewById(R.id.item_item);
            view.setTag(holder);
        }else {
            holder = (ChildViewHolder) view.getTag();
        }
        holder.childText.setText(childData[i][i1]);
        return view;
    }
    //        指定位置上的子元素是否可选中
    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    private static class GroupViewHolder{
        TextView groupText;
    }
    private static class ChildViewHolder{
        TextView childText;
    }
}

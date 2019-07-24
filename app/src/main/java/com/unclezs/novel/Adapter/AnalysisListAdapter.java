package com.unclezs.novel.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.unclezs.novel.Model.Chapter;
import com.unclezs.novel.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Uncle
 * 2019.07.21.
 */
public class AnalysisListAdapter extends BaseAdapter implements View.OnClickListener {
    private Context mContext;
    private List<Chapter> listData;
    private List<Boolean> isChecked;

    public AnalysisListAdapter(Context mContext, List<Chapter> listData,boolean state) {
        this.mContext = mContext;
        this.listData = listData;
        isChecked = new ArrayList<>(listData.size());
        for (int i = 0; i < listData.size(); i++) {
            isChecked.add(state);
        }
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int i) {
        return listData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.analysis_list_item, viewGroup, false);
            holder = new ViewHolder();
            holder.box = view.findViewById(R.id.analysis_list_item_box);
            holder.text = view.findViewById(R.id.analysis_list_item_name);
            holder.text.setTag(i);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.box.setChecked(isChecked.get(i));
        holder.box.setOnClickListener(e -> {
            if (holder.box.isChecked()) {
                isChecked.set(i, true);
            } else {
                isChecked.set(i, false);
            }
        });
        holder.text.setText(listData.get(i).getChapterName());
        return view;
    }

    @Override
    public void onClick(View view) {
        ViewHolder holder = (ViewHolder) view.getTag();
        int index = (int) holder.text.getTag();
        Toast.makeText(mContext, listData.get(index).getChapterUrl(), Toast.LENGTH_SHORT).show();
    }

    public void  selectAll(boolean b){
        if(isChecked==null){
            return;
        }
        for (int i = 0; i < isChecked.size(); i++) {
            isChecked.set(i,b);
        }
        notifyDataSetChanged();
    }
    static class ViewHolder {
        CheckBox box;
        TextView text;
    }

    public List<Boolean> getIsChecked() {
        return isChecked;
    }
}

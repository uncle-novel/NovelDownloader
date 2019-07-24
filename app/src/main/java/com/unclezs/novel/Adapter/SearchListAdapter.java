package com.unclezs.novel.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.unclezs.novel.Activity.AnalysisFragment;
import com.unclezs.novel.Activity.MainActivity;
import com.unclezs.novel.Model.NovelInfo;
import com.unclezs.novel.R;

import java.util.List;

/**
 * 小说搜索结果列表适配器
 * Created by Uncle
 * 2019.07.20.
 */
public class SearchListAdapter extends BaseAdapter {
    private Context mContext;//上下文
    private List<NovelInfo> dataList;

    public SearchListAdapter(Context mContext, List<NovelInfo> dataList) {
        this.mContext = mContext;
        this.dataList = dataList;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int i) {
        return dataList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.list_item, viewGroup, false);
            holder = new ViewHolder();
            holder.title = view.findViewById(R.id.novelTitle);
            holder.author = view.findViewById(R.id.author);
            holder.src = view.findViewById(R.id.src);
            holder.img = view.findViewById(R.id.novelImg);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.src.setText("来源:" + dataList.get(i).getUrl());
        holder.title.setText("标题:" + dataList.get(i).getTitle());
        holder.author.setText("作者:" + dataList.get(i).getAuthor());
        view.setOnClickListener(e -> {
            MainActivity.changeFragment(1);
            AnalysisFragment.startAnalysis(dataList.get(i));
        });
        Glide.with(mContext).load(dataList.get(i).getImgUrl()).into(holder.img);
        return view;
    }

    public void add(NovelInfo info) {
        dataList.add(info);
        notifyDataSetChanged();
    }

    //批量添加
    public void addAll(List<NovelInfo> info) {
        dataList.addAll(info);
        notifyDataSetChanged();
    }


    static class ViewHolder {
        ImageView img;
        TextView title;
        TextView author;
        TextView src;
    }
}

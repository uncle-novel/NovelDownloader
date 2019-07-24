package com.unclezs.novel.Adapter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.bumptech.glide.Glide;
import com.unclezs.novel.Activity.DownloadFragment;
import com.unclezs.novel.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Uncle
 * 2019.07.22.
 */
public class DownloadListAdapter extends BaseAdapter {
    private Context mContext;
    private List<DownloadAdapter> listData;
    private List<ViewHolder> holders = new ArrayList<>();

    public DownloadListAdapter(Context mContext, List<DownloadAdapter> listData) {
        this.mContext = mContext;
        this.listData = listData;
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
        if (view == null || holders.size() <= i) {
            view = LayoutInflater.from(mContext).inflate(R.layout.download_list_litem, viewGroup, false);
            holder = new ViewHolder();
            holder.pb = view.findViewById(R.id.download_pb);
            holder.cancel = view.findViewById(R.id.download_cancelBtn);
            holder.cancel.setOnClickListener(e -> {
                //停止
                System.out.println(i + "-" + listData.size() + "-" + holders.size());
                DownloadAdapter adapter = listData.get(i);
                adapter.stop();
                //删除
                SQLiteDatabase db = DownloadFragment.db.getWritableDatabase();
                DownloadFragment.db.remove(db, listData.get(i).getTitle());
                db.close();
                listData.remove(i);
                holders.remove(i);
                notifyDataSetChanged();
            });
            holder.title = view.findViewById(R.id.download_title);
            holder.img = view.findViewById(R.id.download_img);
            holder.text = view.findViewById(R.id.download_text_pb);
            holder.text.setText(listData.get(i).getText());
            if (listData.get(i).getText().contains("下载完成")) {
                holder.cancel.setText("移除");
            }
            holder.pb.setProgress(1);
            holder.title.setText(listData.get(i).getTitle());
            System.out.println(listData.get(i).getImgPath());
            Glide.with(mContext).load(listData.get(i).getImgPath()).into(holder.img);
            holder.img.setTag(listData.get(i).getImgPath());
            holders.add(holder);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        if (holders.size() > i) {
            holder.title = holders.get(i).title;
            holder.img = holders.get(i).img;
            holder.text = holders.get(i).text;
            holder.pb = holders.get(i).pb;
        }
        return view;
    }

    public void addTask(DownloadAdapter downloadAdapter) {
        listData.add(downloadAdapter);
        notifyDataSetChanged();
    }

    public ViewHolder getLastHolder(int index) {
        return holders.get(index);
    }

    public int getSize() {
        return holders.size();
    }


    public class ViewHolder {
        public RoundCornerProgressBar pb;
        public BootstrapButton cancel;
        public TextView title;
        public TextView text;
        public ImageView img;
    }
}

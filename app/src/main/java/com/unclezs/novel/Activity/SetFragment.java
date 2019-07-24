package com.unclezs.novel.Activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.unclezs.novel.Adapter.DownloadAdapter;
import com.unclezs.novel.Adapter.DownloadListAdapter;
import com.unclezs.novel.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 设置面板
 * Created by Uncle
 * 2019.07.20.
 */
public class SetFragment extends Fragment {
    View searchView;
    ListView listView;
    List<DownloadListAdapter.ViewHolder> task;
    private static DownloadListAdapter adapter;
    private static Handler mHandler;
    private static Context context;
    public SetFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        searchView = inflater.inflate(R.layout.fragment_download, container, false);
        bindView();
        context=getContext();
        return searchView;
    }

    private void bindView() {
        this.listView = searchView.findViewById(R.id.task_list);
        adapter = new DownloadListAdapter(getContext(), new ArrayList<>());
        task = new ArrayList<>();
        listView.setAdapter(adapter);
        mHandler = new Handler(msg -> {
            Bundle bundle = msg.getData();
            switch (msg.what) {
                default:
                    if (msg.what < adapter.getSize()) {//防止删除任务的时候多线程任务还未完全停止导致数组越界
                        DownloadListAdapter.ViewHolder holder = adapter.getLastHolder(msg.what);
                        holder.pb.setProgress(bundle.getFloat("p"));
                        holder.text.setText(bundle.getString("pt"));
                        if (bundle.getFloat("p") == 1.0f) {
                            holder.cancel.setText("移除");
                        }
                    }
                    break;
            }
            return true;
        });
    }

    public static void addTask(DownloadAdapter downloadTask) {
        Toast.makeText(context,"添加下载成功",Toast.LENGTH_SHORT).show();
        final int index = adapter.getSize() == 0 ? 0 : adapter.getSize();
        System.out.println(index);
        //启动下载
        adapter.addTask(downloadTask);
        new Thread(() -> downloadTask.start()).start();
        //启动监控
        new Thread(() -> {
            try {
                Thread.sleep(100);
                while (downloadTask.getMaxNum() != downloadTask.getOverNum() && !downloadTask.isStoped()) {
                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putFloat("p", 1.0f * downloadTask.getOverNum() / downloadTask.getMaxNum());
                    bundle.putString("pt", downloadTask.getOverNum() + "/" + downloadTask.getMaxNum());
                    message.setData(bundle);
                    message.what = index;
                    mHandler.sendMessage(message);
                    Thread.sleep(1000);
                }
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putFloat("p", 1.0f);
                bundle.putString("pt", "下载完成 共" + downloadTask.getMaxNum() + "章节");
                message.setData(bundle);
                message.what = index;
                mHandler.sendMessage(message);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}

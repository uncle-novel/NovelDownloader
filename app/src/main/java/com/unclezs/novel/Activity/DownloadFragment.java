package com.unclezs.novel.Activity;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.unclezs.novel.Adapter.DownloadAdapter;
import com.unclezs.novel.Adapter.DownloadListAdapter;
import com.unclezs.novel.Dao.MainDao;
import com.unclezs.novel.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Uncle
 * 2019.07.20.
 */
public class DownloadFragment extends Fragment implements AdapterView.OnItemClickListener {
    View searchView;
    ListView listView;
    List<DownloadListAdapter.ViewHolder> task;
    private static DownloadListAdapter adapter;
    private static Handler mHandler;
    private static Context context;
    public static MainDao db;

    public DownloadFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        searchView = inflater.inflate(R.layout.fragment_download, container, false);
        context = getContext();
        db=new MainDao(context,"uncle.db",null,1);
        bindView();
        return searchView;
    }

    private void bindView() {
        this.listView = searchView.findViewById(R.id.task_list);
        //查库
        SQLiteDatabase d=db.getWritableDatabase();
        List<DownloadAdapter> adapters = db.queryAll(d);
        d.close();
        adapter = new DownloadListAdapter(getContext(),adapters);
        task = new ArrayList<>();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
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
                            //保存入库
                            SQLiteDatabase database=db.getWritableDatabase();
                            db.insert(database,holder.title.getText().toString(),holder.img.getTag().toString(),bundle.getString("pt"));
                            db.close();
                        }
                    }
                    break;
            }
            return true;
        });
    }

    public static void addTask(DownloadAdapter downloadTask) {
        Toast.makeText(context, "添加下载成功", Toast.LENGTH_SHORT).show();
        final int index = adapter.getSize() == 0 ? 0 : adapter.getSize();
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

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        DownloadListAdapter.ViewHolder holder = ((DownloadListAdapter.ViewHolder) (view.getTag()));
        if (!holder.cancel.getText().toString().equals("移除")) {
            Toast.makeText(getContext(), "请等待下载完成后再打开", Toast.LENGTH_SHORT).show();
            return;
        }

        String path = "/sdcard/小说下载/" + holder.title.getText().toString().trim() + ".txt";
        Uri uri;
        File file = new File(path);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(getContext(), "com.unclezs.novel.fileProvider", file);
        } else {
            uri = Uri.fromFile(file);
        }
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setDataAndType(uri, "text/plain");
        this.startActivity(intent);
    }
}

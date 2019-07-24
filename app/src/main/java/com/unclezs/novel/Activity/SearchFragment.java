package com.unclezs.novel.Activity;

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

import com.unclezs.novel.Adapter.SearchListAdapter;
import com.unclezs.novel.Crawl.NovelSpider;
import com.unclezs.novel.Model.NovelInfo;
import com.unclezs.novel.R;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Uncle
 * 2019.07.20.
 */
public class SearchFragment extends Fragment {
    AVLoadingIndicatorView loading;//加载
    Handler mHandler;//事件处理
    ListView list;//结果列表
    private final int FINISH_SEARCH = 1;//开始搜索
    private View searchView;
    private USearchBox searchBox;//搜索框
    NovelSpider spider = new NovelSpider();

    public SearchFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        searchView = inflater.inflate(R.layout.fragment_search, container, false);
        bindView();//绑定组件
        initEventListener();//事件绑定
        return searchView;
    }

    public void searchByName(String name) {
        if (name == null || name.length() <= 0) {
            Toast.makeText(getContext(), "内容不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        loading.show();//加载loading
        SearchListAdapter adapter = new SearchListAdapter(getContext(), new ArrayList<>());
        list.setAdapter(adapter);
        new Thread(() -> {
            List<NovelInfo> info;
            //获取列表
            try {
                info = spider.queryNovelList(name);
            } catch (IOException e) {
                loading.hide();
                return;
            }
            Bundle data = new Bundle();
            data.putSerializable("info", (Serializable) info);
            Message message = new Message();
            message.setData(data);
            message.what = FINISH_SEARCH;
            mHandler.sendMessage(message);
        }).start();
    }

    //绑定组件
    private void bindView() {
        searchBox = searchView.findViewById(R.id.f_search_box);
        //loading动画
        loading = searchView.findViewById(R.id.loading);
        loading.hide();
        //列表数据
        list = searchView.findViewById(R.id.list);
    }

    //绑定事件
    private void initEventListener() {
        searchBox.setCallBack(name -> searchByName(name));
        mHandler = new Handler(msg -> {
            Bundle data = msg.getData();
            switch (msg.what) {
                //搜索小说
                case FINISH_SEARCH:
                    List<NovelInfo> listData = (List<NovelInfo>) data.getSerializable("info");
                    ((SearchListAdapter) (list.getAdapter())).addAll(listData);
                    loading.hide();
                    break;
            }
            return true;
        });
    }


}

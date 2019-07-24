package com.unclezs.novel.Activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.unclezs.novel.Adapter.AnalysisListAdapter;
import com.unclezs.novel.Crawl.NovelDownloader;
import com.unclezs.novel.Crawl.NovelSpider;
import com.unclezs.novel.InterFaces.OnSearchListener;
import com.unclezs.novel.Model.AnalysisConfig;
import com.unclezs.novel.Model.Chapter;
import com.unclezs.novel.Model.NovelInfo;
import com.unclezs.novel.R;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Uncle
 * 2019.07.20.
 */
public class AnalysisFragment extends Fragment implements OnSearchListener {
    private BootstrapButton setting;
    private BootstrapButton selectAll;
    private BootstrapButton downLoadBtn;
    private BootstrapButton searchBtn;
    private USearchBox input;
    private AVLoadingIndicatorView loading;
    private ListView listView;
    private View analysisView;
    private static Handler mHandler;
    private static final int FINISHED_ANALYSIS = 1;
    private static final int FINISHED_CONTENT = 2;
    private static final int FAILED_LOADING = 3;
    private static final int START_ANALYSIS_CHAPTER = 4;
    private AnalysisConfig config = new AnalysisConfig();
    private NovelSpider spider = new NovelSpider(config);
    private boolean isSelectAll = true;
    //小说正文弹窗
    View alertView;
    TextView alertText;//弹出窗口正文
    TextView alertTitle;//弹出窗口标题
    AlertDialog dialog;
    List<Chapter> chapters;
    //解析设置弹窗
    View alertSettingView;//弹窗面板设置
    Switch filter;//过滤
    Switch sort;//重排
    Switch ncr;//ncr转中文
    Switch ts;//繁转简体
    Spinner rule;//规则
    EditText ad;//去广告

    AlertDialog settingDialog;

    public AnalysisFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        analysisView = inflater.inflate(R.layout.fragment_analysis, container, false);
        bindView();
        initListener();
        return analysisView;
    }

    private void initListener() {
        this.mHandler = new Handler(message -> {
            Bundle data = message.getData();
            switch (message.what) {
                //解析目录结束
                case FINISHED_ANALYSIS:
                    this.chapters = (List<Chapter>) data.getSerializable("chapters");
                    this.listView.setAdapter(new AnalysisListAdapter(getContext(), chapters, true));
                    this.isSelectAll = true;
                    this.loading.hide();
                    break;
                //解析正文
                case FINISHED_CONTENT:
                    String content = data.getString("content");
                    this.alertText.setText(content);
                    this.loading.hide();
                    this.dialog.show();
                    break;
                case FAILED_LOADING:
                    loading.hide();
                    Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
                    break;
                case START_ANALYSIS_CHAPTER:
                    NovelInfo info = (NovelInfo) data.getSerializable("info");
                    input.setText(info.getUrl());
                    spider.setImgUrl(info.getImgUrl());
                    analysisChapter(info.getUrl());
                    break;
            }
            return true;
        });
        this.listView.setOnItemClickListener((adapterView, view, id, position) -> {
            Chapter chapter = chapters.get((int) position);
            this.alertTitle.setText(chapter.getChapterName());
            analysisContent(chapter.getChapterUrl());
        });
        //全选/反选
        this.selectAll.setOnClickListener(e -> {
            isSelectAll = !isSelectAll;
            if (isSelectAll) {
                selectAll.setText("全选");
            } else {
                selectAll.setText("反选");
            }
            if (listView.getAdapter() == null) {
                Toast.makeText(getContext(), "请先解析目录", Toast.LENGTH_SHORT).show();
                return;
            }
            ((AnalysisListAdapter) (listView.getAdapter())).selectAll(isSelectAll);
        });
        //下载
        downLoadBtn.setOnClickListener(e -> {
            //获取读取手机储存权限
            if (Build.VERSION.SDK_INT >= 23) { //安卓6.0以上需要允许授权才能写入图片到本地
                int REQUEST_CODE_CONTACT = 101;
                String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                //验证是否许可权限
                for (String str : permissions) {
                    if (getActivity().checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                        //申请权限
                        Toast.makeText(getContext(), "请先授权读写储存权限", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
            List<String> list = new ArrayList<>();
            List<String> nameList = new ArrayList<>();
            if (chapters == null) {
                Toast.makeText(getContext(), "清先解析目录", Toast.LENGTH_SHORT).show();
                return;
            }
            for (int i = 0; i <chapters.size(); i++) {
                if (((AnalysisListAdapter) (listView.getAdapter())).getIsChecked().get(i)){//筛选选中
                    list.add(chapters.get(i).getChapterUrl());
                    nameList.add(chapters.get(i).getChapterName());
                }
            }
            DownloadFragment.addTask(new NovelDownloader(list, nameList, MainActivity.config, spider));
        });
        //解析
        this.searchBtn.setOnClickListener(e -> {
            analysisChapter(input.getText());
        });
        //打开配置
        this.setting.setOnClickListener(e -> {
            settingDialog.show();
        });
        //保存配置
        this.settingDialog.setOnDismissListener(e -> {
            config.setAdStr(ad.getText().toString());
            config.setNcrToZh(ncr.isChecked());
            switch (rule.getSelectedItem().toString()) {
                case "正文规则1":
                    config.setRule("1");
                    break;
                case "正文规则2":
                    config.setRule("2");
                    break;
                default:
                    config.setRule("3");
                    break;
            }
            config.setTraToSimple(ts.isChecked());
            config.setChapterSort(sort.isChecked());
            config.setChapterFilter(filter.isChecked());
            spider.setConf(config);
        });
    }

    private void bindView() {
        this.setting = analysisView.findViewById(R.id.analysis_config_btn);
        this.selectAll = analysisView.findViewById(R.id.analysis_selectAll_btn);
        this.downLoadBtn = analysisView.findViewById(R.id.analysis_download_btn);
        this.searchBtn = analysisView.findViewById(R.id.analysis_search_btn);
        this.input = analysisView.findViewById(R.id.f_analysis_box);
        this.input.setCallBack(this);
        this.loading = analysisView.findViewById(R.id.analysis_loading);
        loading.hide();
        this.listView = analysisView.findViewById(R.id.analysis_list);
        this.alertView = LayoutInflater.from(getContext()).inflate(R.layout.analysis_content_alert, null, false);
        this.alertText = alertView.findViewById(R.id.analysis_content_alert_text);
        this.alertTitle = alertView.findViewById(R.id.analysis_content_alert_title);
        this.dialog = new AlertDialog.Builder(getContext())
                .setMessage(null)
                .setTitle(null)
                .setView(alertView)
                .create();
        //配置弹出框
        this.alertSettingView = LayoutInflater.from(getContext()).inflate(R.layout.analysis_setting_alert, null, false);
        this.ad = alertSettingView.findViewById(R.id.setting_ad);
        this.filter = alertSettingView.findViewById(R.id.setting_gl);
        this.rule = alertSettingView.findViewById(R.id.setting_rule);
        List<String> setDataList = new ArrayList<>();
        setDataList.add("推荐规则");
        setDataList.add("正文规则1");
        setDataList.add("正文规则2");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_expandable_list_item_1, setDataList);
        this.rule.setAdapter(arrayAdapter);
        this.sort = alertSettingView.findViewById(R.id.setting_cp);
        this.ncr = alertSettingView.findViewById(R.id.setting_ncr);
        this.ts = alertSettingView.findViewById(R.id.setting_fzj);
        this.settingDialog = new AlertDialog.Builder(getContext())
                .setMessage(null)
                .setTitle(null)
                .setNegativeButton("确定", null)
                .setView(alertSettingView)
                .create();
    }

    @Override
    public void search(String url) {
        analysisChapter(url);
    }

    //解析目录
    private void analysisChapter(String url) {
        if (url == null || url.length() <= 0 || !url.startsWith("http")) {
            Toast.makeText(getContext(), "目录地址不正确", Toast.LENGTH_SHORT).show();
            return;
        }
        loading.bringToFront();
        loading.show();
        new Thread(() -> {
            //抓取小说目录
            try {
                Map<String, String> chapterList = spider.getChapterList(url);
                List<Chapter> list = new ArrayList<>();
                for (String key : chapterList.keySet()) {
                    String v = chapterList.get(key);
                    list.add(new Chapter(v, key));
                }
                //绑定数据发送处理请求
                Bundle data = new Bundle();
                data.putSerializable("chapters", (Serializable) list);
                Message message = new Message();
                message.setData(data);
                message.what = FINISHED_ANALYSIS;
                mHandler.sendMessage(message);
            } catch (IOException e) {
                mHandler.sendEmptyMessage(FAILED_LOADING);
            }
        }).start();
    }

    //解析正文
    private void analysisContent(String url) {
        loading.bringToFront();
        loading.show();
        new Thread(() -> {
            try {
                String content = spider.getContent(url, spider.getConfig().get("charset"));
                Bundle bundle = new Bundle();
                bundle.putString("content", content);
                Message message = new Message();
                message.setData(bundle);
                message.what = FINISHED_CONTENT;
                mHandler.sendMessage(message);
            } catch (IOException e) {
                mHandler.sendEmptyMessage(FAILED_LOADING);
            }
        }).start();
    }

    //跳转开始解析
    public static void startAnalysis(NovelInfo info) {
        Message message = new Message();
        Bundle data = new Bundle();
        data.putSerializable("info", info);
        message.what = START_ANALYSIS_CHAPTER;
        message.setData(data);
        mHandler.sendMessage(message);
    }
}

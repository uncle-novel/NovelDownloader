package com.unclezs.novel.Activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;
import com.unclezs.novel.Adapter.MainFragmentAdapter;
import com.unclezs.novel.Model.DownloadConfig;
import com.unclezs.novel.R;

import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener, ViewPager.OnPageChangeListener {
    //页面常量
    public static final int SEARCH_PAGE = 0;
    public static final int ANALYSIS_PAGE = 1;
    public static final int DOWNLOAD_PAGE = 2;
    //tab按钮
    private RadioButton searchTab;
    private RadioButton analysisTab;
    private RadioButton downloadTab;
    private RadioGroup tabBar;
    //页面管理
    private static ViewPager vPager;
    private MainFragmentAdapter adapter;//适配器
    private TitleBar titleBar;
    private PopupMenu popup;
    private View alertSetting;//
    private AlertDialog alertDialog;
    public static DownloadConfig config = new DownloadConfig("/sdcard/小说下载", 50, 0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        adapter = new MainFragmentAdapter(getSupportFragmentManager());
        bindView();

        //获取读取手机储存权限
        if (Build.VERSION.SDK_INT >= 23) { //安卓6.0以上需要允许授权才能写入图片到本地
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            //验证是否许可权限
            for (String str : permissions) {
                if (checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                }
            }
        }
    }

    private void bindView() {
        searchTab = findViewById(R.id.tab_search);
        downloadTab = findViewById(R.id.tab_download);
        analysisTab = findViewById(R.id.tab_analysis);
        alertSetting = LayoutInflater.from(this).inflate(R.layout.download_config_set, null, false);
        Spinner delay = alertSetting.findViewById(R.id.download_delay);
        ArrayAdapter<String> delayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, new String[]{"0", "1", "2", "3", "5", "10"});
        delay.setAdapter(delayAdapter);
        ArrayAdapter<String> numAdapter = new ArrayAdapter<>(this, android.R.layout.simple_expandable_list_item_1, new String[]{"50", "100", "200", "500", "1000", "2000", "单线程"});
        Spinner number = alertSetting.findViewById(R.id.download_number);
        number.setAdapter(numAdapter);
        //读取配置
        SharedPreferences sp = this.getSharedPreferences("downloadConfig", Context.MODE_PRIVATE);
        number.setSelection(sp.getInt("chapterNum", 0));
        delay.setSelection(sp.getInt("delay", 0));
        alertDialog = new AlertDialog.Builder(this)
                .setTitle(null)
                .setMessage(null)
                .setView(alertSetting)
                .create();
        alertDialog.setOnDismissListener(e -> {
            config.setPerThreadDownNum(number.getSelectedItem().toString().equals("单线程") ? 999999 : Integer.parseInt(number.getSelectedItem().toString()));
            config.setSleepTime(Integer.parseInt(delay.getSelectedItem().toString()));
            saveConfig(number.getSelectedItemPosition(), delay.getSelectedItemPosition());
        });
        titleBar = findViewById(R.id.title_bar);
        tabBar = findViewById(R.id.tab_bar);
        tabBar.setOnCheckedChangeListener(this);
        vPager = findViewById(R.id.vPager);
        vPager.setAdapter(adapter);
        vPager.addOnPageChangeListener(this);
        vPager.setCurrentItem(SEARCH_PAGE);//初始为搜索页面

        popup = new PopupMenu(MainActivity.this, titleBar.getRightView());
        popup.getMenuInflater().inflate(R.menu.set_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.github:
                    Uri uri = Uri.parse("https://github.com/unclezs/NovelDownloader");
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    intent.setData(uri);
                    startActivity(intent);
                    break;
                case R.id.down_set:
                    alertDialog.show();
                    break;
                case R.id.support_author:
                    Intent zyb = null;
                    try {
                        zyb = Intent.parseUri("intent://platformapi/startapp?saId=10000007&clientVersion=3.7.0.0718&qrcode=https%3A%2F%2Fqr.alipay.com%2F{urlCode}%3F_s%3Dweb-other&_t=1472443966571#Intent;scheme=alipayqr;package=com.eg.android.AlipayGphone;end".replace("{urlCode}", "fkx09459uuksg30f6d6ml02"), 1);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                    startActivity(zyb);
                    break;
                case R.id.qq_group:
                    joinQQGroup("dGfr4cxrqHd5SzGB6iAERFR45IsTqUqh");
                    break;
            }
            return true;
        });
        titleBar.setOnTitleBarListener(new OnTitleBarListener() {
            @Override
            public void onLeftClick(View v) {

            }

            @Override
            public void onTitleClick(View v) {
            }

            @Override
            public void onRightClick(View v) {
                popup.show();
            }
        });
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int id) {
        switch (id) {
            case R.id.tab_search:
                vPager.setCurrentItem(SEARCH_PAGE);
                break;
            case R.id.tab_analysis:
                vPager.setCurrentItem(ANALYSIS_PAGE);
                break;
            case R.id.tab_download:
                vPager.setCurrentItem(DOWNLOAD_PAGE);
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == 2) {
            switch (vPager.getCurrentItem()) {
                case SEARCH_PAGE:
                    searchTab.setChecked(true);
                    break;
                case ANALYSIS_PAGE:
                    analysisTab.setChecked(true);
                    break;
                case DOWNLOAD_PAGE:
                    downloadTab.setChecked(true);
                    break;
            }
        }
    }

    public static void changeFragment(int flag) {
        vPager.setCurrentItem(flag);
    }

    public boolean joinQQGroup(String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(intent);
            return true;
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            return false;
        }
    }

    private void saveConfig(Integer number, Integer delay) {
        SharedPreferences sp = this.getSharedPreferences("downloadConfig", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putInt("chapterNum", number);
        edit.putInt("delay", delay);
        edit.commit();
    }
}

package top.uncle.spider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import top.uncle.spider.crawl.sql.SearchSql;

public class MainActivity extends Activity implements View.OnClickListener,NovelAdapter.Callback{
    EditText surl;//搜素框
    ListView n_listview;//搜索结果框
    boolean onsearch;//是否还在搜索标志
    List<String[]> results;//搜索结果
    NovelAdapter adapter;//listview适配器
    Button btn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
         /*
        获取读取手机储存权限
         */
        if (Build.VERSION.SDK_INT >= 23) { //安卓6.0以上需要允许授权才能写入图片到本地
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            //验证是否许可权限
            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    this.requestPermissions(permissions, REQUEST_CODE_CONTACT);
                }
            }
        }
        btn=findViewById(R.id.btnSearch);
        surl=findViewById(R.id.serachedt);
        n_listview =findViewById(R.id.novellistview);
        btn.setOnClickListener(this);
    }


    //事务处理
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            final Bundle data=msg.getData();
            onsearch=true;
            switch (msg.what){
                case 1:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            results=new ArrayList<>();
                            results.addAll(SearchSql.getResults(data.getString("name")));
                            onsearch=false;
                        }
                    }).start();
                    while (onsearch){

                        adapter=new NovelAdapter(results,MainActivity.this,MainActivity.this);
                        n_listview.setAdapter(adapter);
                    }
            }
            super.handleMessage(msg);
        }
    };
    //监听listView的子组件点击事件
    @Override
    public void click(View v){
        switch (v.getId()){
            case R.id.downthis:
                Button dtn= v.findViewById(R.id.downthis);
                String[] data=results.get((int)(dtn.getTag()));
                Bundle bundle=new Bundle();
                bundle.putString("title",data[1]);
                bundle.putString("url",data[0]);
                bundle.putString("author",data[2]);
                bundle.putString("fromto",data[3]);
                Intent intent=new Intent();
                intent.setClass(this,Download.class);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.chapter:
                Button dtns= v.findViewById(R.id.chapter);
                String[] datas=results.get((int)(dtns.getTag()));
                Bundle bundles=new Bundle();
                bundles.putString("title",datas[1]);
                bundles.putString("url",datas[0]);
                bundles.putString("author",datas[2]);
                Intent intents=new Intent();
                intents.setClass(this,ShowChapter.class);
                intents.putExtras(bundles);
                startActivity(intents);
                break;
        }


    }
    //监听当前ACtivity按钮点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSearch:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String name = surl.getText().toString().trim();
                        Message message = new Message();
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        bundle.putString("name", name);
                        message.setData(bundle);
                        MainActivity.this.handler.sendMessage(message);
                    }
                }).start();
        }
    }
}

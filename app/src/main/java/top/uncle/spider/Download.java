package top.uncle.spider;

import android.app.Activity;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import top.uncle.spider.crawl.download.DownloadByName;
import top.uncle.spider.crawl.download.GlobalValue;
import top.uncle.spider.crawl.entitys.Chapter;

/**
 * Created by uncle on 2018/11/6.
 * 小说下载页
 */
@SuppressWarnings("ALL")
public class Download extends Activity {
    ProgressBar pgb;//进度条
    TextView jindu;//进度textview
    TextView chapter;//章节目录
    Bundle data;//提交异步下载数据
    List<Chapter> chapter_list=new ArrayList<>();
    public final int BEGIN_DOWNLOAD=1;
    public final int SHOW_PROGRESS=2;
    public final int SHOW_CHAPTER=3;
    ServiceConnection connection;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.downloadpage);
        chapter=findViewById(R.id.chaptern);
        jindu=findViewById(R.id.jindu);
        pgb=findViewById(R.id.pgb);
        data=getIntent().getExtras();
        init();
        new Thread(new Runnable() {
            @Override
            public void run() {
                //发送下载信息
                Message message = new Message();
                message.what = BEGIN_DOWNLOAD;
                Bundle dates=new Bundle();
                dates.putString("name",data.getString("title"));
                dates.putString("url",data.getString("url"));
                message.setData(dates);
                Download.this.handler.sendMessage(message);
                while (true){
                    if(GlobalValue.list!=null){
                        //加入目录
                        Message list=new Message();
                        list.what=SHOW_CHAPTER;
                        Download.this.handler.sendMessage(list);
                    }
                    Message ms=new Message();
                    ms.what=SHOW_PROGRESS;
                    Download.this.handler.sendMessage(ms);
                    if(!GlobalValue.ondownloading)
                        break;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
    //每次下载初始化
    public void init(){
        GlobalValue.ondownloading=true;
        GlobalValue.list=null;
        pgb.setVisibility(View.INVISIBLE);
        jindu.setVisibility(View.INVISIBLE);
        GlobalValue.progress=new ArrayList<>();
        GlobalValue.num=0;
        jindu.setText("");
        chapter.setText("");
    }
    //事务处理
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
           final Bundle bundle=msg.getData();
            switch (msg.what) {
                //提交下载请求
                case BEGIN_DOWNLOAD:

                            Toast.makeText(Download.this,"Uncle:开始下载",Toast.LENGTH_SHORT).show();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                    String name = bundle.getString("name");
                                    String url=bundle.getString("url");
                                    DownloadByName.doInBackground(url,name);
                                    }catch (Exception e){
                                        Message faildown=new Message();
                                        faildown.what=4;
                                        handler.sendMessage(faildown);
                                    }
                                }
                            }).start();
                        break;
                //进度条显示
                case SHOW_PROGRESS:
                        pgb.setVisibility(View.VISIBLE);
                        jindu.setVisibility(View.VISIBLE);
                        jindu.setText(GlobalValue.progress.size()+"章/"+GlobalValue.num+"章");
                        pgb.setMax(GlobalValue.num);
                        pgb.setProgress(GlobalValue.progress.size());
                        if (!GlobalValue.ondownloading) {
                            pgb.setProgress(GlobalValue.num);
                            jindu.setText(GlobalValue.num+"章/"+GlobalValue.num+"章|下载完成！");
                            //吐丝提示
                            Toast.makeText(Download.this,"Uncle:下载完成",Toast.LENGTH_SHORT).show();
                        }
                        break;
                //显示章节列表
                case SHOW_CHAPTER:
                        chapter.setVisibility(View.VISIBLE);
                        StringBuffer lists=new StringBuffer();
                        if(GlobalValue.list==null){
                            break;
                        }
                        for(Chapter c:GlobalValue.list){
                            lists.append(c.getTitle());
                            lists.append(c.getUrl());
                            lists.append("\n");
                        }
                        chapter_list=GlobalValue.list;
                        chapter.setText(lists);
                        GlobalValue.list=null;
                        break;
                case 4:
                    Toast.makeText(Download.this,"下载失败，请更换源重试",Toast.LENGTH_SHORT).show();
            }
            super.handleMessage(msg);
        }
    };
}

package top.uncle.spider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import top.uncle.spider.crawl.entitys.Chapter;
import top.uncle.spider.crawl.impl.AChapterSpider;


/**
 * Created by uncle on 2018/11/19.
 */
public class ShowChapter extends Activity {
    public TextView title;
    public TextView author;
    public String durl;
    ChapterAdapter adapter;
    ListView listview;
    public boolean onSearch=true;
    final int SHOW_LIST=1;
    final int GET_LIST=2;
    List<String[]> content=new ArrayList<>();
    Handler mhandler=new Handler(){
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void handleMessage(final Message msg) {
            switch (msg.what){
                case GET_LIST:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            AChapterSpider list=new AChapterSpider();
                            List<Chapter> chapterlist = list.getChapterlist(durl);
                            String[] temp=new String[2];
                            int flag=0;
                            for(Chapter c:chapterlist){
                                if(flag==0){
                                    temp[0]=c.getTitle();
                                    flag++;
                                }else if (flag==1){
                                    temp[1]=c.getTitle();
                                    content.add(temp);
                                    temp=new String[2];
                                    flag=0;
                                }
                            }
                            onSearch=false;
                        }
                    }).start();
                case SHOW_LIST:
                    Toast.makeText(ShowChapter.this,"加载中。。",Toast.LENGTH_LONG).show();
                    adapter=new ChapterAdapter(content,ShowChapter.this);
                    listview.setAdapter(adapter);
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chapter);
        Bundle data = this.getIntent().getExtras();
        title=findViewById(R.id.chapter_title);
        listview=findViewById(R.id.chapter_listview);
        author=findViewById(R.id.chapter_author);
        title.setText(data.getString("title"));
        author.setText(data.getString("author"));
        durl=data.getString("url");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg=new Message();
                msg.what=GET_LIST;
                mhandler.sendMessage(msg);
                while(true){
                    try {
                        if(!onSearch){
                            Message smsg=new Message();
                            smsg.what=SHOW_LIST;
                            mhandler.sendMessage(smsg);
                            break;
                        }
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}

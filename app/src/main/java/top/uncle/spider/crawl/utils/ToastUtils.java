package top.uncle.spider.crawl.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by uncle on 2018/11/25.
 */
public class ToastUtils {
    public static void show(Context context,String ...msg){
        Toast.makeText(context,msg[0],Toast.LENGTH_SHORT).show();
    }
}

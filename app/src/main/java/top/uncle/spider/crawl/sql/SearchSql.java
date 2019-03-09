package top.uncle.spider.crawl.sql;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import top.uncle.spider.crawl.utils.SpiderUtils;


/**
 * Created by uncle on 2018/11/14.
 */
public class SearchSql{
    public static List<String[]> getResults(String name) {
        List<String[]> result=new ArrayList<>();
        try {
            name= URLEncoder.encode(name,"utf-8");
            String jsondata= SpiderUtils.getSource("https://unclez.top:8443/Novel/search?name="+name);
            JsonObject bookjson = (JsonObject) new JsonParser().parse(jsondata);
            JsonArray books = bookjson.get("book").getAsJsonArray();
            for(JsonElement book:books){
                JsonObject obj=book.getAsJsonObject();
                String[] res=new String[4];
                res[0]=obj.get("url").getAsString();
                res[1]=obj.get("title").getAsString();
                res[2]=obj.get("author").getAsString();
                res[3]=SpiderUtils.getRealUrl(res[0]);
                Log.d("url",res[0]);
                result.add(res);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}

package top.uncle.spider.crawl.download;


import top.uncle.spider.crawl.impl.ADownload;

/**
 * Created by uncle on 2018/11/17.
 * 小说异步下载器
 */
public class DownloadByName{
    public static Integer doInBackground(String url,String name) {
        ADownload aDownload = new ADownload();
        ConfDownload conf = new ConfDownload();
        conf.setPath("/sdcard/小说下载");
        conf.setSize(30);
        aDownload.download(url, conf,name);
        return 0;
    }
}
//public class DownloadByName extends AsyncTask<String,Integer,Integer>{
//    @Override
//    public Integer doInBackground(String... params) {
//        ADownload aDownload = new ADownload();
//        ConfDownload conf = new ConfDownload();
//        conf.setPath("/sdcard/小说下载");
//        conf.setSize(30);
//        aDownload.download(params[0], conf,params[1]);
//        return 0;
//    }
//}

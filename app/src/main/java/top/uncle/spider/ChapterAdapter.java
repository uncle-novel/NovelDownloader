package top.uncle.spider;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by uncle on 2018/11/19.
 */
public class ChapterAdapter extends BaseAdapter {
    public List<String[]> list;
    private LayoutInflater mInflater;
    public boolean isloading=true;
    public ChapterAdapter(List<String[]> list, Context context){
        this.list=list;
        mInflater=LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view=null;
        if(convertView==null){
            view=mInflater.inflate(R.layout.chapter_item,null);
        }else {
            view=convertView;
        }
        TextView item1 = view.findViewById(R.id.chapter_item1);
        TextView item2 = view.findViewById(R.id.chapter_item2);
        item1.setText(list.get(position)[0]);
        item2.setText(list.get(position)[1]);
        if(position==list.size()-1)
            isloading=false;
        return view;
    }
}

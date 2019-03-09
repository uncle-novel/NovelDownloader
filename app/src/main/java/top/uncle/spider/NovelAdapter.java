package top.uncle.spider;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

/**
 * Created by uncle on 2018/11/17.
 */
public class NovelAdapter extends BaseAdapter implements View.OnClickListener{
    private List<String[]> list;
    private LayoutInflater mInflater;
    private Callback mcallback;
    public NovelAdapter(List<String[]> list, Context context, Callback callback){
        this.list=list;
        mInflater=LayoutInflater.from(context);
        this.mcallback=callback;
    }
    public interface Callback{
        public void click(View view);
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
            view=mInflater.inflate(R.layout.novel,null);
        }else {
            view=convertView;
        }
        TextView title=view.findViewById(R.id.title);
        TextView author=view.findViewById(R.id.author);
        TextView from=view.findViewById(R.id.from);
        Button btnd=view.findViewById(R.id.downthis);
        View btnc = view.findViewById(R.id.chapter);
        title.setText("标题:"+list.get(position)[1]);
        author.setText("作者:"+list.get(position)[2]);
        from.setText("来源:"+list.get(position)[3]);
        btnd.setTag(position);
        btnc.setTag(position);
        btnd.setOnClickListener(this);
        btnc.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        mcallback.click(v);
    }
}

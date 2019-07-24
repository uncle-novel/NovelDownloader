package com.unclezs.novel.Activity;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.unclezs.novel.InterFaces.OnSearchListener;
import com.unclezs.novel.R;

/**
 * 自定义搜索框
 * Created by Uncle
 * 2019.07.21.
 */
public class USearchBox extends LinearLayout implements TextWatcher {

    private EditText searchBox;//搜索框
    private ImageView deleteImg;//搜索框
    private OnSearchListener searchListener;
    private Context context;
    private AttributeSet attrs;

    public USearchBox(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public USearchBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.attrs = attrs;
        initView();
    }

    public USearchBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.attrs = attrs;
        this.context = context;
        initView();
    }

    void initView() {
        View view = LayoutInflater.from(context).inflate(R.layout.u_search_box, null);
        deleteImg = view.findViewById(R.id.search_img_delete);
        searchBox = view.findViewById(R.id.et_search_top);
        searchBox.addTextChangedListener(this);
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.USearchBox);
            String hint = a.getString(R.styleable.USearchBox_searchHint);
            System.out.println(hint);
            searchBox.setHint(hint);
            a.recycle();
        }
        initListener();
        this.addView(view);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence s, int i, int i1, int i2) {
        if (s.length() > 0) {
            deleteImg.setVisibility(View.VISIBLE);
        } else {
            deleteImg.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    private void initListener() {
        //点击清除内容
        this.deleteImg.setOnClickListener(e -> {
            this.searchBox.getText().clear();
        });
        this.searchBox.setOnEditorActionListener((v, id, e) -> {
            if (id == EditorInfo.IME_ACTION_SEARCH) {
                searchListener.search(searchBox.getText().toString().trim());
            }
            return false;
        });
    }

    public void setCallBack(OnSearchListener onSearchListener) {
        this.searchListener = onSearchListener;
    }
    public String getText(){
        return searchBox.getText().toString().trim();
    }
    public void setText(String text){
        searchBox.setText(text);
    }
}

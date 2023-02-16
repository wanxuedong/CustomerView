package com.example.customerview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class BaseView extends View {

    public int viewWidth;
    public int viewHeight;

    public BaseView(Context context) {
        super(context);
        init(context);
    }

    public BaseView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BaseView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setClickable(true);
        viewWidth = getMeasuredWidth();
        viewHeight = getMeasuredHeight();
    }

    public void init(Context context) {

    }


}

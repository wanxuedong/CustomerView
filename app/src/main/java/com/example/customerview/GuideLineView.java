package com.example.customerview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.customerview.utils.TxtViewUtils;

import java.util.ArrayList;
import java.util.List;


public class GuideLineView extends View {

    private int width;
    private int height;
    private int selectIndex = 0;
    private Paint textPaint;
    private Paint linePaint;
    private int itemWidth;
    private List<String> contents = new ArrayList<>();
    private int lineHeadX1;
    private int lineHeadX2;
    private int lineEndX1;
    private int lineEndX2;
    private int lineHeight;
    private int drawStart = -1;
    private int drawEnd;
    private int textSize;
    private ValueAnimator leftAnimator;
    private ValueAnimator rightAnimator;

    public GuideLineView(Context context) {
        super(context);
        init(context);
    }

    public GuideLineView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GuideLineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        lineHeight = ScreenUtil.dp2px(context, 3);
        textSize = ScreenUtil.dp2px(context, 12);
        linePaint = new Paint();
        linePaint.setColor(Color.RED);
        linePaint.setStrokeWidth(lineHeight);
        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(textSize);
        setSelectIndex(selectIndex);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawText(canvas);
        drawLine(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        itemWidth = width / contents.size();
        lineHeadX1 = itemWidth * selectIndex;
        lineEndX1 = itemWidth * (selectIndex + 1);
    }

    /**
     * 绘制文字
     *
     * @param canvas 画板
     **/
    private void drawText(Canvas canvas) {
        for (int i = 0; i < contents.size(); i++) {
            int textHeight = TxtViewUtils.getTextHeight(textPaint,contents.get(i));
            canvas.drawText(contents.get(i), itemWidth * i, textHeight, textPaint);
        }
    }

    /**
     * 绘制底部线条
     *
     * @param canvas 画板
     **/
    private void drawLine(Canvas canvas) {
        if (drawStart == -1){
            drawStart = itemWidth * selectIndex;
            drawEnd = itemWidth * (selectIndex + 1);
        }
        canvas.drawLine(drawStart, height - lineHeight, drawEnd, height - lineHeight, linePaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float x = event.getX();
                selectIndex = (int) x / itemWidth;
                lineHeadX2 = itemWidth * selectIndex;
                lineEndX2 = itemWidth * (selectIndex + 1);
                moveLine();
                break;
        }
        return super.onTouchEvent(event);
    }

    private void moveLine() {
        Log.d("fsdafdsa", lineHeadX1 + " : " + lineHeadX2 + " : " + lineEndX1 + " : " + lineEndX2);
        leftAnimator = ValueAnimator.ofInt(lineHeadX1,lineHeadX2);
        leftAnimator.setDuration(500);
        leftAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                drawStart = (int) animation.getAnimatedValue();
                lineHeadX1 = drawStart;
                Log.d("addUpdateListener",drawStart + " : " + drawEnd);
                postInvalidate();
                if (drawStart == lineHeadX2){
                    lineHeadX1 = lineHeadX2;
                }
            }
        });
        leftAnimator.start();
        rightAnimator = ValueAnimator.ofInt(lineEndX1,lineEndX2);
        rightAnimator.setDuration(500);
        rightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                drawEnd = (int) animation.getAnimatedValue();
                lineEndX1 = drawEnd;
                Log.d("addUpdateListener",drawEnd + "");
                if (drawStart == lineHeadX2){
                    lineEndX1 = lineEndX2;
                }
            }
        });
        rightAnimator.start();
    }

    /**
     * 设置显示选择器内容
     *
     * @param contents 文字内容
     **/
    public void setContents(List<String> contents) {
        if (contents != null) {
            this.contents = contents;
            postInvalidate();
        }
    }

    /**
     * 设置当前选中位置
     *
     * @param selectIndex 选中下标，从1开始
     **/
    public void setSelectIndex(int selectIndex) {
        if (selectIndex >= contents.size()) {
            selectIndex = contents.size() - 1;
        }
        if (selectIndex < 0){
            selectIndex = 0;
        }
        this.selectIndex = selectIndex;

        postInvalidate();
    }

}

package com.example.customerview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class ProgressView extends View {

    private int viewWidth;
    private int viewHeight;
    private int circleRadius;

    private Paint circlePaint;
    private int progressWidth;
    private int progressHeight;

    private Drawable progressDrawable;

    private int position;
    private int progress = 0;
    private final int MAX_LEVEL = 10000;
    private int duration = 10000;

    public ProgressView(Context context) {
        super(context);
        init(context);
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        circlePaint = new Paint();
        circlePaint.setStrokeWidth(ScreenUtil.dp2px(context, 1));
        circlePaint.setColor(context.getResources().getColor(R.color.teal_700));
        circleRadius = ScreenUtil.dp2px(context, 10);
        progressDrawable = context.getDrawable(R.drawable.progress_drawable);
        progressHeight = ScreenUtil.dp2px(context,6);
        setClickable(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewWidth = getMeasuredWidth();
        viewHeight = getMeasuredHeight();
        progressWidth = viewWidth - circleRadius * 2;
        position = circleRadius;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawProgress(canvas);
        drawCircle(canvas);
    }

    private void drawCircle(Canvas canvas) {
        canvas.drawCircle(position, viewHeight / 2, circleRadius, circlePaint);
    }

    private void drawProgress(Canvas canvas) {
        Rect rect = new Rect(circleRadius,(viewHeight - progressHeight) / 2,progressWidth + circleRadius, (viewHeight + progressHeight) / 2);
        progressDrawable.setBounds(rect);
        progressDrawable.draw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (event.getX() < viewWidth - circleRadius && event.getX() > circleRadius) {
                    position = (int) event.getX();
                    calculateProgress();
                    invalidate();
                }
                break;
            default:
        }
        return super.onTouchEvent(event);
    }

    private void calculateProgress(){
        progress = (int) (MAX_LEVEL * (1.0f * (position - circleRadius) / progressWidth));
        progressDrawable.setLevel(progress);
        if (onProgressListen != null){
            onProgressListen.onProgress(getProgress());
        }
    }

    public void setProgress(int value){
        position = (int) ((1f * value / duration) * progressWidth) + circleRadius;
        calculateProgress();
        invalidate();
    }


    public void setDuration(int duration) {
        this.duration = duration;
    }

    private OnProgressListen onProgressListen;

    public void setOnProgressListen(OnProgressListen onProgressListen) {
        this.onProgressListen = onProgressListen;
    }

    public interface OnProgressListen{
        void onProgress(int progress);
    }

    private int getProgress() {
        return (int) ((1f * progress / MAX_LEVEL) * duration);
    }
}

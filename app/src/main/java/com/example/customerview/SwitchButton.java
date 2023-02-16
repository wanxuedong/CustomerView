package com.example.customerview;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import androidx.annotation.Nullable;

public class SwitchButton extends View {

    private Path backPath;
    private Paint backPaint;
    private Path centerPath;
    private Paint centerPaint;
    private boolean isOpen;

    private int width;
    private int height;

    private int margin;
    private int centerWidth;

    private int currentPosition;
    private int radius;
    private static final int DURATION = 300;
    public static final int CENTER_WIDTH = 60;
    public static final int MARGIN_LENGTH = 15;
    public static final int RECT_RADIUS = 10;

    private int minPosition;
    private int maxPosition;

    private SwitchStateListener switchStateListener;

    public void setSwitchStateListener(SwitchStateListener switchStateListener) {
        this.switchStateListener = switchStateListener;
    }

    public SwitchButton(Context context) {
        super(context);
        init(context);
    }

    public SwitchButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SwitchButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        backPaint = new Paint();
        backPaint.setColor(getResources().getColor(R.color.close_back));
        centerPaint = new Paint();
        centerPaint.setColor(Color.WHITE);
        margin = ScreenUtil.dp2px(context, MARGIN_LENGTH);
        centerWidth = ScreenUtil.dp2px(context, CENTER_WIDTH);
        radius = ScreenUtil.dp2px(context, RECT_RADIUS);
        currentPosition = margin + centerWidth / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        width = getMeasuredWidth();
        height = getMeasuredHeight();
        minPosition = margin + centerWidth / 2;
        maxPosition = width - margin - centerWidth / 2;
        backPath = new Path();
        centerPath = new Path();
        drawback(canvas);
        drawCenter(canvas);
    }

    private void drawback(Canvas canvas) {
        RectF rect = new RectF(0, 0, width, height);
        backPath.addRoundRect(rect, radius, radius, Path.Direction.CCW);
        canvas.drawPath(backPath, backPaint);

    }

    private void drawCenter(Canvas canvas) {
        RectF rect = new RectF(currentPosition - centerWidth / 2, margin, currentPosition + centerWidth / 2, height - margin);
        centerPath.addRoundRect(rect, radius, radius, Path.Direction.CCW);
        canvas.drawPath(centerPath, centerPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                switchState(isOpen,true);
                break;
        }
        return super.onTouchEvent(event);
    }

    public void switchState(boolean isOpen,boolean needAnim) {
        if (needAnim) {
            Resources resources = getResources();
            int startColor = resources.getColor(isOpen ? R.color.open_back : R.color.close_back);
            int endColor = resources.getColor(isOpen ? R.color.close_back : R.color.open_back);
            ValueAnimator colorAnim = ObjectAnimator.ofArgb(startColor, endColor);
            colorAnim.setDuration(DURATION);
            colorAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    backPaint.setColor((Integer) animation.getAnimatedValue());
                    invalidate();
                }
            });
            colorAnim.start();

            ValueAnimator centerAnim = ValueAnimator.ofInt(isOpen ? maxPosition : minPosition, isOpen ? minPosition : maxPosition);
            centerAnim.setDuration(DURATION);
            centerAnim.setInterpolator(new OvershootInterpolator());
            centerAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    currentPosition = (int) animation.getAnimatedValue();
                    invalidate();
                }
            });
            centerAnim.start();
        }else {
            backPaint.setColor(getResources().getColor(isOpen ? R.color.close_back: R.color.open_back));
            currentPosition = isOpen ? minPosition : maxPosition;
            invalidate();
        }
        this.isOpen = !isOpen;
        if (switchStateListener != null){
            switchStateListener.stateChange(isOpen);
        }
    }

    public interface SwitchStateListener{
        void stateChange(boolean isOpen);
    }

}

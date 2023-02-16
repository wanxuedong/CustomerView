package com.example.customerview.picker;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.widget.Scroller;

import androidx.annotation.Nullable;

import com.example.customerview.BaseView;
import com.example.customerview.R;
import com.example.customerview.ScreenUtil;
import com.example.customerview.utils.TxtViewUtils;

public class PickView extends BaseView {

    private final int MAX_LENGTH = 7;

    private Paint textPaint;
    private Paint linePaint;
    private int lineHeight;

    private String[] contents = new String[]{"1月", "2月", "3月", "4月", "5月", "6月", "7月", "8月", "9月", "10月", "11月", "12月"};

    private int touchY = -1;
    private int moveY;
    private int lastMoveY;
    private int movePosition;

    private int itemHeight;

    private Scroller scroller;
    private VelocityTracker velocityTracker;

    private int scrollTime = 500;

    private int selectIndex = 0;

    private OnSelectLister onSelectLister;

    /**
     * 是否需要惯性滚动
     * **/
    private boolean isNeedInertia = true;

    public PickView(Context context) {
        super(context);
    }

    public PickView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PickView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void init(Context context) {
        super.init(context);
        textPaint = new Paint();
        textPaint.setColor(getResources().getColor(R.color.black));
        textPaint.setTextSize(ScreenUtil.sp2px(context, 16));
        linePaint = new Paint();
        lineHeight = ScreenUtil.dp2px(context, 30);
        linePaint.setColor(getResources().getColor(R.color.black));
        scroller = new Scroller(context);
        velocityTracker = VelocityTracker.obtain();;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        itemHeight = (int) (1f * viewHeight / MAX_LENGTH);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawText(canvas);
        drawLine(canvas);
    }

    private void drawText(Canvas canvas) {
        for (int i = 0; i < MAX_LENGTH; i++) {
            String content = contents[getIndex(i, -movePosition)];
            int textWidth = TxtViewUtils.getTextWidth(textPaint, content);
            int textHeight = TxtViewUtils.getTextHeight(textPaint, content);
            canvas.drawText(content, (viewWidth - textWidth) / 2, itemHeight * (i + 1) - (itemHeight - textHeight) / 2 + moveY % itemHeight, textPaint);
        }
    }

    private void drawLine(Canvas canvas) {
        canvas.drawLine(0, (viewHeight - lineHeight) / 2, viewWidth, (viewHeight - lineHeight) / 2, linePaint);
        canvas.drawLine(0, (viewHeight + lineHeight) / 2, viewWidth, (viewHeight + lineHeight) / 2, linePaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        velocityTracker.addMovement(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchY = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                velocityTracker.computeCurrentVelocity(1000);
                moveY = (int) event.getY() - touchY + lastMoveY;
                if (Math.abs(moveY) == contents.length * itemHeight) {
                    moveY = 0;
                }
                movePosition = moveY / itemHeight % contents.length;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                velocityTracker.computeCurrentVelocity(1000);
                int speed = (int) velocityTracker.getYVelocity();
                calculateTime(Math.abs(speed));
                ValueAnimator valueAnimator = ValueAnimator.ofInt(moveY, itemHeight * movePosition + (isNeedInertia ? speed / 50 * itemHeight : 0));
                valueAnimator.setDuration(scrollTime);
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        moveY = (int) animation.getAnimatedValue();
                        if (Math.abs(moveY) == contents.length * itemHeight) {
                            moveY = 0;
                        }
                        movePosition = moveY / itemHeight % contents.length;
                        Log.d("velocityTracker",moveY + "");
                        postInvalidate();
                    }
                });
                valueAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        lastMoveY = moveY = itemHeight * movePosition;
                        if (onSelectLister != null){
                            onSelectLister.onScroll(getIndex(MAX_LENGTH / 2,-movePosition));
                        }
                    }
                });
                valueAnimator.start();
                break;
            default:
        }
        return super.onTouchEvent(event);
    }

    private void calculateTime(int speed){
        if (speed > 0){
            scrollTime = 300;
        }else if (speed > 1000){
            scrollTime = 600;
        }else if (speed > 2000){
            scrollTime = 1200;
        }else if (speed > 300){
            scrollTime = 2000;
        }else {
            scrollTime = 3000;
        }
    }

    /**
     * @param position 固定各个文本下表
     * @param movePosition 随手指移动浮动下表
     * **/
    private int getIndex(int position, int movePosition) {
        int realPosition;
        if (movePosition >= 0) {
            realPosition = (position + movePosition) % contents.length;
        } else {
            realPosition = (position + contents.length + movePosition) % contents.length;
        }
        if (selectIndex >= 0){
            realPosition = (realPosition + selectIndex) % contents.length;
        }else {
            realPosition = (realPosition + contents.length + selectIndex) % contents.length;
        }
        return realPosition;
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            postInvalidate();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (velocityTracker != null) {
            velocityTracker.recycle();
            velocityTracker = null;
        }
    }

    public void setSelectIndex(int selectIndex) {
        if (selectIndex < 0){
            return;
        }
        this.selectIndex = selectIndex % contents.length - MAX_LENGTH / 2;
        moveY = 0;
        lastMoveY = 0;
        movePosition = 0;
        invalidate();
    }

    public void setOnSelectLister(OnSelectLister onSelectLister) {
        this.onSelectLister = onSelectLister;
    }

    public void setContents(String[] contents) {
        this.contents = contents;
    }

    public String[] getContents() {
        return contents;
    }

    public interface OnSelectLister{
        void onScroll(int index);
    }

}

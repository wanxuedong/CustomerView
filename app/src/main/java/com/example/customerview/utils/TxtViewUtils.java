package com.example.customerview.utils;

import android.graphics.Paint;
import android.graphics.Rect;

public class TxtViewUtils {

    /**
     * 获取文字高度
     *
     * @param content 文字内容
     * @return 文字高度
     **/
    public static int getTextHeight(Paint paint, String content) {
        Rect rect = new Rect();
        paint.getTextBounds(content, 0, content.length(), rect);
        return rect.height();
    }

    /**
     * 获取文字宽度
     *
     * @param content 文字内容
     * @return 文字高度
     **/
    public static int getTextWidth(Paint paint, String content) {
        Rect rect = new Rect();
        paint.getTextBounds(content, 0, content.length(), rect);
        return rect.width();
    }

}

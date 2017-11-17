package com.upyun.shortvideo.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;


public class GreyView extends View {
    public GreyView(Context context) {
        super(context);
    }

    public GreyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GreyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public GreyView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        Paint paint = new Paint();
        paint.setColor(0xff323333);
        paint.setAntiAlias(true);
        canvas.drawLine(0, 0, width, height, paint);
    }
}

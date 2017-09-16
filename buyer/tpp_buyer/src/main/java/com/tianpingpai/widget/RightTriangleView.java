package com.tianpingpai.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class RightTriangleView extends View {

    private Path path = new Path();
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public RightTriangleView(Context context) {
        super(context);
        init();
    }

    public RightTriangleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RightTriangleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public RightTriangleView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init(){
        paint.setColor(Color.parseColor("#f8e05c"));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        path.reset();
        path.moveTo(getWidth(),0);
        path.lineTo(getWidth(),getHeight());
        path.lineTo(0,getHeight());
        path.close();
        canvas.drawPath(path,paint);
    }
}

package com.tianpingpai.widget;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;

import com.tianpingpai.utils.DimensionUtil;

public class NotificationView extends View {

    public interface OnAnimationListener{
        void onAnimationFinished();
    }

    private TextPaint mPaint = new TextPaint();
    private Scroller mScroller;
    private String mText;
    private float mTextWidth;
    private float speed = 2;
    private Camera camera = new Camera();

    public void setWidth(int width) {
        this.width = width;
    }

    private int width;

    public void setAnimationListener(OnAnimationListener animationListener) {
        this.animationListener = animationListener;
    }

    private OnAnimationListener animationListener;

    public void setText(String text) {
        this.mText = text;
        mTextWidth = mPaint.measureText(mText);
    }

    public void start() {
        float gap = mTextWidth;
        if (gap > 0) {
            float lenghth = mTextWidth - width * 0.5f;
            int time = (int) (lenghth * 10 / speed);
            mScroller.startScroll(0, 0, (int) lenghth, 0, time);
            invalidate();
        }
    }

    public NotificationView(Context context) {
        super(context);
        init();
    }

    public NotificationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NotificationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mScroller = new Scroller(getContext(), new LinearInterpolator());
        mPaint.setColor(Color.BLACK);
        mPaint.setTextSize(DimensionUtil.sp2px(14));
    }

    Matrix matrix = new Matrix();

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int index = 0;
        camera.rotateY(20);
        camera.getMatrix(matrix);
//        canvas.setMatrix(matrix);

        if (mScroller.computeScrollOffset()) {
            index = -mScroller.getCurrX();
            invalidate();
        } else {
            if (mScroller.isFinished()) {
                Log.e("xx", "finished");
                if(animationListener != null){
                    animationListener.onAnimationFinished();
                }
            }
        }
        if(mText != null) {
            canvas.drawText(mText, index, 50, mPaint);
        }
    }
}

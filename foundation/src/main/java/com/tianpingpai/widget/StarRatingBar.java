package com.tianpingpai.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;

import com.tianpingpai.foundation.R;

public class StarRatingBar extends android.widget.RatingBar {
    public StarRatingBar(Context context) {
        super(context);
    }

    public StarRatingBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.StarRatingBar);
        if(a.hasValue(R.styleable.StarRatingBar_fullStar)){
            fullStar = ((BitmapDrawable)a.getDrawable(R.styleable.StarRatingBar_fullStar)).getBitmap();
            emptyStar = ((BitmapDrawable)a.getDrawable(R.styleable.StarRatingBar_emptyStar)).getBitmap();
        }

        a.recycle();

    }

    public StarRatingBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    Paint paint = new Paint();

    {
        paint.setColor(Color.RED);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);

    }

    public void setFullStartRes(int fullStartRes) {
        fullStar = BitmapFactory.decodeResource(getResources(), fullStartRes);
        requestLayout();
    }

    public void setEmptyStarRes(int emptyStarRes) {
        emptyStar = BitmapFactory.decodeResource(getResources(), emptyStarRes);
    }

    private Bitmap fullStar;
    private Bitmap emptyStar;

    @Override
    protected void onDraw(Canvas canvas) {
        if(fullStar == null){
            return;
        }
        int max = getMax();
        float ratings = getRating();
        Log.e("xx","ratings:" + ratings + "max:" + max);
        for (int i = 0; i < 5; i++) {
            if (i < ratings / max * 5) {
                canvas.drawBitmap(fullStar, 0, 0, paint);
            } else {
                canvas.drawBitmap(emptyStar, 0, 0, paint);
            }
            assert fullStar != null;
            canvas.translate(fullStar.getWidth() * 1.2f, 0);
        }
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) {
            if (fullStar != null) {
                int height = fullStar.getHeight();
                int width = (int) (fullStar.getWidth() * 5.8f);
                setMeasuredDimension(width, height);
            }
        }
    }
}

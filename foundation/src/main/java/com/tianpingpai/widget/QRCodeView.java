package com.tianpingpai.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.onbarcode.barcode.android.AndroidColor;
import com.onbarcode.barcode.android.IBarcode;
import com.onbarcode.barcode.android.QRCode;
import com.tianpingpai.utils.DimensionUtil;

public class QRCodeView extends View {
    public QRCodeView(Context context) {
        super(context);
    }

    public QRCodeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public QRCodeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private QRCode mCode = new QRCode();

    {
        mCode.setDataMode(QRCode.M_AUTO);
        mCode.setVersion(1);
        mCode.setEcl(QRCode.ECL_L);
        mCode.setFnc1Mode(IBarcode.FNC1_NONE);
        mCode.setProcessTilde(false);
        mCode.setUom(IBarcode.UOM_PIXEL);
    }

    public void setData(String data){
        mCode.setData(data);
        int size = DimensionUtil.dip2px(240);
        mCode.setBarcodeWidth(size);
        mCode.setBarcodeHeight(size);
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = (int) mCode.getBarcodeWidth();
        int height = (int) mCode.getBarcodeHeight();
        setMeasuredDimension(width,height);
    }

    private RectF bounds = new RectF();
    private Rect rect = new Rect();

    private Bitmap bitmap;

    private ColorMatrix ma = new ColorMatrix();
    {
        ma.setSaturation(0);
    }
    private ColorMatrixColorFilter colorFilter = new ColorMatrixColorFilter(ma);
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.getClipBounds(rect);
        bounds.set(0, 0, rect.width(), rect.height());

        if(bitmap == null){
            bitmap = Bitmap.createBitmap(rect.width(),rect.height(), Bitmap.Config.ARGB_4444);
            Canvas mapCanvas = new Canvas(bitmap);

            mCode.setX(40);
            mCode.setLeftMargin(50f);
            mCode.setRightMargin(50f);
            mCode.setTopMargin(50f);
            mCode.setBottomMargin(50f);
            mCode.setResolution(72);
            mCode.setAutoResize(true);

            mCode.setForeColor(AndroidColor.red);
            mCode.setBackColor(AndroidColor.white);
            try {
                mCode.drawBarcode(mapCanvas,bounds);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        mPaint.setColorFilter(colorFilter);
        canvas.drawBitmap(bitmap,0,0,mPaint);
    }
}

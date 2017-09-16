package com.tianpingpai.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tianpingpai.foundation.R;
import com.tianpingpai.utils.DimensionUtil;

public class BadgeView extends RelativeLayout {

    public static final int STYLE_FILL = 1;
    public static final int STYLE_STROKE = 2;

    private int style = STYLE_STROKE;

    private TextView mTextView;
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private GradientDrawable strokeBackground;
    private GradientDrawable whiteFillBackground;
    private GradientDrawable redFillBackground;

    public void setStyle(int style) {
        this.style = style;
        if (style == STYLE_FILL) {
            mTextView.setTextColor(Color.WHITE);
        } else {
            mTextView.setTextColor(red);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int w = mTextView.getMeasuredWidth();
        int h = mTextView.getMeasuredHeight();
        int x = (getMeasuredWidth() - w) / 2;
        int y = (getMeasuredHeight() - h) / 2 - 1;


        Log.e("xx","w:" + w + " __h:" + h);
        Log.e("xx","a w:" + r + "  " + l + " __h:" + h);
        mTextView.layout(x , y, x + w, h + y);
        mTextView.setGravity(Gravity.CENTER_HORIZONTAL);
    }

    public BadgeView(Context context) {
        super(context);
        init();
    }

    public BadgeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BadgeView);
        int textSize = a.getDimensionPixelSize(R.styleable.BadgeView_textSize, 4);
        mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);

        int style = a.getInt(R.styleable.BadgeView_style, STYLE_FILL);

        setStyle(style);
        a.recycle();
    }

    private void init() {
        mTextView = new TextView(getContext());
        mTextView.setGravity(Gravity.CENTER);

        red = getResources().getColor(R.color.red_ff6);

        setWillNotDraw(false);
        mPaint.setStrokeWidth(3);
        int padding = DimensionUtil.dip2px(1);
        mTextView.setText("");
        mTextView.setPadding(padding, padding, padding, padding);
        mTextView.setTextColor(Color.RED);
        mPaint.setColor(Color.RED);

        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        addView(mTextView, lp);

        strokeBackground = new GradientDrawable();
        strokeBackground.setStroke(DimensionUtil.dip2px(1),red);
        whiteFillBackground = new GradientDrawable();
        whiteFillBackground.setColor(Color.WHITE);
        redFillBackground = new GradientDrawable();
//        redFillBackground.setColor(red);
    }

    public void setDisableBackgroundIfZero(boolean disableBackgroundIfZero) {
        this.disableBackgroundIfZero = disableBackgroundIfZero;
    }

    private boolean disableBackgroundIfZero = true;

    public void setTextSize(float textSize) {
        mTextView.setTextSize(textSize);
        requestLayout();
    }

    public void setBadge(int bad) {
//        if (disableBackgroundIfZero && bad == 0) {
//            return;
//        }
        setText(String.valueOf(bad));
    }

    public void setText(String text) {
        if (disableBackgroundIfZero) {
            if (text == null || text.equals("0")) {
                mTextView.setText("");
                setDisableBackgroundIfZero(true);
                requestLayout();
                return;
            }
        }
        mTextView.setText(text);
        requestLayout();
    }

    public int getPadding() {
        return padding;
    }

    public void setPadding(int padding) {
        this.padding = padding;
    }

    private int padding = 8;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int ws = MeasureSpec.makeMeasureSpec(30,MeasureSpec.UNSPECIFIED);
        int hs = MeasureSpec.makeMeasureSpec(30,MeasureSpec.UNSPECIFIED);
        measureChildren(ws,hs);
        mTextView.measure(ws, hs);


        int h = mTextView.getMeasuredHeight();
        int w = mTextView.getMeasuredWidth();

        if (style == STYLE_FILL) {
            h += DimensionUtil.dip2px(4);
            w += DimensionUtil.dip2px(4);
        } else {
            h += DimensionUtil.dip2px(4);
            w += DimensionUtil.dip2px(4);
        }

//        Log.e("xx", "w:" + w + " h:" + h);



        if (w < h) {
            setMeasuredDimension(h, h);
        } else {
            setMeasuredDimension(w, h);
        }
    }

    private Rect bounds = new Rect();

    int red = Color.parseColor("#ff6000");

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (disableBackgroundIfZero) {
            if (mTextView.getText().toString().equals("0")
                    || mTextView.getText().toString().equals("")) {
                return;
            }
        }

        int halfPadding = DimensionUtil.dip2px(4) / 2;
        int r = getHeight() / 2 - halfPadding;

        whiteFillBackground.setCornerRadius(r);
        redFillBackground.setCornerRadius(r);
        strokeBackground.setCornerRadius(r);

        if(getWidth()  <= getHeight()){
            bounds.set(halfPadding,halfPadding,getHeight() - halfPadding,getHeight() - halfPadding);
        }else{
            bounds.set(halfPadding,halfPadding,getWidth() - halfPadding,getHeight() - halfPadding);
        }

//        bounds.set(-30,0,getHeight() - getPadding(),getHeight() - getPadding());
//    canvas.drawColor(Color.GREEN);

        if(style == STYLE_STROKE){
            whiteFillBackground.setBounds(bounds);
            strokeBackground.setBounds(bounds);
            whiteFillBackground.draw(canvas);
            strokeBackground.draw(canvas);
        }else{
            redFillBackground.setBounds(bounds);
            redFillBackground.setColorFilter(red, PorterDuff.Mode.ADD);
            redFillBackground.draw(canvas);
        }
    }
}

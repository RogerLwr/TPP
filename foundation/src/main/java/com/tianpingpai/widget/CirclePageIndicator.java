package com.tianpingpai.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;

import com.tianpingpai.foundation.R;
import com.tianpingpai.utils.DimensionUtil;

public class CirclePageIndicator extends View {

    public static final int LEFT_BOTTOM = 0x01;
    public static final int TOP = 0x02;
    public static final int RIGHT = 0x03;
    public static final int BOTTOM = 0x10;
    public static final int CENTER = 0x20;

    private int gravity = BOTTOM | CENTER;

	public interface OnPageItemClickListener{
		void onPageItemClicked(int position);
	}

	private ViewPager viewPager;
	private OnPageItemClickListener pageItemClickListener;
	
	public void setOnPageItemClickListener(OnPageItemClickListener itemClickListener){
		pageItemClickListener = itemClickListener;
	}
	
	
	private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {
		@Override
		public void onPageSelected(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg1, float xx, int arg3) {
			total = viewPager.getAdapter().getCount();
			current = viewPager.getCurrentItem();
			invalidate();
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}
	};

	private int total;
	private int current;
	private int radius = 30;

	public void setUnselectedColor(int unselectedColor) {
		this.unselectedColor = unselectedColor;
	}

	private int unselectedColor = Color.GRAY;

	public void setSelectedColor(int selectedColor) {
		this.selectedColor = selectedColor;
	}

	private int selectedColor = Color.WHITE;
	private OnGestureListener mGestureListener = new OnGestureListener() {
		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			Log.e("xx","clicked:" + current + pageItemClickListener);
			if(pageItemClickListener != null){
				pageItemClickListener.onPageItemClicked(current);
			}
			return true;
		}
		
		@Override
		public void onShowPress(MotionEvent e) {
			
		}
		
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
				float distanceY) {
			return false;
		}
		
		@Override
		public void onLongPress(MotionEvent e) {
		}
		
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			return false;
		}
		
		@Override
		public boolean onDown(MotionEvent e) {
			return false;
		}
	};

	public void setViewPager(final ViewPager viewPager) {
		this.viewPager = viewPager;
		viewPager.removeOnPageChangeListener(pageChangeListener);
		viewPager.addOnPageChangeListener(pageChangeListener);
		radius = DimensionUtil.dip2px(4);
		startAnimation();
		mGestureDetector = new GestureDetector(getContext(), mGestureListener);
	}

	public CirclePageIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CirclePageIndicator);
        gravity = a.getInteger(R.styleable.CirclePageIndicator_gravity, -1);
        Log.e("xx","gravity:" + gravity);

        parentId = a.getInt(R.styleable.CirclePageIndicator_viewPager,-1);
		a.recycle();
	}

	public CirclePageIndicator(Context context) {
		super(context);
	}

	private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	{
		mPaint.setTextSize(50);
		mPaint.setColor(Color.RED);
	}

	private static Handler mHandler = new Handler();
	private int interval = 3000;
	private boolean intercepting;
	private long lastTouchEvent = 0;

    private int parentId = -1;
	
	private Runnable swipeRun = new Runnable() {
		@Override
		public void run() {
			mHandler.removeCallbacks(swipeRun);
			if(viewPager == null){
				return;
			}

			if(intercepting){
				return;
			}
			if(System.currentTimeMillis() - lastTouchEvent < interval){
				return;
			}
			int target = current + 1;
			if(current >= total - 1){
				target = 0;
			}
			try{
				viewPager.setCurrentItem(target, true);
			}catch(IllegalStateException e){
				viewPager.getAdapter().notifyDataSetChanged();
			}
			mHandler.postDelayed(swipeRun, interval);
		}
	};

    protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		stopAnimation();
	}
	
	public void startAnimation(){
		mHandler.removeCallbacks(swipeRun);
		mHandler.postDelayed(swipeRun, interval);
	}
	
	public void stopAnimation(){
		mHandler.removeCallbacks(swipeRun);
	}
	
	
	GestureDetector mGestureDetector;
    private boolean interceptGesture = true;

    public void setInterceptGesture(boolean intercept){
        this.interceptGesture = intercept;
    }
	
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
        if(!interceptGesture){
            return super.onTouchEvent(event);
        }
		if(viewPager !=null){
			viewPager.onTouchEvent(event);
		}
		mGestureDetector.onTouchEvent(event);
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			intercepting = true;
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			intercepting = false;
			lastTouchEvent = System.currentTimeMillis();
			break;
		default:
			break;
		}
		return true;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(viewPager == null){
			return;
		}
		if(viewPager.getAdapter() != null){
			total = viewPager.getAdapter().getCount();
		}

		int x = 0;
        int y = 0;
//        Log.e("xx","gravity:" + gravity);
//        Log.e("xx","b|c" + (BOTTOM | CENTER));
//        Log.e("xx","b|r" + (BOTTOM | RIGHT));
        switch (gravity){
            case BOTTOM | CENTER:
                x = (int) (getWidth() - (total * 1.5) * radius * 2) / 2;
                y = (int) (getHeight() - getPaddingBottom() - radius * 1.5);
                break;
            case BOTTOM | RIGHT:
                x = (int) (getWidth() - (total * 1.5) * radius * 2);
                y = (int) (getHeight() - getPaddingBottom() - radius * 1.5) - getPaddingBottom();
                break;
        }

		for (int i = 0; i < total; i++) {
			if (i == current) {
				mPaint.setColor(selectedColor);
			} else {
				mPaint.setColor(unselectedColor);
			}
			canvas.drawCircle(x, y, radius, mPaint);
			x += radius * 3;
		}
	}
}

package com.tianpingpai.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

public class ScrollTab extends ViewGroup {
	
	public static interface OnTabItemChangedListener{
		public void onTabItemChanged(int position);
	}

	private class SelectionOnClickListener implements OnClickListener {
		int position = 0;

		SelectionOnClickListener(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			bottomView.setSelection(position, true);
		}
	}

	public void setOnTabItemChangedListener(OnTabItemChangedListener listener){
		bottomView.mOnTabItemChangedListener = listener;
	}
	public ScrollTab(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ScrollTab(Context context) {
		super(context);
		init();
	}

	private SelectionView bottomView;

	private void init() {
		bottomView = new SelectionView(getContext());
		bottomView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				24));
		this.addView(bottomView);
	}

	public void setCurrentItem(int index) {
		bottomView.setSelection(index, true);
	}

	public void setCurrentItem(int index, boolean smoothScroll) {
		bottomView.setSelection(index, smoothScroll);
	}

	public void setTabs(View[] tabViews) {
		this.removeAllViewsInLayout();
		int tabWidth = (int) (getWidth() * 1.0 / tabViews.length * 1.0);
		for (int i = 0; i < tabViews.length; i++) {
			tabViews[i].setOnClickListener(new SelectionOnClickListener(i));
			LayoutParams lp = new LayoutParams(tabWidth,
					LayoutParams.WRAP_CONTENT);
			tabViews[i].setLayoutParams(lp);
			this.addView(tabViews[i]);
		}
		bottomView = new SelectionView(getContext());
		bottomView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				24));
		bottomView.setTabNum(tabViews.length);
		this.addView(bottomView);
		requestLayout();
		invalidate();
	}

	private int getChildDesiredHeight() {
		int tabDesiredHeight = 0;
		for (int i = 0; i < getChildCount(); i++) {
			tabDesiredHeight = Math.max(getChildAt(i).getMeasuredHeight(),
					tabDesiredHeight);
		}
		Log.e("desiredHeight:", "--" + tabDesiredHeight);
		return Math.max(42, tabDesiredHeight);
	}

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		for (int index = 0; index < getChildCount(); index++) {
			final View child = getChildAt(index);
			// measure
			if (getChildCount() != 1) {
				child.measure(
						MeasureSpec.makeMeasureSpec(getWidth()
								/ getChildCount() - 1, MeasureSpec.EXACTLY),
						MeasureSpec.UNSPECIFIED);
			}
		}

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		this.setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),
				getChildDesiredHeight() + 10);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		Log.e("onlayout", this.getChildCount() + "l:" + l + "t:" + t + "r:" + r
				+ "b:" + b);
		int width = r - l;

		if (getChildCount() == 1) {
			getChildAt(getChildCount() - 1).layout(l, t + 50, r, t + 58);
			return;
		}
		int tabWidth = (int) (width / (getChildCount() - 1) * 1.0);
		int tabDesiredHeight = getChildDesiredHeight();
		for (int i = 0; i < getChildCount() - 1; i++) {
			this.getChildAt(i).layout(l + tabWidth * (i), t,
					l + tabWidth * (i + 1), t + tabDesiredHeight);
		}
		getChildAt(getChildCount() - 1).layout(l, t + tabDesiredHeight, r,
				t + tabDesiredHeight + 8);
	}

	public void setState(int mode){
		this.invalidate();
		if(bottomView.mode == mode){
			return;
		}
		bottomView.mode = mode;
		
	}
	
	public void scroll(int selection,float percent){
		bottomView.mode = 1;
		bottomView.selection = selection;
		bottomView.percent = percent;
		bottomView.invalidate();
		
	}
	
	private class SelectionView extends View {

		private int mode = 0;
		private float percent = 0;
		Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		Paint selectionPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

		int selection = 0;
		int tabNum = 1;

		Scroller mScroller = null;

		{
//			int color = Color.rgb(20, 90, 40);
			linePaint.setColor(Color.rgb(20, 90, 40));
			selectionPaint.setColor(Color.rgb(20, 90, 40));
			linePaint.setStrokeWidth(4);
			selectionPaint.setStrokeWidth(10);
		}

		public SelectionView(Context context, AttributeSet attrs) {
			super(context, attrs);
			init();
		}

		public SelectionView(Context context) {
			super(context);
			init();
		}

		private void init() {
			mScroller = new Scroller(getContext());
		}

		private void setTabNum(int num) {
			this.tabNum = num;
			this.invalidate();
		}

		OnTabItemChangedListener mOnTabItemChangedListener;
		public void setSelection(int selection, boolean smothScroll) {
			if (selection == this.selection) {
				return;
			}
			mScroller.forceFinished(true);
			int tabWidth = (int) (getWidth() / tabNum);
			if (smothScroll) {
				mScroller.startScroll(tabWidth * this.selection, 0, tabWidth
						* (selection - this.selection), 0);
				mScroller.extendDuration(500);
			}
			this.selection = selection;
			mOnTabItemChangedListener.onTabItemChanged(selection);
			this.invalidate();
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			int width = this.getWidth();
			int height = this.getHeight();
			// draw the line first
			canvas.drawLine(0, height - 2, width, height - 2, linePaint);

			if (selection < 0) {
				return;
			}

			int tabWidth = (int) (width / tabNum);
			// draw animation until it is finished ,if any
			while (mScroller.computeScrollOffset()) {
				canvas.drawRect(mScroller.getCurrX(), 0, mScroller.getCurrX()
						+ tabWidth, height, selectionPaint);
				this.invalidate();
				return;
			}
			// no more animation is going on,draw selection
			switch(mode){
			case 0:
				canvas.drawRect(tabWidth * selection, 0,
						tabWidth * (selection + 1), height, selectionPaint);
				break;
			case 1:
			case 2:
				canvas.drawRect(tabWidth * (selection + percent), 0,
						tabWidth * (selection + 1 + percent), height, selectionPaint);
				break;
			}
			
		}
	}
}

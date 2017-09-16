package com.tianpingpai.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

public class TabBarView extends HorizontalScrollView {
	public interface IconTabProvider {
		public int getPageIconResId(int position);
	}

	private static final int STRIP_HEIGHT = 6;
	public final Paint mPaint;

	private int mStripHeight;
	private float mOffset = 0f;
	public static int mSelectedTab = 0;
	public ViewPager pager;

	public static int tabCount;
	private final PageListener pageListener = new PageListener();
	public OnPageChangeListener delegatePageListener;

	public TabBarView(Context context) {
		this(context, null);
	}

	public TabBarView(Context context, AttributeSet attrs) {
		this(context, attrs, android.R.attr.actionBarTabBarStyle);
	}

	public TabBarView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setWillNotDraw(false);
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setColor(mStripColor);

		mStripHeight = (int) (STRIP_HEIGHT
				* getResources().getDisplayMetrics().density + .5f);
		mTabContainer = new LinearLayout(getContext());
		mTabContainer.setOrientation(LinearLayout.HORIZONTAL);

		mTabContainer.setLayoutParams(new LayoutParams(
				LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.MATCH_PARENT));
		this.addView(mTabContainer);
	}

	private int mStripColor = Color.WHITE;
	private int mNormalColor = Color.GRAY;

	public void setStrechMode(int mode) {
		mTabContainer.setLayoutParams(new LayoutParams(mode,
				android.view.ViewGroup.LayoutParams.MATCH_PARENT));
	}

	public void setStripColor(int color) {
		if (mPaint.getColor() != color) {
			mPaint.setColor(color);
			invalidate();
			mStripColor = color;
		}
	}

	public void setStripHeight(int height) {
		if (mStripHeight != height) {
			mStripHeight = height;
			invalidate();
		}
	}

	public void setSelectedTab(int tabIndex) {
		if (tabIndex < 0) {
			tabIndex = 0;
		}
		final int childCount = getChildCount();
		if (tabIndex >= childCount) {
			tabIndex = childCount - 1;
		}
		if (mSelectedTab != tabIndex) {
			mSelectedTab = tabIndex;
			invalidate();
		}
	}

	public void setOffset(int position, float offset) {
		if (mOffset != offset) {
			mOffset = offset;
			invalidate();
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		// Draw the strip manually
		View child = mTabContainer.getChildAt(mSelectedTab);
		int height = getHeight();
		if (child != null) {
			float left = child.getLeft();
			float right = child.getRight();
			if (mOffset > 0f && mSelectedTab < tabCount - 1) {
				View nextChild = mTabContainer.getChildAt(mSelectedTab + 1);
				if (nextChild != null) {
					final float nextTabLeft = nextChild.getLeft();
					final float nextTabRight = nextChild.getRight();
					left = (mOffset * nextTabLeft + (1f - mOffset) * left);
					right = (mOffset * nextTabRight + (1f - mOffset) * right);
				}
			}
			canvas.drawRect(left, height - mStripHeight, right, height, mPaint);
		}
	}

	public void setViewPager(ViewPager pager) {
		this.pager = pager;

		if (pager.getAdapter() == null) {
			throw new IllegalStateException(
					"ViewPager does not have adapter instance.");
		}

		pager.removeOnPageChangeListener(pageListener);
		pager.addOnPageChangeListener(pageListener);
		notifyDataSetChanged();
	}

	private class PageListener implements OnPageChangeListener {

		@Override
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {

			mSelectedTab = position;
			mOffset = positionOffset;

			invalidate();

			if (delegatePageListener != null) {
				delegatePageListener.onPageScrolled(position, positionOffset,
						positionOffsetPixels);
			}

		}

		@Override
		public void onPageScrollStateChanged(int state) {
			if (state == ViewPager.SCROLL_STATE_IDLE) {

			}

			if (delegatePageListener != null) {
				delegatePageListener.onPageScrollStateChanged(state);
			}
		}

		@Override
		public void onPageSelected(int position) {
			if (delegatePageListener != null) {
				delegatePageListener.onPageSelected(position);
			}

			for (int i = 0; i < mTabContainer.getChildCount(); i++) {
				View child = mTabContainer.getChildAt(i);
				if (child instanceof TabView) {
					TabView tv = (TabView) child;
					tv.setTextColor(mNormalColor);
				}
			}
			TabView c = (TabView) mTabContainer.getChildAt(position);
			c.setTextColor(mStripColor);

			if (c.getRight() > getScrollX() + getWidth()) {
				smoothScrollTo(c.getLeft(), 0);
			}

			if (c.getLeft() < getScrollX()) {
				smoothScrollTo(c.getRight() - getWidth(), 0);
			}
		}

	}

	public void notifyDataSetChanged() {

		mTabContainer.removeAllViews();
		tabCount = pager.getAdapter().getCount();
		for (int i = 0; i < tabCount; i++) {
			addTabViewL(i, pager.getAdapter().getPageTitle(i).toString(), 0);
		}
		getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {
					@SuppressLint("NewApi")
					@SuppressWarnings("deprecation")
					@Override
					public void onGlobalLayout() {

						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
							getViewTreeObserver().removeOnGlobalLayoutListener(
									this);
						} else {
							getViewTreeObserver().removeGlobalOnLayoutListener(
									this);
						}
						mSelectedTab = pager.getCurrentItem();
					}
				});

	}

	LinearLayout mTabContainer;

	private void addTabViewL(final int i, String string, int pageIconResId) {
		TabView tab = new TabView(getContext());
		tab.setText(string, pageIconResId);
		if (i == mSelectedTab) {
			tab.setTextColor(mStripColor);
		} else {
			tab.setTextColor(mNormalColor);
		}
		tab.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				pager.setCurrentItem(i);
			}
		});

//		int width = getWidth() / 3;
//		
//		TableRow.LayoutParams p = new TableRow.LayoutParams(0,
//				android.widget.LinearLayout.LayoutParams.MATCH_PARENT, 1f);
//		tab.setLayoutParams(p);
		
		Log.e("xx","getWidth:" + getWidth());
		
		mTabContainer.addView(tab);
	}

	public void setOnPageChangeListener(OnPageChangeListener listener) {
		this.delegatePageListener = listener;
	}
}
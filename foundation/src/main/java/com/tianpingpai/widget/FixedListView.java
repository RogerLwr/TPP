package com.tianpingpai.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;

public class FixedListView extends ListView {

	public FixedListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FixedListView(Context context) {
		super(context);
	}

	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setOverScrollMode(View.OVER_SCROLL_NEVER);
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

}

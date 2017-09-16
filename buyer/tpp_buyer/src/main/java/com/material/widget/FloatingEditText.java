package com.material.widget;

import com.tianpingpai.buyer.R;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.EditText;

public class FloatingEditText extends EditText {
	private int mColor;
	private int mHighlightedColor;
	private int mUnderlineHeight;
	private int mUnderlineHighlightedHeight;
	private Rect lineRect = new Rect();

	private Paint mHintPaint;

	public FloatingEditText(Context context) {
		this(context, null);
	}

	public FloatingEditText(Context context, AttributeSet attrs) {
		this(context, attrs, android.R.attr.editTextStyle);
	}

	@SuppressWarnings("deprecation")
	public FloatingEditText(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, android.R.attr.editTextStyle);
		TypedArray attributes = context.obtainStyledAttributes(attrs,
				R.styleable.FloatingEditText);
		mColor = attributes.getColor(
				R.styleable.FloatingEditText_floating_edit_text_color,
				getResources().getColor(R.color.floating_edit_text_color));
		mHighlightedColor = attributes
				.getColor(
						R.styleable.FloatingEditText_floating_edit_text_highlighted_color,
						getResources().getColor(
								R.color.floating_edit_text_highlighted_color));
		mUnderlineHeight = attributes
				.getDimensionPixelSize(
						R.styleable.FloatingEditText_floating_edit_text_underline_height,
						getResources().getDimensionPixelSize(
								R.dimen.floating_edit_text_underline_height));
		mUnderlineHighlightedHeight = attributes
				.getDimensionPixelSize(
						R.styleable.FloatingEditText_floating_edit_text_underline_highlighted_height,
						getResources()
								.getDimensionPixelSize(
										R.dimen.floating_edit_text_underline_highlighted_height));
		mHintPaint = new Paint();
		mHintPaint.setAntiAlias(true);

		Drawable drawable = new Drawable() {
			@Override
			public void draw(Canvas canvas) {
				if (isFocused()) {
					Rect rect = getThickLineRect(canvas);
					mHintPaint.setColor(mHighlightedColor);
					canvas.drawRect(rect, mHintPaint);
				} else {
					Rect rect = getThinLineRect(canvas);
					mHintPaint.setColor(mColor);
					canvas.drawRect(rect, mHintPaint);
				}
			}

			@Override
			public void setAlpha(int alpha) {
				mHintPaint.setAlpha(alpha);
			}

			@Override
			public void setColorFilter(ColorFilter colorFilter) {
				mHintPaint.setColorFilter(colorFilter);
			}

			@Override
			public int getOpacity() {
				return PixelFormat.TRANSPARENT;
			}
		};
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
			setBackgroundDrawable(drawable);
		} else {
			setBackground(drawable);
		}
		int paddingTop = dpToPx(12);
		int paddingBottom = dpToPx(10);
		setPadding(getPaddingLeft(), paddingTop, getPaddingRight(),
				paddingBottom);
	}

	private Rect getThinLineRect(Canvas canvas) {
		lineRect.left = 0;
		lineRect.top = canvas.getHeight() - mUnderlineHeight - dpToPx(9);
		lineRect.right = getWidth();
		lineRect.bottom = canvas.getHeight() - dpToPx(8.6f);
		return lineRect;
	}

	private Rect getThickLineRect(Canvas canvas) {
		lineRect.left = 0;
		lineRect.top = canvas.getHeight() - mUnderlineHighlightedHeight
				- dpToPx(9);
		lineRect.right = getWidth();
		lineRect.bottom = canvas.getHeight() - dpToPx(8.6f);
		return lineRect;
	}

	public void setNormalColor(int color) {
		this.mColor = color;
		invalidate();
	}

	public void setHighlightedColor(int color) {
		this.mHighlightedColor = color;
		invalidate();
	}

	public static int dpToPx(float dp) {
		return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
	}
}

package com.tianpingpai.ui;

import com.tianpingpai.foundation.R;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class InputDialog extends DialogFragment {
	private View rootView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Translucent_NoTitleBar);
	}
	
	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.dialog_action_sheet,
				null);
		
		
		ValueAnimator anim = ValueAnimator.ofFloat(0, 1); // animate from 0 to 1
		final long start = System.currentTimeMillis();
		final int duration = 1200;
		anim.setDuration(duration); // for 300 ms
		anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				long now = System.currentTimeMillis();
				int alpha = (int) ((now - start) * 1.0 / duration * 50);
				if(now < 900){
					alpha = 0;
				}
				alpha = Math.min(alpha, 50);
				rootView.setBackgroundColor(Color.argb(alpha, 0, 0, 0));
			}
		});
		anim.start();
		return rootView;
	}
}

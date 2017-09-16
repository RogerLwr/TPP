package com.tianpingpai.ui;

import android.app.Activity;
import android.content.Intent;

public interface ActivityEventListener {
	void onActivityCreated(Activity a);
	void willSetContentView(Activity a);
	void didSetContentView(Activity a);

	void onActivityResumed(Activity a);
	void onActivityPaused(Activity a);

	void onActivityDestroyed(Activity a);
	boolean onBackKeyDown(Activity a);
	void onActivityResult(Activity a,int requestCode, int resultCode, Intent data);
}

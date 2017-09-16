package com.brother.tpp.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brother.tpp.tools.CommonUtil;
import com.tianpingpai.buyer.R;
import com.umeng.analytics.MobclickAgent;

/** 帮助界面
 * @author lwr
 *
 */
public class HelpActivity extends Activity implements OnClickListener{
	private LinearLayout operaLinear,serviceLinear,addserviceLinear , comeBack;
	private ImageView operaAspect,serviceAspect,addserviceAspect;
	private TextView operatx,servicetx,addservicetx;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		init();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(CommonUtil.HELP_PAGE_UMENG); //统计页面
		MobclickAgent.onResume(this); 
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(CommonUtil.HELP_PAGE_UMENG); // 保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息 
		MobclickAgent.onPause(this);
	}
	
	private void init() {
		comeBack =  (LinearLayout) this.findViewById(R.id.common_back);
		operaLinear = (LinearLayout) this.findViewById(R.id.help_opera);
		serviceLinear = (LinearLayout) this.findViewById(R.id.help_service);
		addserviceLinear = (LinearLayout) this.findViewById(R.id.help_addservice);
		
		operaAspect = (ImageView) this.findViewById(R.id.help_opera_aspect);
		serviceAspect = (ImageView) this.findViewById(R.id.help_service_aspect);
		addserviceAspect = (ImageView) this.findViewById(R.id.help_addservice_aspect);
		
		operatx = (TextView) this.findViewById(R.id.help_opera_content);
		servicetx = (TextView) this.findViewById(R.id.help_service_content);
		addservicetx = (TextView) this.findViewById(R.id.help_addservice_content);
	
		comeBack.setOnClickListener(this);
		operaLinear.setOnClickListener(this);
		serviceLinear.setOnClickListener(this);
		addserviceLinear.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.common_back:
			HelpActivity.this.finish();
			break;
		case R.id.help_opera:
			operaAspect.setImageResource(R.drawable.help_up);
			serviceAspect.setImageResource(R.drawable.help_down);
			addserviceAspect.setImageResource(R.drawable.help_down);
			operatx.setVisibility(View.VISIBLE);
			servicetx.setVisibility(View.GONE);
			addservicetx.setVisibility(View.GONE);
			break;
		case R.id.help_service:
			operaAspect.setImageResource(R.drawable.help_down);
			serviceAspect.setImageResource(R.drawable.help_up);
			addserviceAspect.setImageResource(R.drawable.help_down);
			operatx.setVisibility(View.GONE);
			servicetx.setVisibility(View.VISIBLE);
			addservicetx.setVisibility(View.GONE);
			break;
		case R.id.help_addservice:
			operaAspect.setImageResource(R.drawable.help_down);
			serviceAspect.setImageResource(R.drawable.help_down);
			addserviceAspect.setImageResource(R.drawable.help_up);
			operatx.setVisibility(View.GONE);
			servicetx.setVisibility(View.GONE);
			addservicetx.setVisibility(View.VISIBLE);
			break;
		}
	}

}

package com.tianpingpai.buyer.ui;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.parser.HttpResult;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.ContainerActivity;

public class ResultHandler {
	public static boolean handleError(HttpResult<?> result,Fragment fragment){
		if(result.getCode() ==  HttpResult.CODE_AUTH){
			Intent intent = new Intent(fragment.getActivity(),ContainerActivity.class);
			intent.putExtra(ContainerActivity.KEY_CONTENT, LoginViewController.class);
			fragment.getActivity().startActivity(intent);
			fragment.getActivity().finish();
		}else{
			Toast.makeText(ContextProvider.getContext(), result.getDesc(), Toast.LENGTH_SHORT).show();
		}
		return true;
	}

	public static boolean handleError(HttpResult<?> result,BaseViewController vc){
		if(result.getCode() !=  HttpResult.CODE_AUTH){
			Toast.makeText(ContextProvider.getContext(), result.getDesc(), Toast.LENGTH_SHORT).show();
		}
		return true;
	}
}

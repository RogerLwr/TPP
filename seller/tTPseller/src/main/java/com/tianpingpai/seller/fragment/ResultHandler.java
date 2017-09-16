package com.tianpingpai.seller.fragment;

import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.parser.HttpResult;
import com.tianpingpai.ui.BaseViewController;

public class ResultHandler {
	public static boolean handleError(HttpResult<?> result,Fragment fragment){
		if(result.getCode() ==  HttpResult.CODE_AUTH){
//			Intent intent = new Intent(fragment.getActivity(), ContainerActivity.class);
//			intent.putExtra(ContainerActivity.KEY_CONTENT, LoginViewController.class);
//			fragment.getActivity().startActivity(intent);
		}else{
			Toast.makeText(ContextProvider.getContext(), result.getDesc(), Toast.LENGTH_SHORT).show();
		}
		return true;
	}

	public static boolean handleError(HttpResult<?> result,BaseViewController fragment){
		if(result.getCode() ==  HttpResult.CODE_AUTH){
//			Intent intent = new Intent(fragment.getActivity(), ContainerActivity.class);
//			intent.putExtra(ContainerActivity.KEY_CONTENT, LoginViewController.class);
//			fragment.getActivity().startActivity(intent);
		}else{
			Toast.makeText(ContextProvider.getContext(), result.getDesc(), Toast.LENGTH_SHORT).show();
		}
		return true;
	}
}

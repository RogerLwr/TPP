package com.tianpingpai.ui;

import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.tianpingpai.foundation.R;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.utils.DimensionUtil;


public class SubmittingViewController extends BaseViewController {

    public void setActionSheet(ActionSheet actionSheet) {
        this.actionSheet = actionSheet;
    }

    private ActionSheet actionSheet;

    public void setRetryButtonListener(View.OnClickListener retryButtonListener) {
        this.retryButtonListener = retryButtonListener;
    }

    private View.OnClickListener retryButtonListener;

    public void setCancelButtonListener(View.OnClickListener cancelButtonListener) {
        this.cancelButtonListener = cancelButtonListener;
    }

    private View.OnClickListener cancelButtonListener;

    private View loadingContainer;
    private View resultButtonContainer;

    {
        setLayoutId(R.layout.dialog_submit);
    }

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        hideActionBar();
        loadingContainer = rootView.findViewById(R.id.loading_container);
        resultButtonContainer = rootView.findViewById(R.id.result_button_container);
        rootView.findViewById(R.id.retry_button).setOnClickListener(mRetryButtonListener);
        rootView.findViewById(R.id.cancel_button).setOnClickListener(mCancelButtonListener);
    }

    public void show(FragmentActivity a){
        if(actionSheet == null){
            actionSheet = getActionSheet(true);
        }
        actionSheet.setViewController(this);
        actionSheet.setHeight(DimensionUtil.dip2px(300));
        this.setActivity(a);
        actionSheet.setCancelable(false);
        actionSheet.show();
    }

    private View.OnClickListener  mRetryButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            loadingContainer.setVisibility(View.VISIBLE);
            resultButtonContainer.setVisibility(View.INVISIBLE);
            retryButtonListener.onClick(v);
        }
    };

    private View.OnClickListener mCancelButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            actionSheet.dismiss();
            if(cancelButtonListener != null){
                cancelButtonListener.onClick(v);
            }
        }
    };

    public boolean handleBack(){
        return actionSheet.isShowing();
    }

    public void showButtons() {
        loadingContainer.setVisibility(View.INVISIBLE);
        resultButtonContainer.setVisibility(View.VISIBLE);
    }

    public void showLoading(){
        loadingContainer.setVisibility(View.VISIBLE);
        resultButtonContainer.setVisibility(View.INVISIBLE);
    }
}

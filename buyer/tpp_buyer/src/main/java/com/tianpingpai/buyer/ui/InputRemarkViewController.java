package com.tianpingpai.buyer.ui;

import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.tianpingpai.buyer.R;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.widget.ContainsEmojiEditText;

import java.util.Timer;
import java.util.TimerTask;

@Layout(id = R.layout.vc_input_remark)
public class InputRemarkViewController extends BaseViewController {

    public interface OnRemarkConfirmListener{
        void onRemarkConfirm(String text);
    }

    private String mText;
    private ContainsEmojiEditText remarkEditText;

    public void setOnRemarkConfirmListener(OnRemarkConfirmListener onRemarkConfirmListener) {
        this.onRemarkConfirmListener = onRemarkConfirmListener;
    }

    private OnRemarkConfirmListener onRemarkConfirmListener;

    public void setText(String text){
        mText = text;
    }

    @Override
    protected void onConfigureView() {
        super.onConfigureView();
        View actionBarView = setActionBarLayout(R.layout.ab_title_green);
        TextView submitButton = (TextView) actionBarView.findViewById(R.id.ab_right_button);
        submitButton.setText("确定");
        submitButton.setOnClickListener(doneButtonListener);
        Toolbar toolbar = (Toolbar) actionBarView.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(closeButtonListener);
        remarkEditText = (ContainsEmojiEditText) getView().findViewById(R.id.remark_edit_text);
        remarkEditText.setText(mText);
        showInput();
    }

    private void showInput(){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                InputMethodManager m = (InputMethodManager) remarkEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }, 300);
    }

    private View.OnClickListener closeButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //TODO
            dismiss();
        }
    };

    private View.OnClickListener doneButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dismiss();
            onRemarkConfirmListener.onRemarkConfirm(remarkEditText.getText().toString());
        }
    };


    private void dismiss(){
        hideKeyboard();
        ActionSheet as = (ActionSheet) getViewTransitionManager();
        as.dismiss();
    }

}

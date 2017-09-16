package com.tianpingpai.crm.ui;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.tianpingpai.crm.R;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.BaseViewController;

@SuppressWarnings("unused")
public class InputTextViewController extends BaseViewController {

    private String viewControllerTitle;
    private String contentText;
    private int number = 50;
    private ActionSheet actionSheet;
    private EditText contentEditText;
    private TextView numberTextView;

    private OnContentTextListener mOnContentTextListener;

    public interface OnContentTextListener{

        void onGetText(String content);
    }

    public void setOnContentTextListener(OnContentTextListener listener){
        this.mOnContentTextListener = listener;
    }

    public void setActionSheet(ActionSheet actionSheet){
        this.actionSheet = actionSheet;
    }

    public void setViewControllerTitle(String title){
        this.viewControllerTitle = title;
    }

    public void setContentText(String text){
        this.contentText = text;
    }

    public void setNumber(int number){
        this.number = number;
    }

    {
        setLayoutId(R.layout.vc_input_text);
    }

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);

        contentEditText = (EditText) rootView.findViewById(R.id.input_edt);
        contentEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(number)});
        contentEditText.addTextChangedListener(contentTextWatcher);
        numberTextView = (TextView) rootView.findViewById(R.id.number_tv);
        int b = 0;
        if(contentText != null){
            contentEditText.setText(contentText);
            b = number - contentText.length();
        }else{
            b = number;
        }
        numberTextView.setText("限制"+b+"字");

        View actionBar = setActionBarLayout(com.tianpingpai.foundation.R.layout.ab_select_city);
        actionBar.findViewById(com.tianpingpai.foundation.R.id.ab_close_button).setVisibility(View.VISIBLE);
        actionBar.findViewById(com.tianpingpai.foundation.R.id.ab_right_button).setVisibility(View.VISIBLE);
        actionBar.findViewById(com.tianpingpai.foundation.R.id.ab_right_button).setEnabled(true);
        actionBar.findViewById(com.tianpingpai.foundation.R.id.ab_close_button).setOnClickListener(closeButtonListener);
        actionBar.findViewById(com.tianpingpai.foundation.R.id.ab_right_button).setOnClickListener(okayButtonListener);
        setTitle(viewControllerTitle);
    }

    private TextWatcher contentTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            int b = number - editable.length();
            numberTextView.setText("限制"+b+"字");
        }
    };

    private View.OnClickListener closeButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            actionSheet.dismiss();
            InputTextViewController.this.hideKeyboard();
        }
    };

    private View.OnClickListener okayButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            actionSheet.dismiss();
            InputTextViewController.this.hideKeyboard();
            contentText = contentEditText.getText().toString();
            if(mOnContentTextListener != null){
                mOnContentTextListener.onGetText(contentText);
            }
        }
    };
}

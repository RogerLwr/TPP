package com.tianpingpai.seller.ui;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.seller.R;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.Layout;

@ActionBar(title = "商品描述",rightText = "保存")
@Layout(id = R.layout.ui_input_text)
public class InputTextViewController extends BaseViewController{

    @Binding(id = R.id.word_count_text_view)
    private TextView wordCountTextView;
    @Binding(id = R.id.hint_text_view)
    private TextView hintTextView;

    public interface OnInputCompleteListener{
        void onInputComplete(String text);
    }

    private EditText editText;

    private String newRemark;

    public void setTitle(String title) {
        this.title = title;
    }

    private String title;

    public void setNewRemark(String newRemark) {
        this.newRemark = newRemark;
    }

    public void setOnInputCompleteListener(OnInputCompleteListener onInputCompleteListener) {
        this.onInputCompleteListener = onInputCompleteListener;
    }

    private OnInputCompleteListener onInputCompleteListener;

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        editText = (EditText) rootView.findViewById(R.id.edit_text);
        rootView.findViewById(R.id.ab_right_button).setOnClickListener(saveButtonListener);
        editText.setText(newRemark);
        editText.addTextChangedListener(valueTextWatcher);
        int length = editText.getText().toString().length();
        if(length > 100){
            wordCountTextView.setTextColor(wordCountTextView.getResources().getColor(R.color.red));
        }else {
            wordCountTextView.setTextColor(wordCountTextView.getResources().getColor(R.color.gray_99));

        }
        if(title != null){
            super.setTitle(title);
            hintTextView.setText(title + ":");
        }
        wordCountTextView.setText(length + "/100");
//        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
//        toolbar.setNavigationOnClickListener(backButtonListener);
    }

    private View.OnClickListener saveButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            onInputCompleteListener.onInputComplete(editText.getText().toString());
            int length = editText.getText().toString().length();
            if(length > 100){
                Toast.makeText(ContextProvider.getContext(), "字数必须小于100", Toast.LENGTH_LONG).show();
                return;
            }
            ActionSheet as = (ActionSheet) getViewTransitionManager();
            as.dismiss();
            hideKeyboard();
        }
    };

    private TextWatcher valueTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            int length = editText.getText().toString().length();
            if(length > 100){
                wordCountTextView.setTextColor(wordCountTextView.getResources().getColor(R.color.red));
            }else {
                wordCountTextView.setTextColor(wordCountTextView.getResources().getColor(R.color.gray_99));

            }
            wordCountTextView.setText(length +"/100");
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
}

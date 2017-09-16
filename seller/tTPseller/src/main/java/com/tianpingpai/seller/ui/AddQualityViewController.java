package com.tianpingpai.seller.ui;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.seller.R;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.OnClick;

@SuppressWarnings("unused")
@ActionBar(title = "添加品质描述",rightText = "保存")
@Layout(id = R.layout.ui_add_quality)
public class AddQualityViewController extends BaseViewController{

    public interface OnQualityOnConfirmListener {
        boolean onQualityConfirm(String qualityName,String qualityDescription);
    }

    public void setOnQualityConfirmListener(OnQualityOnConfirmListener onQualityOnConfirmListener) {
        this.onQualityOnConfirmListener = onQualityOnConfirmListener;
    }

    private OnQualityOnConfirmListener onQualityOnConfirmListener;

    @Binding(id = R.id.quality_name_edit_text)
    private EditText qualityNameEditText;
    @Binding(id = R.id.quality_description_edit_text)
    private EditText qualityDescriptionEditText;
    @Binding(id = R.id.name_limit_text_view)
    private TextView nameLimitTextView;
    @Binding(id = R.id.description_limit_text_view)
    private TextView descriptionLimitTextView;

    public void setName(String name) {
        this.name = name;
    }

    private String name;

    public void setDescription(String description) {
        this.description = description;
    }

    private String description;

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        qualityNameEditText.setText(name);
        qualityDescriptionEditText.setText(description);

        qualityNameEditText.addTextChangedListener(nameLimitTextWatcher);
        qualityDescriptionEditText.addTextChangedListener(descriptionLimitTextWatcher);

        if(name == null && description == null){
            qualityNameEditText.setEnabled(true);
        }else{
            qualityNameEditText.setEnabled(false);
        }
    }

    @OnClick(R.id.ab_right_button)
    private View.OnClickListener doneButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String name = qualityNameEditText.getText().toString().trim();
            if(TextUtils.isEmpty(name)){
                Toast.makeText(ContextProvider.getContext(),"品质名称不能为空!",Toast.LENGTH_LONG).show();
                return;
            }
            String description = qualityDescriptionEditText.getText().toString();
            if(TextUtils.isEmpty(description)){
                Toast.makeText(ContextProvider.getContext(),"品质描述不能为空!",Toast.LENGTH_LONG).show();
                return;
            }
            boolean accepted = onQualityOnConfirmListener.onQualityConfirm(name, qualityDescriptionEditText.getText().toString());
            if(accepted){
                ActionSheet as = (ActionSheet) getViewTransitionManager();
                as.dismiss();
                hideKeyboard();
            }else{
                Toast.makeText(ContextProvider.getContext(),"添加的品质名不能与已有的品质名重复",Toast.LENGTH_LONG).show();
            }
        }
    };

    private TextWatcher nameLimitTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            nameLimitTextView.setText(String.format("%d/10",qualityNameEditText.length()));
        }
    };

    private TextWatcher descriptionLimitTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            descriptionLimitTextView.setText(String.format("%d/100",qualityDescriptionEditText.length()));
        }
    };
}

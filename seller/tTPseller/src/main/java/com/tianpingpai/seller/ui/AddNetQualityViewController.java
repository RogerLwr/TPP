package com.tianpingpai.seller.ui;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.model.Model;
import com.tianpingpai.seller.R;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.utils.NumberUtils;
import com.tianpingpai.utils.PriceFormat;

@ActionBar(title = "添加净含量",rightText = "保存")
@Layout(id = R.layout.ui_add_net_quality)
public class AddNetQualityViewController extends BaseViewController {

    public void setUnit(String unit) {
        this.unit = unit;
    }

    private String unit;

    public void setOnModelsSelectListener(OnModelsSelectListener onModelsSelectListener) {
        this.onModelsSelectListener = onModelsSelectListener;
    }

    private OnModelsSelectListener onModelsSelectListener;

    @Binding(id = R.id.value_edit_text)
    private EditText numberEditText;
    @Binding(id = R.id.unit_text_view)
    private TextView unitTextView;

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        unitTextView.setText(unit);
    }

    @OnClick(R.id.ab_right_button)
    private View.OnClickListener doneButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            double number = 0;
            try{
                number = Double.parseDouble(numberEditText.getText().toString());
            }catch (NumberFormatException e){
                number = 0;
            }
            if(number <= 0){
                Toast.makeText(ContextProvider.getContext(), "请输入有效的数字", Toast.LENGTH_LONG).show();
                return;
            }
            Model m = new Model();
            int numberInt = (int) number;
            if(NumberUtils.equals(numberInt,number)){
                m.set("value",String.valueOf(numberInt));
            }else{
                m.set("value",String.valueOf(number));
            }
            m.set("unit_name", unit);
            if(!onModelsSelectListener.isValid(m)){
                Toast.makeText(ContextProvider.getContext(),"该净含量已存在!",Toast.LENGTH_LONG).show();
                return;
            }
            onModelsSelectListener.onModelsSelect(m);
            ActionSheet as = (ActionSheet) getViewTransitionManager();
            as.dismiss();
        }
    };
}

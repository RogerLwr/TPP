package com.tianpingpai.seller.ui;

import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tianpingpai.core.ContextProvider;
import com.tianpingpai.model.Model;
import com.tianpingpai.seller.R;
import com.tianpingpai.ui.ActionBar;
import com.tianpingpai.ui.ActionSheet;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ButtonGroup;
import com.tianpingpai.ui.Layout;
import com.tianpingpai.ui.BaseViewController;
import com.tianpingpai.ui.OnClick;
import com.tianpingpai.utils.NumberUtils;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@SuppressWarnings("unused")
@ActionBar(title = "添加规格",rightText = "保存")
@Layout(id = R.layout.ui_add_spec)
public class AddSpecViewController extends BaseViewController{

    public void setOnModelsSelectListener(OnModelsSelectListener onModelsSelectListener) {
        this.onModelsSelectListener = onModelsSelectListener;
    }

    private OnModelsSelectListener onModelsSelectListener;

    @Binding(id = R.id.value_edit_text)
    private EditText numberEditText;
    @Binding(id = R.id.unit_container)
    private FlowLayout unitContainer;
    @Binding(id = R.id.unit_text_view)
    private TextView unitTextView;

    private String title;

    public void setTitle(String title){
        this.title = title;
    }

    public void setUnitList(List<Model> unitList) {
        HashSet<String> units = new HashSet<>();
        for(Model m:unitList){
            if(units.contains(m.getString("unit_name"))){
                continue;
            }
            units.add(m.getString("unit_name"));
            this.unitList.add(m);
        }
    }

    private ArrayList<Model> unitList = new ArrayList<>();

    private ButtonGroup unitButtonContainer = new ButtonGroup();

    @Override
    protected void onConfigureView(View rootView) {
        super.onConfigureView(rootView);
        unitButtonContainer.setOnCheckedChangeListener(unitCheckChangedListener);
        for(Model m:unitList){
            CompoundButton view = (CompoundButton) getActivity().getLayoutInflater().inflate(R.layout.view_single_selection_green,null);
            unitContainer.addView(view);
            view.setText(m.getString("unit_name"));
            unitButtonContainer.add(view, view);
        }
        unitButtonContainer.select(0);
        if(title != null){
            super.setTitle(title);
        }
    }

    private CompoundButton.OnCheckedChangeListener unitCheckChangedListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
           select(unitButtonContainer.indexOf(buttonView));
        }
    };

    private Model selectedUnit;

    private void select(int index){
        Model m = unitList.get(index);
        selectedUnit = m;
        unitTextView.setText(m.getString("unit_name"));
    }

    @OnClick(R.id.ab_right_button)
    private View.OnClickListener doneButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            double number;
            try{
                number = Double.parseDouble(numberEditText.getText().toString());
            }catch (NumberFormatException e){
                number = 0;
            }
            if(number <= 0){
                Toast.makeText(ContextProvider.getContext(),"请输入有效的数字",Toast.LENGTH_LONG).show();
                return;
            }
            Model model = new Model();
            Model.copy(selectedUnit,model,"unit_name","unit_id","attr_id");
//            model.set("unit_name", selectedUnit.getString("unit_name"));
//            model.set("attr_id",selectedUnit.getInt("attr_id"));
            model.set("id",0);

            int numberInt = (int) number;
            if(NumberUtils.equals(numberInt, number)){
                model.set("value", String.valueOf(numberInt));
            }else{
                model.set("value", String.valueOf(number));
            }
            if(!onModelsSelectListener.isValid(model)){
                Toast.makeText(ContextProvider.getContext(),"该规格/净含量已存在!",Toast.LENGTH_LONG).show();
                return;
            }
            onModelsSelectListener.onModelsSelect(model);
            ActionSheet as = (ActionSheet) getViewTransitionManager();
            as.dismiss();
        }
    };
}

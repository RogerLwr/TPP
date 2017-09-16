package com.tianpingpai.ui;

import android.view.View;
import android.widget.CompoundButton;

import java.util.ArrayList;

public class ButtonGroup {
    private ArrayList<CompoundButton> buttons = new ArrayList<>();

    private boolean enabled = true;
    private View containerView;

    public void setEnabled(boolean enabled){
        this.enabled = enabled;
        for(CompoundButton b:buttons){
            b.setEnabled(enabled);
            b.setClickable(enabled);
        }
        containerView.setEnabled(enabled);
        containerView.setClickable(enabled);
    }

    public void add(CompoundButton cb,View container){
        containerView = container;
        container.setTag(cb);
        container.setOnClickListener(containerViewOnClickListener);
        cb.setOnCheckedChangeListener(onCheckedChangeListener);
        if(cb != container) {
            cb.setClickable(false);
        }
        buttons.add(cb);
    }

    public void select(int index){
        buttons.get(index).setChecked(true);
    }

    public int indexOf(CompoundButton button){
        return buttons.indexOf(button);
    };

    private View.OnClickListener containerViewOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(!enabled){
                return;
            }
            CompoundButton cb = (CompoundButton) v.getTag();
            if(cb.isChecked()){
                return;
            }
            for(CompoundButton button:buttons){
                if(button != cb){
                    button.setChecked(false);
                }
            }
            cb.setChecked(true);
        }
    };

    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(!enabled){
                return;
            }
            for(CompoundButton button:buttons){
                if(button != buttonView){
                    button.setOnCheckedChangeListener(null);
                    button.setChecked(false);
                    button.setOnCheckedChangeListener(onCheckedChangeListener);
                }
            }
            if(callback != null){
                callback.onCheckedChanged(buttonView, isChecked);
            }
        }
    };

    private CompoundButton.OnCheckedChangeListener callback;

    public void setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener l){
        callback = l;
    }

    public void clear() {
        buttons.clear();
    }
}

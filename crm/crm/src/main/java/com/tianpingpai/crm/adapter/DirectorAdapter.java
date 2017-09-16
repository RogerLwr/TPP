package com.tianpingpai.crm.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.tianpingpai.crm.R;
import com.tianpingpai.model.Model;
import com.tianpingpai.ui.Binder;
import com.tianpingpai.ui.Binding;
import com.tianpingpai.ui.ModelAdapter;

import java.util.ArrayList;

@SuppressWarnings("unused")
public class DirectorAdapter extends ModelAdapter<Model> {

    private Activity activity;
    public void setActivity(Activity a){
        this.activity = a;
    }

    @Override
    protected ViewHolder<Model> onCreateViewHolder(LayoutInflater inflater) {
        @SuppressLint("InflateParams") View convertView = activity.getLayoutInflater().inflate(R.layout.item_director, null);
        return new DirectorViewHolder(convertView);
    }

    private class DirectorViewHolder implements ViewHolder<Model>{

        private View view;
        @Binding(id = R.id.director_text_view,format = "{{display_name}}")
        private TextView directorTextView;
        private Model model;
        Binder binder = new Binder();

        DirectorViewHolder(View v){
            v.setTag(this);
            this.view = v;
            binder.bindView(this,v);
        }

        @Override
        public void setModel(Model m) {
            this.model = m;
            binder.bindData(model);
        }

        @Override
        public View getView() {
            return view;
        }
    }
}

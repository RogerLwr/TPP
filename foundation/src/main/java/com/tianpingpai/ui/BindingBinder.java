package com.tianpingpai.ui;

import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;

import com.tianpingpai.utils.TextUtils;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import liqp.Template;
import liqp.filters.Filter;

public class BindingBinder implements ViewBinder<Binding> {

    private SparseArray<View> views = new SparseArray<>();
    private HashMap<String,Template> templateCacheMap = new HashMap<>();

    @Override
    public void bindView(Binder binder, Object object, Field field, Binding b, View view) {
        View v = view.findViewById(b.id());
        views.append(b.id(), v);
        try {
            boolean accessible = field.isAccessible();
            field.setAccessible(true);
            field.set(object, v);
            field.setAccessible(accessible);
        } catch (IllegalAccessException | IllegalArgumentException e) {
            e.printStackTrace();
            Log.e("xx", "id:" + b.id() + ":" + v);
        }
    }

    @Override
    public void bindData(Binding b, Map<String, Object> model) {
        View v = views.get(b.id());
        if (v != null) {
            if (v instanceof TextView) {
                bindTextView((TextView) v, model, b);
            }
        }
    }

    private void bindTextView(TextView tv, Map<String, Object> map, Binding b) {
        if(TextUtils.isEmpty(b.format())){
            return;
        }
        Template template = templateCacheMap.get(b.format());
        if(template == null){
            template = Template.parse(b.format());
            templateCacheMap.put(b.format(),template);
        }

        String value = template.render(map);
        if (!TextUtils.isEmpty(value)) {
            tv.setText(value);
        }else{
            tv.setText("");
        }
    }

    private static DecimalFormat moneyFormat = new DecimalFormat("###0.0");

    static {
        Filter.registerFilter(new Filter("money") {
            @Override
            public Object apply(Object value, Object... objects) {
                double amount;
                if (value == null) {
                    amount = 0.00;
                } else {
                    if (value instanceof Double) {
                        amount = (Double) value;
                    } else if (value instanceof Integer) {
                        amount = (Integer) value;
                    } else if (value instanceof Float) {
                        amount = (Float) value;
                    } else {
                        String text = super.asString(value);
                        try {
                            amount = Double.parseDouble(text);
                        } catch (NumberFormatException e) {
                            amount = 0.00;
                        }
                    }
                }
                return moneyFormat.format(amount);
            }
        });
        Filter.registerFilter(new Filter("limit") {
            @Override
            public Object apply(Object value, Object... params) {
                String text = super.asString(value);
                // check if an optional parameter is provided
                int limit = params.length == 0 ? 0 : super.asNumber(params[0]).intValue();

                if (limit <= 0) {
                    return text;
                }

                StringBuilder builder = new StringBuilder();
                if (text.length() < limit) {
                    builder.append(text);
                    for (int i = 0; i < limit - text.length(); i++) {
                        builder.append(" ");
                    }
                } else {
                    builder.append(text.substring(0, limit));
                }
                return builder.toString();
            }
        });
    }
}

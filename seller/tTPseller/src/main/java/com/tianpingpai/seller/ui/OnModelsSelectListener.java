package com.tianpingpai.seller.ui;

import com.tianpingpai.model.Model;

public interface OnModelsSelectListener {
    void onModelsSelect(Model... models);
    boolean isValid(Model... models);
}

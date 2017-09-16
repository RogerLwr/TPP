package com.tianpingpai.buyer.parser;

import com.tianpingpai.model.Model;
import com.tianpingpai.parser.ListResult;

public class CollectionProductListResult<T> extends ListResult<T> {

    private Model model;

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }
}

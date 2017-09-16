package com.tianpingpai.model;

import java.io.Serializable;


public class CategoryInfo implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String name;
    private String category_id;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getCategory_id() {
        return category_id;
    }
    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }
    @Override
    public String toString() {
        return "SortInfo [name=" + name + ", category_id=" + category_id
                +"]";
    }

}

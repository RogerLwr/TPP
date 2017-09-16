package com.tianpingpai.db;

import android.content.ContentValues;
import android.database.Cursor;

import com.tianpingpai.model.ProductModel;

public class ProductDao extends BaseDao<ProductModel> {
	@Override
	protected void fillContentValues(ContentValues cv, ProductModel e) {
		super.fillContentValues(cv, e);
	}
	
	@Override
	protected String getTableName() {
		return "product_table";
	}

	@Override
	protected void fillFields(ProductModel e, Cursor c) {
		super.fillFields(e, c);
	}
}

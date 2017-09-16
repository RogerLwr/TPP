package com.tianpingpai.buyer.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.Pair;

import com.tianpingpai.buyer.model.ShopRemark;
import com.tianpingpai.db.BaseDao;

public class ShopRemarkDao extends BaseDao<ShopRemark> {
	
	public ShopRemark getBySellerId(long sellerId){
		ShopRemark result = null;
		SQLiteDatabase db = getDb();
		Cursor cursor = db.rawQuery("SELECT * FROM [" + getTableName() + "] WHERE [sellerId] = " + sellerId + ";", null);
		while(cursor.moveToNext()){
			result = new ShopRemark();
			fillFields(result, cursor);
		}
		cursor.close();
		return result;
	}
	
	public void deleteBySellerId(long sellerId){
		SQLiteDatabase db = getDb();
		db.delete(getTableName(), "sellerId = " + sellerId, null);
	}
	
	
	@Override
	protected void fillContentValues(ContentValues cv, ShopRemark e) {
		cv.put("sellerId", e.getSellerId());
		cv.put("remark", e.getRemark());
	}
	
	@Override
	protected int getVersion() {
		return 3;
	}

	@Override
	protected void fillFields(ShopRemark e, Cursor c) {
		super.fillFields(e, c);
		e.setSellerId(c.getInt(c.getColumnIndex("sellerId")));
		e.setRemark(c.getString(c.getColumnIndex("remark")));
	}

	@Override
	protected String getDatabaseFileName() {
		return "ramark.db";
	}

	@Override
	protected String getTableName() {
		return "seller_remark_table";
	}

	@Override
	protected Pair<String, String> getPrimaryKeyAndValue(ShopRemark e) {
		return new Pair<>("sellerId", e.getSellerId() + "");
	}

	@Override
	protected void onCreate(SQLiteDatabase db) {
		super.onCreate(db);
		Log.e("xx","============oncreate===========");
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE  TABLE  IF NOT EXISTS [" + getTableName() + "](");
		sb.append("[sellerId] INTEGER PRIMARY KEY  NOT NULL ,");
		sb.append("[remark] TEXT)");
		db.execSQL(sb.toString());
	}
}

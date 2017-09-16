package com.tianpingpai.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Pair;

import com.tianpingpai.model.MarketModel;

import java.util.ArrayList;

public class MarketDao extends BaseDao<MarketModel> {
	@Override
	protected void fillContentValues(ContentValues cv, MarketModel e) {
		super.fillContentValues(cv, e);
		cv.put("id", e.getId());
		cv.put("name", e.getName());
		cv.put("address", e.getAddress());
		cv.put("areaId", e.getAreaId());
		cv.put("weight", e.getWeight());
		if(e.isDefault()){
			cv.put("idDefault", 1);
		}else{
			cv.put("idDefault", 2);
		}
	}
	
	@Override
	protected void fillFields(MarketModel e, Cursor c) {
		super.fillFields(e, c);
		e.setId(c.getInt(c.getColumnIndex("id")));
		e.setName(c.getString(c.getColumnIndex("name")));
		e.setAddress(c.getString(c.getColumnIndex("address")));
		e.setAreaId(c.getInt(c.getColumnIndex("areaId")));
		e.setWeight(c.getInt(c.getColumnIndex("weight")));
		e.setDefault(c.getInt(c.getColumnIndex("idDefault")) == 1);
	}
	
	@Override
	protected void onCreate(SQLiteDatabase db) {
		super.onCreate(db);
		String sql = "CREATE TABLE [market_table] ([id] INTEGER PRIMARY KEY  NOT NULL , [name] TEXT, [address] TEXT, [areaId] TEXT, [weight] INTEGER,[idDefault] INTEGER)";
		db.execSQL(sql);
	}
	
	@Override
	protected String getDatabaseFileName() {
		return "common.db";
	}
	
	@Override
	protected String getTableName() {
		return "market_table";
	}
	
	@Override
	protected int getVersion() {
		return 3;
	}
	
	@Override
	protected Pair<String, String> getPrimaryKeyAndValue(MarketModel e) {
		return new Pair<>("id",String.valueOf(e.getId()));
	}

	public ArrayList<MarketModel> getAll() {
		String sql = "SELECT * FROM [" + getTableName() + "];";
		SQLiteDatabase db = getDb();
		Cursor c = db.rawQuery(sql, null);
		ArrayList<MarketModel> markets = new ArrayList<>();
		while(c.moveToNext()){
			MarketModel market = new MarketModel();
			fillFields(market, c);
			markets.add(market);
		}
		c.close();
		return markets;
	}

	public void clear() {
		SQLiteDatabase db = getDb();
		db.delete(getTableName(), null, null);
	}
}

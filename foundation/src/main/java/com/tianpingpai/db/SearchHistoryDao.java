package com.tianpingpai.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Pair;

import com.tianpingpai.model.SearchHistoryModel;

import java.util.ArrayList;

public class SearchHistoryDao extends BaseDao<SearchHistoryModel> {
	@Override
	protected void fillContentValues(ContentValues cv, SearchHistoryModel e) {
		super.fillContentValues(cv, e);
		cv.put("id", e.getId());
		cv.put("name", e.getName());
	}
	
	@Override
	protected void fillFields(SearchHistoryModel e, Cursor c) {
		super.fillFields(e, c);
		e.setId(c.getInt(c.getColumnIndex("id")));
		e.setName(c.getString(c.getColumnIndex("name")));

	}
	
	@Override
	protected void onCreate(SQLiteDatabase db) {
		super.onCreate(db);
		String sql = "CREATE TABLE [search_history_table] ([id] INTEGER, [name] TEXT)";
		db.execSQL(sql);
	}
	
	@Override
	protected String getDatabaseFileName() {
		return "search.db";
	}
	
	@Override
	protected String getTableName() {
		return "search_history_table";
	}
	
	@Override
	protected int getVersion() {
		return 4;
	}

	@Override
	public boolean contains(SearchHistoryModel searchHistoryModel) {
		boolean contains = false;
		SQLiteDatabase db = getDb();
		Cursor cursor = db.rawQuery("SELECT * FROM [" + getTableName() + "] WHERE [name] = " + "\"" +  searchHistoryModel.getName() + "\""  + ";",null);
		if(cursor.moveToFirst()){
			contains = true;
		}
		cursor.close();
		return contains;
	}

	@Override
	protected Pair<String, String> getPrimaryKeyAndValue(SearchHistoryModel e) {
		return new Pair<>("name",e.getName());
	}

	public ArrayList<SearchHistoryModel> getAll() {
		String sql = "SELECT * FROM [" + getTableName() + "];";
		SQLiteDatabase db = getDb();
		Cursor c = db.rawQuery(sql, null);
		ArrayList<SearchHistoryModel> searchHistoryModels = new ArrayList<>();
		while(c.moveToNext()){
			SearchHistoryModel searchHistoryModel = new SearchHistoryModel();
			fillFields(searchHistoryModel, c);
			searchHistoryModels.add(searchHistoryModel);
		}
		c.close();
		return searchHistoryModels;
	}

	public ArrayList<SearchHistoryModel> getHistoryById(int id) {
		String sql = "SELECT * FROM [" + getTableName() + "] WHERE [id] = "+id+";";
		SQLiteDatabase db = getDb();
		Cursor c = db.rawQuery(sql, null);
		ArrayList<SearchHistoryModel> searchHistoryModels = new ArrayList<>();
		while(c.moveToNext()){
			SearchHistoryModel searchHistoryModel = new SearchHistoryModel();
			fillFields(searchHistoryModel, c);
			searchHistoryModels.add(searchHistoryModel);
		}
		c.close();
		return searchHistoryModels;
	}

	public void clear() {
		SQLiteDatabase db = getDb();
		db.delete(getTableName(), null, null);
	}
	public void clearByStoreId(int storeId) {
		SQLiteDatabase db = getDb();
		db.delete(getTableName(), "id=?", new String[]{"" + storeId});
	}

	@Override
	protected void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		super.onUpgrade(db, oldVersion, newVersion);
		String sql = "CREATE TABLE [search_history_table] ([name] TEXT)";
		db.execSQL(sql);
	}
}

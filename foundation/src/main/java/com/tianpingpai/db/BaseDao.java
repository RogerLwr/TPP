package com.tianpingpai.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Pair;

import com.tianpingpai.core.ContextProvider;

import java.util.Collection;

public abstract class BaseDao <E> {
	
	SQLiteOpenHelper dbHelper;
	
	protected SQLiteDatabase getDb() {
		if(dbHelper == null){
			dbHelper = new SQLiteOpenHelper(ContextProvider.getContext(), getDatabaseFileName(), null, getVersion()) {
				@Override
				public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
					BaseDao.this.onUpgrade(db,oldVersion,newVersion);
				}
				
				@Override
				public void onCreate(SQLiteDatabase db) {
					BaseDao.this.onCreate(db);
				}
			};
		}
		return dbHelper.getWritableDatabase();
	}

	public void save(E e){
		if(contains(e)){
			update(e);
		}else{
			insert(e);
		}
	}
	
	public void save(Collection<E> models){
		for(E e:models){
			save(e);
		}
	}
	
	public boolean contains(E e){
		boolean contains = false;
		Pair<String,String> pkAndValue = getPrimaryKeyAndValue(e);
		SQLiteDatabase db = getDb();
		Cursor cursor = db.rawQuery("SELECT * FROM [" + getTableName() + "] WHERE [" + pkAndValue.first + "] = " + pkAndValue.second+ ";",null);
		if(cursor.moveToFirst()){
			contains = true;
		}
		cursor.close();
		return contains;
	}
	

	public boolean delete(E e){
		SQLiteDatabase db = getDb();
		Pair<String,String> pv =getPrimaryKeyAndValue(e);
		int rn = db.delete(getTableName(), pv.first + " = " + pv.second + " ;", null);
		return rn > 1;
	}
	
	protected long insert(E e) {
		SQLiteDatabase db = getDb();
		ContentValues cv = new ContentValues();
		fillContentValues(cv, e);
		return db.insert(getTableName(), null, cv);
	}
	
	protected boolean update(E e) {
		SQLiteDatabase db = getDb();
		Pair<String,String> pkAndValue = getPrimaryKeyAndValue(e);
		ContentValues cv = new ContentValues();
		fillContentValues(cv, e);
		int numResults = db.update(getTableName(), cv, " " + pkAndValue.first + " = " + "? ;", new String[]{pkAndValue.second});
		return numResults >= 1;
	}
	
	protected void onCreate(SQLiteDatabase db){
		
	}

	protected void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

	}
	
	protected int getVersion() {
		return 1;
	}
	
	protected String getDatabaseFileName(){
		return "xx.db";
	}
	
	protected String getTableName() {
		return "";//TODO
	}
	
	protected Pair<String,String> getPrimaryKeyAndValue(E e) {
		return null;//TODO
	}
	
	protected void fillContentValues(ContentValues cv,E e){
		
	}
	protected void fillFields(E e, Cursor c){
		
	}
}

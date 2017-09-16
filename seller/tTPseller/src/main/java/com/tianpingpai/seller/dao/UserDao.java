package com.tianpingpai.seller.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Pair;

import com.tianpingpai.db.BaseDao;
import com.tianpingpai.seller.model.UserModel;

public class UserDao extends BaseDao<UserModel> {

	private static final String TABLE_NAME = "user_table";

	@Override
	protected void onCreate(SQLiteDatabase db) {
		super.onCreate(db);
		String sql = "CREATE TABLE [" + TABLE_NAME
				+ "] ([userId] INTEGER PRIMARY KEY  NOT NULL  UNIQUE ,"
				+ " [name] TEXT, [phone] TEXT,[grade] INTEGER, [accessToken] TEXT);";
		db.execSQL(sql);
	}

	protected String getTableName() {
		return TABLE_NAME;
	}

	@Override
	protected String getDatabaseFileName() {
		return "user.db";
	}
	
	public UserModel getLast() {
		UserModel user = null;
		SQLiteDatabase db = getDb();
		Cursor cursor = db
				.rawQuery("SELECT * FROM [" + TABLE_NAME + "];", null);
		if (cursor.moveToFirst()) {
			user = new UserModel();
			fillFields(user, cursor);
		}
		cursor.close();
		db.close();
		return user;
	}

	protected Pair<String, String> getPrimaryKeyAndValue(UserModel e) {
		return new Pair<>("userId", e.getUserID());
	}

	protected void fillContentValues(ContentValues cv, UserModel e) {
		cv.put("userId", e.getUserID());
		cv.put("name", e.getNickName());
		cv.put("phone", e.getPhone());
		cv.put("accessToken", e.getAccessToken());
		cv.put("grade",e.getGrade());
	}

	protected void fillFields(UserModel e, Cursor c) {
		e.setUserID(c.getString(c.getColumnIndex("userId")));
		e.setNickName(c.getString(c.getColumnIndex("name")));
		e.setPhone(c.getString(c.getColumnIndex("phone")));
		e.setAccessToken(c.getString(c.getColumnIndex("accessToken")));
		e.setGrade(c.getInt(c.getColumnIndex("grade")));
	}

	@Override
	protected int getVersion() {
		return 2;
	}

	@Override
	protected void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		super.onUpgrade(db, oldVersion, newVersion);
		db.delete(TABLE_NAME, null, null);
		db.execSQL("ALTER TABLE [" + TABLE_NAME + "]\n" +
				"ADD [grade] INTEGER");
	}
}

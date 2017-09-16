package com.tianpingpai.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.util.Pair;

import com.tianpingpai.db.BaseDao;
import com.tianpingpai.user.UserModel;

public class UserDao extends BaseDao<UserModel> {

	private static final String TABLE_NAME = "user_table";

	@Override
	protected void onCreate(SQLiteDatabase db) {
		super.onCreate(db);
		Log.e("onCreate:", "--------onCreate");

		String sql =
				"CREATE TABLE [" + TABLE_NAME + "] ([userId] INTEGER PRIMARY KEY  NOT NULL  UNIQUE ," +
						" [name] TEXT, [phone] TEXT, [departmentId] INTEGER," +
						" [position] TEXT, [parentId] INTEGER, [accessToken] TEXT,[marketerId] INTEGER,[level] INTEGER,[displayName] TEXT);";
		db.execSQL(sql);
	}

	protected String getTableName() {
		return TABLE_NAME;
	}


	public UserModel getLast() {
		UserModel user = null;
		SQLiteDatabase db = getDb();
		Cursor cursor = db.rawQuery("SELECT * FROM [" + TABLE_NAME + "];", null);
		if (cursor.moveToFirst()) {
			user = new UserModel();
			fillFields(user, cursor);
		}
		cursor.close();
		db.close();
		return user;
	}

	protected Pair<String, String> getPrimaryKeyAndValue(UserModel e) {
		return new Pair<>("userId", e.getId() + "");//TODO
	}

	protected void fillContentValues(ContentValues cv, UserModel e) {
		cv.put("userId", e.getId());
		cv.put("name", e.getName());
		cv.put("phone", e.getPhone());
		cv.put("departmentId", e.getDepartmentId());
		cv.put("position", e.getPosition());
		cv.put("parentId", e.getParentId());
		cv.put("accessToken", e.getAccessToken());
		cv.put("marketerId", e.getMarketerId());
		cv.put("level", e.getLevel());
		cv.put("displayName", e.getDisplayName());
	}

	protected void fillFields(UserModel e, Cursor c) {
		super.fillFields(e, c);
		e.setId(c.getInt(c.getColumnIndex("userId")));
		e.setName(c.getString(c.getColumnIndex("name")));
		e.setPhone(c.getString(c.getColumnIndex("phone")));
		e.setDepartmentId(c.getString(c.getColumnIndex("departmentId")));
		e.setPosition(c.getString(c.getColumnIndex("position")));
		e.setParentId(c.getString(c.getColumnIndex("parentId")));
		e.setAccessToken(c.getString(c.getColumnIndex("accessToken")));
		e.setMarketerId(c.getInt(c.getColumnIndex("marketerId")));
		e.setLevel(c.getInt(c.getColumnIndex("level")));
		e.setDisplayName(c.getString(c.getColumnIndex("displayName")));
	}

	@Override
	protected int getVersion() {
		return 2;
	}

	@Override
	protected void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		super.onUpgrade(db, oldVersion, newVersion);

		Log.e("onUpgrade:", "------onUpgrade");

		if (newVersion == 2) {
			String sql1 = "ALTER TABLE " + TABLE_NAME + " ADD " + "marketerId" + " INTEGER";//alter table user add CreateUserID int 4
			String sql2 = "ALTER TABLE " + TABLE_NAME + " ADD " + "level" + " INTEGER";
			String sql3 = "ALTER TABLE " + TABLE_NAME + " ADD " + "displayName" + " TEXT";
			db.execSQL(sql1);
			db.execSQL(sql2);
			db.execSQL(sql3);
		}
	}
}
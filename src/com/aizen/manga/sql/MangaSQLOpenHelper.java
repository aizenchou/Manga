package com.aizen.manga.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MangaSQLOpenHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "manga.db";
	private static final int DATABASE_VERSION = 1;

	public MangaSQLOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("CREATE TABLE IF NOT EXISTS manga "
				+ "(_id VARCHAR PRIMARY KEY, " 
				+ "name VARCHAR, "
				+ "author VARCHAR, " 
				+ "publishDate VARCHAR, "
				+ "updateDate VARCHAR, " 
				+ "location VARCHAR, "
				+ "tag VARCHAR, " 
				+ "otherName VARCHAR, " 
				+ "status BOOLEAN, "
				+ "statusIntro TEXT, " 
				+ "description TEXT, "
				+ "mark VARCHAR, " 
				+ "link VARCHAR, " 
				+ "coverURL VARCHAR, "
				+ "updateto VARCHAR, "
				+ "isLike BOOLEAN, "
				+ "lastRead VARCHAR)");
		db.execSQL("CREATE TABLE IF NOT EXISTS local "
				+ "(_id VARCHAR PRIMARY KEY, " 
				+ "name VARCHAR, "
				+ "author VARCHAR, " 
				+ "link VARCHAR" 
				+ ")");
	}


	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("ALTER TABLE manga ADD COLUMN other STRING");  
	}

}

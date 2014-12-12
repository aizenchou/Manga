package com.aizen.manga.sql;

import java.util.ArrayList;

import com.aizen.manga.module.Manga;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MangaDBManager {
	private MangaSQLOpenHelper SQLHelper;
	private SQLiteDatabase SQLDB;

	public MangaDBManager(Context context) {
		SQLHelper = new MangaSQLOpenHelper(context);
		SQLDB = SQLHelper.getWritableDatabase();
	}

	public long addLocal(Manga manga) {
		ContentValues cv = new ContentValues();
		cv.put("_id", manga.getId());
		cv.put("name", manga.getName());
		cv.put("author", manga.getAuthor());
		cv.put("link", manga.getLink());
		return SQLDB.insert("local", null, cv);
	}
	
	public void deleteLocal(String id) {
		SQLDB.delete("local", "_id = ?", new String[] { id });
	}
	
	public boolean add(Manga manga) {
		SQLDB.beginTransaction();
		SQLDB.execSQL(
				"INSERT INTO manga VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
				new Object[] { manga.getId(), manga.getName(),
						manga.getAuthor(), manga.getPublishDate(),
						manga.getUpdateDate(), manga.getLcoation(),
						manga.getTag(), manga.getOtherName(), 
						manga.isStatus(),manga.getStatusIntro(), 
						manga.getDescription(),manga.getMark(), 
						manga.getLink(), manga.getCoverURL(),
						manga.getUpdateto(), manga.isLike(), 
						manga.getLastRead() });
		SQLDB.setTransactionSuccessful();
		SQLDB.endTransaction();
		return true;
	}

	public void setUnlike(String id) {
		ContentValues cv = new ContentValues();
		cv.put("isLike", false);
		SQLDB.update("manga", cv, "_id = ?", new String[] { id });
	}

	public void setLike(String id) {
		ContentValues cv = new ContentValues();
		cv.put("isLike", true);
		SQLDB.update("manga", cv, "_id = ?", new String[] { id });
	}
	
	public void setLastRead(String id,String chapterName) {
		ContentValues cv = new ContentValues();
		cv.put("lastRead", chapterName);
		SQLDB.update("manga", cv, "_id = ?", new String[] { id });
	}

	public String getLastRead(String id) {
		Cursor c = SQLDB.query("manga", null, "_id = ?", new String[] { id },
				null, null, null);
		while (c.moveToNext()) {
			String lr = c.getString(c.getColumnIndex("lastRead"));
			if (lr == null) {
				return null;
			} else {
				return lr;
			}
		}
		return null;
	}
	
	public boolean isLike(String id) {
		Cursor c = SQLDB.query("manga", null, "_id = ?", new String[] { id },
				null, null, null);
		while (c.moveToNext()) {
			if (c.getInt(c.getColumnIndex("isLike")) == 1) {
				return true;
			} else {
				return false;
			}
		}
		return false;
		
	}

	public void delete(String id) {
		SQLDB.delete("manga", "_id = ?", new String[] { id });
	}

	public ArrayList<Manga> queryLikedMangas() {
		ArrayList<Manga> likedMagnas = new ArrayList<Manga>();
		Cursor c = SQLDB.query("manga", null, "isLike = ?", new String[] { "1" },
				null, null, null);
		while (c.moveToNext()) {
			Manga manga = new Manga();
			manga.setId(c.getString(c.getColumnIndex("_id")));
			manga.setName(c.getString(c.getColumnIndex("name")));
			manga.setAuthor(c.getString(c.getColumnIndex("author")));
			manga.setStatusIntro(c.getString(c.getColumnIndex("statusIntro")));
			//manga.setMark(c.getString(c.getColumnIndex("mark")));
			manga.setMark("9.5");
			manga.setLink(c.getString(c.getColumnIndex("link")));
			manga.setCoverURL(c.getString(c.getColumnIndex("coverURL")));
			manga.setLike(true);
			manga.setLastRead(c.getString(c.getColumnIndex("lastRead")));
			likedMagnas.add(manga);
		}
		c.close();
		return likedMagnas;
	}

	public ArrayList<Manga> queryLocalMangas() {
		ArrayList<Manga> localMangas = new ArrayList<Manga>();
		Cursor c = SQLDB.query("local", null, null, null, null, null, null);
		while (c.moveToNext()) {
			Manga manga = new Manga();
			manga.setId(c.getString(c.getColumnIndex("_id")));
			manga.setName(c.getString(c.getColumnIndex("name")));
			manga.setAuthor(c.getString(c.getColumnIndex("author")));
			manga.setLink(c.getString(c.getColumnIndex("link")));
			localMangas.add(manga);
		}
		c.close();
		return localMangas;
	}
	
	public void closeDB() {
		SQLDB.close();
	}
}

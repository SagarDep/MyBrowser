package com.example.learnwebview.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.example.learnwebview.BaseApplication;
import com.example.learnwebview.bean.FileItem;
import com.liulishuo.filedownloader.util.FileDownloadUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zy1584 on 2017-11-30.
 */

public class FileDatabase extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;

	private static final String DATABASE_NAME = "fileManager";

	private static final String TABLE_FILE = "file";

	@Nullable
	private SQLiteDatabase mDatabase;

	private static final FileDatabase instance = new FileDatabase();

	public static FileDatabase getInstance() {
		return instance;
	}

	private FileDatabase() {
		super(BaseApplication.getContext(), DATABASE_NAME, null, DATABASE_VERSION);
		initialize();
	}

	private void initialize() {
		BaseApplication.getTaskThread().execute(new Runnable() {
			@Override
			public void run() {
				synchronized (FileDatabase.this) {
					mDatabase = FileDatabase.this.getWritableDatabase();
				}
			}
		});
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String CREATE_FILE_TABLE = "CREATE TABLE " + TABLE_FILE + '('
				+ FileItem.ID + " INTEGER PRIMARY KEY,"
				+ FileItem.URL + " TEXT,"
				+ FileItem.NAME + " TEXT,"
				+ FileItem.PATH + " TEXT" + ')';
		db.execSQL(CREATE_FILE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if it exists
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_FILE);
		// Create tables again
		onCreate(db);
	}

	@Override
	public synchronized void close() {
		if (mDatabase != null) {
			mDatabase.close();
			mDatabase = null;
		}
		super.close();
	}

	@NonNull
	private SQLiteDatabase openIfNecessary() {
		if (mDatabase == null || !mDatabase.isOpen()) {
			mDatabase = this.getWritableDatabase();
		}
		return mDatabase;
	}

	public synchronized void deleteTable() {
		mDatabase = openIfNecessary();
		mDatabase.delete(TABLE_FILE, null, null);
		mDatabase.close();
		mDatabase = this.getWritableDatabase();
	}

	private synchronized void addItem(@NonNull FileItem item) {
		mDatabase = openIfNecessary();
		mDatabase.insert(TABLE_FILE, null, item.toContentValues());
	}

	public FileItem addItem(String name, String url, String path) {
		if (TextUtils.isEmpty(url) || TextUtils.isEmpty(path)) {
			return null;
		}

		// have to use FileDownloadUtils.generateId to associate TasksManagerModel with FileDownloader
		final int id = FileDownloadUtils.generateId(url, path);

		FileItem model = new FileItem();
		model.setId(id);
		model.setName(name);
		model.setUrl(url);
		model.setPath(path);

		addItem(model);
		return model;
	}

	public synchronized void deleteItem(@NonNull int id) {
		mDatabase = openIfNecessary();
		mDatabase.delete(TABLE_FILE, FileItem.ID + " = ?", new String[]{Integer.toString(id)});
	}

	public synchronized void updateItem(@NonNull FileItem item) {
		mDatabase = openIfNecessary();
		Cursor q = mDatabase.query(false, TABLE_FILE, new String[]{FileItem.ID},
				FileItem.ID + " = ?", new String[]{Integer.toString(item.getId())}, null, null, null, "1");
		if (q.getCount() > 0) {
			mDatabase.update(TABLE_FILE, item.toContentValues(), FileItem.ID + " = ?", new String[]{Integer.toString(item.getId())});
		} else {
			addItem(item);
		}
		q.close();
	}

	@NonNull
	public synchronized List<FileItem> getAll() {
		mDatabase = openIfNecessary();
		List<FileItem> itemList = new ArrayList<>();
		String selectQuery = "SELECT  * FROM " + TABLE_FILE + " ORDER BY " + FileItem.ID + " ASC";

		Cursor cursor = mDatabase.rawQuery(selectQuery, null);

		if (cursor.moveToFirst()) {
			do {
				FileItem item = new FileItem();
				item.setId(cursor.getInt(0));
				item.setUrl(cursor.getString(1));
				item.setName(cursor.getString(2));
				item.setPath(cursor.getString(3));
				itemList.add(item);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return itemList;
	}
}

package com.example.learnwebview.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.learnwebview.BaseApplication;

/**
 * PreferenceManager
 * Created by zy1584 on 2017-11-29.
 */

public class PreferenceManager {

	private SharedPreferences mPrefs;
	private static final String FILE_NAME = "settings";
	private static final String FILE_DOWNLOAD_DIR = "/mybrowser/download";

	private static String DATA_ROOT_PATH = null;
	private static String SD_ROOT_PATH = null;

	private static class Name {
		static final String DOWNLOAD_DIRECTORY = "downloadLocation";
	}

	private static final PreferenceManager instance = new PreferenceManager();

	public static PreferenceManager getImpl(){
		return instance;
	}

	private PreferenceManager() {
		DATA_ROOT_PATH = BaseApplication.getContext().getCacheDir().getAbsolutePath();
		SD_ROOT_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();

		this.mPrefs = BaseApplication.getContext().getSharedPreferences(FILE_NAME,
				Context.MODE_PRIVATE);
	}

	@NonNull
	public String getDownloadDirectory() {
		return mPrefs.getString(Name.DOWNLOAD_DIRECTORY, getDefaultDownloadPath());
	}

	public void setDownloadDirectory(String directory) {
		putString(Name.DOWNLOAD_DIRECTORY, directory);
	}

	private void putBoolean(@NonNull String name, boolean value) {
		mPrefs.edit().putBoolean(name, value).apply();
	}

	private void putInt(@NonNull String name, int value) {
		mPrefs.edit().putInt(name, value).apply();
	}

	private void putString(@NonNull String name, @Nullable String value) {
		mPrefs.edit().putString(name, value).apply();
	}

	private String getDefaultDownloadPath() {
		String dir;
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			dir = SD_ROOT_PATH + FILE_DOWNLOAD_DIR;
		} else {
			dir = DATA_ROOT_PATH = FILE_DOWNLOAD_DIR;
		}
		return dir;
	}
}

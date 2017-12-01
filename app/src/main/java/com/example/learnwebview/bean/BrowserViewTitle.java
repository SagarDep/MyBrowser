package com.example.learnwebview.bean;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.learnwebview.R;
import com.example.learnwebview.utils.UIUtils;


public class BrowserViewTitle {

	@Nullable
	private Bitmap mFavicon = null;
	@Nullable
	private Bitmap mShot = null;
	@NonNull
	private String mTitle;
	@NonNull
	private final Context mContext;

	public BrowserViewTitle(@NonNull Context context) {
		mContext = context;
		mTitle = context.getString(R.string.action_new_tab);
	}

	public void setFavicon(@Nullable Bitmap favicon) {
		if (favicon == null) {
			mFavicon = null;
		} else {
			mFavicon = favicon;
		}
	}

	@NonNull
	private static Bitmap getDefaultIcon(@NonNull Context context) {
		return BitmapFactory.decodeResource(UIUtils.getResources(), R.mipmap.ic_launcher);
	}

	public void setTitle(@Nullable String title) {
		if (title == null) {
			mTitle = "";
		} else {
			mTitle = title;
		}
	}

	@NonNull
	public String getTitle() {
		return mTitle;
	}

	@NonNull
	public Bitmap getFavicon() {
		if (mFavicon == null) {
			return getDefaultIcon(mContext);
		}
		return mFavicon;
	}

	@Nullable
	public Bitmap getShot() {
		return mShot;
	}

	public void setShot(@Nullable Bitmap mShot) {
		this.mShot = mShot;
	}

}

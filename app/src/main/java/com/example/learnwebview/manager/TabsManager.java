package com.example.learnwebview.manager;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.example.learnwebview.BaseApplication;
import com.example.learnwebview.utils.FileUtils;
import com.example.learnwebview.web.TabView;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.content.ContentValues.TAG;

/**
 * Created by zy1584 on 2017-12-2.
 */

public class TabsManager {

	private static final String BUNDLE_KEY = "WEBVIEW_";
	private static final String BUNDLE_STORAGE = "SAVED_TABS.parcel";
	private Application mApp;
	private final List<TabView> mTabList = new ArrayList<>();
	private boolean mIsInitialized = false;
	private TabView mCurrentTab;

	private static final TabsManager instance = new TabsManager();

	private TabsManager() {
		mApp = BaseApplication.getApplication();
	}

	public static TabsManager getInstance() {
		return instance;
	}

	public int size() {
		return mTabList.size();
	}

	public synchronized int last() {
		return mTabList.size() - 1;
	}

	public synchronized TabView getCurrentTab() {
		return mCurrentTab;
	}

	public synchronized int positionOf(final TabView tab) {
		return mTabList.indexOf(tab);
	}

	public TabView newTab(Activity activity, String url, boolean isIncognito) {
		TabView tab = new TabView(activity, url, isIncognito);
		mTabList.add(tab);
		return tab;
	}

	public synchronized Observable<Void> initializeTabs(final Activity activity, final Intent intent, final boolean incognito) {
		return Observable.create(new Observable.OnSubscribe<Void>() {
			@Override
			public void call(Subscriber<? super Void> subscriber) {
				shutdown();

				String url = null;
				if (intent != null) {
					url = intent.getDataString();
				}

				// If incognito, only create one tab
				if (incognito) {
					newTab(activity, url, true);
					subscriber.onCompleted();
					return;
				}

				Log.d(TAG, "URL from intent: " + url);
				mCurrentTab = null;
				restoreLostTabs(url, activity, subscriber);
			}
		});
	}

	public synchronized boolean deleteTab(int position) {
		Log.d(TAG, "Delete tab: " + position);
		final TabView currentTab = getCurrentTab();
		int current = positionOf(currentTab);

		if (current == position) {
			if (size() == 1) {
				mCurrentTab = null;
			} else if (current < size() - 1) {
				// There is another tab after this one
				switchToTab(current + 1);
			} else {
				switchToTab(current - 1);
			}
		}

		removeTab(position);
		// TODO: 2017-12-2  
//		if (mTabNumberListener != null) {
//			mTabNumberListener.tabNumberChanged(size());
//		}
		return current == position;
	}

	private synchronized void removeTab(final int position) {
		if (position >= mTabList.size()) {
			return;
		}
		final TabView tab = mTabList.remove(position);
		if (mCurrentTab == tab) {
			mCurrentTab = null;
		}
		tab.onDestroy();
	}

	public synchronized TabView switchToTab(int position) {
		if (position < 0 || position >= mTabList.size()) {
			return null;
		} else {
			final TabView tab = mTabList.get(position);
			if (tab != null) {
				mCurrentTab = tab;
			}
			return tab;
		}
	}

	public synchronized void shutdown() {
		for (TabView tab : mTabList) {
			tab.onDestroy();
		}
		mTabList.clear();
		mIsInitialized = false;
		mCurrentTab = null;
	}

	private void restoreLostTabs(@Nullable final String url, @NonNull final Activity activity,
								 @NonNull final Subscriber subscriber) {

		restoreState().subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Bundle>() {

			@Override
			public void onNext(Bundle item) {
				TabView tab = newTab(activity, "", false);
				if (tab.getWebView() != null) {
					tab.getWebView().restoreState(item);
				}
			}

			@Override
			public void onCompleted() {
				if (!TextUtils.isEmpty(url)) {
					newTab(activity, url, false);
//					finishInitialization();
					subscriber.onCompleted();
				} else {
					if (mTabList.isEmpty()) {
						newTab(activity, null, false);
					}
//					finishInitialization();
					subscriber.onCompleted();
				}
			}

			@Override
			public void onError(Throwable e) {

			}
		});
	}

	/**
	 * Restores the previously saved tabs from the
	 * bundle stored in peristent file storage.
	 * It will create new tabs for each tab saved
	 * and will delete the saved instance file when
	 * restoration is complete.
	 */
	private Observable<Bundle> restoreState() {
		return Observable.create(new Observable.OnSubscribe<Bundle>() {
			@Override
			public void call(Subscriber<? super Bundle> subscriber) {
				Bundle savedState = FileUtils.readBundleFromStorage(mApp, BUNDLE_STORAGE);
				if (savedState != null) {
					for (String key : savedState.keySet()) {
						if (key.startsWith(BUNDLE_KEY)) {
							subscriber.onNext(savedState.getBundle(key));
						}
					}
				}
				FileUtils.deleteBundleInStorage(mApp, BUNDLE_STORAGE);
				subscriber.onCompleted();
			}
		});
	}

	/**
	 * Saves the state of the current WebViews,
	 * to a bundle which is then stored in persistent
	 * storage and can be unparceled.
	 */
	public void saveState() {
		Bundle outState = new Bundle(ClassLoader.getSystemClassLoader());
		for (int n = 0; n < mTabList.size(); n++) {
			TabView tab = mTabList.get(n);
			Bundle state = new Bundle(ClassLoader.getSystemClassLoader());
			if (tab.getWebView() != null) {
				tab.getWebView().saveState(state);
				outState.putBundle(BUNDLE_KEY + n, state);
			}
		}
		FileUtils.writeBundleToStorage(mApp, outState, BUNDLE_STORAGE);
	}

	public List<TabView> getTabList() {
		return mTabList;
	}

	public synchronized TabView getTabAtPosition(final int position) {
		if (position < 0 || position >= mTabList.size()) {
			return null;
		}

		return mTabList.get(position);
	}

	public synchronized int indexOfCurrentTab() {
		return mTabList.indexOf(mCurrentTab);
	}
}

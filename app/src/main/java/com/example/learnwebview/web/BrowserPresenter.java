package com.example.learnwebview.web;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.learnwebview.manager.TabsManager;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

import static android.content.ContentValues.TAG;

/**
 * Created by zy1584 on 2017-12-4.
 */

public class BrowserPresenter {

	private BrowserView mView;
	private boolean mIsIncognito;
	private TabsManager mTabsManager;
	private TabView mCurrentTab;

	public BrowserPresenter(@NonNull BrowserView view, boolean isIncognito) {
		mTabsManager = TabsManager.getInstance();
		mView = view;
		mIsIncognito = isIncognito;
	}

	public void setupTabs(@Nullable Intent intent) {
		mTabsManager.initializeTabs((Activity) mView, intent, mIsIncognito)
				.subscribeOn(AndroidSchedulers.mainThread())
				.subscribe(new Subscriber<Void>() {
					@Override
					public void onCompleted() {
						tabChanged(mTabsManager.last());
					}

					@Override
					public void onError(Throwable e) {

					}

					@Override
					public void onNext(Void aVoid) {

					}
				});
	}

	public void newTab(String url, boolean show) {
		if (mTabsManager.size() > 10) {
//			Toast.makeText(this, "标签页已达到上限", Toast.LENGTH_SHORT).show();
			return;
		}
		mTabsManager.newTab((Activity) mView, url, mIsIncognito);

		if (show) {
			TabView tab = mTabsManager.switchToTab(mTabsManager.last());
			onTabChanged(tab);
		}
		// TODO: 2017-12-2 update tab num
	}

	public void deleteTab(int position) {
		Log.d(TAG, "delete Tab");
		final TabView tabToDelete = mTabsManager.getTabAtPosition(position);

		if (tabToDelete == null) {
			return;
		}

		final boolean isShown = tabToDelete.isShown();
		final TabView currentTab = mTabsManager.getCurrentTab();
		if (mTabsManager.size() == 1 && currentTab != null){
			return;
		} else {
			if (isShown) {
				mView.removeTabView();
			}
			boolean currentDeleted = mTabsManager.deleteTab(position);
			if (currentDeleted) {
				tabChanged(mTabsManager.indexOfCurrentTab());
			}
		}
	}

	public synchronized void tabChanged(int position) {
		if (position < 0 || position >= mTabsManager.size()) {
			return;
		}
		TabView tab = mTabsManager.switchToTab(position);
		onTabChanged(tab);
	}

	private void onTabChanged(TabView newTab) {
		if (mCurrentTab != null) {
			mCurrentTab.setForegroundTab(false);
		}

		newTab.resumeTimers();
		newTab.onResume();
		newTab.setForegroundTab(true);

//		updateProgress(newTab.getProgress());
		mView.setBackButtonEnabled(newTab.canGoBack());
		mView.setForwardButtonEnabled(newTab.canGoForward());
//		updateUrl(newTab.getUrl(), true);
		mView.setTabView(newTab.getWebView());
		mCurrentTab = newTab;
	}
}

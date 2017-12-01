package com.example.learnwebview.web;

import android.app.Activity;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by zy1584 on 2017-12-1.
 */

public class BrowserView {

	private WebView mWebView;
	private Activity mActivity;
	private boolean mIsIncognitoTab;
	private boolean isForegroundTab;
	private static final String TAG = "BrowserView";

	public BrowserView(Activity activity, String url, boolean isIncognito) {
		mActivity = activity;
		mIsIncognitoTab = isIncognito;
		mWebView = new WebView(activity);

		mWebView.setWebViewClient(new WebViewClient());
		mWebView.setWebChromeClient(new MyWebChromeClient(activity));
		mWebView.setDownloadListener(new MyDownloadListener(activity));
		initSettings();
		mWebView.loadUrl(url);
	}

	private void initSettings() {
		WebSettings webSetting = mWebView.getSettings();
		webSetting.setJavaScriptEnabled(true);//支持js
		webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
		// 窗口
		webSetting.setJavaScriptCanOpenWindowsAutomatically(true);//支持通过JS打开新窗口
//		webSetting.setSupportMultipleWindows(true);//多窗口，默认为false，当设置为true时必须要重写WebChromeClient的onCreateWindow方法
		// 访问
		webSetting.setAllowFileAccess(true);//设置可以访问文件，默认为true
		webSetting.setGeolocationEnabled(true);// 访问地理位置，默认为true，当设置为true时必须提供地址权限声明，并实现WebChromeClient#onGeolocationPermissionsShowPrompt
		// 缩放
		webSetting.setSupportZoom(true);// 默认为true
		webSetting.setBuiltInZoomControls(true);//设置内置的缩放控件
		// 显示
		webSetting.setUseWideViewPort(true);
		// webSetting.setLoadWithOverviewMode(true);
		webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);// 控制html布局，此为默认值
		// 缓存
		webSetting.setAppCacheEnabled(true);
		webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
		// webSetting.setDatabaseEnabled(true);
		webSetting.setDomStorageEnabled(true);
		webSetting.setCacheMode(WebSettings.LOAD_NO_CACHE);
	}

	public synchronized WebView getWebView() {
		return mWebView;
	}

	public int getProgress() {
		if (mWebView != null) {
			return mWebView.getProgress();
		} else {
			return 100;
		}
	}

	public boolean isIncognito() {
		return mIsIncognitoTab;
	}

	public boolean isShown() {
		return mWebView != null && mWebView.isShown();
	}

	public boolean isForegroundTab() {
		return isForegroundTab;
	}

	public void setForegroundTab(boolean isForeground) {
		isForegroundTab = isForeground;
		// TODO: 2017-12-1  通知变化
	}

	public synchronized void pauseTimers() {
		if (mWebView != null) {
			mWebView.pauseTimers();
			Log.d(TAG, "Pausing JS timers");
		}
	}

	public synchronized void resumeTimers() {
		if (mWebView != null) {
			mWebView.resumeTimers();
			Log.d(TAG, "Resuming JS timers");
		}
	}

	public synchronized void onPause() {
		if (mWebView != null) {
			mWebView.onPause();
			Log.d(TAG, "WebView onPause: " + mWebView.getId());
		}
	}

	public synchronized void onResume() {
		if (mWebView != null) {
			mWebView.onResume();
			Log.d(TAG, "WebView onResume: " + mWebView.getId());
		}
	}

	public boolean canGoBack() {
		return mWebView != null && mWebView.canGoBack();
	}

	public boolean canGoForward() {
		return mWebView != null && mWebView.canGoForward();
	}

	public synchronized void goBack() {
		if (mWebView != null) {
			mWebView.goBack();
		}
	}

	public synchronized void goForward() {
		if (mWebView != null) {
			mWebView.goForward();
		}
	}

	public synchronized void onDestroy() {
		if (mWebView != null) {
			// Check to make sure the WebView has been removed
			// before calling destroy() so that a memory leak is not created
			ViewGroup parent = (ViewGroup) mWebView.getParent();
			if (parent != null) {
				Log.e(TAG, "WebView was not detached from window before onDestroy");
				parent.removeView(mWebView);
			}
			mWebView.stopLoading();
			mWebView.onPause();
			mWebView.clearHistory();
			mWebView.setVisibility(View.GONE);
			mWebView.removeAllViews();
			mWebView.destroyDrawingCache();
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
				//this is causing the segfault occasionally below 4.2
				mWebView.destroy();
			}
			mWebView = null;
		}
	}
}

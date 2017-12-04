package com.example.learnwebview.web;

import android.app.Activity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.learnwebview.interfaces.UIController;

/**
 * Created by zy1584 on 2017-12-4.
 */

public class MyWebViewClient extends WebViewClient {

	private UIController mUIController;
	private Activity mActivity;
	private TabView mTabView;

	public MyWebViewClient(Activity activity, TabView lightningView) {
		mActivity = activity;
		mUIController = (UIController) activity;
		mTabView = lightningView;
	}

	@Override
	public void onPageFinished(WebView view, String url) {
		if (view.isShown()) {
			mUIController.updateUrl(url, true);
			mUIController.setBackButtonEnabled(view.canGoBack());
			mUIController.setForwardButtonEnabled(view.canGoForward());
			view.postInvalidate();
		}
//		if (view.getTitle() == null || view.getTitle().isEmpty()) {
//			mLightningView.getTitleInfo().setTitle(mActivity.getString(R.string.untitled));
//		} else {
//			mLightningView.getTitleInfo().setTitle(view.getTitle());
//		}
	}
}

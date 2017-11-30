package com.example.learnwebview;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.example.learnwebview.interfaces.UIController;
import com.example.learnwebview.web.MyDownloadListener;
import com.example.learnwebview.web.MyWebChromeClient;

public class MainActivity extends AppCompatActivity implements UIController{

	private Activity mContext;
	private static final String url = "http://www.baidu.com";
	private ProgressBar mProgressBar;
	private WebView mWebView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mContext = this;
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
		FrameLayout fl_container = (FrameLayout) findViewById(R.id.fl_container);
		mWebView = new WebView(getApplicationContext());
		fl_container.addView(mWebView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

		mWebView.setWebViewClient(new WebViewClient());
		mWebView.setWebChromeClient(new MyWebChromeClient(this));
		mWebView.setDownloadListener(new MyDownloadListener(this));
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

	@Override
	public void updateProgress(int newProgress) {
		mProgressBar.setVisibility(newProgress == 100 ? View.GONE : View.VISIBLE);
		mProgressBar.setProgress(newProgress);
	}

}

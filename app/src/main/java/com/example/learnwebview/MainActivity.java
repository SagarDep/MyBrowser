package com.example.learnwebview;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.example.learnwebview.activities.DownloadManagerActivity;
import com.example.learnwebview.bean.FileItem;
import com.example.learnwebview.bus.BrowserEvents;
import com.example.learnwebview.download.DownloadManager;
import com.example.learnwebview.utils.UIUtils;
import com.example.learnwebview.web.MyDownloadListener;
import com.example.learnwebview.web.MyWebChromeClient;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.learnwebview.bus.BrowserEvents.DownloadMessage.MSG_TASK_EXIST;

public class MainActivity extends AppCompatActivity  {

	private Activity mContext;
	private static final String url = "http://www.baidu.com";
	private ProgressBar mProgressBar;
	private WebView mWebView;

	@OnClick(R.id.btn_download_manager)
	void goDownloadManager() {
		Intent intent = new Intent(this, DownloadManagerActivity.class);
		startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);
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

	public void updateProgress(int newProgress) {
		mProgressBar.setVisibility(newProgress == 100 ? View.GONE : View.VISIBLE);
		mProgressBar.setProgress(newProgress);
	}

	@Override
	protected void onStart() {
		super.onStart();
		EventBus.getDefault().register(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		EventBus.getDefault().unregister(this);
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void showTaskExistDialog(BrowserEvents.DownloadMessage event) {
		final FileItem item = event.item;
		if (event.msg == MSG_TASK_EXIST) {
			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
			builder.setTitle(R.string.dialog_task_exist_prompt_title);
			builder.setMessage(item.getName() + " "
					+ UIUtils.getString(R.string.dialog_task_exist_prompt_message));
			builder.setPositiveButton(R.string.action_ok,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							DownloadManager.getImpl().startDownload(item, true);
						}
					});
			builder.setNegativeButton(R.string.action_cancel, null);
			builder.show();
		}
	}
}

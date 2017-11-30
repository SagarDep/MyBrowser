package com.example.learnwebview.web;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.anthonycr.grant.PermissionsManager;
import com.anthonycr.grant.PermissionsResultAction;
import com.example.learnwebview.interfaces.UIController;

/**
 * 自定义WebChromeClient
 * Created by zy1584 on 2017-11-29.
 */

public class MyWebChromeClient extends WebChromeClient {
	private static final String[] PERMISSIONS_LOCATION = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
	
	private Activity mActivity;
	private UIController mUIController;
	public MyWebChromeClient(Activity activity) {
		mActivity = activity;
		mUIController = (UIController) activity;
	}

	@Override
	public void onProgressChanged(WebView view, int newProgress) {
		super.onProgressChanged(view, newProgress);
		// 更新进度
		mUIController.updateProgress(newProgress);
	}

	@Override
	public void onGeolocationPermissionsShowPrompt(final String origin, final GeolocationPermissions.Callback callback) {
		PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(mActivity, PERMISSIONS_LOCATION, new PermissionsResultAction() {
			@Override
			public void onGranted() {
				AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
				String org;
				if (origin.length() > 50) {
					org = origin.subSequence(0, 50) + "...";
				} else {
					org = origin;
				}
				builder.setMessage("网站 " + org + " 请求您的位置信息。")
						.setCancelable(true)
						.setPositiveButton("同意",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int id) {
										callback.invoke(origin, true, true);
									}
								})
						.setNegativeButton("拒绝",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int id) {
										callback.invoke(origin, false, true);
									}
								});
				AlertDialog alert = builder.create();
				alert.show();
			}

			@Override
			public void onDenied(String permission) {

			}
		});
	}
}

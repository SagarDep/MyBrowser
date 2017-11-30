package com.example.learnwebview.web;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;

import com.anthonycr.grant.PermissionsManager;
import com.anthonycr.grant.PermissionsResultAction;
import com.example.learnwebview.download.DownloadHandler;

/**
 * 自定义DownloadListener处理网页下载
 * Created by zy1584 on 2017-11-29.
 */

public class MyDownloadListener implements DownloadListener {
	private static final String[] PERMISSIONS_STORAGE = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
			Manifest.permission.WRITE_EXTERNAL_STORAGE};

	private Activity mActivity;

	public MyDownloadListener(Activity activity) {
		mActivity = activity;
	}

	@Override
	public void onDownloadStart(final String url, final String userAgent, final String contentDisposition, final String mimetype, final long contentLength) {
		PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(mActivity, PERMISSIONS_STORAGE, new PermissionsResultAction() {
			@Override
			public void onGranted() {
				String fileName = URLUtil.guessFileName(url, contentDisposition, mimetype);
				AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
				builder.setTitle(fileName)
						.setMessage("确认下载此文件？")
						.setNegativeButton("取消", null)
						.setPositiveButton("确认", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								DownloadHandler.onDownloadStart(mActivity, url, userAgent, contentDisposition,
										mimetype, contentLength);
							}
						})
						.show();
			}

			@Override
			public void onDenied(String permission) {

			}
		});
	}
}

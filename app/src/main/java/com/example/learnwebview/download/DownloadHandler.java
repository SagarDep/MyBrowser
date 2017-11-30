package com.example.learnwebview.download;

import android.content.Context;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.webkit.URLUtil;

import com.example.learnwebview.R;

/**
 * 下载预处理
 * Created by zy1584 on 2017-11-27.
 */

public class DownloadHandler {

	public static void onDownloadStart(Context context, String url, String userAgent, String contentDisposition,
									   String mimetype, long contentLength) {
		if (!TextUtils.isEmpty(contentDisposition)) {// TODO: 2017-11-30  

		}
		// Check to see if we have an SDCard
		String status = Environment.getExternalStorageState();
		if (!status.equals(Environment.MEDIA_MOUNTED)) {
			int title;
			String msg;

			// Check to see if the SDCard is busy, same as the music app
			if (status.equals(Environment.MEDIA_SHARED)) {
				msg = context.getString(R.string.download_sdcard_busy_dlg_msg);
				title = R.string.download_sdcard_busy_dlg_title;
			} else {
				msg = context.getString(R.string.download_no_sdcard_dlg_msg);
				title = R.string.download_no_sdcard_dlg_title;
			}

			new AlertDialog.Builder(context).setTitle(title)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setMessage(msg)
					.setPositiveButton(R.string.action_ok, null)
					.show();
			return;
		}
		// TODO: 2017-11-30 url处理
		String filename = URLUtil.guessFileName(url, contentDisposition, mimetype);
		DownloadManager.getImpl().addTask(filename, url, true);
	}
}

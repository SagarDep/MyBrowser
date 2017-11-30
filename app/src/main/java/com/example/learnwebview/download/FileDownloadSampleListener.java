package com.example.learnwebview.download;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;

/**
 * Created by zy1584 on 2017-11-30.
 */

public class FileDownloadSampleListener extends FileDownloadListener {
	@Override
	protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {

	}

	@Override
	protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {

	}

	@Override
	protected void completed(BaseDownloadTask task) {

	}

	@Override
	protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {

	}

	@Override
	protected void error(BaseDownloadTask task, Throwable e) {

	}

	@Override
	protected void warn(BaseDownloadTask task) {

	}
}

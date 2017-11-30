package com.example.learnwebview;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.connection.FileDownloadUrlConnection;

import java.net.Proxy;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by zy1584 on 2017-11-27.
 */

public class BaseApplication extends Application {

	private static Context mContext;
	private static final Executor mIOThread = Executors.newSingleThreadExecutor();
	private static final Executor mTaskThread = Executors.newCachedThreadPool();

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = this;

		initFileDownloader();
	}

	private void initFileDownloader() {
		FileDownloader.setupOnApplicationOnCreate(this)
				.connectionCreator(new FileDownloadUrlConnection
						.Creator(new FileDownloadUrlConnection.Configuration()
						.connectTimeout(15_000) // set connection timeout.
						.readTimeout(15_000) // set read timeout.
						.proxy(Proxy.NO_PROXY) // set proxy
				))
				.commit();
	}

	public static Context getContext() {
		return mContext;
	}

	@NonNull
	public static Executor getIOThread() {
		return mIOThread;
	}

	@NonNull
	public static Executor getTaskThread() {
		return mTaskThread;
	}
}

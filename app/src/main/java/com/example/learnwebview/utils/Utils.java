package com.example.learnwebview.utils;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;

/**
 * Created by zy1584 on 2017-12-1.
 */

public class Utils {

	public static String formatFileSize(long size) {
		return new DecimalFormat("##0.00").format(size * 1.0 / (1024 * 1024));
	}

	/**
	 * speed为kb
	 *
	 * @param speed
	 * @return
	 */
	public static String formatSpeed(double speed) {
		if (speed > 1024) {
			return new DecimalFormat("##0.0").format(speed * 1.0 / 1024) + "MB/s";
		} else {
			return new DecimalFormat("##0.0").format(speed * 1.0) + "KB/s";
		}
	}

	public static long getFileSize(String path) {
		File file = new File(path);
		long total = 0;
		if (file.exists() && file.isFile()) {
			total = file.length();
		}
		return total;
	}

	public static boolean isApk(String path) {
		if (TextUtils.isEmpty(path)) return false;
		if (!TextUtils.isEmpty(path) && path.endsWith(".apk")) {
			if (new File(path).exists()) {
				return true;
			}
		}
		return false;
	}

	public static void close(@Nullable Closeable closeable) {
		if (closeable == null)
			return;
		try {
			closeable.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

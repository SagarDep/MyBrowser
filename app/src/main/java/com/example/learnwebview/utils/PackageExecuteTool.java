package com.example.learnwebview.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import java.io.File;

/**
 * Created by zy1584 on 2017-12-1.
 */

public class PackageExecuteTool {

	/**
	 * 兼容7.0
	 *
	 * @param localPath
	 * @param context
	 * @return
	 */
	public static boolean normalInstall(String localPath, Context context) {
		File file = new File(localPath);
		if (!file.exists()) return false;
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		//版本在7.0以上是不能直接通过uri访问的
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			// 参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
			Uri apkUri = FileProvider.getUriForFile(context, "com.example.learnwebview", file);
			//添加这一句表示对目标应用临时授权该Uri所代表的文件
			intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
		} else {
			intent.setDataAndType(Uri.fromFile(file),
					"application/vnd.android.package-archive");
		}
		context.startActivity(intent);
		return true;
	}
}

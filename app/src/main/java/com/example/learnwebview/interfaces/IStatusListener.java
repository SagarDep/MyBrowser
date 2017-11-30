package com.example.learnwebview.interfaces;

import com.liulishuo.filedownloader.BaseDownloadTask;

/**
 * Created by zy1584 on 2017-11-29.
 */

public interface IStatusListener {

	void packageStateChange(Object obj, BaseDownloadTask task);

}

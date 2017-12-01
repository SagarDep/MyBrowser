package com.example.learnwebview.bus;

import com.example.learnwebview.bean.FileItem;

/**
 * Created by zy1584 on 2017-12-1.
 */

public class BrowserEvents {

	private BrowserEvents() {}

	public final static class DownloadMessage{

		public static final int MSG_TASK_EXIST = 1;

		public FileItem item;
		public int msg;

		public DownloadMessage(FileItem item, int msg) {
			this.item = item;
			this.msg = msg;
		}
	}
}

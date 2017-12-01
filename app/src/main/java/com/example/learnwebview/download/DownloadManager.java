package com.example.learnwebview.download;

import android.text.TextUtils;
import android.util.SparseArray;

import com.example.learnwebview.BaseApplication;
import com.example.learnwebview.bean.FileItem;
import com.example.learnwebview.bus.BrowserEvents;
import com.example.learnwebview.db.FileDatabase;
import com.example.learnwebview.interfaces.IStatusListener;
import com.example.learnwebview.preference.PreferenceManager;
import com.example.learnwebview.utils.PackageExecuteTool;
import com.example.learnwebview.utils.Utils;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloadSampleListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.util.FileDownloadUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 下载管理
 * Created by zy1584 on 2017-11-29.
 */

public class DownloadManager {

	private HashMap<Object, ArrayList<Object>> mViewMap = new HashMap<>();
	private HashMap<String, HashMap<Object, IStatusListener>> mStatusMap = new HashMap<>();

	private PreferenceManager mPreferenceManager;
	private FileDatabase mFileDataBase;
	private List<FileItem> modelList; // 与数据库中的数据要同步
	private SparseArray<BaseDownloadTask> taskSparseArray = new SparseArray<>();

	private static final DownloadManager instance = new DownloadManager();

	public static DownloadManager getImpl() {
		return instance;
	}

	private DownloadManager() {
		mPreferenceManager = PreferenceManager.getImpl();

		mFileDataBase = FileDatabase.getInstance();
		modelList = mFileDataBase.getAll();
	}

	/**
	 * 注册下载监听，并与view关联，用于列表的局部更新
	 *
	 * @param keyObj   一般传当前类对象，指定view所属的对象，作为map的key
	 * @param view     需要局部更新的view
	 * @param url      下载地址
	 * @param listener 状态监听器
	 */
	public void registerChangeListener(Object keyObj, Object view, String url, IStatusListener listener) {
		if (TextUtils.isEmpty(url)) return;
		ArrayList<Object> viewList = mViewMap.get(keyObj);
		if (viewList == null) {
			viewList = new ArrayList<>();
			mViewMap.put(keyObj, viewList);
		}
		if (!viewList.contains(view)) {
			viewList.add(view);
		}
		HashMap<Object, IStatusListener> listenMap = mStatusMap.get(url);
		if (listenMap == null) {
			listenMap = new HashMap<>();
			mStatusMap.put(url, listenMap);
		}
		listenMap.put(view, listener);
	}

	/**
	 * 注销下载监听，用于取消view的局部更新监听
	 *
	 * @param keyObj 一般传当前类对象，指定view所属的对象，作为map的key
	 */
	public void unRegisterChangeListener(Object keyObj) {
		ArrayList<Object> viewLst = mViewMap.remove(keyObj);
		if (viewLst != null) {
			for (HashMap<Object, IStatusListener> listenMap : mStatusMap.values()) {
				for (Object v : viewLst) {
					listenMap.remove(v);
				}
			}
		}
	}

	public FileItem addTask(String fileName, String url, boolean wifiRequired) {
		if (TextUtils.isEmpty(url)) return null;
		// 判断是否已经在下载列表中了
		String path = createPath(fileName);
		int id = FileDownloadUtils.generateId(url, path);
		FileItem model = getById(id);
		if (model != null) {// 任务已存在
			EventBus.getDefault().post(new BrowserEvents.DownloadMessage(model,
					BrowserEvents.DownloadMessage.MSG_TASK_EXIST));
			return model;
		}
		FileItem item = mFileDataBase.addItem(fileName, url, path);
		if (item != null) {
			// 开始下载
			BaseDownloadTask task = FileDownloader.getImpl().create(item.getUrl())
					.setPath(item.getPath())
					.setCallbackProgressTimes(100)
					.setWifiRequired(wifiRequired)
					.setListener(mDownloadListener);
			task.start();

			modelList.add(item);
			addTaskToArray(task);

			return item;
		}

		return null;
	}

	public void startDownload(FileItem model, boolean forceReDownload) {
		if (model != null) {
			// 开始下载
			BaseDownloadTask task = FileDownloader.getImpl().create(model.getUrl())
					.setPath(model.getPath())
					.setWifiRequired(true)
					.setForceReDownload(forceReDownload)
					.setListener(mDownloadListener);
			task.start();
			addTaskToArray(task);
		}
	}

	public void clearTask(FileItem model) {
		FileDownloader.getImpl().clear(model.getId(), model.getPath());
		removeTaskFromArray(model.getId());
		mFileDataBase.deleteItem(model.getId());
		modelList.remove(model); // 验证一下引用会不会更新
	}

	public int getStatus(int id, String path) {
		return FileDownloader.getImpl().getStatus(id, path);
	}

	public long getTotal(final int id) {
		return FileDownloader.getImpl().getTotal(id);
	}

	public long getSoFar(final int id) {
		return FileDownloader.getImpl().getSoFar(id);
	}

	private FileItem getById(final int id) {
		for (FileItem model : modelList) {
			if (model.getId() == id) {
				return model;
			}
		}
		return null;
	}

	public double getSpeed(final int id) {
		BaseDownloadTask task = getTaskById(id);
		if (task != null) {
			return task.getSpeed();
		}
		return 0;
	}

	public boolean isReady() {
		return FileDownloader.getImpl().isServiceConnected();
	}

	public List<FileItem> getDownloadList() {
		return modelList;
	}

	public BaseDownloadTask getTaskById(int id) {
		return taskSparseArray.get(id);
	}

	private void addTaskToArray(final BaseDownloadTask task) {
		taskSparseArray.put(task.getId(), task);
	}

	private void removeTaskFromArray(final int id) {
		taskSparseArray.remove(id);
	}

	/**
	 * 返回path
	 *
	 * @param name 文件名
	 * @return 存储路径
	 */
	private String createPath(String name) {
		if (TextUtils.isEmpty(name)) {
			return null;
		}
		return mPreferenceManager.getDownloadDirectory() + "/" + name;
	}

	private FileDownloadListener mDownloadListener = new FileDownloadSampleListener() {
		@Override
		protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
			super.pending(task, soFarBytes, totalBytes);
			informStatusChange(task);
		}

		@Override
		protected void started(BaseDownloadTask task) {
			super.started(task);
			informStatusChange(task);
		}

		@Override
		protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
			super.connected(task, etag, isContinue, soFarBytes, totalBytes);
			informStatusChange(task);
		}

		@Override
		protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
			super.progress(task, soFarBytes, totalBytes);
			informStatusChange(task);
		}

		@Override
		protected void completed(BaseDownloadTask task) {
			super.completed(task);
			informStatusChange(task);
			removeTaskFromArray(task.getId());
			// 如果是apk则启动安装
			boolean isApk = Utils.isApk(task.getPath());
			if (isApk) {
				PackageExecuteTool.normalInstall(task.getPath(), BaseApplication.getContext());
			}
		}

		@Override
		protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
			super.paused(task, soFarBytes, totalBytes);
			informStatusChange(task);
			removeTaskFromArray(task.getId());
		}

		@Override
		protected void error(BaseDownloadTask task, Throwable e) {
			super.error(task, e);
			informStatusChange(task);
			removeTaskFromArray(task.getId());
		}

	};

	private void informStatusChange(BaseDownloadTask task) {
		HashMap<Object, IStatusListener> listenMap = mStatusMap.get(task.getUrl());
		if (listenMap != null) {
			for (Map.Entry<Object, IStatusListener> entry : listenMap.entrySet()) {
				IStatusListener listener = entry.getValue();
				if (listener != null) {
					listener.packageStateChange(entry.getKey(), task);
				}
			}
		}
	}

}

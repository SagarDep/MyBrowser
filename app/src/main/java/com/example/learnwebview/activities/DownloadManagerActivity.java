package com.example.learnwebview.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.learnwebview.BaseApplication;
import com.example.learnwebview.R;
import com.example.learnwebview.adapter.FileDownloadAdapter;
import com.example.learnwebview.bean.FileItem;
import com.example.learnwebview.download.DownloadManager;
import com.example.learnwebview.interfaces.IStatusListener;
import com.example.learnwebview.utils.FileOpenUtils;
import com.example.learnwebview.utils.UIUtils;
import com.example.learnwebview.utils.Utils;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadConnectListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.model.FileDownloadStatus;
import com.liulishuo.filedownloader.util.FileDownloadUtils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DownloadManagerActivity extends AppCompatActivity implements FileDownloadAdapter.OnItemRenderListener,
		BaseQuickAdapter.OnItemChildClickListener, BaseQuickAdapter.OnItemClickListener, IStatusListener {

	@BindView(R.id.recyclerView)
	RecyclerView mRecyclerView;

	private FileDownloadConnectListener listener;
	private FileDownloadAdapter mDownloadAdapter;
	private DownloadManager mDownloadManager;
	private List<FileItem> mData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_download_manager);
		ButterKnife.bind(this);

		mDownloadManager = DownloadManager.getImpl();
		mData = mDownloadManager.getDownloadList();

		mDownloadAdapter = new FileDownloadAdapter(R.layout.item_list_download, mData, this);
		mDownloadAdapter.setOnItemChildClickListener(this);
		mDownloadAdapter.setOnItemClickListener(this);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		mRecyclerView.setAdapter(mDownloadAdapter);

		if (!FileDownloader.getImpl().isServiceConnected()) {
			FileDownloader.getImpl().bindService();
			registerServiceConnectionListener(new WeakReference<>(this));
		}
	}

	@Override
	public void onItemRender(BaseViewHolder viewHolder, int position) {
		FileItem model = mData.get(position);
		mDownloadManager.registerChangeListener(this, viewHolder, model.getUrl(), this);

		if (mDownloadManager.isReady()) {
			int status = mDownloadManager.getStatus(model.getId(), model.getPath());
			updateHolderView(viewHolder, model.getId(), model.getPath(), status);
		}
	}

	private void updateHolderView(BaseViewHolder viewHolder, int id, String path, int status) {
		if (status == FileDownloadStatus.pending || status == FileDownloadStatus.started ||
				status == FileDownloadStatus.connected) {
			// start task, but file not created yet
			updateDownloading(viewHolder, status, mDownloadManager.getSoFar(id),
					mDownloadManager.getTotal(id), mDownloadManager.getSpeed(id));
		} else if (!new File(path).exists() &&
				!new File(FileDownloadUtils.getTempPath(path)).exists()) {
			// not exist file
			updateNotDownloaded(viewHolder, status, mDownloadManager.getSoFar(id),
					mDownloadManager.getTotal(id));
		} else if (status == FileDownloadStatus.completed) {
			// already downloaded and exist
			updateDownloaded(viewHolder, Utils.getFileSize(path));
		} else if (status == FileDownloadStatus.progress) {
			// downloading
			updateDownloading(viewHolder, status, mDownloadManager.getSoFar(id),
					mDownloadManager.getTotal(id), mDownloadManager.getSpeed(id));
		} else {
			// not start
			updateNotDownloaded(viewHolder, status, mDownloadManager.getSoFar(id),
					mDownloadManager.getTotal(id));
		}
	}

	public void updateDownloaded(BaseViewHolder viewHolder, long total) {
		viewHolder.setProgress(R.id.pb_task, 1, 1);

		viewHolder.setText(R.id.tv_process, Utils.formatFileSize(total) + "M");
		viewHolder.setText(R.id.tv_status, R.string.tasks_manager_demo_status_completed);
		viewHolder.setText(R.id.btn_action, R.string.open);
	}

	public void updateNotDownloaded(BaseViewHolder viewHolder, int status, long sofar, long total) {
		if (sofar > 0 && total > 0) {
			final float percent = sofar / (float) total;
			viewHolder.setProgress(R.id.pb_task, (int) (percent * 100), 100);
		} else {
			viewHolder.setProgress(R.id.pb_task, 0, 1);
		}

		switch (status) {
			case FileDownloadStatus.error:
				viewHolder.setText(R.id.tv_status, R.string.tasks_manager_demo_status_error);
				break;
			case FileDownloadStatus.paused:
				viewHolder.setText(R.id.tv_status, R.string.tasks_manager_demo_status_paused);
				break;
			default:
				viewHolder.setText(R.id.tv_status, R.string.tasks_manager_demo_status_not_downloaded);
				break;
		}
		viewHolder.setText(R.id.tv_process, Utils.formatFileSize(sofar) + "/" + Utils.formatFileSize(total) + "M");
		viewHolder.setText(R.id.btn_action, R.string.start);
	}

	public void updateDownloading(BaseViewHolder viewHolder, int status, long sofar, long total, double speed) {
		final float percent = sofar / (float) total;
		viewHolder.setProgress(R.id.pb_task, (int) (percent * 100), 100);

		switch (status) {
			case FileDownloadStatus.pending:
				viewHolder.setText(R.id.tv_status, R.string.tasks_manager_demo_status_pending);
				break;
			case FileDownloadStatus.started:
				viewHolder.setText(R.id.tv_status, R.string.tasks_manager_demo_status_started);
				break;
			case FileDownloadStatus.connected:
				viewHolder.setText(R.id.tv_status, R.string.tasks_manager_demo_status_connected);
				break;
			case FileDownloadStatus.progress:
				viewHolder.setText(R.id.tv_status, Utils.formatSpeed(speed));
				break;
		}
		viewHolder.setText(R.id.tv_process, Utils.formatFileSize(sofar) + "/" + Utils.formatFileSize(total) + "M");
		viewHolder.setText(R.id.btn_action, R.string.pause);
	}

	@Override
	public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
		FileItem fileItem = mData.get(position);
		openTask(fileItem);
	}

	private void openTask(final FileItem model) {
		BaseDownloadTask task = mDownloadManager.getTaskById(model.getId());
		if (task != null && task.getStatus() != FileDownloadStatus.completed) return;
		String path = model.getPath();
		if (!TextUtils.isEmpty(path)) {
			if (new File(path).exists()) {
				Intent intent = FileOpenUtils.openFile(model.getPath());
				if (intent != null) {
					BaseApplication.getContext().startActivity(intent);
				}
			} else { // 文件已删除，是否重新下载
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(R.string.dialog_file_no_exist_prompt_message);
				builder.setPositiveButton(R.string.action_ok,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								mDownloadManager.startDownload(model, true);
							}
						});
				builder.setNegativeButton(R.string.action_cancel, null);
				builder.show();
			}
		}
	}

	@Override
	public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
		FileItem model = mData.get(position);
		switch (view.getId()) {
			case R.id.iv_delete:
				// 询问是否删除本地文件及任务
				mDownloadManager.clearTask(model);
				mDownloadAdapter.notifyDataSetChanged();
				break;
			case R.id.btn_action:
				CharSequence action = ((TextView) view).getText();
				if (action.equals(UIUtils.getString(R.string.pause))) {
					// to pause
					FileDownloader.getImpl().pause(model.getId());
				} else if (action.equals(UIUtils.getString(R.string.start))) {
					mDownloadManager.startDownload(model, false);
				} else if (action.equals(UIUtils.getString(R.string.open))) {
					openTask(model);
				}
				break;
		}
	}

	@Override
	public void packageStateChange(Object obj, BaseDownloadTask task) {
		BaseViewHolder holder = (BaseViewHolder) obj;
		updateHolderView(holder, task.getId(), task.getPath(), task.getStatus());
	}

	private void registerServiceConnectionListener(final WeakReference<DownloadManagerActivity>
														   activityWeakReference) {
		if (listener != null) {
			FileDownloader.getImpl().removeServiceConnectListener(listener);
		}
		listener = new FileDownloadConnectListener() {

			@Override
			public void connected() {
				if (activityWeakReference == null
						|| activityWeakReference.get() == null) {
					return;
				}

				activityWeakReference.get().postNotifyDataChanged();
			}

			@Override
			public void disconnected() {
				if (activityWeakReference == null
						|| activityWeakReference.get() == null) {
					return;
				}

				activityWeakReference.get().postNotifyDataChanged();
			}
		};

		FileDownloader.getImpl().addServiceConnectListener(listener);
	}

	private void unregisterServiceConnectionListener() {
		FileDownloader.getImpl().removeServiceConnectListener(listener);
		listener = null;
	}

	public void postNotifyDataChanged() {
		if (mDownloadAdapter != null) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (mDownloadAdapter != null) {
						mDownloadAdapter.notifyDataSetChanged();
					}
				}
			});
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mDownloadManager.unRegisterChangeListener(this);
		unregisterServiceConnectionListener();
	}

}

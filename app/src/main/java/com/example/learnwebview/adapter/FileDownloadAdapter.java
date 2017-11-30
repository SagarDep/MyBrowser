package com.example.learnwebview.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.learnwebview.bean.FileItem;

import java.util.List;

/**
 * Created by zy1584 on 2017-11-30.
 */

public class FileDownloadAdapter extends BaseQuickAdapter<FileItem, BaseViewHolder> {
	public FileDownloadAdapter(@LayoutRes int layoutResId, @Nullable List<FileItem> data) {
		super(layoutResId, data);
	}

	@Override
	protected void convert(BaseViewHolder helper, FileItem item) {

	}
}

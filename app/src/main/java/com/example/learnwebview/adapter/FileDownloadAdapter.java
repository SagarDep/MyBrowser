package com.example.learnwebview.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.learnwebview.R;
import com.example.learnwebview.bean.FileItem;

import java.util.List;

/**
 * Created by zy1584 on 2017-11-30.
 */

public class FileDownloadAdapter extends BaseQuickAdapter<FileItem, BaseViewHolder> {
	private OnItemRenderListener listener;
	public FileDownloadAdapter(@LayoutRes int layoutResId, @Nullable List<FileItem> data, OnItemRenderListener listener) {
		super(layoutResId, data);
		this.listener = listener;
	}

	@Override
	protected void convert(BaseViewHolder helper, FileItem item) {
		helper.setText(R.id.tv_name, item.getName());
		helper.addOnClickListener(R.id.iv_delete);
		helper.addOnClickListener(R.id.btn_action);
		if (listener != null){
			listener.onItemRender(helper, helper.getLayoutPosition());
		}
	}

	public interface OnItemRenderListener{
		void onItemRender(final BaseViewHolder viewHolder,final int position);
	}
}

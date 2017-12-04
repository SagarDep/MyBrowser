package com.example.learnwebview.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.learnwebview.R;
import com.example.learnwebview.web.TabView;

import java.util.List;

/**
 * Created by zy1584 on 2017-12-4.
 */

public class TabsManagerAdapter extends BaseQuickAdapter<TabView, BaseViewHolder> {

	public TabsManagerAdapter(@LayoutRes int layoutResId, @Nullable List<TabView> data) {
		super(layoutResId, data);
	}

	@Override
	protected void convert(BaseViewHolder helper, TabView item) {
		helper.setText(R.id.tv_tab_name, helper.getLayoutPosition() + "");
		helper.addOnClickListener(R.id.btn_remove);
	}
}

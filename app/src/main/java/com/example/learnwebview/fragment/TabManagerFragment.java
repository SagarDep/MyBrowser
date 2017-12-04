package com.example.learnwebview.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.learnwebview.R;
import com.example.learnwebview.activities.HomeActivity;
import com.example.learnwebview.adapter.TabsManagerAdapter;
import com.example.learnwebview.manager.TabsManager;
import com.example.learnwebview.web.TabView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zy1584 on 2017-12-4.
 */

public class TabManagerFragment extends Fragment implements BaseQuickAdapter.OnItemChildClickListener,
		BaseQuickAdapter.OnItemClickListener{

	@BindView(R.id.recyclerView)
	RecyclerView mRecyclerView;
	private TabsManagerAdapter mAdapter;

	@OnClick(R.id.btn_add_tab)
	void addTab(){
		((HomeActivity)mContext).newTabClicked();
		mContext.onBackPressed();
	}

	private Activity mContext;
	private TabsManager mTabsManager;
	private List<TabView> mTabList;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_tab_manager, container, false);
		ButterKnife.bind(this, rootView);
		mTabsManager = TabsManager.getInstance();
		mTabList = mTabsManager.getTabList();
		mAdapter = new TabsManagerAdapter(R.layout.item_list_tab_manager, mTabList);
		mAdapter.setOnItemClickListener(this);
		mAdapter.setOnItemChildClickListener(this);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
		mRecyclerView.setAdapter(mAdapter);
		return rootView;
	}

	@Override
	public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
		if (mTabList.size() > 1){
			((HomeActivity)mContext).deleteTab(position);
		} else{
			mContext.onBackPressed();
		}
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
		((HomeActivity)mContext).showTab(position);
		mContext.onBackPressed();
	}
}

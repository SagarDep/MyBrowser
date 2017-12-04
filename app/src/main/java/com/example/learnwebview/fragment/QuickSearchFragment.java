package com.example.learnwebview.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.learnwebview.R;
import com.example.learnwebview.activities.HomeActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zy1584 on 2017-12-1.
 */

public class QuickSearchFragment extends Fragment {

	@BindView(R.id.et_search)
	EditText et_search;
	@OnClick(R.id.search)
	void search(){
		String query = et_search.getText().toString().trim();
		if (!TextUtils.isEmpty(query)){
			((HomeActivity)mContext).searchTheWeb(query);
			mContext.onBackPressed();
		} else{
			Toast.makeText(mContext, "搜索内容不能为空！", Toast.LENGTH_SHORT).show();
		}
	}

	private View rootView;
	private Activity mContext;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_quick_search, container, false);
		ButterKnife.bind(this, rootView);
		RecyclerView recyclerView = rootView.findViewById(R.id.recyclerView);
		recyclerView.setLayoutManager(new GridLayoutManager(mContext, 4));
		return rootView;
	}
}

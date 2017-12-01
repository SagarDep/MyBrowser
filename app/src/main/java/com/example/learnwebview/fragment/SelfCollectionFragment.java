package com.example.learnwebview.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.learnwebview.R;

/**
 * Created by zy1584 on 2017-12-1.
 */

public class SelfCollectionFragment extends Fragment {

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
		rootView = inflater.inflate(R.layout.fragment_self_collection, container, false);
		RecyclerView recyclerView = rootView.findViewById(R.id.recyclerView);
		recyclerView.setLayoutManager(new GridLayoutManager(mContext, 4));
		return rootView;
	}
}

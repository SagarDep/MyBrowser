package com.example.learnwebview.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.example.learnwebview.Constants.Constants;
import com.example.learnwebview.R;
import com.example.learnwebview.fragment.QuickSearchFragment;
import com.example.learnwebview.fragment.TabManagerFragment;
import com.example.learnwebview.interfaces.UIController;
import com.example.learnwebview.manager.TabsManager;
import com.example.learnwebview.utils.UrlUtils;
import com.example.learnwebview.web.BrowserPresenter;
import com.example.learnwebview.web.BrowserView;
import com.example.learnwebview.web.TabView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class HomeActivity extends AppCompatActivity implements UIController, BrowserView {

	@BindView(R.id.toolbar)
	Toolbar toolbar;
	@BindView(R.id.fl_web_container)
	FrameLayout fl_web_container;
	@BindView(R.id.ib_back)
	ImageButton ib_back;
	@BindView(R.id.ib_forward)
	ImageButton ib_forward;
	@BindView(R.id.progressBar)
	ProgressBar mProgressBar;

	@OnClick(R.id.btn_search)
	void jumpToSearch() {
		QuickSearchFragment fragment = new QuickSearchFragment();
		addFragment(fragment, R.id.fl_container, true);
	}

	@OnClick(R.id.ib_tab)
	void jumpToTabsManager(){
		TabManagerFragment fragment = new TabManagerFragment();
		addFragment(fragment, R.id.fl_container, true);
	}

	@OnClick(R.id.ib_back)
	void goBack() {
		final TabView currentTab = mTabsManager.getCurrentTab();
		if (currentTab != null) {
			if (currentTab.canGoBack()) {
				currentTab.goBack();
			}
		}
	}

	@OnClick(R.id.ib_forward)
	void goForward() {
		final TabView currentTab = mTabsManager.getCurrentTab();
		if (currentTab != null) {
			if (currentTab.canGoForward()) {
				currentTab.goForward();
			}
		}
	}

	private static final ViewGroup.LayoutParams MATCH_PARENT = new ViewGroup.LayoutParams(
			ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
	private TabsManager mTabsManager;
	private BrowserPresenter mPresenter;
	private TabView mCurrentTab;
	private View mCurrentView;
	private boolean mIsIncognito;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		ButterKnife.bind(this);
		mPresenter = new BrowserPresenter(this, mIsIncognito);
		mTabsManager = TabsManager.getInstance();
		Intent intent = savedInstanceState == null ? getIntent() : null;
		boolean launchedFromHistory = intent != null && (intent.getFlags() & Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) != 0;
		if (launchedFromHistory) {
			intent = null;
		}
		mPresenter.setupTabs(intent);
		setIntent(null);
	}

	@Override
	public void setTabView(@NonNull final View view) {
		if (mCurrentView == view) {
			return;
		}
		removeViewFromParent(mCurrentView);
		fl_web_container.addView(view, 0, MATCH_PARENT);
		view.requestFocus();
		mCurrentView = view;
	}

	@Override
	public void removeTabView() {
		removeViewFromParent(mCurrentView);
		mCurrentView = null;
	}

	/**
	 * 标签页管理：删除
	 * @param position
	 */
	public synchronized void deleteTab(int position) {
		mPresenter.deleteTab(position);
	}

	/**
	 * 标签页管理：新增
	 */
	public void newTabClicked() {
		mPresenter.newTab(null, true);
	}

	/**
	 * 标签页管理：切换
	 * @param position
	 */
	public synchronized void showTab(final int position) {
		mPresenter.tabChanged(position);
	}

	@Override
	public void updateUrl(@Nullable String title, boolean shortUrl) {

	}

	@Override
	public void updateProgress(int newProgress) {
		mProgressBar.setVisibility(newProgress == 100 ? View.GONE : View.VISIBLE);
		mProgressBar.setProgress(newProgress);
	}

	@Override
	public void setBackButtonEnabled(boolean enabled) {
		ib_back.setEnabled(enabled);
	}

	@Override
	public void setForwardButtonEnabled(boolean enabled) {
		ib_forward.setEnabled(enabled);
	}

	public void searchTheWeb(@NonNull String query) {
		final TabView currentTab = mTabsManager.getCurrentTab();
		if (query.isEmpty()) {
			return;
		}
		String searchUrl = Constants.BAIDU_SEARCH + UrlUtils.QUERY_PLACE_HOLDER;
		query = query.trim();
		if (currentTab != null) {
			currentTab.stopLoading();
			currentTab.loadUrl(UrlUtils.smartUrlFilter(query, true, searchUrl));
		}
	}

	private void removeViewFromParent(@Nullable View view) {
		if (view == null) {
			return;
		}
		ViewParent parent = view.getParent();
		if (parent instanceof ViewGroup) {
			((ViewGroup) parent).removeView(view);
		}
	}

	public void addFragment(Fragment fragment, int contentId, boolean addToBackStack) {
		if (fragment != null) {
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			transaction.add(contentId, fragment, fragment.getClass().getSimpleName());
			if (addToBackStack) {
				transaction.addToBackStack(null);
			}
			transaction.commitAllowingStateLoss();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		mTabsManager.saveState();
	}
}

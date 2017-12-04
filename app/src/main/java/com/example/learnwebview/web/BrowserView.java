package com.example.learnwebview.web;

import android.support.annotation.NonNull;
import android.view.View;

/**
 * Created by zy1584 on 2017-12-4.
 */

public interface BrowserView {

	void setTabView(@NonNull View view);

	void removeTabView();

	void setForwardButtonEnabled(boolean enabled);

	void setBackButtonEnabled(boolean enabled);
}

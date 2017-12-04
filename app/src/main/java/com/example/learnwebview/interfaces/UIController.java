package com.example.learnwebview.interfaces;

import android.support.annotation.Nullable;

/**
 * Created by zy1584 on 2017-11-29.
 */

public interface UIController {

	void updateUrl(@Nullable String title, boolean shortUrl);

	void updateProgress(int newProgress);

	void setBackButtonEnabled(boolean enabled);

	void setForwardButtonEnabled(boolean enabled);

}

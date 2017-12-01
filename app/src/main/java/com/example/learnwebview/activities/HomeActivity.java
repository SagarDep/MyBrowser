package com.example.learnwebview.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;

import com.example.learnwebview.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity {

	@BindView(R.id.toolbar)
	Toolbar toolbar;
	@BindView(R.id.autoCompleteView)
	AutoCompleteTextView autoCompleteView;
	@BindView(R.id.fl_container)
	FrameLayout fl_container;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		ButterKnife.bind(this);


	}
}

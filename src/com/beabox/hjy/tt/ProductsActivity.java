package com.beabox.hjy.tt;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;

import com.app.base.init.BaseActivity;
import com.app.base.init.MyApplication;
import com.umeng.message.PushAgent;

/**
 * Created by fangjilue on 14-8-29.
 */
public class ProductsActivity extends BaseActivity implements
		View.OnClickListener {
	View backBtn;

	private boolean isLoading = false;
	private int currentPage;

	RadioGroup cateContainer;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		MyApplication.getInstance().addActivity(this);
    	PushAgent.getInstance(this).onAppStart();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_products);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.backBtn:
			finish();
			break;
		}
	}

	@Override
	public void dataCallBack(Object tag,  int statusCode,Object result) {

	}

	@Override
	public void setupView() {

	}

	@Override
	public void addListener() {

	}

	@Override
	public void sendMessageHandler(int messageCode) {

	}
	
	@Override
	protected String getActivityName() {
		return "产品库";
	}

}

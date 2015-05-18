package com.beabox.hjy.tt;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.app.base.entity.MyMasterEntity;
import com.app.base.init.BaseActivity;
import com.app.base.init.MyApplication;
import com.app.service.MyMasterAsynTaskService;
import com.base.pulltorefresh.view.PullToRefreshBase;
import com.base.pulltorefresh.view.PullToRefreshBase.Mode;
import com.base.pulltorefresh.view.PullToRefreshBase.OnRefreshListener2;
import com.base.pulltorefresh.view.PullToRefreshListView;
import com.base.service.action.constant.HttpTagConstantUtils;
import com.skinrun.trunk.adapter.MyMasterListAdapter;
import com.umeng.message.PushAgent;

@SuppressWarnings("serial")
public class MyMasterActivity extends BaseActivity implements OnItemClickListener{
	private ArrayList<MyMasterEntity> myMasterEntityList = new ArrayList<MyMasterEntity>();
	private MyMasterListAdapter adapter;
	private PullToRefreshListView pullToRefreshListView;
	private ImageView backBtn;
	private ListView realListView;
	private SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		MyApplication.getInstance().addActivity(this);
		PushAgent.getInstance(this).onAppStart();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_master);
		setupView();
		addListener();
	}

	@Override
	public void setupView() {
		pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.myMaster);
		backBtn = (ImageView) findViewById(R.id.backBtn);
		
		realListView = pullToRefreshListView.getRefreshableView();
		pullToRefreshListView.setMode( Mode.DISABLED);
		pullToRefreshListView
				.setOnRefreshListener(new OnRefreshListener2<ListView>() {
					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						String label = format.format(new Date());
						refreshView.getLoadingLayoutProxy()
								.setLastUpdatedLabel("最后更新时间:" + label);
						getData();
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {

					}

				});

		Runnable task = new Runnable() {

			@Override
			public void run() {
				getData();
			}
		};

		new Handler().post(task);
	}

	protected void getData() {
		new MyMasterAsynTaskService(getApplicationContext(), HttpTagConstantUtils.MY_MASTER_TAG, this).doMasterList();
		
	}
	@Override
	public void dataCallBack(Object tag,  int statusCode,Object result) {
		if((Integer)tag == HttpTagConstantUtils.MY_MASTER_TAG){
			myMasterEntityList = (ArrayList<MyMasterEntity>)result ;
			adapter = new MyMasterListAdapter(myMasterEntityList, getApplicationContext());
			pullToRefreshListView.setAdapter(adapter);
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void addListener() {
		backBtn.setOnClickListener(this);
		pullToRefreshListView.setOnItemClickListener(this);
	}
	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.backBtn:
			finish();
			break;

		default:
			break;
		}
	}



	@Override
	public void sendMessageHandler(int messageCode) {
		
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View v, int position, long _id) {
		Intent intent = new Intent(this,MasterInfoActivity.class);
		startActivity(intent);
	}
	
	@Override
	protected String getActivityName() {
		return "我的美容老师";
	}

}

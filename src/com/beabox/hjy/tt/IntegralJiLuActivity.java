package com.beabox.hjy.tt;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.app.base.entity.GoldCoinsEntity;
import com.app.base.init.BaseActivity;
import com.app.base.init.MyApplication;
import com.app.service.GetCoinRecordsAsynTaskService;
import com.base.pulltorefresh.view.PullToRefreshBase;
import com.base.pulltorefresh.view.PullToRefreshBase.Mode;
import com.base.pulltorefresh.view.PullToRefreshBase.OnRefreshListener2;
import com.base.pulltorefresh.view.PullToRefreshListView;
import com.base.service.action.constant.HttpTagConstantUtils;
import com.skinrun.trunk.adapter.CoinsRecordListAdapter;
import com.umeng.message.PushAgent;

/**
 * 积分记录页面 Created by beabox on 14-12-22.
 */
public class IntegralJiLuActivity extends BaseActivity {
	private ArrayList<GoldCoinsEntity> data = new ArrayList<GoldCoinsEntity>();
	private CoinsRecordListAdapter adapter;
	private PullToRefreshListView pullToRefreshListView;
	private ListView realListView;
	private SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");

	private ImageView backBtn;
	private int pageIndex=1;
	private int pageSize=10;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		MyApplication.getInstance().addActivity(this);
		PushAgent.getInstance(this).onAppStart();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.integral_record);
		setupView();
		addListener();
	}

	@Override
	public void setupView() {
		pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.my_coin_record_listView);
		backBtn = (ImageView) findViewById(R.id.backBtn);
		realListView = pullToRefreshListView.getRefreshableView();
		
		adapter=new CoinsRecordListAdapter(data, this);
		realListView.setAdapter(adapter);
		
		pullToRefreshListView.setMode(Mode.BOTH);
		pullToRefreshListView
				.setOnRefreshListener(new OnRefreshListener2<ListView>() {
					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						pageIndex=1;
						
						String label = format.format(new Date());
						refreshView.getLoadingLayoutProxy()
								.setLastUpdatedLabel("最后更新时间:" + label);
						getMyMessage();
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						pageIndex++;
						getMyMessage();
					}

				});

		Runnable task = new Runnable() {

			@Override
			public void run() {
				getMyMessage();
			}
		};

		new Handler().post(task);
	}

	protected void getMyMessage() {
		new GetCoinRecordsAsynTaskService(getApplicationContext(),
				HttpTagConstantUtils.MY_INTEGRAL_RECORD, this).doCoinRecords(aCache.getAsString("Token"), pageIndex, pageSize);

	}

	@Override
	public void addListener() {
		backBtn.setOnClickListener(this);
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
	public void dataCallBack(Object tag, int statusCode, Object result) {
		try {
			if ((Integer) (tag) == HttpTagConstantUtils.MY_INTEGRAL_RECORD) {
				ArrayList<GoldCoinsEntity> tem = (ArrayList<GoldCoinsEntity>) result;
				if(tem!=null&&tem.size()>0){
					if(pageIndex==1){
						data.clear();
					}
					data.addAll(tem);
					adapter.notifyDataSetChanged();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pullToRefreshListView.onRefreshComplete();
		}
	}

	@Override
	public void sendMessageHandler(int messageCode) {

	}
	
	@Override
    protected String getActivityName() {
        return "积分记录";
    }

}

package com.beabox.hjy.tt;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.app.base.entity.PushEntity;
import com.app.base.entity.TestNewsEntity;
import com.app.base.entity.UserEntity;
import com.app.base.init.MyApplication;
import com.app.service.GetTestNewsService;
import com.base.app.utils.DBService;
import com.base.app.utils.HomeTag;
import com.base.app.utils.PushNumUtil;
import com.base.pulltorefresh.view.PullToRefreshBase;
import com.base.pulltorefresh.view.PullToRefreshBase.Mode;
import com.base.pulltorefresh.view.PullToRefreshBase.OnRefreshListener2;
import com.base.pulltorefresh.view.PullToRefreshListView;
import com.base.service.action.constant.HttpTagConstantUtils;
import com.base.service.impl.HttpAysnResultInterface;
import com.base.supertoasts.util.AppToast;
import com.skinrun.trunk.adapter.MyTestNewsAdapter;
import com.umeng.message.PushAgent;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class MyTestNewsActivity extends Activity implements OnRefreshListener2<ListView>, HttpAysnResultInterface, OnClickListener {
	private TextView head_title;
	private PullToRefreshListView pullToRefreshListView;
	private ListView realListView;
	private ArrayList<TestNewsEntity> data=new ArrayList<TestNewsEntity>();
	private MyTestNewsAdapter mAdapter;
	private SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
	private int pageIndex=1;
	private int pageSize=10;
	private String test_type=HomeTag.FACE_MARK;
	private int unReadNum;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(this);
		PushAgent.getInstance(this).onAppStart();
		test_type=getIntent().getStringExtra("test_type");
		unReadNum=getIntent().getIntExtra("unReadNum", 0);
		setContentView(R.layout.my_test_news_activity);
		initView();
	}
	
	
	private void initView(){
		head_title=(TextView) findViewById(R.id.head_title);
		head_title.setText("消息");
		pullToRefreshListView=(PullToRefreshListView)findViewById(R.id.commentListView);
		
		pullToRefreshListView.setMode(Mode.BOTH);
		pullToRefreshListView.setOnRefreshListener(this);
		realListView=pullToRefreshListView.getRefreshableView();
		mAdapter=new MyTestNewsAdapter(this, data);
		realListView.setAdapter(mAdapter);
		findViewById(R.id.backComment).setOnClickListener(this);
		
		loadData();
	}
	private void loadData(){
		UserEntity user=DBService.getUserEntity();
		if(user!=null){
			new GetTestNewsService(this, HttpTagConstantUtils.GET_MY_TEST_MESSAGE, this).doGet(user.getToken(), test_type, pageIndex, pageSize);
		}
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		pageIndex=1;
		String label = format.format(new Date());
		refreshView.getLoadingLayoutProxy().setLastUpdatedLabel("最后更新时间:" + label);
		loadData();
	}


	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		pageIndex++;
		loadData();
	}

	private void changeUnreadNum(){
		UserEntity user=DBService.getUserEntity();
		if(user!=null){
			PushEntity pushEntity=PushNumUtil.get(""+user.getToken());
			pushEntity.setMyTestMessage(0);
			PushNumUtil.save(pushEntity);
		}
	}
	
	
	
	@Override
	public void dataCallBack(Object tag, int statusCode, Object result) {
		try{
			switch((Integer)tag){
			case HttpTagConstantUtils.GET_MY_TEST_MESSAGE:
				if(statusCode==200||statusCode==201){
					//改变未读条数
					changeUnreadNum();
					
					ArrayList<TestNewsEntity> tem=(ArrayList<TestNewsEntity>)result;
					if(tem!=null&&tem.size()>0){
						if(pageIndex==1){
							data.clear();
						}
						data.addAll(tem);
						mAdapter.notifyDataSetChanged();
					}else{
						if(pageIndex>1){
							AppToast.toastMsg(this, "没有更多数据了~");
						}
					}
				}
				break;
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			pullToRefreshListView.onRefreshComplete();
		}
	}


	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.backComment:
			finish();
			break;
		}
	}
	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.activity_enter_from_left, R.anim.activity_exit_to_right);
	}
}

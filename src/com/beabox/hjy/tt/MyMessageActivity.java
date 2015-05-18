package com.beabox.hjy.tt;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.kymjs.aframe.utils.SystemTool;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.app.base.entity.MineMessageEntity;
import com.app.base.entity.MyMessageEntity;
import com.app.base.entity.PushEntity;
import com.app.base.entity.UserEntity;
import com.app.base.init.BaseActivity;
import com.app.base.init.MyApplication;
import com.app.service.CancleAttentionMerchantService;
import com.app.service.DeleteMyMessageService;
import com.app.service.GetMyMessageAsynTaskService;
import com.app.service.GetMyMessageService;
import com.base.app.utils.DBService;
import com.base.app.utils.PushNumUtil;
import com.base.dialog.lib.Effectstype;
import com.base.dialog.lib.NiftyDialogBuilder;
import com.base.pulltorefresh.view.PullToRefreshBase;
import com.base.pulltorefresh.view.PullToRefreshBase.Mode;
import com.base.pulltorefresh.view.PullToRefreshBase.OnRefreshListener2;
import com.base.pulltorefresh.view.PullToRefreshListView;
import com.base.service.action.constant.HttpTagConstantUtils;
import com.base.supertoasts.util.AppToast;
import com.skinrun.trunk.adapter.MyMessageListAdapter;
import com.skinrun.trunk.main.MerchantFrag;
import com.umeng.message.PushAgent;

public class MyMessageActivity extends BaseActivity implements
		OnItemLongClickListener {

	private ArrayList<MineMessageEntity> data = new ArrayList<MineMessageEntity>();
	private MyMessageListAdapter mAdapter;
	private PullToRefreshListView pullToRefreshListView;
	private ListView realListView;
	private SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
	private ImageView no_my_message;
	private ImageView backBtn;

	private int pageIndex = 1;
	private int pageSize = 10;

	private String TAG = "MyMessageActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		MyApplication.getInstance().addActivity(this);
		PushAgent.getInstance(this).onAppStart();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_message_activity);
		setupView();
		addListener();
	}

	@Override
	public void setupView() {
		no_my_message = (ImageView) findViewById(R.id.no_my_message);
		pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.my_message_listView);
		backBtn = (ImageView) findViewById(R.id.backBtn);
		realListView = pullToRefreshListView.getRefreshableView();
		pullToRefreshListView.setMode(Mode.BOTH);

		realListView = pullToRefreshListView.getRefreshableView();
		mAdapter = new MyMessageListAdapter(this, data);
		realListView.setAdapter(mAdapter);
		//realListView.setDividerHeight(20);
		realListView.setOnItemLongClickListener(this);
		// realListView.setSelector(getResources().getDrawable(R.drawable.list_item_selector));
		pullToRefreshListView
				.setOnRefreshListener(new OnRefreshListener2<ListView>() {
					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						String label = format.format(new Date());
						refreshView.getLoadingLayoutProxy()
								.setLastUpdatedLabel("最后更新时间:" + label);
						pageIndex = 1;
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
		new GetMyMessageService(this, HttpTagConstantUtils.GET_MINE_MESSAGE,this).doGet(aCache.getAsString("Token"), 0, pageIndex, pageSize);
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
			if((Integer) (tag) == HttpTagConstantUtils.DELETE_MY_MESSAGE){
				
				Log.e(TAG, "========删除我的消息返回码："+statusCode);
				
				
			}else if ((Integer) (tag) == HttpTagConstantUtils.GET_MINE_MESSAGE) {
				@SuppressWarnings("unchecked")
				ArrayList<MineMessageEntity> tem = (ArrayList<MineMessageEntity>) result;
				Log.e(TAG, "==========我的消息长度：" + tem.size()+"statusCode:"+statusCode);
				if (statusCode == 200 || statusCode == 201) {
					UserEntity user=DBService.getUserEntity();
					if(user!=null){
						PushEntity pushEntity=PushNumUtil.get(user.getToken());
						if(pushEntity!=null){
							pushEntity.setMyMessage(0);
							PushNumUtil.save(pushEntity);
						}
					}
					if (tem != null && tem.size() > 0) {
						mAdapter.notifyDataSetChanged();
						no_my_message.setVisibility(View.GONE);
						pullToRefreshListView.setVisibility(View.VISIBLE);
						if (pageIndex == 1) {
							data.clear();
						}
						data.addAll(tem);
						mAdapter.notifyDataSetChanged();
					} else {
						if (pageIndex > 1) {
							AppToast.toastMsgCenter(getApplicationContext(),
									"~_~没有更多了").show();
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					if (null != pullToRefreshListView) {
						pullToRefreshListView.onRefreshComplete();
					}
				}
			}, 100);
		}
	}

	@Override
	public void sendMessageHandler(int messageCode) {

	}

	@Override
	protected String getActivityName() {
		return "我的消息";
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
			final int position, long arg3) {

		if (!SystemTool.checkNet(this)) {
			AppToast.toastMsgCenter(this,
					this.getResources().getString(R.string.no_network)).show();
			return false;
		}
		final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder
				.getInstance(this);

		try {
			dialogBuilder.withTitle(null).withMessage(null).withMessageColor("#FFFFFFFF").isCancelableOnTouchOutside(false)
					.withDuration(200).withEffect(Effectstype.Fadein).withButton1Text("是").setCustomView(R.layout.delete_my_message_dialog, this)
					.setButton1Click(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							dialogBuilder.dismiss();
							UserEntity u = DBService.getUserEntity();
							if (u != null) {
								int id = Integer.parseInt(data.get(position - 1).getMsg_id());
								new DeleteMyMessageService(MyMessageActivity.this,HttpTagConstantUtils.DELETE_MY_MESSAGE,
										MyMessageActivity.this).delete(u.getToken(), id);

								Log.e(TAG, "===========长按事件ID:"+id);

							}
							data.remove(position - 1);
							if (data.size() == 0) {
								no_my_message.setVisibility(View.VISIBLE);
							}
							mAdapter.notifyDataSetChanged();
						}
					}).withButton2Text("否")
					.setButton2Click(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							dialogBuilder.dismiss();
						}
					}).show();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}

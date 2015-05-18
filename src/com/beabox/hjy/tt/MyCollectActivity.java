package com.beabox.hjy.tt;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.kymjs.aframe.utils.SystemTool;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.app.base.entity.CollectEntity;
import com.app.base.entity.HomeSelectedEntity;
import com.app.base.init.MyApplication;
import com.app.service.DeleteMyCollectService;
import com.app.service.GetHomeCollectService;
import com.base.app.utils.DBService;
import com.base.app.utils.HomeTag;
import com.base.dialog.lib.Effectstype;
import com.base.dialog.lib.NiftyDialogBuilder;
import com.base.pulltorefresh.view.PullToRefreshBase;
import com.base.pulltorefresh.view.PullToRefreshBase.Mode;
import com.base.pulltorefresh.view.PullToRefreshBase.OnRefreshListener2;
import com.base.pulltorefresh.view.PullToRefreshListView;
import com.base.service.action.constant.HttpTagConstantUtils;
import com.base.service.impl.HttpAysnResultInterface;
import com.base.supertoasts.util.AppToast;
import com.skinrun.trunk.adapter.MyCollectAdapter;
import com.umeng.message.PushAgent;

public class MyCollectActivity extends Activity implements OnClickListener, OnRefreshListener2<ListView>, 
		HttpAysnResultInterface, OnItemLongClickListener, OnItemClickListener{
	private PullToRefreshListView pullToRefreshListView;
	private ListView realListView; 
	private TextView tvTitle;
	private MyCollectAdapter mAdapter;
	private ArrayList<CollectEntity> data=new ArrayList<CollectEntity>();
	
	private String action="myFavorites";
	private int pageIndex=1,pageSize=8;
	private ImageView no_my_message;
	private SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		MyApplication.getInstance().addActivity(this);
		PushAgent.getInstance(this).onAppStart();
		super.onCreate(savedInstanceState);
		
		
		setContentView(R.layout.comment_layout);
		findViewById(R.id.contain_sendMessage).setVisibility(View.GONE);
		no_my_message=(ImageView)findViewById(R.id.no_my_message);
		no_my_message.setVisibility(View.VISIBLE);
		findViewById(R.id.backComment).setOnClickListener(this);
		tvTitle=(TextView)findViewById(R.id.head_title);
		tvTitle.setText("我的收藏");
		
		pullToRefreshListView=(PullToRefreshListView)findViewById(R.id.commentListView);
		
		pullToRefreshListView.setMode(Mode.BOTH);
		pullToRefreshListView.setOnRefreshListener(this);
		realListView=pullToRefreshListView.getRefreshableView();
		
		realListView.setSelector(this.getResources().getDrawable(R.drawable.list_item_selector));
		
		mAdapter=new MyCollectAdapter(this, data);
		realListView.setAdapter(mAdapter);
		realListView.setDividerHeight(0);
		realListView.setOnItemLongClickListener(this);
		realListView.setOnItemClickListener(this);
		//加载数据
		loadData();
	}


	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.backComment:
			finish();
			break;
		}
	}
	
	private void loadData(){
		
		try{
			String token=DBService.getUserEntity().getToken();
			new GetHomeCollectService(this, HttpTagConstantUtils.GET_HOME_COLLECT, this).getCollect(token, action,"", pageIndex, pageSize);
		}catch(Exception e){
			
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
		try{
			pageIndex++;
			loadData();
			
		}catch(Exception e){
			
		}
	}


	@Override
	public void dataCallBack(Object tag, int statusCode, Object result) {
		try{
			switch((Integer)tag){
			case HttpTagConstantUtils.GET_HOME_COLLECT:
				pullToRefreshListView.onRefreshComplete();
				ArrayList<CollectEntity> tem=(ArrayList<CollectEntity>)result;
				if(statusCode==200||statusCode==201){
					if(tem!=null&&tem.size()>0){
						if(pageIndex==1){
							data.clear();
						}
						no_my_message.setVisibility(View.GONE);
						data.addAll(tem);
						mAdapter.notifyDataSetChanged();
					}else{
						AppToast.toastMsg(this, "没有更多数据了~").show();
					}
				}
				
				break;
			}
			
		}catch(Exception e){
			
		}
		
		
		
	}


	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int position,
			long arg3) {
		if (!SystemTool.checkNet(this)) {
			AppToast.toastMsgCenter(this,this.getResources().getString(R.string.no_network)).show();
			return false;
		}
		final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(MyCollectActivity.this);

		
		
		try{
			dialogBuilder.withTitle(null).withMessage(null).withMessageColor("#FFFFFFFF")
			.isCancelableOnTouchOutside(false).withDuration(200).withEffect(Effectstype.Fadein)
			.withButton1Text("是").setCustomView(R.layout.delete_my_collect,MyCollectActivity.this)
			.setButton1Click(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					dialogBuilder.dismiss();
					new DeleteMyCollectService(MyCollectActivity.this, HttpTagConstantUtils.DELETE_MY_COLLECT, MyCollectActivity.this)
					.doDelete(DBService.getUserEntity().getToken(), "deleteFavorite", "single", data.get(position-1).getFavorite_id());
					data.remove(position-1);
					if(data.size()==0){
						no_my_message.setVisibility(View.VISIBLE);
					}
					mAdapter.notifyDataSetChanged();
				}
			}).withButton2Text("否").setButton2Click(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					dialogBuilder.dismiss();
				}
			}).show();
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}


	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if (!SystemTool.checkNet(this)) {
			AppToast.toastMsgCenter(this,this.getResources().getString(R.string.no_network)).show();
			return;
		}
		try{
			CollectEntity c=data.get(arg2-1);
			if(c.getData_type().equals(HomeTag.FACE_MARK)){
				Intent intent=new Intent(this, SelectedSecondActivity.class);
				HomeSelectedEntity h=(HomeSelectedEntity) c.getFavorite_info();
				
				intent.putExtra("ID", h.getId());
				intent.putExtra("SOURSE_TYPE", HomeTag.FACE_MARK);
				startActivity(intent);
			}
			
		}catch(Exception e){
			
		}
		
	}
	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.activity_enter_from_left, R.anim.activity_exit_to_right);
	}
}

package com.skinrun.trunk.main;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.kymjs.aframe.utils.SystemTool;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.app.base.entity.CollectEntity;
import com.app.base.entity.HomeSelectedEntity;
import com.app.base.entity.PushEntity;
import com.app.base.entity.UnReadMessageEntity;
import com.app.base.entity.UserEntity;
import com.app.service.GetHomeCollectService;
import com.app.service.GetUnReadTestMessageService;
import com.avoscloud.chat.service.UserService;
import com.avoscloud.chat.util.PhotoUtils;
import com.baidu.mobstat.StatService;
import com.base.app.utils.DBService;
import com.base.app.utils.HomeTag;
import com.base.app.utils.PushNumUtil;
import com.base.app.utils.StringUtil;
import com.base.pulltorefresh.view.PullToRefreshBase;
import com.base.pulltorefresh.view.PullToRefreshBase.Mode;
import com.base.pulltorefresh.view.PullToRefreshBase.OnRefreshListener2;
import com.base.pulltorefresh.view.PullToRefreshListView;
import com.base.service.action.constant.HttpTagConstantUtils;
import com.base.service.impl.HttpAysnResultInterface;
import com.base.service.impl.HttpClientUtils;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.MyTestNewsActivity;
import com.beabox.hjy.tt.R;
import com.beabox.hjy.tt.SelectedSecondActivity;
import com.idongler.widgets.CircleImageView;
import com.skinrun.trunk.adapter.MyCollectAdapter;
import com.umeng.analytics.MobclickAgent;

public class MyTestFrag extends Fragment implements HttpAysnResultInterface, OnClickListener, OnRefreshListener2<ListView>, OnItemClickListener {
	private ArrayList<CollectEntity> data=new ArrayList<CollectEntity>();
	private String action="myTests";
	private String test_type=HomeTag.FACE_MARK;
	private int pageIndex=1;
	private int pageSize=8;
	private PullToRefreshListView pullToRefreshListView;
	private ListView realListView; 
	private MyCollectAdapter mAdapter;
	private ImageView no_my_message;
	private SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
	private UnReadMessageEntity unreadEntity=new UnReadMessageEntity();
	
	private View headView;
	
	private String TAG="MyTestFrag";
	
	public static MyTestFrag getInstance(Bundle bundle){
		MyTestFrag myTestFrag=new MyTestFrag();
		myTestFrag.setArguments(bundle);
		return myTestFrag;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle=getArguments();
		test_type=bundle.getString("TEST_TYPE");
	}
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view=inflater.inflate(R.layout.comment_layout, null);
		
		no_my_message=(ImageView)view.findViewById(R.id.no_my_message);
		no_my_message.setVisibility(View.VISIBLE);
		
		view.findViewById(R.id.contain_sendMessage).setVisibility(View.GONE);
		view.findViewById(R.id.containTitle).setVisibility(View.GONE);
		pullToRefreshListView=(PullToRefreshListView)view.findViewById(R.id.commentListView);
		pullToRefreshListView.setMode(Mode.BOTH);
		pullToRefreshListView.setOnRefreshListener(this);
		
		realListView=pullToRefreshListView.getRefreshableView();
		realListView.setSelector(getActivity().getResources().getDrawable(R.drawable.list_item_selector));
		
		mAdapter=new MyCollectAdapter(getActivity(), data);
		realListView.setDividerHeight(0);
		realListView.setAdapter(mAdapter);
		realListView.setOnItemClickListener(this);
		
		loadData();
		
		return view;
	}
	
	
	private void addHeader(){
		
		if(realListView.getHeaderViewsCount()>0&&headView!=null){
			realListView.removeHeaderView(headView);
		}
		
		headView=LayoutInflater.from(getActivity()).inflate(R.layout.news_come_layout, null);
		View news_come_container=headView.findViewById(R.id.news_come_container);
		CircleImageView news_from_joinerIcon=(CircleImageView) headView.findViewById(R.id.news_from_joinerIcon);
		TextView tvNewsNum=(TextView) headView.findViewById(R.id.tvNewsNum);
		
		tvNewsNum.setText(unreadEntity.getUnread()+"条新消息");
		if(unreadEntity.getLast_user_avatar()!=null&&!unreadEntity.getLast_user_avatar().equals("")){
			UserService.imageLoader.displayImage(""+unreadEntity.getLast_user_avatar(),news_from_joinerIcon,
					PhotoUtils.myPicImageOptions);
		}else{
			UserService.imageLoader.displayImage(""+HttpClientUtils.USER_IMAGE_URL+""+StringUtil
					.getPathByUid(String.valueOf(unreadEntity.getLast_uid())),news_from_joinerIcon,
					PhotoUtils.myPicImageOptions);
		}
		
		news_come_container.setOnClickListener(this);
		
		
		realListView.addHeaderView(headView);
		
	}
	
	private void loadData(){
		try{
			UserEntity user=DBService.getUserEntity();
			if(user!=null){
				if(test_type.equals(HomeTag.FACE_MARK)){
					new GetHomeCollectService(getActivity(), HttpTagConstantUtils.GET_MY_MASK_TEST,MyTestFrag.this)
					.getCollect(user.getToken(), action, test_type, pageIndex, pageSize);
				}else if(test_type.equals(HomeTag.SKIN_TEST)){
					new GetHomeCollectService(getActivity(), HttpTagConstantUtils.GET_MY_SKIN_TEST,MyTestFrag.this)
					.getCollect(user.getToken(), action, test_type, pageIndex, pageSize);
				}
				
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	@Override
	public void onResume() {
		super.onResume();
		loadUnreadNumber();
		MobclickAgent.onResume(getActivity());
		StatService.onResume(getActivity());
		
	}
	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(getActivity());
		StatService.onPause(getActivity());
	}
	private void loadUnreadNumber(){
		try{
			UserEntity user=DBService.getUserEntity();
			if(user!=null){
				new GetUnReadTestMessageService(getActivity(), HttpTagConstantUtils.GET_UNREAD_TEST_MESSAGE, this).
					doGetUnread(user.getToken(), test_type);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void dataCallBack(Object tag, int statusCode, Object result) {
		try{
			if((Integer)tag==HttpTagConstantUtils.GET_MY_SKIN_TEST||(Integer)tag==HttpTagConstantUtils.GET_MY_MASK_TEST){
				pullToRefreshListView.onRefreshComplete();
				Log.e(TAG, "============dataCallBack:statusCode："+statusCode);
				
				if(statusCode==200||statusCode==201){
					changeUnreadNum();
					
					
					ArrayList<CollectEntity> tem=(ArrayList<CollectEntity>)result;
					Log.e(TAG, "============dataCallBack:");
					
					if(tem!=null&&tem.size()>0){
						Log.e(TAG, "============dataCallBack:"+tem.size());
						if(pageIndex==1){
							data.clear();
						}
						no_my_message.setVisibility(View.GONE);
						data.addAll(tem);
						mAdapter.notifyDataSetChanged();
					}else{
						AppToast.toastMsg(getActivity(), "没有更多数据了~").show();
					}
				}
				
				
			}else if(HttpTagConstantUtils.GET_UNREAD_TEST_MESSAGE==(Integer)tag){
				unreadEntity=(UnReadMessageEntity)result;
				if(unreadEntity!=null&&unreadEntity.getUnread()>0){
					//加头
					addHeader();
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.news_come_container:
			realListView.removeHeaderView(headView);
			Intent intent=new Intent(getActivity(), MyTestNewsActivity.class);
			intent.putExtra("test_type", test_type);
			intent.putExtra("unReadNum", unreadEntity.getUnread());
			startActivity(intent);
			getActivity().overridePendingTransition(R.anim.activity_enter_from_right, R.anim.activity_exit_to_left);
			break;
		}
	}
	
	private void changeUnreadNum(){
		UserEntity user=DBService.getUserEntity();
		if(user!=null){
			PushEntity pushEntity=PushNumUtil.get(""+user.getToken());
			if(pushEntity!=null){
				pushEntity.setMyTestMessage(0);
				PushNumUtil.save(pushEntity);
			}
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

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		
		if (!SystemTool.checkNet(getActivity())) {
			AppToast.toastMsgCenter(getActivity(),getActivity().getResources().getString(R.string.no_network)).show();
			return;
		}
		try{
			CollectEntity c=data.get(arg2-1);
			if(c.getData_type().equals(HomeTag.FACE_MARK)){
				Intent intent=new Intent(getActivity(), SelectedSecondActivity.class);
				HomeSelectedEntity h=(HomeSelectedEntity) c.getFavorite_info();
				
				intent.putExtra("ID", h.getId());
				intent.putExtra("SOURSE_TYPE", HomeTag.FACE_MARK);
				startActivity(intent);
			}
			
		}catch(Exception e){
			
		}
	}
}

package com.skinrun.trunk.main;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.kymjs.aframe.utils.SystemTool;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import com.app.base.entity.Pm9HistoryDataEntity;
import com.app.base.entity.TopicsEntity;
import com.app.base.entity.UserEntity;
import com.app.base.init.ACache;
import com.app.base.init.MyApplication;
import com.app.service.GetPm9HistoryAsynTaskService;
import com.app.service.GetTopicsService;
import com.app.service.GetUserAddressService;
import com.app.service.PostUserShareService;
import com.app.service.PutUserAddressTaskService;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.Group;
import com.avos.avoscloud.LogInCallback;
import com.avoscloud.chat.service.UserService;
import com.avoscloud.chat.service.listener.GroupEventListener;
import com.avoscloud.chat.service.receiver.GroupMessageReceiver;
import com.avoscloud.chat.service.receiver.MsgReceiver;
import com.avoscloud.chat.util.PhotoUtils;
import com.baidu.mobstat.StatService;
import com.base.app.utils.DBService;
import com.base.app.utils.DateUtil;
import com.base.app.utils.EasyLog;
import com.base.app.utils.StringUtil;
import com.base.app.utils.UMShareUtil;
import com.base.app.utils.UMShareUtil.ShareAction;
import com.base.dialog.lib.Effectstype;
import com.base.dialog.lib.NiftyDialogBuilder;
import com.base.pulltorefresh.view.PullToRefreshBase;
import com.base.pulltorefresh.view.PullToRefreshBase.Mode;
import com.base.pulltorefresh.view.PullToRefreshBase.OnRefreshListener2;
import com.base.pulltorefresh.view.PullToRefreshListView;
import com.base.service.action.constant.HttpTagConstantUtils;
import com.base.service.impl.HttpAysnResultInterface;
import com.base.service.impl.HttpClientUtils;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.LeanCloudChatActivity;
import com.beabox.hjy.tt.LoginActivity;
import com.beabox.hjy.tt.NineIntroduceActivity;
import com.beabox.hjy.tt.R;
import com.skinrun.trunk.adapter.Pm9DataListAdapter;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.listener.SocializeListeners.SocializeClientListener;

/**
 * 晚九点页面
 * @author zhup
 * 
 */
public class NinePointFrag extends Fragment implements OnClickListener,
		HttpAysnResultInterface , GroupEventListener{
	
	private final static String TAG = "NinePointFrag";
	private String default_username =  "zhupei";
	private String default_password = "111111";
	private String timmer_str = "21:00:00";// 每晚九点
	String date = "yyyy-MM-dd HH:mm:ss";
	private Date now = new Date();
	
	private boolean isForbidden = false ;
	
	SimpleDateFormat formatAll = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	SimpleDateFormat formatYMD = new SimpleDateFormat("yyyy-MM-dd ");
	SimpleDateFormat formatHMS = new SimpleDateFormat("HH:mm:ss");
	SimpleDateFormat formatNotice = new SimpleDateFormat("MM~dd");
	
	TextView hour;
	TextView minute;
	TextView second;
	View be_start;//新版即将开始
	View going_start;
	View over;
	View get_in;
	View nine_clock_introduce ;
	
	//RequestQueue mQueue ; 
	
	private TextView nine_clock_title ;//晚九点标题
	private TextView nine_clock_content ;//晚九点内容
	private TextView nine_status; //状态
	private TextView pm9_status_tips; //状态v2
	private ImageView nine_clock_logo ;//晚九点logo
	
	private View noticeLayout;
	private View noticeTxtLayout;
	private TextView noticeTxt; //中奖公告
	private boolean animRunning;
	
	private   float moveY; 

	private InListOfAwardedPopupWindow inListOfAwardedPopupWindow;
	private NoListOfAwardedPopupWindow noListOfAwardedPopupWindow;

	private final static int LOGIN_FAILED = 1;
	private final static int LOGIN_SUCCESS = 2;
	private final static int LOGOUT_SUCCESS = 3;
	private final static int LOGOUT_FAIL = 4;
	private final static int JOIN_GROUP = 5;

	private Handler handler = new Handler();
	private MyHandler myHandler = new MyHandler();

	//private static String groupId = "1419565317182989";
	
	Calendar calendarInstatnce = Calendar.getInstance() ;
	
	AVUser user ; 
	public static boolean isLoginSuccess = false ;
	public static boolean isEnd = false ;
	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			try {
				if(!isEnd){
					Date now = new Date();
					String now_str = formatYMD.format(now);// 得到当前日期格式化后的字符串
					String tonight_time_str = now_str + timmer_str;
					Date tonight_date = formatAll.parse(tonight_time_str);// 每晚的九点
					long left_time = (now.getTime() - tonight_date.getTime()) / 1000;// 每天到晚上9点之间的毫秒数
					
					if (left_time < 0) {
						going_start.setVisibility(View.VISIBLE);
						get_in.setVisibility(View.GONE); 
						over.setVisibility(View.GONE);
						//nine_status.setText("准时开启!");
						pm9_status_tips.setText("距离开始:");
						pm9_status_tips.setTextColor(0xff5a5a5a);
						//pm9_status_tips.setTextColor(0xF989C4);
						be_start.setVisibility(View.VISIBLE);
						int hour_ = (int) Math.abs(left_time / 3600);
						int minute_ = (int) Math.abs(left_time % 3600 / 60);
						int sec_ = (int) Math.abs(left_time % 3600 % 60);
	
						hour.setText("" + (hour_ < 10 ? "0" + hour_ : hour_));
						minute.setText(""
								+ (minute_ < 10 ? "0" + minute_ : minute_));
						second.setText("" + (sec_ < 10 ? "0" + sec_ : sec_));
						if ("00".equals(hour.getText().toString())
								&& "00".equals(minute.getText().toString())
								&& "01".equals(second.getText().toString())) {
							handler.postDelayed(new Runnable() {
	
								@Override
								public void run() {
									second.setText("00");
									//loginNineClockGroup();
									handler.postDelayed(runnable, 1000);
								}
							}, 1000);
							
						} 
					} else if ((left_time > 0 || left_time == 1)
							&& left_time < 3600) {// 21:00-22:00：活动进行中
						going_start.setVisibility(View.GONE);
						pm9_status_tips.setText("火热进行中");
						
						pm9_status_tips.setTextColor(0xffff73bc);
						over.setVisibility(View.GONE);
						be_start.setVisibility(View.GONE);
						get_in.setVisibility(View.VISIBLE);
						//nine_status.setText("火热进行中!");
					} else if (left_time >= 3600 && left_time < 3 * 3600) {// 活动结束
						//nine_status.setText("已结束");
						pm9_status_tips.setText("活动已结束");
						pm9_status_tips.setTextColor(0xffff73bc);//只接受8位的颜色值
						//pm9_status_tips.setTextColor(R.color.pm9_tip_color);
						going_start.setVisibility(View.GONE);
						get_in.setVisibility(View.GONE);
						over.setVisibility(View.VISIBLE);
						be_start.setVisibility(View.GONE);
						isEnd = true ;
						try {
							DBService.deleteAllMsg();
							MyApplication.deleteChatCache();
						} catch (Exception e) {
							
						}
					}else if(left_time >= 3*3600){
						isEnd = false ;
						
						try {
							DBService.deleteAllMsg();
							MyApplication.deleteChatCache();
						} catch (Exception e) {
							
						}
						handler.postDelayed(runnable, 1000);
					}
					handler.postDelayed(runnable, 1000);
				}
			} catch (Exception e) {
				
			}
		}

	};
	
	private ArrayList<Pm9HistoryDataEntity> pm9DataEntity = new ArrayList<Pm9HistoryDataEntity>();
	private PullToRefreshListView pullToRefreshListView;
	private ListView realListView;
	private SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
	private int pageIndex=1;
	private Pm9DataListAdapter mAdapter;
	
	private ImageView go_top ;
	
	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//mQueue  = Volley.newRequestQueue(getActivity());  
		initUmeng();
		dialogUploadImage = NiftyDialogBuilder.getInstance(getActivity(),
				R.layout.dialog_login_layout);
		
		if(SystemTool.checkNet(getActivity())){
			String currentDate = "" ;
			if(isForbidden){
				currentDate = "2015-03-05" ;
			}else{
			    currentDate  = formatYMD.format(new Date());
			}
			new GetTopicsService(getActivity(), HttpTagConstantUtils.TOPIC, this).getTopicsInfo(currentDate);
		}else{
			AppToast.toastMsgCenter(getActivity(), R.string.ERROR_404).show();
		}
		
//		String Token = ACache.get(getActivity()).getAsString("Token");
//		Log.e(TAG, "Token = "+Token);
//		String uid = ACache.get(getActivity()).getAsString("uid");
//		Log.e(TAG, "uid = "+uid);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void loginNineClockGroup() {// 登录聊天室
		Message msg = new Message(); 
		msg.what = NinePointFrag.LOGIN_SUCCESS;
		myHandler.sendMessage(msg);

	}

	NiftyDialogBuilder dialogUploadImage;

	class MyHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			switch (msg.what) {
			case LOGIN_FAILED:
				AppToast.toastMsgCenter(getActivity(), "登录失败!").show();
				break;
			case LOGOUT_SUCCESS:
				loginNineClockGroup();
				break;
			case JOIN_GROUP:
				dialogDismiss();
				try {
					if(MyApplication.CURRENT_USER != null){
						if(!MsgReceiver.isSessionPaused()){
							startActivity(new Intent(getActivity(),LeanCloudChatActivity.class));
						}
					}else{
						AppToast.toastMsgCenter(getActivity(),"登录失败，请稍后重试!").show();
					}
						
				} catch (Exception e) {
					AppToast.toastMsgCenter(getActivity(), "连接异常,请稍后再试!").show();
				}
				break;
			case LOGOUT_FAIL:
				if(null!= dialogUploadImage && dialogUploadImage.isShowing()){
					dialogUploadImage.dismiss();
				}
				AppToast.toastMsgCenter(getActivity(), "网络异常，登录失败!");
				break;
			case LOGIN_SUCCESS:
				/*if(isAdded()){
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							//加入群组
							Session session = SessionManager.getInstance(user.getObjectId());
					        Group group = session.getGroup(MyApplication.GROUP_ID);
					        Log.e(TAG, "group_id = "+group.getGroupId());
					        group.join();
						}
					});
				}*/
				//AppToast.toastMsgCenter(getActivity(), "活动维护中，敬请期待!").show();
				break;

			default:
				break;
			}
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view  ;
		
		if(isForbidden){
			view = inflater.inflate(R.layout.nine_point_main_fragment_forbidden,container, false);
		}else{
			//view = inflater.inflate(R.layout.nine_point_main_fragment,container, false);
			view = inflater.inflate(R.layout.nine_point_main_fragment_v2,container, false);
		}
		
		view.findViewById(R.id.backBtn).setOnClickListener(this);
		view.findViewById(R.id.popupShareBtn).setOnClickListener(this);
		go_top = (ImageView) view.findViewById(R.id.go_top);
		go_top.setVisibility(View.GONE);
		pullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.pm9DataListView);
		
		realListView = pullToRefreshListView.getRefreshableView();
				
		mAdapter=new Pm9DataListAdapter(pm9DataEntity, getActivity());
		realListView.setAdapter(mAdapter);
		
		pullToRefreshListView.setMode(Mode.PULL_FROM_END);
		pullToRefreshListView
				.setOnRefreshListener(new OnRefreshListener2<ListView>() {
					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						String label = format.format(new Date());
						refreshView.getLoadingLayoutProxy()
								.setLastUpdatedLabel("最后更新时间:" + label);
						pageIndex=1;
						getData();
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						pageIndex++;
						getData();
					}
				});
		
		addPm9HeaderView();	
		go_top.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				realListView.setSelection(0);
				//go_top.setVisibility(View.GONE);
			}
		});
		getData();
		
		realListView.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if(scrollState == OnScrollListener.SCROLL_STATE_IDLE
						&& view.getLastVisiblePosition() == view.getCount() - 1){
					
				}
				
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount){
				//EasyLog.e("firstVisibleItem="+firstVisibleItem+",\ttotalItemCount="+visibleItemCount+",\ttotalItemCount"+totalItemCount);
				if(firstVisibleItem<2){
					go_top.setVisibility(View.GONE);
				}else{
					go_top.setVisibility(View.VISIBLE);
				}
				
			}
		});
		

//		Log.e(TAG, "UUID====" + UUIDGeneratorUtil.getUUID());
//		final String uuid = UUIDGeneratorUtil.getUUID();
		/*view.findViewById(R.id.nine_clock_scrollview)*/realListView.setOnTouchListener(new OnTouchListener() {
			@Override
		    public boolean onTouch(View v, MotionEvent event) {
		        switch (event.getAction()) {
		            case MotionEvent.ACTION_DOWN:
		                moveY = v.getScrollY();//event.getRawY();
		                break;
		            case MotionEvent.ACTION_MOVE:
		            	moveY = v.getScrollY() ;
		            	if(noticeTxtLayout.getVisibility() == View.VISIBLE){
		            		// 上推
		                    closeNotice();
		            	}
//		                if(moveY - event.getRawY() > 20){
//		                    // 上推
//		                    closeNotice();
//		                }
		                moveY = v.getScrollY(); //event.getRawY();
		                break;
		            default:
		                break;
		        }
		        return false;
		    }

		});
		return view;
	}
	
	protected void getData() {
		new GetPm9HistoryAsynTaskService(getActivity(), HttpTagConstantUtils.PM9_HISTORY, this).doDataList(pageIndex);
	}

	public void addPm9HeaderView(){
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.nine_point_main_head_v2, null);
			
		hour = (TextView) view.findViewById(R.id.hour);
		minute = (TextView) view.findViewById(R.id.minute);
		second = (TextView) view.findViewById(R.id.seconds);
		nine_clock_title = (TextView) view.findViewById(R.id.nine_clock_title);
		nine_clock_content = (TextView) view.findViewById(R.id.nine_clock_content);
		nine_status = (TextView) view.findViewById(R.id.nine_status);
		pm9_status_tips = (TextView) view.findViewById(R.id.pm9_status_tips);
		
		nine_clock_logo = (ImageView) view.findViewById(R.id.nine_clock_logo);

		be_start = view.findViewById(R.id.be_start);
		going_start = view.findViewById(R.id.going_start);
		over = view.findViewById(R.id.over);
		get_in = view.findViewById(R.id.get_in);
		
		if(!isForbidden){
			get_in.setOnClickListener(this);
		}
		
		going_start.setVisibility(View.VISIBLE);
		over.setVisibility(View.GONE);
		get_in.setVisibility(View.GONE);
		
		
		nine_clock_introduce = view.findViewById(R.id.nine_clock_introduce);
		nine_clock_introduce.setOnClickListener(this);
		
		try {
			Date now = new Date();
			String now_str = formatYMD.format(now);// 得到当前日期格式化后的字符串
			String tonight_time_str = now_str + timmer_str;
			Date tonight_date = formatAll.parse(tonight_time_str);// 每晚的九点
			long left_time = (now.getTime() - tonight_date.getTime()) / 1000;// 每天到晚上9点之间的毫秒数
			if ((left_time > 0 || left_time == 1)
					&& left_time < 3600) {// 21:00-22:00：活动进行中
				going_start.setVisibility(View.GONE);
				get_in.setVisibility(View.VISIBLE);
				over.setVisibility(View.GONE);
				pm9_status_tips.setText("火热进行中");
				//nine_status.setText("火热进行中!");
				
			}
		} catch (Exception e) {
			EasyLog.e(e.toString());
		}
		noticeTxtLayout = view.findViewById(R.id.noticeTxtLayout);
		// 中奖公告
		if(isForbidden){
			noticeTxtLayout.setVisibility(View.GONE);
		}
		
		noticeTxt = (TextView) view.findViewById(R.id.noticeTxt);
		noticeTxt.setText(formatNotice.format(new Date(new Date().getTime() - 24 * 3600*1000)) +getString(R.string.zhongjianghint));
		noticeTxt.setOnClickListener(this);
		
		view.findViewById(R.id.jiangbeiBtn).setOnClickListener(this);
		view.findViewById(R.id.noticeCloseBtn).setOnClickListener(this);
		
		realListView.addHeaderView(view);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onResume() {
		super.onResume();
		GroupMessageReceiver.addListener(this);
		isEnd = false ;
		handler.postDelayed(runnable, 1000);
		Log.e(TAG, "MyApplication.resume_index = "+MyApplication.resume_index);
		MyApplication.resume_index = 3 ;
		try {
			MobclickAgent.onPageStart(getActivityName()); //统计页面
		    MobclickAgent.onResume(getActivity());
		    StatService.onResume(getActivity());
		    if(!isLoginSuccess || MsgReceiver.isSessionPaused()){
			    String Uid = ACache.get(getActivity()).getAsString("uid");
				String loginName = "" ;
				if(Uid != null && !"".equals(Uid)){
					loginName = Uid ;
				}else{
					loginName = MyApplication.DEVICE_ID ;
				}
				AVUser.logInInBackground(loginName, MyApplication.PWD, new LogInCallback() {
				    public void done(AVUser user, AVException e) {
				        if (user != null) {
				            // 登录成功
				        	Log.e(TAG,"NinePointFrag  登录成功.........");
				        	NinePointFrag.this.user = user ;
				        	MyApplication.CURRENT_USER = user ;
				        	isLoginSuccess = true ;
				        	//登录成功后打开session
				        	MsgReceiver.openSession(user);
				        } else {
				        	Log.e(TAG,"登录失败 by reason " + e.toString());
				        }
				    }
				});
		    }
		} catch (Exception e) {

		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.nine_clock_introduce:
			Intent intent = new Intent(this.getActivity(),NineIntroduceActivity.class);
			startActivity(intent);
			break;
		case R.id.backBtn:
			break;
		case R.id.popupShareBtn:
			popupShare();
			break;
		case R.id.get_in:// 进入活动
			//AppToast.toastMsgCenter(getActivity(), "活动维护进行中，敬请期待!").show();
			//
			
			String Uid = ACache.get(getActivity()).getAsString("uid");
			String Token = ACache.get(getActivity()).getAsString("Token");
			Log.e(TAG, "Uid = "+Uid);
			Log.e(TAG, "Token = "+Token);
			if(Uid == null || Token == null || "".equals(Token) || "".equals(Uid)
					){
				AppToast.toastMsgCenter(getActivity(), R.string.please_login).show();
				Intent i = new Intent(getActivity(),LoginActivity.class);
				startActivity(i);
				return ;
			}
			
			if(!SystemTool.checkNet(getActivity())){
				AppToast.toastMsgCenter(getActivity(), getActivity().getResources().getString(R.string.no_network)).show();
				return ;
			}
			if( MyApplication.CURRENT_USER == null){
				AppToast.toastMsgCenter(getActivity(),"登录失败，请稍后重试!").show();
				return ;
			}
			
			//if(!MsgReceiver.isSessionPaused()){
				Log.e("get_in", "user " + user.getObjectId());
				dialogUploadImage = NiftyDialogBuilder.getInstance(getActivity(),
						R.layout.dialog_login_layout);
				final View text = LayoutInflater.from(getActivity()).inflate(
						R.layout.dialog_login_view, null);
				TextView t = (TextView) text.findViewById(R.id.loading_text);
				t.setText("登录活动中....");
				dialogUploadImage.withTitle(null).withMessage(null)
						.withEffect(Effectstype.Fadein).withDuration(100)
						.isCancelableOnTouchOutside(false)
						.setCustomView(text, getActivity()).show();
				
				Message msg = new Message() ;
				msg.what = JOIN_GROUP ;
				myHandler.sendMessageDelayed(msg, 1000);
				
			//}
			
			
			
			break;
		case R.id.noticeTxt:
			Token = ACache.get(getActivity()).getAsString("Token");
			String uid = ACache.get(getActivity()).getAsString("uid");
			uid = "".equals(uid) || null == uid?"0":uid ;
			Log.e(TAG, "Token = "+Token);
			if (Integer.valueOf(uid) == 0 ) {
				AppToast.toastMsgCenter(getActivity(), R.string.please_login).show();
				Intent i = new Intent(getActivity(),LoginActivity.class);
				startActivity(i);
				return;
			}else{
				popupWindow(true);// 弹出中奖名单
			}
			
			
			break;
		case R.id.noticeCloseBtn:
			closeNotice();
			break;
		case R.id.jiangbeiBtn://
			String Token_ = ACache.get(getActivity()).getAsString("Token");
			
			if (StringUtil.isBlank(Token_)) {
				AppToast.toastMsgCenter(getActivity(), R.string.please_login).show();
				Intent i = new Intent(getActivity(),LoginActivity.class);
				startActivity(i);
				return;
			}else{
				popupWindow(true);// 弹出中奖名单
			}
			//popupWindow(false);
			break;
		}
	}

	private void popupWindow(boolean isAwarded) {
		
		String uid = ACache.get(getActivity()).getAsString("uid");
		Log.e(TAG, "uid = "+uid);
		uid = ("".equals(uid) || null == uid)?"0":uid ;
		boolean isAward = false;
		Integer current_uid = 0;
		current_uid = (Integer) (uid == null ? 0 : Integer.valueOf(uid));
		StringBuffer buffer = new StringBuffer();
		Map<Integer,String> map = new HashMap<Integer,String>();
		Log.e(TAG,"prize_users = "+prize_users);
		for (UserEntity entity : prize_users) {
			map.put(entity.getU_id(), entity.getNiakname());
		}
		for (Integer key : map.keySet()) {
			
			buffer.append(map.get(key) + ";");
			Log.e(TAG,"current_uid = "+current_uid);
			Log.e(TAG,"key = "+key);
			if (current_uid == key || key.equals(current_uid)) {
				isAward = true;
			}
		}
		Log.e(TAG,"isAward = "+isAward);
		
		if (isAward) {
			// 如果已经登录，并且中奖弹出中奖框
			if (inListOfAwardedPopupWindow == null) {
				inListOfAwardedPopupWindow = new InListOfAwardedPopupWindow();
			}
			inListOfAwardedPopupWindow.show(getActivity(), buffer.toString());
		} else {
			// 弹出未中奖
			if (noListOfAwardedPopupWindow == null) {
				noListOfAwardedPopupWindow = new NoListOfAwardedPopupWindow();
			}
			noListOfAwardedPopupWindow.show(getActivity(), buffer.toString());
		}

	}

	// 关闭公告栏
	void closeNotice() {
		if (View.VISIBLE != noticeTxtLayout.getVisibility()) {
			return;
		}

		if (animRunning) {
			return;
		}

		animRunning = true;
		// 动画效果
		Animation animation = AnimationUtils.loadAnimation(getActivity(),
				R.anim.close_notes_scale_anim);
		noticeTxtLayout.startAnimation(animation);
		animation.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				noticeTxtLayout.setVisibility(View.GONE);
				animRunning = false;
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});
	}

	// 中奖名单
	class InListOfAwardedPopupWindow implements OnClickListener,HttpAysnResultInterface {

		private PopupWindow popupWindow;
		private Activity context;
		private View zhongJianGroup;
		private View addressGroup;
		private EditText Address;
		private ScrollView scrollView;
		private View activityView;

		public InListOfAwardedPopupWindow() {

		}

		public void show(final Activity context, String nameList) {
			this.context = context;
			this.activityView = context.getWindow().getDecorView()
					.findViewById(android.R.id.content);

			if (popupWindow == null) {

				Rect frame = new Rect();
				context.getWindow().getDecorView()
						.getWindowVisibleDisplayFrame(frame);
				int statusBarHeight = frame.top;
				int width = frame.width();
				int height = frame.height();

				LayoutInflater layoutInflater = LayoutInflater.from(context);
				View view = layoutInflater.inflate(
						R.layout.in_list_awarded_popup, null);

				initViews(view, nameList);

				popupWindow = new PopupWindow(view, width, height);
				popupWindow.setFocusable(true);
			} else {
				setViewVisibility(0);
			}

			// popupWindow.setAnimationStyle(R.style.DialogAnimation);
			popupWindow.showAtLocation(activityView, Gravity.BOTTOM, 0, 0);

		}

		public void dismiss() {
			if (popupWindow != null && popupWindow.isShowing()) {
				popupWindow.dismiss();
			}
		}

		public void destroy() {
			context = null;
			popupWindow = null;
		}

		private void initViews(View rootView, String nameList) {
			scrollView = (ScrollView) rootView.findViewById(R.id.scrollView);
			zhongJianGroup = rootView.findViewById(R.id.zhongJianGroup);
			addressGroup = rootView.findViewById(R.id.addressGroup);
			TextView nameListTitle = (TextView) rootView
					.findViewById(R.id.nameListTitle);
			TextView nameListTxt = (TextView) rootView
					.findViewById(R.id.nameListTxt);
			Address = (EditText) rootView.findViewById(R.id.shippingAddress);

			String date = DateUtil.formatDate(
					DateUtil.datePlusOneDay(new Date(), -1), "MM-dd");
			String notice = context.getString(
					R.string.night9oclock_notice_title, date);
			nameListTitle.setText(notice);
			nameListTxt.setText(nameList);
			// scrooView 中TextView 可滚动增加下列代码
			new GetUserAddressService(getActivity(),
					HttpTagConstantUtils.GET_ADDRESS, this).get_address(ACache.get(getActivity())
					.getAsString("Token"));
			nameListTxt
					.setMovementMethod(ScrollingMovementMethod.getInstance());
			nameListTxt.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					scrollView.requestDisallowInterceptTouchEvent(true);
					return false;
				}
			});

			rootView.findViewById(R.id.closeBtn).setOnClickListener(this);
			rootView.findViewById(R.id.openBtn).setOnClickListener(this);
			rootView.findViewById(R.id.submitBtn).setOnClickListener(this);

			setViewVisibility(1);
		}

		// 0 显示名单，1 显示地址
		private void setViewVisibility(int flag) {
			// 默认显示中奖名单，如果已经中奖，登录后直接跳到填写地址请加上参数判断一下
			if (flag == 0) {
				zhongJianGroup.setVisibility(View.VISIBLE);
				addressGroup.setVisibility(View.GONE);
			} else {
				zhongJianGroup.setVisibility(View.GONE);
				addressGroup.setVisibility(View.VISIBLE);
			}
			// IDLApplication.getInstance().Flag = 0;
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.openBtn:
				zhongJianGroup.setVisibility(View.GONE);
				addressGroup.setVisibility(View.VISIBLE);
				break;
			case R.id.closeBtn:
				dismiss();
				break;
			case R.id.submitBtn:
				submitAddress();
				break;
			}
		}

		void openAddress() {
			zhongJianGroup.setVisibility(View.GONE);
			addressGroup.setVisibility(View.VISIBLE);
		}

		void submitAddress() {
			// 异步提交地址，成功后关闭弹出框
			/*String shippingAddress = Address.getText().toString();

			if (StringUtil.isBlank(shippingAddress)) {
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setMessage(context
						.getString(R.string.night9oclock_address_empty_msg));
				builder.setNegativeButton("确定", null);
				builder.show();
				return;
			}*/
			
			// 异步提交地址，成功后关闭弹出框
			String shippingAddress = Address.getText().toString();
			if (StringUtil.isBlank(shippingAddress)) {
				AppToast.toastMsg(getActivity(), "~亲，您还什么都没有填哦")
						.show();
				return;
			}
			if (StringUtil.isBlank(shippingAddress)) {
				AppToast.toastMsg(getActivity(), "~亲，您还什么都没有填哦")
						.show();
				return;
			} else {
				new PutUserAddressTaskService(getActivity(),
						HttpTagConstantUtils.PUT_ADDRESS, this)
						.put_user_address(ACache.get(getActivity()).getAsString("Token"),
								shippingAddress);
			}
		}
		
		@Override
		public void dataCallBack(Object tag, int statusCode, Object result) {
			try {
				if (dialogUploadImage != null) {
					dialogUploadImage.dismiss();
				}
				if(HttpTagConstantUtils.PUT_ADDRESS == (Integer)tag){
					if ((HttpStatus.SC_OK == statusCode || HttpStatus.SC_CREATED == statusCode) && (Boolean)result) {
						dismiss();
						AppToast.toastMsgCenter(getActivity(),getString(R.string.put_address_success)).show();
					} else {
						AppToast.toastMsgCenter(getActivity(),getString(R.string.put_address_fail)).show();
					}
				}else if(HttpTagConstantUtils.GET_ADDRESS == (Integer)tag){
					Address.setText(result.toString()+"");
					Address.setSelection((result.toString()+"").length());
				}
			} catch (Exception e) {
				Log.e(TAG, "更新用户收获地址异常......."+e.toString());
			} finally {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						if (dialogUploadImage != null) {
							dialogUploadImage.dismiss();
						}
					}
				}, 1000);
			}

		}

	}

	class NoListOfAwardedPopupWindow implements View.OnClickListener {

		private PopupWindow popupWindow;
		private Activity context;
		private View zhongJianGroup;
		private ScrollView scrollView;
		private View activityView;
		private TextView noHappy;
		private Button openBtn;

		public NoListOfAwardedPopupWindow() {

		}

		public void show(final Activity context, String nameList) {
			this.context = context;
			this.activityView = context.getWindow().getDecorView()
					.findViewById(android.R.id.content);
			if (popupWindow == null) {

				Rect frame = new Rect();
				context.getWindow().getDecorView()
						.getWindowVisibleDisplayFrame(frame);
				int statusBarHeight = frame.top;
				int width = frame.width();
				int height = frame.height();

				LayoutInflater layoutInflater = LayoutInflater.from(context);
				View view = layoutInflater.inflate(
						R.layout.no_list_awarded_popup, null);

				initViews(view, nameList);

				popupWindow = new PopupWindow(view, width, height);
				popupWindow.setFocusable(true);

			} else {

				setViewVisibility();
			}

			// popupWindow.setAnimationStyle(R.style.DialogAnimation);
			popupWindow.showAtLocation(activityView, Gravity.BOTTOM, 0, 0);

		}

		public void dismiss() {
			if (popupWindow != null && popupWindow.isShowing()) {
				popupWindow.dismiss();
			}
		}

		public void destroy() {
			dismiss();
			context = null;
			popupWindow = null;
		}

		private void initViews(View rootView, String nameList) {
			scrollView = (ScrollView) rootView.findViewById(R.id.scrollView);
			zhongJianGroup = rootView.findViewById(R.id.zhongJianGroup);
			TextView nameListTitle = (TextView) rootView
					.findViewById(R.id.nameListTitle);
			TextView nameListTxt = (TextView) rootView
					.findViewById(R.id.nameListTxt);
			noHappy = (TextView) rootView.findViewById(R.id.noHappy);
			noHappy.setVisibility(View.GONE);
			String date = DateUtil.formatDate(
					DateUtil.datePlusOneDay(new Date(), -1), "MM-dd");
			String notice = context.getString(
					R.string.night9oclock_notice_title, date);
			nameListTitle.setText(notice);
			nameListTxt.setText(nameList);
			// scrooView 中TextView 可滚动增加下列代码
			nameListTxt
					.setMovementMethod(ScrollingMovementMethod.getInstance());
			nameListTxt.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					scrollView.requestDisallowInterceptTouchEvent(true);
					return false;
				}
			});

			openBtn = (Button) rootView.findViewById(R.id.openBtn);
			openBtn.setOnClickListener(this);
			openBtn.setVisibility(View.GONE);
			rootView.findViewById(R.id.closeBtn).setOnClickListener(this);
			setViewVisibility();
		}

		private void setViewVisibility() {
			// 默认显示中奖名单，如果已经中奖，登录后直接跳到填写地址请加上参数判断一下
			zhongJianGroup.setVisibility(View.VISIBLE);
			// 未登录显示按钮，已经登录显示一句话
			openBtn.setVisibility(View.VISIBLE);
			noHappy.setVisibility(View.GONE);
			noHappy.setVisibility(View.VISIBLE);
			openBtn.setVisibility(View.GONE);
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.openBtn:
				break;
			case R.id.closeBtn:
				dismiss();
				break;

			}
		}
	}

	// 打开公告栏
	void openNotice() {
		Animation animation = AnimationUtils.loadAnimation(getActivity(),
				R.anim.open_notes_scale_anim);
		noticeTxtLayout.startAnimation(animation);
	}

	void popupShare() {
		try {
			logout(SHARE_MEDIA.SINA);
			logout(SHARE_MEDIA.QZONE);
			logout(SHARE_MEDIA.WEIXIN);
			
			if(entity != null){
			// 使用友盟分享
			Bitmap logo = BitmapFactory.decodeResource(getResources(),
					R.drawable.app_icon);
			UMShareUtil
					.getInstance()
					.share(this.getActivity(),
							entity.getTitle(),
							entity.getTitle(),
							"http://napi.skinrun.cn/v1/pm9Details/"+entity.getId(),
							logo,
							//"http://sharepage.skinrun.me/nightnine/share/448a0a9008f64ba0b7fe13a356d0b4b2?from=singlemessage&isappinstalled=1",
							HttpClientUtils.IMAGE_URL+entity.getImg_url(),
							
							new ShareAction() {
								
								@Override
								public void onSuccess() {
									try {
										String token = ACache.get(getActivity()).getAsString("Token");
										token = (token == null ?"":token) ;
										new PostUserShareService(getActivity(),
												HttpTagConstantUtils.SHARE_JIFEN,
												NinePointFrag.this).post_share(token);
									} catch (Exception e) {
										// TODO: handle exception
									}
								}
							});
			}
		} catch (Exception e) {
			
		}
		
	}
	
	
	 /**
     * 注销本次登录</br>
     */
    public void logout(final SHARE_MEDIA platform) {
    	UMShareUtil.getInstance().getUmsocialService().deleteOauth(getActivity(), platform, new SocializeClientListener() {

            @Override
            public void onStart() {

            }

            @Override
            public void onComplete(int status, SocializeEntity entity) {
                
            }
        });
    }


	//晚九点主题
	TopicsEntity entity = new TopicsEntity();
	private ArrayList<UserEntity> prize_users = new ArrayList<UserEntity>();
	@Override
	public void dataCallBack(Object tag, int statusCode, Object result) {
		try {
			if ((Integer) tag == HttpTagConstantUtils.TOPIC) {//晚九点活动详情
				if (statusCode == HttpStatus.SC_CREATED || 
						statusCode == HttpStatus.SC_OK) {
					entity = (TopicsEntity)result ;
					nine_clock_content.setText(""+entity.getContent());
					prize_users = entity.getPrize_user();
					Log.e(TAG, "img_url = "+entity.getImg_url());
					nine_clock_title.setText(""+entity.getTitle());
					

					UserService.imageLoader.displayImage(""
							+ HttpClientUtils.IMAGE_URL + entity.getImg_url(),
							nine_clock_logo, PhotoUtils.articleImageOptions);
				} else {
					Log.e(TAG, "晚九情异常.......");
					if(isAdded()){
						//AppToast.toastMsgCenter(this.getActivity(),R.string.ERROR_404).show();
					}
				}
				
			}else if((Integer) tag == HttpTagConstantUtils.PM9_HISTORY){//晚九点历史记录
				if(result != null){
					ArrayList<Pm9HistoryDataEntity> pm9DataEntity = (ArrayList<Pm9HistoryDataEntity>) result ;
					if(pm9DataEntity.size() > 0){
						this.pm9DataEntity.addAll(pm9DataEntity);
						mAdapter.notifyDataSetChanged();
					}
					
				}
				
				/*if(pageIndex > 1){
					go_top.setVisibility(View.VISIBLE);
				}else{
					go_top.setVisibility(View.GONE);
				}*/
			}
		} catch (Exception e) {

		} catch(OutOfMemoryError error){
			
		}finally {
			if(pullToRefreshListView != null){
				pullToRefreshListView.onRefreshComplete();
			}
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					//pullToRefreshListView.onRefreshComplete();
				}
			}, 1000);
		}

	}

	public boolean isWifiConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mWiFiNetworkInfo = mConnectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (mWiFiNetworkInfo != null) {
				return mWiFiNetworkInfo.isAvailable();
			}
		}
		return false;
	}
	

	public boolean isMobileConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mMobileNetworkInfo = mConnectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			if (mMobileNetworkInfo != null) {
				return mMobileNetworkInfo.isAvailable();
			}
		}
		return false;
	}
	
	public String getActivityName(){
		return "晚九点" ;
	}
	
	//友盟统计
	private void initUmeng(){
        MobclickAgent.setDebugMode(false);
        MobclickAgent.openActivityDurationTrack(false);
        MobclickAgent.updateOnlineConfig(getActivity());
    }
    
    /**
     * 自定义事件统计
     *
     * @param eventCode
     * @param eventName
     */
    public void logEvent(String eventCode, String eventName) {
        MobclickAgent.onEvent(getActivity(), eventCode);
    }


    @Override
    public void onPause() {
        super.onPause();
        dialogDismiss();
        GroupMessageReceiver.removeListener(this);
        //GroupMessageReceiver.removeMsgRecevierListener(this);
        //页面结束
        if (!StringUtil.isEmpty(getActivityName())) {
            MobclickAgent.onPageEnd(getActivityName()); // 保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息
            MobclickAgent.onPause(getActivity());
            StatService.onPause(getActivity());
        }
       
    }
    
    @Override
    public void onDestroy() {
		super.onDestroy();
		MyApplication.resume_index = 3 ;
    }
    public void dialogDismiss(){
    	if(dialogUploadImage != null && dialogUploadImage.isShowing()){
			dialogUploadImage.dismiss();
		}
    }

	@Override
	public void onJoined(Group group) {
		Log.e(TAG, "join group succeed--------"); 
		dialogDismiss();
	}

	@Override
	public void onMemberUpdate(Group group) {
		
	}

	@Override
	public void onQuit(Group group) {
		Log.e(TAG, "quit group--------"); 
	}

	@Override
	public void onError(Group group) {
		dialogDismiss();
		AppToast.toastMsgCenter(getActivity(), "连接异常,请稍后再试...").show();
	}
    

}

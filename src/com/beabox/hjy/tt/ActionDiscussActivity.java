package com.beabox.hjy.tt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.app.base.entity.ActionCommentEntity;
import com.app.base.entity.ActionDiscussEntity;
import com.app.base.entity.ActionReplayEntity;
import com.app.base.entity.PraiseJoinEntity;
import com.app.base.entity.UserEntity;
import com.app.base.init.ACache;
import com.app.base.init.BaseActivity;
import com.app.base.init.MyApplication;
import com.app.service.GetActionCommentListAsynTaskService;
import com.app.service.GetActionDetailAsynTaskService;
import com.app.service.GetUserAddressService;
import com.app.service.PostActionCommentAsynTaskService;
import com.app.service.PostArticleCommentAsynTaskService;
import com.app.service.PostCommentPraiseAsynTaskService;
import com.app.service.PostPraiseAsynTaskService;
import com.app.service.PostUserShareService;
import com.app.service.PutUserAddressTaskService;
import com.avos.avoscloud.LogUtil.log;
import com.avoscloud.chat.base.ChatMsgAdapter;
import com.avoscloud.chat.base.ExpandGridView;
import com.avoscloud.chat.base.ExpressionAdapter;
import com.avoscloud.chat.base.ExpressionGridGuideAdapter;
import com.avoscloud.chat.base.ExpressionPagerAdapter;
import com.avoscloud.chat.base.SmileUtils;
import com.avoscloud.chat.service.UserService;
import com.avoscloud.chat.util.PathUtils;
import com.avoscloud.chat.util.PhotoUtils;
import com.avoscloud.chat.util.Utils;
import com.base.app.utils.DateUtil;
import com.base.app.utils.DeviceUtil;
import com.base.app.utils.EasyLog;
import com.base.app.utils.FileUtil;
import com.base.app.utils.StringUtil;
import com.base.app.utils.UMShareUtil;
import com.base.app.utils.UMShareUtil.ShareAction;
import com.base.dialog.lib.Effectstype;
import com.base.dialog.lib.NiftyDialogBuilder;
import com.base.facedemo.FaceRelativeLayout;
import com.base.pulltorefresh.view.PullToRefreshBase;
import com.base.pulltorefresh.view.PullToRefreshBase.Mode;
import com.base.pulltorefresh.view.PullToRefreshBase.OnRefreshListener2;
import com.base.pulltorefresh.view.PullToRefreshListView;
import com.base.service.action.constant.HttpTagConstantUtils;
import com.base.service.impl.HttpAysnResultInterface;
import com.base.service.impl.HttpClientUtils;
import com.base.supertoasts.util.AppToast;
import com.idongler.widgets.CircleImageView;
import com.imagechooser.ui.SelectorImageMainActivity;
import com.skinrun.trunk.adapter.JoinUsersPicsDataListAdapter;
import com.umeng.message.PushAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.listener.SocializeListeners.SocializeClientListener;

/**
 * 活动讨论详情页面
 * @author zhupei
 *************************/
@SuppressWarnings("serial")
public class ActionDiscussActivity extends BaseActivity implements
		OnItemClickListener {
	private static final String TAG = "ActionDiscussActivity";
	private ArrayList<ActionCommentEntity> action_comment_list = new ArrayList<ActionCommentEntity>();
	private ArrayList<ActionReplayEntity> replay_lists = new ArrayList<ActionReplayEntity>();
	private ActionListAdapter adapter;
	private PullToRefreshListView pullToRefreshListView;
	
	Map<Integer,String> join_users = new HashMap<Integer,String>();
	
	
	private ImageView backBtn;
	private ListView realListView;
	private SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
	
	InputMethodManager imm;


	//发表评论属性
	private View mBtnSend;
	private ImageView btn_set_mode_keyboard,btn_camera;
	private EditText mEditTextContent;
	private StringBuffer activity_show_or_hiden = new StringBuffer("");
	
	private View ll_image_display ;
	private ImageView post_img , delete_img;
	private StringBuffer post_img_base64 = new StringBuffer("");
	private StringBuffer post_img_url = new StringBuffer("");
	
	NiftyDialogBuilder dialogUploadImage;

	private View ll_facechoose;// 表情区域

	int pageIndex = 1;

	private static long current_to_uid = -1;//
	private static long current_comment_id = -1;
	private static long comment_id = -1;
	
	private static int current_position = 0 ;
	
	SimpleDateFormat formatAll = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	SimpleDateFormat formatYMD = new SimpleDateFormat("yyyy-MM-dd ");
	SimpleDateFormat formatHMS = new SimpleDateFormat("HH:mm:ss");
	Calendar calendarInstatnce = Calendar.getInstance() ;
	
	View going_start ;
	
	TextView day;
	TextView hour;
	TextView minute;
	
	int status = -100 ;
	
	String end_time = "2015-04-10 00:00:00" ;
	String public_time = "2015-03-10 00:00:00" ;
	//TextView second;

	private Handler handler = new Handler();
	
	private InListOfAwardedPopupWindow inListOfAwardedPopupWindow;
	private NoListOfAwardedPopupWindow noListOfAwardedPopupWindow;
	
	//活动
	private ActionDiscussEntity result_entity;
	
	public static boolean isBegin = false ;
	
	
	private long activityId;
	
	Runnable runnable = new Runnable() {

		@Override
		public void run() {
			try {
				Date now = new Date();
				//String now_str = formatYMD.format(now);// 得到当前日期格式化后的字符串
				String tonight_time_str = end_time ; //formatAll.format(calendarInstatnce.getTime());//now_str + timmer_str;
				Date tonight_date = formatAll.parse(tonight_time_str);// 
				long left_time = (now.getTime() - tonight_date.getTime()) / 1000;// 
				EasyLog.e(tonight_time_str+","+left_time) ;
				if (left_time < 0) {
					going_start.setVisibility(View.VISIBLE);
					action_content.setVisibility(View.VISIBLE);
					//get_in.setVisibility(View.GONE); 
					//over.setVisibility(View.GONE);
					int day_ = (int) Math.abs(left_time / (3600 * 24)) ;
					int hour_ = (int) Math.abs(left_time % (3600*24) / 3600);
					int minute_ = (int) Math.abs(left_time % (3600*24) % 3600 / 60);
					//int sec_ = (int) Math.abs(left_time % 3600 % 60);

					day.setText("" + (day_ < 10 ? "0" + day_ : day_));
					hour.setText("" + (hour_ < 10 ? "0" + hour_ : hour_));
					minute.setText(""
							+ (minute_ < 10 ? "0" + minute_ : minute_));
					//second.setText("" + (sec_ < 10 ? "0" + sec_ : sec_));
					if ("00".equals(day.getText().toString())
							&& "00".equals(hour.getText().toString())
							&& "00".equals(minute.getText().toString())
							) {
						handler.postDelayed(new Runnable() {

							@Override
							public void run() {
								//活动结束

							}
						}, 1000);

					} else {
						handler.postDelayed(this, 1000);
					}
				} else {
					going_start.setVisibility(View.GONE);
					action_content.setVisibility(View.VISIBLE);
					if(status == 101){
						reward_btn.setVisibility(View.GONE);
					}else{
						reward_btn.setVisibility(View.VISIBLE);
					}
					
					reward_layout.setVisibility(View.VISIBLE);
					
					StringBuffer buffer = new StringBuffer();
					for(UserEntity entity : prize_users){
						buffer.append(entity.getNiakname()+";");
					}
					user_reward_list.setText(buffer.toString());
				}

			} catch (Exception e) {
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		MyApplication.getInstance().addActivity(this);
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		PushAgent.getInstance(this).onAppStart();
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.action_main_list);
		
		activityId=getIntent().getLongExtra("activityId", -1);
		status = getIntent().getIntExtra("status", -100);
		
		if(activityId==-1){
			Bundle bun = getIntent().getExtras();
			log.e(TAG, "========BUNDLE:"+bun.toString());
			try{
				activityId=Long.parseLong(bun.getString("id"));
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
		
		log.e(TAG, "===========activityId:"+activityId);
		
		
		current_position = 0 ;
		
		setupView();
		addListener();
	}

	@Override
	public void setupView() {
		activity_show_or_hiden = activity_show_or_hiden.append(getResources().getString(R.string.activity_show_or_hiden));
		
		pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.action_list_view);
		mBtnSend =  findViewById(R.id.btn_send);
		btn_camera = (ImageView) findViewById(R.id.btn_camera);
		mBtnSend.setOnClickListener(this);
		btn_camera.setOnClickListener(this);
		
		if(!"on".equals(activity_show_or_hiden.toString())){
			//btn_camera.setVisibility(View.GONE);
		}
		
		mEditTextContent = (EditText) findViewById(R.id.et_sendmessage);
		backBtn = (ImageView) findViewById(R.id.backBtn);
		
		btn_set_mode_keyboard = (ImageView) findViewById(R.id.btn_set_mode_keyboard);
		ll_facechoose = findViewById(R.id.ll_facechoose);
		
		ll_image_display = findViewById(R.id.ll_image_display);//图片预览布局
		post_img = (ImageView) findViewById(R.id.post_img);//选择的图片
		delete_img = (ImageView) findViewById(R.id.delete_img);//删除图片
		delete_img.setOnClickListener(this);
		
		//删除图片
		

		//表情
		//expressionViewpager = (ViewPager) findViewById(R.id.vp_contains);
		//infoshow_gridview = (GridView) findViewById(R.id.infoshow_gridview);
		//去掉默认背景
		//infoshow_gridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
		//initExpression();
		
		mEditTextContent.setOnClickListener(this);
		btn_set_mode_keyboard.setOnClickListener(this);
		findViewById(R.id.btn_face).setOnClickListener(this);
		findViewById(R.id.popupShareBtn).setOnClickListener(this);//分享
		
		// imm.hideSoftInputFromWindow(comment_text.getWindowToken(), 0);//强制隐藏
		realListView = pullToRefreshListView.getRefreshableView();
		
		actionListViewAddHeader();
		
		pullToRefreshListView.setMode(Mode.BOTH);// 下拉加载更多
		pullToRefreshListView
				.setOnRefreshListener(new OnRefreshListener2<ListView>() {
					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						String label = format.format(new Date());
						refreshView.getLoadingLayoutProxy()
								.setLastUpdatedLabel("最后更新时间:" + label);
						pageIndex = 1 ;
						if(action_comment_list != null){
							action_comment_list.clear();
						}
						action_comment_list = new ArrayList<ActionCommentEntity>();
						getData();
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						pageIndex++;
						getCommentList();
					}

				});
		
		log.e(TAG, "=======setupView");

		Runnable task = new Runnable() {

			@Override
			public void run() {
				getData();
			}
		};

		new Handler().post(task);
		findViewById(R.id.root_action_container).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				imm.hideSoftInputFromWindow(mEditTextContent.getWindowToken(), 0); 
				resetReplay();
			}
		});
		
	}
	
	
	private List<String> reslist;
	String class_for_name = "com.avoscloud.chat.base.SmileUtils";
	private ViewPager expressionViewpager;
	private LinearLayout layout_point;
	
	/** 游标点集合 */
	private ArrayList<ImageView> pointViews;
	/** 表情页界面集合 */
	private ArrayList<View> pageViews = new ArrayList<View>(2);

	private GridView infoshow_gridview;
	private ExpressionGridGuideAdapter gridviewAdapter;

	private View buttonSetModeKeyboard;

	private int currentIndex_ = 0;
	private boolean m = true;
	private int gridview_horSpac = 26;//
	private int gridview_xpadding = 10;
	private int gridview_ypadding = 5;
	private int emoji_size = 6;
	/**
	 * 初始化表情
	 ******************************/
	private void initExpression() {
		// 表情list
		reslist = getExpressionRes(117);
		// 初始化表情viewpager
		List<View> views = new ArrayList<View>();
		View gv1 = getGridChildView(1);
		View gv2 = getGridChildView(2);
		View gv3 = getGridChildView(3);
		View gv4 = getGridChildView(4);
		View gv5 = getGridChildView(5);
		View gv6 = getGridChildView(6);
		views.add(gv1);
		views.add(gv2);
		views.add(gv3);
		views.add(gv4);
		views.add(gv5);
		views.add(gv6);

		// expressionViewpager.setCurrentItem(1);
		expressionViewpager.setAdapter(new ExpressionPagerAdapter(views));
		// Init_Point();//
		expressionViewpager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				currentIndex_ = position;
				gridviewAdapter.setCurrentIndex(currentIndex_);
				gridviewAdapter.notifyDataSetChanged();
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
		
		
		gridviewAdapter = new ExpressionGridGuideAdapter(
				this.getApplicationContext(), emoji_size);
		infoshow_gridview.setAdapter(gridviewAdapter);
		
		Bitmap bmp = BitmapFactory.decodeResource(getResources(),
				R.drawable.d1);
		int width = bmp.getWidth();
		int height = bmp.getHeight();
		infoshow_gridview.setColumnWidth(width);
		infoshow_gridview.setHorizontalSpacing(gridview_horSpac);//
		infoshow_gridview.setNumColumns(emoji_size);//
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) infoshow_gridview
				.getLayoutParams();
		lp.width = width * emoji_size + gridview_horSpac * (emoji_size - 1)
				+ (gridview_xpadding << 1);
		lp.height = height + (gridview_ypadding << 1);
		infoshow_gridview.setLayoutParams(lp);
		infoshow_gridview.setPadding(gridview_xpadding, gridview_ypadding,
				gridview_xpadding, gridview_ypadding);
		
		
		gridviewAdapter.notifyDataSetChanged();

		// 监听文字框
		mEditTextContent.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}
	
	/**
	 * 获取表情的gridview的子view
	 * 
	 * @param i
	 * @return
	 */
	private View getGridChildView(int i) {
		View view = View.inflate(this, R.layout.pm9_expression_gridview, null);
		ExpandGridView gv = (ExpandGridView) view.findViewById(R.id.gridview);
		//gv.setSelector(new ColorDrawable(Color.TRANSPARENT));
		List<String> list = new ArrayList<String>();
		if (i == 1) {
			List<String> list1 = reslist.subList(0, 23);
			list.addAll(list1);
		} else if (i == 2) {
			list.addAll(reslist.subList(23, 46));
		} else if (i == 3) {
			list.addAll(reslist.subList(46, 69));
		} else if (i == 4) {
			list.addAll(reslist.subList(69, 92));
		} else if (i == 5) {
			list.addAll(reslist.subList(92, 115));
		} else if (i == 6) {
			list.addAll(reslist.subList(115, reslist.size()));
		}
		list.add("delete_expression");
		final ExpressionAdapter expressionAdapter = new ExpressionAdapter(this,
				1, list);
		gv.setAdapter(expressionAdapter);
		gv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String filename = expressionAdapter.getItem(position);
				try {
					EasyLog.e("选择表情");
					// 文字输入框可见时，才可输入表情
					// 按住说话可见，不让输入表情
					if (btn_set_mode_keyboard.getVisibility() != View.VISIBLE) {
						if (filename != "delete_expression") { // 不是删除键，显示表情
							// 这里用的反射，所以混淆的时候不要混淆SmileUtils这个类
							Class clz = Class.forName(class_for_name);
							Field field = clz.getField(filename);
							mEditTextContent.append(SmileUtils.getSmiledText(
									ActionDiscussActivity.this,
									(String) field.get(null)));
						} else { // 删除文字或者表情
							if (!TextUtils.isEmpty(mEditTextContent.getText())) {

								int selectionStart = mEditTextContent
										.getSelectionStart();// 获取光标的位置
								if (selectionStart > 0) {
									String body = mEditTextContent.getText()
											.toString();
									String tempStr = body.substring(0,
											selectionStart);
									int i = tempStr.lastIndexOf("[");// 获取最后一个表情的位置
									if (i != -1) {
										CharSequence cs = tempStr.substring(i,
												selectionStart);
										if (SmileUtils.containsKey(cs
												.toString()))
											mEditTextContent.getEditableText()
													.delete(i, selectionStart);
										else
											mEditTextContent.getEditableText()
													.delete(selectionStart - 1,
															selectionStart);
									} else {
										mEditTextContent.getEditableText()
												.delete(selectionStart - 1,
														selectionStart);
									}
								}
							}

						}
					}
				} catch (Exception e) {
					EasyLog.e(e.toString());
				}

			}
		});
		return view;
	}
	
	public List<String> getExpressionRes(int getSum) {
		List<String> reslist = new ArrayList<String>();
		for (int x = 1; x <= getSum; x++) {
			String filename = "emoji_" + x;
			reslist.add(filename);
		}
		return reslist;

	}

	/**
	 * 添加ListView头部信息
	 **********************************/

	private TextView action_support_count,user_reward_list;
	private WebView action_content ;
	private TextView title,people ;
	private TextView  awarded_hint;
	private View write_address,user_reward_list_layout ;
	private View action_support ;
	private View view ;
	private ImageView action_img,home_support_1,no_support_btn ;
	private View reward_btn ;
	private View reward_layout ;
	private GridView join_user_pic ;
	private JoinUsersPicsDataListAdapter joinUsersPicsDataListAdapter ;
	/*private int[] pics = new int[] { R.id.pic_1, R.id.pic_2, R.id.pic_3,
			R.id.pic_4, R.id.pic_5, R.id.pic_6, R.id.pic_7, R.id.pic_8,
			R.id.pic_9, R.id.pic_10 };*/	
	
	public void actionListViewAddHeader() {
		view = LayoutInflater.from(this).inflate(
				R.layout.action_main_list_view_head, null);
		awarded_hint = (TextView) view.findViewById(R.id.awarded_hint);
		write_address =  view.findViewById(R.id.write_address);
		user_reward_list_layout =  view.findViewById(R.id.user_reward_list_layout);
		
		title = (TextView) view.findViewById(R.id.title);
		people = (TextView) view.findViewById(R.id.people);
		user_reward_list = (TextView) view.findViewById(R.id.user_reward_list);
		going_start =view.findViewById(R.id.going_start);
		reward_btn =view.findViewById(R.id.reward_btn);
		home_support_1 = (ImageView) view.findViewById(R.id.home_support_1);
		no_support_btn = (ImageView) view.findViewById(R.id.no_support_btn);
		join_user_pic = (GridView) view.findViewById(R.id.join_user_pic);
		join_user_pic.setSelector(new ColorDrawable(Color.TRANSPARENT));
		reward_layout =view.findViewById(R.id.reward_layout);
		going_start.setVisibility(View.GONE);
		reward_btn.setVisibility(View.GONE);
		reward_btn.setOnClickListener(this);
		reward_layout.setVisibility(View.GONE);
		day = (TextView) view.findViewById(R.id.day);
		action_img = (ImageView) view.findViewById(R.id.action_img);
		hour = (TextView) view.findViewById(R.id.hour);
		minute = (TextView) view.findViewById(R.id.minute);
		//second = (TextView) view.findViewById(R.id.seconds);
		
		action_content = (WebView) view.findViewById(R.id.action_content);
		action_content.setVisibility(View.GONE);
		action_content.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				return false;
			}
		});
		
		if(status == 101){
			awarded_hint.setText("活动已结束，等待公布获奖名单");
			user_reward_list.setVisibility(View.GONE);
			write_address.setVisibility(View.GONE);
			reward_btn.setVisibility(View.GONE);
			
		}
		
		realListView.addHeaderView(view);
	}

	protected void getData() {
		try {
			long action_id = activityId;
			String token = aCache.getAsString("Token");
			// 获取活动评论列表
			new GetActionDetailAsynTaskService(getApplicationContext(),
					HttpTagConstantUtils.ACTION_DETAIL, this)
					.doActionAndCommentsList(action_id, pageIndex,token);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void getCommentList() {
		try {
			
			long action_id = activityId;
			// 获取活动评论列表
			new GetActionCommentListAsynTaskService(getApplicationContext(),HttpTagConstantUtils.ACTION_COMMENT_LIST, this)
			.doCommentsList(action_id, pageIndex);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private ArrayList<UserEntity> prize_users = new ArrayList<UserEntity>();
	@Override
	public void dataCallBack(Object tag, int statusCode, Object result) {
		
		try {
			if (dialogUploadImage != null) {
				dialogUploadImage.dismiss();
			}

			if ((Integer) tag == HttpTagConstantUtils.ACTION_DETAIL) {//活动详情
				//pullToRefreshListView.onRefreshComplete();
				if (isSuccess(statusCode)) {
					result_entity = (ActionDiscussEntity) result;
					prize_users = result_entity.getPrize_user();
					int praise_status = result_entity.getPraise_status() ;
					if(praise_status == 1){
						home_support_1.setVisibility(View.VISIBLE);
						no_support_btn.setVisibility(View.GONE);
					}else{
						home_support_1.setVisibility(View.GONE);
						no_support_btn.setVisibility(View.VISIBLE);
					}
					
					end_time = result_entity.getEnd_time();
					
					
					action_support = view.findViewById(R.id.action_support);
					action_support_count = (TextView) view
							.findViewById(R.id.action_support_count);// 点赞数目
					
					title.setText("" +result_entity.getTitle());
					//action_content.setText(""+action.getContent());
					action_support.setOnClickListener(this);
					action_support_count.setText(""+result_entity.getPraise_count());
					
					
					join_users = result_entity.getJoin_users();
					ArrayList<PraiseJoinEntity> list = new ArrayList<PraiseJoinEntity>();
					for (Integer key : join_users.keySet()) {
						PraiseJoinEntity p=new PraiseJoinEntity();
						p.setUid(key+"");
						list.add(p);
					}
					people.setText(""+result_entity.getJoin_users().size()+"人参与");
					
					joinUsersPicsDataListAdapter = new JoinUsersPicsDataListAdapter(list,this.getApplicationContext());
					
					join_user_pic.setAdapter(joinUsersPicsDataListAdapter);
					joinUsersPicsDataListAdapter.notifyDataSetChanged();
					String public_time = result_entity.getPublic_time() ;
					EasyLog.e("public_time ---- >"+public_time);
					Date date = formatAll.parse(public_time) ;
					calendarInstatnce.setTime((date));
					
					//end_time = result_entity.getEnd_time() ;
					EasyLog.e("end_time ---- >"+end_time);
					//calendarInstatnce.add(Calendar.DAY_OF_MONTH, 3) ;
					handler.postDelayed(runnable, 1000);
					isBegin = true ;
					//if(!isBegin){
						
					//}
					
					EasyLog.e("image_url------>"+HttpClientUtils.IMAGE_URL
									+ result_entity.getImgURL());
					
					/*UrlImageViewHelper.setUrlDrawable(
							action_img,
							HttpClientUtils.IMAGE_URL
									+ result_entity.getImgURL(),
							R.drawable.home_default);*/
					
					UserService.imageLoader.displayImage(HttpClientUtils.IMAGE_URL
							+ result_entity.getImgURL(),action_img,PhotoUtils.articleImageOptions);
					
					//action_content.setText(StringUtil.ToDBC("" +  Html.fromHtml(result_entity.getContent())));
					//action_content.loadData(new String(result_entity.getContent().replaceAll("\r", "").replaceAll("\n", "").getBytes("UTF-8")), "text/html; charset=UTF-8", null);
					action_content.loadDataWithBaseURL(null,new String(result_entity.getContent().replaceAll("\r", "").replaceAll("\n", "").getBytes("UTF-8")), "text/html",  "utf-8", null);
					
					//action_content.loadData(StringUtil.ToDBC("" + result_entity.getContent()), "text/html", "utf-8");
					/*action_content.setText(Html.fromHtml(result_entity.getContent(),new ImageGetter() {
						
						@Override
						public Drawable getDrawable(String source) {
							 Drawable drawable = null;
			                 drawable = Drawable.createFromPath(source);  // Or fetch it from the URL
			                  // Important
			                 drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable
			                                 .getIntrinsicHeight());
			                 return drawable; 
						}
					},null));*/
					ArrayList<ActionCommentEntity> action_comment_list = result_entity.getCommentList();
					
					if (action_comment_list != null && action_comment_list.size() > 0) {
						this.action_comment_list = action_comment_list ;
						//this.action_comment_list.addAll(action_comment_list);
						adapter = new ActionListAdapter(this.action_comment_list,getApplicationContext());
						realListView.setAdapter(adapter);
						current_position = current_position>0?current_position:0;
					}else{
						//adapter = new ActionListAdapter(this.action_comment_list,getApplicationContext());
						//realListView.setAdapter(adapter);
					}
					realListView.setSelection(current_position);
					adapter.notifyDataSetChanged();

				} else {
					EasyLog.e("活动详情异常.......");
					//AppToast.toastMsgCenter(getApplicationContext(),getString(R.string.ERROR_404)).show();
				}
				
			} else if ((Integer) tag == HttpTagConstantUtils.POST_ACTION_COMMENT) {// 评论活动
				
				if (isSuccess(statusCode)) {
					current_position = 2 ;
					AppToast.toastMsgCenter(getApplicationContext(),"发布成功!").show();
					mEditTextContent.setText("");
					post_img_base64 = new StringBuffer("");
					ll_facechoose.setVisibility(View.GONE);
					imm.hideSoftInputFromWindow(mEditTextContent.getWindowToken(), 0);
					ll_image_display.setVisibility(View.GONE);
					resetReplay();
					getData();
				} else {
					AppToast.toastMsgCenter(getApplicationContext(),
							getString(R.string.ERROR_404)).show();
				}
			} 
			else if ((Integer) tag == HttpTagConstantUtils.POST_COMMENT_REPLAY) {// 回复活动中的某条评论
				if (isSuccess(statusCode)) {
					//pageIndex = 1 ;
					ActionCommentEntity entity = this.action_comment_list.get(current_position-2<0 ? 0 :current_position-2);
					
					//current_comment_id = entity.getId() ;
					if(null != entity){
						
						ActionReplayEntity replayEntity = new ActionReplayEntity() ;
						replayEntity.setId(1);
						replayEntity.setPost_id(entity.getPost_id());
						replayEntity.setParent_id(entity.getParent_id());
						replayEntity.setTo_uid(entity.getFrom_uid());
						replayEntity.setTo_nick(entity.getFrom_nick());
						replayEntity.setFrom_uid(Long.valueOf((aCache.getAsString("uid")==null ||"".equals(aCache.getAsString("uid"))) ? "0" : aCache.getAsString("uid")));
						replayEntity.setFrom_nick((aCache.getAsString("uid")==null ||"".equals(aCache.getAsString("nickname"))) ? "格格" : aCache.getAsString("nickname"));
						replayEntity.setContent(mEditTextContent.getText().toString());
						
						if(entity.getId() != comment_id){
							replay_lists.clear();
							if(entity.getReplys()!=null ){
								//replay_lists = entity.getReplys() ;
								//replay_lists.clear();
								//replay_lists.removeAll(entity.getReplys());
								replay_lists.addAll(entity.getReplys());
							}
						}
						
						
						replay_lists.add(0,replayEntity);
						
						comment_id = entity.getId() ;
						entity.setReplys(replay_lists);
						this.action_comment_list.remove(current_position-2);
						this.action_comment_list.add(current_position-2, entity);
						adapter = new ActionListAdapter(this.action_comment_list,getApplicationContext());
						realListView.setAdapter(adapter);
						current_position = current_position>0?current_position:0;
						realListView.setSelection(current_position);
						adapter.notifyDataSetChanged();
					}
					
					
					
					mEditTextContent.setText("");
					mEditTextContent.setHint("");
					resetReplay();
					//getData();
					
				} else {
					AppToast.toastMsgCenter(getApplicationContext(),
							getString(R.string.ERROR_404)).show();
				}
			}
//			else if ((Integer) tag == HttpTagConstantUtils.POST_PRAISE_ACTION) {// 活动点赞次数
//				if (isSuccess(statusCode)) {
//					action_support_count.setText(""+(((Integer.valueOf(action_support_count.getText()+""))+1)));
//					//getData();
//				} else {
//					AppToast.toastMsgCenter(getApplicationContext(),getString(R.string.ERROR_404)).show();
//				}
//			}
			else if ((Integer) tag == HttpTagConstantUtils.PUT_ADDRESS) {// 更新用户收获地址
				if (isSuccess(statusCode) && (Boolean)result) {
					AppToast.toastMsgCenter(getApplicationContext(),getString(R.string.put_address_success)).show();
				} else {
					AppToast.toastMsgCenter(getApplicationContext(),getString(R.string.put_address_fail)).show();
				}
			}
			
			else if ((Integer) tag == HttpTagConstantUtils.POST_PRAISE_ACTION) {// 活动点赞
				if (isSuccess(statusCode)) {
					int praise_status = (Integer)result ;
					if(praise_status == 100){
						home_support_1.setVisibility(View.VISIBLE);
						no_support_btn.setVisibility(View.GONE);
						this.action_support_count.setText(""+(((Integer.valueOf(action_support_count.getText()+""))+1)));
					}else{
						home_support_1.setVisibility(View.GONE);
						no_support_btn.setVisibility(View.VISIBLE);
						int count =  (Integer.valueOf(action_support_count.getText() + "") - 1) ;
						if(count >=0){
							this.action_support_count.setText(""+ count);
						}else{
							this.action_support_count.setText("0");
						}
						
					}
					//article_support_count.setText(""+(((Integer.valueOf(article_support_count.getText()+""))+1)));
					//getData();
				} else {
					//AppToast.toastMsgCenter(getApplicationContext(),getString(R.string.ERROR_404), 1500).show();
				}
			}
			else if ((Integer) tag == HttpTagConstantUtils.ACTION_COMMENT_LIST) {// 活动评论列表
				ArrayList<ActionCommentEntity> action_comment_list = (ArrayList<ActionCommentEntity>) result;
				if (isSuccess(statusCode)) {
					if (action_comment_list != null) {
						if(action_comment_list.size() > 0){
							this.action_comment_list.addAll(action_comment_list);
							adapter = new ActionListAdapter(this.action_comment_list,getApplicationContext());
							realListView.setAdapter(adapter);
							realListView.setSelection((pageIndex-1)*10+3);
							adapter.notifyDataSetChanged();
						}else if(action_comment_list.size() == 0){
							AppToast.toastMsgCenter(getApplicationContext(),"~_~没有更多了").show();
						}
						
					}else if(action_comment_list == null){
						AppToast.toastMsgCenter(getApplicationContext(),"~_~没有更多了").show();
						/*adapter = new ActionListAdapter(this.action_comment_list,getApplicationContext());
						realListView.setAdapter(adapter);*/
					}
					
				}else{
					//AppToast.toastMsgCenter(getApplicationContext(),"~_~没有更多了").show();
				}
				
			}
		} catch (Exception e) {
			EasyLog.e("活动详情异常......."+e.toString());
		} finally {
			resetReplay();
			if (pullToRefreshListView != null) {
				pullToRefreshListView.onRefreshComplete();
			}
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
		case R.id.popupShareBtn: //活动分享页
			popupShare();
			break;
		case R.id.reward_btn:
			if (!isLogin()) {
				AppToast.toastMsgCenter(getApplicationContext(), R.string.please_login).show();
				Intent i = new Intent(ActionDiscussActivity.this,LoginActivity.class);
				startActivity(i);
				return;
			}
			popupWindow(true);// 弹出中奖名单
			break;
		case R.id.action_support:// 活动点赞
			if (!isLogin()) {
				AppToast.toastMsgCenter(getApplicationContext(), R.string.please_login).show();
				Intent i = new Intent(ActionDiscussActivity.this,LoginActivity.class);
				startActivity(i);
				return;
			}
			praise_action();

			break;
		case R.id.btn_send:
			if (!isLogin()) {
				AppToast.toastMsgCenter(getApplicationContext(), R.string.please_login).show();
				Intent i = new Intent(ActionDiscussActivity.this,LoginActivity.class);
				startActivity(i);
				return;
			}
			/**
			findViewById(R.id.btn_face).setVisibility(View.VISIBLE);
			btn_set_mode_keyboard.setVisibility(View.INVISIBLE);
			if (ll_facechoose.getVisibility() == View.VISIBLE) {
				ll_facechoose.setVisibility(View.GONE);
				//imm.showSoftInput(mEditTextContent,InputMethodManager.SHOW_FORCED);
			} else {// 如果出现表情框，弹出软键盘
				ll_facechoose.setVisibility(View.VISIBLE);
				imm.hideSoftInputFromWindow(mEditTextContent.getWindowToken(), 0);
			}
			**/
			if (current_comment_id != -1 && current_to_uid != -1) {
				replay_someone();
			} else {
				replay_action();
			}
			break;
		case R.id.btn_set_mode_keyboard:
			ll_image_display.setVisibility(View.GONE);
			findViewById(R.id.btn_face).setVisibility(View.VISIBLE);
			btn_set_mode_keyboard.setVisibility(View.INVISIBLE);
//			if(!"on".equals(activity_show_or_hiden.toString())){
//				
//			}
			////btn_camera.setVisibility(View.GONE);
			// 隐藏表情选择框
			if (ll_facechoose.getVisibility() == View.VISIBLE) {
				ll_facechoose.setVisibility(View.GONE);
				imm.showSoftInput(mEditTextContent,
						InputMethodManager.SHOW_FORCED);
			} else {// 如果出现表情框，弹出软键盘
				ll_facechoose.setVisibility(View.VISIBLE);
				imm.hideSoftInputFromWindow(mEditTextContent.getWindowToken(), 0);
				// changeInput();
			}
			//imm.hideSoftInputFromWindow(mEditTextContent.getWindowToken(), 0);
			//changeInput();
			break;
		case R.id.btn_face:
			ll_image_display.setVisibility(View.GONE);
			////btn_camera.setVisibility(View.GONE);
			imm.showSoftInput(v,InputMethodManager.SHOW_FORCED);    
			btn_set_mode_keyboard.setVisibility(View.VISIBLE);
			findViewById(R.id.btn_face).setVisibility(View.INVISIBLE);
			// 隐藏表情选择框
			if (ll_facechoose.getVisibility() == View.VISIBLE) {
				ll_facechoose.setVisibility(View.GONE);
				imm.showSoftInput(mEditTextContent,
						InputMethodManager.SHOW_FORCED);
			} else {// 如果出现表情框，弹出软键盘
				ll_facechoose.setVisibility(View.VISIBLE);
				imm.hideSoftInputFromWindow(mEditTextContent.getWindowToken(), 0);
				// changeInput();
			}
			break;
		case R.id.et_sendmessage:
			ll_image_display.setVisibility(View.GONE);
			//findViewById(R.id.btn_face).setVisibility(View.VISIBLE);
			//btn_set_mode_keyboard.setVisibility(View.INVISIBLE);
			// 隐藏表情选择框
			if (ll_facechoose.getVisibility() == View.VISIBLE) {
				ll_facechoose.setVisibility(View.GONE);
				imm.showSoftInput(mEditTextContent,
						InputMethodManager.SHOW_FORCED);
			} else {// 如果出现表情框，弹出软键盘
				ll_facechoose.setVisibility(View.VISIBLE);
				imm.hideSoftInputFromWindow(mEditTextContent.getWindowToken(), 0);
				// changeInput();
			}
			break;
		case R.id.btn_camera:
			ll_facechoose.setVisibility(View.GONE);
			imm.hideSoftInputFromWindow(mEditTextContent.getWindowToken(), 0);
			if(current_comment_id != -1){
				AppToast.toastMsgCenter(getApplicationContext(), "回复不能上传图片!").show();
				return ;
			}
			if(!"".equals(post_img_base64.toString())){
				ll_facechoose.setVisibility(View.GONE);
				if(View.VISIBLE == ll_image_display.getVisibility()){
					ll_image_display.setVisibility(View.GONE);
				}else{
					ll_image_display.setVisibility(View.VISIBLE);
				}
				
			}else{
				select();
			}
			
			break;
			
		case R.id.btn_register_photo1:
			if (selectPhotoDialog != null) {
				selectPhotoDialog.dismiss();
				selectPhotoDialog = null;
			}
		
			String state = Environment.getExternalStorageState();
			if (state.equals(Environment.MEDIA_MOUNTED)) {
				selectPicFromCamera();
			} else {
				AppToast.toastMsgCenter(getApplicationContext(), "没有SDK卡", 2000).show();
				// saveDir = getApplicationContext().getCacheDir() + "/temple";
			}

			break;
		case R.id.btn_register_photo2:
			if (selectPhotoDialog != null) {
				selectPhotoDialog.dismiss();
				selectPhotoDialog = null;
			}
			//自定义
			Intent intent = new Intent(this,SelectorImageMainActivity.class);
			startActivityForResult(intent, SELECT_FROM_LOCAL);
			overridePendingTransition(R.anim.activity_enter_from_right, R.anim.activity_exit_to_left);
			
			break;
		case R.id.btn_register_photo3:
			if (selectPhotoDialog != null) {
				selectPhotoDialog.dismiss();
				selectPhotoDialog = null;
			}
			break;
		case R.id.delete_img://删除图片
			ll_image_display.setVisibility(View.GONE);
			post_img_base64 = new StringBuffer("");
			break;
		default:
			break;
		}
	}
	
	private Dialog selectPhotoDialog;
	private ColorDrawable dw = new ColorDrawable(Color.TRANSPARENT);
	private void select() {
		try {
			View view = View.inflate(this, R.layout.activity_select_photo, null);
			Button btnPhoto;
			Button btnImage;
			Button btnDismis;
			
			btnPhoto = (Button) view.findViewById(R.id.btn_register_photo1);
			btnImage = (Button) view.findViewById(R.id.btn_register_photo2);
			btnDismis = (Button) view.findViewById(R.id.btn_register_photo3);
			btnPhoto.setOnClickListener(this);
			btnImage.setOnClickListener(this);
			btnDismis.setOnClickListener(this);
			
			selectPhotoDialog = new Dialog(this);
			
			selectPhotoDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			selectPhotoDialog.setContentView(view);
			Window regionWindow = selectPhotoDialog.getWindow();
			regionWindow.setGravity(Gravity.BOTTOM);
			regionWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			regionWindow.setWindowAnimations(R.style.view_animation);
			regionWindow.setBackgroundDrawable(dw);
			selectPhotoDialog.setCanceledOnTouchOutside(false);
		
			selectPhotoDialog.show();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	private String localCameraPath = PathUtils.getTmpPath();
	private static final int TAKE_CAMERA_REQUEST = 2;
	/**
	 * 照相获取图片
	 *******************************/
	public void selectPicFromCamera() {
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		Uri imageUri = Uri.fromFile(new File(localCameraPath));
		openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		startActivityForResult(openCameraIntent, TAKE_CAMERA_REQUEST);
		overridePendingTransition(R.anim.activity_enter_from_right, R.anim.activity_exit_to_left);
	}
	public final static int SELECT_FROM_LOCAL = 20 ;
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		//super.onActivityResult(requestCode, resultCode, data);
		//本地图片
		if (requestCode == SELECT_FROM_LOCAL && resultCode == Activity.RESULT_OK) {
			Uri uri = data.getData();
			EasyLog.e("uri = " + uri);
			try {
				if (data != null) {
					String path = "" ;
					Uri selectedImage = data.getData();
					if (selectedImage != null) {
						Cursor cursor = getContentResolver().query(selectedImage, null, null,
								null, null);
						if (cursor != null) {
							cursor.moveToFirst();
							int columnIndex = cursor.getColumnIndex("_data");
							String picturePath = cursor.getString(columnIndex);
							cursor.close();
							cursor = null;
		
							if (picturePath == null || picturePath.equals("null")) {
								AppToast.toastMsgCenter(getApplicationContext(), "找不到图片").show();
								return;
							}
							path = picturePath ;
							EasyLog.e("picturePath = " + picturePath);
							
						} else {
							File file = new File(selectedImage.getPath());
							if (!file.exists()) {
								AppToast.toastMsgCenter(getApplicationContext(), "找不到图片").show();
								return;
		
							}
							
							EasyLog.e("file.getAbsolutePath() = " + file.getAbsolutePath());
							
							path = file.getAbsolutePath() ;
						}
					}
					
					if(!"".equals(path)){
						
						// avatarImg.setImageBitmap(getSDCardImg(path));// 显示本地图片
						Log.e("photo---------path:", path);
						String img = new FileUtil(getApplicationContext())
						.getBitmapBase64(path/*localUrl*/);
						//EasyLog.e("base_code_64_img >>>>" + img);
						post_img_base64.append(img);
						
					}
					if(!StringUtil.isBlank(post_img_base64.toString())){
						ll_image_display.setVisibility(View.VISIBLE);
						UserService.imageLoader.displayImage("file://"+path, post_img,PhotoUtils.normalImageOptions);
					}
					
					
					
				}
		
			} catch (Exception e) {
				EasyLog.e(e.toString());
			}
		} else if (TAKE_CAMERA_REQUEST == requestCode
				&& resultCode == Activity.RESULT_OK) {//拍照
			
			try {
				EasyLog.e("localCameraPath = " + localCameraPath);
				
				final String objectId = Utils.uuid();
				final String newPath = PathUtils.getChatFilePath(objectId);
				
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
				BitmapFactory.decodeFile(localCameraPath, options);
				int inSampleSize = 1;
				int maxSize = 3000;
				if (options.outWidth > maxSize || options.outHeight > maxSize) {
					int widthScale = (int) Math.ceil(options.outWidth * 1.0 / maxSize);
					int heightScale = (int) Math
							.ceil(options.outHeight * 1.0 / maxSize);
					inSampleSize = Math.max(widthScale, heightScale);
				}
				options.inJustDecodeBounds = false;
				options.inSampleSize = inSampleSize;
				Bitmap bitmap = BitmapFactory.decodeFile(localCameraPath, options);
				//avatarImg.setImageBitmap(bitmap);
				//旋转图片
				Matrix matrix = new Matrix();
				int angle = PhotoUtils.readPictureDegree(localCameraPath);
				if (angle != 0) { // 如果照片出现了 旋转 那么 就更改旋转度数
					matrix = new Matrix();
					matrix.postRotate(angle);
					bitmap = Bitmap.createBitmap(bitmap, 0, 0,
							bitmap.getWidth(), bitmap.getHeight(),
							matrix, true);// 从新生成图片
	
					FileOutputStream out = new FileOutputStream(
							new File(localCameraPath));
					bitmap.compress(Bitmap.CompressFormat.JPEG,
							100, out);
					out.flush();
					out.close();
				}
				
				int w = bitmap.getWidth();
				int h = bitmap.getHeight();
				int newW = w;
				int newH = h;
				if (w > maxSize || h > maxSize) {
					if (w > h) {
						newW = maxSize;
						newH = (int) (newW * h * 1.0 / w);
					} else {
						newH = maxSize;
						newW = (int) (newH * w * 1.0 / h);
					}
				}
				
				Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, newW, newH, false);
				
	
				FileOutputStream outputStream = null;
				try {
					outputStream = new FileOutputStream(newPath);
					newBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} finally {
					try {
						if (outputStream != null) {
							outputStream.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
	
				}
				recycle(newBitmap);
				recycle(bitmap);
				// 上传图片
				 
				
				final String img = new FileUtil(getApplicationContext()).getBitmapBase64(newPath);
				//EasyLog.e("TakePic = " + img);
				post_img_base64.append(img);
				
				if(!StringUtil.isBlank(post_img_base64.toString())){
					ll_image_display.setVisibility(View.VISIBLE);
					UserService.imageLoader.displayImage("file://"+newPath, post_img,PhotoUtils.normalImageOptions);
				}
				
				
				
			} catch (Exception e) {
				EasyLog.e("TAKE_CAMERA_REQUEST ERROR = "+e.toString());
			} catch(OutOfMemoryError error){
				
			}
		}
	}
	
	/**
	 * 回收垃圾 recycle
	 * 
	 * @throws
	 */
	public static void recycle(Bitmap bitmap) {
		// 先判断是否已经回收
		if (bitmap != null && !bitmap.isRecycled()) {
			// 回收并且置为null
			bitmap.recycle();
			bitmap = null;
		}
		System.gc();
	}

	@Override
	protected void onResume() {
		super.onResume();
		current_position = 0;
		//getData();
	}

	private void praise_action() {//活动点赞
		final String token = aCache.getAsString("Token");
		if(!DeviceUtil.isOnline()){
			AppToast.toastMsgCenter(getApplicationContext(), R.string.ERROR_404).show();
			return ;
		}
		
		if(StringUtil.isBlank(token)){
			AppToast.toastMsgCenter(getApplicationContext(), R.string.please_login).show();
			Intent i = new Intent(getApplicationContext(),LoginActivity.class);
			startActivity(i);
			return;
		}
		
		new PostPraiseAsynTaskService(getApplicationContext(),
				HttpTagConstantUtils.POST_PRAISE_ACTION,
				ActionDiscussActivity.this).doPostArticlePraise(
				activityId,token);
	}
	
	private void replay_action() {
		
		final String comment = mEditTextContent.getText().toString();
		
		if(StringUtil.isBlank(comment) && StringUtil.isBlank(post_img_base64.toString())){
			AppToast.toastMsgCenter(getApplicationContext(), R.string.no_comment).show();
			return ;
		}
		
		final String token = aCache.getAsString("Token");
		dialogUploadImage = NiftyDialogBuilder.getInstance(this,
				R.layout.dialog_login_layout);
		final View text = LayoutInflater.from(this).inflate(
				R.layout.dialog_login_view, null);
		TextView t = (TextView) text.findViewById(R.id.loading_text);
		t.setText("发布中....");
		dialogUploadImage.withTitle(null).withMessage(null)
				.withEffect(Effectstype.Fadein).withDuration(100)
				.isCancelableOnTouchOutside(false)
				.setCustomView(text, this.getApplicationContext()).show();
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {//活动评论发布
				new PostActionCommentAsynTaskService(getApplicationContext(),
						HttpTagConstantUtils.POST_ACTION_COMMENT,
						ActionDiscussActivity.this).doPostActionComment(
						activityId, result_entity.getAuthor_id(),
						comment, token,post_img_base64);
			}
		}, 500);

		// EasyLog.e("发送内容>>>>>" + contString);
	}

	private void replay_someone() {
		final String comment = mEditTextContent.getText().toString();
		final String token = aCache.getAsString("Token");
		
		if(!DeviceUtil.isOnline()){
			AppToast.toastMsgCenter(getApplicationContext(), R.string.ERROR_404).show();
			return ;
		}
		
		if(StringUtil.isBlank(token)){
			AppToast.toastMsgCenter(getApplicationContext(), R.string.please_login).show();
			Intent i = new Intent(getApplicationContext(),LoginActivity.class);
			startActivity(i);
			return;
		}
		
		if(StringUtil.isBlank(comment)){
			AppToast.toastMsgCenter(getApplicationContext(), R.string.no_comment).show();
			return ;
		}
		
		dialogUploadImage = NiftyDialogBuilder.getInstance(this,
				R.layout.dialog_login_layout);
		final View text = LayoutInflater.from(this).inflate(
				R.layout.dialog_login_view, null);
		TextView t = (TextView) text.findViewById(R.id.loading_text);
		t.setText("发布中....");
		dialogUploadImage.withTitle(null).withMessage(null)
				.withEffect(Effectstype.Fadein).withDuration(100)
				.isCancelableOnTouchOutside(false)
				.setCustomView(text, this.getApplicationContext()).show();
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				new PostArticleCommentAsynTaskService(getApplicationContext(),
						HttpTagConstantUtils.POST_COMMENT_REPLAY,
						ActionDiscussActivity.this).doPostComment(
						activityId, current_comment_id,
						current_to_uid, comment, token);
			}
		}, 500);

		// EasyLog.e("发送内容>>>>>" + contString);
	}

	@Override
	public void sendMessageHandler(int messageCode) {

	}

	public void resetReplay() {
		EasyLog.e("set hint empty.....");
		mEditTextContent.setHint("");
		current_to_uid = -1;
		current_comment_id = -1;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		findViewById(R.id.btn_face).setVisibility(View.VISIBLE);
		btn_set_mode_keyboard.setVisibility(View.INVISIBLE);
		if (ll_facechoose.getVisibility() == View.VISIBLE) {
			ll_facechoose.setVisibility(View.GONE);
			resetReplay();
			return true;
		} 
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& ((FaceRelativeLayout) findViewById(R.id.FaceRelativeLayout))
						.hideFaceView()) {
			
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View v, int position,
			long _id) {
		findViewById(R.id.btn_face).setVisibility(View.VISIBLE);
		btn_set_mode_keyboard.setVisibility(View.INVISIBLE);
//		if(!"on".equals(activity_show_or_hiden.toString())){
//			//btn_camera.setVisibility(View.GONE);
//		}else{
//			if(View.GONE == btn_camera.getVisibility()){
//				btn_camera.setVisibility(View.VISIBLE);
//			}else if(current_comment_id != -1){
//				//btn_camera.setVisibility(View.GONE);
//			}else if(current_comment_id == -1){
//				btn_camera.setVisibility(View.VISIBLE);
//			}
//		}
		
		if (ll_facechoose.getVisibility() == View.VISIBLE) {
			ll_facechoose.setVisibility(View.GONE);
			resetReplay();
			/*imm.showSoftInput(mEditTextContent,
					InputMethodManager.SHOW_FORCED);*/
			return ;
		} else {// 如果出现表情框，弹出软键盘
			ll_facechoose.setVisibility(View.VISIBLE);
			imm.hideSoftInputFromWindow(mEditTextContent.getWindowToken(), 0);
			resetReplay();
			return ;
		}
			
		// AppToast.toastMsgCenter(getApplicationContext(), ">>>" + position,
		// 2000).show();
		// Intent intent = new Intent(this,MasterInfoActivity.class);
		// startActivity(intent);
	}

	class ActionListAdapter extends BaseAdapter {
		private final static String TAG = "ArticleListAdapter";
		private ArrayList<ActionCommentEntity> arrayList;
		private Context context;
		private LayoutInflater layoutInflater;

		public ActionListAdapter(ArrayList<ActionCommentEntity> list,
				Context c) {
			arrayList = list;
			context = c;
			layoutInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			if (arrayList == null ||(arrayList!=null && arrayList.size() <=0)) {
				return 0;
			}
			return arrayList.size();
		}

		@Override
		public Object getItem(int position) {
			return arrayList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {

			final ActionCommentEntity entity = arrayList.get(position);
			convertView = layoutInflater.inflate(
					R.layout.action_listview_item, null);
			if(entity.getId() == -1){
				convertView = layoutInflater.inflate(
						R.layout.empty_view, null);
				return convertView ;
			}

			View support_layout = convertView.findViewById(R.id.support_layout);
			View comment_layout = convertView.findViewById(R.id.comment_layout);

			TextView author = (TextView) convertView.findViewById(R.id.author);
			author.setText((entity.getFrom_nick()==null || "".equals(entity.getFrom_nick())?"格格":entity.getFrom_nick()));//评论作者

			TextView level = (TextView) convertView.findViewById(R.id.level);
			level.setText(""+StringUtil.getLevel(entity.getIntegral()));

			TextView time = (TextView) convertView.findViewById(R.id.time);
			time.setText("" + StringUtil.humanmizeTime(entity.getCreate_time()));

			TextView comment = (TextView) convertView
					.findViewById(R.id.comment);
			
			Spannable spannableString = SmileUtils.getSmiledText(context, entity.getContent());
			if("".equals(entity.getContent()) || null == entity.getContent()){
				comment.setVisibility(View.GONE);
			}
			
			comment.setText(spannableString, BufferType.SPANNABLE);
			
			ImageView img = (ImageView) convertView.findViewById(R.id.img);
			
			if("".equals(entity.getImg()) || null == entity.getImg()){
				img.setVisibility(View.GONE);
			}else{
				
				img.setVisibility(View.VISIBLE);
				UserService.imageLoader.displayImage(
						entity.getImg()+"", img,
						PhotoUtils.normalImageOptions);
				
			}
			TextView support = (TextView) convertView
					.findViewById(R.id.support);
			support.setText("" + entity.getSupport_count());

			TextView comment_count = (TextView) convertView
					.findViewById(R.id.comment_count);
			comment_count.setText("" + entity.getComment_count());
			
			ImageView no_support_pic = (ImageView) convertView.findViewById(R.id.no_support_pic);
			ImageView support_pic = (ImageView) convertView.findViewById(R.id.support_pic);
			
			if(1 == entity.getPraise_status()){
				support_pic.setVisibility(View.VISIBLE);
				no_support_pic.setVisibility(View.GONE);
			}else{
				support_pic.setVisibility(View.GONE);
				no_support_pic.setVisibility(View.VISIBLE);
			}

			support_layout.setOnClickListener(new OnClickHandler(support,
					position,no_support_pic,support_pic));
			
			comment_layout.setOnClickListener(new OnClickHandler(comment_count,
					position,no_support_pic,support_pic));

			CircleImageView master_pic = (CircleImageView) convertView
					.findViewById(R.id.master_pic);
			
			//UrlImageViewHelper.setUrlDrawable(master_pic, HttpClientUtils.USER_IMAGE_URL+""+StringUtil.getPathByUid(String.valueOf(entity.getFrom_uid())), R.drawable.my_pic_default);
			
			UserService.imageLoader.displayImage(
					HttpClientUtils.USER_IMAGE_URL
							+ StringUtil.getPathByUid(String.valueOf(entity
									.getFrom_uid())), master_pic,
					PhotoUtils.myPicImageOptions);
																
			LinearLayout comment_list_layout = (LinearLayout) convertView
					.findViewById(R.id.comment_list_layout);
			// LinearLayout.LayoutParams parms = new
			// LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);

			// comment_list_layout.setLayoutParams(parms);
			ArrayList<ActionReplayEntity> arrayList = entity.getReplys();

			if (arrayList != null && arrayList.size() > 0) {
				convertView.findViewById(R.id.up_arrow).setVisibility(View.VISIBLE);
				comment_list_layout.removeAllViews();
				ArrayList<View> v = new ArrayList<View>();
				for (int j = 0; j < arrayList.size(); j++) {
					View convertView_ = LayoutInflater.from(context).inflate(
							R.layout.article_replay_list_view_item, null);
					final ActionReplayEntity articleReplayEntity = arrayList.get(j);
					if (null != articleReplayEntity) {
						TextView from = (TextView) convertView_
								.findViewById(R.id.from);//评论人==当前登录的用户昵称
						from.setText("" + articleReplayEntity.getFrom_nick());
						TextView to = (TextView) convertView_
								.findViewById(R.id.to);//楼主
						to.setText("" + articleReplayEntity.getTo_nick()+":");

						TextView comment_ = (TextView) convertView_
								.findViewById(R.id.replay_comment);
						
						Spannable span_replay = SmileUtils.getSmiledText(context, articleReplayEntity.getContent());
						//span, BufferType.SPANNABLE
						comment_.setText(span_replay, BufferType.SPANNABLE/*FaceConversionUtil.getInstace()
								.getExpressionString(context,
										articleReplayEntity.getContent())*/);
						
						
						v.add(convertView_);
						
						from.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View view) {
								if(checkAuthor(articleReplayEntity.getFrom_uid())){
									//btn_camera.setVisibility(View.GONE);
									findViewById(R.id.btn_face).setVisibility(View.VISIBLE);
									btn_set_mode_keyboard.setVisibility(View.INVISIBLE);
									if (ll_facechoose.getVisibility() == View.VISIBLE) {
										ll_facechoose.setVisibility(View.GONE);
										imm.showSoftInput(mEditTextContent,
												InputMethodManager.SHOW_FORCED);
									} else {// 如果出现表情框，弹出软键盘
										ll_facechoose.setVisibility(View.VISIBLE);
										imm.hideSoftInputFromWindow(mEditTextContent.getWindowToken(), 0);
									}
									mEditTextContent.setHint("回复：" + articleReplayEntity.getFrom_nick());
									current_to_uid = articleReplayEntity.getFrom_uid();//entity.getTo_uid();//
									current_position = position + 2;
									current_comment_id = entity.getId();
									EasyLog.e("current_position = " +current_position);
								}
								
							}
						});
						
						to.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View view) {
								if(checkAuthor(articleReplayEntity.getTo_uid())){
									//btn_camera.setVisibility(View.GONE);
									findViewById(R.id.btn_face).setVisibility(View.VISIBLE);
									btn_set_mode_keyboard.setVisibility(View.INVISIBLE);
									if (ll_facechoose.getVisibility() == View.VISIBLE) {
										ll_facechoose.setVisibility(View.GONE);
										imm.showSoftInput(mEditTextContent,
												InputMethodManager.SHOW_FORCED);
									} else {// 如果出现表情框，弹出软键盘
										ll_facechoose.setVisibility(View.VISIBLE);
										imm.hideSoftInputFromWindow(mEditTextContent.getWindowToken(), 0);
									}
									mEditTextContent.setHint("回复：" + articleReplayEntity.getTo_nick());
									current_to_uid = articleReplayEntity.getTo_uid();
									current_position = position + 2;
									current_comment_id = entity.getId();
									EasyLog.e("current_position = " +current_position);
								}
								
							}
						});

					}
				}
				for (int i = 0; i < v.size(); i++) {
					comment_list_layout.addView(v.get(i), i);
				}

			} else {
				comment_list_layout.setVisibility(View.GONE);
			}

			return convertView;
		}
		
		public boolean checkAuthor(long from_uid){
			try {
				if (!isLogin()) {
					AppToast.toastMsgCenter(getApplicationContext(), R.string.please_login).show();
					Intent i = new Intent(ActionDiscussActivity.this,LoginActivity.class);
					startActivity(i);
					return false;
				}
				String uid = ACache.get(getApplicationContext()).getAsString("uid");
				if(from_uid == Integer.valueOf(uid)){
					AppToast.toastMsgCenter(getApplicationContext(), R.string.cant_replay_self).show();
					return false;
				}
				return true ;
			} catch (Exception e) {
				return false;
			}
		}

		// 处理回复
		class OnClickHandler implements View.OnClickListener,
				HttpAysnResultInterface {
			
			public TextView textView;
			ImageView no_support_pic ;
			ImageView support_pic ;
			public ActionCommentEntity entity;
			public int position;

			public OnClickHandler(TextView textView, final int position,ImageView no_support_pic,ImageView support_pic) {
				this.textView = textView;
				this.no_support_pic = no_support_pic;
				this.support_pic = support_pic;
				this.position = position;
			}

			@Override
			public void onClick(View v) {
				if(!isLogin()){
					AppToast.toastMsgCenter(getApplicationContext(),"请登录!").show();
					Intent i = new Intent(ActionDiscussActivity.this,LoginActivity.class);
					startActivity(i);
					return ;
				}
				current_position = position ;
				this.entity = arrayList.get(position);
				switch (v.getId()) {
				case R.id.support_layout:// 活动评论点赞
					//this.textView.setText(""+ (Integer.valueOf(textView.getText() + "") + 1));
					// AppToast.toastMsgCenter(getApplicationContext(),"support_layout---",
					// 2000).show();

					new PostCommentPraiseAsynTaskService(
							getApplicationContext(),
							HttpTagConstantUtils.POST_COMMENT_PRAISE_ACTION,
							OnClickHandler.this).doPostCommentPraise(
							entity.getId(), aCache.getAsString("Token"));
					break;
				case R.id.comment_layout:// 评论回复
					// AppToast.toastMsgCenter(getApplicationContext(),
					// "comment_layout---",2000).show();
					//btn_camera.setVisibility(View.GONE);
					findViewById(R.id.btn_face).setVisibility(View.VISIBLE);
					btn_set_mode_keyboard.setVisibility(View.INVISIBLE);
					if (ll_facechoose.getVisibility() == View.VISIBLE) {
						ll_facechoose.setVisibility(View.GONE);
						imm.showSoftInput(mEditTextContent,
								InputMethodManager.SHOW_FORCED);
					} else {// 如果出现表情框，弹出软键盘
						ll_facechoose.setVisibility(View.VISIBLE);
						imm.hideSoftInputFromWindow(mEditTextContent.getWindowToken(), 0);
					}
					
					mEditTextContent.setHint("回复：" + entity.getFrom_nick()/*entity.getTo_nick()*/);//楼主
					current_to_uid = entity.getFrom_uid();
					current_position = position + 2;
					current_comment_id = entity.getId();
					break;
				default:
					break;
				}

			}

			@Override
			public void dataCallBack(Object tag, int statusCode, Object result) {
				try {
					if (dialogUploadImage != null) {
						dialogUploadImage.dismiss();
					}
					if ((Integer) tag == HttpTagConstantUtils.POST_COMMENT_PRAISE_ACTION) {
						if (isSuccess(statusCode)) {
							
							int praise_status = (Integer)result ;
							if(praise_status == 100){
								support_pic.setVisibility(View.VISIBLE);
								no_support_pic.setVisibility(View.GONE);
								this.textView.setText(""+ (Integer.valueOf(textView.getText() + "") + 1));
							}else{
								support_pic.setVisibility(View.GONE);
								no_support_pic.setVisibility(View.VISIBLE);
								int count =  (Integer.valueOf(textView.getText() + "") - 1) ;
								if(count >=0){
									this.textView.setText(""+ count);
								}else{
									this.textView.setText("0");
								}
								
							}
							// this.textView.setText(""+(Integer.valueOf(textView.getText()+"")+1));
							// getData();
						} else {
							AppToast.toastMsgCenter(getApplicationContext(),ActionDiscussActivity.this.getResources().getString(R.string.ERROR_404)).show();
						}
					}
				} catch (Exception e) {

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

	}
	
	@Override
	protected void onPause() {
		super.onPause();
		try {
			resetReplay() ;
			current_position = 0 ;
			isBegin = false ;
			//handler.removeCallbacksAndMessages(null);
			handler.removeCallbacks(runnable); 
		} catch (Exception e) {
			
		}
	}
	
	@Override
	protected void onStop() {
		super.onDestroy();
		try {
			resetReplay() ;
			current_position = 0 ;
			isBegin = false ;
			//handler.removeCallbacksAndMessages(null);
			handler.removeCallbacks(runnable); 
		} catch (Exception e) {
			
		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			resetReplay() ;
			current_position = 0 ;
			isBegin = false ;
			//handler.removeCallbacksAndMessages(null);
			handler.removeCallbacks(runnable); 
		} catch (Exception e) {
			
		}
	}
	
	//活动分享
	void popupShare() {
		try {
			logout(SHARE_MEDIA.SINA);
			logout(SHARE_MEDIA.QZONE);
			logout(SHARE_MEDIA.WEIXIN);
			// 使用友盟分享
			Bitmap logo = BitmapFactory.decodeResource(getResources(),
					R.drawable.app_icon);
			UMShareUtil.getInstance().share(
					this,
					"" + result_entity.getTitle(),
					"" + result_entity.getTitle(),
					"http://napi.skinrun.cn/v1/archiveDetails/"
							+ activityId,
					logo,
					"" + HttpClientUtils.IMAGE_URL + ""
							+ result_entity.getImgURL(), new ShareAction() {
								
								@Override
								public void onSuccess() {
									try {
										String token = ACache.get(ActionDiscussActivity.this).getAsString("Token");
										token = (token == null ?"":token) ;
										new PostUserShareService(ActionDiscussActivity.this,
												HttpTagConstantUtils.SHARE_JIFEN,
												ActionDiscussActivity.this).post_share(token);
									} catch (Exception e) {
										// TODO: handle exception
									}
								
									
								}
							});

		} catch (Exception e) {
			
		}
		
	}
	
	/**
     * 注销本次登录</br>
     */
    public void logout(final SHARE_MEDIA platform) {
    	UMShareUtil.getInstance().getUmsocialService().deleteOauth(this, platform, new SocializeClientListener() {

            @Override
            public void onStart() {

            }

            @Override
            public void onComplete(int status, SocializeEntity entity) {
                
            }
        });
    }
	
	// 活动中奖名单
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
						R.layout.activity_awarded_popup, null);

				initViews(view, nameList);

				popupWindow = new PopupWindow(view, width, height);
				popupWindow.setFocusable(true);
			} else {
				setViewVisibility(1);
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
			new GetUserAddressService(getApplicationContext(),
					HttpTagConstantUtils.GET_ADDRESS, this).get_address(aCache
					.getAsString("Token"));
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
			String shippingAddress = Address.getText().toString();
			if (StringUtil.isBlank(shippingAddress)) {
				AppToast.toastMsg(getApplicationContext(), "~亲，您还什么都没有填哦").show();
				return;
			}
			if (StringUtil.isBlank(shippingAddress)) {
				AppToast.toastMsg(getApplicationContext(), "~亲，您还什么都没有填哦").show();
				return;
			}else{
				new PutUserAddressTaskService(getApplicationContext(),
						HttpTagConstantUtils.PUT_ADDRESS,
						this).put_user_address(
						aCache.getAsString("Token"), shippingAddress);
			}
		}
		
		@Override
		public void dataCallBack(Object tag, int statusCode, Object result) {
			try {
				if (dialogUploadImage != null) {
					dialogUploadImage.dismiss();
				}
				if(HttpTagConstantUtils.PUT_ADDRESS == (Integer)tag){
					if (isSuccess(statusCode) && (Boolean)result) {
						dismiss();
						AppToast.toastMsgCenter(getApplicationContext(),getString(R.string.put_address_success)).show();
					} else {
						AppToast.toastMsgCenter(getApplicationContext(),getString(R.string.put_address_fail)).show();
					}
				}else if(HttpTagConstantUtils.GET_ADDRESS == (Integer)tag){
					Address.setText(result.toString()+"");
					Address.setSelection((result.toString()+"").length());
				}
			} catch (Exception e) {
				EasyLog.e("更新用户收获地址异常......."+e.toString());
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

	// 活动没有中奖
	class NoListOfAwardedPopupWindow implements View.OnClickListener {

		private PopupWindow popupWindow;
		private View activityView;
		private Activity context;

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
						R.layout.activity_no_awarded_popup, null);

				initViews(view, nameList);

				popupWindow = new PopupWindow(view, width, height);
				popupWindow.setFocusable(true);
				popupWindow.setAnimationStyle(R.style.DialogAnimation);
				popupWindow.showAtLocation(activityView, Gravity.BOTTOM, 0, 0);
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						dismiss();

					}
				}, 2000);

			} else {
				popupWindow.setAnimationStyle(R.style.DialogAnimation);
				popupWindow.showAtLocation(activityView, Gravity.BOTTOM, 0, 0);
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						dismiss();

					}
				}, 2000);
			}

			

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
			
		}

		private void setViewVisibility(int flag) {
			
			// 默认显示中奖名单，如果已经中奖，登录后直接跳到填写地址请加上参数判断一下
			// zhongJianGroup.setVisibility(View.GONE);
			// 未登录显示按钮，已经登录显示一句话
			// openBtn.setVisibility(View.VISIBLE);
			// noHappy.setVisibility(View.GONE);
			// noHappy.setVisibility(View.VISIBLE);
			// openBtn.setVisibility(View.GONE);
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

	private void popupWindow(boolean isAwarded) {
		if (!isLogin()) {
			AppToast.toastMsgCenter(getApplicationContext(),
					R.string.please_login).show();
			Intent i = new Intent(ActionDiscussActivity.this,
					LoginActivity.class);
			startActivity(i);
			return;
		}
		boolean isAward = false;
		int current_uid = MyApplication.getInstance().getSessionUser().getU_id() ;
		
		StringBuffer buffer = new StringBuffer();
		Map<Integer,String> map = new HashMap<Integer,String>();
		for (UserEntity entity : prize_users) {
			map.put(entity.getU_id(), entity.getNiakname());
		}
		for (Integer key : map.keySet()) {
			buffer.append(map.get(key) + ";");
			if (current_uid == key || key.equals(current_uid)) {
				isAward = true;
			}
		}
		if (isAward) {
			// 如果已经登录，并且中奖弹出中奖框
			if (inListOfAwardedPopupWindow == null) {
				inListOfAwardedPopupWindow = new InListOfAwardedPopupWindow();
			}

			inListOfAwardedPopupWindow.show(this, buffer.toString());
		} else {
			// 弹出未中奖
			if (noListOfAwardedPopupWindow == null) {
				noListOfAwardedPopupWindow = new NoListOfAwardedPopupWindow();
			}
			noListOfAwardedPopupWindow.show(this, buffer.toString());
		}

	}
	
	@Override
    protected String getActivityName() {
        return "活动详情页面";
    }


}

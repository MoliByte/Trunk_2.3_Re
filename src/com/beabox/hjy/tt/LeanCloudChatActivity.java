package com.beabox.hjy.tt;

import java.io.File;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;
import org.kymjs.aframe.utils.SystemTool;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.ClipboardManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.app.base.init.ACache;
import com.app.base.init.MyApplication;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVMessage;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.Group;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.Session;
import com.avoscloud.chat.base.ChatMsgAdapter;
import com.avoscloud.chat.base.ChatMsgEntity;
import com.avoscloud.chat.base.ExpandGridView;
import com.avoscloud.chat.base.ExpressionAdapter;
import com.avoscloud.chat.base.ExpressionGridGuideAdapter;
import com.avoscloud.chat.base.ExpressionPagerAdapter;
import com.avoscloud.chat.base.FileUtils;
import com.avoscloud.chat.base.SmileUtils;
import com.avoscloud.chat.db.DBHelper;
import com.avoscloud.chat.entity.Msg;
import com.avoscloud.chat.entity.MsgBuilder;
import com.avoscloud.chat.entity.RoomType;
import com.avoscloud.chat.entity.SendCallback;
import com.avoscloud.chat.service.ChatService;
import com.avoscloud.chat.service.MsgAgent;
import com.avoscloud.chat.service.listener.MsgReceiverListener;
import com.avoscloud.chat.service.receiver.GroupMessageReceiver;
import com.avoscloud.chat.service.receiver.MsgReceiver;
import com.avoscloud.chat.util.Connectivity;
import com.avoscloud.chat.util.PathUtils;
import com.avoscloud.chat.util.PhotoUtils;
import com.avoscloud.chat.util.Utils;
import com.base.app.utils.DBService;
import com.base.app.utils.StringUtil;
import com.base.pulltorefresh.view.PullToRefreshBase;
import com.base.pulltorefresh.view.PullToRefreshBase.Mode;
import com.base.pulltorefresh.view.PullToRefreshBase.OnRefreshListener2;
import com.base.pulltorefresh.view.PullToRefreshListView;
import com.base.service.impl.HttpClientUtils;
import com.base.supertoasts.util.AppToast;
import com.imagechooser.ui.SelectorImageMainActivity;
import com.umeng.message.PushAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;

/**
 * 聊天页面
 * 
 */
public class LeanCloudChatActivity extends Activity implements OnClickListener,
		MsgReceiverListener{
	public static LeanCloudChatActivity ctx;
	// 反射，修改了包名，这里也需要相应的修改，否则出现不了表情框
	String class_for_name = "com.avoscloud.chat.base.SmileUtils";
	static String RETRY_ACTION = "com.avoscloud.chat.RETRY_CONNECT";
	private static final String TAG = "LeanCloudChatActivity";
	public static final int REQUEST_CODE_COPY_AND_PASTE = 11;
	public static final int REQUEST_CODE_CAMERA = 18;
	
	public static final String MSG_TEXT_TYPE = "Text" ;
	public static final String MSG_IMG_TYPE = "Image" ;

	public static final int REQUEST_CODE_LOCAL = 19;
	private static final int TAKE_CAMERA_REQUEST = 2;

	private InputMethodManager manager;
	private ClipboardManager clipboard;
	private String localCameraPath = PathUtils.getTmpPath();

	private PullToRefreshListView listView;
	private ChatMsgAdapter mAdapter;

	private List<ChatMsgEntity> mDataArrays = new ArrayList<ChatMsgEntity>();

	private RelativeLayout edittext_layout;
	private View more;
	private View root_layout;
	private EditText mEditTextContent;
	private View buttonSend;
	private View backHome;
	private Button btnMore;
	private ImageView btn_take_picture;
	private ImageView btn_picture;

	private RelativeLayout emojiIconContainer;
	private LinearLayout btnContainer;
	private ImageView iv_emoticons_normal;
	private ImageView iv_emoticons_checked;

	private ViewPager expressionViewpager;
	private LinearLayout layout_point;
	
	/** 游标点集合 */
	private ArrayList<ImageView> pointViews;
	/** 表情页界面集合 */
	private ArrayList<View> pageViews = new ArrayList<View>(2);

	private GridView infoshow_gridview;
	private ExpressionGridGuideAdapter gridviewAdapter;

	private List<String> reslist;

	private View buttonSetModeKeyboard;
	
	private View new_msg_layout ;

	private int currentIndex_ = 0;
	private boolean m = true;
	private int gridview_horSpac = 26;//
	private int gridview_xpadding = 10;
	private int gridview_ypadding = 5;
	private int emoji_size = 6;
	
	MsgAgent msgAgent;
	
	static int totalPage = 0 ;

	static int pageIndex = 1 ;
	
//	向聊天室发送请求URL
//	private final String SEND_URL = "https://api.cn.rong.io/message/chatroom/publish.json";

	@SuppressLint("SimpleDateFormat")
	private static SimpleDateFormat format = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm");
	
	private final static int SEND_MSG_SUCCESS = 200 ;
	private final static int SEND_MSG_FAILED = 404 ;

	private MyHandler myHandler = new MyHandler() ;
	class MyHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			switch (msg.what) {
			case SEND_MSG_SUCCESS:
				//AppToast.toastMsgCenter(LeanCloudChatActivity.this, "消息发送成功!").show();
				break;
			case SEND_MSG_FAILED:
				AppToast.toastMsgCenter(LeanCloudChatActivity.this, "发送失败!").show();
				break;
			default:
				break;
			}
		}

	}
	
	private float moveY; 
	
	private float beginY ;
	private float endY ;
	
	//static AVUser curUser;
	public static Group group;
	DBHelper dbHelper;
	public static boolean isLoginSuccess = false ;
	
	
	private Handler handler = new Handler();
	private String timmer_str = "21:00:00";// 每晚九点
	
	SimpleDateFormat formatAll = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	SimpleDateFormat formatYMD = new SimpleDateFormat("yyyy-MM-dd ");
	SimpleDateFormat formatHMS = new SimpleDateFormat("HH:mm:ss");

	Runnable runnable = new Runnable() {

		@Override
		public void run() {
			try {
				Date now = new Date();
				String now_str = formatYMD.format(now);// 得到当前日期格式化后的字符串
				String tonight_time_str = now_str + timmer_str;
				Date tonight_date = formatAll.parse(tonight_time_str);// 每晚的九点
				long left_time = (now.getTime() - tonight_date.getTime()) / 1000;// 每天到晚上9点之间的毫秒数
				
				if (left_time < 0) {
				} else if ((left_time > 0 || left_time == 1)
						&& left_time < 3600) {// 21:00-22:00：活动进行中
					
				} else if (left_time >= 3600 && left_time < 3 * 3600) {// 活动结束
					if(!LeanCloudChatActivity.this.isFinishing()){
						pageIndex = 1 ;
						try {
							//GroupMessageReceiver.removeMsgRecevierListener(LeanCloudChatActivity.this);
							quitGroup() ;
							DBService.deleteAllMsg();
							MyApplication.deleteChatCache();
							finish();
							
						} catch (Exception e) {
							
						}
						
					}
				}
				handler.postDelayed(this, 1000);
			} catch (Exception e) {
			}
		}

	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		MyApplication.getInstance().addActivity(this);
		PushAgent.getInstance(this).onAppStart();
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		//getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		super.onCreate(savedInstanceState);
		ctx = this ;
		setContentView(R.layout.pm9_activity_chat);
		MyApplication.resume_index = 3 ;
		initView();
		setUpView();
		initDate();
	}

	private void initDate() {
		Session session = ChatService.getSession();
		group = session.getGroup(MyApplication.GROUP_ID);
		
		dbHelper = new DBHelper(getApplicationContext(), MyApplication.DB_NAME, MyApplication.DB_VER);
		//创建消息发送代理
		msgAgent = new MsgAgent(RoomType.Group, MyApplication.GROUP_ID);
		
	}

	/**
	 * initView
	 */
	protected void initView() {
		List<String> emoji_list = FileUtils.getEmojiFile(this);
		clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
		manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		more = findViewById(R.id.more);
		root_layout = findViewById(R.id.root_layout);
		edittext_layout = (RelativeLayout) findViewById(R.id.edittext_layout);
		mEditTextContent = (EditText) findViewById(R.id.et_sendmessage);
		btnMore = (Button) findViewById(R.id.btn_more);
		buttonSend = findViewById(R.id.btn_send);
		listView = (PullToRefreshListView) findViewById(R.id.list);
		emojiIconContainer = (RelativeLayout) findViewById(R.id.ll_face_container);
		btnContainer = (LinearLayout) findViewById(R.id.ll_btn_container);
		iv_emoticons_normal = (ImageView) findViewById(R.id.iv_emoticons_normal);
		iv_emoticons_checked = (ImageView) findViewById(R.id.iv_emoticons_checked);
		expressionViewpager = (ViewPager) findViewById(R.id.vPager);
		layout_point = (LinearLayout) findViewById(R.id.iv_image);

		buttonSetModeKeyboard = findViewById(R.id.btn_set_mode_keyboard);
		infoshow_gridview = (GridView) findViewById(R.id.infoshow_gridview);
		//去掉默认背景
		infoshow_gridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
		
		backHome = findViewById(R.id.backHome);

		btn_picture = (ImageView) findViewById(R.id.btn_picture);
		btn_take_picture = (ImageView) findViewById(R.id.btn_take_picture);
		
		
		new_msg_layout = findViewById(R.id.new_msg_layout);
		
		btn_picture.setOnClickListener(this);
		btn_take_picture.setOnClickListener(this);
		backHome.setOnClickListener(this);
		
		new_msg_layout.setOnClickListener(this);
	}

	@SuppressWarnings("deprecation")
	private void setUpView() {
		
		listView.getLoadingLayoutProxy().setPullLabel("下拉加载更多消息");
		listView.getLoadingLayoutProxy().setReleaseLabel("放开加载更多消息");
		listView.setMode(Mode.PULL_FROM_START);// 下拉加载更多
		listView
				.setOnRefreshListener(new OnRefreshListener2<ListView>() {
					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						String label = format.format(new Date());
						refreshView.getLoadingLayoutProxy()
								.setLastUpdatedLabel("最后加载时间:" + label);
						pageIndex ++ ;
						new UpdateDataTask().execute(new Void[]{});
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {
					}

				});
		
		listView.getRefreshableView().setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if(scrollState == OnScrollListener.SCROLL_STATE_IDLE
						&& view.getLastVisiblePosition() == view.getCount() - 1){
					Message msg = new Message() ;
    				msg.what = 100 ;
    				dataHandler.sendMessage(msg);
				}
				
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount){
				
				
			}
		});
		
		/*listView.getRefreshableView().setOnTouchListener(new OnTouchListener() {
			@Override
		    public boolean onTouch(View v, MotionEvent event) {
		        switch (event.getAction()) {
		            case MotionEvent.ACTION_DOWN:
		            	beginY = event.getY();
		                break;
		            case MotionEvent.ACTION_MOVE:
		            	moveY = v.getScrollY() ;
		            	
		                if(moveY - event.getRawY() > 0){
		                    // 上推
		                }
		                moveY = v.getScrollY();
		                
		                endY = event.getY();
		                break;
		            case MotionEvent.ACTION_UP:
		            	if(endY - beginY > 0){//手指从上往下滑动
		            		
		            	}
		            	
		            	if(endY - beginY < 0){//手指从下往上滑动
		            		Message msg = new Message() ;
		    				msg.what = 100 ;
		    				dataHandler.sendMessage(msg);
		            	}
		            	break;
		            default:
		                break;
		        }
		        return false;
		    }

		});*/
		
		totalPage = DBService.getMsgCount() ;
		Log.e(TAG, "totalPage = " + totalPage);
		//pageIndex = totalPage ;
		mDataArrays =  DBService.loadMsgByPage(pageIndex);//DBService.loadAllMsg();
		// 数据
		mAdapter = new ChatMsgAdapter(this,this, mDataArrays);
		listView.setAdapter(mAdapter);
		
		int count = listView.getRefreshableView().getCount();
		if (count > 0) {
			listView.getRefreshableView().setSelection(count - 1);
		}
		listView.getRefreshableView().setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
		
		root_layout.setOnClickListener(this);
		
		initExpression();
		
		

	}
	
	class UpdateDataTask extends AsyncTask<Void,Integer,List<ChatMsgEntity>>{  
  
        /** 
         * 运行在UI线程中，在调用doInBackground()之前执行 
         */  
        @Override  
        protected void onPreExecute() {  
            //Toast.makeText(context,"开始执行",Toast.LENGTH_SHORT).show();  
        }  
        /** 
         * 后台运行的方法，可以运行非UI线程，可以执行耗时的方法 
         */  
        @Override  
        protected List<ChatMsgEntity> doInBackground(Void... params) {  
           /* int i=0;  
            while(i<2){  
                i++;  
                publishProgress(i);  
                
            }  */
            return DBService.loadMsgByPage(pageIndex);  
        }  
  
        /** 
         * 运行在ui线程中，在doInBackground()执行完毕后执行 
         */  
        @Override  
        protected void onPostExecute(List<ChatMsgEntity> list) {  

			if(pageIndex <= totalPage){
				mDataArrays.addAll(list);
				listView.setAdapter(mAdapter);
				Collections.sort(mDataArrays);
				listView.getRefreshableView().setSelection((pageIndex-1) * 10);
				mAdapter.refresh();
			}else if(list.size() <= 0){
				AppToast.toastMsgCenter(getApplicationContext(), getResources().getString(R.string.no_more_msg)).show();
			}else{
				AppToast.toastMsgCenter(getApplicationContext(), getResources().getString(R.string.no_more_msg)).show();
			}
			
			listView.onRefreshComplete() ;
        }  
  
        /** 
         * 在publishProgress()被调用以后执行，publishProgress()用于更新进度 
         */  
        @Override  
        protected void onProgressUpdate(Integer... values) {  
            
        }  
    }  

	
	public void rootClick(View v){
		hideKeyboard();
		more.setVisibility(View.GONE);
		iv_emoticons_normal.setVisibility(View.VISIBLE);
		iv_emoticons_checked.setVisibility(View.INVISIBLE);
		emojiIconContainer.setVisibility(View.GONE);
		btnContainer.setVisibility(View.GONE); 
	}
	/***键盘隐藏*/
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN
				&& ((getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
						/*|| emojiIconContainer.getVisibility() == View.VISIBLE 
						|| btnContainer.getVisibility() == View.VISIBLE*/)
				
				) {
			View v = getCurrentFocus(); 
			if(v.getId() == R.id.et_sendmessage){
				hideKeyboard();
			}
			
		}
		// 必不可少，否则所有的组件都不会有TouchEvent了
		if (getWindow().superDispatchTouchEvent(ev)) {
			return true;
		}
		return onTouchEvent(ev);
	}
	
	
	/**
	 * 初始化表情
	 ******************************/
	private void initExpression() {

		iv_emoticons_normal.setOnClickListener(this);
		iv_emoticons_checked.setOnClickListener(this);

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
				if (!TextUtils.isEmpty(s)) {
					btnMore.setVisibility(View.GONE);
					buttonSend.setVisibility(View.VISIBLE);
				} else {
					btnMore.setVisibility(View.VISIBLE);
					buttonSend.setVisibility(View.GONE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		
		mEditTextContent
				.setOnEditorActionListener(new OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						if (actionId == EditorInfo.IME_ACTION_SEND) {
							send();
						}
						return false;
					}
				});
		
	}

	@Override
	public void onClick(View v) {
		try {
			
			int id = v.getId();
			new_msg_layout.setVisibility(View.INVISIBLE);
			if (id == R.id.iv_emoticons_normal) { // 点击显示表情框
				manager.hideSoftInputFromWindow(mEditTextContent.getWindowToken(), 0);
				more.setVisibility(View.VISIBLE);
				iv_emoticons_normal.setVisibility(View.GONE);
				iv_emoticons_checked.setVisibility(View.VISIBLE);
				btnContainer.setVisibility(View.GONE);
				emojiIconContainer.setVisibility(View.VISIBLE);
			} else if (id == R.id.iv_emoticons_checked) {//点击键盘
				iv_emoticons_normal.setVisibility(View.VISIBLE);
				iv_emoticons_checked.setVisibility(View.GONE);
				btnContainer.setVisibility(View.VISIBLE);
				emojiIconContainer.setVisibility(View.GONE);
				more.setVisibility(View.GONE);
				// 点击隐藏表情框，弹出键盘
				showKeyboard();
			} else if (id == R.id.btn_send) {
				send();
			} else if (id == R.id.btn_take_picture) {// 拍照
				if(isStateFine()){
					selectPicFromCamera();
				}
				hideMoreContainer();
			} else if (id == R.id.btn_picture) {// 本地图片
				if(isStateFine()){
					selectPicFromLocal();
				}
				hideMoreContainer();
			} else if(id == R.id.root_layout){
				Log.e(TAG, "keyboard is gone");
				more.setVisibility(View.GONE);
				iv_emoticons_normal.setVisibility(View.VISIBLE);
				iv_emoticons_checked.setVisibility(View.INVISIBLE);
				emojiIconContainer.setVisibility(View.GONE);
				btnContainer.setVisibility(View.GONE); 
			}else if(id == R.id.backHome){
				quitGroup();
			}else if(id == R.id.new_msg_layout){
				//点击消息
				mAdapter.refresh();
				//由于消息刷新太快，不直接显示到底部
				listView.getRefreshableView().setSelection(listView.getRefreshableView().getCount() - 1);
				new_msg_layout.setVisibility(View.INVISIBLE);
			}
		} catch (Exception e) {
			
		} catch(OutOfMemoryError e){
			
		}
		

	}
	
	public void hideMoreContainer(){
		if (more.getVisibility() == View.GONE) {
			System.out.println("more gone");
			more.setVisibility(View.VISIBLE);
			btnContainer.setVisibility(View.VISIBLE);
			emojiIconContainer.setVisibility(View.GONE);
			manager.hideSoftInputFromWindow(mEditTextContent.getWindowToken(), 0);
		} else {
			
			if (emojiIconContainer.getVisibility() == View.VISIBLE) {
				emojiIconContainer.setVisibility(View.GONE);
				btnContainer.setVisibility(View.VISIBLE);
				iv_emoticons_normal.setVisibility(View.VISIBLE);
				iv_emoticons_checked.setVisibility(View.GONE);
				manager.hideSoftInputFromWindow(mEditTextContent.getWindowToken(), 0);
			} else {
				more.setVisibility(View.GONE);
				//showKeyboard();
			}

		}
	}

	/**
	 * 绘制游标背景
	 */
	public void draw_Point(int index) {
		for (int i = 1; i < pointViews.size(); i++) {
			if (index == i) {
				pointViews.get(i).setBackgroundResource(R.drawable.d2);
			} else {
				pointViews.get(i).setBackgroundResource(R.drawable.d1);
			}
		}
	}

	/**
	 * 显示键盘图标
	 * 
	 * @param view
	 */
	public void setModeKeyboard(View view) {
		edittext_layout.setVisibility(View.VISIBLE);
		more.setVisibility(View.GONE);
		view.setVisibility(View.GONE);
		mEditTextContent.requestFocus();
		if (TextUtils.isEmpty(mEditTextContent.getText())) {
			btnMore.setVisibility(View.VISIBLE);
			buttonSend.setVisibility(View.GONE);
		} else {
			btnMore.setVisibility(View.GONE);
			buttonSend.setVisibility(View.VISIBLE);
		}

	}

	/**
	 * 显示或隐藏图标按钮页
	 * 
	 * @param view
	 */
	public void more(View view) {
		new_msg_layout.setVisibility(View.INVISIBLE);
		//mEditTextContent.clearFocus() ;
		if (more.getVisibility() == View.GONE) {
			System.out.println("more gone");
			more.setVisibility(View.VISIBLE);
			btnContainer.setVisibility(View.VISIBLE);
			emojiIconContainer.setVisibility(View.GONE);
			manager.hideSoftInputFromWindow(mEditTextContent.getWindowToken(), 0);
		} else {
			
			if (emojiIconContainer.getVisibility() == View.VISIBLE) {
				emojiIconContainer.setVisibility(View.GONE);
				btnContainer.setVisibility(View.VISIBLE);
				iv_emoticons_normal.setVisibility(View.VISIBLE);
				iv_emoticons_checked.setVisibility(View.GONE);
				manager.hideSoftInputFromWindow(mEditTextContent.getWindowToken(), 0);
			} else {
				more.setVisibility(View.GONE);
				showKeyboard();
			}

		}

	}

	/**
	 * 隐藏软键盘
	 */
	private void hideKeyboard() {
		if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getCurrentFocus() != null)
				manager.hideSoftInputFromWindow(getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
	/**
	 * 显示软键盘
	 */
	private void showKeyboard() {
		manager.showSoftInput(mEditTextContent,
			InputMethodManager.SHOW_FORCED);
		mEditTextContent.requestFocus();
	}

	/**
	 * 点击文字输入框
	 * 
	 * @param v
	 */
	public void editClick(View v) {
		if (more.getVisibility() == View.VISIBLE) {
			more.setVisibility(View.GONE);
			iv_emoticons_normal.setVisibility(View.VISIBLE);
			iv_emoticons_checked.setVisibility(View.GONE);
		}
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
					// 文字输入框可见时，才可输入表情
					// 按住说话可见，不让输入表情
					if (buttonSetModeKeyboard.getVisibility() != View.VISIBLE) {

						if (filename != "delete_expression") { // 不是删除键，显示表情
							// 这里用的反射，所以混淆的时候不要混淆SmileUtils这个类
							Class clz = Class.forName(class_for_name);
							Field field = clz.getField(filename);
							mEditTextContent.append(SmileUtils.getSmiledText(
									LeanCloudChatActivity.this,
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
				}

			}
		});
		return view;
	}

	private void send() {
		try {
			final String contString = mEditTextContent.getText().toString();
	
			final JSONObject messageJson = new JSONObject();
		
			//messageJson.put("avatar", "http://napi.skinrun.cn/uploads/000/025/815/source.jpg");
			messageJson.put("uid", ""+ACache.get(MyApplication.ctx).getAsString("uid"));
			messageJson.put("nickname", ""+ACache.get(MyApplication.ctx).getAsString("nickname"));
			messageJson.put("content", contString);
			messageJson.put("localpath", "");
			
			if(!SHARE_MEDIA.WEIXIN.equals(ACache.get(this).getAsString("type"))
					&&!SHARE_MEDIA.SINA.equals(ACache.get(this).getAsString("type"))
					&&!SHARE_MEDIA.QZONE.equals(ACache.get(this).getAsString("type"))){
				
				messageJson.put("avatar", HttpClientUtils.USER_IMAGE_URL
								+ StringUtil.getPathByUid("" + ACache.get(MyApplication.ctx).getAsString("uid"))) ;
			}else{
				messageJson.put("avatar", 
						""+ACache.get(this).getAsString("avatar"));
			}
		
			Log.e(TAG, "send text content : "+messageJson.toString());
			
			if (TextUtils.isEmpty(contString) == false) {
				if (isStateFine()) {
					msgAgent.createAndSendMsg(new MsgAgent.MsgBuilderHelper() {
						@Override
						public void specifyType(MsgBuilder msgBuilder) {
							msgBuilder.text(/*contString*/messageJson.toString());
							refreshData(""+messageJson.toString(), false,Msg.Type.Text);
							mEditTextContent.setText("");
						}
					}, sendCallback/*new DefaultSendCallback() {
						@Override
						public void onSuccess(Msg msg) {
							super.onSuccess(msg);
	//						Log.e(TAG, "txt --- "+msg.getContent());
	//						refreshData(contString, msgType,MSG_TEXT_TYPE);
	//						mEditTextContent.setText("");
						}
					}*/);
				}
			}
		
		} catch (Exception e) {
		} catch (OutOfMemoryError e) {
		}

	}

	/**
	 * 从图库获取图片
	 ******************************/
	public void selectPicFromLocal() {
//		Intent intent;
//		if (Build.VERSION.SDK_INT < 19) {
//			intent = new Intent(Intent.ACTION_GET_CONTENT);
//			intent.setType("image/*");
//
//		} else {
//			intent = new Intent(
//					Intent.ACTION_PICK,
//					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//		}
//		startActivityForResult(intent, REQUEST_CODE_LOCAL);

		//自定义
		Intent intent = new Intent(this,SelectorImageMainActivity.class);
		startActivityForResult(intent, REQUEST_CODE_LOCAL);
		overridePendingTransition(R.anim.activity_enter_from_right, R.anim.activity_exit_to_left);
		
		//系统
//		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
//			Intent intent = new Intent();
//			intent.setType("image/*");
//			intent.setAction(Intent.ACTION_GET_CONTENT);
//			startActivityForResult(
//					Intent.createChooser(intent,
//							getResources().getString(R.string.select_picture)),
//							REQUEST_CODE_LOCAL);
//		} else {
//			Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//			intent.addCategory(Intent.CATEGORY_OPENABLE);
//			intent.setType("image/*");
//			startActivityForResult(intent, REQUEST_CODE_LOCAL);
//		}
	}

	/**
	 * 照相获取图片
	 *******************************/
	public void selectPicFromCamera() {
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		Uri imageUri = Uri.fromFile(new File(localCameraPath));
		openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		startActivityForResult(openCameraIntent, TAKE_CAMERA_REQUEST);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(!SystemTool.checkNet(this)){
			AppToast.toastMsgCenter(this, getResources().getString(R.string.no_network)).show();
			return ;
		}
		if(MsgReceiver.isSessionPaused()){
			AppToast.toastMsgCenter(this, getResources().getString(R.string.send_fail)).show();
			return ;
		}
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case REQUEST_CODE_LOCAL:// 从本地上传图片
				if (data != null) {
					Uri selectedImage = data.getData();
					if (selectedImage != null) {
						sendPicByUri(selectedImage);
					}
				}
				
//				if (data == null) {
//					return;
//				}
//				Uri uri;
//				if (requestCode == REQUEST_CODE_LOCAL) {
//					uri = data.getData();
//				} else {
//					uri = data.getData();
//					final int takeFlags = data.getFlags()
//							& (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//					getContentResolver().takePersistableUriPermission(uri,
//							takeFlags);
//				}
//				String localSelectPath = ProviderPathUtils.getPath(ctx, uri);
//				sendImageByPath(localSelectPath);
				
				break;
			case TAKE_CAMERA_REQUEST:// 照相获取图片
				try {
					Log.e(TAG, "localCameraPath = " + localCameraPath);
					sendImageByPath(localCameraPath);
				} catch (Exception e) {
					Log.e(TAG, "TAKE_CAMERA_REQUEST ERROR = "+e.toString());
				} catch(OutOfMemoryError error){
					
				}
				
				break;
			}
		}
		// hideBottomLayout();
		super.onActivityResult(requestCode, resultCode, data);
	}

	// 发送图片
	private void sendImageByPath(String localSelectPath) {
		try {
			final String objectId = Utils.uuid();
			final String newPath = PathUtils.getChatFilePath(objectId);
			PhotoUtils.compressImage(localSelectPath, newPath);
			final JSONObject messageJson = new JSONObject();

			messageJson.put("uid", ""
					+ ACache.get(MyApplication.ctx).getAsString("uid"));
			messageJson.put("nickname", ""
					+ ACache.get(MyApplication.ctx).getAsString("nickname"));
			messageJson.put("content", newPath);
			messageJson.put("localpath", newPath);
			messageJson.put("objectId", "" + objectId);
			
			if(!SHARE_MEDIA.WEIXIN.equals(ACache.get(this).getAsString("type"))
					&&!SHARE_MEDIA.SINA.equals(ACache.get(this).getAsString("type"))
					&&!SHARE_MEDIA.QZONE.equals(ACache.get(this).getAsString("type"))){
				
				messageJson.put("avatar", HttpClientUtils.USER_IMAGE_URL
								+ StringUtil.getPathByUid("" + ACache.get(MyApplication.ctx).getAsString("uid"))) ;
			}else{
				messageJson.put("avatar", 
						""+ACache.get(this).getAsString("avatar"));
			}

			Log.e(TAG, "send image content : " + messageJson.toString());
			if (isStateFine()) {
				msgAgent.createAndSendMsg(new MsgAgent.MsgBuilderHelper() {
					@Override
					public void specifyType(MsgBuilder msgBuilder) {
						msgBuilder.image(messageJson.toString());
						refreshData("" + messageJson.toString(), false,
								Msg.Type.Image);
					}
				}, sendCallback);
			} 
		} catch (Exception e) {
			
		} catch (OutOfMemoryError e) {
			
		}
	}

	/**
	 * 根据图库图片uri发送图片
	 * 
	 * @param selectedImage
	 */
	private void sendPicByUri(Uri selectedImage) {
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
			Log.e(TAG, "picturePath = " + picturePath);
			sendImageByPath(picturePath);
		} else {
			File file = new File(selectedImage.getPath());
			if (!file.exists()) {
				AppToast.toastMsgCenter(getApplicationContext(), "找不到图片").show();
				return;

			}
			Log.e(TAG, "file.getAbsolutePath() = " + file.getAbsolutePath());
			sendImageByPath(file.getAbsolutePath());
		}

	}

	class DefaultSendCallback implements SendCallback {
		@Override
		public void onError(Exception e) {
			e.printStackTrace();
			//Utils.toast(e.getMessage());
			//loadMsgsFromDB(false);
			Message message = new Message() ;
			message.what = LeanCloudChatActivity.SEND_MSG_FAILED ;
			myHandler.sendMessage(message);
		}

		@Override
		public void onStart(Msg msg) {//开始发送消息
			//loadMsgsFromDB(false);
			//type[Text,Image]
		}

		@Override
		public void onSuccess(Msg msg) {
			Message message = new Message() ;
			message.what = LeanCloudChatActivity.SEND_MSG_SUCCESS ;
			myHandler.sendMessage(message);
			
			//loadMsgsFromDB(false);
			/*
			final JSONObject messageJson = new JSONObject();
			try {
				messageJson.put("uid", ""+ACache.get(MyApplication.ctx).getAsString("uid"));
				messageJson.put("nickname", ""+ACache.get(MyApplication.ctx).getAsString("nickname"));
				messageJson.put("content", ""+msg.getContent());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			Log.e(TAG, "type:"+msg.getType()+", and "+msg.getContent());//服务器图片地址
			if(Msg.Type.Image == msg.getType()){
				//refreshData(""+msg.getContent(), false,msg.getType());
			}else if(Msg.Type.Text == msg.getType()){
				//refreshData(""+msg.getContent(), false,msg.getType());
				//mEditTextContent.setText("");
			}*/
		}
	}

	private SendCallback sendCallback = new DefaultSendCallback();

	public boolean isStateFine() {
		if (Connectivity.isConnected(getApplicationContext()) == false) {
			AppToast.toastMsgCenter(getApplicationContext(), MyApplication.ctx.getString(R.string.pleaseCheckNetwork)).show();
			return false;
		} else if (MsgReceiver.isSessionPaused()) {
//			getApplicationContext().sendBroadcast(new Intent(RETRY_ACTION));
			try {
				//MsgReceiver.openSession(curUser);
			} catch (Exception e) {
				Log.e(TAG, "isStateFine exception " + e.toString() );
			} catch(OutOfMemoryError error){
				Log.e(TAG, "isStateFine error " + error );
			}
			
			//Utils.toast(getApplicationContext(), MyApplication.ctx.getString(R.string.sessionPausedTips));
			AppToast.toastMsgCenter(getApplicationContext(), MyApplication.ctx.getString(R.string.sessionPausedTips)).show();
			return false;
		} else {
			return true;
		}
	}

	private String getDate() {
		return format.format(new Date());
	}

	static boolean quitStatus = false;

	/*@SuppressWarnings("deprecation")
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//如果返回的时候
		if(emojiIconContainer.getVisibility() == View.VISIBLE){
			emojiIconContainer.setVisibility(View.GONE);
			iv_emoticons_normal.setVisibility(View.VISIBLE);
			iv_emoticons_checked.setVisibility(View.GONE);
			return true ;
		}
		
		else if(btnContainer.getVisibility() == View.VISIBLE){
			btnContainer.setVisibility(View.GONE);
			iv_emoticons_normal.setVisibility(View.VISIBLE);
			iv_emoticons_checked.setVisibility(View.GONE);
			return true ;
		}else if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
			//GroupMessageReceiver.removeMsgListener(this);
			//GroupMessageReceiver.removeMsgRecevierListener(this);
			quitGroup();
			finish();
			return true ;
		}
		
		//AppToast.toastMsgCenter(getApplicationContext(), "再按一次退出聊天室!").show();
		
		return super.onKeyDown(keyCode, event);

	}*/

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		// 如果返回的时候
		if (emojiIconContainer.getVisibility() == View.VISIBLE) {
			emojiIconContainer.setVisibility(View.GONE);
			iv_emoticons_normal.setVisibility(View.VISIBLE);
			iv_emoticons_checked.setVisibility(View.GONE);
			return ;
		}else if (btnContainer.getVisibility() == View.VISIBLE) {
			btnContainer.setVisibility(View.GONE);
			iv_emoticons_normal.setVisibility(View.VISIBLE);
			iv_emoticons_checked.setVisibility(View.GONE);
			return ;
		} else {
			//GroupMessageReceiver.removeMsgListener(this);
			//GroupMessageReceiver.removeMsgRecevierListener(this);
			quitGroup();
			finish();
		}
		super.onBackPressed();

	}
	
	public void quitGroup(){
		try {
			// 退出群组
			GroupMessageReceiver.removeMsgRecevierListener(this);
			group.quit();
			pageIndex = 1 ;
			//MsgReceiver.closeSession(curUser);
			try {
				MsgReceiver.setSessionPaused(true);
				//MsgReceiver.closeSession(MyApplication.CURRENT_USER);
			} catch (Exception e) {
				
			}
			finish();
		} catch (Exception e) {
			
		}
		

	}

	private Handler dataHandler = new Handler() {

		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if(msg.what == 100){
				new_msg_layout.setVisibility(View.INVISIBLE);
				mAdapter.refresh();
			}else{
				ChatMsgEntity entity = (ChatMsgEntity) msg.obj ;
				mDataArrays.add(entity);
				if(mDataArrays.size() <= 6 || !entity.isComMeg()){
					mAdapter.refresh();
					//new_msg_layout.setVisibility(View.INVISIBLE);
				}
				
				//接受到新的消息后显示新消息
				if(entity.isComMeg() && mDataArrays.size() > 6 ){
					new_msg_layout.setVisibility(View.VISIBLE);
				}
				//发送消息不显示新的消息
				else if(!entity.isComMeg() && mDataArrays.size() > 6 ){
					new_msg_layout.setVisibility(View.INVISIBLE);
				}
			}
			
			
			//由于消息刷新太快，不直接显示到底部
			//listView.getRefreshableView().setSelection(listView.getRefreshableView().getCount() - 1);
		}
	};

	public void refreshData(final String contString, final boolean msgType,final Msg.Type type) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				
				try {
					if(Msg.Type.Image == type && msgType){//图片并且接受消息,显示在左边
						final JSONObject messageJson = new JSONObject(contString);
						Log.e(TAG, "messageJson -- >"+messageJson.toString() );
						final JSONObject messagebody = new JSONObject(messageJson.optString("content"));
						if (contString.length() >= 0 && messagebody!= null ) {
							ChatMsgEntity entity = new ChatMsgEntity();
							entity.setDate(getDate());
							entity.setName(messagebody.optString("nickname"));
							entity.setUid(messagebody.optString("uid"));
							entity.setMsgType(msgType);
							entity.setText(messagebody.optString("content"));
							entity.setLocalpath(messageJson.optString("localpath"));
							entity.setAvatar(messagebody.optString("avatar"));
							entity.setType(type);
							Message msg = new Message();
							msg.obj = entity;
							msg.what = 101 ;
							dataHandler.sendMessage(msg);
						}
					}else if(Msg.Type.Image == type && !msgType){//图片并且接受消息显示在右边
						final JSONObject messageJson = new JSONObject(contString);
						if (contString.length() >= 0 && messageJson!= null ) {
							ChatMsgEntity entity = new ChatMsgEntity();
							entity.setDate(getDate());
							entity.setName(messageJson.optString("nickname"));
							entity.setUid(messageJson.optString("uid"));
							entity.setMsgType(msgType);
							entity.setLocalpath(messageJson.optString("localpath"));
							entity.setText(messageJson.optString("content"));
							entity.setType(type);
							entity.setAvatar(messageJson.optString("avatar"));
							Message msg = new Message();
							msg.obj = entity;
							msg.what = 101 ;
							dataHandler.sendMessage(msg);
						}
					}else if(Msg.Type.Text == type && msgType){//txt left
						final JSONObject messageJson = new JSONObject(contString);
						Log.e(TAG, "messageJson -- >"+messageJson.toString() );
						final JSONObject messagebody = new JSONObject(messageJson.optString("content"));
						if (contString.length() >= 0 && messagebody != null) {
							ChatMsgEntity entity = new ChatMsgEntity();
							entity.setDate(getDate());
							entity.setName(messagebody.optString("nickname"));
							entity.setUid(messagebody.optString("uid"));
							entity.setMsgType(msgType);
							entity.setText(messagebody.optString("content"));
							entity.setType(type);
							entity.setAvatar(messagebody.optString("avatar"));
							Message msg = new Message();
							msg.obj = entity;
							msg.what = 101 ;
							dataHandler.sendMessage(msg);
						}
					}else if(Msg.Type.Text == type && !msgType){
						final JSONObject messageJson = new JSONObject(contString);
						
						if (contString.length() >= 0 && messageJson != null) {
							ChatMsgEntity entity = new ChatMsgEntity();
							entity.setDate(getDate());
							entity.setName(messageJson.optString("nickname"));
							entity.setUid(messageJson.optString("uid"));
							entity.setMsgType(msgType);
							entity.setText(messageJson.optString("content"));
							entity.setType(type);
							entity.setAvatar(messageJson.optString("avatar"));
							Message msg = new Message();
							msg.obj = entity;
							msg.what = 101 ;
							dataHandler.sendMessage(msg);
						}
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				

			}
		}).start();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onResume() {
		super.onResume();
		try {
			Log.e(TAG, "onResume ......");
			handler.postDelayed(runnable, 1000);
			////MsgReceiver.openSession(curUser);
			if(!SystemTool.checkNet(getApplicationContext())){
				AppToast.toastMsgCenter(getApplicationContext(), R.string.ERROR_404).show();
			}
			
			GroupMessageReceiver.addMsgRecevierListener(this);
			if(!SystemTool.checkNet(this)){
				AppToast.toastMsgCenter(this, getResources().getString(R.string.no_network)).show();
				return ;
			}
			if( MyApplication.CURRENT_USER == null || MsgReceiver.isSessionPaused()){
				AppToast.toastMsgCenter(this,"会话断开，即将重新连接!").show();
			}
			if(!isLoginSuccess || MsgReceiver.isSessionPaused()){
			    String Uid = ACache.get(this).getAsString("uid");
				AVUser.logInInBackground(Uid, MyApplication.PWD, new LogInCallback() {
				    public void done(AVUser user, AVException e) {
				        if (user != null) {
				            // 登录成功
				        	Log.e(TAG,"NinePointFrag  登录成功.........");
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
			
		} catch (OutOfMemoryError e) {
			
		}
		
		
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.e(TAG, "onPause ......");
		//GroupMessageReceiver.removeMsgListener(this);
		//GroupMessageReceiver.removeMsgRecevierListener(this);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		//GroupMessageReceiver.removeMsgRecevierListener(this);
	}
	
	//接受发送消息回调
	@Override
	public void onMessageReceiver(Context context, Group group,
			AVMessage avMsg) {
		try {
			final Msg msg = Msg.fromAVMessage(avMsg);
			Log.e(TAG, avMsg.getMessage());
			//接受的消息类型
			if(Msg.Type.Image == msg.getType()){
				refreshData(avMsg.getMessage(), true,msg.getType());
			}else if(Msg.Type.Text == msg.getType()){
				refreshData(avMsg.getMessage(), true,msg.getType());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	
	}


}

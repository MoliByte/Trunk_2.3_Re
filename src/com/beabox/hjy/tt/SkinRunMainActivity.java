package com.beabox.hjy.tt;

import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.app.base.entity.PushEntity;
import com.app.base.entity.UpdateInfo;
import com.app.base.entity.UserEntity;
import com.app.base.init.ACache;
import com.app.base.init.AppBaseUtil;
import com.app.base.init.MyApplication;
import com.app.iview.IDownloadView;
import com.app.presenter.DownloadPresenter;
import com.app.service.GetMyMessageCountService;
import com.app.service.JudgeUserInfoService;
import com.app.service.LoginAsynTaskService;
import com.avos.avoscloud.LogUtil.log;
import com.base.app.utils.EasyLog;
import com.base.app.utils.GetUserInfoUtil;
import com.base.app.utils.HomeTag;
import com.base.app.utils.KVOEvents;
import com.base.app.utils.PushNumUtil;
import com.base.dialog.lib.Effectstype;
import com.base.dialog.lib.NiftyDialogBuilder;
import com.base.service.action.constant.HttpTagConstantUtils;
import com.base.service.impl.HttpAysnResultInterface;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.main.skintest.component.KVO.Observer;
import com.daimajia.numberprogressbar.NumberProgressBar;
import com.daimajia.numberprogressbar.OnProgressBarListener;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;
import com.koushikdutta.ion.builder.Builders.Any.B;
import com.skinrun.trunk.facial.mask.test.FacialmaskEntryActivity;
import com.skinrun.trunk.main.ActivityFrag;
import com.skinrun.trunk.main.MainFrag;
import com.skinrun.trunk.main.MineFrag;
import com.skinrun.trunk.main.NewMainFragment;
import com.skinrun.trunk.main.NinePointFrag;
import com.skinrun.trunk.main.ProductionFrag;
import com.skinrun.trunk.main.TestingFrag;
import com.skinrun.trunk.test.popuwindow.ChooseTestPopuWindow;
import com.skinrun.trunk.test.popuwindow.ChooseTestPopuWindow.OnChooseClickListener;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengRegistrar;


public class SkinRunMainActivity extends FragmentActivity implements
		android.view.View.OnClickListener,HttpAysnResultInterface, OnCheckedChangeListener, Observer,IDownloadView ,OnProgressBarListener{
	
	private RadioGroup radioGroupHome;
	private ChooseTestPopuWindow chooseTestPopuWindow;
	private int currentIndex,clickIndex;
	private ArrayList<Fragment> frags;
	private RadioButton rabTest;
	private boolean resumeTag=false;
	private View popupWindowBack;
	private String show_test_type;
	
	private String TAG="SkinRunMainActivity";
	private int defalutIndex;
	
	private NotificationManager notificationManager;
	private NotificationCompat.Builder ntfBuilder;
	private static final int COMPLETE_DOWNLOAD_APK = 0x2;
	private static final int DOWNLOAD_NOTIFICATION_ID = 0x3;
	private static final int INSTALL_APK = 0x4;
	
	private DownloadPresenter downloadPresenter ;
	static NiftyDialogBuilder dialogUploadImage ;
	static NumberProgressBar number_progress_bar ;
	
	static String APK_PATH = "" ;
	Object downloadGroup = new Object();
	
	static boolean isDownLoading = false ;
	static boolean isForced = false ;
	static boolean isInstalling = false ;
	final static int Down_Pro = 100 ;
	
	private String Token="";
	
	Handler handler = new Handler(){

		@Override
		public void dispatchMessage(Message msg) {
			switch (msg.what) {
			case Down_Pro:
				//content.setText((String)msg.obj+"");
				//NumberProgressBar number_progress_bar = (NumberProgressBar)msg.obj ;
				long downloaded = msg.getData().getLong("downloaded");
				long total = msg.getData().getLong("total");
				
				if(null != number_progress_bar){
					number_progress_bar.incrementProgressBy(1);
			 	    number_progress_bar.setProgress(msg.arg1);
			 	    number_progress_bar.setMax(msg.arg2);
				}
				EasyLog.e(downloaded+" / "+total);
				if(msg.arg1 == 100 && isForced && !isInstalling && (downloaded == total)){
					isInstalling = true ;
					if(null != dialogUploadImage){
						dialogUploadImage.dismiss();
						//dialogUploadImage = null ;
					}
					installApkDialog(Uri.parse("file://" + APK_PATH));
				}
				
				else if((downloaded == total)){
					installApk(Uri.parse("file://" + APK_PATH));
				}
				
		 	   	//EasyLog.e(msg.arg1+","+msg.arg2);
		 	   	showDownloadNotificationUI(msg.arg1);
			break;
			case COMPLETE_DOWNLOAD_APK:
					if (ntfBuilder == null) {
						ntfBuilder = new NotificationCompat.Builder(getApplicationContext());
					}
					ntfBuilder.setSmallIcon(getApplicationInfo().icon)
							.setContentTitle(getResources().getString(R.string.app_name))
							.setContentText("下载完成，点击安装").setTicker("任务下载完成");
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setDataAndType(
							Uri.parse("file://" + APK_PATH),
							"application/vnd.android.package-archive");
					PendingIntent pendingIntent = PendingIntent.getActivity(
							SkinRunMainActivity.this, 0, intent, 0);
					ntfBuilder.setContentIntent(pendingIntent);
					if (notificationManager == null) {
						notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
					}
					notificationManager.notify(DOWNLOAD_NOTIFICATION_ID,
							ntfBuilder.build());
				break;
			case INSTALL_APK:
				//installApk(Uri.parse("file://" + APK_PATH));
				//installApkDialogByCancel(Uri.parse("file://" + APK_PATH));
				break;

			default:
				break;
			}
			super.dispatchMessage(msg);
		}
	};
	
	//肌肤测试or面膜测试的标志
	private String TestType="";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		MyApplication.getInstance().addActivity(this);
		MyApplication.getInstance().getKvo().registerObserver(KVOEvents.GET_PUSH_NUM, this);
		MyApplication.getInstance().getKvo().registerObserver(KVOEvents.GET_MYMESSAGE_COUNT, this);
		MyApplication.getInstance().getKvo().registerObserver(KVOEvents.ACTION_AFTER_INFO, this);
		
		PushAgent mPushAgent = PushAgent.getInstance(getApplicationContext());
		mPushAgent.enable();
		String device_token = UmengRegistrar.getRegistrationId(getApplicationContext());
		//Log.e("SkinRunMainActivity", "==========Device Token:"+device_token);
		//启动本地通知服务
		startService(new Intent(this, LocalPushService.class));
		
		
		try{
			Bundle bun = getIntent().getExtras();
			if(bun!=null){
				defalutIndex=Integer.parseInt(bun.getString("defalutIndex"));
				log.e(TAG, "======defalutIndex:"+defalutIndex);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		log.e(TAG, "======defalutIndex:"+defalutIndex);
		EasyLog.e("==========Device Token:"+device_token);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_version_two);
		popupWindowBack=findViewById(R.id.popup_back);
		
		/**
		UpdateHelper updateHelper = new UpdateHelper.Builder(this)
        .checkUrl(MyApplication.UPDATE_URL)
        .isAutoInstall(true) //设置为false需在下载完手动点击安装;默认值为true，下载后自动安装。
        .isHintNewVersion(false)//是否hint提示
        .build();
		updateHelper.check();
		//autoLogin();
		 */
		
		
		setupView();
		initPopupWindow();
		show_test_type=getResources().getString(R.string.show_test_type);
		
		/**下载更新***/
		downloadPresenter = new DownloadPresenter(this);
		downloadPresenter.download(getApplicationContext());
		
	}
	//初始化推送的结果
	private void initPush(){
		try{
			Token = ACache.get(this).getAsString("Token");
			
			if(Token!=null){
				PushEntity pushEntity=PushNumUtil.get(""+Token);
				if(pushEntity!=null){
					if(pushEntity.getMyMessage()+pushEntity.getMyTestMessage()>0){
						RadioButton r=(RadioButton) radioGroupHome.getChildAt(4);
						r.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.my_has_message), null, null);
					}else{
						RadioButton r=(RadioButton) radioGroupHome.getChildAt(4);
						
						r.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.radio_botton_mine), null, null);
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void initPopupWindow() {
		chooseTestPopuWindow = new ChooseTestPopuWindow();
		chooseTestPopuWindow
				.setOnChooseClickListener(new OnChooseClickListener() {

					@Override
					public void onChooseSkinTest() {
						chooseTestPopuWindow.dismiss();
						popupWindowBack.setVisibility(View.INVISIBLE);
						TestType=HomeTag.SKIN_TEST;
						isNeedCompleteUserinfo();
					}

					@Override
					public void onChooseNone() {
						chooseTestPopuWindow.dismiss();
						popupWindowBack.setVisibility(View.INVISIBLE);
						RadioButton rb=(RadioButton)radioGroupHome.getChildAt(currentIndex);
						rb.setChecked(true);
					}

					@Override
					public void onChooseFacialTest() {//面膜测试
						chooseTestPopuWindow.dismiss();
						popupWindowBack.setVisibility(View.INVISIBLE);
						TestType=HomeTag.FACE_MARK;
						isNeedCompleteUserinfo();
						
					}
				});
	}   

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
	}
	
	public void autoLogin(){
		try {
			String username_ = ACache.get(getApplicationContext()).getAsString("username")==null?"":ACache.get(getApplicationContext()).getAsString("username");
			String password_ = ACache.get(getApplicationContext()).getAsString("password")==null?"":ACache.get(getApplicationContext()).getAsString("password");
			new LoginAsynTaskService(this, HttpTagConstantUtils.LOGIN_TAG,
					this).doLogin(username_, password_);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 是否需要完善资料
	 * @return
	 */
	private void isNeedCompleteUserinfo(){
		UserEntity user=GetUserInfoUtil.getUserEntity();
		
		if(user!=null&&user.getToken()!=null){
			new JudgeUserInfoService(this, HttpTagConstantUtils.JUDGE_USERINFO, this).doGet(""+user.getToken());
		}
	}
               
	// 提示退出
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (((keyCode == KeyEvent.KEYCODE_BACK))) {
			dialog_Exit(this);
			return true;
		}
		return false;
	}
	
	public static void dialog_Exit(Context context) {

		final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder
				.getInstance(context);// new NiftyDialogBuilder(context);

		dialogBuilder.withTitle(null).withMessage(null)
				.withMessageColor("#FFFFFFFF")
				.isCancelableOnTouchOutside(false).withDuration(200)
				.withEffect(Effectstype.Fadein).withButton1Text("确定")
				.withButton2Text("取消")
				.setCustomView(R.layout.dialog_with_two_button, context)
				.setButton1Click(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						dialogBuilder.dismiss();
						MyApplication.getInstance().exit();

					}
				}).setButton2Click(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						dialogBuilder.dismiss();

					}
				}).show();

	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btn_test:
			
			if(!isLogin()){
				RadioButton rb=(RadioButton)radioGroupHome.getChildAt(4);
				rb.setChecked(true);
			}else{
				if(show_test_type.equals("only_skin")){
					goToFrag(2);
					
					
				}else if(show_test_type.equals("both_two")){
					log.e(TAG, "=========onClick  both_two");
					
					chooseTestPopuWindow.show(this);
					popupWindowBack.setVisibility(View.VISIBLE);
					
					
				}else if(show_test_type.equals("only_facial_mask")){
					resumeTag=true;
					Intent intent = new Intent(SkinRunMainActivity.this,
							FacialmaskEntryActivity.class);
					startActivity(intent);
					overridePendingTransition(
							R.anim.activity_enter_from_right,
							R.anim.activity_exit_to_left);
				}
			}
			break;
		}
	}
	
	private void getMyMessage(String token){
		new GetMyMessageCountService(this, HttpTagConstantUtils.GET_MESSAGE_NOCOUNT, this).doGet(token);
	}
	protected void onResume() {
		super.onResume();
		
		RadioButton rb=(RadioButton) radioGroupHome.getChildAt(currentIndex);
		rb.setChecked(true);
		resumeTag=false;
		
		initPush();
		Token = ACache.get(this).getAsString("Token");
		if(Token!=null){
			getMyMessage(""+Token);
		}
		
	}
	private void goToFrag(int index){
		FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
		trx.hide(frags.get(currentIndex));
		if(!frags.get(index).isAdded()){
			trx.add(R.id.fragment_container, frags.get(index));
		}
		trx.show(frags.get(index)).commit();
		currentIndex=index;
	}

	public void setupView() {
		
		radioGroupHome=(RadioGroup)findViewById(R.id.radioGroupHome);
		
		frags=new ArrayList<Fragment>();
		String home_on = getResources().getString(R.string.home_switch);
		if("home_on".equals(home_on)){
			frags.add(new NewMainFragment());
		}else{
			frags.add(new MainFrag());
		}
		
		frags.add(new ActivityFrag());
		frags.add(new TestingFrag());
		
		if("on".equals(getResources().getString(R.string.production_or_pmg9))){
			frags.add(new NinePointFrag());
		}else{
			frags.add(new ProductionFrag());
		}
		
		
		frags.add(new MineFrag());
		
		FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
		log.e(TAG, "======defalutIndex:::::"+defalutIndex);
		trx.add(R.id.fragment_container, frags.get(0)).show(frags.get(0)).commit();
		radioGroupHome.setOnCheckedChangeListener(this);
		rabTest=(RadioButton)findViewById(R.id.btn_test);
		rabTest.setOnClickListener(this);
		
		
		if(defalutIndex==3){
			RadioButton rb=(RadioButton)radioGroupHome.getChildAt(3);
			rb.setChecked(true);
		}
		
	}
	
	
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	private boolean isLogin(){
		String Uid = ACache.get(this).getAsString("uid");
		Token = ACache.get(this).getAsString("Token");
		if(Uid == null || Token == null || "".equals(Token) || "".equals(Uid)){
			return false;
		}
		return true;
	}

	@Override
	public void dataCallBack(Object tag, int statusCode, Object result) {
		try{
			
			switch((Integer)tag){
			case HttpTagConstantUtils.GET_MESSAGE_NOCOUNT:
				if(statusCode==200||statusCode==201){
					int myMsgCount=(Integer) result;
					Token = ACache.get(this).getAsString("Token");
					
					if(Token!=null){
						PushEntity p=PushNumUtil.get(""+Token);
						if(p==null){
							if(myMsgCount>0){
								RadioButton r=(RadioButton) radioGroupHome.getChildAt(4);
								
								r.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.my_has_message), null, null);
							}else{
								RadioButton r=(RadioButton) radioGroupHome.getChildAt(4);
								
								r.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.radio_botton_mine), null, null);
							}
						}else{
							if(myMsgCount>0||p.getMyMessage()>0||p.getMyTestMessage()>0){
								RadioButton r=(RadioButton) radioGroupHome.getChildAt(4);
								
								r.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.my_has_message), null, null);
							}else{
								RadioButton r=(RadioButton) radioGroupHome.getChildAt(4);
								
								r.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.radio_botton_mine), null, null);
							}
						}
					}
				}
				
				break;
			case HttpTagConstantUtils.JUDGE_USERINFO:
				String message=(String)result;
				log.e(TAG, "============是否完善资料返回："+message);
				
				if(message.equals("1")){
					if(TestType.equals(HomeTag.FACE_MARK)){
						chooseTestPopuWindow.dismiss();
						resumeTag=true;
						popupWindowBack.setVisibility(View.INVISIBLE);
						Intent intent = new Intent(SkinRunMainActivity.this,FacialmaskEntryActivity.class);
						startActivity(intent);
						overridePendingTransition(R.anim.activity_enter_from_right,R.anim.activity_exit_to_left);
						
					}else if(TestType.equals(HomeTag.SKIN_TEST)){
						goToFrag(2);
						chooseTestPopuWindow.dismiss();
						popupWindowBack.setVisibility(View.INVISIBLE);
						log.e("SkinRunMainActivity", "==============点击肌肤测试");
					}
					
				}else{
					AppToast.toastMsg(this, "亲爱的小主：肌肤测试要有完整的个人资料才能进行哟~").show();
					Intent intent = new Intent(this, UpdateBaseInfoActivity.class);
					intent.putExtra("ACTION_TYPE", TestType);
					
					startActivity(intent);
					overridePendingTransition(R.anim.activity_enter_from_right,R.anim.activity_exit_to_left);
				}
				break;
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
	}
	
	@Override
	public void onCheckedChanged(RadioGroup arg0, int arg1) {
		if(!resumeTag){
			switch(arg1){
			case R.id.btn_home:
				clickIndex=0;
				goToFrag(clickIndex);
				break;
				
			case R.id.btn_activity:
				clickIndex=1;
				goToFrag(clickIndex);
				break;
				
			case R.id.btn_nine_clock:
				clickIndex=3;
				goToFrag(clickIndex);
				break;
				
			case R.id.btn_mine:
				clickIndex=4;
				goToFrag(clickIndex);
				break;	
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		MyApplication.getInstance().getKvo().removeObserver(KVOEvents.GET_PUSH_NUM, this);
		MyApplication.getInstance().getKvo().removeObserver(KVOEvents.GET_MYMESSAGE_COUNT, this);
		MyApplication.getInstance().getKvo().removeObserver(KVOEvents.ACTION_AFTER_INFO, this);
	}
	
	@Override
	public void onEvent(String event, Object... args) {
		
		try{

			if(event.equals(KVOEvents.ACTION_AFTER_INFO)){
				log.e(TAG, "============完善信息观察者");
				
				/*
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						currentIndex=2;
						goToFrag(2);
						RadioButton rb=(RadioButton) radioGroupHome.getChildAt(currentIndex);
						rb.setChecked(true);
						
					}
				}, 1000);*/
				
			}else if(event.equals(KVOEvents.GET_PUSH_NUM)){
				PushEntity pushEntity=(PushEntity)args[0];
				if(pushEntity==null){
					return;
				}
				
				log.e("SkinRunMainActivity", "============收到的推送值："+pushEntity.getMyMessage()+"   "+pushEntity.getMyMessage()+"  "+pushEntity.getToken());
				if(pushEntity.getMyMessage()+pushEntity.getMyTestMessage()>0){
					RadioButton r=(RadioButton) radioGroupHome.getChildAt(4);
					
					r.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.my_has_message), null, null);
				}else{
					RadioButton r=(RadioButton) radioGroupHome.getChildAt(4);
					
					r.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.radio_botton_mine), null, null);
				}
			}else if(event.equals(KVOEvents.GET_MYMESSAGE_COUNT)){
				int myMsgCount=(Integer) args[0];
				Token = ACache.get(this).getAsString("Token");
				log.e("SkinRunMainActivity", "============传递来的未读我的消息"+myMsgCount);
				
				if(Token!=null){
					PushEntity p=PushNumUtil.get(""+Token);
					if(p==null){
						if(myMsgCount>0){
							RadioButton r=(RadioButton) radioGroupHome.getChildAt(4);
							
							r.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.my_has_message), null, null);
						}else{
							RadioButton r=(RadioButton) radioGroupHome.getChildAt(4);
							
							r.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.radio_botton_mine), null, null);
						}
					}else{
						if(myMsgCount>0||p.getMyMessage()>0||p.getMyTestMessage()>0){
							RadioButton r=(RadioButton) radioGroupHome.getChildAt(4);
							
							r.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.my_has_message), null, null);
						}else{
							RadioButton r=(RadioButton) radioGroupHome.getChildAt(4);
							
							r.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.radio_botton_mine), null, null);
						}
					}
					
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	/**下载更新***/
	@Override
	public void download(UpdateInfo modelInfo) {
		try {
			if (modelInfo != null) {
				if (Integer.parseInt(modelInfo.getVersionCode()) > getPackageInfo().versionCode) {
					showUpdateUI(modelInfo);
				} else {
					//主页不提示
					//AppToast.toastMsgCenter(this, "当前已是最新版").show();
				}
			}
		} catch (Exception e) {
			
		}
		
	}
	
	/**
	 * 获取当前app版本
	 * 
	 * @return
	 * @throws android.content.pm.PackageManager.NameNotFoundException
	 */
	private PackageInfo getPackageInfo() {
		PackageInfo pinfo = null;
		if (this != null) {
			try {
				pinfo = this.getPackageManager().getPackageInfo(
						this.getPackageName(), 0);
			} catch (PackageManager.NameNotFoundException e) {
				e.printStackTrace();
			}
		}
		return pinfo;
	}
	
	/**
	 * 弹出提示更新窗口
	 * 
	 * @param updateInfo
	 */
	private void showUpdateUI(final UpdateInfo updateInfo) {
		String downloadPathString = AppBaseUtil.APK_DOWNLOAD + File.separator + "trunk_"+updateInfo.getVersionName()+".apk"; 
		APK_PATH = downloadPathString ;
		
		if(new File(downloadPathString).exists()){
			installApkDialogByCancel(Uri.parse("file://" + APK_PATH));
		}else{
			dialog_update(updateInfo);
		}
	}
	
	public  void dialog_update(final UpdateInfo updateInfo) {
		
		dialogUploadImage = NiftyDialogBuilder.getInstance(this);
		final View view = LayoutInflater.from(this).inflate(R.layout.update_dialog_with_two_button, null);
		final TextView t = (TextView) view.findViewById(R.id.text);
		number_progress_bar = (NumberProgressBar) view.findViewById(R.id.number_progress_bar);
		number_progress_bar.setOnProgressBarListener(this);
		String changeLog = updateInfo.getChangeLog().replaceAll("\\\\n", "\n");
		t.setText(changeLog);
		if(!"".equals(updateInfo.getIs_force()) && "1".equals(updateInfo.getIs_force())){
			dialogUploadImage.setCanceledOnTouchOutside(false);
			dialogUploadImage.setCancelable(false);
			dialogUploadImage.withTitle(null).withMessage(null)
					.withMessageColor("#FFFFFFFF")
					.isCancelableOnTouchOutside(false).withDuration(100)
					.withEffect(Effectstype.Fadein).withButton1Text("确定")
					//.withButton2Text("取消")
					.setCustomView(view, this)
					
					.setButton1Click(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							if(null != dialogUploadImage){
								dialogUploadImage.dismiss();
							}
							isForced = true ;
							if(!isDownLoading){
								isDownLoading = true ;
								//t.setVisibility(View.GONE);
								//number_progress_bar.setVisibility(View.VISIBLE);
								downloadCanceled(updateInfo);
								
								
							}else{
								AppToast.toastMsgCenter(getApplicationContext(), "下载中...").show();
							}
							
							
							
						}
					}).show();
		}else{
			dialogUploadImage.setCanceledOnTouchOutside(false);
			dialogUploadImage.setCancelable(false);
			dialogUploadImage.withTitle(null).withMessage(null)
			.withMessageColor("#FFFFFFFF")
			.isCancelableOnTouchOutside(false).withDuration(100)
			.withEffect(Effectstype.Fadein).withButton1Text("确定")
			.withButton2Text("取消")
			.setCustomView(view, this)
			.setButton1Click(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					dialogUploadImage.dismiss();
					if(!isDownLoading){
						isDownLoading = true ;
						t.setVisibility(View.GONE);
						number_progress_bar.setVisibility(View.VISIBLE);
						//downloadApk(updateInfo, number_progress_bar);
						downloadCanceled(updateInfo);
					}else{
						AppToast.toastMsgCenter(getApplicationContext(), "下载中...").show();
					}
					

				}
			}).setButton2Click(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					isDownLoading = false ;
					if(null != dialogUploadImage){
						dialogUploadImage.dismiss();
					}
					//Ion.getDefault(SkinRunMainActivity.this).cancelAll(downloadGroup);
					//Ion.getDefault(SkinRunMainActivity.this).cancelAll(SkinRunMainActivity.this);

				}
			}).show();
		}

	}
	
	public void downloadCanceled(final UpdateInfo updateInfo){
		if(null != dialogUploadImage){
			dialogUploadImage.dismiss();
		}
		dialogUploadImage = NiftyDialogBuilder.getInstance(this);
		final View view = LayoutInflater.from(this).inflate(R.layout.update_dialog_with_two_button, null);
		final TextView t = (TextView) view.findViewById(R.id.text);
		number_progress_bar = (NumberProgressBar) view.findViewById(R.id.number_progress_bar);
		t.setVisibility(View.GONE);
		number_progress_bar.setVisibility(View.VISIBLE);
		number_progress_bar.setOnProgressBarListener(this);
		downloadApk(updateInfo, number_progress_bar);
		dialogUploadImage.setCanceledOnTouchOutside(false);
		dialogUploadImage.setCancelable(false);
		dialogUploadImage.withTitle(null).withMessage(null)
				.withMessageColor("#FFFFFFFF")
				.isCancelableOnTouchOutside(false).withDuration(100)
				.withEffect(Effectstype.Fadein).withButton1Text("取消")
				//.withButton2Text("取消")
				.setCustomView(view, this)
				
				.setButton1Click(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						if(!isForced){
							if(null != dialogUploadImage){
								dialogUploadImage.dismiss();
							}
							Ion.getDefault(SkinRunMainActivity.this).cancelAll(downloadGroup);
						}else{
							Ion.getDefault(SkinRunMainActivity.this).cancelAll(downloadGroup);
							MyApplication.getInstance().exit();
						}
						
						
					}
				}).show();
	}
	
	public void installApkDialog(final Uri data){
		dialogUploadImage = NiftyDialogBuilder.getInstance(this);
		final View view = LayoutInflater.from(this).inflate(R.layout.update_dialog_with_two_button, null);
		final TextView t = (TextView) view.findViewById(R.id.text);
		number_progress_bar = (NumberProgressBar) view.findViewById(R.id.number_progress_bar);
		//t.setVisibility(View.GONE);
		t.setText("下载完成请安装!");
		number_progress_bar.setProgress(100);
		number_progress_bar.setVisibility(View.GONE);
		number_progress_bar.setOnProgressBarListener(this);
		dialogUploadImage.setCanceledOnTouchOutside(false);
		dialogUploadImage.setCancelable(false);
		dialogUploadImage.withTitle(null).withMessage(null)
		.withMessageColor("#FFFFFFFF")
		.isCancelableOnTouchOutside(false).withDuration(100)
		.withEffect(Effectstype.Fadein).withButton1Text("安装")
		.setCustomView(view, this)
		
		.setButton1Click(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setDataAndType(data, "application/vnd.android.package-archive");
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				SkinRunMainActivity.this.startActivity(i);
			}
		}).show();
	}
	
	public void installApkDialogByCancel(final Uri data){
		dialogUploadImage = NiftyDialogBuilder.getInstance(this);
		final View view = LayoutInflater.from(this).inflate(R.layout.update_dialog_with_two_button, null);
		final TextView t = (TextView) view.findViewById(R.id.text);
		number_progress_bar = (NumberProgressBar) view.findViewById(R.id.number_progress_bar);
		//t.setVisibility(View.GONE);
		t.setText("下载完成请安装!");
		number_progress_bar.setProgress(100);
		number_progress_bar.setVisibility(View.GONE);
		number_progress_bar.setOnProgressBarListener(this);
		dialogUploadImage.setCanceledOnTouchOutside(false);
		dialogUploadImage.setCancelable(false);
		dialogUploadImage.withTitle(null).withMessage(null)
		.withMessageColor("#FFFFFFFF")
		.isCancelableOnTouchOutside(false).withDuration(100)
		.withEffect(Effectstype.Fadein).withButton1Text("安装")
		.withEffect(Effectstype.Fadein).withButton2Text("取消")
		
		.setCustomView(view, this)
		
		.setButton1Click(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(dialogUploadImage!=null){
					dialogUploadImage.dismiss();
				}
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setDataAndType(data, "application/vnd.android.package-archive");
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				SkinRunMainActivity.this.startActivity(i);
			}
		}).setButton2Click(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(dialogUploadImage!=null){
					dialogUploadImage.dismiss();
				}
			}
		}).show();
	}
	
	
	/**确认开始下载*/
	public void downloadApk(UpdateInfo modelInfo,final NumberProgressBar number_progress_bar ){
		String downloadPathString = AppBaseUtil.APK_DOWNLOAD + File.separator + "trunk_"+modelInfo.getVersionName()+".apk"; 
		APK_PATH = downloadPathString ;
		try {
			if(!new File(downloadPathString).exists()){
				File[] files = new File(AppBaseUtil.APK_DOWNLOAD + File.separator).listFiles() ;
				for (int i = 0; i < files.length; i++) {
					if(!downloadPathString.equals(files[i].getAbsolutePath())){
						files[i].delete();
					}
				}
				//new File(downloadPathString).delete();
			}
		} catch (Exception e) {
			
		}
		
		B b = Ion.with(getApplicationContext())
        .load(modelInfo.getApkUrl());//
		b.group(downloadGroup) ;
	     b.progress(new ProgressCallback() {
	    	@Override
	        public void onProgress(long downloaded, long total) {
	    		String result="0%";//接受百分比的值
	 		    NumberFormat format = NumberFormat.getPercentInstance();// 获取格式化类实例 
	 		    NumberFormat.getIntegerInstance();
	 	        format.setMinimumFractionDigits(0);// 设置小数位 
	 	        result = format.format((double)downloaded/(double)total);
	 	        result = (result == null || "".equals(result)) ?"0%":result ;
	    		Message msgMessage = new Message() ;
	    		msgMessage.what = Down_Pro ;
	    		msgMessage.obj = number_progress_bar ;
	    		msgMessage.arg1 =  Integer.valueOf(result.replaceAll("%", ""));
	    		msgMessage.arg2 = 100 ;//(int)total ;
	    		Bundle bundle = new Bundle();
	    		bundle.putLong("downloaded", downloaded);
	    		bundle.putLong("total", total);
	    		msgMessage.setData(bundle);
	    		handler.sendMessageDelayed(msgMessage,1000);
	        }
	     })
	     .write(new File(downloadPathString))
	     
	     .setCallback(new FutureCallback<File>() {
	        @Override
	         public void onCompleted(Exception e, File file) {
	        	if(e != null){
					EasyLog.e(e.toString());
				}
	        	//Toast.makeText(getApplicationContext(), "下载完成", 2000).show();
	         }
	     });
	}
	
	@Override
	public void onProgressChange(int current, int max) {
		if(current == max){
			if(null != dialogUploadImage){
				dialogUploadImage.dismiss();
			}
			//EasyLog.e(current+"=========="+max);
			//installApk(Uri.parse("file://" + APK_PATH));
			
			
			if(!isForced){
				//number_progress_bar.setVisibility(View.GONE);
				isDownLoading = false ;
				//installApk(Uri.parse("file://" + APK_PATH));
				
			}else{
				/*if(null != dialogUploadImage){
				dialogUploadImage.dismiss();
				//dialogUploadImage = null ;
				}*/
				//installApkDialog(Uri.parse("file://" + APK_PATH));
			}
			handler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					Message msgMessage = new Message() ;
					msgMessage.what = INSTALL_APK ;
					//handler.obtainMessage(INSTALL_APK).sendToTarget();
					handler.sendMessage(msgMessage);
					
				}
			},1000);
			
			
		}
		
		
	}
	
	private void installApk(Uri data) {
		
		if (!isForced) {
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setDataAndType(Uri.parse("file://" + APK_PATH), "application/vnd.android.package-archive");
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			this.startActivity(i);
		} else {
			
			//AppToast.toastMsgCenter(getApplicationContext(), "安装失败!").show();
			
			//Log.e("NullPointerException", "The context must not be null.");
			//MyApplication.getInstance().exit();
			
		}

	}
	
	/**
	 * 通知栏弹出下载提示进度
	 * 
	 * @param updateInfo
	 * @param progress
	 */
	private void showDownloadNotificationUI(
			final int progress) {
		if (this != null) {
			String contentText = new StringBuffer().append(progress)
					.append("%").toString();
			PendingIntent contentIntent = PendingIntent.getActivity(this,
					0, new Intent(), PendingIntent.FLAG_CANCEL_CURRENT);
			if (notificationManager == null) {
				notificationManager = (NotificationManager) this
						.getSystemService(Context.NOTIFICATION_SERVICE);
			}
			if (ntfBuilder == null) {
				ntfBuilder = new NotificationCompat.Builder(this)
						.setSmallIcon(this.getApplicationInfo().icon)
						.setTicker("开始下载...")
						.setContentTitle(getResources().getString(R.string.app_name))
						.setContentIntent(contentIntent);
			}
			ntfBuilder.setContentText(contentText);
			ntfBuilder.setProgress(100, progress, false);
			notificationManager.notify(DOWNLOAD_NOTIFICATION_ID,
					ntfBuilder.build());
		}
	}
}

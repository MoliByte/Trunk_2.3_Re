package com.beabox.hjy.tt;

import java.io.File;
import java.text.NumberFormat;
import java.util.List;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.base.entity.UpdateInfo;
import com.app.base.init.AppBaseUtil;
import com.app.base.init.BaseActivity;
import com.app.base.init.MyApplication;
import com.app.iview.IDownloadView;
import com.app.presenter.DownloadPresenter;
import com.avos.avoscloud.AVUser;
import com.avoscloud.chat.service.receiver.MsgReceiver;
import com.base.app.utils.CleanCacheTask;
import com.base.app.utils.DBService;
import com.base.app.utils.EasyLog;
import com.base.dialog.lib.Effectstype;
import com.base.dialog.lib.NiftyDialogBuilder;
import com.base.service.action.constant.HttpTagConstantUtils;
import com.base.supertoasts.util.AppToast;
import com.daimajia.numberprogressbar.NumberProgressBar;
import com.daimajia.numberprogressbar.OnProgressBarListener;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;
import com.koushikdutta.ion.builder.Builders.Any.B;
import com.umeng.message.PushAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;

/**
 * 系统设置
 */
public class SysSettingActivity extends BaseActivity implements IDownloadView ,OnProgressBarListener {
	
	private static final String TAG= "SysSettingActivity--" ;
	
	private ImageView backBtn; // 返回按钮
	private View mod_password; // 修改密码
	private View help; // 使用帮助
	private View check_version; // 版本更新
	private View good_layout; // 管家好评
	private View suggestion_feedback; // 意见反馈
	private View device_buy; // 购买设备
	private View about; // 关于SkinRun
	private View logoutBtn; // 退出登录
	private View clean_cache;//清除缓存
	
	private TextView version_code ;//版本号
	
	//public final String UPDATE_URL = "http://napi.skinrun.cn/v1/Version";
	
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
	static int progress_num = -1 ;
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
							SysSettingActivity.this, 0, intent, 0);
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		MyApplication.getInstance().addActivity(this);
    	PushAgent.getInstance(this).onAppStart();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sys_settings);
		setupView();
		/**下载更新***/
		downloadPresenter = new DownloadPresenter(this);
	}

	@Override
	public void setupView() {
		backBtn = (ImageView) findViewById(R.id.backBtn);
		version_code = (TextView) findViewById(R.id.version_code);
		version_code.setText(""+getVersion());
		mod_password = findViewById(R.id.mod_password);
		help = findViewById(R.id.help);
		check_version = findViewById(R.id.check_version);
		good_layout = findViewById(R.id.good_layout);
		suggestion_feedback = findViewById(R.id.suggestion_feedback);
		device_buy = findViewById(R.id.device_buy);
		about = findViewById(R.id.about);
		logoutBtn = findViewById(R.id.logoutBtn);
		clean_cache=findViewById(R.id.clean_cache);
		

		addListener();

	}

	@Override
	public void addListener() {
		backBtn.setOnClickListener(this);
		mod_password.setOnClickListener(this);
		help.setOnClickListener(this);
		check_version.setOnClickListener(this);
		good_layout.setOnClickListener(this);
		suggestion_feedback.setOnClickListener(this);
		device_buy.setOnClickListener(this);
		about.setOnClickListener(this);
		logoutBtn.setOnClickListener(this);
		clean_cache.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.about: //关于SkinRun
			intent = new Intent(this,AboutSkinRunActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.activity_enter_from_right,
					R.anim.activity_exit_to_left);
			break;
		case R.id.backBtn:
			finish();
			break;
		case R.id.good_layout: //管家五星好评
			Intent i = getIntent(this);  
	        boolean b = judge(this, i);  
	        if(b==false)  
	        {  
	            startActivity(i);  
	        } 
			break;
		case R.id.check_version: //检查版本更新
			/*UpdateHelper updateHelper = new UpdateHelper.Builder(this)
            .checkUrl(MyApplication.UPDATE_URL)
            .isAutoInstall(true) //设置为false需在下载完手动点击安装;默认值为true，下载后自动安装。
            .build();
			updateHelper.check(); */
			isDownLoading = false ;
			 dialogUploadImage = NiftyDialogBuilder.getInstance(this, R.layout.dialog_login_layout);
				final View text_ = LayoutInflater.from(this).inflate(R.layout.dialog_login_view, null);
				TextView t_ = (TextView) text_.findViewById(R.id.loading_text);
				t_.setText("正在检查新版本...");
				dialogUploadImage.withTitle(null)
				.withMessage(null)
				.withEffect(Effectstype.Fadein)
				.withDuration(100)
				.isCancelableOnTouchOutside(false)
				.setCustomView(text_, this.getApplicationContext()).show();
				new Handler().postDelayed(new Runnable() {
					
					@Override
					public void run() {
						downloadPresenter.download(getApplicationContext());
						
					}
				}, 500);
			
			break;
		case R.id.help:// 帮助
			intent = new Intent(this, HelpActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.activity_enter_from_right,
					R.anim.activity_exit_to_left);
			break;
		case R.id.device_buy:// 购买设备
			intent = new Intent(this, PurchaseDeviceActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.activity_enter_from_right,
					R.anim.activity_exit_to_left);
			break;
		case R.id.mod_password:
			intent = new Intent(this, PwdChangeActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.activity_enter_from_right,
					R.anim.activity_exit_to_left);
			break;
		case R.id.suggestion_feedback:// 意见反馈
			//intent = new Intent(this, SuggestionFeedBack.class);
			intent = new Intent(this, SuggestionFeedBackV2.class);
			startActivity(intent);
			overridePendingTransition(R.anim.activity_enter_from_right,
					R.anim.activity_exit_to_left);
			break;
		case R.id.clean_cache:
			
			
			CleanCacheTask cleanCacheTask=new CleanCacheTask(this);
			cleanCacheTask.execute();
			break;
		case R.id.logoutBtn:// 注销当前用户
			final View text = LayoutInflater.from(this).inflate(
					R.layout.dialog_with_two_button, null);
			TextView t = (TextView) text.findViewById(R.id.text);
			t.setText("确定注销当前用户吗?");
			final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder
					.getInstance(this);

			dialogBuilder.withTitle(null).withMessage(null)
					.withMessageColor("#FFFFFFFF")
					.isCancelableOnTouchOutside(false).withDuration(200)
					.withEffect(Effectstype.Fadein).withButton1Text("确定")
					.withButton2Text("取消")
					.setCustomView(text, getApplicationContext())
					.setButton1Click(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							dialogBuilder.dismiss();
							if("weibo".equals(aCache.getAsString("type"))){
								logout(SHARE_MEDIA.SINA);
							}else if("qq".equals(aCache.getAsString("type"))){
								logout(SHARE_MEDIA.QZONE);
							}else if("weixin".equals(aCache.getAsString("type"))){
								logout(SHARE_MEDIA.WEIXIN);
							}
							logoutNative();
							try {
								MsgReceiver.setSessionPaused(true);
								//MsgReceiver.closeSession(AVUser.getCurrentUser());
								AVUser.logOut();
							} catch (Exception e) {
								
							}
							
							finish();
							/*new SessionLogoutService(getApplicationContext(),
									HttpTagConstantUtils.SESSION_DELETE,
									SysSettingActivity.this).doLogout(aCache
									.getAsString("Token"));*/

						}
					}).setButton2Click(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							dialogBuilder.dismiss();
						}
					}).show();

			break;
		default:
			break;
		}

	}
	
	public void logoutNative(){
		try {
			aCache.put("Token", "");
			aCache.put("username", "");
			aCache.put("password", "");
			aCache.put("type", "app");
			AppToast.toastMsgCenter(getApplicationContext(), "注销成功!",
					Toast.LENGTH_LONG).show();
			DBService.deleteSession();
			logout(SHARE_MEDIA.SINA);
			logout(SHARE_MEDIA.QZONE);
			logout(SHARE_MEDIA.WEIXIN);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 调转到市场
	 * @param paramContext
	 * @return
	 */
	public static Intent getIntent(Context paramContext) {
		StringBuilder localStringBuilder = new StringBuilder()
				.append("market://details?id=");
		String str = paramContext.getPackageName();
		localStringBuilder.append(str);
		Uri localUri = Uri.parse(localStringBuilder.toString());
		return new Intent("android.intent.action.VIEW", localUri);
	}

	// 直接跳转不判断是否存在市场应用
	public static void start(Context paramContext, String paramString) {
		Uri localUri = Uri.parse(paramString);
		Intent localIntent = new Intent("android.intent.action.VIEW", localUri);
		localIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		paramContext.startActivity(localIntent);
	}

	public static boolean judge(Context paramContext, Intent paramIntent) {
		List<ResolveInfo> localList = paramContext.getPackageManager()
				.queryIntentActivities(paramIntent,
						PackageManager.GET_INTENT_FILTERS);
		if ((localList != null) && (localList.size() > 0)) {
			return false;
		} else {
			return true;
		}
	}
	

	@Override
	public void sendMessageHandler(int messageCode) {

	}

	@Override
	public void dataCallBack(Object tag, int statusCode, Object result) {
		try {
			if ((Integer) (tag) == HttpTagConstantUtils.SESSION_DELETE) {
				if (isSuccess(statusCode)) {
					
					aCache.put("Token", "");
					aCache.put("username", "");
					aCache.put("password", "");
					aCache.put("type", "");
					AppToast.toastMsgCenter(getApplicationContext(), "注销成功!",
							Toast.LENGTH_LONG).show();
					DBService.deleteSession();
					
					logout(SHARE_MEDIA.SINA);
					logout(SHARE_MEDIA.QZONE);
					logout(SHARE_MEDIA.WEIXIN);
					logoutGroupSession();
					finish();
				} else {
					AppToast.toastMsgCenter(getApplicationContext(),getResources().getString(R.string.ERROR_404), Toast.LENGTH_LONG).show();
				}
			}
		} catch (Exception e) {

		} finally {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {

				}
			}, 1000);
		}
	}
	
	public void logoutGroupSession(){
		
	}
	
	@Override
	protected String getActivityName() {
		return "系统设置";
	}
	
	/**下载更新***/
	@Override
	public void download(UpdateInfo modelInfo) {
		try {
			if(dialogUploadImage != null){
				dialogUploadImage.dismiss();
			}
			if (modelInfo != null) {
				if (Integer.parseInt(modelInfo.getVersionCode()) > getPackageInfo().versionCode) {
					showUpdateUI(modelInfo);
				} else {
					//主页不提示
					AppToast.toastMsgCenter(this, "当前已是最新版").show();
				}
			}else{
				AppToast.toastMsgCenter(this, "当前已是最新版").show();
			}
		} catch (Exception e) {
			AppToast.toastMsgCenter(this, "当前已是最新版").show();
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
		if(null != dialogUploadImage){
			dialogUploadImage.dismiss();
		}
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
							Ion.getDefault(SysSettingActivity.this).cancelAll(downloadGroup);
						}else{
							Ion.getDefault(SysSettingActivity.this).cancelAll(downloadGroup);
							MyApplication.getInstance().exit();
						}
						
						
					}
				}).show();
	}
	
	public void installApkDialog(final Uri data){
		if(null != dialogUploadImage){
			dialogUploadImage.dismiss();
		}
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
				SysSettingActivity.this.startActivity(i);
			}
		}).show();
	}
	
	private void installApkDialogByCancel(final Uri data){
		if(null != dialogUploadImage){
			dialogUploadImage.dismiss();
		}
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
				SysSettingActivity.this.startActivity(i);
				
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
	           // System.out.println("result = " + result);
	           // System.out.println("" + downloaded + " / " + total);
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
			//Message msgMessage = new Message() ;
			//msgMessage.what = INSTALL_APK ;
			handler.obtainMessage(INSTALL_APK).sendToTarget();
			//handler.sendMessage(msgMessage);
			
			
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

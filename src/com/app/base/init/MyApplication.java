package com.app.base.init;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.kymjs.aframe.database.KJDB;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.app.base.entity.HandCommentEntity;
import com.app.base.entity.HumidityEntity;
import com.app.base.entity.IndustryStandardEntity;
import com.app.base.entity.PushEntity;
import com.app.base.entity.SkinCommentEntity;
import com.app.base.entity.SkinTypeEntity;
import com.app.base.entity.TemperatureEntity;
import com.app.base.entity.UserEntity;
import com.app.base.entity.UvEntity;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.PushService;
import com.avos.avoscloud.SignUpCallback;
import com.avoscloud.chat.service.receiver.MsgReceiver;
import com.avoscloud.chat.util.Logger;
import com.avoscloud.chat.util.PhotoUtils;
import com.base.app.utils.DBService;
import com.base.app.utils.DensityUtil;
import com.base.app.utils.EasyLog;
import com.base.app.utils.KVOEvents;
import com.base.app.utils.PushNumUtil;
import com.base.facedemo.FaceConversionUtil;
import com.base.service.action.constant.HttpTagConstantUtils;
import com.base.service.impl.HttpAysnResultInterface;
import com.beabox.hjy.tt.R;
import com.beabox.hjy.tt.SkinRunSplashActivity;
import com.beabox.hjy.tt.main.skintest.component.KVO;
import com.beabox.hjy.tt.main.skintest.component.KVO.Observer;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.skinrun.trunk.main.AudioProcess;
import com.skinrun.trunk.main.ReadLocalFile;
import com.skinrun.trunk.main.TestingPartUtils;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengMessageHandler;
import com.umeng.message.UmengNotificationClickHandler;
import com.umeng.message.entity.UMessage;

/**
 * 
 * @author zhup
 * 
 */
public class MyApplication extends Application implements HttpAysnResultInterface, Observer{
	private static final String TAG = "MyApplication";
	private static MyApplication instance;
	public static Context applicationContext;
	public List<Activity> activityList = new LinkedList();
	
	public static String DEVICE_ID = "" ;
	public static boolean isRegisterSuccess = false ;
	public static String PWD = "123456" ;
	public static boolean isVisitor = true ;//是否访客，有Token为正式用户，没有Token为访客
	
	public static String UPDATE_URL = "http://napi.skinrun.cn/v1/Version?type=4";
	
	public static Point screenSize;
	
	public static String GROUP_ID = "55010942e4b032d91918510e";
	
	private KVO kvo = new KVO();
	 
	public final static String USER_INFO = "user_info" ;
	
	public static boolean isChatLoginSuccess = false ; 
	
	public static boolean isCreateConversion = true ;
	
	public synchronized static MyApplication getInstance() {
		return instance;
	}

	private static KJDB db;

	public static SharedPreferences preferences ;
	
	public synchronized static KJDB getDB() {
		return db;
	}
	
	public static MyApplication ctx ;
	
	public static int resume_index = 0 ;
	
	public static boolean debug = false;//是否打开调试
	public static final String DB_NAME = "chat.db3";
	public static final int DB_VER = 4;
	
	public static AVUser CURRENT_USER = null ;
	
	//public Group group ;
	
	//测试AudioProcess 单实例
	private static AudioProcess audioProcess ;
	
	public static AudioProcess getInsProcess(){
		if(audioProcess == null){
			audioProcess = new AudioProcess();
			Log.e(TAG, "audioProcess = "+ audioProcess);
		}
		return audioProcess ;
	}
	
	
	//leancloud
	String appId = "vqoyzl9cx3krrex3g8kouzv5nl2y8h9ie0shtqkqoozg98hz";
	String appKey = "739c9co8e233cnwn0yfgbknkj11es21m25pznszey0rtxf56";
	
	private Point getScreenWidthAndHeight(){
		WindowManager manager = (WindowManager)getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
	    Display display = manager.getDefaultDisplay();
	    return DensityUtil.getDisplaySize(display);
	}
	//友盟自定义消息处理
	private void handlerUMCustomMessage(){
		PushAgent mPushAgent = PushAgent.getInstance(getApplicationContext());
		//自定义通知处理
		UmengNotificationClickHandler notificationClickHandler = new UmengNotificationClickHandler(){
		    @Override
		    public void dealWithCustomAction(Context context, UMessage msg) {
		        //Toast.makeText(context, msg.custom, Toast.LENGTH_LONG).show();
		    }
		};
		mPushAgent.setNotificationClickHandler(notificationClickHandler);
		
		//自定义消息处理
		UmengMessageHandler messageHandler=new UmengMessageHandler(){
			@Override
			public void dealWithCustomMessage(final Context context, final UMessage msg) {
				 new Handler(getMainLooper()).post(new Runnable() {

			            @Override
			            public void run() {
			            	
			            	String json=msg.custom;
			            	//Toast.makeText(context, json, Toast.LENGTH_LONG).show();
			            	Log.e(TAG, "================推送过来的消息:"+json);
			            	try {
								JSONObject jsonObject=new JSONObject(json);
								int myMessage=jsonObject.getInt("myMsgCount");
								int myTestMessage=jsonObject.getInt("myTestMsgCount");
								
								
								PushEntity pushEntity=null;
								String Token = ACache.get(MyApplication.this).getAsString("Token");
								
								
								if(Token!=null){
									pushEntity=PushNumUtil.get(""+Token);
									if(pushEntity!=null){
										if(pushEntity.getMyMessage()>0){
											pushEntity.setMyMessage(pushEntity.getMyMessage()+myMessage);
										}else{
											pushEntity.setMyMessage(myMessage);
										}
										
										if(pushEntity.getMyTestMessage()>0){
											pushEntity.setMyTestMessage(pushEntity.getMyTestMessage()+myTestMessage);
										}else{
											pushEntity.setMyTestMessage(myTestMessage);
										}
										
										
									}else{
										pushEntity=new PushEntity();
										pushEntity.setMyMessage(myMessage);
										pushEntity.setMyTestMessage(myTestMessage);
										
									}
									pushEntity.setToken(""+Token);
									
									PushNumUtil.save(pushEntity);
								}
								
								
								
								
								
								getKvo().fire(KVOEvents.GET_PUSH_NUM, pushEntity);
								
								
								
							} catch (JSONException e) {
								e.printStackTrace();
							}
			            }
			     });
			}
		};
		
		mPushAgent.setMessageHandler(messageHandler);
	}
	
	@Override
	public void onCreate() {
		
		super.onCreate();
		//友盟自定义消息处理
		if("on".equals(getResources().getString(R.string.is_receive_UMMessage))){
			handlerUMCustomMessage();
		}
		UPDATE_URL = getResources().getString(R.string.download_url);
		
		//注册观察者，以便传输推送数据
		getKvo().registerObserver(KVOEvents.GET_PUSH_NUM, this);
		
		resume_index = 0 ;
		instance = this;
		ctx = this ;
		applicationContext = instance;
		//日志上报接口
		CrashHandler crashHandler = CrashHandler.getInstance();  
        crashHandler.init(getApplicationContext());

		db = KJDB.create(instance, "trunk");// 创建数据库
		try {
			//UserEntity entity = DBService.getUserEntity();
			//Log.e(TAG, entity.toString());
			preferences = getSharedPreferences(USER_INFO, Context.MODE_PRIVATE);
			if(preferences!=null){
				String user_info = preferences.getString("user_info", "");
				Log.e(TAG, "user_info = "+user_info);
				try {
					if(!"".equals(user_info)){
						UserEntity entity  = new UserEntity();
						entity.setAvatar(ACache.get(ctx).getAsString("avatar"));
						entity.setUsername(ACache.get(ctx).getAsString("username"));
						entity.setMobile(ACache.get(ctx).getAsString("username"));
						entity.setNiakname(ACache.get(ctx).getAsString("nickname"));
						entity.setRealname(ACache.get(ctx).getAsString("realname"));
						entity.setGender(ACache.get(ctx).getAsString("gender"));
						entity.setBirthday(ACache.get(ctx).getAsString("birthday"));
						entity.setRegion(ACache.get(ctx).getAsString("region"));
						entity.setSkinType(ACache.get(ctx).getAsString("skinType"));
						
						String integral = ACache.get(ctx).getAsObject("integral")+"";
						
						entity.setIntegral(Integer.valueOf(integral == null
								|| "null".equals(integral) || "".equals(integral) ? "0"
								: integral));
						entity.setUuid(HttpTagConstantUtils.UUID);
						entity.setU_id(ACache.get(ctx).getAsInteger("uid"));//返回的用户id
						entity.setToken(ACache.get(ctx).getAsString("Token"));
						
						DBService.saveOrUpdateUserEntity(entity);
					}
				} catch (Exception e) {
					Log.e(TAG, e.toString());
				}
			}

			initAction();
			initBaseData();
			initImageLoader(ctx);
			initLocalDataBase();//初始化环境因素
			initUmeng();// 友盟统计初始化
	
			FaceConversionUtil.getInstace().getFileText(this);
			
			TelephonyManager TelephonyMgr = (TelephonyManager)getSystemService(TELEPHONY_SERVICE); 
			DEVICE_ID = TelephonyMgr.getDeviceId(); 
			Log.e(TAG, "DEVICE_ID="+DEVICE_ID);
			// Log.e(TAG, ""+DeviceUtil.getDeviceInfo());
			initDeviceConstant();
			
			screenSize=getScreenWidthAndHeight();
			
			initLeanCloud();
			registerUser();//如果已经登录过，那么去注册
			EasyLog.e(getChannel());
		} catch (Exception e) {

		}
	}
	
	/**
	 * 初始化
	 ***********/
	public void initLeanCloud() {
		AVOSCloud.initialize(this, appId, appKey);
		PushService.setDefaultPushCallback(ctx, SkinRunSplashActivity.class);
		
		AVOSCloud.setDebugLogEnabled(debug);
		if (debug) {
			Logger.level = Logger.VERBOSE;
		} else {
			Logger.level = Logger.NONE;
		}
		//AVUser.logOut();
		
	}
	
	public String getChannel(){
		String UMENG_CHANNEL = "tencent" ;
		try {
			ApplicationInfo appInfo = this.getPackageManager()
	                .getApplicationInfo(getPackageName(),
	        PackageManager.GET_META_DATA);
			
			UMENG_CHANNEL =appInfo.metaData.getString("UMENG_CHANNEL");
		} catch (Exception e) {
			
		}
		
		
		return UMENG_CHANNEL ;
	}
	
	void registerUser() {
		final String uid = getSessionUser().getId()+"" ;//ACache.get(ctx).getAsString("uid");
		if (uid != null && !"".equals(uid)) {
			AVQuery<AVUser> q = AVUser.getQuery();
			q.whereEqualTo("username", uid);
			q.getFirstInBackground(new GetCallback<AVUser>() {
				@Override
				public void done(AVUser object, AVException e) {
					if (e != null) {
						Log.e(TAG, "注册失败" + ENCODING.toString());
					} else {
						if (object == null) {// 注册用户
							final AVUser user = new AVUser();
							user.setUsername(uid);
							user.setPassword(MyApplication.PWD);
							user.signUpInBackground(new SignUpCallback() {
								@Override
								public void done(AVException e) {
									if (e != null) {
										Log.e(TAG, "注册失败");
									} else {
										Log.e(TAG, uid + "注册成功");
									}
								}
							});
						}
					}
				}
			});
		}
	}

	public void informedUser(){
		try {//注册正式用户
			final String uid = ACache.get(ctx).getAsString("uid");
			Log.e(TAG, "informedUser uid = "+uid);
			String username = "" ; 
			if(uid != null && !"".equals(uid)){
				username = uid ;
			}else{
				username = DEVICE_ID ;
			}
			final String name = username ;
			AVQuery<AVUser> q = AVUser.getQuery();
		    q.whereEqualTo("username", name);
		    q.getFirstInBackground(new GetCallback<AVUser>() {
		      @Override
		      public void done(AVUser object, AVException e) {
		        if (e != null) {
		          //toastExeption(e);
		        } else {
		          if (object == null) {//注册用户
		            final AVUser user = new AVUser();
		            user.setUsername(name);
		            user.setPassword(MyApplication.PWD);
		            user.signUpInBackground(new SignUpCallback() {
		              @Override
		              public void done(AVException e) {
		                if (e != null) {
		                  //toastExeption(e);
		                } else {
		                   login(name);
		                }
		              }
		            });
		          } else {
		        	try {
						MsgReceiver.closeSession(CURRENT_USER);
					} catch (Exception e2) {
						
					}
		            AVUser.logInInBackground(name, MyApplication.PWD, new LogInCallback<AVUser>() {
		              @Override
		              public void done(AVUser user, AVException e) {
		                if (e != null) {
		                  //toastExeption(e);
		                } else {
		                  login(name);
		                }
		              }
		            });
		          }
		        }
		      }
		    });
			//注册UID用户
			/*if(username != null && !"".equals(username)){
				AVUser user = new AVUser();
				user.setUsername(username);
				user.setPassword(PWD);
				// 其他属性可以像其他AVObject对象一样使用put方法添加
				// user.put("phone", "");
				user.signUpInBackground(new SignUpCallback() {
					public void done(AVException e) {
						if (e == null) {
							log.e("注册成功.........");
							login(uid);
							// successfully
						} else {
							// failed
							log.e("注册失败........." + e.getMessage());
							//login(uid);
						}
					}
				});
			}*/
		} catch (Exception e) {
		}
	}
	
	@SuppressWarnings("unchecked")
	public void login(String loginName){
		AVUser.logInInBackground(loginName, MyApplication.PWD, new LogInCallback() {
		    public void done(AVUser user, AVException e) {
		        if (user != null) {
		            // 登录成功
		        	Log.e(TAG,"登录成功.........");
		        	//登录成功后打开session
		        	CURRENT_USER = user ;
		        	MsgReceiver.openSession(CURRENT_USER);
		        } else {
		        	Log.e(TAG,"登录失败 by reason " + e.toString());
		        }
		    }
		});
	}
	/**
	 * 初始化缓存目录
	 ************************/
	void initAction() {
		// 如果sdcard不存在的话
		if (!AppBaseUtil.isSdCardExists()) {
			AppBaseUtil.DEFAULT_CACHE_FOLDER = getApplicationContext()
					.getCacheDir() + AppBaseUtil.getImageCachePath();
			AppBaseUtil.DEFAULT_CACHE_PRIVATE = getApplicationContext()
					.getCacheDir() + AppBaseUtil.getDataPath();
			AppBaseUtil.APK_DOWNLOAD = getApplicationContext().getCacheDir()
					+ AppBaseUtil.getDownloadPath();
			Log.i(TAG, "img_cache_path>>>" + AppBaseUtil.DEFAULT_CACHE_FOLDER);
			Log.i(TAG, "data_path>>>" + AppBaseUtil.DEFAULT_CACHE_PRIVATE);
			Log.i(TAG, "download_path>>>" + AppBaseUtil.APK_DOWNLOAD);
		}

		// 如果存在直接创建缓存目录
		if (AppBaseUtil.isSdCardExists()) {
			File f = new File(AppBaseUtil.DEFAULT_CACHE_FOLDER);
			if (!f.exists()) {
				f.mkdirs();
			}

			f = new File(AppBaseUtil.DEFAULT_CACHE_PRIVATE);
			if (!f.exists()) {
				f.mkdirs();
			}

			f = new File(AppBaseUtil.APK_DOWNLOAD);
			if (!f.exists()) {
				f.mkdirs();
			}
			Log.i(TAG, "img_cache_path>>>" + AppBaseUtil.DEFAULT_CACHE_FOLDER);
			Log.i(TAG, "data_path>>>" + AppBaseUtil.DEFAULT_CACHE_PRIVATE);
			Log.i(TAG, "download_path>>>" + AppBaseUtil.APK_DOWNLOAD);
		}
	}
	
	
	/**
	 * 初始化ImageLoader
	 */
	public static void initImageLoader(Context context) {
		File cacheDir = StorageUtils.getOwnCacheDirectory(context,
				AppBaseUtil.appName + File.separator + AppBaseUtil.imgcache);
		ImageLoaderConfiguration config = PhotoUtils.getImageLoaderConfig(
				context, cacheDir);
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}
	
	/**
	 * 删除本地会话记录
	 */
	public static void deleteChatCache(){
		try {
			
			File cacheDir = StorageUtils.getOwnCacheDirectory(ctx,"leanchat/Cache");
			Log.e(TAG,"cacheDir = "+ cacheDir.getAbsolutePath());
			File[] filelist = cacheDir.listFiles() ;
			for (File file : filelist) {
				if(file.exists()){
					file.delete();
				}
			}
			
			File filesDir = StorageUtils.getOwnCacheDirectory(ctx,"leanchat/files");
			Log.e(TAG,"files = "+ cacheDir.getAbsolutePath());
			filelist = filesDir.listFiles() ;
			for (File file : filelist) {
				if(file.exists()){
					file.delete();
				}
			}
		} catch (Exception e) {
			Log.e("delete file ", e.toString());
		}
		
	}
	
	/**
     * 递归删除目录下的所有文件及子目录下所有文件
     * @param dir 将要删除的文件目录
     * @return boolean Returns "true" if all deletions were successful.
     *                 If a deletion fails, the method stops attempting to
     *                 delete and returns "false".
     */
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            //递归删除目录中的子目录下
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        // 目录此时为空，可以删除
        return dir.delete();
    }

	/**
	 * 获取应用版本号
	 * 
	 * @return app 版本号
	 */
	public int getAppVersionCode() {
		try {
			PackageInfo packageInfo = getPackageManager().getPackageInfo(
					getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			return 0;
		}
	}

	public void addActivity(Activity activity) {
		if(activityList!=null){
			activityList.add(activity);
		}
	}

	public void exit() {
		for (Activity activity : activityList) {
			activity.finish();
			activity = null;
		}
		activityList.clear();
		activityList = null;
		MyApplication.resume_index = 0 ;
		try {
			//MsgReceiver.getSession(AVUser.getCurrentUser()).close();
			Log.e(TAG, "close session ......");
			//LeanCloudChatActivity.group.quit();
			android.os.Process.killProcess(android.os.Process.myPid());
			System.exit(-1);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}

	public List<Activity> getAllActivities() {
		return activityList;
	}

	/**
	 * 初始化地区数据
	 */
	public static Map<String, String> mProvinceMap = new HashMap<String, String>();
	public static Map<String, String> mProvinceMap_ = new HashMap<String, String>();
	public static final String ENCODING = "UTF-8";

	public void initBaseData() {
		try {
			JSONArray cityData = new JSONArray(getFromAssets("regions"));
			JSONObject obj = null;

			for (int i = 0; i < cityData.length(); i++) {
				obj = (JSONObject) cityData.get(i);
				mProvinceMap.put(obj.optString("name"), obj.optString("code"));
				mProvinceMap_.put(obj.optString("code"), obj.optString("name"));
				obj = null;
			}
		} catch (Exception e) {

		}

	}

	// 从assets 文件夹中获取文件并读取数据
	public String getFromAssets(String fileName) {
		String result = "";
		try {
			InputStream in = getResources().getAssets().open(fileName);
			// 获取文件的字节数
			int lenght = in.available();
			// 创建byte数组
			byte[] buffer = new byte[lenght];
			// 将文件中的数据读到byte数组中
			in.read(buffer);
			result = EncodingUtils.getString(buffer, ENCODING);
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private void initUmeng() {
		MobclickAgent.setDebugMode(false);
		MobclickAgent.openActivityDurationTrack(false);
		MobclickAgent.updateOnlineConfig(this);
	}

	public static void initDeviceConstant() {

		DisplayMetrics dm = applicationContext.getResources()
				.getDisplayMetrics();
		StringBuffer text = new StringBuffer();
		text.append("density: " + dm.density);
		text.append("\n");

		text.append("densityDpi: " + dm.densityDpi);
		text.append("\n");

		text.append("scaledDensity: " + dm.scaledDensity);
		text.append("\n");

		text.append("[width,height]: [" + dm.widthPixels + ","
				+ dm.heightPixels + "]");
		text.append("\n");

		text.append("xdpi: " + dm.xdpi);
		text.append("\n");

		text.append("ydpi: " + dm.ydpi);
		text.append("\n");

		text.append("\n\n");
		text.append("getResources().getConfiguration()");
		text.append("\n=========================\n");

		Log.e(TAG, "" + text);
	}

	
	/**
     * 获取KVO对象
     *
     * @return
     */
    public KVO getKvo() {
        return kvo;
    }
    
 // 初始化数据
 		private void initLocalDataBase() {
 			// 初始化温度数据
 			List<TemperatureEntity> temps = DBService.getDB().findAll(
 					TemperatureEntity.class);
 			if (temps == null || temps.size() <= 0) {
 				initTemperature();
 			}
 			// 初始化湿度数据
 			List<HumidityEntity> hums = DBService.getDB().findAll(
 					HumidityEntity.class);
 			if (hums == null || hums.size() <= 0) {
 				initHunidity();
 			}
 			// 初始化光照数据
 			List<UvEntity> uvs = DBService.getDB().findAll(UvEntity.class);
 			if (uvs == null || uvs.size() <= 0) {
 				initUv();
 			}
 			// 初始化肌肤类型数据
 			List<SkinTypeEntity> skins = DBService.getDB().findAll(
 					SkinTypeEntity.class);
 			if (skins == null || skins.size() <= 0) {
 				initSkinType();
 			}
 			// 初始化行业标准数据
 			List<IndustryStandardEntity> industrys = DBService.getDB().findAll(
 					IndustryStandardEntity.class);
 			if (industrys == null || industrys.size() <= 0) {
 				initIndustryStandard();
 			}
 			// 初始化肌肤评价数据
 			List<SkinCommentEntity> skinComments = DBService.getDB().findAll(
 					SkinCommentEntity.class);
 			if (skinComments == null || skinComments.size() <= 0) {
 				initSkinComment();
 			}
 			//初始化手部肌肤评价数据
 			List<HandCommentEntity> handComments=DBService.getDB().findAll(HandCommentEntity.class);
 			if(handComments==null||handComments.size()<=0){
 				initHandComment();
 			}
 		}
 		//读取手部评价数据
 		private void initHandComment(){
 			new ReadLocalFile(this, TestingPartUtils.HAND_COMMENT).readFile("hand_comment");
 		}
 		
 		// 读取温度数据
 		private void initTemperature() {
 			Log.e(TAG, "=======读取温度对比数据");
 			 new ReadLocalFile(this,TestingPartUtils.TYPE_TEMP).readFile("temperatureFactor");
 		}

 		// 读取湿度数据
 		private void initHunidity() {
 			Log.e(TAG, "=======读取湿度对比数据");
 			new ReadLocalFile(this,TestingPartUtils.TYPE_HUM).readFile("humiditysFactor");
 		}

 		// 读取光照数据
 		private void initUv() {
 			Log.e(TAG, "=======读取光照对比数据");
 			new ReadLocalFile(this,TestingPartUtils.TYPE_UV).readFile("uvsFactor");
 		}

 		// 读取肌肤类型数据
 		private void initSkinType() {
 			Log.e(TAG, "=======读取肌肤类型对比数据");
 			new ReadLocalFile(this,TestingPartUtils.TYPE_SKIN).readFile("skinTypeFactor");
 		}

 		// 读取行业标准数据
 		private void initIndustryStandard() {
 			Log.e(TAG, "=======读取行业标准对比数据");
 			 new ReadLocalFile(this,TestingPartUtils.INDUSTRY_STANDARD).readFile("industryStandard");
 		}

 		private void initSkinComment() {
 			Log.e(TAG, "=======读取肌肤评价对比数据");
 			new ReadLocalFile(this,TestingPartUtils.SKIN_COMMENT).readFile("skin_comment");
 		}

 		//用户登录
		@Override
		public void dataCallBack(Object tag, int statusCode, Object result) {
			
			
		}
		
		public UserEntity getSessionUser(){
			UserEntity entity = new UserEntity();
			try {
				preferences = getSharedPreferences(USER_INFO, Context.MODE_PRIVATE);
				if(preferences!=null){
					String user_info = preferences.getString("user_info", "");
					JSONObject obj = new JSONObject(user_info);
					Log.e(TAG, "CURRENT_USER_INFO--->"+user_info);
					entity.setId(obj.optInt("id"));
					entity.setAvatar(obj.optString("avatar").replaceAll("\\\\", ""));
					entity.setUsername(obj.optString("username").replaceAll("\\\\", ""));
					entity.setEmail(obj.optString("email"));
					entity.setMember_id(obj.optString("member_id"));
					entity.setMobile(obj.optString("mobile"));
					entity.setNiakname(obj.optString("niakname"));
					entity.setRealname(obj.optString("realname"));
					entity.setGender(obj.optString("gender"));
					entity.setBirthday(obj.optString("birthday"));
					entity.setRegion(obj.optString("region"));
					entity.setRegionNames(obj.optString("regionNames"));
					entity.setSkinType(obj.optString("skinType"));
					entity.setHasMerchant(obj.optString("hasMerchant"));
					entity.setIntegral(obj.optInt("integral"));
					entity.setUuid(HttpTagConstantUtils.UUID);
					entity.setU_id(obj.optInt("id"));//返回的用户id
					entity.setToken(obj.optString("Token"));
				}
			} catch (Exception e) {
				
			}
			
			return entity ;
		}
		@Override
		public void onEvent(String event, Object... args) {
			
		}
}

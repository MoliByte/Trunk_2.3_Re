package com.skinrun.trunk.main;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpStatus;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.base.entity.PushEntity;
import com.app.base.entity.UserEntity;
import com.app.base.init.ACache;
import com.app.base.init.MyApplication;
import com.app.custom.view.BadgeView;
import com.app.service.ClearMyMessageCountService;
import com.app.service.GetMyMessageCountService;
import com.app.service.GetUserInfoService;
import com.app.service.LoginAsynTaskService;
import com.app.service.PostThirdLoginService;
import com.avos.avoscloud.LogUtil.log;
import com.avoscloud.chat.service.UserService;
import com.avoscloud.chat.util.PhotoUtils;
import com.baidu.mobstat.StatService;
import com.base.app.utils.DBService;
import com.base.app.utils.DateUtil;
import com.base.app.utils.EasyLog;
import com.base.app.utils.HomeTag;
import com.base.app.utils.KVOEvents;
import com.base.app.utils.PushNumUtil;
import com.base.app.utils.StringUtil;
import com.base.app.utils.UMShareUtil;
import com.base.app.utils.UMengPushUtil;
import com.base.dialog.lib.Effectstype;
import com.base.dialog.lib.NiftyDialogBuilder;
import com.base.service.action.constant.HttpTagConstantUtils;
import com.base.service.impl.HttpAysnResultInterface;
import com.base.service.impl.HttpClientUtils;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.AddressFeedBack;
import com.beabox.hjy.tt.MyCollectActivity;
import com.beabox.hjy.tt.MyIntegralActivity;
import com.beabox.hjy.tt.MyMasterActivity;
import com.beabox.hjy.tt.MyMessageActivity;
import com.beabox.hjy.tt.MyTestActivity;
import com.beabox.hjy.tt.R;
import com.beabox.hjy.tt.RegisterActivity_1;
import com.beabox.hjy.tt.ResetPassword;
import com.beabox.hjy.tt.SweepActivity;
import com.beabox.hjy.tt.SysSettingActivity;
import com.beabox.hjy.tt.UpdateBaseInfoActivity;
import com.beabox.hjy.tt.main.skintest.component.KVO.Observer;
import com.idongler.widgets.CircleImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.UMAuthListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMDataListener;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;

/**
 * 我的个人信息页面
 * 
 * @author zhup
 * 
 */
public class MineFrag extends Fragment implements OnClickListener,
		HttpAysnResultInterface ,Observer{
	
	private final static String TAG = "MineFrag" ;
	
	private View my_master; 		// 我的导师
	
	private View sys_settings,address_settings,my_collect; 		// 系统设置
	private View sweep_scan; 		// 扫一扫
	private  View my_info; 			// 个人信息
	private View my_integral;		// 我的积分
	private View man;		
	private View women;		
	private View my_subscribe_layout;//我的订阅
	UserEntity entity;

	private TextView nick_name; 	// 昵称
	private TextView regionNames; 	// 区域名称
	private TextView skinType; 		// 皮肤类型
	private TextView realname; 		// 正式名称
	private TextView level; 		// 等级
	private TextView age; 			// 年龄
	private TextView title_name; 

	private View my_message_layout;
	private CircleImageView avatar_img;
	private View mine_container;
	ACache aCache;
	FragmentManager fmgr;

	// login -----view
	private EditText username;
	private EditText password;
	private ImageView backBtn;
	private View login_btn,un_login_btn;
	private View toRegisterBtn;
	static NiftyDialogBuilder dialogUploadImage;
	
	
	
	SimpleDateFormat format = new SimpleDateFormat("yyyy");

	private View forget_pwd;// 忘记密码
	
	protected String mCurrentProviceName = "" ;
	protected String mCurrentCityName = "" ;
	protected String mCurrentDistrictName = "";
	protected Map<String, String> mProvinceMap = new HashMap<String, String>();
	private InputMethodManager imm;
	private View login_main_layout ;
	private View my_info_layout ;
	
	//第三方SSO登录
	private ImageView weixin_logo ; //微信
	private ImageView qq_logo ;		//QQ
	private ImageView weibo_logo ;	//微博
	
	private static boolean isQQInstalled ; 
	private static boolean isWeiXinInstalled ; 
	//private static boolean isWeiboInstalled ; 
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().getKvo().registerObserver(KVOEvents.USER_LOGININ, this);
		MyApplication.getInstance().getKvo().registerObserver(KVOEvents.GET_PUSH_NUM, this);
		MyApplication.getInstance().getKvo().registerObserver(KVOEvents.GET_MYMESSAGE_COUNT, this);
		
		if(isAdded()){
			initUmeng();
			imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		}
		fmgr = getFragmentManager();
		entity = DBService.getUserEntity();//DBService.getEntityByToken(aCache.getAsString("Token"));
		//UMShareUtil.getInstance().getUmsocialService().getConfig().setSsoHandler();
		// 添加QZone平台
		/*QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(getActivity(),"1104063317",
				"XNIB4GzYvU8IbYlK") ;
		qZoneSsoHandler.addToSocialSDK();*/
		String appId = "100381049";//"1104063317";//"100424468";
		String appKey = "3ad747d61375966b12e2862ef8a70e61";//"XNIB4GzYvU8IbYlK";//"c7394704798a158208a74ab60104f0ba";
		
		QZoneSsoHandler qqSsoHandler = new QZoneSsoHandler(getActivity(),
				appId, appKey);
		qqSsoHandler.setTargetUrl("http://www.umeng.com");
		qqSsoHandler.addToSocialSDK();
		
		isQQInstalled = qqSsoHandler.isClientInstalled();
		
		String wxappId = "wx4297582bc8743dc3";
		String wxappSecret = "fad627ead5f6622ecacde5caf71895ec";
		// 添加微信平台
		UMWXHandler wxHandler = new UMWXHandler(getActivity(), wxappId,
				wxappSecret);
		
		isWeiXinInstalled = wxHandler.isClientInstalled() ;
		
		wxHandler.addToSocialSDK();
		

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.mine_page, container, false);
		//ButterKnife.inject(this, view);

		mine_container =  view
				.findViewById(R.id.mine_container);
		login_main_layout = view.findViewById(R.id.login_main_layout);
		login_main_layout.setOnClickListener(this);
		my_info_layout = view.findViewById(R.id.my_info_layout);
		loginPage(view);
		getInfoPageView(view);
		//mine_container.removeAllViews();
		if (!isLogin()) {
			login_main_layout.setVisibility(View.VISIBLE);
			my_info_layout.setVisibility(View.GONE);
			//mine_container.addView(loginPage());
		} else {
			login_main_layout.setVisibility(View.GONE);
			my_info_layout.setVisibility(View.VISIBLE);
			// 如果用户没有登录则显示登陆页面，如果Token在的话就显示用户的个人信息
			//mine_container.addView(getInfoPageView());
		}
		return view;
	}

	public boolean isLogin() {
		/*String token = ACache.get(getActivity()).getAsString("Token");
		if(token != null && !"".equals(token)){
			return true;
		}else{
			return true;
		}*/
		
		if (!"".equals(ACache.get(getActivity()).getAsString("Token"))
				&& !"".equals(ACache.get(getActivity()).getAsString("uid"))) {
			return true;
		} else {
			if (entity != null && null != entity.getToken()
					&& !"".equals(entity.getToken())) {
				// updateUserInfo(entity);
				return true;
			} else {
				return false;
			}
		}
	}

	// 登陆页面
	public void loginPage(View view) {
		/*view = LayoutInflater.from(getActivity()).inflate(
				R.layout.activity_login, null);*/
		
		backBtn = (ImageView) view.findViewById(R.id.backBtn);
		login_btn = view.findViewById(R.id.login_btn);
		un_login_btn = view.findViewById(R.id.un_login_btn);
		
		weixin_logo = (ImageView) view.findViewById(R.id.weixin_logo);
		qq_logo = (ImageView) view.findViewById(R.id.qq_logo);
		weibo_logo = (ImageView) view.findViewById(R.id.weibo_logo);
		
		username = (EditText) view.findViewById(R.id.username);
		password = (EditText) view.findViewById(R.id.password);
		toRegisterBtn = view.findViewById(R.id.toRegisterBtn);
		forget_pwd = view.findViewById(R.id.forget_pwd);
		if(!SHARE_MEDIA.WEIXIN.equals(ACache.get(getActivity()).getAsString("type"))
				&&!SHARE_MEDIA.SINA.equals(ACache.get(getActivity()).getAsString("type"))
				&&!SHARE_MEDIA.QZONE.equals(ACache.get(getActivity()).getAsString("type"))){
			
		
				////aCache.getAsString("mobile") == null ? "" : aCache.getAsString("mobile");
				String username = entity == null ?"": entity.getMobile() == null ?"":entity.getMobile();
				this.username.setText(username + "");
				this.username.setSelection(username.length());
				
				String password = aCache.getAsString("password")==null?"":aCache.getAsString("password");
				this.password.setText(password+"");
				this.password.setSelection(password.length());
				if(!StringUtil.isBlank(username) && !StringUtil.isBlank(password)){
					MineFrag.this.un_login_btn.setVisibility(View.GONE);
					MineFrag.this.login_btn.setVisibility(View.VISIBLE);
				}
				
		}else{
			this.username.setText("");
			this.password.setText("");
		}
		
		backBtn.setVisibility(View.GONE);
		backBtn.setOnClickListener(this);
		login_btn.setOnClickListener(this);
		un_login_btn.setOnClickListener(this);
		toRegisterBtn.setOnClickListener(this);
		forget_pwd.setOnClickListener(this);
		
		weixin_logo.setOnClickListener(this);
		qq_logo.setOnClickListener(this);
		weibo_logo.setOnClickListener(this);
		
		this.username.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View arg0, boolean isfocus) {
				
				/*if(isfocus && "".equals(MineFrag.this.username.getText().toString())){
					MineFrag.this.username.setHint("请输入手机号");
					MineFrag.this.un_login_btn.setVisibility(View.VISIBLE);
					MineFrag.this.login_btn.setVisibility(View.GONE);
				}
				
				if(isfocus && !"".equals(MineFrag.this.username.getText().toString())){
					MineFrag.this.username.setHint("");
				}*/
				if(isfocus){
					MineFrag.this.username.setHint("");
					MineFrag.this.un_login_btn.setVisibility(View.GONE);
					MineFrag.this.login_btn.setVisibility(View.VISIBLE);
				}else{
					MineFrag.this.username.setHint("请输入手机号");
					MineFrag.this.un_login_btn.setVisibility(View.VISIBLE);
					MineFrag.this.login_btn.setVisibility(View.GONE);
				}
			}
		});
		this.password.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View arg0, boolean isfocus) {
				
				/*if(isfocus && ("".equals(MineFrag.this.password.getText().toString()) || "".equals(MineFrag.this.username.getText().toString()))){
					MineFrag.this.un_login_btn.setVisibility(View.VISIBLE);
					MineFrag.this.login_btn.setVisibility(View.GONE);
					MineFrag.this.password.setHint("请输入密码");
				}
				
				if(isfocus && !"".equals(MineFrag.this.password.getText().toString())){
					MineFrag.this.password.setHint("");
					MineFrag.this.un_login_btn.setVisibility(View.GONE);
					MineFrag.this.login_btn.setVisibility(View.VISIBLE);
				}*/
				
				if(isfocus){
					MineFrag.this.password.setHint("");
					MineFrag.this.un_login_btn.setVisibility(View.GONE);
					MineFrag.this.login_btn.setVisibility(View.VISIBLE);
				}else{
					MineFrag.this.password.setHint("请输入密码");
					MineFrag.this.un_login_btn.setVisibility(View.VISIBLE);
					MineFrag.this.login_btn.setVisibility(View.GONE);
				}
			}
		});

		//return view;
	}
	private BadgeView my_message_badge,my_test_badge;
	
	/**
	 * 
	 * @return
	 */
	public void getInfoPageView(View view) {
		/*View view = LayoutInflater.from(getActivity()).inflate(
				R.layout.mine_main_fragment, null);*/
		my_master = view.findViewById(R.id.my_master);
		man = view.findViewById(R.id.man);
		women = view.findViewById(R.id.women);
		my_master = view.findViewById(R.id.my_master);
		avatar_img = (CircleImageView) view.findViewById(R.id.avatar_img);
		title_name = (TextView) view.findViewById(R.id.title_name);
		nick_name = (TextView) view.findViewById(R.id.nick_name);
		skinType = (TextView) view.findViewById(R.id.skin_type);
		age = (TextView) view.findViewById(R.id.age);
		
		regionNames = (TextView) view.findViewById(R.id.regionNames);
		my_message_layout = view.findViewById(R.id.my_message_layout);
		sys_settings = view.findViewById(R.id.sys_settings);
		address_settings = view.findViewById(R.id.address_settings);
		my_collect=view.findViewById(R.id.my_collect_layout);
		sweep_scan = view.findViewById(R.id.mine_sweep_scan);
		my_subscribe_layout=view.findViewById(R.id.my_subscribe_layout);
		
		my_message_badge=(BadgeView)view.findViewById(R.id.my_message_badge);
		my_test_badge=(BadgeView)view.findViewById(R.id.my_test_badge);
		
		my_test_badge.hide();
		my_message_badge.hide();
		
		my_info = view.findViewById(R.id.my_info);
		
		my_integral = view.findViewById(R.id.my_integral);
		// realname = (TextView) view.findViewById(R.id.realname);
		level = (TextView) view.findViewById(R.id.level);
		
		view.findViewById(R.id.my_test_layout).setOnClickListener(this);
		my_subscribe_layout.setOnClickListener(this);
		
		updateUserInfoShow();
		
		//开关
		String scan_switch=getActivity().getResources().getString(R.string.scan_switch);
		if(!scan_switch.equals("scan_on")){
			sweep_scan.setVisibility(View.GONE);
		}
		
		String message_switch=getActivity().getResources().getString(R.string.message_switch);
		if(!message_switch.equals("message_on")){
			my_message_layout.setVisibility(View.GONE);
		}
		
		String subcribe_switch=getActivity().getResources().getString(R.string.subcribe_switch);
		if(!subcribe_switch.equals("subscribe_on")){
			my_subscribe_layout.setVisibility(View.GONE);
		}
		
		String collect_switch=getActivity().getResources().getString(R.string.collect_switch);
		if(!collect_switch.equals("collect_on")){
			my_collect.setVisibility(View.GONE);
		}
		
		String test_switch=getActivity().getResources().getString(R.string.my_test_switch);
		if(!test_switch.equals("test_on")){
			view.findViewById(R.id.my_test_layout).setVisibility(View.GONE);
		}
		
		String my_integral_switch=getActivity().getResources().getString(R.string.my_integral_switch);
		if(!my_integral_switch.equals("my_integral_on")){
			my_integral.setVisibility(View.GONE);
		}
		
		String aderess_switch=getActivity().getResources().getString(R.string.aderess_switch);
		if(!aderess_switch.equals("aderess_on")){
			address_settings.setVisibility(View.GONE);
		}
		
		String system_setting_switch=getActivity().getResources().getString(R.string.system_setting_switch);
		if(!system_setting_switch.equals("system_setting_on")){
			sys_settings.setVisibility(View.GONE);
		}
		
		//return view;
	}

	public void login() {
		final String loginName = username.getText().toString();
		final String password_ = password.getText().toString();

		if (StringUtil.isBlank(loginName)) {
			AppToast.toastMsgCenter(getActivity(),
					getString(R.string.login_loginNameBlankMessage),
					Toast.LENGTH_LONG).show();
			return;
		}

		if (StringUtil.isEmpty(password_)) {
			AppToast.toastMsgCenter(getActivity(),
					getString(R.string.login_passwordBlankMessage),
					Toast.LENGTH_LONG).show();
			return;
		}

		dialogUploadImage = NiftyDialogBuilder.getInstance(getActivity(),
				R.layout.dialog_login_layout);
		final View text = LayoutInflater.from(getActivity()).inflate(
				R.layout.dialog_login_view, null);
		TextView t = (TextView) text.findViewById(R.id.loading_text);
		t.setText("正在登陆....");
		dialogUploadImage.withTitle(null).withMessage(null)
				.withEffect(Effectstype.Fadein).withDuration(100)
				.isCancelableOnTouchOutside(false)
				.setCustomView(text, getActivity().getApplicationContext())
				.show();
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				new LoginAsynTaskService(getActivity(),
						HttpTagConstantUtils.LOGIN_TAG, MineFrag.this).doLogin(
						loginName, password_);

			}
		}, 500);
	}

	public void getAccount() {
		new LoginAsynTaskService(getActivity(), HttpTagConstantUtils.LOGIN_TAG,
				this).doLogin(username.getText().toString(), password.getText()
				.toString());
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	// 如果有使用任一平台的SSO授权, 则必须在对应的activity中实现onActivityResult方法, 并添加如下代码
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 根据requestCode获取对应的SsoHandler
		UMSsoHandler ssoHandler = UMShareUtil.getInstance()
				.getUmsocialService().getConfig().getSsoHandler(resultCode);
		if (ssoHandler != null) {
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		aCache = ACache.get(activity);
	}

	 // 整个平台的Controller, 负责管理整个SDK的配置、操作等处理
    private UMSocialService mController = UMServiceFactory
            .getUMSocialService(DESCRIPTOR);
    public static final String DESCRIPTOR = "com.umeng.share";
   
	/**
	 * 授权。如果授权成功，则获取用户信息
	 * 
	 * @param platform
	 */
	private void login(final SHARE_MEDIA platform) {
		/*if(UMInfoAgent.){
			
		}*/
		/*UMShareUtil.getInstance().getUmsocialService()*/mController.doOauthVerify(getActivity(), platform,
				new UMAuthListener() {

					@Override
					public void onStart(SHARE_MEDIA platform) {
						dialogUploadImage = NiftyDialogBuilder.getInstance(getActivity(),
								R.layout.dialog_login_layout);
						final View text = LayoutInflater.from(getActivity()).inflate(
								R.layout.dialog_login_view, null);
						TextView t = (TextView) text.findViewById(R.id.loading_text);
						t.setText("正在登录....");
						dialogUploadImage.withTitle(null).withMessage(null)
								.withEffect(Effectstype.Fadein).withDuration(100)
								.isCancelableOnTouchOutside(false)
								.setCustomView(text, getActivity())
								.show();
					}

					@Override
					public void onError(SocializeException e,
							SHARE_MEDIA platform) {
						dismissDialog();
						AppToast.toastMsgCenter(getActivity(), e+",授权失败").show();
					}

					@Override
					public void onComplete(Bundle value, SHARE_MEDIA platform) {
						// 获取uid
						String uid = value.getString("uid");
						
						if (!TextUtils.isEmpty(uid)) {
							getUserInfo(platform);
						} else {
							dismissDialog();
							AppToast.toastMsgCenter(getActivity(), "授权失败").show();
							
						}
					}

					@Override
					public void onCancel(SHARE_MEDIA platform) {
						dismissDialog();
						AppToast.toastMsgCenter(getActivity(), "授权取消").show();
						
					}
				});
	}
	
	public void dismissDialog(){
		if(dialogUploadImage!=null && dialogUploadImage.isShowing()){
			dialogUploadImage.dismiss();
		}
	}
	
	/**
	 * 获取用户信息
	 * 
	 * @param platform
	 */
	private void getUserInfo(final SHARE_MEDIA platform) {
		UMShareUtil.getInstance().getUmsocialService().getPlatformInfo(getActivity(), platform,
				new UMDataListener() {

					@Override
					public void onStart() {
						// uid不为空，获取用户信息
						dismissDialog();
						dialogUploadImage = NiftyDialogBuilder.getInstance(getActivity(),
								R.layout.dialog_login_layout);
						final View text = LayoutInflater.from(getActivity()).inflate(
								R.layout.dialog_login_view, null);
						TextView t = (TextView) text.findViewById(R.id.loading_text);
						t.setText("正在同步用户信息....");
						dialogUploadImage.withTitle(null).withMessage(null)
								.withEffect(Effectstype.Fadein).withDuration(100)
								.isCancelableOnTouchOutside(false)
								.setCustomView(text, getActivity().getApplicationContext())
								.show();
					}

					@Override
					public void onComplete(int status, final Map<String, Object> info) {
						
						if (info != null) {//登录成功
							final Map<String,String> param_map = new HashMap<String,String>();
							Log.e("user info ", ">>>"+info.toString());
							//AppToast.toastMsgCenter(getActivity(), info.toString(),5000).show();
							if(platform == SHARE_MEDIA.WEIXIN){
								ACache.get(getActivity()).put("weixin", info.toString());
								param_map.put("type", "weixin");
								param_map.put("uid", info.get("uid")+"");
								param_map.put("gender", info.get("gender")+"");
								param_map.put("screen_name", info.get("screen_name")+"");
								param_map.put("openid", info.get("openid")+"");
								param_map.put("profile_image_url", info.get("profile_image_url")+"");
							}else if(platform == SHARE_MEDIA.SINA){
								ACache.get(getActivity()).put("sina", info.toString());
								param_map.put("type", "weibo");//sina weibo
								
								param_map.put("uid", info.get("uid")+"");
								param_map.put("gender", ("1".equals(info.get("gender")+"")?"男":"女")+"");
								param_map.put("screen_name", info.get("screen_name")+"");
								param_map.put("openid", HttpClientUtils.md5(info.get("uid")+""));
								param_map.put("profile_image_url", info.get("profile_image_url")+"");
							}else if(platform == SHARE_MEDIA.QZONE){
								ACache.get(getActivity()).put("qq", info.toString());
								param_map.put("type", "qq");
								
								param_map.put("uid", info.get("uid")+"");
								param_map.put("gender", info.get("gender")+"");
								param_map.put("screen_name", info.get("screen_name")+"");
								param_map.put("openid", info.get("openid")+"");
								param_map.put("profile_image_url", info.get("profile_image_url")+"");
								
							}
							//AppToast.toastMsgCenter(getActivity(), param_map.toString()+"",10000).show();
							EasyLog.e(platform+"-->"+param_map.toString());
							
							new Handler().postDelayed(new Runnable() {

								@Override
								public void run() {
									new PostThirdLoginService(getActivity(),
											HttpTagConstantUtils.THIRD_LOGIN_TAG, MineFrag.this).doPostThirdLogin(param_map);

								}
							}, 500);
							
						}else{
							dismissDialog();
							
							AppToast.toastMsgCenter(getActivity(), "授权失败").show();
						}
					}
				});
	}
	
	@Override
	public void onPause() {
		super.onPause();
		MyApplication.resume_index = 4 ;
		MineFrag.this.username.setHint("请输入手机号");
		MineFrag.this.password.setHint("请输入密码");
		MineFrag.this.un_login_btn.setVisibility(View.VISIBLE);
		MineFrag.this.login_btn.setVisibility(View.GONE);
		/*if(dialogUploadImage!=null && dialogUploadImage.isShowing()){
			dialogUploadImage.dismiss();
		}*/
		
		//页面结束
        if (!StringUtil.isEmpty(getActivityName())) {
            MobclickAgent.onPageEnd(getActivityName()); // 保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息
            MobclickAgent.onPause(getActivity());
            StatService.onPause(getActivity());
        }
	}
	
	@Override
	public void onStop() {
		super.onStop();

		/*if(dialogUploadImage!=null && dialogUploadImage.isShowing()){
			dialogUploadImage.dismiss();
		}*/
	}
	
	private void getMyMessage(String token){
		new GetMyMessageCountService(getActivity(), HttpTagConstantUtils.GET_MESSAGE_NOCOUNT, this).doGet(token);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		UserEntity user=DBService.getUserEntity();
		if(user!=null){
			getMyMessage(""+user.getToken());
			PushEntity pushEntity=PushNumUtil.get(user.getToken());
			if(pushEntity!=null){
				if(pushEntity.getMyMessage()>0){
					my_message_badge.setVisibility(View.VISIBLE);
					my_message_badge.setText(""+pushEntity.getMyMessage());
				}else{
					my_message_badge.hide();
				}
				
				if(pushEntity.getMyTestMessage()>0){
					my_test_badge.setVisibility(View.VISIBLE);
					my_test_badge.setText(""+pushEntity.getMyTestMessage());
				}else{
					my_test_badge.hide();
				}
			}
		}
		
		try {
			
		
			if (null != mine_container) {
				//mine_container.removeAllViews();
				//String token = aCache.getAsString("Token");
				entity = DBService.getUserEntity();
				if (!isLogin()) {
					//mine_container.addView(loginPage());
					login_main_layout.setVisibility(View.VISIBLE);
					my_info_layout.setVisibility(View.GONE);
					String username = aCache.getAsString("username")==null?"":aCache.getAsString("username");
					this.username.setText(username + "");
					this.username.setSelection(username.length());
					
					String password = aCache.getAsString("password")==null?"":aCache.getAsString("password");
					this.password.setText(password+"");
					this.password.setSelection(password.length());
					if(!StringUtil.isBlank(username)&&!StringUtil.isBlank(password)){
						un_login_btn.setVisibility(View.GONE);
						login_btn.setVisibility(View.VISIBLE);
					}
					
				} else {
					//updateUserInfoShow();
					login_main_layout.setVisibility(View.GONE);
					my_info_layout.setVisibility(View.VISIBLE);
					
					 //login_main_layout.setVisibility(View.GONE);
					 //my_info_layout.setVisibility(View.VISIBLE);
				}
				
				String token = ACache.get(getActivity()).getAsString("Token");
				if(token != null && !"".equals(token)){
					new GetUserInfoService(getActivity(),
							 HttpTagConstantUtils.USER_INFO, this)
							 .getUserInfo(token);
				}
				
			}
			
			
			MobclickAgent.onPageStart(getActivityName()); //统计页面
		    MobclickAgent.onResume(getActivity());
		    StatService.onResume(getActivity());
		    
		   // dismissDialog();
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}

	@Override
	public void onClick(View v) {
		MyApplication.resume_index = 4 ;
		Intent intent = null;
		switch (v.getId()) {
		case R.id.weibo_logo:
			login(SHARE_MEDIA.SINA);
			break;
		case R.id.weixin_logo:
			if(isWeiXinInstalled){
				MyApplication.resume_index = 4 ;
				login(SHARE_MEDIA.WEIXIN);
			}else{
				AppToast.toastMsgCenter(getActivity(), "请先安装微信客户端!").show();
			}
			break;
		case R.id.qq_logo:
			if(isQQInstalled){
				MyApplication.resume_index = 4 ;
				login(SHARE_MEDIA.QZONE);
			}else{
				AppToast.toastMsgCenter(getActivity(), "请先安装QQ客户端!").show();
			}
			
			break;
		case R.id.login_main_layout:
			if(null!=imm && imm.isActive()){
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			}
			break;
		case R.id.my_master:// 我的导师
			intent = new Intent(getActivity(), MyMasterActivity.class);
			startActivity(intent);
			getActivity().overridePendingTransition(
					R.anim.activity_enter_from_right,
					R.anim.activity_exit_to_left);
			break;

		case R.id.my_integral: // 我的积分
			intent = new Intent(getActivity(), MyIntegralActivity.class);
			startActivity(intent);
			getActivity().overridePendingTransition(
					R.anim.activity_enter_from_right,
					R.anim.activity_exit_to_left);
			break;
		case R.id.address_settings:// 收货地址
			intent = new Intent(getActivity(), AddressFeedBack.class);
			startActivity(intent);
			getActivity().overridePendingTransition(
					R.anim.activity_enter_from_right,
					R.anim.activity_exit_to_left);
			break;
		case R.id.sys_settings:// 系统设置
			intent = new Intent(getActivity(), SysSettingActivity.class);
			startActivity(intent);
			getActivity().overridePendingTransition(
					R.anim.activity_enter_from_right,
					R.anim.activity_exit_to_left);
			break;
		case R.id.my_collect_layout://我的收藏
			intent=new Intent(getActivity(), MyCollectActivity.class);
			startActivity(intent);
			getActivity().overridePendingTransition(
					R.anim.activity_enter_from_right,
					R.anim.activity_exit_to_left);
			break;
		case R.id.my_test_layout://我的测试
			intent=new Intent(getActivity(), MyTestActivity.class);
			intent.putExtra("FLAG", HomeTag.MY_TEST);
			//intent=new Intent(getActivity(), MySubscibeActivity.class);
			startActivity(intent);
			getActivity().overridePendingTransition(
					R.anim.activity_enter_from_right,
					R.anim.activity_exit_to_left);
			break;
		case R.id.my_subscribe_layout://我的订阅
			intent=new Intent(getActivity(), MyTestActivity.class);
			intent.putExtra("FLAG", HomeTag.MY_SUBSCRIB);
			startActivity(intent);
			getActivity().overridePendingTransition(
					R.anim.activity_enter_from_right,
					R.anim.activity_exit_to_left);
			
			break;
			
		case R.id.mine_sweep_scan:// 扫一扫
			intent = new Intent(getActivity(),
					SweepActivity.class);
			startActivity(intent);
			getActivity().overridePendingTransition(
					R.anim.activity_enter_from_right,
					R.anim.activity_exit_to_left);
			break;

		case R.id.my_info: //个人信息
			intent = new Intent(getActivity(), UpdateBaseInfoActivity.class);
			//intent.putExtra("userinfo", entity);
			startActivity(intent);
			getActivity().overridePendingTransition(
					R.anim.activity_enter_from_right,
					R.anim.activity_exit_to_left);
			break;
		case R.id.my_message_layout: // 我的消息列表
			intent = new Intent(getActivity(), MyMessageActivity.class);
			startActivity(intent);
			getActivity().overridePendingTransition(
					R.anim.activity_enter_from_right,
					R.anim.activity_exit_to_left);
			break;

		// login --------
		case R.id.login_btn:
			login();
			break;
		case R.id.un_login_btn:
			
			break;
		case R.id.forget_pwd:
			intent = new Intent(getActivity(), ResetPassword.class);
			startActivity(intent);
			getActivity().overridePendingTransition(
					R.anim.activity_enter_from_right,
					R.anim.activity_exit_to_left);
			break;
		case R.id.toRegisterBtn:// 到注册页面
			intent = new Intent(getActivity(), RegisterActivity_1.class);
			startActivity(intent);
			getActivity().overridePendingTransition(
					R.anim.activity_enter_from_right,
					R.anim.activity_exit_to_left);
			break;
		default:
			break;
		}
	}

	@Override
	public void dataCallBack(Object tag, int statusCode, Object result) {
		try {
			if((Integer)tag==HttpTagConstantUtils.CLEAR_MESSAGE_NOCOUNT){
				if(statusCode==200||statusCode==201){
					//MyApplication.getInstance().getKvo().fire(KVOEvents.GET_MYMESSAGE_COUNT, 0);
				}
				
				
			}else if((Integer)tag==HttpTagConstantUtils.GET_MESSAGE_NOCOUNT){
				if(statusCode==200||statusCode==201){
					int myMsgCount=(Integer) result;
					log.e(TAG, "============未读消息条数");
					MyApplication.getInstance().getKvo().fire(KVOEvents.GET_MYMESSAGE_COUNT, myMsgCount);
					if(myMsgCount>0){
						my_message_badge.setVisibility(View.VISIBLE);
						my_message_badge.setText(""+myMsgCount);
					}else{
						my_message_badge.hide();
					}
					
					new ClearMyMessageCountService(getActivity(), HttpTagConstantUtils.CLEAR_MESSAGE_NOCOUNT, this)
									.doGet(""+DBService.getUserEntity().getToken());
				}
				
			}else if ((Integer) (tag) == HttpTagConstantUtils.USER_INFO) {
				if (statusCode == HttpStatus.SC_OK
						|| statusCode == HttpStatus.SC_CREATED) {
					UserEntity entity = (UserEntity) result;
					this.entity = entity;
					//DBService.saveOrUpdateUserEntity(entity);
					updateUserInfo(entity);
					updateUserInfoShow();
					
					//login_main_layout.setVisibility(View.GONE);
					//my_info_layout.setVisibility(View.VISIBLE);
					
					try {
						//获取到用户信息后给测试页面发消息
						MyApplication.getInstance().getKvo().fire(KVOEvents.USER_LOGININ,"LOGIN_SUCCESS");
					} catch (Exception e) {
						
					}
					/*View view = getInfoPageView();
					mine_container.removeAllViews();
					mine_container.addView(view);*/
				}else{
					//AppToast.toastMsgCenter(getActivity(),"Error Code:" + statusCode).show();
				}
				
			} else if ((Integer) (tag) == HttpTagConstantUtils.LOGIN_TAG) {
				if (statusCode == HttpStatus.SC_OK
						|| statusCode == HttpStatus.SC_CREATED) {
					UserEntity entity = (UserEntity) result;
//					entity.setMobile(username.getText().toString().trim());
//					DBService.saveOrUpdateUserEntity(entity);
					aCache.put("username", username.getText().toString().trim());
					aCache.put("password", password.getText().toString().trim());
					this.entity = entity;
					//友盟推送加别名
					new UMengPushUtil().addAlice(getActivity(), ""+entity.getU_id());
					
					
					updateUserInfo(entity);
					updateUserInfoShow();
					AppToast.toastMsgCenter(getActivity(), "登陆成功!").show();
					login_main_layout.setVisibility(View.GONE);
					my_info_layout.setVisibility(View.VISIBLE);
					
					try {
						//获取到用户信息后给测试页面发消息
						MyApplication.getInstance().getKvo().fire(KVOEvents.USER_LOGININ,"LOGIN_SUCCESS");
					} catch (Exception e) {
						
					}
					/*new GetUserInfoService(getActivity(),
							HttpTagConstantUtils.USER_INFO, this)
							.getUserInfo(entity.getToken());*/
				} else if (HttpStatus.SC_UNAUTHORIZED == statusCode) {
					AppToast.toastMsgCenter(getActivity(), "登录失败，用户名或者密码错误!")
							.show();
				} else {
					AppToast.toastMsgCenter(getActivity(), "网络异常，登录失败!").show();
					//AppToast.toastMsgCenter(getActivity(),"Error Code:" + statusCode).show();
				}

			}//THIRD_LOGIN_TAG
			else if ((Integer) (tag) == HttpTagConstantUtils.THIRD_LOGIN_TAG) {
				if (statusCode == HttpStatus.SC_OK
						|| statusCode == HttpStatus.SC_CREATED) {
					UserEntity entity = (UserEntity) result;
					//ACache.get(getActivity()).put("username", username.getText().toString().trim());
					//ACache.get(getActivity()).put("password", password.getText().toString().trim());
					this.entity = entity;
					updateUserInfo(entity);
					updateUserInfoShow();
					
					//友盟推送加别名
					new UMengPushUtil().addAlice(getActivity(), ""+entity.getU_id());
					
					AppToast.toastMsgCenter(getActivity(), "登陆成功!").show();
					login_main_layout.setVisibility(View.GONE);
					my_info_layout.setVisibility(View.VISIBLE);
					try {
						//获取到用户信息后给测试页面发消息
						MyApplication.getInstance().getKvo().fire(KVOEvents.USER_LOGININ,"LOGIN_SUCCESS");
					} catch (Exception e) {
						
					}
					MyApplication.isVisitor = false ;
					
					/*new GetUserInfoService(getActivity(),
							HttpTagConstantUtils.USER_INFO, this)
							.getUserInfo(entity.getToken());*/
				} else if (HttpStatus.SC_UNAUTHORIZED == statusCode) {
					dismissDialog();
					AppToast.toastMsgCenter(getActivity(), "授权失败").show();
				} else {
					dismissDialog();
					AppToast.toastMsgCenter(getActivity(), "授权失败").show();
				}

			}
		} catch (Exception e) {
			Log.e(TAG, e.toString());
			if ((Integer) (tag) == HttpTagConstantUtils.LOGIN_TAG){
				//AppToast.toastMsgCenter(getActivity(), "网络异常，登录失败!").show();
			}else{
				//AppToast.toastMsgCenter(getActivity(), "网络异常!").show();
			}
		} finally {
			if (null != dialogUploadImage) {
				dialogUploadImage.dismiss();
			}
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					
				}
			}, 1000);
		}
	}
	
	public static void displayImageByUri(ImageView imageView, String localPath,
			String url) {
		File file = new File(localPath);
		ImageLoader imageLoader = UserService.imageLoader;
		if (file.exists()) {
			imageLoader.displayImage("file://" + localPath, imageView,
					PhotoUtils.myPicImageOptions);
		} else {
			imageLoader.displayImage(url, imageView,
					PhotoUtils.myPicImageOptions);
		}
	}
	
	public void updateUserInfoShow(){
		try {
			//avatar_img.setBackgroundDrawable(null);
			String avatar_str = entity.getAvatar();
			if(!SHARE_MEDIA.WEIXIN.equals(ACache.get(getActivity()).getAsString("type"))
					&&!SHARE_MEDIA.SINA.equals(ACache.get(getActivity()).getAsString("type"))
					&&!SHARE_MEDIA.QZONE.equals(ACache.get(getActivity()).getAsString("type"))){
				String avatar = avatar_str == null ? "" : avatar_str.replaceAll("\\\\","");
				EasyLog.e("1.avatar-------->"+ avatar);
				
				if(!"".equals(avatar)){
					UserService.imageLoader.displayImage(""+avatar+ "?time="
							+ System.currentTimeMillis(), avatar_img,
							PhotoUtils.myPicImageOptions);
				}else{
					displayImageByUri(avatar_img,avatar,HttpClientUtils.USER_IMAGE_URL
							+ StringUtil.getPathByUid("" + aCache.getAsString("uid"))+"?time="
							+ System.currentTimeMillis());
				}
				
			}else{
				String avatar = ACache.get(getActivity()).getAsString("avatar") ;
				EasyLog.e("2.avatar-------->"+ avatar);
				//UrlImageViewHelper.setUrlDrawable(avatar_img, ACache.get(getActivity()).getAsString("avatar") + "", R.drawable.my_pic_default);
				UserService.imageLoader.displayImage(ACache.get(getActivity()).getAsString("avatar")+"?time="
						+ System.currentTimeMillis(), avatar_img,
						PhotoUtils.myPicImageOptions);
			}
			
			String gender = aCache.getAsString("gender");
			//sex.setText("1".equals(gender) ? "男" : "女");
			if("1".equals(gender)){
				 women.setVisibility(View.GONE);
				 man.setVisibility(View.VISIBLE);
			}else{
				women.setVisibility(View.VISIBLE);
				 man.findViewById(R.id.man).setVisibility(View.GONE);
			}
			
			if (null != entity) {
				nick_name.setText("" + entity.getNiakname());
				skinType.setText("" + HttpTagConstantUtils.getSkinTypeByKey(entity.getSkinType()));
				log.e("MineFrag", "===========skinType:"+entity.getSkinType());
				log.e("MineFrag", "===========skinType:"+HttpTagConstantUtils.getSkinTypeByKey(entity.getSkinType()));
				
				//regionNames.setText("" + entity.getRegionNames());
				String birthday = entity.getBirthday()==null||"".equals(entity.getBirthday())?"1990-01-01": entity.getBirthday();
				age.setText(DateUtil.getAge(birthday) + "岁");
				
				/*String region = entity.getRegion() == null
						|| "".equals(entity.getRegion()) ? "110101"
						:entity.getRegion().length() < 6 ? "110101"
								: entity.getRegion();
				
				for (String key : MyApplication.mProvinceMap.keySet()) {
					String code = MyApplication.mProvinceMap.get(key);
					if (code.equals(region.substring(0, 2) + "0000")) {
						mCurrentProviceName = key;
					}
					if (code.equals(region.substring(0, 4) + "00")) {
						mCurrentCityName = key;
					}
					if (code.equals(region)) {
						mCurrentDistrictName = key;
					}
				}*/
				
				mCurrentProviceName = ACache.get(getActivity()).getAsString("province");
				mCurrentCityName = ACache.get(getActivity()).getAsString("city");
				mCurrentDistrictName = ACache.get(getActivity()).getAsString("district");
				
				//mCurrentDistrictName = mZipcodeDatasMap.get(region);
			
				if(
						"北京".equals(mCurrentCityName)
						|| "上海".equals(mCurrentCityName)
						|| "重庆".equals(mCurrentCityName)
						|| "天津".equals(mCurrentCityName)
						|| "北京市".equals(mCurrentCityName)
						|| "上海市".equals(mCurrentCityName)
						|| "重庆市".equals(mCurrentCityName)
						|| "天津市".equals(mCurrentCityName)
						|| "县".equals(mCurrentCityName)
						|| null == mCurrentCityName
						|| "".equals(mCurrentCityName)
						){
					this.regionNames.setText("" + mCurrentProviceName + mCurrentDistrictName.replaceAll("bj", ""));
					
				}else if(
						"香港".equals(mCurrentCityName)
						||"台湾".equals(mCurrentCityName)
						){
					this.regionNames.setText("" + mCurrentProviceName + mCurrentDistrictName);
				}else if("澳门".equals(mCurrentCityName)){
					this.regionNames.setText("" + mCurrentProviceName);
				}else{
					this.regionNames.setText("" + mCurrentProviceName
								 + (mCurrentCityName != null ? mCurrentCityName
																.replaceAll("bj", "")
																.replaceAll("sjz", "")
																.replaceAll("xt", "")
																.replaceAll("zjk", "")
																: mCurrentCityName) );
				}
				
				//this.regionNames.setText(""+ ACache.get(getActivity()).getAsString("regionNames"));

				Log.e(TAG, "mCurrentProviceName-->"+mCurrentProviceName);
				Log.e(TAG, "mCurrentCityName-->"+mCurrentCityName);
				//this.regionNames.setText("" + mCurrentProviceName+ mCurrentCityName);
			}// 否则赋予默认值
			   
			String integral = ACache.get(getActivity()).getAsObject("integral")+"";
			Log.e(TAG, "integral = "+this.entity.getIntegral());
			Log.e(TAG, "level = "+StringUtil.getLevel(this.entity.getIntegral()));
			level.setText(""+StringUtil.getLevel(Integer.valueOf(integral==null||"null".equals(integral)|| "".equals(integral)?"0":integral)));
			String home_title_str = getResources().getString(
					R.string.mine_title_name);
			title_name.setText("" + home_title_str);
			my_master.setOnClickListener(this);
			my_integral.setOnClickListener(this);
			sys_settings.setOnClickListener(this);
			address_settings.setOnClickListener(this);
			my_collect.setOnClickListener(this);
			
			sweep_scan.setOnClickListener(this);
			my_info.setOnClickListener(this);
			my_message_layout.setOnClickListener(this);
			sweep_scan.setOnClickListener(this);
		} catch (Exception e) {
			
		}
		
	}

	public void updateUserInfo(UserEntity entity) {
		try {
			aCache.put("Token", entity.getToken());// 保存Token
			ACache.get(getActivity()).put("nickname", entity.getNiakname());
			ACache.get(getActivity()).put("mobile", entity.getMobile());
			ACache.get(getActivity()).put("username", entity.getMobile());
			ACache.get(getActivity()).put("skin_type", entity.getSkinType());
			ACache.get(getActivity()).put("region_name", entity.getRegionNames());
			ACache.get(getActivity()).put("region", entity.getRegion());
			ACache.get(getActivity()).put("realname", entity.getRealname());
			ACache.get(getActivity()).put("birthday", entity.getBirthday());
			ACache.get(getActivity()).put("gender", entity.getGender());
			ACache.get(getActivity()).put("avatar", entity.getAvatar());
			ACache.get(getActivity()).put("integral", entity.getIntegral());
		} catch (Exception e) {
			
		}
		
	}
	
	public String getActivityName(){
		return "我的" ;
	}
	
	
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
	public void onEvent(String event, Object... args) {
		if(event.equals(KVOEvents.GET_PUSH_NUM)){
			PushEntity pushEntity=(PushEntity)args[0];
			log.e("SkinRunMainActivity", "============收到的推送值："+pushEntity.getMyMessage()+"   "+pushEntity.getMyTestMessage());
			if(pushEntity.getMyMessage()>0){
				my_message_badge.setVisibility(View.VISIBLE);
				my_message_badge.setText(""+pushEntity.getMyMessage());
			}else{
				my_message_badge.hide();
			}
			
			if(pushEntity.getMyTestMessage()>0){
				my_test_badge.setVisibility(View.VISIBLE);
				my_test_badge.setText(""+pushEntity.getMyTestMessage());
			}else{
				my_test_badge.hide();
			}
			
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		MyApplication.resume_index = 4 ;
		try {
			MyApplication.getInstance().getKvo().removeObserver(KVOEvents.USER_LOGININ, this);
			MyApplication.getInstance().getKvo().removeObserver(KVOEvents.GET_PUSH_NUM, this);
			MyApplication.getInstance().getKvo().removeObserver(KVOEvents.GET_MYMESSAGE_COUNT, this);
		} catch (Exception e) {
			e.printStackTrace();
		} catch(OutOfMemoryError e){
			e.printStackTrace();
		}
		
	}

}

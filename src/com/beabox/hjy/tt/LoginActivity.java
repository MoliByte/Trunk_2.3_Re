package com.beabox.hjy.tt;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpStatus;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.base.entity.UserEntity;
import com.app.base.init.ACache;
import com.app.base.init.BaseActivity;
import com.app.base.init.MyApplication;
import com.app.service.LoginAsynTaskService;
import com.app.service.PostThirdLoginService;
import com.base.app.utils.StringUtil;
import com.base.app.utils.UMShareUtil;
import com.base.app.utils.UMengPushUtil;
import com.base.dialog.lib.Effectstype;
import com.base.dialog.lib.NiftyDialogBuilder;
import com.base.service.action.constant.HttpTagConstantUtils;
import com.base.service.impl.HttpClientUtils;
import com.base.supertoasts.util.AppToast;
import com.umeng.message.PushAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners.UMAuthListener;
import com.umeng.socialize.controller.listener.SocializeListeners.UMDataListener;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;

/**
 * 登陆页面
 * Author zhup
 */
@SuppressWarnings("serial")
public class LoginActivity extends BaseActivity{
	private final static String  TAG = "LoginActivity" ;
	//////////////
	private EditText username ;
	private EditText password ;
	private ImageView backBtn ;
	private View login_btn ,un_login_btn;
	private View toRegisterBtn ;
	NiftyDialogBuilder dialogUploadImage ;
	
	private View forget_pwd ;//忘记密码
	
	//第三方SSO登录
	private ImageView weixin_logo ; //微信
	private ImageView qq_logo ;		//QQ
	private ImageView weibo_logo ;	//微博
	
	private static boolean isQQInstalled ; 
	private static boolean isWeiXinInstalled ; 
	//private static boolean isWeiboInstalled ;
	private int from;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		MyApplication.getInstance().addActivity(this);
		PushAgent.getInstance(this).onAppStart();
		super.onCreate(savedInstanceState);
		from=getIntent().getIntExtra("FROM", 0);
		
		setContentView(R.layout.activity_login);
		
		String appId = "100381049";//"1104063317";//"100424468";
		String appKey = "3ad747d61375966b12e2862ef8a70e61";//"XNIB4GzYvU8IbYlK";//"c7394704798a158208a74ab60104f0ba";
		
		QZoneSsoHandler qqSsoHandler = new QZoneSsoHandler(this,
				appId, appKey);
		qqSsoHandler.setTargetUrl("http://www.umeng.com");
		qqSsoHandler.addToSocialSDK();
		
		isQQInstalled = qqSsoHandler.isClientInstalled();
		
		String wxappId = "wx4297582bc8743dc3";
		String wxappSecret = "fad627ead5f6622ecacde5caf71895ec";
		// 添加微信平台
		UMWXHandler wxHandler = new UMWXHandler(this, wxappId,
				wxappSecret);
		
		wxHandler.addToSocialSDK();
		isWeiXinInstalled = wxHandler.isClientInstalled();
		setupView();
	}

	@Override
	public void setupView() {
		backBtn = (ImageView) findViewById(R.id.backBtn);
		login_btn = findViewById(R.id.login_btn);
		un_login_btn = findViewById(R.id.un_login_btn);
		username = (EditText) findViewById(R.id.username);
		password = (EditText) findViewById(R.id.password);
		toRegisterBtn = findViewById(R.id.toRegisterBtn);
		forget_pwd = findViewById(R.id.forget_pwd);
		
		weixin_logo = (ImageView) findViewById(R.id.weixin_logo);
		qq_logo = (ImageView) findViewById(R.id.qq_logo);
		weibo_logo = (ImageView) findViewById(R.id.weibo_logo);
		
		String username = aCache.getAsString("username")==null?"":aCache.getAsString("username");
		String password = aCache.getAsString("password")==null?"":aCache.getAsString("password");
		/*this.username.setText(username+"");
		this.username.setSelection(username.length());
		this.password.setText(password+"");
		this.password.setSelection(password.length());*/
		
		if(!SHARE_MEDIA.WEIXIN.equals(ACache.get(this).getAsString("type"))
				&&!SHARE_MEDIA.SINA.equals(ACache.get(this).getAsString("type"))
				&&!SHARE_MEDIA.QZONE.equals(ACache.get(this).getAsString("type"))){
				this.username.setText(username + "");
				this.username.setSelection(username.length());
				
				this.password.setText(password+"");
				this.password.setSelection(password.length());
				if(!StringUtil.isBlank(username) && !StringUtil.isBlank(password)){
					this.un_login_btn.setVisibility(View.GONE);
					login_btn.setVisibility(View.VISIBLE);
				}
				
		}else{
			this.username.setText("");
			this.password.setText("");
		}
		
		this.username.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View arg0, boolean isfocus) {
				
				if(isfocus){
					LoginActivity.this.username.setHint("");
					LoginActivity.this.un_login_btn.setVisibility(View.GONE);
					LoginActivity.this.login_btn.setVisibility(View.VISIBLE);
				}else{
					LoginActivity.this.username.setHint("请输入手机号");
					LoginActivity.this.un_login_btn.setVisibility(View.VISIBLE);
					LoginActivity.this.login_btn.setVisibility(View.GONE);
				}
			}
		});
		this.password.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View arg0, boolean isfocus) {
				
				if(isfocus){
					LoginActivity.this.password.setHint("");
					LoginActivity.this.un_login_btn.setVisibility(View.GONE);
					LoginActivity.this.login_btn.setVisibility(View.VISIBLE);
				}else{
					LoginActivity.this.password.setHint("请输入密码");
					LoginActivity.this.un_login_btn.setVisibility(View.VISIBLE);
					LoginActivity.this.login_btn.setVisibility(View.GONE);
				}
			}
		});
		

		addListener();

	}

	@Override
	public void addListener() {
		backBtn.setOnClickListener(this);
		login_btn.setOnClickListener(this);
		toRegisterBtn.setOnClickListener(this);
		forget_pwd.setOnClickListener(this);
		
		weixin_logo.setOnClickListener(this);
		qq_logo.setOnClickListener(this);
		weibo_logo.setOnClickListener(this);
	}
	
	public void login() {
		final String loginName = username.getText().toString();
       final  String password_ = password.getText().toString();

        if (StringUtil.isBlank(loginName)) {
        	AppToast.toastMsgCenter(this, getString(R.string.login_loginNameBlankMessage), Toast.LENGTH_LONG).show();
            return;
        }

        if (StringUtil.isEmpty(password_)) {
            AppToast.toastMsgCenter(this, getString(R.string.login_passwordBlankMessage), Toast.LENGTH_LONG).show();
            return;
        }
        
        dialogUploadImage = NiftyDialogBuilder.getInstance(this, R.layout.dialog_login_layout);
		final View text = LayoutInflater.from(this).inflate(R.layout.dialog_login_view, null);
		TextView t = (TextView) text.findViewById(R.id.loading_text);
		t.setText("正在登陆....");
		dialogUploadImage.withTitle(null)
		.withMessage(null)
		.withEffect(Effectstype.Fadein)
		.withDuration(100)
		.isCancelableOnTouchOutside(false)
		.setCustomView(text, this.getApplicationContext()).show();
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				new LoginAsynTaskService(LoginActivity.this, HttpTagConstantUtils.LOGIN_TAG,
						LoginActivity.this).doLogin(loginName, password_);
				
			}
		}, 500);
	}


	@Override
    public void onClick(View v) {
		Intent intent = null ;
        switch (v.getId()){
        case R.id.weibo_logo:
			login(SHARE_MEDIA.SINA);
			break;
		case R.id.weixin_logo:
			if (isWeiXinInstalled) {
				login(SHARE_MEDIA.WEIXIN);
			} else {
				AppToast.toastMsgCenter(this, "请先安载微信客户端!").show();
			}
			break;
		case R.id.qq_logo:
			if (isQQInstalled) {
				login(SHARE_MEDIA.QZONE);
			} else {
				AppToast.toastMsgCenter(this, "请先安载QQ客户端!").show();
			}
			break;
		case R.id.backBtn:
			// intent = new Intent(this,SkinRunMainActivity.class);
			// intent.putExtra("index", 0);
			// startActivity(intent);
			finish();
			overridePendingTransition(R.anim.activity_enter_from_left,
					R.anim.activity_exit_to_right);

			break;
		case R.id.login_btn:
			login();
			break;
		case R.id.forget_pwd:
			intent = new Intent(this, ResetPassword.class);
			startActivity(intent);
			overridePendingTransition(R.anim.activity_enter_from_right,
					R.anim.activity_exit_to_left);
			break;
		case R.id.toRegisterBtn:// 到注册页面
			intent = new Intent(this, RegisterActivity_1.class);
			startActivity(intent);
			overridePendingTransition(R.anim.activity_enter_from_right,
					R.anim.activity_exit_to_left);
			break;
		default:
			break;
        }

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
		/*UMShareUtil.getInstance().getUmsocialService()*/mController.doOauthVerify(this, platform,
				new UMAuthListener() {

					@Override
					public void onStart(SHARE_MEDIA platform) {
						dialogUploadImage = NiftyDialogBuilder.getInstance(LoginActivity.this,
								R.layout.dialog_login_layout);
						final View text = LayoutInflater.from(LoginActivity.this).inflate(
								R.layout.dialog_login_view, null);
						TextView t = (TextView) text.findViewById(R.id.loading_text);
						t.setText("正在登录....");
						dialogUploadImage.withTitle(null).withMessage(null)
								.withEffect(Effectstype.Fadein).withDuration(100)
								.isCancelableOnTouchOutside(false)
								.setCustomView(text, LoginActivity.this)
								.show();
					}

					@Override
					public void onError(SocializeException e,
							SHARE_MEDIA platform) {
						dismissDialog();
						AppToast.toastMsgCenter(LoginActivity.this, e+",授权失败").show();
					}

					@Override
					public void onComplete(Bundle value, SHARE_MEDIA platform) {
						// 获取uid
						String uid = value.getString("uid");
						if (!TextUtils.isEmpty(uid)) {
							// uid不为空，获取用户信息
							getUserInfo(platform);
						} else {
							dismissDialog();
							AppToast.toastMsgCenter(LoginActivity.this, "授权失败").show();
						}
					}

					@Override
					public void onCancel(SHARE_MEDIA platform) {
						dismissDialog();
						AppToast.toastMsgCenter(LoginActivity.this, "授权取消").show();
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
		UMShareUtil.getInstance().getUmsocialService().getPlatformInfo(this, platform,
				new UMDataListener() {

					@Override
					public void onStart() {
						if(dialogUploadImage!=null && dialogUploadImage.isShowing()){
							dialogUploadImage.dismiss();
						}
						dialogUploadImage = NiftyDialogBuilder.getInstance(LoginActivity.this,
								R.layout.dialog_login_layout);
						final View text = LayoutInflater.from(LoginActivity.this).inflate(
								R.layout.dialog_login_view, null);
						TextView t = (TextView) text.findViewById(R.id.loading_text);
						t.setText("正在同步用户信息....");
						dialogUploadImage.withTitle(null).withMessage(null)
								.withEffect(Effectstype.Fadein).withDuration(100)
								.isCancelableOnTouchOutside(false)
								.setCustomView(text, LoginActivity.this)
								.show();
					}

					@Override
					public void onComplete(int status, final Map<String, Object> info) {
						
						if (info != null) {//登录成功
							final Map<String,String> param_map = new HashMap<String,String>();
							Log.e("user info ", ">>>"+info.toString());
							//AppToast.toastMsgCenter(this, info.toString(),5000).show();
							if(platform == SHARE_MEDIA.WEIXIN){
								ACache.get(LoginActivity.this).put("weixin", info.toString());
								param_map.put("type", "weixin");
								param_map.put("uid", info.get("uid")+"");
								param_map.put("gender", info.get("gender")+"");
								param_map.put("screen_name", info.get("screen_name")+"");
								param_map.put("openid", info.get("openid")+"");
								param_map.put("profile_image_url", info.get("profile_image_url")+"");
							}else if(platform == SHARE_MEDIA.SINA){
								ACache.get(LoginActivity.this).put("sina", info.toString());
								param_map.put("type", "weibo");//sina weibo
								param_map.put("uid", (info.get("uid")+""));
								param_map.put("gender", ("1".equals(info.get("gender")+"")?"男":"女")+"");
								param_map.put("screen_name", info.get("screen_name")+"");
								param_map.put("openid", HttpClientUtils.md5(info.get("uid")+""));
								param_map.put("profile_image_url", info.get("profile_image_url")+"");
							}else if(platform == SHARE_MEDIA.QZONE){
								ACache.get(LoginActivity.this).put("qq", info.toString());
								param_map.put("type", "qq");
								param_map.put("uid", info.get("uid")+"");
								param_map.put("gender", info.get("gender")+"");
								param_map.put("screen_name", info.get("screen_name")+"");
								param_map.put("openid", info.get("openid")+"");
								param_map.put("profile_image_url", info.get("profile_image_url")+"");
								
							}
							//AppToast.toastMsgCenter(this, param_map.toString()+"",10000).show();
							Log.e(TAG, platform+"-->"+param_map.toString());
							new Handler().postDelayed(new Runnable() {

								@Override
								public void run() {
									new PostThirdLoginService(LoginActivity.this,
											HttpTagConstantUtils.THIRD_LOGIN_TAG, LoginActivity.this).doPostThirdLogin(param_map);

								}
							}, 500);
							
						}else{
							dismissDialog();
							AppToast.toastMsgCenter(LoginActivity.this, "授权失败").show();
						}
					}
				});
	}
	
	@Override
	public void onStop() {
		super.onStop();
		/*if(dialogUploadImage!=null && dialogUploadImage.isShowing()){
			dialogUploadImage.dismiss();
		}*/
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		/*if(dialogUploadImage!=null && dialogUploadImage.isShowing()){
			dialogUploadImage.dismiss();
		}*/
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
//			Intent intent = new Intent(this,SkinRunMainActivity.class);
//			intent.putExtra("index", 0);
//        	startActivity(intent);
        	finish();
        	overridePendingTransition(R.anim.activity_enter_from_left, R.anim.activity_exit_to_right);
			return true ;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void sendMessageHandler(int messageCode) {

	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
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
	}
	
	public void dataCallBack(Object tag,  int statusCode,Object result) {
		try {
			Log.e(TAG, ""+result);
			if((Integer)(tag) == HttpTagConstantUtils.LOGIN_TAG){
				if(null!=dialogUploadImage){
					dialogUploadImage.dismiss();
				}
				if(isSuccess(statusCode)){
					UserEntity entity = (UserEntity) result;
//					entity.setId(1);
//					entity.setMobile(username.getText().toString().trim());
//					DBService.saveOrUpdateUserEntity(entity);
					aCache.put("Token", entity.getToken());//保存Token
					aCache.put("username", username.getText().toString().trim());
					aCache.put("password", password.getText().toString().trim());
					
					//友盟推送加别名
					new UMengPushUtil().addAlice(this, ""+entity.getU_id());
					
					
					AppToast.toastMsgCenter(this, "登陆成功!").show();
					if(from==1){
						Intent intent = new Intent(this,SkinRunMainActivity.class	);
						startActivity(intent);
					}
					
					finish();
				}else if(HttpStatus.SC_UNAUTHORIZED == statusCode){
					AppToast.toastMsgCenter(this, "登录失败，用户名或者密码错误!", 2000).show();
				}else{
					AppToast.toastMsgCenter(this, "登陆失败!").show();
					//AppToast.toastMsgCenter(this, "Error Code:"+statusCode, 2000).show();
				}
				
			}
			
			else if ((Integer) (tag) == HttpTagConstantUtils.THIRD_LOGIN_TAG) {
				if (statusCode == HttpStatus.SC_OK
						|| statusCode == HttpStatus.SC_CREATED) {
					UserEntity entity = (UserEntity) result;
					aCache.put("Token", entity.getToken());//保存Token
					aCache.put("username", username.getText().toString().trim());
					aCache.put("password", password.getText().toString().trim());
					dismissDialog();
					AppToast.toastMsgCenter(this, "登陆成功!").show();
					
					//友盟推送加别名
					new UMengPushUtil().addAlice(this, ""+entity.getU_id());
					
					if(from==1){
						Intent intent = new Intent(this,SkinRunMainActivity.class	);
						startActivity(intent);
					}
					finish();
				} else if (HttpStatus.SC_UNAUTHORIZED == statusCode) {
					dismissDialog();
					AppToast.toastMsgCenter(this, "授权失败").show();
				} else {
					dismissDialog();
					AppToast.toastMsgCenter(this, "授权失败").show();
				}

			}
		} catch (Exception e) {
			AppToast.toastMsgCenter(this, "登陆失败!").show();
		} finally {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					if(null!=dialogUploadImage){
						dialogUploadImage.dismiss();
					}
				}
			}, 1000);
		}
	}
	
	@Override
    protected String getActivityName() {
        return "登录页面";
    }



}

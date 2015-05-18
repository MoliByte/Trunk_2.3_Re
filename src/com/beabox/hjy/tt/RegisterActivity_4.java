package com.beabox.hjy.tt;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.base.entity.UserEntity;
import com.app.base.init.BaseActivity;
import com.app.base.init.MyApplication;
import com.app.service.LoginAsynTaskService;
import com.base.app.utils.DBService;
import com.base.dialog.lib.Effectstype;
import com.base.dialog.lib.NiftyDialogBuilder;
import com.base.service.action.constant.HttpTagConstantUtils;
import com.base.supertoasts.util.AppToast;
import com.umeng.message.PushAgent;

/**
 * 注册成功后 Author zhup
 */
@SuppressWarnings("serial")
public class RegisterActivity_4 extends BaseActivity {
	private final static String TAG = "RegisterActivity_4";

	private ImageView backBtn;
	private View loginBtn;// 注册成功到登录页面登录
	String tel_num, verify_code, password;
	TextView hint_text;// 您可以使用手机号进行登陆肌肤管家

	NiftyDialogBuilder dialogUploadImage;
	private int from=0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		MyApplication.getInstance().addActivity(this);
    	PushAgent.getInstance(this).onAppStart();
		from=getIntent().getIntExtra("FROM", 0);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_4);
		setupView();
	}

	@Override
	public void setupView() {
		tel_num = getIntent().getStringExtra("tel_num");
		verify_code = getIntent().getStringExtra("verify_code");
		password = getIntent().getStringExtra("password");
		backBtn = (ImageView) findViewById(R.id.backBtn);
		hint_text = (TextView) findViewById(R.id.hint_text);
		hint_text.setText("您可以使用手机号" + (tel_num == null ? "" : tel_num)
				+ "登陆肌肤管家!");

		loginBtn =  findViewById(R.id.loginBtn);

		addListener();

	}

	@Override
	public void addListener() {
		backBtn.setOnClickListener(this);
		loginBtn.setOnClickListener(this);
		login();
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.backBtn:
			finish();
			break;
		case R.id.loginBtn:// 注册成功到登录页面登录
			
			login();

			break;
		default:
			break;
		}

	}

	private void login() {
		dialogUploadImage = NiftyDialogBuilder.getInstance(this,
				R.layout.dialog_login_layout);
		final View text = LayoutInflater.from(this).inflate(
				R.layout.dialog_login_view, null);
		TextView t = (TextView) text.findViewById(R.id.loading_text);
		t.setText("正在登陆....");
		dialogUploadImage.withTitle(null).withMessage(null)
				.withEffect(Effectstype.Fadein).withDuration(100)
				.isCancelableOnTouchOutside(false)
				.setCustomView(text, this.getApplicationContext()).show();
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				new LoginAsynTaskService(RegisterActivity_4.this,
						HttpTagConstantUtils.LOGIN_TAG, RegisterActivity_4.this)
						.doLogin(tel_num, password);

			}
		}, 1000);

	}

	@Override
	public void sendMessageHandler(int messageCode) {

	}

	public void dataCallBack(Object tag, int statusCode, Object result) {
		try {
			Log.e(TAG, "" + result);
			if ((Integer) (tag) == HttpTagConstantUtils.LOGIN_TAG) {
				if(null!=dialogUploadImage){
					dialogUploadImage.dismiss()	;
				}
				DBService.deleteSession();
				UserEntity entity = (UserEntity) result;
				entity.setMobile(tel_num);
				DBService.saveOrUpdateUserEntity(entity);
				aCache.put("Token", entity.getToken());// 保存Token
				AppToast.toastMsgCenter(this, "登陆成功!", 2000).show();
//				Intent intent = new Intent(this, SkinRunMainActivity.class);
//				intent.putExtra("index", 4);
//				startActivity(intent);
				MyApplication.resume_index = 4 ;
				if(from==1){
					Intent intent=new Intent(this, SkinRunMainActivity.class);
					startActivity(intent);
				}
				
				finish();
				overridePendingTransition(R.anim.activity_enter_from_right,
						R.anim.activity_exit_to_left);
				
			}
		} catch (Exception e) {

		} finally {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					if(null!=dialogUploadImage){
						dialogUploadImage.dismiss()	;
					}
				}
			}, 1000);
		}
	}
	
	@Override
	protected String getActivityName() {
		return "注册页面--4";
	}

}

package com.beabox.hjy.tt;


import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.base.init.BaseActivity;
import com.app.base.init.MyApplication;
import com.app.service.LoginAsynTaskService;
import com.app.service.ModifiedPasswordService;
import com.base.app.utils.DBService;
import com.base.app.utils.StringUtil;
import com.base.dialog.lib.Effectstype;
import com.base.dialog.lib.NiftyDialogBuilder;
import com.base.service.action.constant.HttpTagConstantUtils;
import com.base.supertoasts.util.AppToast;
import com.umeng.message.PushAgent;
/**
 * 密码修改页面
 * @author zhup
 *
 */
@SuppressWarnings("serial")
public class PwdChangeActivity extends BaseActivity {
	
	private final static String TAG = "PwdChangeActivity" ;
	private EditText currnent_pwd ;
	private EditText new_password ;
	private View save_password_btn ;
	private ImageView backBtn ;
	
	NiftyDialogBuilder dialogUploadImage ;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		MyApplication.getInstance().addActivity(this);
    	PushAgent.getInstance(this).onAppStart();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modify_password);
		setupView();
		addListener();
	}

	@Override
	public void setupView() {
		currnent_pwd = (EditText) findViewById(R.id.currnent_pwd);
		new_password = (EditText) findViewById(R.id.new_password);
		save_password_btn = findViewById(R.id.save_password_btn);
		backBtn = (ImageView) findViewById(R.id.backBtn);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.backBtn:
			finish();
			break;
		case R.id.save_password_btn:
			final String oldPassword = currnent_pwd.getText().toString() ;
			final String newPassword = new_password.getText().toString() ;
			
			if(org.kymjs.aframe.utils.StringUtils.isEmpty(oldPassword)){
				AppToast.toastMsgCenter(getApplicationContext(), "请输入原始密码!", 1500).show();
				return ;
			}
			
			if(org.kymjs.aframe.utils.StringUtils.isEmpty(newPassword)){
				AppToast.toastMsgCenter(getApplicationContext(), "请输入新密码!", 1500).show();
				return ;
			}
			final String token = aCache.getAsString("Token");
			dialogUploadImage = NiftyDialogBuilder.getInstance(this, R.layout.dialog_login_layout);
			final View text = LayoutInflater.from(this).inflate(R.layout.dialog_login_view, null);
			TextView t = (TextView) text.findViewById(R.id.loading_text);
			t.setText("修改密码中....");
			dialogUploadImage.withTitle(null)
			.withMessage(null)
			.withEffect(Effectstype.Fadein)
			.withDuration(100)
			.isCancelableOnTouchOutside(false)
			.setCustomView(text, this.getApplicationContext()).show();
			new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					
					new ModifiedPasswordService(getApplicationContext(),
							HttpTagConstantUtils.MOD_PASSWORD,
							PwdChangeActivity.this).doPasswordChange(token,
							oldPassword, newPassword);
				}
			}, 500);
			break;

		default:
			break;
		}
	}

	@Override
	public void addListener() {
		backBtn.setOnClickListener(this);
		save_password_btn.setOnClickListener(this);
	}

	@Override
	public void sendMessageHandler(int messageCode) {
		
	}

	@Override
	public void dataCallBack(Object tag,  int statusCode,Object result) {
		try {
			if ((Integer) (tag) == HttpTagConstantUtils.MOD_PASSWORD) {
				if (isSuccess(statusCode)) {
					DBService.deleteSession();
					AppToast.toastMsg(getApplicationContext(), "密码修改成功!")
							.show();
//					Intent intent = new Intent(this, LoginActivity.class);
//					startActivity(intent);
					if (dialogUploadImage != null) {
						dialogUploadImage.dismiss();
					}
					login();
					//finish();
				} else {
					AppToast.toastMsg(getApplicationContext(),"修改失败!").show();
				}
			}else if ((Integer) (tag) == HttpTagConstantUtils.LOGIN_TAG) {
				if (isSuccess(statusCode)) {
					//AppToast.toastMsgCenter(this, "登陆成功!").show();
					aCache.put("password", new_password.getText().toString());
//					if (dialogUploadImage != null) {
//						dialogUploadImage.dismiss();
//					}
					finish();
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
	
	public void login() {
		try {
			//UserEntity entity = DBService.getUserEntity() ;
			//Log.e("entity ", entity.toString());
			final String loginName = aCache.getAsString("username")==null?"":aCache.getAsString("username");
			final String password_ = new_password.getText().toString();
			Log.e(TAG, loginName+","+password_);
			
			if (StringUtil.isBlank(loginName)) {
				AppToast.toastMsgCenter(this,
						getString(R.string.login_loginNameBlankMessage),
						Toast.LENGTH_LONG).show();
				return;
			}

			if (StringUtil.isEmpty(password_)) {
				AppToast.toastMsgCenter(this,
						getString(R.string.login_passwordBlankMessage),
						Toast.LENGTH_LONG).show();
				return;
			}

			dialogUploadImage = NiftyDialogBuilder.getInstance(this,
					R.layout.dialog_login_layout);
			final View text = LayoutInflater.from(this).inflate(
					R.layout.dialog_login_view, null);
			TextView t = (TextView) text.findViewById(R.id.loading_text);
			t.setText("登陆中....");
			dialogUploadImage.withTitle(null).withMessage(null)
					.withEffect(Effectstype.Fadein).withDuration(100)
					.isCancelableOnTouchOutside(false)
					.setCustomView(text, getApplicationContext())
					.show();
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					new LoginAsynTaskService(PwdChangeActivity.this,
							HttpTagConstantUtils.LOGIN_TAG,PwdChangeActivity.this).doLogin(
							loginName, password_);

				}
			}, 500);
		} catch (Exception e) {
			Log.e("", ""+e.toString());
			
		}
		
	}
	
	@Override
	protected String getActivityName() {
		return "密码修改";
	}

}

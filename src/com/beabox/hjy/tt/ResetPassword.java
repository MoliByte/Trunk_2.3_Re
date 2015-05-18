package com.beabox.hjy.tt;


import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.base.init.BaseActivity;
import com.app.base.init.MyApplication;
import com.app.service.GetVerifyCodeNewAsynTaskService;
import com.app.service.PutVerifyCodeNewAsynTaskService;
import com.app.service.ResetPasswordService;
import com.base.app.utils.StringUtil;
import com.base.dialog.lib.Effectstype;
import com.base.dialog.lib.NiftyDialogBuilder;
import com.base.service.action.constant.HttpTagConstantUtils;
import com.base.supertoasts.util.AppToast;
import com.umeng.message.PushAgent;

public class ResetPassword extends BaseActivity {

	private final static String TAG = "ResetPassword";

	private EditText verify_code, telephone, new_password;
	private ImageView backBtn;
	// private ImageView register_next;
	private View save_password_btn;
	private Button get_verify_code_again;

	int count = 60;
	String telephone_, verify_code_, new_password_;
	private Handler handler = new Handler();

	Runnable runnable = new Runnable() {

		@Override
		public void run() {
			count--;
			if (count == 0) {
				count = 60;
				get_verify_code_again.setClickable(true);
				get_verify_code_again.setText("获取验证码");
			} else {
				get_verify_code_again.setText("(" + count + "秒后)重新获取");
				get_verify_code_again.setClickable(false);
				handler.postDelayed(this, 1000);
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(this);
    	PushAgent.getInstance(this).onAppStart();
		setContentView(R.layout.reset_password);
		setupView();
	}

	@Override
	public void setupView() {
		//handler.postDelayed(runnable, 1000);
		backBtn = (ImageView) findViewById(R.id.backBtn);
		save_password_btn = findViewById(R.id.save_password_btn);
		verify_code = (EditText) findViewById(R.id.verify_code);
		telephone = (EditText) findViewById(R.id.telephone);
		new_password = (EditText) findViewById(R.id.new_password);
		get_verify_code_again = (Button) findViewById(R.id.get_verify_code_again);

		addListener();

	}

	@Override
	public void addListener() {
		backBtn.setOnClickListener(this);
		get_verify_code_again.setOnClickListener(this);
		save_password_btn.setOnClickListener(this);
		// get_verify_code_again.setOnClickListener(this);//重新获取验证码
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.backBtn:
			finish();
			break;
		case R.id.get_verify_code_again:
			
			telephone_ = telephone.getText().toString();
			verify_code_ = this.verify_code.getText().toString();
			new_password_ = this.new_password.getText().toString();
			if (StringUtil.isEmpty(telephone_)) {
				AppToast.toastMsgCenter(getApplicationContext(), "请填写您的手机号码!",
						TOAST_TIME).show();
				return;
			}
			if (!StringUtil.isMobile(telephone_)) {
				AppToast.toastMsgCenter(getApplicationContext(), "请填写正确的手机号码!",
						TOAST_TIME).show();
				return;
			}
			handler.postDelayed(runnable, 1000);
			// AppToast.toastMsgCenter(getApplicationContext(), "已发送!", 2000).show();
//			new GetVerifyCodeAsynTaskService(getApplicationContext(),
//					HttpTagConstantUtils.VERIFY_CODE, this).doGetVerifyCode(
//					telephone_, HttpTagConstantUtils.RESET_PASSWORD_TYPE);
			//发送验证码
			new GetVerifyCodeNewAsynTaskService(this
					.getApplicationContext(),
					HttpTagConstantUtils.VERIFY_CODE,
					this).doGetVerifyCode(telephone_,
					HttpTagConstantUtils.RESET_PASSWORD_TYPE);
			break;
		case R.id.save_password_btn:// 保存新密码
			telephone_ = telephone.getText().toString();
			verify_code_ = this.verify_code.getText().toString();
			new_password_ = this.new_password.getText().toString();
			if (StringUtil.isEmpty(telephone_)) {
				AppToast.toastMsgCenter(getApplicationContext(), "请填写您的手机号码!",
						TOAST_TIME).show();
				return;
			}
			if (!StringUtil.isMobile(telephone_)) {
				AppToast.toastMsgCenter(getApplicationContext(), "请填写正确的手机号码!",
						TOAST_TIME).show();
				return;
			}
			if (StringUtil.isEmpty(verify_code_)) {
				AppToast.toastMsgCenter(getApplicationContext(), "请填写您收到的验证码!",
						TOAST_TIME).show();
				return;
			}
			if (StringUtil.isEmpty(new_password_)) {
				AppToast.toastMsgCenter(getApplicationContext(), "请输入您的新密码!",
						TOAST_TIME).show();
				return;
			}

			dialogUploadImage = NiftyDialogBuilder.getInstance(this,
					R.layout.dialog_login_layout);
			final View text = LayoutInflater.from(this).inflate(
					R.layout.dialog_login_view, null);
			TextView t = (TextView) text.findViewById(R.id.loading_text);
			t.setText("校验中....");
			dialogUploadImage.withTitle(null).withMessage(null)
					.withEffect(Effectstype.Fadein).withDuration(100)
					.isCancelableOnTouchOutside(false)
					.setCustomView(text, this.getApplicationContext()).show();
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					// 校验验证码
//					new PutVerifyCodeAsynTaskService(getApplicationContext(),
//							HttpTagConstantUtils.CHECK_CODE,
//							ResetPassword.this).doPutVerifyCode(telephone_,
//							verify_code_);
					
					// 校验验证码
					new PutVerifyCodeNewAsynTaskService(getApplicationContext(),
							HttpTagConstantUtils.CHECK_CODE,
							ResetPassword.this).doPutVerifyCode(telephone_,
									verify_code_);

				}
			}, 500);
			
			

			break;
		default:
			break;
		}

	}

	public void resetPwd() {
		dialogUploadImage = NiftyDialogBuilder.getInstance(this,
				R.layout.dialog_login_layout);
		final View text = LayoutInflater.from(this).inflate(
				R.layout.dialog_login_view, null);
		TextView t = (TextView) text.findViewById(R.id.loading_text);
		t.setText("正在修改....");
		dialogUploadImage.withTitle(null).withMessage(null)
				.withEffect(Effectstype.Fadein).withDuration(100)
				.isCancelableOnTouchOutside(false)
				.setCustomView(text, this.getApplicationContext()).show();
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				new ResetPasswordService(getApplicationContext(),
						HttpTagConstantUtils.RESET_PWD, ResetPassword.this)
						.doResetPassword(aCache.getAsString("Token"),
								verify_code_, new_password_);
			}
		}, 500);
	}

	@Override
	public void sendMessageHandler(int messageCode) {

	}

	public void dataCallBack(Object tag, int statusCode, Object result) {
		try {
			Log.e(TAG, "result --- >" + result);
			
			if ((Integer) (tag) == HttpTagConstantUtils.CHECK_CODE) {
				if (null != dialogUploadImage) {
					dialogUploadImage.dismiss();
				}
				JSONObject obj = new JSONObject(result.toString()) ;
				int code = obj.optInt("code") ;
				if (isSuccess(statusCode) && isSuccess(code)) {
					resetPwd() ;
				} else {
					AppToast.toastMsgCenter(this, ""+obj.optString("message")/*getResources().getString(R.string.ERROR_404)*/).show();
				}

			}
			else if ((Integer) (tag) == HttpTagConstantUtils.RESET_PWD) {
				if(null!=dialogUploadImage){
					dialogUploadImage.dismiss();
				}
				if(isSuccess(statusCode)){
					AppToast.toastMsgCenter(getApplicationContext(), "修改成功!", TOAST_TIME).show();
					finish();
				}else{
					AppToast.toastMsgCenter(getApplicationContext(), "Error Code :"+statusCode, TOAST_TIME).show();
				}
			}
		} catch (Exception e) {

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

	NiftyDialogBuilder dialogUploadImage;
	
	@Override
	protected String getActivityName() {
		return "密码重置";
	}

}

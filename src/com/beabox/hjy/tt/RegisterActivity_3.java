package com.beabox.hjy.tt;

import org.kymjs.aframe.utils.StringUtils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.base.init.BaseActivity;
import com.app.base.init.MyApplication;
import com.app.service.RegisterAsynTaskService;
import com.base.dialog.lib.Effectstype;
import com.base.dialog.lib.NiftyDialogBuilder;
import com.base.service.action.constant.HttpTagConstantUtils;
import com.base.supertoasts.util.AppToast;
import com.umeng.message.PushAgent;

/**
 * 注册页面3--提交信息注册 Author zhup
 */
@SuppressWarnings("serial")
public class RegisterActivity_3 extends BaseActivity {
	private final static String TAG = "RegisterActivity_3";

	private EditText password;
	private ImageView backBtn;
	private View registerSubmitBtn;// 提交注册
	String tel_num, verify_code;
	private InputMethodManager imm;
	private View register_3 ;
	private int from=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		MyApplication.getInstance().addActivity(this);
    	PushAgent.getInstance(this).onAppStart();
		from=getIntent().getIntExtra("FROM", 0);
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_3);
		setupView();
	}

	@Override
	public void setupView() {
		tel_num = getIntent().getStringExtra("tel_num");
		verify_code = getIntent().getStringExtra("verify_code");
		password = (EditText) findViewById(R.id.password);
		backBtn = (ImageView) findViewById(R.id.backBtn);
		registerSubmitBtn =  findViewById(R.id.registerSubmitBtn);
		register_3 =  findViewById(R.id.register_3);

		addListener();

	}

	@Override
	public void addListener() {
		backBtn.setOnClickListener(this);
		registerSubmitBtn.setOnClickListener(this);
		register_3.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.backBtn:
			finish();
			break;
		case R.id.register_3:
			if(null!=imm && imm.isActive()){
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			}
			break;
		case R.id.registerSubmitBtn:// 提交注册
			final String password = this.password.getText().toString();
			if (StringUtils.isEmpty(password)) {
				AppToast.toastMsgCenter(getApplicationContext(), "请设置您的个人密码!", 2000)
						.show();
				return;
			}
			dialogUploadImage = NiftyDialogBuilder.getInstance(this, R.layout.dialog_login_layout);
			final View text = LayoutInflater.from(this).inflate(R.layout.dialog_login_view, null);
			TextView t = (TextView) text.findViewById(R.id.loading_text);
			t.setText("注册中....");
			dialogUploadImage.withTitle(null)
			.withMessage(null)
			.withEffect(Effectstype.Fadein)
			.withDuration(100)
			.isCancelableOnTouchOutside(false)
			.setCustomView(text, this.getApplicationContext()).show();
			new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					new RegisterAsynTaskService(getApplicationContext(),
							HttpTagConstantUtils.REGISTER_SUBMIT, RegisterActivity_3.this)
							.doRegisterAsynTaskService(tel_num, verify_code, password);
				}
			}, 500);
		

			break;
		default:
			break;
		}

	}

	@Override
	public void sendMessageHandler(int messageCode) {

	}
	NiftyDialogBuilder dialogUploadImage ;
	

	public void dataCallBack(Object tag, int statusCode, Object result) {
		try {
			Log.e(TAG, "" + result);
			if ((Integer) (tag) == HttpTagConstantUtils.REGISTER_SUBMIT) {
				if (dialogUploadImage != null) {
					dialogUploadImage.dismiss();
				}
				 if(isSuccess(statusCode) && result!=null){
//					 JSONObject obj = new JSONObject(result.toString());
//					 int code = obj.optInt("code");
//					 String message = obj.optString("message") ;
//					 String error = obj.optString("error") ;
					 AppToast.toastMsgCenter(getApplicationContext(), "注册成功!",
								2000).show();
						Intent intent = new Intent(this,
								RegisterActivity_4.class);
						intent.putExtra("tel_num",tel_num );
		            	intent.putExtra("verify_code",verify_code );
		            	intent.putExtra("password",this.password.getText().toString() );
		            	intent.putExtra("FROM", from);
						startActivity(intent);
						finish();
						overridePendingTransition(R.anim.activity_enter_from_right,
								R.anim.activity_exit_to_left);
				 }else{
					 AppToast.toastMsgCenter(getApplicationContext(), "Error Code :"+statusCode+"，注册失败!", 2000).show();
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
	
	@Override
	protected String getActivityName() {
		return "注册页面--3";
	}


}

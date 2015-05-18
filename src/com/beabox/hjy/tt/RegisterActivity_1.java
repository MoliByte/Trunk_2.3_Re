package com.beabox.hjy.tt;

import org.json.JSONObject;
import org.kymjs.aframe.utils.StringUtils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.base.init.BaseActivity;
import com.app.base.init.MyApplication;
import com.app.service.GetVerifyCodeNewAsynTaskService;
import com.base.app.utils.StringUtil;
import com.base.dialog.lib.Effectstype;
import com.base.dialog.lib.NiftyDialogBuilder;
import com.base.service.action.constant.HttpTagConstantUtils;
import com.base.supertoasts.util.AppToast;
import com.umeng.message.PushAgent;

/**
 * 注册页面-1填写手机号码 Author zhup
 */
@SuppressWarnings("serial")
public class RegisterActivity_1 extends BaseActivity {
	private final static String TAG = "RegisterActivity_1";

	private EditText telephone;
	private ImageView backBtn;
	private View get_identified_code;
	private InputMethodManager imm;

	private View register_1 ;
	
	NiftyDialogBuilder dialogUploadImage;
	
	private int from=0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		MyApplication.getInstance().addActivity(this);
    	PushAgent.getInstance(this).onAppStart();
		from=getIntent().getIntExtra("FROM", 0);
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_1);
		setupView();
	}

	@Override
	public void setupView() {
		backBtn = (ImageView) findViewById(R.id.backBtn);
		get_identified_code =  findViewById(R.id.get_identified_code);
		telephone = (EditText) findViewById(R.id.telephone);
		register_1 = findViewById(R.id.register_1);

		addListener();

	}

	@Override
	public void addListener() {
		backBtn.setOnClickListener(this);
		get_identified_code.setOnClickListener(this);
		register_1.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.backBtn:
			finish();
			break;
		case R.id.register_1:
			if(null!=imm && imm.isActive()){
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			}
			break;
		case R.id.get_identified_code:// 到注册页面2--填写验证码页面
			final String tel_num = telephone.getText().toString();
			if(!StringUtil.isMobile(tel_num)){
				AppToast.toastMsgCenter(getApplicationContext(), "请输入正确的手机号!", 2000)
				.show();
				return;
			}
			/*if (!StringUtils.isPhone(tel_num)) {
				
			}*/
			if (StringUtils.isEmpty(tel_num)) {
				AppToast.toastMsgCenter(getApplicationContext(), "手机号不能为空!", 2000)
						.show();
				return;
			}
			dialogUploadImage = NiftyDialogBuilder.getInstance(this,
					R.layout.dialog_login_layout);
			final View text = LayoutInflater.from(this).inflate(
					R.layout.dialog_login_view, null);
			TextView t = (TextView) text.findViewById(R.id.loading_text);
			t.setText("正在验证....");
			dialogUploadImage.withTitle(null).withMessage(null)
					.withEffect(Effectstype.Fadein).withDuration(100)
					.isCancelableOnTouchOutside(false)
					.setCustomView(text, this.getApplicationContext()).show();
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					new GetVerifyCodeNewAsynTaskService(RegisterActivity_1.this
							.getApplicationContext(),
							HttpTagConstantUtils.VERIFY_CODE,
							RegisterActivity_1.this).doGetVerifyCode(telephone.getText().toString(),
							HttpTagConstantUtils.REGISTER_TYPE);
//					new GetVerifyCodeAsynTaskService(RegisterActivity_1.this
//							.getApplicationContext(),
//							HttpTagConstantUtils.VERIFY_CODE,
//							RegisterActivity_1.this).doGetVerifyCode(tel_num,
//									HttpTagConstantUtils.REGISTER_TYPE);
				}
			}, 500);
			// AppToast.toastMsgCenter(getApplicationContext(), "已发送!", 2000).show();

			break;
		default:
			break;
		}

	}

	@Override
	public void sendMessageHandler(int messageCode) {

	}

	public void dataCallBack(Object tag, int statusCode, Object result) {
		try {
			if ((Integer) (tag) == HttpTagConstantUtils.VERIFY_CODE) {
				if (isSuccess(statusCode) && result != null) {
					JSONObject obj = new JSONObject(result.toString());
					String message = obj.optString("message");
					if (obj.optInt("code") == 200 || obj.optInt("code") == 201) {
						if (null != dialogUploadImage) {
							dialogUploadImage.dismiss();
						}
						String tel_num = telephone.getText().toString();
						AppToast.toastMsgCenter(getApplicationContext(), "已发送短信到"+tel_num+",请稍候...").show();
						Intent intent = new Intent(this,
								RegisterActivity_2.class);
						intent.putExtra("tel_num", tel_num);
						intent.putExtra("FROM", from);
						startActivity(intent);
						finish();
						overridePendingTransition(
								R.anim.activity_enter_from_right,
								R.anim.activity_exit_to_left);
					}else {
						/*AppToast.toastMsgCenter(getApplicationContext(),
								"该手机号已注册!").show();*/
						AppToast.toastMsgCenter(getApplicationContext(),message+"").show();
					}

				} else {
					AppToast.toastMsgCenter(getApplicationContext(),R.string.ERROR_404).show();
				}
			}
		} catch (Exception e) {

		} finally {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					if (null != dialogUploadImage) {
						dialogUploadImage.dismiss();
					}
				}
			}, 1000);
		}
	}
	
	@Override
	protected String getActivityName() {
		return "注册页面--1";
	}

}

package com.beabox.hjy.tt;

import org.json.JSONObject;
import org.kymjs.aframe.utils.StringUtils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.base.init.BaseActivity;
import com.app.base.init.MyApplication;
import com.app.service.GetVerifyCodeNewAsynTaskService;
import com.app.service.PutVerifyCodeNewAsynTaskService;
import com.base.dialog.lib.Effectstype;
import com.base.dialog.lib.NiftyDialogBuilder;
import com.base.service.action.constant.HttpTagConstantUtils;
import com.base.supertoasts.util.AppToast;
import com.umeng.message.PushAgent;

/**
 * 注册页面2--填写验证码 Author zhup
 */
@SuppressWarnings("serial")
public class RegisterActivity_2 extends BaseActivity {
	private final static String TAG = "RegisterActivity_2";

	private EditText verify_code;
	private ImageView backBtn;
	private View register_next;
	private Button get_verify_code_again;
	private View register_2 ;
	int count = 60;
	String tel_num;
	private InputMethodManager imm;
	private int from=0;
	private Handler handler = new Handler();
	Runnable runnable = new Runnable() {

		@Override
		public void run() {
			count--;
			if (count == 0) {
				count = 60;
				get_verify_code_again.setClickable(true);
				get_verify_code_again.setText("重新获取!");
			} else {
				get_verify_code_again.setText("(" + count + "秒后)重新获取!");
				get_verify_code_again.setClickable(false);
				handler.postDelayed(this, 1000);
			}
		}

	};

	NiftyDialogBuilder dialogUploadImage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		MyApplication.getInstance().addActivity(this);
    	PushAgent.getInstance(this).onAppStart();
		from=getIntent().getIntExtra("FROM", 0);
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_2);
		setupView();
	}

	@Override
	public void setupView() {
		handler.postDelayed(runnable, 1000);
		tel_num = getIntent().getStringExtra("tel_num");
		backBtn = (ImageView) findViewById(R.id.backBtn);
		register_next =  findViewById(R.id.register_next);
		verify_code = (EditText) findViewById(R.id.verify_code);
		get_verify_code_again = (Button) findViewById(R.id.get_verify_code_again);
		register_2 = findViewById(R.id.register_2);
		addListener();

	}

	@Override
	public void addListener() {
		backBtn.setOnClickListener(this);
		get_verify_code_again.setOnClickListener(this);
		register_next.setOnClickListener(this);
		//register_2.setOnClickListener(this);
		// get_verify_code_again.setOnClickListener(this);//重新获取验证码
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.backBtn:
			finish();
			break;
		case R.id.register_2:
			if(null!=imm && imm.isActive()){
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			}
			break;
		case R.id.get_verify_code_again://再次获取验证码
			handler.postDelayed(runnable, 1000);
			AppToast.toastMsgCenter(getApplicationContext(), "已发送短信到"+tel_num+",请稍候...").show();
			// AppToast.toastMsgCenter(getApplicationContext(), "已发送!", 2000).show();
			/*new GetVerifyCodeAsynTaskService(getApplicationContext(),
					HttpTagConstantUtils.VERIFY_CODE, this).doGetVerifyCode(
					tel_num, HttpTagConstantUtils.REGISTER_TYPE);*/
			
			new GetVerifyCodeNewAsynTaskService(RegisterActivity_2.this
					.getApplicationContext(),
					HttpTagConstantUtils.VERIFY_CODE,
					RegisterActivity_2.this).doGetVerifyCode(tel_num,
					HttpTagConstantUtils.REGISTER_TYPE);

			// intent = new Intent(this,RegisterActivity_2.class);
			// intent.putExtra("tel_num",tel_num );
			//
			// startActivity(intent);
			// overridePendingTransition(R.anim.activity_enter_from_right,
			// R.anim.activity_exit_to_left);
			break;
		case R.id.register_next:// 下一步
			final String verify_code = this.verify_code.getText().toString();// intent.putExtra("verify_code",verify_code
			// );
			if (StringUtils.isEmpty(verify_code)) {
				AppToast.toastMsgCenter(getApplicationContext(), "请输入您收到的验证码!")
						.show();
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
					new PutVerifyCodeNewAsynTaskService(getApplicationContext(),
							HttpTagConstantUtils.CHECK_CODE,
							RegisterActivity_2.this).doPutVerifyCode(tel_num,
							verify_code);

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

	public void dataCallBack(Object tag, int statusCode, Object result) {
		try {
			Log.e(TAG, "" + result);
			if ((Integer) (tag) == HttpTagConstantUtils.CHECK_CODE) {
				if (null != dialogUploadImage) {
					dialogUploadImage.dismiss();
				}
				JSONObject obj = new JSONObject(result.toString()) ;
				int code =  obj.optInt("code");
				if (isSuccess(statusCode) && isSuccess(code)) {
					String verify_code = this.verify_code.getText().toString();
					Intent intent = new Intent(this, RegisterActivity_3.class);
					intent.putExtra("tel_num", tel_num);
					intent.putExtra("verify_code", verify_code);
					intent.putExtra("FROM", from);
					startActivity(intent);
					finish();
					overridePendingTransition(R.anim.activity_enter_from_right,
							R.anim.activity_exit_to_left);
					
				} else {
					AppToast.toastMsgCenter(this, "验证码错误，请重新输入!", 2000).show();
				}

			}
		} catch (Exception e) {

		} finally {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					if (null != dialogUploadImage && dialogUploadImage.isShowing()) {
						dialogUploadImage.dismiss();
					}
				}
			}, 1000);
		}
	}
	
	@Override
	protected String getActivityName() {
		return "注册页面--2";
	}

}

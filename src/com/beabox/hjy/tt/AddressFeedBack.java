package com.beabox.hjy.tt;

import org.apache.http.HttpStatus;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.base.init.ACache;
import com.app.base.init.BaseActivity;
import com.app.base.init.MyApplication;
import com.app.service.GetUserAddressService;
import com.app.service.PutUserAddressTaskService;
import com.base.app.utils.StringUtil;
import com.base.dialog.lib.Effectstype;
import com.base.dialog.lib.NiftyDialogBuilder;
import com.base.service.action.constant.HttpTagConstantUtils;
import com.base.supertoasts.util.AppToast;
import com.umeng.message.PushAgent;

/**
 * 收货地址反馈
 * Author zhup
 */
@SuppressWarnings("serial")
public class AddressFeedBack extends BaseActivity{
	private final static String  TAG = "AddressFeedBack" ;
	
	private ImageView backBtn ;//返回按钮
	private EditText suggestions ;//收货地址
	private View suggestion_btn ;//提交收货地址
	NiftyDialogBuilder dialogUploadImage ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		MyApplication.getInstance().addActivity(this);
		PushAgent.getInstance(this).onAppStart();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.address_feed_back);
		setupView();
		new GetUserAddressService(this,
				HttpTagConstantUtils.GET_ADDRESS, this).get_address(ACache.get(this)
				.getAsString("Token"));
	}

	@Override
	public void setupView() {
		backBtn = (ImageView) findViewById(R.id.backBtn);
		suggestions = (EditText) findViewById(R.id.suggestion);
		suggestion_btn = findViewById(R.id.suggestion_btn);
		
		addListener();

	}

	@Override
	public void addListener() {
		backBtn.setOnClickListener(this);
		suggestion_btn.setOnClickListener(this);
	}
	

	@Override
    public void onClick(View v) {
		Intent intent = null ;
        switch (v.getId()){
            case R.id.backBtn:
                finish();
                break;
            case R.id.suggestion_btn://提交
            	final String token = aCache.getAsString("Token");
            	final String suggestions = this.suggestions.getText().toString();
            	
            	// 异步提交地址，成功后关闭弹出框
    			if (StringUtil.isBlank(suggestions)) {
    				AppToast.toastMsg(this, "~亲，您还什么都没有填哦")
    						.show();
    				return;
    			}
            	
            	dialogUploadImage = NiftyDialogBuilder.getInstance(this, R.layout.dialog_login_layout);
            			final View text = LayoutInflater.from(this).inflate(R.layout.dialog_login_view, null);
            			TextView t = (TextView) text.findViewById(R.id.loading_text);
            			t.setText("提交中....");
            			dialogUploadImage.withTitle(null)
            			.withMessage(null)
            			.withEffect(Effectstype.Fadein)
            			.withDuration(100)
            			.isCancelableOnTouchOutside(false)
            			.setCustomView(text, this.getApplicationContext()).show();
            			new Handler().postDelayed(new Runnable() {
            				
            				@Override
            				public void run() {
            					new PutUserAddressTaskService(AddressFeedBack.this,
                						HttpTagConstantUtils.PUT_ADDRESS, AddressFeedBack.this)
                						.put_user_address(token,
                								suggestions);
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
	
	public void dataCallBack(Object tag,  int statusCode,Object result) {
		try {
			Log.e(TAG, ""+result);
			if(HttpTagConstantUtils.PUT_ADDRESS == (Integer)tag){
				if ((HttpStatus.SC_OK == statusCode || HttpStatus.SC_CREATED == statusCode) && (Boolean)result) {
					AppToast.toastMsgCenter(this,getString(R.string.put_address_success)).show();
					finish();
				} else {
					AppToast.toastMsgCenter(this,getString(R.string.put_address_fail)).show();
				}
			}else if(HttpTagConstantUtils.GET_ADDRESS == (Integer)tag){
				suggestions.setText(result.toString()+"");
				suggestions.setSelection((result.toString()+"").length());
			}
		} catch (Exception e) {
			
		} finally {
			new Handler().post(new Runnable() {
				@Override
				public void run() {
					  if(null!=dialogUploadImage && dialogUploadImage.isShowing()){
						  dialogUploadImage.dismiss();
					  }
				}
			});
		}
	}
	
	@Override
	protected String getActivityName() {
		return "收货地址";
	}



}

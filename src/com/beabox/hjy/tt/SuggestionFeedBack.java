package com.beabox.hjy.tt;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.base.init.BaseActivity;
import com.app.base.init.MyApplication;
import com.app.service.PostFeedBackAsynTaskService;
import com.base.dialog.lib.Effectstype;
import com.base.dialog.lib.NiftyDialogBuilder;
import com.base.service.action.constant.HttpTagConstantUtils;
import com.umeng.message.PushAgent;

/**
 * 发送意见反馈
 * Author zhup
 */
@SuppressWarnings("serial")
public class SuggestionFeedBack extends BaseActivity{
	private final static String  TAG = "SuggestionFeedBack" ;
	private ImageView backBtn ;//返回按钮
	private EditText suggestions ;//反馈意见
	private EditText keep_qq ;//预留QQ
	private EditText keep_phone_num ;//预留手机号码
	private View suggestion_btn ;//提交反馈
	NiftyDialogBuilder dialogUploadImage ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		MyApplication.getInstance().addActivity(this);
    	PushAgent.getInstance(this).onAppStart();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.suggest_feed_back);
		setupView();
	}

	@Override
	public void setupView() {
		backBtn = (ImageView) findViewById(R.id.backBtn);
		suggestions = (EditText) findViewById(R.id.suggestion);
		keep_phone_num = (EditText) findViewById(R.id.keep_phone);
		keep_qq = (EditText) findViewById(R.id.keep_qq);
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
            	final String keep_qq = this.keep_qq.getText().toString();
            	final String keep_phone = this.keep_phone_num.getText().toString();
            	
            	
            	dialogUploadImage = NiftyDialogBuilder.getInstance(this, R.layout.dialog_login_layout);
            			final View text = LayoutInflater.from(this).inflate(R.layout.dialog_login_view, null);
            			TextView t = (TextView) text.findViewById(R.id.loading_text);
            			t.setText("意见反馈中....");
            			dialogUploadImage.withTitle(null)
            			.withMessage(null)
            			.withEffect(Effectstype.Fadein)
            			.withDuration(100)
            			.isCancelableOnTouchOutside(false)
            			.setCustomView(text, this.getApplicationContext()).show();
            			new Handler().postDelayed(new Runnable() {
            				
            				@Override
            				public void run() {
            					new PostFeedBackAsynTaskService(getApplicationContext(),
            							HttpTagConstantUtils.FEED_BACK,SuggestionFeedBack.this).doPostFeedBack(token,
            							suggestions, keep_qq, keep_phone);
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
			if((Integer)(tag) == HttpTagConstantUtils.FEED_BACK && isSuccess(statusCode)){
				finish();
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
		return "意见反馈";
	}



}

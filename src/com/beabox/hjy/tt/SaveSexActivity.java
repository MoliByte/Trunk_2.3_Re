package com.beabox.hjy.tt;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.base.entity.UserEntity;
import com.app.base.init.BaseActivity;
import com.app.base.init.MyApplication;
import com.app.service.UpdateNickNameService;
import com.base.app.utils.DBService;
import com.base.dialog.lib.Effectstype;
import com.base.dialog.lib.NiftyDialogBuilder;
import com.base.service.action.constant.HttpTagConstantUtils;
import com.base.supertoasts.util.AppToast;
import com.umeng.message.PushAgent;

/**
 * 性别修改页面
 * Author zhup
 */
@SuppressWarnings("serial")
public class SaveSexActivity extends BaseActivity{
	private final static String  TAG = "SaveNickNameActivity" ;
	
	private EditText nick_name ;
	private ImageView backBtn ;
	private ImageView save_nickname_btn ;
	NiftyDialogBuilder dialogUploadImage ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		MyApplication.getInstance().addActivity(this);
    	PushAgent.getInstance(this).onAppStart();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.update_nick_name);
		setupView();
	}

	@Override
	public void setupView() {
		backBtn = (ImageView) findViewById(R.id.backBtn);
		save_nickname_btn = (ImageView) findViewById(R.id.save_nickname_btn);
		nick_name = (EditText) findViewById(R.id.nick_name);

		addListener();

	}

	@Override
	public void addListener() {
		backBtn.setOnClickListener(this);
		save_nickname_btn.setOnClickListener(this);
	}

	@Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.backBtn:
                finish();
                break;
            case R.id.save_nickname_btn:
            	save_nick_name();
            	break;
            default:
                break;
        }

    }

	private void save_nick_name() {
		dialogUploadImage = NiftyDialogBuilder.getInstance(this, R.layout.dialog_login_layout);
		final View text = LayoutInflater.from(this).inflate(R.layout.dialog_login_view, null);
		TextView t = (TextView) text.findViewById(R.id.loading_text);
		t.setText("正在保存....");
		dialogUploadImage.withTitle(null)
		.withMessage(null)
		.withEffect(Effectstype.Fadein)
		.withDuration(100)
		.isCancelableOnTouchOutside(false)
		.setCustomView(text, this.getApplicationContext()).show();
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				String Token = aCache.getAsString("Token") ;
				new UpdateNickNameService(SaveSexActivity.this, HttpTagConstantUtils.UPDATE_NICK_NAME,
						SaveSexActivity.this).doNickNameUpdate(Token, nick_name.getText().toString());
				
			}
		}, 500);
		
	}

	@Override
	public void sendMessageHandler(int messageCode) {

	}
	
	public void dataCallBack(Object tag,  int statusCode,Object result) {
		try {
			Log.e(TAG, statusCode+","+result);
			if((Integer)(tag) == HttpTagConstantUtils.UPDATE_NICK_NAME){
				if(statusCode==200){
					UserEntity entity = DBService.getEntityByToken(aCache.getAsString("Token"));
					entity.setNiakname(nick_name.getText().toString());
					DBService.saveOrUpdateUserEntity(entity);
					aCache.put("nickname", nick_name.getText().toString());
					if(dialogUploadImage!=null && dialogUploadImage.isShowing()){
						dialogUploadImage.dismiss();
					}
					finish();
					AppToast.toastMsgCenter(this, "昵称保存成功!", 2000).show();//
				}
			}
		} catch (Exception e) {
			
		} finally {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					if(dialogUploadImage!=null && dialogUploadImage.isShowing()){
						dialogUploadImage.dismiss();
					}
				}
			}, 1000);
		}
	}
	@Override
	protected String getActivityName() {
		return "性别修改";
	}
}
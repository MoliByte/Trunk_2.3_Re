package com.beabox.hjy.tt;

import com.app.base.entity.UserEntity;
import com.app.base.init.MyApplication;
import com.app.service.GoToHomeService;
import com.app.service.PostUserShareService;
import com.app.service.PublishSkinMoodService;
import com.avos.avoscloud.LogUtil.log;
import com.base.app.utils.DBService;
import com.base.app.utils.ImageParamSetter;
import com.base.app.utils.KVOEvents;
import com.base.app.utils.UMShareUtil;
import com.base.app.utils.UMShareUtil.ShareAction;
import com.base.service.action.constant.HttpTagConstantUtils;
import com.base.service.impl.HttpAysnResultInterface;
import com.beabox.hjy.tt.main.skintest.component.KVO.Observer;
import com.idongler.image.ImageUtil;
import com.skinrun.trunk.main.TestingPartUtils;
import com.umeng.message.PushAgent;
import com.umeng.socialize.sso.UMSsoHandler;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class SkinResultShareActivity extends Activity implements OnClickListener, OnCheckedChangeListener, HttpAysnResultInterface, Observer {
	private TextView textViewTestPart,tvWaterValue,tvOilValue,tvFlexibleValue;
	private ImageView imageViewBig,ivStartShare;
	private int TestId;
	private float waterValues = 0, oilValues = 0, flexibleValues = 0;
	private int partFlag=-999;
	private byte[] imageByte;
	private RadioGroup radioGroupShare;
	private EditText editTextWord;
	private final int WXShare=0x122,SinaShare=0x134;
	private int sharePlam=WXShare;
	private String content="";
	private ImageView ivIsToMain;
	
	private boolean toMainSwitch=true;
	
	
	private String TAG="SkinResultShareActivity";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(this);
		MyApplication.getInstance().getKvo().registerObserver(KVOEvents.SHOT_SKIN_SCREEN, this);
		
    	PushAgent.getInstance(this).onAppStart();
		
		Bundle bundle = getIntent().getExtras();
		waterValues=bundle.getFloat("waterValues");
		oilValues=bundle.getFloat("oilValues");
		flexibleValues=bundle.getFloat("flexibleValues");
		partFlag=bundle.getInt("partFlag");
		TestId=bundle.getInt("TestId");
		imageByte=bundle.getByteArray("BITMAP");
		
		Log.e(TAG, "=========partFlag:"+partFlag);
		Log.e(TAG, "=========测试Id:"+TestId);
		
		setContentView(R.layout.skin_share_activity);
		initView();
		
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		MyApplication.getInstance().getKvo().registerObserver(KVOEvents.SHOT_SKIN_SCREEN, this);
	}
	
	private void initView(){
		textViewTestPart=(TextView)findViewById(R.id.textViewTestPart);
		tvWaterValue=(TextView)findViewById(R.id.tvWaterValue);
		tvOilValue=(TextView)findViewById(R.id.tvOilValue);
		tvFlexibleValue=(TextView)findViewById(R.id.tvFlexibleValue);
		
		imageViewBig=(ImageView)findViewById(R.id.imageViewBig);
		radioGroupShare=(RadioGroup)findViewById(R.id.radioGroupShare);
		ivStartShare=(ImageView)findViewById(R.id.ivStartShare);
		
		editTextWord=(EditText)findViewById(R.id.editTextWord);
		ivIsToMain=(ImageView)findViewById(R.id.ivIsToMain);
		ivIsToMain.setSelected(true);
		ivIsToMain.setOnClickListener(this);
		
		setValue();
		addListener();
	}
	private void setValue(){
		tvWaterValue.setText(String.format("%1.1f",waterValues)+"%");
		tvOilValue.setText(String.format("%1.1f",oilValues)+"%");
		tvFlexibleValue.setText((int)flexibleValues+"");
		textViewTestPart.setText("完成 "+getTestPartName(partFlag)+" 测试");
		
		if(imageByte!=null&&imageByte.length>0){
			Bitmap bitmap=BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
			
			if(bitmap!=null&&bitmap.getWidth()!=0&&bitmap.getHeight()!=0){
				ImageParamSetter.setImageXFull(this, imageViewBig, bitmap.getHeight()*1.0/bitmap.getWidth(), 0);
				imageViewBig.setImageBitmap(bitmap);
				
			}
		}
	}
	
	private void addListener(){
		findViewById(R.id.skinShareBack).setOnClickListener(this);
		ivStartShare.setOnClickListener(this);
		radioGroupShare.setOnCheckedChangeListener(this);
	}

	private String getTestPartName(int part) {
		switch (part) {
		case TestingPartUtils.TFace:
			return "T区";
		case TestingPartUtils.IceFace:
			return "眼周";
			
		case TestingPartUtils.UFace:
			
			return "U区";
			
		case TestingPartUtils.OnHand:
			
			return "手背";
		}
		return "";
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.skinShareBack:
			finish();
			break;
		case R.id.ivIsToMain:
			if(toMainSwitch){
				toMainSwitch=false;
				ivIsToMain.setSelected(false);
				
				
			}else{
				toMainSwitch=true;
				ivIsToMain.setSelected(true);
				
			}
			
			
			break;
		case R.id.ivStartShare:
			content=""+editTextWord.getText().toString();
			UserEntity u=DBService.getUserEntity();
			//上传心情
			new PublishSkinMoodService(this, HttpTagConstantUtils.PUBLISH_SKIN_MOOD, this).publish(u.getToken(), TestId, content);
			if(u!=null&&toMainSwitch){
				new GoToHomeService(this, HttpTagConstantUtils.SKIN_GO_TO_HOME, this).goToHome(""+u.getToken(), TestId);
			}
			
			Intent intent=new Intent(this, ScreenShotSkinActivity.class);
			Bundle bundle=new Bundle();
			
			bundle.putFloat("waterValues", waterValues);
			bundle.putFloat("oilValues", oilValues);
			bundle.putInt("TestId", TestId);
			bundle.putFloat("flexibleValues", flexibleValues);
			bundle.putInt("partFlag", partFlag);
			bundle.putString("content", content);
			bundle.putByteArray("BITMAP",imageByte);
			intent.putExtras(bundle);
			startActivityForResult(intent, 0x001);
			
			
			
			break;
		}
	}
	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.activity_enter_from_left, R.anim.activity_exit_to_right);
	}

	@Override
	public void onCheckedChanged(RadioGroup arg0, int id) {
		switch(id){
		case R.id.radioWXShare:
			sharePlam=WXShare;
			break;
		case R.id.radioSinaShare:
			sharePlam=SinaShare;
			break;
		}
	}

	@Override
	public void dataCallBack(Object tag, int statusCode, Object result) {
		try{
			switch((Integer)tag){
			case HttpTagConstantUtils.PUBLISH_SKIN_MOOD:
				log.e(TAG, "=========发表心情返回码："+statusCode);
				break;
			case HttpTagConstantUtils.SKIN_GO_TO_HOME:
				log.e(TAG, "=========上首页返回码："+statusCode);
				break;
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	// 友盟分享sso 登录
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		/** 使用SSO授权必须添加如下代码 */
		UMSsoHandler ssoHandler = UMShareUtil.getInstance()
				.getUmsocialService().getConfig().getSsoHandler(requestCode);
		if (ssoHandler != null) {
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}

	@Override
	public void onEvent(String event, Object... args) {
		if(event.equals(KVOEvents.SHOT_SKIN_SCREEN)){
			final Bitmap bitmap=(Bitmap) args[0];
			if(bitmap==null){
				log.e(TAG, "============返回bitmap：为空");
				return;
			}
			log.e(TAG, "============返回bitmap：不为空");
			switch(sharePlam){
			case WXShare:
				
				UMShareUtil.getInstance().WXCShareImage(this, " ", bitmap, new ShareAction() {
					
					@Override
					public void onSuccess() {
						postShareResult();
						
						bitmap.recycle();
						finish();
					}
				});
				break;
			case SinaShare:
				UMShareUtil.getInstance().sinaShareImage(this, " ", bitmap, new ShareAction() {
					
					@Override
					public void onSuccess() {
						postShareResult();
						bitmap.recycle();
						finish();
					}
				});
				break;
			}
		}
	}
	
	private void postShareResult(){
		UserEntity user=DBService.getUserEntity();
		if(user!=null){
			new PostUserShareService(SkinResultShareActivity.this,HttpTagConstantUtils.SHARE_JIFEN,
					SkinResultShareActivity.this).post_share(user.getToken());
		}
	}
}

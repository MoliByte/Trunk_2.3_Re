package com.beabox.hjy.tt;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.app.base.entity.SkinTypeEntity;
import com.app.base.entity.UserEntity;
import com.app.base.init.ACache;
import com.app.base.init.MyApplication;
import com.avoscloud.chat.service.UserService;
import com.avoscloud.chat.util.PhotoUtils;
import com.base.app.utils.DBService;
import com.base.app.utils.DateUtil;
import com.base.app.utils.FileUtil;
import com.base.app.utils.ImageParamSetter;
import com.base.app.utils.KVOEvents;
import com.base.app.utils.StringUtil;
import com.base.service.action.constant.HttpTagConstantUtils;
import com.beabox.hjy.tt.main.skintest.component.KVO.Observer;
import com.idongler.image.ImageUtil;
import com.idongler.widgets.CircleImageView;
import com.skinrun.trunk.main.TestingPartUtils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

public class ScreenShotSkinActivity extends Activity implements Observer{
	private CircleImageView circleUserImage;
	private TextView tvUserNick,tvLocation,tvUserAge,tvSkinType,userLevel;
	private ImageView ivTakePhoto;
	private TextView tvWaterValue,tvOilValue,tvFlexibleValue,tvTestPart;
	private TextView pulishedMood;
	
	private float waterValues = 0, oilValues = 0, flexibleValues = 0;
	private int partFlag=-999;
	private byte[] imageByte;
	private String content;
	
	private UserEntity user;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().getKvo().registerObserver(KVOEvents.SHOT_SKIN_SCREEN, this);
		Bundle bundle = getIntent().getExtras();
		waterValues=bundle.getFloat("waterValues");
		oilValues=bundle.getFloat("oilValues");
		flexibleValues=bundle.getFloat("flexibleValues");
		partFlag=bundle.getInt("partFlag");
		imageByte=bundle.getByteArray("BITMAP");
		content=bundle.getString("content");
		user=DBService.getUserEntity();
		
		setContentView(R.layout.screenshot_skin_layout);
		initView();
		
	}
	@Override
	protected void onResume() {
		super.onResume();
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				finish();
			}
		}, 1);
	}
	@Override
	protected void onStop() {
		super.onStop();
		getImage();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		MyApplication.getInstance().getKvo().removeObserver(KVOEvents.SHOT_SKIN_SCREEN, this);
	}
	
	private void initView(){
		circleUserImage=(CircleImageView)findViewById(R.id.circleUserImage);
		ivTakePhoto=(ImageView)findViewById(R.id.ivTakePhoto);
		
		tvUserNick=(TextView)findViewById(R.id.tvUserNick);
		tvLocation=(TextView)findViewById(R.id.tvLocation);
		tvUserAge=(TextView)findViewById(R.id.tvUserAge);
		tvSkinType=(TextView)findViewById(R.id.tvSkinType);
		userLevel=(TextView)findViewById(R.id.userLevel);
		
		tvTestPart=(TextView)findViewById(R.id.tvTestPart);
		
		tvWaterValue=(TextView)findViewById(R.id.tvWaterValue);
		tvOilValue=(TextView)findViewById(R.id.tvOilValue);
		tvFlexibleValue=(TextView)findViewById(R.id.tvFlexibleValue);
		pulishedMood=(TextView)findViewById(R.id.pulishedMood);
		
		setValue();
	}
	
	private void getImage(){
		
		ScrollView view=(ScrollView) findViewById(R.id.screen_shot_skin_container);
		Bitmap bitmap=ImageUtil.getBitmapByView(view);
		String imageName="";
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		imageName=sdf.format(new Date())+".png";
		
		FileUtil f=new FileUtil(this);
		
		try {
			f.savaBitmap(imageName, bitmap, 100);
			MyApplication.getInstance().getKvo().fire(KVOEvents.SHOT_SKIN_SCREEN,bitmap);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void setValue(){
		
		if(user!=null){
			tvUserNick.setText(user.getNiakname());
			tvLocation.setText(getRegion());
			tvUserAge.setText(DateUtil.getAge(user.getBirthday())+"岁");
			
			tvSkinType.setText("" + HttpTagConstantUtils.getSkinTypeByKey(user.getSkinType()));
			
			//tvSkinType.setText(getSkinType());
			String integral =""+ ACache.get(this).getAsObject("integral");
			
			userLevel.setText(""+StringUtil.getLevel(Integer.valueOf(integral==null||"null".equals(integral)|| "".equals(integral)?"0":integral)));
		}
		
		
		tvWaterValue.setText(String.format("%1.1f",waterValues)+"%");
		tvOilValue.setText(String.format("%1.1f",oilValues)+"%");
		tvFlexibleValue.setText((int)flexibleValues+"");
		tvTestPart.setText("完成 "+getTestPartName(partFlag)+" 测试");
		pulishedMood.setText(""+content);
		
		UserService.imageLoader.displayImage(""+user.getAvatar(), circleUserImage,
				PhotoUtils.normalImageOptions);
		
		if(imageByte!=null&&imageByte.length>0){
			Bitmap bitmap=BitmapFactory.decodeByteArray(imageByte, 0, imageByte.length);
			
			if(bitmap!=null&&bitmap.getWidth()!=0&&bitmap.getHeight()!=0){
				ImageParamSetter.setImageXFull(this, ivTakePhoto, bitmap.getHeight()*1.0/bitmap.getWidth(), 0);
				ivTakePhoto.setImageBitmap(bitmap);
				imageByte=null;
			}
		}
	}
	
	//得到肌肤类型
	private String getSkinTyp(){
		try{
			int skinType=Integer.parseInt(user.getSkinType());
			List<SkinTypeEntity> skins=DBService.getDB().findAllByWhere(SkinTypeEntity.class, "id="+skinType);
			if(skins!=null&&skins.size()>0){
				return skins.get(0).getName();
			}
		}catch(NumberFormatException e){
			e.printStackTrace();
		}
		return "未知";
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
	
	private String getRegion(){
		try{
			String region = user.getRegion() == null|| "".equals(user.getRegion()) ? "110101":user.getRegion().length() < 6 ? "110101": user.getRegion();
			
			String mCurrentProviceName="";
			String mCurrentCityName="";
			String mCurrentDistrictName="";
			
			for (String key : MyApplication.mProvinceMap.keySet()) {
				String code = MyApplication.mProvinceMap.get(key);
				if (code.equals(region.substring(0, 2) + "0000")) {
					mCurrentProviceName = key;
				}
				if (code.equals(region.substring(0, 4) + "00")) {
					mCurrentCityName = key;
				}
				if (code.equals(region)) {
					mCurrentDistrictName = key;
				}

			}
			
			if(
					"北京".equals(mCurrentProviceName)
					|| "上海".equals(mCurrentProviceName)
					|| "重庆".equals(mCurrentProviceName)
					|| "天津".equals(mCurrentProviceName)
					|| "北京市".equals(mCurrentProviceName)
					|| "上海市".equals(mCurrentProviceName)
					|| "重庆市".equals(mCurrentProviceName)
					|| "天津市".equals(mCurrentProviceName)
					|| null == mCurrentProviceName
					|| "".equals(mCurrentProviceName)
					){
				return ("" + mCurrentProviceName + mCurrentDistrictName);
			}else{
				return ("" + mCurrentProviceName+ mCurrentCityName);
			}
		}catch(Exception e){
			return "北京市东城区";
		}
		
	}

	@Override
	public void onEvent(String event, Object... args) {
		
	}
}

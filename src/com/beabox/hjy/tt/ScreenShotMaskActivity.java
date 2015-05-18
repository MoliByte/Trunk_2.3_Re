package com.beabox.hjy.tt;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.app.base.entity.ProductEntity;
import com.app.base.entity.SkinTypeEntity;
import com.app.base.entity.UserEntity;
import com.app.base.init.ACache;
import com.app.base.init.MyApplication;
import com.app.custom.view.FacialChartView;
import com.avos.avoscloud.LogUtil.log;
import com.avoscloud.chat.service.UserService;
import com.avoscloud.chat.util.PhotoUtils;
import com.base.app.utils.DBService;
import com.base.app.utils.DateUtil;
import com.base.app.utils.DensityUtil;
import com.base.app.utils.FileUtil;
import com.base.app.utils.ImageParamSetter;
import com.base.app.utils.KVOEvents;
import com.base.app.utils.StringUtil;
import com.base.service.action.constant.HttpTagConstantUtils;
import com.beabox.hjy.tt.main.skintest.component.KVO.Observer;
import com.idongler.image.ImageUtil;
import com.idongler.widgets.CircleImageView;
import com.skinrun.trunk.facial.mask.test.DownLoadImageHelper;
import com.skinrun.trunk.facial.mask.test.ImageNameSaverUtil;
import com.skinrun.trunk.facial.mask.test.SaveSelectedProductUtil;
import com.umeng.message.PushAgent;


public class ScreenShotMaskActivity extends Activity implements  Observer{
	private CircleImageView circleUserImage;
	private TextView tvUserNick, tvLocation, tvUserAge, tvSkinType, userLevel,
			tvProductName, water1, water2, water3, tvPublishContent;
	private FacialChartView chart;
	private ImageView ivProduct;
	private String content="";
	private float c1,c2,c3;
	private int chartWidth, chartHeight, border;
	private UserEntity user;
	private float w1,w2,w3,w4;
	private ImageView raise1,raise2,raise3;
	Point p=MyApplication.screenSize;
	private String TAG="ScreenShotActivity";
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		MyApplication.getInstance().addActivity(this);
    	PushAgent.getInstance(this).onAppStart();
		MyApplication.getInstance().getKvo().registerObserver(KVOEvents.SHOT_SCREEN, this);
		super.onCreate(savedInstanceState);
		 //无title    
		requestWindowFeature(Window.FEATURE_NO_TITLE);    
        //全屏    
       getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,      
                      WindowManager.LayoutParams. FLAG_FULLSCREEN);  
		
		setContentView(R.layout.screenshot_layout);
		
			w1=getIntent().getFloatExtra("W1", 0);
			w2=getIntent().getFloatExtra("W2", 0);
			w3=getIntent().getFloatExtra("W3", 0);
			w4=getIntent().getFloatExtra("W4", 0);
			
			content=getIntent().getStringExtra("CONTENT");
			Log.e("ScreenShotActivity", "=========:"+w1+"  "+w2+"  "+w3+"  "+w4);
			
			
			Log.e("ScreenShotActivity", "=============要分享的文字："+content);
			try{
				if(w1!=0){
					if(w2!=0){
						c1=w2-w1;
					}
					if(w3!=0){
						c2=w3-w1;
					}
					if(w4!=0){
						c3=w4-w1;
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			
			initView();	
			getChartParam();
			setValue();
			
		
	}
	
	private void setRaiseOrDownIcon(ImageView iv,float num){
		try{
			if(num>=0){
				iv.setImageDrawable(getResources().getDrawable(R.drawable.facialraise));
			}else{
				iv.setImageDrawable(getResources().getDrawable(R.drawable.facial_result_down));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	//绝对值
	private int abValue(int num){
		if(num>=0){
			return num;
		}
		return -num;
	}
	
	private void setValue(){
		water1.setText(abValue((int)c1)+"%");
		water2.setText(abValue((int)c2)+"%");
		water3.setText(abValue((int)c3)+"%");
		
		setRaiseOrDownIcon(raise1, c1);
		setRaiseOrDownIcon(raise2, c2);
		setRaiseOrDownIcon(raise3, c3);
		
		tvPublishContent.setText(content);
		setCharData();
		user=DBService.getUserEntity();
		if(user!=null){
			ProductEntity selectedProduct=SaveSelectedProductUtil.getSelectProduct(user.getToken());
			
			
			tvProductName.setText(selectedProduct.getPro_brand_name());
			try{
				if(selectedProduct!=null){
					
					String imageName=ImageNameSaverUtil.getImageName(selectedProduct.getProductId()+"");
					FileUtil fileUtil=new FileUtil(this);
					Bitmap bitmap=fileUtil.getBitmapByPath(imageName);
					
					if(bitmap!=null){
						
						if(bitmap.getWidth()!=0||bitmap.getHeight()!=0){
							ImageParamSetter.setImageXFull(this, ivProduct, bitmap.getHeight()*1.0/bitmap.getWidth(), 0);
						}
						ivProduct.setVisibility(View.VISIBLE);
						ivProduct.setImageBitmap(bitmap);
					}else{
						DownLoadImageHelper.downLoadImage(this, selectedProduct.getPro_image() + "", ivProduct);
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			
			UserService.imageLoader.displayImage(""+user.getAvatar(), circleUserImage,
					PhotoUtils.normalImageOptions);
			
			
			tvUserNick.setText(user.getNiakname());
			tvLocation.setText(getRegion());
			tvUserAge.setText(DateUtil.getAge(user.getBirthday())+"岁");
			//tvSkinType.setText(getSkinType());
			
			tvSkinType.setText("" + HttpTagConstantUtils.getSkinTypeByKey(user.getSkinType()));
			
			String integral = ACache.get(this).getAsObject("integral")+"";
			
			userLevel.setText(""+StringUtil.getLevel(Integer.valueOf(integral==null||"null".equals(integral)|| "".equals(integral)?"0":integral)));
			
		}
	}
	
	
	
	private void initView(){
		circleUserImage=(CircleImageView)findViewById(R.id.circleUserImage);
		tvUserNick=(TextView)findViewById(R.id.tvUserNick);
		tvLocation=(TextView)findViewById(R.id.tvLocation);
		tvUserAge=(TextView)findViewById(R.id.tvUserAge);
		tvSkinType=(TextView)findViewById(R.id.tvSkinType);
		userLevel=(TextView)findViewById(R.id.userLevel);
		
		tvProductName=(TextView)findViewById(R.id.tvProductName);
		water1=(TextView)findViewById(R.id.water1);
		water2=(TextView)findViewById(R.id.water2);
		water3=(TextView)findViewById(R.id.water3);
		
		tvPublishContent=(TextView)findViewById(R.id.tvPublishContent);
		chart=(FacialChartView)findViewById(R.id.screenShotChartView);
		ivProduct=(ImageView)findViewById(R.id.ivProduct);
		
		raise1=(ImageView)findViewById(R.id.raise1);
		raise2=(ImageView)findViewById(R.id.raise2);
		raise3=(ImageView)findViewById(R.id.raise3);
	}
	
	
	@Override
	protected void onStart() {
		super.onStart();
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
	protected void onPause() {
		super.onPause();
	}
	
	
	@Override
	protected void onStop() {
		super.onStop();
		getImage();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		MyApplication.getInstance().getKvo().removeObserver(KVOEvents.SHOT_SCREEN, this);
	}
	@SuppressLint("SimpleDateFormat")
	private void getImage(){
		/*View view=findViewById(R.id.imageContainers);
		View image=view.getRootView();
		image.setDrawingCacheEnabled(true);
		image.buildDrawingCache();
		Bitmap bitmap=image.getDrawingCache();
		
		ByteArrayOutputStream out=new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);*/
		
		ScrollView view=(ScrollView) findViewById(R.id.scrollViewShot);
		Bitmap bitmap=ImageUtil.getBitmapByView(view);
		String imageName="";
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		imageName=sdf.format(new Date())+".png";
		
		FileUtil f=new FileUtil(this);
		
		try {
			
			String path=f.savaBitmap(imageName, bitmap, 100);
			MyApplication.getInstance().getKvo().fire(KVOEvents.SHOT_SCREEN,bitmap,path);
			
			/*
			SharedPreferences facialMaskShare=getSharedPreferences("FACIAL_MASK_SHARE_IMAGE_NAME",Activity.MODE_PRIVATE);
			SharedPreferences.Editor editor = facialMaskShare.edit();
			//用putString的方法保存数据
			editor.putString("SHARE_IMAGE_NAME", imageName);
			//提交当前数据
			editor.commit(); 
			setResult(0);
			log.e(TAG, "===========imageName"+imageName);*/
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	//得到肌肤类型
	private String getSkinTye(){
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
	
	private void getChartParam() {
		Point point = MyApplication.screenSize;
		chartWidth = point.x - DensityUtil.dip2px(this, 20);
		chartHeight = DensityUtil.dip2px(this, 60);
		border = DensityUtil.px2dip(this, 10);
	}
	private com.app.base.entity.Point getPoint(int index, float num) {
		com.app.base.entity.Point p = new com.app.base.entity.Point();

		if (index == 0) {
			int x = (chartWidth - 3 * border) / 8;
			p.setX(x);
		} else {
			int x = index * border + index * (chartWidth - 3 * border) / 4
					+ (chartWidth - 3 * border) / 8;
			p.setX(x);
		}
		int y = (int) (chartHeight - chartHeight / 80 * num);
		p.setY(y);

		return p;
	}
	
	private void setCharData() {
		ArrayList<String> texts = new ArrayList<String>();
		ArrayList<com.app.base.entity.Point> points = new ArrayList<com.app.base.entity.Point>();
		
			if(w1!=0){
				texts.add(m1(w1) + "%");
				points.add(getPoint(0, w1));
			}
			
			if(w2!=0){
				texts.add(m1(w2)+ "%");
				points.add(getPoint(1, w2));
			}
			
			if(w3!=0){
				texts.add(m1(w3)+ "%");
				points.add(getPoint(2, w3));
			}
			
			if(w4!=0){
				texts.add(m1(w4) + "%");
				points.add(getPoint(3, w4));
			
		}
		
		
		chart.setPoints(points);
		chart.setTexts(texts);
		chart.invalidate();
	}
	//保留一位小数
	private float m1(float f) {
		try {
			DecimalFormat df = new DecimalFormat("#.0");
			String ns = df.format(f);
			float result = Float.parseFloat(ns);
			return result;
		} catch (Exception e) {
			return 0;
		}
	}

	@Override
	public void onEvent(String event, Object... args) {
		
	}
}

package com.skinrun.trunk.facial.mask.test;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.app.base.entity.FacialComparaEntity;
import com.app.base.entity.FacialTestEntity;
import com.app.base.entity.ProductEntity;
import com.app.base.entity.UserEntity;
import com.app.base.init.AppBaseUtil;
import com.app.base.init.MyApplication;
import com.app.custom.view.FacialChartView;
import com.app.service.GetFacialmaskAdviceService;
import com.app.service.PblishMoodService;
import com.app.service.PostUserShareService;
import com.avos.avoscloud.LogUtil.log;
import com.base.app.utils.DBService;
import com.base.app.utils.DensityUtil;
import com.base.app.utils.DeviceUtil;
import com.base.app.utils.FileUtil;
import com.base.app.utils.ImageParamSetter;
import com.base.app.utils.KVOEvents;
import com.base.app.utils.UMShareUtil;
import com.base.app.utils.UMShareUtil.ShareAction;
import com.base.service.action.constant.HttpTagConstantUtils;
import com.base.service.impl.HttpAysnResultInterface;
import com.beabox.hjy.tt.R;
import com.beabox.hjy.tt.ScreenShotMaskActivity;
import com.beabox.hjy.tt.SkinResultShareActivity;
import com.beabox.hjy.tt.main.skintest.component.KVO.Observer;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.listener.SocializeListeners.SocializeClientListener;
import com.umeng.socialize.sso.UMSsoHandler;

public class FacialmaskResultActivity extends Activity implements
		OnClickListener, HttpAysnResultInterface, OnCheckedChangeListener, Observer {
	private FacialChartView facialChartView;
	private TextView tvFacialResultName, step2time, step3time, step4time,
			facial_text1, facial_text2, facial_text3, tvFacial_advice;
	private ImageView facial_water1, facial_water2, facial_water3,
			facial_water4, facial_water5, facial_water6, facial_water7,
			facial_water8, facial_water9, facial_water10, facial_water11,
			facial_water12, facial_water13, facial_water14, facial_water15,
			facial_raise1, facial_raise2, facial_raise3,ivProductImage;
	private int chartWidth, chartHeight, border;
	private EditText editTextWord;
	private RadioGroup rgShare;
	private String shareText="";
	
	//测试ID
	private int id;
	
	private int ShareTag=WX_SHARE;
	private static final int SINA_SHARE=0x001,WX_SHARE=0x002;
	private FacialTestEntity facialTestEntity;
	private ProductEntity selectedProduct;
	private FacialComparaEntity facialComparaEntity;
	
	private String Tag="FacialmaskResultActivity";
	
	private long waterTime1,waterTime2,waterTime3,waterTime4;
	//阶段测试比较值
	private float c1=0,c2=0,c3=0;
	private float w1,w2,w3,w4;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		MyApplication.getInstance().addActivity(this);
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().getKvo().registerObserver(KVOEvents.SHOT_SCREEN, this);
		
		Intent intent=getIntent();
		if(intent!=null){
			facialTestEntity=(FacialTestEntity) intent.getSerializableExtra("FacialTestEntity");
			facialComparaEntity=(FacialComparaEntity) intent.getSerializableExtra("facialComparaEntity");
			try{
				
				if(facialTestEntity.getWater1()!=0){
					w1=facialTestEntity.getWater1();
					if(facialTestEntity.getWater2()!=0){
						w2=facialTestEntity.getWater2();
						c1=facialTestEntity.getWater2()-facialTestEntity.getWater1();
					}
					if(facialTestEntity.getWater3()!=0){
						w3=facialTestEntity.getWater3();
						c2=facialTestEntity.getWater3()-facialTestEntity.getWater1();
					}
					if(facialTestEntity.getWater4()!=0){
						w4=facialTestEntity.getWater4();
						c3=facialTestEntity.getWater4()-facialTestEntity.getWater1();
					}
				}
				id=facialComparaEntity.getId();
			}catch(Exception e){
				
			}
		}
		UserEntity user=DBService.getUserEntity();
		if(user!=null){
			selectedProduct=SaveSelectedProductUtil.getSelectProduct(user.getToken());
			//下载图片
			//DownLoadImageHelper.downLoadImage(selectedProduct.getPro_image(), this);
			//SaveFacialResultUtil.delete(""+user.getToken());
		}
		
		
		setContentView(R.layout.facialresult);

		getChartParam();

		initView();

	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		MyApplication.getInstance().getKvo().removeObserver(KVOEvents.SHOT_SCREEN, this);
		UserEntity user=DBService.getUserEntity();
		if(user!=null){
			SaveFacialResultUtil.delete(user.getToken());
		}
	}
	
	private void setCharData() {
		ArrayList<String> texts = new ArrayList<String>();
		ArrayList<com.app.base.entity.Point> points = new ArrayList<com.app.base.entity.Point>();
		if(facialTestEntity!=null){
			if(facialTestEntity.getWater1()!=0){
				texts.add(m1(facialTestEntity.getWater1()) + "%");
				points.add(getPoint(0, facialTestEntity.getWater1()));
				waterTime1=Long.parseLong(facialTestEntity.getTime1());
			}
			
			if(facialTestEntity.getWater2()!=0){
				texts.add(m1(facialTestEntity.getWater2() )+ "%");
				points.add(getPoint(1, facialTestEntity.getWater2()));
				waterTime2=Long.parseLong(facialTestEntity.getTime2());
			}
			
			if(facialTestEntity.getWater3()!=0){
				texts.add(m1(facialTestEntity.getWater3() )+ "%");
				points.add(getPoint(2, facialTestEntity.getWater3()));
				waterTime3=Long.parseLong(facialTestEntity.getTime3());
			}
			
			if(facialTestEntity.getWater4()!=0){
				texts.add(m1(facialTestEntity.getWater4()) + "%");
				points.add(getPoint(3, facialTestEntity.getWater4()));
				waterTime4=Long.parseLong(facialTestEntity.getTime4());
			}
		}
		
		
		facialChartView.setPoints(points);
		facialChartView.setTexts(texts);
		facialChartView.invalidate();
		
		if(waterTime2==0){
			step2time.setText("") ;
		}else{
			long druation2=(waterTime3-waterTime2)/1000;
			step3time.setText(dealTime(druation2));
		}
		
		if(waterTime3==0){
			step4time.setText("");
		}else{
			long druation3=(waterTime4-waterTime3)/1000;
			step4time.setText(dealTime(druation3));
		}
		
		long druation1=(waterTime2-waterTime1)/1000;
		step2time.setText(dealTime(druation1)) ;
		
	}
	
	private String dealTime(long druation){
		if(druation>0){
			int hour = (int) Math.abs(druation / 3600);
			int minute = (int) Math.abs(druation % 3600 / 60);
			int second = (int) Math.abs(druation % 3600 % 60);
			if(hour==0){
				return minute+"分"+second+"秒";
			}else{
				return hour+"时"+minute+"分";
			}
		}else{
			return "";
		}
	}
	//绝对值
	private float abValue(float num){
		if(num>=0){
			return num;
		}
		return -num;
	}
	
	//保留一位小数
	private float m1(float f) {
		try{
			 DecimalFormat df = new DecimalFormat("#.0");
		     String ns=df.format(f);
		     float result=Float.parseFloat(ns);
		     
		     
		     return result;
		}catch(Exception e){
			return 0;
		}
       
    }
	
	
	
	private void initView() {
		facialChartView = (FacialChartView) findViewById(R.id.facialChartView);
		tvFacialResultName = (TextView) findViewById(R.id.tvFacialResultName);

		step2time = (TextView) findViewById(R.id.step2time);
		step3time = (TextView) findViewById(R.id.step3time);
		step4time = (TextView) findViewById(R.id.step4time);
		facial_text1 = (TextView) findViewById(R.id.facial_text1);
		facial_text2 = (TextView) findViewById(R.id.facial_text2);
		facial_text3 = (TextView) findViewById(R.id.facial_text3);
		tvFacial_advice = (TextView) findViewById(R.id.tvFacial_advice);

		facial_water1 = (ImageView) findViewById(R.id.facial_water1);
		facial_water2 = (ImageView) findViewById(R.id.facial_water2);
		facial_water3 = (ImageView) findViewById(R.id.facial_water3);
		facial_water4 = (ImageView) findViewById(R.id.facial_water4);
		facial_water5 = (ImageView) findViewById(R.id.facial_water5);
		facial_water6 = (ImageView) findViewById(R.id.facial_water6);
		facial_water7 = (ImageView) findViewById(R.id.facial_water7);
		facial_water8 = (ImageView) findViewById(R.id.facial_water8);
		facial_water9 = (ImageView) findViewById(R.id.facial_water9);
		facial_water10 = (ImageView) findViewById(R.id.facial_water10);
		facial_water11 = (ImageView) findViewById(R.id.facial_water11);
		facial_water12 = (ImageView) findViewById(R.id.facial_water12);
		facial_water13 = (ImageView) findViewById(R.id.facial_water13);
		facial_water14 = (ImageView) findViewById(R.id.facial_water14);
		facial_water15 = (ImageView) findViewById(R.id.facial_water15);

		facial_raise1 = (ImageView) findViewById(R.id.facial_raise1);
		facial_raise2 = (ImageView) findViewById(R.id.facial_raise2);
		facial_raise3 = (ImageView) findViewById(R.id.facial_raise3);

		ivProductImage=(ImageView)findViewById(R.id.ivProductImage);
		
		findViewById(R.id.ivStartShare).setOnClickListener(this);
		
		editTextWord=(EditText)findViewById(R.id.editTextWord);
		
		findViewById(R.id.backBtnFacialResult).setOnClickListener(this);
		findViewById(R.id.facialResultShare).setOnClickListener(this);
		
		rgShare=(RadioGroup)findViewById(R.id.radioGroupShare);
		rgShare.setOnCheckedChangeListener(this);
		
		if(selectedProduct!=null){
			tvFacialResultName.setText(selectedProduct.getPro_brand_name());
			//UrlImageViewHelper.setUrlDrawable(ivProductImage, selectedProduct.getPro_image(), R.drawable.mianmo);
			try{
				if(selectedProduct!=null){
					String imageName=ImageNameSaverUtil.getImageName(selectedProduct.getProductId()+"");
					FileUtil fileUtil=new FileUtil(this);
					Bitmap bitmap=fileUtil.getBitmapByPath(imageName);
					
					if(bitmap!=null&&bitmap.getWidth()!=0&&bitmap.getHeight()!=0){
						ImageParamSetter.setImageXFull(this, ivProductImage, bitmap.getHeight()*1.0/bitmap.getWidth(), 0);
						ivProductImage.setVisibility(View.VISIBLE);
						ivProductImage.setImageBitmap(bitmap);
					}else{
						/*UserService.imageLoader.displayImage(selectedProduct.getPro_image() + "", ivProductImage,
								PhotoUtils.normalImageOptions);*/
						DownLoadImageHelper.downLoadImage(this, selectedProduct.getPro_image() + "", ivProductImage);
						//MyImageLoader.showFullImage(this, selectedProduct.getPro_image() + "", ivProductImage);
					}
				}
			}catch(Exception e){
				
			}
			
			
		}
		
		if(facialTestEntity!=null){
			if(facialTestEntity.getWater1()!=0){
				if(facialTestEntity.getWater2()!=0){
					facial_text1.setText(abValue(m1(c1))+"%");
					setRaiseOrDownIcon(facial_raise1,(m1(c1)));
				}else{
					facial_raise1.setVisibility(View.GONE);
					facial_text1.setText("没有测试");
					facial_text1.setTextColor(getResources().getColor(R.color.facial_gray));
				}
				
				if(facialTestEntity.getWater3()!=0){
					facial_text2.setText(abValue(m1(c2))+"%");
					setRaiseOrDownIcon(facial_raise2, (m1(c2)));
					
				}else{
					facial_raise2.setVisibility(View.GONE);
					facial_text2.setText("没有测试");
					facial_text2.setTextColor(getResources().getColor(R.color.facial_gray));
				}
				
				if(facialTestEntity.getWater4()!=0){
					facial_text3.setText(abValue(m1(c3))+"%");
					setRaiseOrDownIcon(facial_raise3, (m1(c3)));
					
				}else{
					facial_raise3.setVisibility(View.GONE);
					facial_text3.setText("没有测试");
					facial_text3.setTextColor(getResources().getColor(R.color.facial_gray));
				}
				
				
				
			}else{
				facial_raise1.setVisibility(View.GONE);
				facial_raise2.setVisibility(View.GONE);
				facial_raise3.setVisibility(View.GONE);
				facial_text1.setText("没有测试");
				facial_text2.setText("没有测试");
				facial_text3.setText("没有测试");
				
				facial_text1.setTextColor(getResources().getColor(R.color.facial_gray));
				facial_text2.setTextColor(getResources().getColor(R.color.facial_gray));
				facial_text3.setTextColor(getResources().getColor(R.color.facial_gray));
			}
		}else{
			facial_raise1.setVisibility(View.GONE);
			facial_raise2.setVisibility(View.GONE);
			facial_raise3.setVisibility(View.GONE);
			facial_text1.setText("没有测试");
			facial_text2.setText("没有测试");
			facial_text3.setText("没有测试");
			
			facial_text1.setTextColor(getResources().getColor(R.color.facial_gray));
			facial_text2.setTextColor(getResources().getColor(R.color.facial_gray));
			facial_text3.setTextColor(getResources().getColor(R.color.facial_gray));
			
		}
		
		
		
		/*else if(facialComparaEntity!=null){
			try{
				id=facialComparaEntity.getId();
				
				facial_text1.setText(facialComparaEntity.getTestbefore1()+"");
				facial_text2.setText(facialComparaEntity.getTestbefore2()+"");
				facial_text3.setText(facialComparaEntity.getTestbefore3()+"");
				
				if(facialComparaEntity.getTestbefore1()<0){
					facial_raise1.setImageDrawable(getResources().getDrawable(R.drawable.facial_result_down));
				}
				
				if(facialComparaEntity.getTestbefore2()<0){
					facial_raise2.setImageDrawable(getResources().getDrawable(R.drawable.facial_result_down));
				}
				
				if(facialComparaEntity.getTestbefore3()<0){
					facial_raise3.setImageDrawable(getResources().getDrawable(R.drawable.facial_result_down));
				}
			}catch(Exception e){}
		}*/
		//设置折线图数据
		setCharData();
		
		GetFacialmaskAdviceService getFacialmaskAdviceService=new GetFacialmaskAdviceService(this, HttpTagConstantUtils.GET_FACIALMASK_ADVICE, this);
		try{
			getFacialmaskAdviceService.doGetAdvice(DBService.getUserEntity().getToken(), c1, c2, c3, selectedProduct.getProductId());
		}catch(Exception e){
			
		}
	}
	
	private void setRaiseOrDownIcon(ImageView iv,float num){
		try{
			if(num>=0){
				iv.setImageDrawable(getResources().getDrawable(R.drawable.facialraise));
			}else{
				iv.setImageDrawable(getResources().getDrawable(R.drawable.facial_result_down));
			}
		}catch(Exception e){
			
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.backBtnFacialResult:
			finish();
			break;
		case R.id.facialResultShare:
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");

			File file = new File(AppBaseUtil.DEFAULT_CACHE_FOLDER, "肌肤管家"
					+ sdf.format(new Date()) + ".png");
			break;
		case R.id.ivStartShare:
			try{
				//上传图片
				new PblishMoodService(this, HttpTagConstantUtils.PUBLISH_MOOD, this).
				publishMoood(DBService.getUserEntity().getToken(), id, editTextWord.getText().toString());
				Log.e(Tag, "=============点击上传");
				
			}catch(Exception e){
				
			}
			
			shareText=editTextWord.getText().toString();
			
			Intent i=new Intent(FacialmaskResultActivity.this, ScreenShotMaskActivity.class);
			i.putExtra("W1", w1);
			i.putExtra("W2", w2);
			i.putExtra("W3", w3);
			i.putExtra("W4", w4);
			
			i.putExtra("CONTENT", shareText);
			
			startActivityForResult(i, 0x123);
			
			
			break;
		}
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		log.e(Tag, "=========="+"回传bitmap"+requestCode+"       "+resultCode);
		/** 使用SSO授权必须添加如下代码 */
		UMSsoHandler ssoHandler = UMShareUtil.getInstance()
				.getUmsocialService().getConfig().getSsoHandler(requestCode);
		if (ssoHandler != null) {
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
		
	}
	
	
	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.activity_enter_from_left,
				R.anim.activity_exit_to_right);
	}

	@Override
	public void dataCallBack(Object tag, int statusCode, Object result) {
		switch((Integer)tag){
		case HttpTagConstantUtils.GET_FACIALMASK_ADVICE:
			try{
				String suggestion=(String) result;
				if(!suggestion.equals("")){
					tvFacial_advice.setText(suggestion);
				}
				
			}catch(Exception e){
				
			}
			
			
			break;
		case HttpTagConstantUtils.PUBLISH_MOOD:
			Log.e(Tag, "=============上传心情返回码"+statusCode);
			if(statusCode==200||statusCode==201){
				//AppToast.toastMsg(FacialmaskResultActivity.this, "上传心情成功").show();
			}else{
				//AppToast.toastMsg(FacialmaskResultActivity.this, "上传心情失败").show();
			}
			break;
		}
	}
	 /**
     * 注销本次登录</br>
     */
    public void logout(final SHARE_MEDIA platform) {
    	UMShareUtil.getInstance().getUmsocialService().deleteOauth(this, platform, new SocializeClientListener() {

            @Override
            public void onStart() {
            	
            }

            @Override
            public void onComplete(int status, SocializeEntity entity) {
                
            }
        });
    }

	@Override
	public void onCheckedChanged(RadioGroup arg0, int arg1) {
		switch(arg1){
		case R.id.radioWXShare:
			ShareTag=WX_SHARE;
			break;
		case R.id.radioSinaShare:
			ShareTag=SINA_SHARE;
			break;
		}
	}
	//得到所拍照片路径
	private String getImagePath(){
		try{
			return ImageNameSaverUtil.getImageName(selectedProduct.getProductId()+"");
			
		}catch(Exception e){
			return "";
		}
	}
	

	@Override
	public void onEvent(String event, Object... args) {
		
		try{
			
			//Bitmap bitmap=PhotoUtils.getImageThumbnail(path, 400, 800);
			final Bitmap bitmap=(Bitmap)args[0];
			
			String path=(String) args[1];
			
			
			final Bitmap bitmap2=BitmapFactory.decodeFile(path);
			
			if(bitmap!=null){
				bitmap2.recycle();
			}
			
			//ivProductImage.setImageBitmap(bitmap);
			// 使用友盟分享
			switch (ShareTag) {
			case WX_SHARE:
				/*CustomShareUtil.getInstance().WXHShare(this, "#皮肤测试",editTextWord.getText().toString(), "", bitmap,
						path, new ShareAction() {

							@Override
							public void onSuccess() {
								Intent i=new Intent(FacialmaskResultActivity.this, SkinRunMainActivity.class);
								AppToast.toastMsg(FacialmaskResultActivity.this,"分享成功");
								startActivity(i);
								overridePendingTransition(R.anim.activity_enter_from_right, R.anim.activity_exit_to_left);
								finish();
							}
						});*/
				if (!DeviceUtil.isOnline()) {
					Toast.makeText(this, R.string.test_network_error_share_message,
							Toast.LENGTH_LONG).show();
					return;
				}
				logout(SHARE_MEDIA.WEIXIN_CIRCLE);
				if(bitmap!=null){
					UMShareUtil.getInstance().WXCShareImage(this, " ", bitmap, new ShareAction() {
						
						@Override
						public void onSuccess() {
							bitmap.recycle();
							 postShareResult();
							finish();
						}
					});
				}else if(bitmap2!=null){
					UMShareUtil.getInstance().WXCShareImage(this, " ", bitmap2, new ShareAction() {
						
						@Override
						public void onSuccess() {
							bitmap2.recycle();
							 postShareResult();
							finish();
						}
					});
				}
				
				break;
			case SINA_SHARE:
				if (!DeviceUtil.isOnline()) {
					Toast.makeText(this, R.string.test_network_error_share_message,
							Toast.LENGTH_LONG).show();
					return;
				}
				logout(SHARE_MEDIA.SINA);
				if(bitmap!=null){
					UMShareUtil.getInstance().sinaShareImage(this, " ", bitmap, new ShareAction() {
						
						@Override
						public void onSuccess() {
							bitmap.recycle();
							 postShareResult();
							finish();
						}
					});
				}else if(bitmap2!=null){
						UMShareUtil.getInstance().sinaShareImage(this, " ", bitmap2, new ShareAction() {
						
						@Override
						public void onSuccess() {
							bitmap2.recycle();
							 postShareResult();
							finish();
						}
					});
				}
				break;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	private void postShareResult(){
		UserEntity user=DBService.getUserEntity();
		if(user!=null){
			new PostUserShareService(FacialmaskResultActivity.this,HttpTagConstantUtils.SHARE_JIFEN,
					FacialmaskResultActivity.this).post_share(user.getToken());
		}
	}
}

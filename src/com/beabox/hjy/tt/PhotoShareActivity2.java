package com.beabox.hjy.tt;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.codec.binary.Base64;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.app.base.entity.ProductEntity;
import com.app.base.entity.ProductImageEntity;
import com.app.base.entity.UserEntity;
import com.app.base.init.MyApplication;
import com.app.service.UpLoadImageService;
import com.app.service.UpLoadSkinImageService;
import com.avoscloud.chat.base.ChatMsgAdapter;
import com.avoscloud.chat.util.PhotoUtils;
import com.base.app.utils.DBService;
import com.base.app.utils.DensityUtil;
import com.base.app.utils.DeviceUtil;
import com.base.app.utils.FileUtil;
import com.base.app.utils.KVOEvents;
import com.base.app.utils.ProgressDialogUtil;
import com.base.app.utils.UMShareUtil;
import com.base.app.utils.UMShareUtil.ShareAction;
import com.base.service.action.constant.HttpTagConstantUtils;
import com.base.service.impl.HttpAysnResultInterface;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.main.skintest.component.CartoonImageComp;
import com.beabox.hjy.tt.main.skintest.component.CartoonTextComp;
import com.beabox.hjy.tt.main.skintest.component.KVO.Observer;
import com.beabox.hjy.tt.main.skintest.component.MarginParams;
import com.beabox.hjy.tt.main.skintest.component.UserInfoComp;
import com.idongler.image.ImageUtil;
import com.idongler.imagefilter.IImageFilter;
import com.idongler.imagefilter.Image;
import com.idongler.imagefilter.effect.BrightContrastFilter;
import com.idongler.imagefilter.effect.FilmFilter;
import com.idongler.imagefilter.effect.NewLomoFilter;
import com.idongler.imagefilter.effect.SaturationModifyFilter;
import com.skinrun.trunk.facial.mask.test.ImageNameSaverUtil;
import com.skinrun.trunk.facial.mask.test.PhotoType;
import com.skinrun.trunk.facial.mask.test.SaveSelectedProductUtil;
import com.umeng.message.PushAgent;
import com.umeng.socialize.sso.UMSsoHandler;


public class PhotoShareActivity2 extends Activity implements OnClickListener,
		ShareAction, Observer, HttpAysnResultInterface, OnCheckedChangeListener {
	private FileUtil fileUtil;

	private ViewGroup container;

	private int maxWidthHeigth = 800;

	private int dis_width;
	private int dis_height;

	private String link = null;
	private String fileName;

	private UserInfoComp userInfoComp;
	private CartoonImageComp catonImageComp;
	private CartoonTextComp catonTextComp;
	private ImageView imageView;

	private ImageView nativeImage;
	private TextView nativeImageTxt;
	private View nativeImageBorder;

	private ImageView blackWhiteImage;
	private TextView blackWhiteImageTxt;
	private View blackWhiteImageBorder;

	private ImageView blurImage;
	private TextView blurImageTxt;
	private View blurImageBorder;

	private ImageView lomoImage;
	private TextView lomoImageTxt;
	private View lomoImageBorder;

	private ImageView softGlowImage;
	private TextView softGlowImageTxt;
	private View softGlowImageBorder;

	private Handler mHandler = new Handler();
	private final int BLUR_RADIUS = 20;
	private int dp80 = 80;
	private int dp2 = 2;

	private String shareTitle;
	private String shareContent;
	private String shareLinkUrl;
	
	private ProgressDialog dialog;
	
	private int TestId;
	float waterValues = 0, oilValues = 0, flexibleValues = 0;
	private int partFlag;
	
	
	private UpLoadImageService upLoadImageService;

	private static int photoType = PhotoType.SKIN_PHOTO;
	
	
	private RadioGroup rgSpecialEffect;
	private View filter_container,overlayContainer,signContainer;
	private ImageView overlay0,overlay1,overlay2,overlay3,overlay4,sign1,sign2,sign3,ivSign;
	
	private int testPreDiff;
	
	Bitmap filterBitmap ;
	Bitmap blackWhite ;
	Bitmap blur ;
	Bitmap lomo ;
	Bitmap softGlow ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		MyApplication.getInstance().addActivity(this);
    	PushAgent.getInstance(this).onAppStart();
		super.onCreate(savedInstanceState);
		register();
		upLoadImageService=new UpLoadImageService(this, HttpTagConstantUtils.UPLOAD_IMAGE, this);
		setContentView(R.layout.activity_photo_share2);
		init();
		initOverLayView();
		
		setClickListener();
	}
	
	private void initOverLayView(){
		rgSpecialEffect=(RadioGroup)findViewById(R.id.rgSpecialEffect);
		rgSpecialEffect.setOnCheckedChangeListener(this);
		
		filter_container=findViewById(R.id.filter_container);
		overlayContainer=findViewById(R.id.overlayContainer);
		signContainer=findViewById(R.id.signContainer);
		
		overlay0=(ImageView)findViewById(R.id.overlay0);
		overlay1=(ImageView)findViewById(R.id.overlay1);
		overlay2=(ImageView)findViewById(R.id.overlay2);
		overlay3=(ImageView)findViewById(R.id.overlay3);
		overlay4=(ImageView)findViewById(R.id.overlay4);
		
		sign1=(ImageView)findViewById(R.id.sign1);
		sign2=(ImageView)findViewById(R.id.sign2);
		sign3=(ImageView)findViewById(R.id.sign3);
		ivSign=(ImageView)findViewById(R.id.ivSign);
		
		/*Resources res = getResources();
	    String[] imgName = null;
		
	    if(testPreDiff>0){
			imgName = res.getStringArray(R.array.skintest_value_up_img);
		}else if(testPreDiff<0){
			imgName = res.getStringArray(R.array.skintest_value_down_img);
		}else{
			imgName = res.getStringArray(R.array.skintest_value_same_img);
		}
		
		int resId0=getResources().getIdentifier(imgName[0], "drawable", getPackageName());
		int resId1=getResources().getIdentifier(imgName[1], "drawable", getPackageName());
		int resId2=getResources().getIdentifier(imgName[2], "drawable", getPackageName());
		int resId3=getResources().getIdentifier(imgName[3], "drawable", getPackageName());
		int resId4=getResources().getIdentifier(imgName[4], "drawable", getPackageName());
		
		try{
			Drawable drawable0=getResources().getDrawable(resId0);
			Drawable drawable1=getResources().getDrawable(resId1);
			Drawable drawable2=getResources().getDrawable(resId2);
			Drawable drawable3=getResources().getDrawable(resId3);
			Drawable drawable4=getResources().getDrawable(resId4);
			
			overlay0.setImageDrawable(drawable0);
			overlay1.setImageDrawable(drawable1);
			overlay2.setImageDrawable(drawable2);
			overlay3.setImageDrawable(drawable3);
			overlay4.setImageDrawable(drawable4);
			
			
		}catch(Exception e){
			e.printStackTrace();
		}*/
	}
	
	
	private void setClickListener(){
		overlay0.setOnClickListener(this);
		overlay1.setOnClickListener(this);
		overlay2.setOnClickListener(this);
		overlay3.setOnClickListener(this);
		overlay4.setOnClickListener(this);
		sign1.setOnClickListener(this);
		sign2.setOnClickListener(this);
		sign3.setOnClickListener(this);
		
		findViewById(R.id.container).setOnClickListener(this);
		findViewById(R.id.delete_image).setOnClickListener(this);
		findViewById(R.id.ivDeleteSign).setOnClickListener(this);
		
		findViewById(R.id.overlay5).setOnClickListener(this);
		findViewById(R.id.overlay6).setOnClickListener(this);
		findViewById(R.id.overlay7).setOnClickListener(this);
		findViewById(R.id.overlay8).setOnClickListener(this);
		findViewById(R.id.overlay9).setOnClickListener(this);
		findViewById(R.id.overlay10).setOnClickListener(this);
		
		findViewById(R.id.overlay11).setOnClickListener(this);
		findViewById(R.id.overlay12).setOnClickListener(this);
		findViewById(R.id.overlay13).setOnClickListener(this);
		findViewById(R.id.overlay14).setOnClickListener(this);
		findViewById(R.id.overlay15).setOnClickListener(this);
		findViewById(R.id.overlay16).setOnClickListener(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (userInfoComp != null) {
			userInfoComp.destroy();
			userInfoComp = null;
		}
		if (catonImageComp != null) {
			catonImageComp.destroy();
			catonImageComp = null;
		}
		if (catonTextComp != null) {
			catonTextComp.destroy();
			catonTextComp = null;
		}
		recycleImg();
		MyApplication.getInstance().getKvo()
				.removeObserver(KVOEvents.SHARE_SUCCESS, this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		/*Bitmap filterBitmap ;
		Bitmap blackWhite ;
		Bitmap blur ;
		Bitmap lomo ;
		Bitmap softGlow ;*/
		recycleImg();
		
		
	}
	
	public void recycleImg(){

		if(filterBitmap != null){
			filterBitmap.recycle() ;
		}
		if(blackWhite != null){
			blackWhite.recycle() ;
		}
		if(blur != null){
			blur.recycle() ;
		}
		if(lomo != null){
			lomo.recycle() ;
		}
		if(softGlow != null){
			softGlow.recycle() ;
		}
		filterBitmap = null ;
		blackWhite = null ;
		blur = null ;
		lomo = null ;
		softGlow = null ;
		System.gc();
	}

	private void init() {
		findViewById(R.id.backBtn).setOnClickListener(this);
		findViewById(R.id.popupShareBtn).setOnClickListener(this);
		container = (ViewGroup) findViewById(R.id.container);

		link = getString(R.string.skintest_share_link);
		dp80 = DensityUtil.dip2px(this, dp80);
		dp2 = DensityUtil.dip2px(this, dp2);
		fileUtil = new FileUtil(this);

		DisplayMetrics dm = DensityUtil.getWindowRect(this);
		dis_width = dm.widthPixels;// - DensityUtil.dip2px(this, 20);
		dis_height = dm.heightPixels/2;// - DensityUtil.dip2px(this, 170);

		initView();
	}

	private void register() {
		MyApplication.getInstance().getKvo()
				.registerObserver(KVOEvents.SHARE_SUCCESS, this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.backBtn:
			recycleImg();
			finish();
			break;
		case R.id.popupShareBtn:
			switch(photoType){
			case PhotoType.SKIN_PHOTO:
				popupShare();
				break;
			case PhotoType.FACIAL_PHOTO:
				upLoadImage();
				
				break;
			}
			
			
			break;
		case R.id.nativeImage:
			makFilter(nativeImageBorder, null);
			break;
		case R.id.blackWhiteImage:
			makFilter(blackWhiteImageBorder, new FilmFilter());
			break;
		case R.id.blurImage:
			makFilter(blurImageBorder, new SaturationModifyFilter());
			break;
		case R.id.lomoImage:
			makFilter(lomoImageBorder, new NewLomoFilter());
			break;
		case R.id.softGlowImage:
			makFilter(softGlowImageBorder, new BrightContrastFilter());
			break;
		case R.id.overlay0:
			findViewById(R.id.ctImageGroupInner).setSelected(true);
			findViewById(R.id.ctImageGroup).setVisibility(View.VISIBLE);
			catonImageComp.setImageDrawable(R.drawable.ct_up_0);
			
			break;
		case R.id.overlay1:
			findViewById(R.id.ctImageGroupInner).setSelected(true);
			findViewById(R.id.ctImageGroup).setVisibility(View.VISIBLE);
			catonImageComp.setImageDrawable(R.drawable.ct_up_1);
			
			break;
		case R.id.overlay2:
			findViewById(R.id.ctImageGroupInner).setSelected(true);
			findViewById(R.id.ctImageGroup).setVisibility(View.VISIBLE);
			catonImageComp.setImageDrawable(R.drawable.ct_up_2);
			
			break;
		case R.id.overlay3:
			findViewById(R.id.ctImageGroupInner).setSelected(true);
			findViewById(R.id.ctImageGroup).setVisibility(View.VISIBLE);
			catonImageComp.setImageDrawable(R.drawable.ct_up_3);
			
			break;
		case R.id.overlay4:
			findViewById(R.id.ctImageGroupInner).setSelected(true);
			findViewById(R.id.ctImageGroup).setVisibility(View.VISIBLE);
			catonImageComp.setImageDrawable(R.drawable.ct_up_4);
			break;
		case R.id.overlay5:
			findViewById(R.id.ctImageGroupInner).setSelected(true);
			findViewById(R.id.ctImageGroup).setVisibility(View.VISIBLE);
			catonImageComp.setImageDrawable(R.drawable.ct_down_0);
			break;
			
		case R.id.overlay6:
			findViewById(R.id.ctImageGroupInner).setSelected(true);
			findViewById(R.id.ctImageGroup).setVisibility(View.VISIBLE);
			catonImageComp.setImageDrawable(R.drawable.ct_down_1);
			break;
		case R.id.overlay7:
			findViewById(R.id.ctImageGroupInner).setSelected(true);
			findViewById(R.id.ctImageGroup).setVisibility(View.VISIBLE);
			catonImageComp.setImageDrawable(R.drawable.ct_down_2);
			break;
		case R.id.overlay8:
			findViewById(R.id.ctImageGroupInner).setSelected(true);
			findViewById(R.id.ctImageGroup).setVisibility(View.VISIBLE);
			catonImageComp.setImageDrawable(R.drawable.ct_down_3);
			break;
		case R.id.overlay9:
			findViewById(R.id.ctImageGroupInner).setSelected(true);
			findViewById(R.id.ctImageGroup).setVisibility(View.VISIBLE);
			catonImageComp.setImageDrawable(R.drawable.ct_down_4);
			break;
		case R.id.overlay10:
			findViewById(R.id.ctImageGroupInner).setSelected(true);
			findViewById(R.id.ctImageGroup).setVisibility(View.VISIBLE);
			catonImageComp.setImageDrawable(R.drawable.ct_same_0);
			break;
		case R.id.overlay11:
			findViewById(R.id.ctImageGroupInner).setSelected(true);
			findViewById(R.id.ctImageGroup).setVisibility(View.VISIBLE);
			catonImageComp.setImageDrawable(R.drawable.ct_same_1);
			break;
		case R.id.overlay12:
			findViewById(R.id.ctImageGroupInner).setSelected(true);
			findViewById(R.id.ctImageGroup).setVisibility(View.VISIBLE);
			catonImageComp.setImageDrawable(R.drawable.ct_same_2);
			break;
		case R.id.overlay13:
			findViewById(R.id.ctImageGroupInner).setSelected(true);
			findViewById(R.id.ctImageGroup).setVisibility(View.VISIBLE);
			catonImageComp.setImageDrawable(R.drawable.ct_same_3);
			break;
		case R.id.overlay14:
			findViewById(R.id.ctImageGroupInner).setSelected(true);
			findViewById(R.id.ctImageGroup).setVisibility(View.VISIBLE);
			catonImageComp.setImageDrawable(R.drawable.ct_same_4);
			break;
		case R.id.overlay15:
			findViewById(R.id.ctImageGroupInner).setSelected(true);
			findViewById(R.id.ctImageGroup).setVisibility(View.VISIBLE);
			catonImageComp.setImageDrawable(R.drawable.overlay16);
			break;
		case R.id.overlay16:
			findViewById(R.id.ctImageGroupInner).setSelected(true);
			findViewById(R.id.ctImageGroup).setVisibility(View.VISIBLE);
			catonImageComp.setImageDrawable(R.drawable.overlay17);
			break;
		
			
		case R.id.container:
			findViewById(R.id.delete_image).setVisibility(View.GONE);
			findViewById(R.id.ctImageGroupInner).setSelected(false);
			findViewById(R.id.ivDeleteSign).setVisibility(View.GONE);
			findViewById(R.id.userInfoGroupInner).setSelected(false);
			
			break;
		case R.id.delete_image:
			findViewById(R.id.ctImageGroup).setVisibility(View.GONE);
			break;
		case R.id.sign1:
			findViewById(R.id.userInfoGroupInner).setSelected(true);
			findViewById(R.id.userInfoGroup).setVisibility(View.VISIBLE);
			ivSign.setImageDrawable(getResources().getDrawable(R.drawable.sign1));
			break;
		case R.id.sign2:
			findViewById(R.id.userInfoGroupInner).setSelected(true);
			findViewById(R.id.userInfoGroup).setVisibility(View.VISIBLE);
			ivSign.setImageDrawable(getResources().getDrawable(R.drawable.sign2));
			break;
		case R.id.sign3:
			findViewById(R.id.userInfoGroupInner).setSelected(true);
			findViewById(R.id.userInfoGroup).setVisibility(View.VISIBLE);
			ivSign.setImageDrawable(getResources().getDrawable(R.drawable.sign3));
			break;
		case R.id.ivDeleteSign:
			findViewById(R.id.userInfoGroup).setVisibility(View.GONE);
			break;
		}

	}
	//上传图片
	private void upLoadImage(){
		if (!DeviceUtil.isOnline()) {
			Toast.makeText(this, R.string.test_network_error_share_message,
					Toast.LENGTH_LONG).show();
	
			return;
		}
		dialog = ProgressDialogUtil.show(this, true);

		Bitmap bitmap = ImageUtil.getScreenShot(container);
		if (bitmap != null) {
			int max = Math.min(maxWidthHeigth, Math.max(dis_height, dis_width));
			Bitmap photo = ImageUtil.createScaleBitmap(bitmap, max);
			if (photo != bitmap) {
				bitmap.recycle();
			}
			saveImage(photo,true);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			photo.compress(CompressFormat.PNG, 100, bos);
			byte[] data = bos.toByteArray();
			byte[] base64 = Base64.encodeBase64(data);
			try {
				String image=new String(base64,"utf-8");
				UserEntity userEntity=DBService.getUserEntity();
				ProductEntity selectedProduct=SaveSelectedProductUtil.getSelectProduct(DBService.getUserEntity().getToken());
				if(userEntity!=null||selectedProduct!=null){
					upLoadImageService.putImage(userEntity.getToken(), image,selectedProduct.getProductId());
				}
				
				
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
	}
	void popupShare() {
		if (!DeviceUtil.isOnline()) {
			Toast.makeText(this, R.string.test_network_error_share_message,
					Toast.LENGTH_LONG).show();
			return;
		}
		Bitmap bitmap =null;
		try{
			bitmap = ImageUtil.getScreenShot(container);
		}catch(OutOfMemoryError e){
			e.printStackTrace();
		}
		
		if (bitmap != null) {
			int max = Math.min(maxWidthHeigth, Math.max(dis_height, dis_width));
			Bitmap photo = ImageUtil.createScaleBitmap(bitmap, max);
			if (photo != bitmap) {
				bitmap.recycle();
			}
			
			ByteArrayOutputStream out=new ByteArrayOutputStream();
			photo.compress(CompressFormat.JPEG, 100, out);
			
			byte[] data = out.toByteArray();
			byte[] base64 = Base64.encodeBase64(data);
			
			try {
				String image=new String(base64,"utf-8");
				UserEntity userEntity=DBService.getUserEntity();
				if(userEntity!=null){
					new UpLoadSkinImageService(this, HttpTagConstantUtils.UPLOAD_SKIN_IMAGE, this).upLoad(userEntity.getToken(), TestId, image);
				}
				
				
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			
			//String fileName=saveImage(photo,false);
			Intent intent=new Intent(this, SkinResultShareActivity.class);
			Bundle bundle = new Bundle();
			bundle.putFloat("waterValues", waterValues);
			bundle.putFloat("oilValues", oilValues);
			bundle.putInt("TestId", TestId);
			bundle.putFloat("flexibleValues", flexibleValues);
			bundle.putInt("partFlag", partFlag);
			//bundle.putString("fileName", fileName);
			bundle.putByteArray("BITMAP", out.toByteArray());
			intent.putExtras(bundle);
			startActivity(intent);
			this.overridePendingTransition(
					R.anim.activity_enter_from_right,
					R.anim.activity_exit_to_left);
			
			finish();
			
			//UMShareUtil.getInstance().shareImage(this, link, photo, this);
		}
	}
	
	
	@Override
	public void onEvent(String event, Object... args) {
		if (KVOEvents.SHARE_SUCCESS.equals(event)) {
			if (!isFinishing()) {
				finish();
			}
		}
	}

	// 小图加滤镜
	private Bitmap makeFilter(Bitmap bt, IImageFilter filter) {
		try {
			if (filter != null) {
				Image img = new Image(bt);
				img = filter.process(img);
				img.copyPixelsFromBuffer();
				return img.getImage();
			} else {
				return bt;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bt;
	}

	// 大图加滤镜
	private void makFilter(final View view, final IImageFilter filter) {
		if (view.isSelected()) {
			return;
		}
		final ProgressDialog dialog = ProgressDialogUtil.show(this, true);

		new Thread(new Runnable() {
			@Override
			public void run() {
				Bitmap maxBitmap = PhotoUtils.getImageThumbnail(fileName, MyApplication.screenSize.x, MyApplication.screenSize.y - DensityUtil.dip2px(getApplicationContext(), 120));//fileUtil.getBitmapByPath(fileName);

				filterBitmap = makeFilter(maxBitmap, filter);
				if (maxBitmap != filterBitmap) {
					if (!maxBitmap.isRecycled()) {
						maxBitmap.recycle();
					}
				}

				mHandler.post(new Runnable() {
					@Override
					public void run() {
						imageView.setImageBitmap(filterBitmap);
						
						/*if (null!=filterBitmap && !filterBitmap.isRecycled()) {
							filterBitmap.recycle();
						}*/
						if (view.getId() == R.id.nativeImageBorder) {
							nativeImageBorder.setSelected(true);
							nativeImageTxt.setSelected(true);
						} else {
							if (nativeImageBorder.isSelected()) {
								nativeImageBorder.setSelected(false);
								nativeImageTxt.setSelected(false);
							}
						}

						if (view.getId() == R.id.blackWhiteImageBorder) {
							blackWhiteImageBorder.setSelected(true);
							blackWhiteImageTxt.setSelected(true);
						} else {
							if (blackWhiteImageBorder.isSelected()) {
								blackWhiteImageBorder.setSelected(false);
								blackWhiteImageTxt.setSelected(false);
							}
						}

						if (view.getId() == R.id.blurImageBorder) {
							blurImageBorder.setSelected(true);
							blurImageTxt.setSelected(true);
						} else {
							if (blurImageBorder.isSelected()) {
								blurImageBorder.setSelected(false);
								blurImageTxt.setSelected(false);
							}
						}
						if (view.getId() == R.id.lomoImageBorder) {
							lomoImageBorder.setSelected(true);
							lomoImageTxt.setSelected(true);
						} else {
							if (lomoImageBorder.isSelected()) {
								lomoImageBorder.setSelected(false);
								lomoImageTxt.setSelected(false);
							}
						}

						if (view.getId() == R.id.softGlowImageBorder) {
							softGlowImageBorder.setSelected(true);
							softGlowImageTxt.setSelected(true);
						} else {
							if (softGlowImageBorder.isSelected()) {
								softGlowImageBorder.setSelected(false);
								softGlowImageTxt.setSelected(false);
							}
						}
					}
				});
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
			}
		}).start();
	}

	private void initView() {
		Bundle bundle = getIntent().getExtras();
		fileName = bundle.getString("fileName");
		testPreDiff = bundle.getInt("testPreDiff");
		int testScore = bundle.getInt("testScore");
		int descIndex = bundle.getInt("descIndex");
		int imageIndex = bundle.getInt("imageIndex");

		shareTitle = bundle.getString("shareTitle");
		shareContent = bundle.getString("shareContent");
		shareLinkUrl = bundle.getString("shareLinkUrl");
		photoType = bundle.getInt("photoType");
		waterValues=bundle.getFloat("waterValues");
		oilValues=bundle.getFloat("oilValues");
		flexibleValues=bundle.getFloat("flexibleValues");
		partFlag=bundle.getInt("partFlag");
		TestId=bundle.getInt("TestId");

		switch (photoType) {
		case PhotoType.SKIN_PHOTO:
			
			userInfoComp = new UserInfoComp(this,
					(ViewGroup) findViewById(R.id.userInfoGroup), container);
			catonImageComp = new CartoonImageComp(this,
					(ViewGroup) findViewById(R.id.ctImageGroup), container,
					testPreDiff, imageIndex);
			catonTextComp = new CartoonTextComp(this,
					(ViewGroup) findViewById(R.id.ctTextGroup), container,
					testPreDiff, testScore, descIndex);

			findViewById(R.id.userInfoGroup).setVisibility(View.GONE);
			findViewById(R.id.ctImageGroup).setVisibility(View.GONE);
			findViewById(R.id.ctTextGroup).setVisibility(View.GONE);
			break;
		case PhotoType.FACIAL_PHOTO:
			findViewById(R.id.userInfoGroup).setVisibility(View.GONE);
			findViewById(R.id.ctImageGroup).setVisibility(View.GONE);
			findViewById(R.id.ctTextGroup).setVisibility(View.GONE);
			
			findViewById(R.id.rgSpecialEffect).setVisibility(View.GONE);
			findViewById(R.id.overlayContainer).setVisibility(View.GONE);
			findViewById(R.id.signContainer).setVisibility(View.GONE);
			findViewById(R.id.filter_container).setVisibility(View.VISIBLE);
			break;
		}

		// MarginParams catonTextCompMargin =
		// JsonUtil.deSerialize(bundle.getString("catonTextCompMargin"),
		// MarginParams.class);

		imageView = (ImageView) findViewById(R.id.imageView);
		Bitmap maxBitmap = fileUtil.getBitmapByPath(fileName);
		if(maxBitmap==null){
			finish();
			return;
		}
		ChatMsgAdapter.displayImageByUri(imageView, fileName, "");
		//imageView.setImageBitmap(maxBitmap);

		// imageView.setOnTouchListener(new CartoonContainerListener(this,
		// testPreDiff, testScore));
		// imageView.setLongClickable(true);

		// 设置宽度
		/*
		 * ViewGroup filter_container = (ViewGroup)
		 * findViewById(R.id.filter_container); int size =
		 * filter_container.getChildCount(); int width = (dis_width -
		 * DensityUtil.dip2px(this, 20)) / 4; for (int i = 0; i < size; i++) {
		 * LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)
		 * filter_container.getChildAt(i).getLayoutParams(); params.width =
		 * width; filter_container.getChildAt(i).setLayoutParams(params); }
		 */

		// 加滤镜
		nativeImage = (ImageView) findViewById(R.id.nativeImage);
		nativeImageTxt = (TextView) findViewById(R.id.nativeImageTxt);
		nativeImageBorder = findViewById(R.id.nativeImageBorder);

		final Bitmap minBitmap = ImageUtil.createScaleBitmap(maxBitmap, dp80);
		/*if(minBitmap==null){
			finish();
			return;
		}*/
		nativeImage.setImageBitmap(minBitmap);
		nativeImageBorder.setSelected(true);
		nativeImageTxt.setSelected(true);
		nativeImage.setOnClickListener(this);

		blackWhiteImage = (ImageView) findViewById(R.id.blackWhiteImage);
		blackWhiteImageTxt = (TextView) findViewById(R.id.blackWhiteImageTxt);
		blackWhiteImageBorder = findViewById(R.id.blackWhiteImageBorder);

		blurImage = (ImageView) findViewById(R.id.blurImage);
		blurImageTxt = (TextView) findViewById(R.id.blurImageTxt);
		blurImageBorder = findViewById(R.id.blurImageBorder);

		lomoImage = (ImageView) findViewById(R.id.lomoImage);
		lomoImageTxt = (TextView) findViewById(R.id.lomoImageTxt);
		lomoImageBorder = findViewById(R.id.lomoImageBorder);

		softGlowImage = (ImageView) findViewById(R.id.softGlowImage);
		softGlowImageTxt = (TextView) findViewById(R.id.softGlowImageTxt);
		softGlowImageBorder = findViewById(R.id.softGlowImageBorder);
		if(null != maxBitmap){
			maxBitmap.recycle();
		}
		final ProgressDialog dialog = ProgressDialogUtil.show(this, true);
		new Thread(new Runnable() {
			@Override
			public void run() {

				blackWhite = makeFilter(minBitmap,
						new FilmFilter());
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						blackWhiteImage.setImageBitmap(blackWhite);
						blackWhiteImage
								.setOnClickListener(PhotoShareActivity2.this);
					}
				});

				blur = makeFilter(minBitmap, new SaturationModifyFilter());
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						blurImage.setImageBitmap(blur);
						blurImage.setOnClickListener(PhotoShareActivity2.this);
					}
				});

				lomo = makeFilter(minBitmap, new NewLomoFilter());
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						lomoImage.setImageBitmap(lomo);
						lomoImage.setOnClickListener(PhotoShareActivity2.this);
					}
				});

				softGlow = makeFilter(minBitmap, new BrightContrastFilter());
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						softGlowImage.setImageBitmap(softGlow);
						softGlowImage
								.setOnClickListener(PhotoShareActivity2.this);
					}
				});

				if (dialog.isShowing()) {
					dialog.dismiss();
				}
			}
		}).start();
	}
	
	@Override
	public void onSuccess() {
		MyApplication.getInstance().getKvo().fire(KVOEvents.SHARE_SUCCESS, -1);

		Intent intent = new Intent(this, SkinRunMainActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.activity_enter_from_right,
				R.anim.activity_exit_to_left);
	}

	
	//保存图片
	private String saveImage(Bitmap photo,boolean isFacialmask){
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		try {
			String imageName=format.format(new Date())+"_fliter.png";
			String filePath=fileUtil.savaBitmap(imageName, photo, 100);
			if(isFacialmask){
				ProductEntity selectedProduct=SaveSelectedProductUtil.getSelectProduct(DBService.getUserEntity().getToken());
				ProductImageEntity imageEntity=new ProductImageEntity();
				imageEntity.setImageName(filePath);
				if(selectedProduct!=null){
					imageEntity.setProductId(selectedProduct.getProductId()+"");
					ImageNameSaverUtil.save(imageEntity);
				}
			}
			return filePath;
			
		} catch (IOException e) {
			e.printStackTrace();
			return "";		
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
	public void finish() {
		super.finish();
		if(photoType!=PhotoType.SKIN_PHOTO){
			overridePendingTransition(R.anim.activity_enter_from_left,
					R.anim.activity_exit_to_right);
		}
	
	}

	@Override
	public void dataCallBack(Object tag, int statusCode, Object result) {
		switch((Integer)tag){
		case  HttpTagConstantUtils.UPLOAD_IMAGE:
			
			if(dialog.isShowing()&&dialog!=null){
				dialog.dismiss();
			}
			
			if(statusCode==200){
				AppToast.toastMsgCenter(this, "图片上传成功").show();
				Log.e("photoShare", "=============上传图片返回的链接："+result.toString());
			}else{
				//AppToast.toastMsgCenter(this, "图片上传失败").show();
				Log.e("photoShare", "=============上传图片返回的链接："+result.toString());
			}
			finish();
			break;
		case HttpTagConstantUtils.UPLOAD_SKIN_IMAGE:
			Log.e("photoShare", "=============上传肌肤图片返回的链接："+result.toString());
			break;
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup arg0, int id) {
		switch(id){
		case R.id.radioSign:
			signContainer.setVisibility(View.VISIBLE);
			filter_container.setVisibility(View.GONE);
			overlayContainer.setVisibility(View.GONE);
			break;
		case R.id.radioOverlay:
			signContainer.setVisibility(View.GONE);
			filter_container.setVisibility(View.GONE);
			overlayContainer.setVisibility(View.VISIBLE);
			break;
		case R.id.radioFilter:
			signContainer.setVisibility(View.GONE);
			filter_container.setVisibility(View.VISIBLE);
			overlayContainer.setVisibility(View.GONE);
			
			break;
		}
	}
}

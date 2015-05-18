package com.skinrun.trunk.facial.mask.test;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.app.base.entity.FacialTestEntity;
import com.app.base.entity.ProductEntity;
import com.app.base.entity.UserEntity;
import com.app.base.init.MyApplication;
import com.app.service.GetFacialmaskBrandService;
import com.avos.avoscloud.LogUtil.log;
import com.base.app.utils.DBService;
import com.base.app.utils.FileUtil;
import com.base.dialog.lib.Effectstype;
import com.base.dialog.lib.NiftyDialogBuilder;
import com.base.service.action.constant.HttpTagConstantUtils;
import com.base.service.impl.HttpAysnResultInterface;
import com.beabox.hjy.tt.R;
import com.beabox.hjy.tt.TakePhotoActivity;
import com.beabox.hjy.tt.TestInstructActivity;

public class FacialmaskEntryActivity extends Activity implements
		OnClickListener, TextWatcher, OnItemClickListener, HttpAysnResultInterface{

	private EditText facialBrand;
	private TextView facialName;
	private ListView lvProductHint;
	private GetFacialmaskBrandService getFacialmaskBrandService;
	private ProductAdapter mAdapter;
	private ArrayList<ProductEntity> products;
	private ProductEntity selectedProduct;
	private View upline,downLine;
	private String TAG="FacialmaskEntryActivity";
	private ImageView UpLoadImage,ivTakedPhoto;
	private FileUtil fileUtil;
	
	private boolean compelExit=false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		MyApplication.getInstance().addActivity(this);
		super.onCreate(savedInstanceState);


		setContentView(R.layout.facialmaskentrylayout);
		initView();
		fileUtil=new FileUtil(this);
		new Handler().postDelayed(run, 1000);
	}

	private void initView() {
		findViewById(R.id.backBtnFacialMask).setOnClickListener(this);
		findViewById(R.id.hintFacialMask).setOnClickListener(this);
		UpLoadImage=(ImageView) findViewById(R.id.imageViewUpLoadImage);
		ivTakedPhoto=(ImageView)findViewById(R.id.ivTakedPhoto);
		UpLoadImage.setOnClickListener(this);
		findViewById(R.id.buttonStartFacialTest).setOnClickListener(this);

		facialBrand = (EditText) findViewById(R.id.facialMaskBrand);
		facialName = (TextView) findViewById(R.id.facialMaskName);
		lvProductHint=(ListView)findViewById(R.id.lvProductHint);
		upline=findViewById(R.id.upline);
		downLine=findViewById(R.id.downline);
		
		facialBrand.addTextChangedListener(this);
		lvProductHint.setOnItemClickListener(this);
		findViewById(R.id.ll_outSide).setOnClickListener(this);
		
		try{
			selectedProduct=SaveSelectedProductUtil.getSelectProduct(DBService.getUserEntity().getToken());
			if(selectedProduct!=null){
				Log.e("FacialmaskEntryActivity", "===========存储的产品名称："+selectedProduct.getPro_brand_name());
				facialName.setText(selectedProduct.getPro_brand_name());
				facialBrand.setText(selectedProduct.getPro_brand_name());
			}
			
		}catch(Exception e){
			
		}
		
		
		mAdapter=new ProductAdapter(null, this);
		lvProductHint.setAdapter(mAdapter);
		getFacialmaskBrandService=new GetFacialmaskBrandService(this, HttpTagConstantUtils.PRODUCT_NAME, this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	
	
	
	Runnable run=new Runnable() {
		
		@Override
		public void run() {
			JudgeIsTesting();
		}
	};
	private int reply=0;
	private void JudgeIsTesting(){
		try{
			UserEntity userEntity=DBService.getUserEntity();
			if(userEntity!=null){
				FacialTestEntity facialTestEntity=SaveFacialResultUtil.getRecord(userEntity.getToken());
				if(facialTestEntity!=null){
					if(facialTestEntity.getWater1()==0){
						return;
					}
					
					long d=System.currentTimeMillis()-Long.parseLong(facialTestEntity.getTime1());
					Log.e(TAG, "============距离上次测试时间间隔："+d);
					
					if(d<=FacialDuration.YanShiBuShui){
						final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(FacialmaskEntryActivity.this);

						dialogBuilder.withTitle(null).withMessage(null).withMessageColor("#FFFFFFFF")
								.isCancelableOnTouchOutside(false).withDuration(200).withEffect(Effectstype.Fadein)
								.withButton1Text("继续").withButton2Text("重新测试")
								.setCustomView(R.layout.is_continue_test_facialmask,FacialmaskEntryActivity.this)
								.setButton1Click(new OnClickListener() {
									@Override
									public void onClick(View arg0) {
										UserEntity userEntity=DBService.getUserEntity();
										if(userEntity!=null){
											selectedProduct=SaveSelectedProductUtil.getSelectProduct(userEntity.getToken());
											Intent intent=new Intent(FacialmaskEntryActivity.this, FacialmaskTestActivity.class);
											intent.putExtra("REPLY", 0);
											
											startActivity(intent);
											
											overridePendingTransition(R.anim.activity_enter_from_right,
													R.anim.activity_exit_to_left);
											compelExit=true;
											finish();
										}
										
										
										dialogBuilder.dismiss();
									}
								}).setButton2Click(new OnClickListener() {
									
									@Override
									public void onClick(View arg0) {
										
										UserEntity userEntity=DBService.getUserEntity();
										if(userEntity!=null){
											SaveFacialResultUtil.delete(userEntity.getToken());
										}
										reply=1;
										
										dialogBuilder.dismiss();
									}
								}).show();
					}else{
						UserEntity u=DBService.getUserEntity();
						if(userEntity!=null){
							SaveFacialResultUtil.delete(u.getToken());
							reply=1;
						}
					}
				}
			}else{
				reply=1;
			}
		}catch(Exception e){
			
		}
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		try{
			if(selectedProduct!=null){
				String imageName=ImageNameSaverUtil.getImageName(selectedProduct.getProductId()+"");
				log.e(TAG, "==========selectedProduct:"+selectedProduct.getProductId()+"");
				log.e(TAG, "==========imageName:"+imageName);
				Bitmap bitmap=fileUtil.getBitmapByPath(imageName);
				if(bitmap!=null){
					ivTakedPhoto.setVisibility(View.VISIBLE);
					ivTakedPhoto.setImageBitmap(bitmap);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.backBtnFacialMask:
			finish();
			break;
		case R.id.hintFacialMask:
			Intent intent=new Intent(this, TestInstructActivity.class);
			intent.putExtra("CONTENT_TYPE", 2);
			startActivity(intent);
			overridePendingTransition(R.anim.activity_enter_from_right,
					R.anim.activity_exit_to_left);
			//finish();
			break;
		case R.id.imageViewUpLoadImage:
			if(selectedProduct==null){
				final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(FacialmaskEntryActivity.this);

				dialogBuilder.withTitle(null).withMessage(null).withMessageColor("#FFFFFFFF")
						.isCancelableOnTouchOutside(false).withDuration(200).withEffect(Effectstype.Fadein)
						.withButton1Text("我知道了").setCustomView(R.layout.no_select_product,FacialmaskEntryActivity.this)
						.setButton1Click(new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								dialogBuilder.dismiss();
							}
						}).show();
			}else{
				try{
					Intent i = new Intent(this, TakePhotoActivity.class);
					Bundle b = new Bundle();
					b.putInt("photoType", PhotoType.FACIAL_PHOTO);
					i.putExtras(b);
					startActivity(i);
					overridePendingTransition(R.anim.activity_enter_from_right,
							R.anim.activity_exit_to_left);
					
				}catch(Exception e){
					e.printStackTrace();
				}
				
			}
			
			break;
		case R.id.ll_outSide:
			lvProductHint.setVisibility(View.GONE);
			upline.setVisibility(View.GONE);
			downLine.setVisibility(View.GONE);
			break;
		case R.id.buttonStartFacialTest:
			
			
			if(selectedProduct==null){
				final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(FacialmaskEntryActivity.this);

				dialogBuilder.withTitle(null).withMessage(null).withMessageColor("#FFFFFFFF")
						.isCancelableOnTouchOutside(false).withDuration(200).withEffect(Effectstype.Fadein)
						.withButton1Text("我知道了").setCustomView(R.layout.no_select_product,FacialmaskEntryActivity.this)
						.setButton1Click(new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								dialogBuilder.dismiss();
							}
						}).show();
				
				
			}else{
				Intent in = new Intent(this, FacialmaskTestActivity.class);
				in.putExtra("REPLY", reply);
				startActivity(in);
				
				overridePendingTransition(R.anim.activity_enter_from_right,
						R.anim.activity_exit_to_left);
				compelExit=true;
				finish();
			}
			
			
			break;

		}
	}

	@Override
	public void finish() {
		super.finish();
		if(!compelExit){
			overridePendingTransition(R.anim.activity_enter_from_left,
					R.anim.activity_exit_to_right);
		}
	}
	
	private void search(){
		String content=facialBrand.getText().toString();
		UserEntity userEntity=DBService.getUserEntity();
		if(userEntity!=null){
			getFacialmaskBrandService.getProductName(userEntity.getToken(), content);
		}
		
		
		lvProductHint.setVisibility(View.VISIBLE);
		upline.setVisibility(View.VISIBLE);
		downLine.setVisibility(View.VISIBLE);
	}
	
	
	@Override
	public void afterTextChanged(Editable arg0) {
		search();
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		
	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
		try{
			selectedProduct=products.get(index);
			if(selectedProduct!=null){
				facialName.setText(selectedProduct.getPro_brand_name());
				facialBrand.setText(selectedProduct.getPro_brand_name());
				UserEntity user=DBService.getUserEntity();
				if(user!=null){
					selectedProduct.setToken(user.getToken());
					SaveSelectedProductUtil.save(selectedProduct);
				}
				
				
			}
			lvProductHint.setVisibility(View.GONE);
			upline.setVisibility(View.GONE);
			downLine.setVisibility(View.GONE);
		}catch(Exception e){
			
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void dataCallBack(Object tag, int statusCode, Object result) {
		try{
			products=(ArrayList<ProductEntity>) result;
			if(products!=null&&products.size()>0){
				mAdapter.setProducts(products);
				mAdapter.notifyDataSetChanged();
			}else{
				mAdapter.setProducts(null);
				mAdapter.notifyDataSetChanged();
			}
			ListViewHeightSetter.setListViewHeight(lvProductHint);
		}catch(Exception e){
			
		}
	}
}

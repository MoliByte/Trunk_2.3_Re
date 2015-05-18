package com.beabox.hjy.tt;

import com.app.base.entity.MerchantDetailEntity;
import com.app.base.entity.UserEntity;
import com.app.service.CancleAttentionMerchantService;
import com.app.service.GetMerchantDetailService;
import com.avoscloud.chat.service.UserService;
import com.avoscloud.chat.util.PhotoUtils;
import com.base.app.utils.DBService;
import com.base.service.action.constant.HttpTagConstantUtils;
import com.base.service.impl.HttpAysnResultInterface;
import com.umeng.message.PushAgent;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MerchantDetailActivity extends Activity implements OnClickListener, HttpAysnResultInterface{
	private ImageView ivMerchantIcon;
	private TextView tvMerchantRemark,tvMerchantPhone,tvMerchantAderess,tvMerchantName;
	private String merchant_id;
	private Dialog menu;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PushAgent.getInstance(this).onAppStart();
		merchant_id=getIntent().getStringExtra("merchant_id");
		
		setContentView(R.layout.merchant_detail_activity);
		ivMerchantIcon=(ImageView)findViewById(R.id.ivMerchantIcon);
		tvMerchantRemark=(TextView)findViewById(R.id.tvMerchantRemark);
		tvMerchantPhone=(TextView)findViewById(R.id.tvMerchantPhone);
		tvMerchantAderess=(TextView)findViewById(R.id.tvMerchantAderess);
		tvMerchantName=(TextView)findViewById(R.id.tvMerchantName);
		findViewById(R.id.backBtnMerchantDetail).setOnClickListener(this);
		findViewById(R.id.much_menu).setOnClickListener(this);
		
		loadData();
	}
	
	private void loadData(){
		UserEntity u=DBService.getUserEntity();
		if(u!=null){
			int merchantDetail=Integer.parseInt(merchant_id);
			new GetMerchantDetailService(this, HttpTagConstantUtils.GET_MERCHANT_DETAIL, this).doGetDetail(u.getToken(), merchantDetail);
		}
	}
	
	private void menuShow() {
		try {
			View view = View.inflate(this, R.layout.like_actionbar_menu, null);
			Button btn_cancle;
			Button btn_cancle_attention;
			
			btn_cancle = (Button) view.findViewById(R.id.btn_cancle);
			btn_cancle_attention = (Button) view.findViewById(R.id.btn_cancle_attention);
			btn_cancle.setOnClickListener(this);
			btn_cancle_attention.setOnClickListener(this);
			
			menu = new Dialog(this);
			
			menu.requestWindowFeature(Window.FEATURE_NO_TITLE);
			menu.setContentView(view);
			Window regionWindow = menu.getWindow();
			regionWindow.setGravity(Gravity.BOTTOM);
			regionWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			regionWindow.setWindowAnimations(R.style.view_animation);
			ColorDrawable dw = new ColorDrawable(Color.TRANSPARENT);
			regionWindow.setBackgroundDrawable(dw);
			menu.setCanceledOnTouchOutside(false);
		
			menu.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.backBtnMerchantDetail:
			finish();
			break;
		case R.id.much_menu:
			menuShow();
			break;
		case R.id.btn_cancle:
			if(menu!=null&&menu.isShowing()){
				menu.dismiss();
			}
			break;
		case R.id.btn_cancle_attention:
			UserEntity u=DBService.getUserEntity();
			int id=Integer.parseInt(merchant_id);
			if(u!=null){
				Log.e("MerchantFrag", "===========长按事件");	
				new CancleAttentionMerchantService(this, HttpTagConstantUtils.CANCLE_ATTENTION_MERCHANT, this).
					cancle(u.getToken(),id);
			}
			if(menu!=null&&menu.isShowing()){
				menu.dismiss();
			}
			break;
		}
	}
	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.activity_enter_from_left, R.anim.activity_exit_to_right);
	}

	@Override
	public void dataCallBack(Object tag, int statusCode, Object result) {
		switch((Integer)tag){
		case HttpTagConstantUtils.CANCLE_ATTENTION_MERCHANT:
			if(statusCode==200||statusCode==201){
				finish();
			}
			
			break;
		case HttpTagConstantUtils.GET_MERCHANT_DETAIL:
			MerchantDetailEntity md=(MerchantDetailEntity)result;
			if(md!=null){
				
				tvMerchantRemark.setText(md.getRemark()+"");
				tvMerchantPhone.setText("联系电话："+md.getContact_phone());
				if(md.getAddress()==null||md.getAddress().equals("null")){
					tvMerchantAderess.setText("地址：");
				}else{
					tvMerchantAderess.setText("地址："+md.getAddress());
				}
				
				
				tvMerchantName.setText(""+md.getCompany_name());
				if(md.getMerchant_img()!=null&&!md.getMerchant_img().equals("")){
					UserService.imageLoader.displayImage(""+md.getMerchant_img(), ivMerchantIcon,PhotoUtils.articleImageOptions);
				}
			}
			break;
		}
	}
}

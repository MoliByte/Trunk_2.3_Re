package com.beabox.hjy.tt;

import com.app.base.entity.MyLovedMerchantEntity;
import com.app.base.entity.UserEntity;
import com.app.base.init.MyApplication;
import com.app.service.CancleAttentionMerchantService;
import com.base.app.utils.DBService;
import com.base.service.action.constant.HttpTagConstantUtils;
import com.base.service.impl.HttpAysnResultInterface;
import com.base.supertoasts.util.AppToast;
import com.skinrun.trunk.main.BrandFrag;
import com.skinrun.trunk.main.MerchantFrag;
import com.umeng.message.PushAgent;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ProducerActivity extends FragmentActivity implements OnClickListener, HttpAysnResultInterface{
	private TextView tvProducerName;
	private MyLovedMerchantEntity entity;
	private int merchant_id;
	private Dialog menu;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(this);
    	PushAgent.getInstance(this).onAppStart();
		entity=(MyLovedMerchantEntity) getIntent().getSerializableExtra("MyLovedMerchantEntity");
		try{
			merchant_id=Integer.parseInt(entity.getId());
		}catch(NumberFormatException e){
			e.printStackTrace();
		}
		
		
		setContentView(R.layout.producer_activity);
		
		Bundle bundle=new Bundle();
		bundle.putInt("merchant_id", merchant_id);
		BrandFrag bf=BrandFrag.getInstance(bundle);
		getSupportFragmentManager().beginTransaction().add(R.id.brand_frag_container, bf).show(bf).commit();
		
		findViewById(R.id.ivProducerBack).setOnClickListener(this);
		findViewById(R.id.much_menu).setOnClickListener(this);
		
		tvProducerName=(TextView)findViewById(R.id.tvProducerName);
		tvProducerName.setText(" "+entity.getName());
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.ivProducerBack:
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
			if(u!=null){
				Log.e("MerchantFrag", "===========长按事件");	
				new CancleAttentionMerchantService(this, HttpTagConstantUtils.CANCLE_ATTENTION_MERCHANT, this).
					cancle(u.getToken(),merchant_id);
			}
			if(menu!=null&&menu.isShowing()){
				menu.dismiss();
			}
			
			break;
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
	public void dataCallBack(Object tag, int statusCode, Object result) {
		switch((Integer)tag){
		case HttpTagConstantUtils.CANCLE_ATTENTION_MERCHANT:
			if(statusCode==200||statusCode==201){
				AppToast.toastMsg(this, "取消关注成功");
				finish();
				
			}
			
			break;
		}
	}
	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.activity_enter_from_left, R.anim.activity_exit_to_right);
	}
}

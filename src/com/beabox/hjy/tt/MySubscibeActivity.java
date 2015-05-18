package com.beabox.hjy.tt;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.base.app.utils.MySubscribeUtil;
import com.skinrun.trunk.main.BrandFrag;
import com.skinrun.trunk.main.MerchantFrag;
import com.umeng.message.PushAgent;



//已经废弃
public class MySubscibeActivity extends FragmentActivity implements OnClickListener, OnCheckedChangeListener {
	private RadioGroup radioGroupSubscribe;
	private View linegray1,linered2,linered1,linegray2;
	private ArrayList<Fragment> frags;
	
	private int currentIndex=0;
	FragmentTransaction ft  ;
	FragmentManager manager ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PushAgent.getInstance(this).onAppStart();
		setContentView(R.layout.my_subscribe_activity);
		manager = getSupportFragmentManager() ;
		initView();
		addListener();
	}
	
	
	private void initView(){
		radioGroupSubscribe=(RadioGroup)findViewById(R.id.radioGroupSubscribe);
		linegray1=findViewById(R.id.linegray1);
		linered1=findViewById(R.id.linered1);
		linegray2=findViewById(R.id.linegray2);
		linered2=findViewById(R.id.linered2);
		
		frags=new ArrayList<Fragment>();
		
		Bundle bundle1=new Bundle();
		bundle1.putString("TYPE",MySubscribeUtil.producer);
		frags.add(MerchantFrag.getInstance(bundle1));
		
		
		Bundle bundle2=new Bundle();
		bundle2.putString("TYPE",MySubscribeUtil.beautyParlor);
		frags.add(MerchantFrag.getInstance(bundle2));
		
		
		ft = manager.beginTransaction();
		ft.add(R.id.subcribeContainer1,frags.get(0)).add(R.id.subcribeContainer1, frags.get(1)).show(frags.get(0)).hide(frags.get(1)).commit();
	}
	
	private void addListener(){
		findViewById(R.id.backBtn).setOnClickListener(this);
		radioGroupSubscribe.setOnCheckedChangeListener(this);
	}


	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.backBtn:
			finish();
			break;
		}
	}
	
	
	@Override
	public void onCheckedChanged(RadioGroup arg0, int id) {
		switch(id){
		case R.id.radioBrand:
			gotoFrag(0);
			linegray1.setVisibility(View.INVISIBLE);
			linered1.setVisibility(View.VISIBLE);
			linered2.setVisibility(View.INVISIBLE);
			linegray2.setVisibility(View.VISIBLE);
			break;
		case R.id.radioMerchant:
			gotoFrag(1);
			linegray1.setVisibility(View.VISIBLE);
			linered1.setVisibility(View.INVISIBLE);
			linered2.setVisibility(View.VISIBLE);
			linegray2.setVisibility(View.INVISIBLE);
			break;
		}
	}
	private void gotoFrag(int index){
		ft = getSupportFragmentManager().beginTransaction();
		ft.hide(frags.get(currentIndex));
		if(!frags.get(index).isAdded()){
			ft.add(R.id.subcribeContainer1, frags.get(index));
		}
		ft.show(frags.get(index)).commit();
		currentIndex=index;
	}
	
	
	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.activity_enter_from_right, R.anim.activity_exit_to_left);
	}
}

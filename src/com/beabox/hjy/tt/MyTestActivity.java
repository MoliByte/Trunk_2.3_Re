package com.beabox.hjy.tt;


import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.app.base.init.MyApplication;
import com.app.custom.view.HomeViewPager;
import com.base.app.utils.HomeTag;
import com.base.app.utils.MySubscribeUtil;
import com.skinrun.trunk.adapter.HomeViewPagerAdapter;
import com.skinrun.trunk.main.MerchantFrag;
import com.skinrun.trunk.main.MyTestFrag;
import com.umeng.message.PushAgent;

public class MyTestActivity extends FragmentActivity implements OnCheckedChangeListener, OnClickListener {
	
	private RadioGroup radioGroup;
	private HomeViewPager mViewPager;
	private View linegray1,linered2,linered1,linegray2;
	private TextView title_name;
	private RadioButton radioSkin,radioMask;
	
	private int flag=HomeTag.MY_TEST;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		MyApplication.getInstance().addActivity(this);
		PushAgent.getInstance(this).onAppStart();
		super.onCreate(savedInstanceState);
		flag=getIntent().getIntExtra("FLAG", HomeTag.MY_TEST);
		
		
		setContentView(R.layout.my_test_activity);
		
		radioGroup=(RadioGroup)findViewById(R.id.radioGroupTest);
		mViewPager=(HomeViewPager)findViewById(R.id.viewPageMyTest);
		title_name=(TextView)findViewById(R.id.title_name);
		radioSkin=(RadioButton)findViewById(R.id.radioSkin);
		radioMask=(RadioButton)findViewById(R.id.radioMask);
		
		findViewById(R.id.backBtn).setOnClickListener(this);
		mViewPager.setNoScroll(true);
		ArrayList<Fragment> frags=new ArrayList<Fragment>();
		
		switch(flag){
		case HomeTag.MY_TEST:
			
			title_name.setText("我的测试");
			radioSkin.setText("肌肤测试");
			radioMask.setText("面膜测试");
			
			Bundle b=new Bundle();
			b.putString("TEST_TYPE", HomeTag.SKIN_TEST);
			frags.add(MyTestFrag.getInstance(b));

			Bundle bundle=new Bundle();
			bundle.putString("TEST_TYPE", HomeTag.FACE_MARK);
			frags.add(MyTestFrag.getInstance(bundle));
			
			break;
		case HomeTag.MY_SUBSCRIB:
			title_name.setText("我的订阅");
			radioSkin.setText("品牌");
			radioMask.setText("美容院");
			
			Bundle b1=new Bundle();
			b1.putString("TYPE",MySubscribeUtil.producer);
			frags.add(MerchantFrag.getInstance(b1));
			
			Bundle b2=new Bundle();
			b2.putString("TYPE", MySubscribeUtil.beautyParlor);
			frags.add(MerchantFrag.getInstance(b2));
			
			break;
		}
		
		linegray1=findViewById(R.id.linegray1);
		linered1=findViewById(R.id.linered1);
		linegray2=findViewById(R.id.linegray2);
		linered2=findViewById(R.id.linered2);
		
		
		HomeViewPagerAdapter mAdapter=new HomeViewPagerAdapter(this.getSupportFragmentManager(), frags);
		mViewPager.setAdapter(mAdapter);
		radioGroup.setOnCheckedChangeListener(this);
	}
	
	
	@Override
	public void onCheckedChanged(RadioGroup arg0, int id) {
		switch(id){
		case R.id.radioSkin:
			mViewPager.setCurrentItem(0);
			
			linegray1.setVisibility(View.INVISIBLE);
			linered1.setVisibility(View.VISIBLE);
			
			linered2.setVisibility(View.INVISIBLE);
			linegray2.setVisibility(View.VISIBLE);
			
			break;
		case R.id.radioMask:
			mViewPager.setCurrentItem(1);
			
			linegray1.setVisibility(View.VISIBLE);
			linered1.setVisibility(View.INVISIBLE);
			
			linered2.setVisibility(View.VISIBLE);
			linegray2.setVisibility(View.INVISIBLE);
			
			break;
		}
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
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.activity_enter_from_left, R.anim.activity_exit_to_right);
	}
}

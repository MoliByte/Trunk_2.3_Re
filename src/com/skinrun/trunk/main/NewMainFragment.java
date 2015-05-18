package com.skinrun.trunk.main;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.app.custom.view.HomeViewPager;
import com.base.app.utils.StringUtil;
import com.beabox.hjy.tt.R;
import com.skinrun.trunk.adapter.HomeViewPagerAdapter;
import com.tencent.stat.StatService;
import com.umeng.analytics.MobclickAgent;

public class NewMainFragment extends Fragment implements OnCheckedChangeListener {
	
	private RadioGroup radioGroup;
	private HomeViewPager mViewPager;
	private String TAG="NewMainFragment";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view=inflater.inflate(R.layout.new_main_fragment_layout, null);
		radioGroup=(RadioGroup)view.findViewById(R.id.radioGroupTitle);
		mViewPager=(HomeViewPager)view.findViewById(R.id.homeViewPagerMain);
		mViewPager.setNoScroll(true);
		ArrayList<Fragment> frags=new ArrayList<Fragment>();
		frags.add(new SelectedFrag());
		frags.add(new DongTaiFrag());
		HomeViewPagerAdapter mAdapter=new HomeViewPagerAdapter(getActivity().getSupportFragmentManager(), frags);
		
		mViewPager.setAdapter(mAdapter);
		
		radioGroup.setOnCheckedChangeListener(this);
		return view;
	}

	@Override
	public void onCheckedChanged(RadioGroup arg0, int arg1) {
		switch(arg1){
		case R.id.radioSelect:
			mViewPager.setCurrentItem(0);
			break;
		case R.id.radioDongTai:
			mViewPager.setCurrentItem(1);
			break;
		}
	}
	

    @Override
    public void onPause() {
        super.onPause();
        //页面结束
        if (!StringUtil.isEmpty(getActivityName())) {
            MobclickAgent.onPageEnd(getActivityName()); // 保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息
            MobclickAgent.onPause(getActivity());
            StatService.onPause(getActivity());
        }
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	StatService.onResume(getActivity());
    }
    
    public String getActivityName(){
		return "肌肤管家" ;
	}
}

package com.beabox.hjy.tt;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RadioGroup;

import com.app.base.init.BaseFragmentActivity;
import com.app.base.init.MyApplication;
import com.skinrun.trunk.adapter.FragAdapter;
import com.skinrun.trunk.main.MasterArticleFrag;
import com.skinrun.trunk.main.MasterIntroduceFrag;
import com.umeng.message.PushAgent;
import com.viewpagerindicator.UnderlinePageIndicator;

public class MasterInfoActivity extends BaseFragmentActivity {
	private static final String[] CONTENT = new String[] { "简介", "文章" };
	private FragAdapter fragAdapter;
	private ViewPager pager;
	private RadioGroup radioGroup;
	private List<Fragment> list  ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		MyApplication.getInstance().addActivity(this);
		PushAgent.getInstance(this).onAppStart();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_master_info);
		list = new ArrayList<Fragment>();
		list.add(MasterIntroduceFrag.newInstance(CONTENT[0 % CONTENT.length]));
		list.add(MasterArticleFrag.newInstance(CONTENT[1 % CONTENT.length]));
		FragmentPagerAdapter adapter = new GoogleMusicAdapter(
				getSupportFragmentManager());

		ViewPager pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(adapter);
		UnderlinePageIndicator indicator = (UnderlinePageIndicator) findViewById(R.id.indicator);
		indicator.setViewPager(pager);
		indicator.setFades(false);//下划线不消失
		adapter.notifyDataSetChanged();
		
	}

	class GoogleMusicAdapter<FragmentManager> extends FragmentPagerAdapter {
		

		public GoogleMusicAdapter(android.support.v4.app.FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			
			return list.get(position);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return CONTENT[position % CONTENT.length].toUpperCase();
		}

		@Override
		public int getCount() {
			return CONTENT.length;
		}
	}

	@Override
	public void onClick(View v) {
		
	}

	@Override
	public void setupView() {
		// TODO Auto-generated method stub

	}

	@Override
	public void addListener() {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendMessageHandler(int messageCode) {
		// TODO Auto-generated method stub

	}
	
	@Override
    protected String getActivityName() {
        return "我的美容老师信息页面";
    }

}

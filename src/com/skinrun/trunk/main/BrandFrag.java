package com.skinrun.trunk.main;

import java.util.ArrayList;
import java.util.Locale;

import com.app.base.entity.BrandEntity;
import com.app.base.entity.UserEntity;
import com.app.service.GetBrandProductService;
import com.base.app.utils.DBService;
import com.base.app.utils.DensityUtil;
import com.base.service.action.constant.HttpTagConstantUtils;
import com.base.service.impl.HttpAysnResultInterface;
import com.beabox.hjy.tt.R;
import com.skinrun.trunk.adapter.HomeViewPagerAdapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class BrandFrag extends Fragment implements HttpAysnResultInterface, OnCheckedChangeListener, OnPageChangeListener {
	
	private RadioGroup radioGroupBrand;
	private View viewLine;
	private ArrayList<BrandEntity> brands=new ArrayList<BrandEntity>();
	private ViewPager viewPageProduct;
	private int bPageIndex=1,bPageSzie=10;
	private String TAG="BrandFrag";
	
	private int IdNum=0x123;
	
	private View view;
	private LayoutParams params;
	private HorizontalScrollView horizontalScrollView;
	
	private int merchant_id;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view=inflater.inflate(R.layout.brand_frag_layout, null);
		horizontalScrollView=(HorizontalScrollView)view.findViewById(R.id.horizontalScrollView);
		
		radioGroupBrand=(RadioGroup)view.findViewById(R.id.radioGroupBrand);
		radioGroupBrand.setOnCheckedChangeListener(this);
		viewLine=view.findViewById(R.id.viewLine);
		viewPageProduct=(ViewPager)view.findViewById(R.id.viewPageProduct);
		viewPageProduct.setOnPageChangeListener(this);
		
		
		params=(LayoutParams) viewLine.getLayoutParams();
		loadBrand();
		return view;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle=getArguments();
		merchant_id=bundle.getInt("merchant_id");
	}
	
	public static BrandFrag getInstance(Bundle bundle){
		BrandFrag b=new BrandFrag();
		b.setArguments(bundle);
		return b;
	}
	
	
	private void loadBrand(){
		new GetBrandProductService(getActivity(), HttpTagConstantUtils.GET_MY_LOVED_BRAND, this).doGet(getToken(),merchant_id, 0, bPageIndex, bPageSzie);
	}
	
	private String getToken(){
		UserEntity user=DBService.getUserEntity();
		if(user!=null){
			return user.getToken();
		}
		return "";
	}

	@Override
	public void dataCallBack(Object tag, int statusCode, Object result) {
		switch((Integer)tag){
		case HttpTagConstantUtils.GET_MY_LOVED_BRAND:
			if(statusCode==200||statusCode==201){
				Log.e(TAG, "==========请求品牌成功");
				@SuppressWarnings("unchecked")
				ArrayList<BrandEntity> tem=(ArrayList<BrandEntity>) result;
				if(tem!=null&&tem.size()>0){
					if(bPageIndex==1){
						brands.clear();
					}
					viewLine.setVisibility(View.VISIBLE);
					brands.addAll(tem);
					
					ArrayList<Fragment> frags=new ArrayList<Fragment>();
					
					for(int i=0;i<brands.size();i++){
						Log.e(TAG, "===============品牌名称："+brands.get(i).getBrand_name());
						RadioButton rbTem=new RadioButton(getActivity());
						rbTem.setButtonDrawable(android.R.color.transparent);
						String brandName=brands.get(i).getBrand_name();
						if(brandName.length()>10){
							brandName=brandName.substring(0, 10);
						}
						
						rbTem.setText(brandName);
						rbTem.setTag(i);
						
						rbTem.setBackgroundResource(android.R.color.transparent);
						rbTem.setId(0x123+i);
						rbTem.setTextColor(getResources().getColor(R.color.brand_unselect));
						if(i==0){
							rbTem.setChecked(true);
							rbTem.setTextColor(getResources().getColor(R.color.brand_selected));
						}
						rbTem.setGravity(Gravity.CENTER);
						
						
						radioGroupBrand.addView(rbTem, DensityUtil.dip2px(getActivity(), 150), LinearLayout.LayoutParams.WRAP_CONTENT);
						
						Bundle bundle=new Bundle();
						bundle.putInt("brand_Id", brands.get(i).getBrand_id());
						bundle.putInt("merchant_id", merchant_id);
						
						ProductFragment pf=ProductFragment.getInstance(bundle);
						frags.add(pf);
					}
					
					HomeViewPagerAdapter adapter=new HomeViewPagerAdapter(getFragmentManager(), frags);
					viewPageProduct.setAdapter(adapter);
				}
			}
			break;
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		int id=checkedId-IdNum;
		viewPageProduct.setCurrentItem(id);
		
		int childs=group.getChildCount();
		for(int i=0;i<childs;i++){
			RadioButton r=(RadioButton) group.getChildAt(i);
			if(i==id){
				r.setTextColor(getResources().getColor(R.color.brand_selected));
			}else{
				r.setTextColor(getResources().getColor(R.color.brand_unselect));
			}
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		if (arg0 == 0) {
			params.setMargins((int) (params.width * arg1), 0, 0, 0);// 第一页
		} else {
			params.setMargins(params.width * arg0 + (int) (params.width * arg1), 0, 0, 0);
		}
		viewLine.setLayoutParams(params);
	}

	@Override
	public void onPageSelected(int arg0) {
		RadioButton radioButton = (RadioButton) radioGroupBrand.getChildAt(arg0);
		radioButton.setChecked(true);// 设置为选中 变换字体颜色

		// 是导航条被选中的栏目滚动到屏幕正中间
		int left = radioButton.getLeft();// 控件左边离屏幕左边距离
		int right = radioButton.getRight();// 控件右边离屏幕左边距离 差为控件width
		int width = getActivity().getWindow().getDecorView().getWidth();

		int x = left - width / 2 + (right - left) / 2;
		// 滚动到指定偏移量
		horizontalScrollView.scrollTo(x, 0);
	}
}

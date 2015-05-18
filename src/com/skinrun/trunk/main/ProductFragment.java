package com.skinrun.trunk.main;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.app.base.entity.BrandProductEntity;
import com.app.base.entity.UserEntity;
import com.app.service.GetBrandProductService;
import com.baidu.mobstat.StatService;
import com.base.app.utils.DBService;
import com.base.app.utils.DensityUtil;
import com.base.pulltorefresh.view.PullToRefreshBase;
import com.base.pulltorefresh.view.PullToRefreshBase.Mode;
import com.base.pulltorefresh.view.PullToRefreshBase.OnRefreshListener2;
import com.base.pulltorefresh.view.PullToRefreshGridView;
import com.base.service.action.constant.HttpTagConstantUtils;
import com.base.service.impl.HttpAysnResultInterface;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.R;
import com.skinrun.trunk.adapter.BrandProductAdapter;
import com.umeng.analytics.MobclickAgent;

public class ProductFragment extends Fragment implements HttpAysnResultInterface, OnRefreshListener2<GridView> {
	private int brand_Id;
	private PullToRefreshGridView pullToRefreshGridViewProduct;
	private GridView gridView;
	private int pageIndex=1,pageSize=10;
	private ArrayList<BrandProductEntity> data=new ArrayList<BrandProductEntity>();
	private BrandProductAdapter mAdapter;
	private int merchant_id;
	private SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
	
	public static ProductFragment getInstance(Bundle bundle){
		ProductFragment p=new ProductFragment();
		p.setArguments(bundle);
		return p;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle=getArguments();
		brand_Id=bundle.getInt("brand_Id");
		merchant_id=bundle.getInt("merchant_id");
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view=LayoutInflater.from(getActivity()).inflate(R.layout.product_fragment, null);
		pullToRefreshGridViewProduct=(PullToRefreshGridView)view.findViewById(R.id.pullToRefreshGridViewProduct);
		pullToRefreshGridViewProduct.setMode(Mode.BOTH);
		pullToRefreshGridViewProduct.setOnRefreshListener(this);
		
		
		gridView=pullToRefreshGridViewProduct.getRefreshableView();
		gridView.setNumColumns(2);
		gridView.setHorizontalSpacing(DensityUtil.dip2px(getActivity(), 10));
		gridView.setVerticalSpacing(DensityUtil.dip2px(getActivity(), 10));
		
		mAdapter=new BrandProductAdapter(getActivity(), data);
		gridView.setAdapter(mAdapter);
		
		loadData();
		
		return view;
	}
	
	private void loadData(){
		UserEntity u=DBService.getUserEntity();
		if(u!=null){
			new GetBrandProductService(getActivity(), HttpTagConstantUtils.GET_MY_LOVED_PRODUCT, this).doGet(u.getToken(),merchant_id, brand_Id, pageIndex, pageSize);
		}
	}

	@Override
	public void dataCallBack(Object tag, int statusCode, Object result) {
		pullToRefreshGridViewProduct.onRefreshComplete();
		if(statusCode==200||statusCode==201){
			ArrayList<BrandProductEntity> tem=(ArrayList<BrandProductEntity>) result;
			if(tem!=null&&tem.size()>0){
				if(pageIndex==1){
					data.clear();
				}
				data.addAll(tem);
				mAdapter.notifyDataSetChanged();
			}else{
				AppToast.toastMsg(getActivity().getApplicationContext(), "没有更多数据了~").show();
			}
		}
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
		pageIndex=1;
		String label = format.format(new Date());
		refreshView.getLoadingLayoutProxy().setLastUpdatedLabel("最后更新时间:" + label);
		loadData();
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
		pageIndex++;
		loadData();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(getActivity());
		StatService.onResume(getActivity());
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		MobclickAgent.onPause(getActivity());
		StatService.onPause(getActivity());
	}
	
	
}

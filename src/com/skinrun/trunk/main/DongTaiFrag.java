package com.skinrun.trunk.main;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.app.base.entity.DongTaiEntity;
import com.app.base.entity.UserEntity;
import com.app.service.PostHomeService;
import com.baidu.mobstat.StatService;
import com.base.app.utils.DBService;
import com.base.app.utils.HomeTag;
import com.base.gifview.GifMovieView;
import com.base.pulltorefresh.view.PullToRefreshBase;
import com.base.pulltorefresh.view.PullToRefreshBase.Mode;
import com.base.pulltorefresh.view.PullToRefreshBase.OnRefreshListener2;
import com.base.pulltorefresh.view.PullToRefreshListView;
import com.base.service.action.constant.HttpTagConstantUtils;
import com.base.service.impl.HttpAysnResultInterface;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.CommentActivity;
import com.beabox.hjy.tt.R;
import com.skinrun.trunk.adapter.DongTaiAdapter;
import com.umeng.analytics.MobclickAgent;

public class DongTaiFrag extends Fragment implements OnRefreshListener2<ListView>, OnItemClickListener,
		HttpAysnResultInterface, OnClickListener, OnScrollListener {
	private PullToRefreshListView pullToRefreshListView;
	private ListView realListView;
	private ArrayList<DongTaiEntity> data=new ArrayList<DongTaiEntity>();;
	private DongTaiAdapter mAdapter;
	private GifMovieView loading;
	private int pageIndex = 1;
	private int pageSize=8;
	private ImageView ivGoTop;
	private SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.main_frags, null);
		
		ivGoTop=(ImageView)view.findViewById(R.id.ivGoTop);
		ivGoTop.setOnClickListener(this);
		pullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.main_listView);
		realListView = pullToRefreshListView.getRefreshableView();
		pullToRefreshListView.setMode(Mode.BOTH);
		pullToRefreshListView.setOnRefreshListener(this);
		if(realListView!=null){
			realListView.setOnItemClickListener(this);
			realListView.setSelector(getActivity().getResources().getDrawable(R.drawable.list_item_selector));
			realListView.setOnScrollListener(this);
			
			mAdapter=new DongTaiAdapter(getActivity(), data);
			realListView.setAdapter(mAdapter);

			loading = (GifMovieView) view.findViewById(R.id.main_loading);
		}
		pageIndex=1;
		loadData();
		return view;
	}

	// 加载数据
	private void loadData() {
		UserEntity userEntity=DBService.getUserEntity();
		if(userEntity!=null){
			new PostHomeService(getActivity(), HttpTagConstantUtils.POST_DONGTAI, this).doPost(userEntity.getToken(),"dynamic",pageIndex, pageSize);
		}else{
			new PostHomeService(getActivity(), HttpTagConstantUtils.POST_DONGTAI, this).doPost("","dynamic",pageIndex, pageSize);
		}
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		pageIndex=1;
		String label = format.format(new Date());
		refreshView.getLoadingLayoutProxy().setLastUpdatedLabel("最后更新时间:" + label);
		loadData();
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		pageIndex++;
		loadData();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
		DongTaiEntity entity=data.get(index-1);
		Intent intent=new Intent(getActivity(), CommentActivity.class);
		
		//传递值
		intent.putExtra("SOURSE_ID", entity.getId());
		
		intent.putExtra("SOURSE_TYPE", HomeTag.SKIN_TEST);
		
		getActivity().startActivity(intent);
	}

	@Override
	public void dataCallBack(Object tag, int statusCode, Object result) {
		switch((Integer)tag){
		case HttpTagConstantUtils.POST_DONGTAI:
			loading.setVisibility(View.INVISIBLE);
			pullToRefreshListView.onRefreshComplete();
			if(statusCode==200||statusCode==201){
				if(result!=null){
					ArrayList<DongTaiEntity> tem=(ArrayList<DongTaiEntity>)result;
					if(tem!=null&&tem.size()>0){
						if(pageIndex==1){
							data.clear();
						}
						data.addAll(tem);
						mAdapter.notifyDataSetChanged();
					}else{
						AppToast.toastMsg(getActivity(), "没有更多数据了~").show();
					}
				}else{
					AppToast.toastMsg(getActivity(), "没有更多数据了~").show();
				}
			}
			break;
		}
		
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.ivGoTop:
			realListView.setSelection(0);
			
			break;
		}
	}
	@Override
	public void onScroll(AbsListView view, int arg1, int arg2, int arg3) {
		if(view.getLastVisiblePosition()>=8){
			ivGoTop.setVisibility(View.VISIBLE);
		}else{
			ivGoTop.setVisibility(View.GONE);
		}
	}
	@Override
	public void onScrollStateChanged(AbsListView arg0, int arg1) {
		
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

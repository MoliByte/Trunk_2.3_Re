package com.skinrun.trunk.main;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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
import com.app.base.entity.HomeSelectedEntity;
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
import com.beabox.hjy.tt.ActionDiscussActivity;
import com.beabox.hjy.tt.ArticleDiscussActivity;
import com.beabox.hjy.tt.R;
import com.beabox.hjy.tt.SelectedSecondActivity;
import com.skinrun.trunk.adapter.SelcetedAdapter;
import com.umeng.analytics.MobclickAgent;

public class SelectedFrag extends Fragment implements OnRefreshListener2<ListView>, OnItemClickListener, 
					HttpAysnResultInterface, OnClickListener, OnScrollListener {
	private PullToRefreshListView pullToRefreshListView;
	private ListView realListView;
	private ArrayList<HomeSelectedEntity> data=new ArrayList<HomeSelectedEntity>();
	private SelcetedAdapter mAdapter;
	private GifMovieView loading;
	private int pageIndex=1;
	private int pageSize=8;
	private ImageView goTop;
	@SuppressLint("SimpleDateFormat")
	private SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
	private String TAG="SelectedFrag";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view=inflater.inflate(R.layout.main_frags, null);
		
		goTop=(ImageView) view.findViewById(R.id.ivGoTop);
		goTop.setOnClickListener(this);
		
		pullToRefreshListView=(PullToRefreshListView)view.findViewById(R.id.main_listView);
		realListView = pullToRefreshListView.getRefreshableView();
		pullToRefreshListView.setMode(Mode.BOTH);
		pullToRefreshListView.setOnRefreshListener(this);
		realListView.setOnItemClickListener(this);
		realListView.setOnScrollListener(this);
		realListView.setSelector(getActivity().getResources().getDrawable(R.drawable.list_item_selector));
		
		mAdapter=new SelcetedAdapter(data, getActivity());
		realListView.setAdapter(mAdapter);
		
		loading=(GifMovieView)view.findViewById(R.id.main_loading);
		
		pageIndex=1;
		loadData();
		return view;
	}

	//加载数据
	private void loadData(){
		UserEntity userEntity=DBService.getUserEntity();
		if(userEntity!=null){
			new PostHomeService(getActivity(), HttpTagConstantUtils.POST_SELECTED, this).doPost(userEntity.getToken(),"pick",pageIndex, pageSize);
		}else{
			new PostHomeService(getActivity(), HttpTagConstantUtils.POST_SELECTED, this).doPost("","pick",pageIndex, pageSize);
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
		Log.e(TAG, "==========点击项index:"+index);
		
		
		HomeSelectedEntity entity=data.get(index-1);
		if(entity.getData_type().equals("facemark")){
			Intent i=new Intent(getActivity(), SelectedSecondActivity.class);
			
			i.putExtra("ID", entity.getId());
			i.putExtra("SOURSE_TYPE", HomeTag.FACE_MARK);
			
			startActivity(i);
			
			
			
		}else if(entity.getData_type().equals("archive")){
			
			if(entity.getType().equals("activity")){
				
				Bundle bundle = new Bundle();
				bundle.putLong("activityId", Long.parseLong(entity.getId()));
				Intent intent = new Intent(getActivity(),ActionDiscussActivity.class);
				intent.putExtras(bundle);
				startActivity(intent);
				
			}else{
				
				Bundle bundle = new Bundle();
				bundle.putLong("articleId", Long.parseLong(entity.getId()+""));
				Intent intent = new Intent(getActivity(),ArticleDiscussActivity.class);
				intent.putExtras(bundle);
				startActivity(intent);
			}
		}
	}

	@Override
	public void dataCallBack(Object tag, int statusCode, Object result) {
		switch((Integer)tag){
		case HttpTagConstantUtils.POST_SELECTED:
			try{
				
				loading.setVisibility(View.INVISIBLE);
				if(statusCode==200||statusCode==201){
					if(result!=null){
						@SuppressWarnings("unchecked")
						ArrayList<HomeSelectedEntity> tem=(ArrayList<HomeSelectedEntity>)result;
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
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				pullToRefreshListView.onRefreshComplete();
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
	public void onScroll(AbsListView v, int arg1, int arg2, int arg3) {
		if(v.getLastVisiblePosition()>=4){
			goTop.setVisibility(View.VISIBLE);
		}else{
			goTop.setVisibility(View.GONE);
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

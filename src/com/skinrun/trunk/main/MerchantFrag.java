package com.skinrun.trunk.main;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.kymjs.aframe.utils.SystemTool;

import com.app.base.entity.MyLovedMerchantEntity;
import com.app.base.entity.UserEntity;
import com.app.service.CancleAttentionMerchantService;
import com.app.service.GetMyLovedMerchant;
import com.baidu.mobstat.StatService;
import com.base.app.utils.DBService;
import com.base.app.utils.MySubscribeUtil;
import com.base.dialog.lib.Effectstype;
import com.base.dialog.lib.NiftyDialogBuilder;
import com.base.pulltorefresh.view.PullToRefreshBase;
import com.base.pulltorefresh.view.PullToRefreshBase.Mode;
import com.base.pulltorefresh.view.PullToRefreshBase.OnRefreshListener2;
import com.base.pulltorefresh.view.PullToRefreshListView;
import com.base.service.action.constant.HttpTagConstantUtils;
import com.base.service.impl.HttpAysnResultInterface;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.MerchantDetailActivity;
import com.beabox.hjy.tt.ProducerActivity;
import com.beabox.hjy.tt.R;
import com.skinrun.trunk.adapter.MerchantAdapter;
import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;

public class MerchantFrag extends Fragment implements HttpAysnResultInterface, OnRefreshListener2<ListView>,
												OnItemClickListener, OnItemLongClickListener{
	private ArrayList<MyLovedMerchantEntity> data=new ArrayList<MyLovedMerchantEntity>();
	private PullToRefreshListView pullToRefreshListView;
	private ImageView no_my_merchant;
	private ListView realListView;
	private int pageIndex=1,pageSize=10;
	private MerchantAdapter mAdapter;
	
	private String type="";
	private SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
	
	public static MerchantFrag getInstance(Bundle bundle){
		MerchantFrag m=new MerchantFrag();
		m.setArguments(bundle);
		return m;
	}
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle=getArguments();
		type=bundle.getString("TYPE");
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view=inflater.inflate(R.layout.merchant_activity, null);
		
		no_my_merchant=(ImageView)view.findViewById(R.id.no_my_merchant);
		pullToRefreshListView=(PullToRefreshListView)view.findViewById(R.id.main_merchant_listView);
		pullToRefreshListView.setMode(Mode.BOTH);
		pullToRefreshListView.setOnRefreshListener(this);
		realListView=pullToRefreshListView.getRefreshableView();
		
		mAdapter=new MerchantAdapter(getActivity(), data);
		realListView.setAdapter(mAdapter);
		realListView.setOnItemClickListener(this);
		realListView.setOnItemLongClickListener(this);
		loadData();
		return view;
	}
	
	private void loadData(){
		UserEntity user=DBService.getUserEntity();
		if(user!=null){
			String token=user.getToken();
			if(token!=null&&!token.equals("")){
				new GetMyLovedMerchant(getActivity(), HttpTagConstantUtils.GET_MY_LOVED_MERCHANT,this).doGet(token,type, pageIndex, pageSize);
			}
		}
	}

	@Override
	public void dataCallBack(Object tag, int statusCode, Object result) {
		switch((Integer)tag){
		case HttpTagConstantUtils.CANCLE_ATTENTION_MERCHANT:
			if(statusCode==200||statusCode==201){
				AppToast.toastMsg(getActivity(), "取消关注成功");
			}
			
			
			break;
		case HttpTagConstantUtils.GET_MY_LOVED_MERCHANT:
			@SuppressWarnings("unchecked")
			ArrayList<MyLovedMerchantEntity> tem=(ArrayList<MyLovedMerchantEntity>) result;
			pullToRefreshListView.onRefreshComplete();
			if(statusCode==200||statusCode==201){
				if(tem==null||tem.size()<=0){
					AppToast.toastMsg(getActivity().getApplicationContext(), "没有更多数据了~");
				}
			}
			
			if(tem!=null&&tem.size()>0){
				if(pageIndex==1){
					data.clear();
				}
				data.addAll(tem);
				no_my_merchant.setVisibility(View.GONE);
				mAdapter.notifyDataSetChanged();
			}
			break;
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
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if(type.equals(MySubscribeUtil.beautyParlor)){
			Intent intent=new Intent(getActivity(), MerchantDetailActivity.class);
			intent.putExtra("merchant_id", data.get(arg2-1).getId());
			startActivity(intent);
		}else if(type.equals(MySubscribeUtil.producer)){
			Intent i=new Intent(getActivity(), ProducerActivity.class);
			//i.putExtra("merchant_id",  data.get(arg2-1).getId());
			i.putExtra("MyLovedMerchantEntity", data.get(arg2-1));
			startActivity(i);
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int position,
			long arg3) {
		if (!SystemTool.checkNet(getActivity())) {
			AppToast.toastMsgCenter(getActivity(),this.getResources().getString(R.string.no_network)).show();
			return false;
		}
		final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(getActivity());
		
		try{
			dialogBuilder.withTitle(null).withMessage(null).withMessageColor("#FFFFFFFF")
			.isCancelableOnTouchOutside(false).withDuration(200).withEffect(Effectstype.Fadein)
			.withButton1Text("是").setCustomView(R.layout.delete_my_collect,getActivity())
			.setButton1Click(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					dialogBuilder.dismiss();
					UserEntity u=DBService.getUserEntity();
					if(u!=null){
						int id=Integer.parseInt( data.get(position-1).getId());
						Log.e("MerchantFrag", "===========长按事件");	
						new CancleAttentionMerchantService(getActivity(), HttpTagConstantUtils.CANCLE_ATTENTION_MERCHANT, MerchantFrag.this).
							cancle(u.getToken(),id);
					}
					data.remove(position-1);
					if(data.size()==0){
						no_my_merchant.setVisibility(View.VISIBLE);
					}
					mAdapter.notifyDataSetChanged();
				}
			}).withButton2Text("否").setButton2Click(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					dialogBuilder.dismiss();
				}
			}).show();
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
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

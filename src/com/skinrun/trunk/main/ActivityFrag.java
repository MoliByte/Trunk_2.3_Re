package com.skinrun.trunk.main;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.app.base.entity.ActivityDataEntity;
import com.app.base.init.MyApplication;
import com.app.service.ActivityAsynTaskService;
import com.baidu.mobstat.StatService;
import com.base.app.utils.StringUtil;
import com.base.pulltorefresh.view.PullToRefreshBase;
import com.base.pulltorefresh.view.PullToRefreshBase.Mode;
import com.base.pulltorefresh.view.PullToRefreshBase.OnRefreshListener2;
import com.base.pulltorefresh.view.PullToRefreshListView;
import com.base.service.action.constant.HttpTagConstantUtils;
import com.base.service.impl.HttpAysnResultInterface;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.ActionDiscussActivity;
import com.beabox.hjy.tt.R;
import com.skinrun.trunk.adapter.ActivityDataListAdapter;
import com.umeng.analytics.MobclickAgent;

/**
 * 活动页面
 * 
 * @author zhup
 * 
 */
public class ActivityFrag extends Fragment implements OnClickListener,
		HttpAysnResultInterface {
	private final static String TAG = "ActivityFrag";
	private ArrayList<ActivityDataEntity> activityDataEntity = new ArrayList<ActivityDataEntity>();
	private PullToRefreshListView pullToRefreshListView;
	private ListView realListView;
	private SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
	private int pageIndex=1;
	private ActivityDataListAdapter mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initUmeng();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_main_fragment,
				container, false);

		pullToRefreshListView = (PullToRefreshListView) view
				.findViewById(R.id.activityDataListView);
		realListView = pullToRefreshListView.getRefreshableView();
		
		mAdapter=new ActivityDataListAdapter(activityDataEntity, getActivity());
		realListView.setAdapter(mAdapter);
		
		pullToRefreshListView.setMode(Mode.BOTH);
		pullToRefreshListView
				.setOnRefreshListener(new OnRefreshListener2<ListView>() {
					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						String label = format.format(new Date());
						refreshView.getLoadingLayoutProxy()
								.setLastUpdatedLabel("最后更新时间:" + label);
						pageIndex=1;
						getData();
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						pageIndex++;
						getData();
					}
				});

		Runnable task = new Runnable() {
			@Override
			public void run() {
				getData();
			}
		};

		new Handler().post(task);
		// 到活动详情页面
		pullToRefreshListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				Log.e(TAG, position + ">>>>>>>>>>>>>>>>>");

				ActivityDataEntity action = activityDataEntity.get(position - 1) ;
				//action.setId(1l);
				Log.e(TAG, "action_id====>" + action.getId());
				Bundle bundle = new Bundle();
				bundle.putLong("activityId",action.getId());
				Intent intent = new Intent(getActivity(),
						ActionDiscussActivity.class);
				intent.putExtras(bundle);
				startActivity(intent);
			}

		});
		return view;
	}

	private void getData() {
		new ActivityAsynTaskService(this.getActivity(),
				HttpTagConstantUtils.ACTIVITY_DATA_TAG, this)
				.doActivityDataList(pageIndex);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onClick(View v) {

	}

	@Override
	public void dataCallBack(Object tag, int statusCode, Object result) {
		try {
			if ((Integer) tag == HttpTagConstantUtils.ACTIVITY_DATA_TAG) {// 活动listview数据
				ArrayList<ActivityDataEntity> tem = (ArrayList<ActivityDataEntity>) result;
				
				Log.e(TAG, "=========TEM长度:"+tem.size());
				
				if(null!=tem && tem.size() > 0){
					
					if(pageIndex==1){
						activityDataEntity.clear();
						activityDataEntity.addAll(tem);
						Collections.sort(activityDataEntity);
						for(ActivityDataEntity entity : activityDataEntity){
							Log.e(TAG, "activity status = "+entity.getStatus());
						}
						
						mAdapter.notifyDataSetChanged();
					}else{
						activityDataEntity.addAll(tem);
						
						Collections.sort(activityDataEntity);
						for(ActivityDataEntity entity : activityDataEntity){
							Log.e(TAG, "activity status = "+entity.getStatus());
						}
						mAdapter.notifyDataSetChanged();
					}
					
					
				}else{
					AppToast.toastMsgCenter(getActivity(), "~_~暂无活动");
				}
				
			}
		} catch (Exception e) {

		} finally {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					if (null != pullToRefreshListView) {
						pullToRefreshListView.onRefreshComplete();
					}
				}
			}, 1000);
		}

	}
	
	public String getActivityName(){
		return "活动列表" ;
	}
	@Override
	public void onResume() {
		super.onResume();
		Log.e(TAG, "MyApplication.resume_index = "+MyApplication.resume_index);
		MyApplication.resume_index = 1 ;
		//getData();
		MobclickAgent.onPageStart(getActivityName()); //统计页面
	    MobclickAgent.onResume(getActivity());
	    StatService.onResume(getActivity());
	}
	
	private void initUmeng(){
        MobclickAgent.setDebugMode(false);
        MobclickAgent.openActivityDurationTrack(false);
        MobclickAgent.updateOnlineConfig(getActivity());
    }
    
    /**
     * 自定义事件统计
     *
     * @param eventCode
     * @param eventName
     */
    public void logEvent(String eventCode, String eventName) {
        MobclickAgent.onEvent(getActivity(), eventCode);
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
    public void onDestroy() {
    	super.onDestroy();
    	MyApplication.resume_index = 1 ;
    }

}

package com.skinrun.trunk.main;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.HttpStatus;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.app.base.entity.ADInfo;
import com.app.base.entity.ActivityDataEntity;
import com.app.base.entity.HomeDataEntity;
import com.app.base.entity.LastTestDataEntity;
import com.app.base.entity.LastTimeTestRecordEntity;
import com.app.base.entity.UserEntity;
import com.app.base.init.ACache;
import com.app.base.init.MyApplication;
import com.app.custom.view.CustomizedGallery;
import com.app.service.GetLastTestDateService;
import com.app.service.HomeADAsynTaskService;
import com.app.service.HomeDataAsynTaskService;
import com.baidu.mobstat.StatService;
import com.base.app.utils.DBService;
import com.base.app.utils.KVOEvents;
import com.base.app.utils.StringUtil;
import com.base.gifview.GifMovieView;
import com.base.pulltorefresh.view.PullToRefreshBase;
import com.base.pulltorefresh.view.PullToRefreshBase.Mode;
import com.base.pulltorefresh.view.PullToRefreshBase.OnRefreshListener2;
import com.base.pulltorefresh.view.PullToRefreshListView;
import com.base.service.action.constant.HttpTagConstantUtils;
import com.base.service.impl.HttpAysnResultInterface;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.ActionDiscussActivity;
import com.beabox.hjy.tt.AdDetailsActivity;
import com.beabox.hjy.tt.ArticleDiscussActivity;
import com.beabox.hjy.tt.R;
import com.beabox.hjy.tt.main.skintest.component.KVO.Observer;
import com.skinrun.trunk.adapter.HomeAdGalleryAdapter;
import com.skinrun.trunk.adapter.HomeDataListAdapter;
import com.skinrun.trunk.adapter.HomeGridGuideAdapter;
import com.umeng.analytics.MobclickAgent;

/**
 * 首页页面
 * 
 * @author zhup
 * 
 */
public class MainFrag extends Fragment implements OnClickListener,
		HttpAysnResultInterface ,Observer{
	
	public static final String TAG = "MainFrag" ;

	private ArrayList<HomeDataEntity> homeDataEntityList = new ArrayList<HomeDataEntity>();
	private HomeDataListAdapter adapter;
	private PullToRefreshListView pullToRefreshListView;
	private ListView realListView;
	private SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");

	// 首页广告窗口数据
	private CustomizedGallery infoshow_gallery;
	private GridView infoshow_gridview;
	private HomeAdGalleryAdapter galleryAdapter;
	private HomeGridGuideAdapter gridviewAdapter;
	private int currentIndex_ = 0;
	private boolean m = true;
	private int gridview_horSpac = 26;//
	private int gridview_xpadding = 10;
	private int gridview_ypadding = 5;
	private ArrayList<ADInfo> adList = new ArrayList<ADInfo>();
	
	private TextView tv_main_water,tv_main_oil,tv_main_flexible;
	
	private GifMovieView home_data_loading ;
	
	private int page = 1 ;
	
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initUmeng();
		try {
			MyApplication.getInstance().getKvo().registerObserver(KVOEvents.TEST_RESULT, this);
			MyApplication.getInstance().getKvo().registerObserver(KVOEvents.USER_LOGININ, this);
		} catch (Exception e) {
			
		}
		
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			MyApplication.resume_index = 0 ;
			MyApplication.getInstance().getKvo().removeObserver(KVOEvents.TEST_RESULT, this);
			MyApplication.getInstance().getKvo().removeObserver(KVOEvents.USER_LOGININ, this);
		} catch (Exception e) {
			
		}catch (OutOfMemoryError e) {
			
		}
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.home_main_fragment, container,
				false);
		pullToRefreshListView = (PullToRefreshListView) view
				.findViewById(R.id.homeDataListView);
		realListView = pullToRefreshListView.getRefreshableView();
		homeListViewAddHeader();
		pullToRefreshListView.setMode(Mode.BOTH);
		pullToRefreshListView
				.setOnRefreshListener(new OnRefreshListener2<ListView>() {
					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						String label = format.format(new Date());
						refreshView.getLoadingLayoutProxy()
								.setLastUpdatedLabel("最后更新时间:" + label);
						page = 1 ;
						if(null != homeDataEntityList){
							homeDataEntityList.clear() ;
						}
						homeDataEntityList = new ArrayList<HomeDataEntity>();
						getData();
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						page ++ ;
						getMore();
					}

				});

		Runnable task = new Runnable() {

			@Override
			public void run() {
				getData();
			}
		};

		new Handler().post(task);
		
		//到文章详情页面
		pullToRefreshListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				Log.e(TAG, position+">>>>>>>>>>>>>>>>>");
				HomeDataEntity info = homeDataEntityList.get(position-2);
				/*Bundle bundle = new Bundle();
				bundle.putSerializable("article", homeDataEntityList.get(position-2));
				
				Intent intent = new Intent(getActivity(),ArticleDiscussActivity.class);
				intent.putExtras(bundle);
				startActivity(intent);*/
				
				
				if("activity".equals(info.getType())){
					//action.setId(1l);
					ActivityDataEntity action = new ActivityDataEntity() ;
					action.setId(info.getArticle_id());
					//action.setActivity_left_time(adInfo.getEnd_date());
					action.setTitle(info.getTitle());
					action.setActivity_title(info.getTitle());
					Bundle bundle = new Bundle();
					bundle.putSerializable("action",action);
					Intent intent = new Intent(getActivity(),
							ActionDiscussActivity.class);
					intent.putExtras(bundle);
					startActivity(intent);
				}else if("post".equals(info.getType())){
					HomeDataEntity article = new HomeDataEntity();
					article.setArticle_id(info.getArticle_id());
					article.setAuthor_img(info.getAuthor_img());
					article.setUsername(info.getUsername());
					article.setReleaseTime(info.getReleaseTime());
					article.setAdPic(info.getImgURL());
					article.setTitle(info.getTitle());
					Bundle bundle = new Bundle();
					bundle.putSerializable("article", article);
					
					Intent intent = new Intent(getActivity(),ArticleDiscussActivity.class);
					intent.putExtras(bundle);
					startActivity(intent);
				}
			}
			
		});
		return view;
	}

	private void getData() {
		new HomeDataAsynTaskService(getActivity().getApplicationContext(),
				HttpTagConstantUtils.HOME_DATA_TAG, MainFrag.this)
				.doHomeDataList(page);

		new HomeADAsynTaskService(getActivity().getApplicationContext(),
				HttpTagConstantUtils.HOME_DATA_AD_TAG, MainFrag.this)
				.doHomeADList();
		
	}
	
	private void getMore(){
		new HomeDataAsynTaskService(getActivity().getApplicationContext(),
				HttpTagConstantUtils.HOME_DATA_TAG, MainFrag.this)
				.doHomeDataList(page);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onClick(View v) {
		/*switch(v.getId()){
		case R.id.lineTestData:
			SkinRunMainActivity s=(SkinRunMainActivity)getActivity();
			if(isLogin()){
				s.goToFrag(2);
			}else {
				s.goToFrag(4);
			}
			
			break;
		}*/
	}
	
	private boolean isLogin(){
		UserEntity u=DBService.getUserEntity();
		if(u==null){
			return false;
			
		}
		return true;
	}
	
	public String getActivityName(){
		return "肌肤管家" ;
	}
	@Override
	public void onResume() {
		super.onResume();
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
        if (!StringUtil.isEmpty(getActivityName())) {
            
            MobclickAgent.onPageEnd(getActivityName()); // 保证 onPageEnd 在onPause 之前调用,因为 onPause 中会保存信息
            MobclickAgent.onPause(getActivity());
            StatService.onPause(getActivity());
        }
    }

	/**
	 * 添加ListView头部信息
	 **********************************/
	public void homeListViewAddHeader() {
		View view = LayoutInflater.from(getActivity()).inflate(
				R.layout.home_main_listview_head, null);
		infoshow_gallery = (CustomizedGallery) view
				.findViewById(R.id.infoshow_gallery);
		infoshow_gridview = (GridView) view
				.findViewById(R.id.infoshow_gridview);
		
		home_data_loading = (GifMovieView) view.findViewById(R.id.home_data_loading);
		tv_main_water=(TextView)view.findViewById(R.id.tv_main_water);
		tv_main_oil=(TextView)view.findViewById(R.id.tv_main_oil);
		tv_main_flexible=(TextView)view.findViewById(R.id.tv_main_flexible);
		
		view.findViewById(R.id.lineTestData).setOnClickListener(this);
		
		String token = ACache.get(getActivity()).getAsString("Token");
		
		Log.e(TAG, "==========主界面TOKEN:"+token);
		
		if(token!=null&&!"".equals(token)){
			getTestValue(token);
			
		}
		
		
		
		realListView.addHeaderView(view);
	}
	
	private void requestLastData(String token){
		
		GetLastTestDateService getLastTestDateService=new GetLastTestDateService(getActivity(), HttpTagConstantUtils.GET_LAST_TEST_DATA, this);
		
		getLastTestDateService.doGetLastTestDate(token);	
	}

	private void getTestValue(String token){
		LastTimeTestRecordEntity lr=null;
		lr=LastTestRecordSaver.getRecord(token, TestingPartUtils.FACEPART);
		
		if(lr==null){
			lr=LastTestRecordSaver.getRecord(token, TestingPartUtils.HANDPART);
		}
		
		if(lr!=null){
			if(WeekUtil.isTodayTest(Long.parseLong(lr.getTime()))){
				try{
					tv_main_water.setText(dealNum(lr.getWater())+"%");
					tv_main_oil.setText(dealNum(lr.getOil())+"%");
					tv_main_flexible.setText(String.format("%1.1f", lr.getFlexible()));
					Log.e(TAG, "=========主界面水油弹："+lr.getWater()+":"+lr.getOil()+":"+lr.getFlexible());
					
				}catch(Exception e){
					
				}
			}
		}else{
			requestLastData(token);
		}
	}
	
	private int dealNum(float a){
		int b=(int)a;
		if(a>=b+0.5){
			return b+1;
		}
		return b;
	}

	@Override
	public void dataCallBack(Object tag, int statusCode, Object result) {
		try {
			if ((Integer) tag == HttpTagConstantUtils.GET_LAST_TEST_DATA) {
				if(statusCode!=200){
					return;
				}
				LastTestDataEntity lr=(LastTestDataEntity)result;
				if(lr!=null){
					if(!lr.getFace_lastday().equals("")&&!lr.getFace_lastday().equals("1970-01-01"))
						if(WeekUtil.isTodayTest(lr.getFace_lastday())){
							tv_main_water.setText(dealNum(lr.getFace_water())+"%");
							tv_main_oil.setText(dealNum(lr.getFace_oil())+"%");
							//tv_main_flexible.setText(String.format("%1.1f",  lr.getFace_elasticity()));
							tv_main_flexible.setText(dealNum(lr.getFace_elasticity())+"");
							Log.e(TAG, "=========主界面w头部水油弹："+lr.getFace_water()+":"+lr.getFace_oil()+":"+lr.getFace_elasticity());
							return;
						}
						
					if(!lr.getHand_lastday().equals("")&&!lr.getHand_lastday().equals("1970-01-01")){
						if(WeekUtil.isTodayTest(lr.getHand_lastday())){
							tv_main_water.setText(dealNum(lr.getHand_water())+"%");
							tv_main_oil.setText(dealNum(lr.getHand_oil())+"%");
							//tv_main_flexible.setText(String.format("%1.1f",  lr.getFace_elasticity()));
							tv_main_flexible.setText(dealNum(lr.getHand_elasticity())+"");
							Log.e(TAG, "=========主界面w手部水油弹："+lr.getFace_water()+":"+lr.getFace_oil()+":"+lr.getFace_elasticity());
							return;
						}
					}
				}
			}
			else if ((Integer) tag == HttpTagConstantUtils.HOME_DATA_TAG) {// 首页listview数据
				if(HttpStatus.SC_OK == statusCode || HttpStatus.SC_CREATED == statusCode){
					ArrayList<HomeDataEntity>  homeDataEntityList = (ArrayList<HomeDataEntity>) result;
					if(null!=homeDataEntityList ){
						if(homeDataEntityList.size() > 0){
							this.homeDataEntityList.addAll(homeDataEntityList);
							adapter = new HomeDataListAdapter(this.homeDataEntityList, this
									.getActivity().getApplicationContext());
							pullToRefreshListView.getRefreshableView().setAdapter(adapter);
							Log.e(TAG, "size = " + homeDataEntityList.size());
							if(page > 1){
								pullToRefreshListView.getRefreshableView().setSelection((page-1)*10+2);
							}
							adapter.notifyDataSetChanged();
						}else if(homeDataEntityList.size() == 0){
							AppToast.toastMsgCenter(getActivity(),"~_~没有更多了").show();
						}
					}else if(null == homeDataEntityList){
						AppToast.toastMsgCenter(getActivity(),"~_~没有更多了").show();
					}
					
				}else{
					//AppToast.toastMsgCenter(getActivity(), getString(R.string.ERROR_404)).show();
				}
				
			} else if ((Integer) tag == HttpTagConstantUtils.HOME_DATA_AD_TAG) {// 首页广告数据
				adList = (ArrayList<ADInfo>)result ;
				for (int i = 0; i < adList.size(); i++) {
					ADInfo info = adList.get(i);
					info.setAdImage(info.getImgURL());
				}
				galleryAdapter = new HomeAdGalleryAdapter(getActivity()
						.getApplicationContext(), adList);
				infoshow_gallery.setAdapter(galleryAdapter);
				gridviewAdapter = new HomeGridGuideAdapter(getActivity()
						.getApplicationContext(), adList);
				infoshow_gridview.setAdapter(gridviewAdapter);
				Bitmap bmp = new BitmapFactory().decodeResource(getResources(),
						R.drawable.ball_normal);
				int width = bmp.getWidth();
				int height = bmp.getHeight();
				infoshow_gridview.setColumnWidth(width);
				infoshow_gridview.setHorizontalSpacing(gridview_horSpac);//
				infoshow_gridview.setNumColumns(adList.size());//
				FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) infoshow_gridview
						.getLayoutParams();
				lp.width = width * adList.size() + gridview_horSpac
						* (adList.size() - 1) + (gridview_xpadding << 1);
				lp.height = height + (gridview_ypadding << 1);
				infoshow_gridview.setLayoutParams(lp);
				infoshow_gridview
						.setPadding(gridview_xpadding, gridview_ypadding,
								gridview_xpadding, gridview_ypadding);
				infoshow_gridview.setSelection(0);
				gridviewAdapter.notifyDataSetChanged();

				infoshow_gallery
						.setOnItemSelectedListener(new Gallery.OnItemSelectedListener() {
							@Override
							public void onItemSelected(AdapterView<?> parent,
									View view, int position, long id) {
								currentIndex_ = position;
								gridviewAdapter.setCurrentIndex(currentIndex_);
								gridviewAdapter.notifyDataSetChanged();
							}

							@Override
							public void onNothingSelected(AdapterView<?> arg0) {

							}

						});
				infoshow_gallery
						.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								// 进入广告
								
								Log.e(TAG, position + ">>>>>>>>>>>>>>>>>");

								ADInfo adInfo = adList.get(position) ;
								
								if("activity".equals(adInfo.getType())){
									//action.setId(1l);
									ActivityDataEntity action = new ActivityDataEntity() ;
									action.setId(Integer.valueOf(adInfo.getAd_link()==null?"0":adInfo.getAd_link()));
									action.setActivity_left_time(adInfo.getEnd_date());
									action.setTitle(adInfo.getTitle());
									action.setActivity_title(adInfo.getTitle());
									action.setAuthor_img(adInfo.getAuthor_img());
									Bundle bundle = new Bundle();
									bundle.putSerializable("action",action);
									bundle.putLong("activityId", Long.valueOf(adInfo.getAd_link()==null
											||"".equals(adInfo.getAd_link())?"0":adInfo.getAd_link()));
									Intent intent = new Intent(getActivity(),
											ActionDiscussActivity.class);
									intent.putExtras(bundle);
									startActivity(intent);
									 getActivity().overridePendingTransition(
												R.anim.activity_enter_from_right,
												R.anim.activity_exit_to_left);
								}else if("post".equals(adInfo.getType())){
									HomeDataEntity article = new HomeDataEntity();
									article.setArticle_id(Integer.valueOf(adInfo.getAd_link()==null?"0":adInfo.getAd_link()));
									article.setAdPic(adInfo.getAdImage());
									article.setTitle(adInfo.getTitle());
									article.setAuthor_img(adInfo.getAuthor_img());
									Bundle bundle = new Bundle();
									bundle.putSerializable("article", article);
									//bundle.putLong("activityId", Long.valueOf(adInfo.getAd_link()==null?"0":adInfo.getAd_link()));
									Intent intent = new Intent(getActivity(),ArticleDiscussActivity.class);
									intent.putExtras(bundle);
									startActivity(intent);
									 getActivity().overridePendingTransition(
												R.anim.activity_enter_from_right,
												R.anim.activity_exit_to_left);
								}else if("url".equals(adInfo.getType())){
									Bundle bundle = new Bundle();
									bundle.putString("ad_link", adInfo.getAd_link());
									Intent intent = new Intent(getActivity(),AdDetailsActivity.class);
									intent.putExtras(bundle);
									startActivity(intent);
									 getActivity().overridePendingTransition(
												R.anim.activity_enter_from_right,
												R.anim.activity_exit_to_left);
								}
								
							}
						});
				// Gallery OnItemClick
				infoshow_gridview
						.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> parent,
									View view, int position, long id) {
								currentIndex_ = position;
								gridviewAdapter.setCurrentIndex(currentIndex_);
								gridviewAdapter.notifyDataSetChanged();
								infoshow_gallery.setSelection(currentIndex_);
							}

						});
				home_data_loading.setVisibility(View.GONE);
				// autoPlay();
			}
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		} finally {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					if (null != pullToRefreshListView) {
						pullToRefreshListView.onRefreshComplete();
					}
				}
			}, 100);
		}

	}

	private void autoPlay() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					currentIndex_ = currentIndex_ % adList.size();
					infoshow_gallery.post(new Runnable() {
						@Override
						public void run() {
							infoshow_gallery.setSelection(currentIndex_);
						}
					});
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					currentIndex_++;
				}
			}
		}).start();
	}

	@Override
	public void onEvent(String event, Object... args) {
		if(event.equals(KVOEvents.TEST_RESULT)){
			float waterValues = (Float) (args[0]);
			float oilValues = (Float) args[1];
			float flexibleValues = (Float) args[2];
			
			tv_main_water.setText((int)waterValues+"%");
			tv_main_oil.setText((int)oilValues+"%");
			tv_main_flexible.setText(String.format("%1.1f", flexibleValues));
			
		}
		
		if(event.equals(KVOEvents.USER_LOGININ)){
			UserEntity userEntity=DBService.getUserEntity();
			if(userEntity!=null){
				getTestValue(userEntity.getToken());
			}
		}
	}	
	
	
}

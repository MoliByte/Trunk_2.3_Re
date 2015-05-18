package com.beabox.hjy.tt;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.app.base.entity.SuggestionFeedBackDataEntity;
import com.app.base.init.BaseActivity;
import com.app.base.init.MyApplication;
import com.app.service.GetFeedBackContentAsynTaskService;
import com.app.service.PostFeedBackAsynTaskService;
import com.base.app.utils.EasyLog;
import com.base.dialog.lib.Effectstype;
import com.base.dialog.lib.NiftyDialogBuilder;
import com.base.pulltorefresh.view.PullToRefreshBase;
import com.base.pulltorefresh.view.PullToRefreshBase.Mode;
import com.base.pulltorefresh.view.PullToRefreshBase.OnRefreshListener2;
import com.base.pulltorefresh.view.PullToRefreshListView;
import com.base.service.action.constant.HttpTagConstantUtils;
import com.base.supertoasts.util.AppToast;
import com.skinrun.trunk.adapter.SuggestionFeedBackAdapter;
import com.umeng.message.PushAgent;

/**
 * Android2.3新版意见反馈
 * Author zhup
 */
@SuppressWarnings("serial")
public class SuggestionFeedBackV2 extends BaseActivity{
	private final static String  TAG = "SuggestionFeedBackV2" ;
	private ImageView backBtn ;//返回按钮
	private EditText suggestions ;//反馈意见
	private EditText keep_qq ;//预留QQ
	private EditText keep_phone_num ;//预留手机号码
	private View btn_send ;//提交反馈
	
	private ArrayList<SuggestionFeedBackDataEntity> suggestionDataEntity = new ArrayList<SuggestionFeedBackDataEntity>();
	private PullToRefreshListView pullToRefreshListView;
	private ListView realListView;
	private SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
	private int pageIndex=1;
	private SuggestionFeedBackAdapter mAdapter;
	
	NiftyDialogBuilder dialogUploadImage ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		MyApplication.getInstance().addActivity(this);
    	PushAgent.getInstance(this).onAppStart();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.suggest_feed_back_v2);
		setupView();
	}

	@Override
	public void setupView() {
		backBtn = (ImageView) findViewById(R.id.backBtn);
		pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.suggestionDataListView);
		realListView = pullToRefreshListView.getRefreshableView();
		realListView.addHeaderView(headView());
		
		mAdapter = new SuggestionFeedBackAdapter(suggestionDataEntity, this);
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
						if(null != suggestionDataEntity){
							suggestionDataEntity.clear();
						}
						suggestionDataEntity = new ArrayList<SuggestionFeedBackDataEntity>();
						getData();
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						pageIndex++;
						getData();
					}
				});

		/*Runnable task = new Runnable() {
			@Override
			public void run() {
				
			}
		};
		new Handler().post(task);*/
		getData();
		suggestions = (EditText) findViewById(R.id.et_sendmessage);
		btn_send = findViewById(R.id.btn_send);
		
		addListener();

	}
		
	private View headView(){
		View view = LayoutInflater.from(this).inflate(R.layout.suggestion_listview_head, null);
		
		return view ;
	}
	
	private void getData() {
		new GetFeedBackContentAsynTaskService(this,
				HttpTagConstantUtils.FEEDBACK_GET, this)
				.doDataList(pageIndex);
	}

	@Override
	public void addListener() {
		backBtn.setOnClickListener(this);
		btn_send.setOnClickListener(this);
	}
	

	@Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.backBtn:
                finish();
                break;
            case R.id.btn_send://提交
            	final String token = aCache.getAsString("Token");
            	final String suggestions = this.suggestions.getText().toString();
            	final String keep_qq = "" ;// this.keep_qq.getText().toString();
            	final String keep_phone = "" ;//this.keep_phone_num.getText().toString();
            	
            	dialogUploadImage = NiftyDialogBuilder.getInstance(this, R.layout.dialog_login_layout);
            			final View text = LayoutInflater.from(this).inflate(R.layout.dialog_login_view, null);
            			TextView t = (TextView) text.findViewById(R.id.loading_text);
            			t.setText("发送中....");
            			dialogUploadImage.withTitle(null)
            			.withMessage(null)
            			.withEffect(Effectstype.Fadein)
            			.withDuration(100)
            			.isCancelableOnTouchOutside(false)
            			.setCustomView(text, this.getApplicationContext()).show();
            			new Handler().postDelayed(new Runnable() {
            				
            				@Override
            				public void run() {
            					new PostFeedBackAsynTaskService(getApplicationContext(),
            							HttpTagConstantUtils.FEED_BACK,SuggestionFeedBackV2.this).doPostFeedBack(token,
            							suggestions, keep_qq, keep_phone);
            				}
            			}, 500);
            	
            	
            	break;
            default:
                break;
        }

    }

	@Override
	public void sendMessageHandler(int messageCode) {

	}
	
	public void dataCallBack(Object tag,  int statusCode,Object result) {
		try {
			Log.e(TAG, ""+result);
			if((Integer)(tag) == HttpTagConstantUtils.FEED_BACK && isSuccess(statusCode)){
				this.suggestions.setText("");
				pageIndex=1;
				if(null != suggestionDataEntity){
					suggestionDataEntity.clear();
				}
				suggestionDataEntity = new ArrayList<SuggestionFeedBackDataEntity>();
				getData();
			}
			else if((Integer)(tag) == HttpTagConstantUtils.FEEDBACK_GET && isSuccess(statusCode)){
				if(result != null ){
					
					ArrayList<SuggestionFeedBackDataEntity> list = (ArrayList<SuggestionFeedBackDataEntity>)result ;
					if(list != null && list.size() > 0){
						suggestionDataEntity.addAll(list);
						int replay = 0 ;
						for (SuggestionFeedBackDataEntity entity : suggestionDataEntity) {
							if(entity.getType() == 1){
								replay ++ ;
							}
						}
						mAdapter = new SuggestionFeedBackAdapter(suggestionDataEntity, this);
						realListView.setAdapter(mAdapter);
						if(pageIndex == 1){
							realListView.setSelection((pageIndex -1) * 10 + 1 );
						}else{
							EasyLog.e("replay = "+ replay);
							realListView.setSelection((pageIndex -1) * 10 + replay-2 );
						}
						
						mAdapter.notifyDataSetChanged();
					}else{
						AppToast.toastMsgCenter(getApplicationContext(),"~_~没有更多了").show();
					}
					
				}
				
			}
		} catch (Exception e) {
			
		} finally {
			new Handler().post(new Runnable() {
				@Override
				public void run() {
					if (null != pullToRefreshListView) {
						pullToRefreshListView.onRefreshComplete();
					}
				  if(null!=dialogUploadImage && dialogUploadImage.isShowing()){
					  dialogUploadImage.dismiss();
				  }
				}
			});
		}
	}
	
	@Override
	protected String getActivityName() {
		return "意见反馈";
	}



}

package com.beabox.hjy.tt;

import java.util.ArrayList;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.app.base.entity.ParentCommentEntity;
import com.app.base.entity.PraiseJoinEntity;
import com.app.base.entity.SelectedDetailEntity;
import com.app.base.entity.UserEntity;
import com.app.base.init.BaseActivity;
import com.app.base.init.MyApplication;
import com.app.service.GetHomeCommentService;
import com.app.service.GetSelectedDetailService;
import com.app.service.PostHomeCommentService;
import com.avoscloud.chat.service.UserService;
import com.avoscloud.chat.util.PhotoUtils;
import com.base.app.utils.DBService;
import com.base.app.utils.DeviceUtil;
import com.base.app.utils.ImageParamSetter;
import com.base.app.utils.StringUtil;
import com.base.dialog.lib.Effectstype;
import com.base.dialog.lib.NiftyDialogBuilder;
import com.base.pulltorefresh.view.PullToRefreshBase;
import com.base.pulltorefresh.view.PullToRefreshBase.Mode;
import com.base.pulltorefresh.view.PullToRefreshBase.OnRefreshListener;
import com.base.pulltorefresh.view.PullToRefreshListView;
import com.base.service.action.constant.HttpTagConstantUtils;
import com.base.service.impl.HttpClientUtils;
import com.base.supertoasts.util.AppToast;
import com.idongler.widgets.CircleImageView;
import com.skinrun.trunk.adapter.CommentAdapter;
import com.skinrun.trunk.adapter.JoinUsersPicsDataListAdapter;
import com.skinrun.trunk.test.popuwindow.MaskHintPopuwindow;
import com.skinrun.trunk.test.popuwindow.MaskHintPopuwindow.onCloseListener;
import com.umeng.message.PushAgent;

@SuppressWarnings("serial")
public class SelectedSecondActivity extends BaseActivity implements OnRefreshListener<ListView>, onCloseListener {
	private CircleImageView civUserImage;
	private TextView tvNickName,tvUserState,tvJiShiNum,tvChiXuNum,tvChangXiaoNum,tvPublishContent,tvPublishTime,tvPraiseNum,tvCommentNum;
	private ImageView ivJiShiRaise,ivChiXuRaise,ivChangXiaoRaise,ivPraise,ivBig;
	private GridView gvUserIcon;
	private PullToRefreshListView pullToRefreshListView;
	private ListView realListView;
	private CommentAdapter mAdapter;
	private JoinUsersPicsDataListAdapter gAdapter;

	private ArrayList<ParentCommentEntity> data=new ArrayList<ParentCommentEntity>();
	private ArrayList<PraiseJoinEntity> arrayList=new ArrayList<PraiseJoinEntity>();
	
	private int pageIndex=1,pageSize=10;
	
	private String TAG="SelectedSecondActivity";
	
	InputMethodManager imm;
	NiftyDialogBuilder dialogUploadImage;
	
	private Button mBtnSend;
	private ImageView btn_set_mode_keyboard;
	private EditText mEditTextContent;
	private View ll_facechoose;// 表情区域
	
	private String action="comments";
	private long source_id;
	private String source_type="";
	private String sendAction="createComment";
	
	private SelectedDetailEntity selectedDetailEntity;
	
	private MaskHintPopuwindow maskHintPopuwindow;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		MyApplication.getInstance().addActivity(this);
    	PushAgent.getInstance(this).onAppStart();
		super.onCreate(savedInstanceState);
		try{
			source_id=Long.parseLong(getIntent().getStringExtra("ID"));
			
			
			source_type=getIntent().getStringExtra("SOURSE_TYPE");
			
			setContentView(R.layout.comment_layout);
			findViewById(R.id.backComment).setOnClickListener(this);
			
			setupView();
			addListener();
			
			imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			getWindow().setSoftInputMode(
					WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void initView(){
		mBtnSend=(Button)findViewById(R.id.btn_send);
		btn_set_mode_keyboard=(ImageView)findViewById(R.id.btn_set_mode_keyboard);
		mEditTextContent=(EditText)findViewById(R.id.et_sendmessage);
		ll_facechoose = findViewById(R.id.ll_facechoose);
		
		pullToRefreshListView=(PullToRefreshListView)findViewById(R.id.commentListView);
		realListView=pullToRefreshListView.getRefreshableView();
		
		pullToRefreshListView.setOnRefreshListener(this);
		pullToRefreshListView.setMode(Mode.PULL_UP_TO_REFRESH);
		
		ParentCommentEntity p=new ParentCommentEntity();
		p.setId("-1");
		
		data.add(p);
		mAdapter=new CommentAdapter(data, this);
		initHeadView();
		
		
		realListView.setAdapter(mAdapter);
		
		
	}
	private void initHeadView(){
		View view=LayoutInflater.from(this).inflate(R.layout.selected_second_activity, null);
		
		maskHintPopuwindow=new MaskHintPopuwindow(this);
		
		civUserImage=(CircleImageView)view.findViewById(R.id.civUserImage);
		tvNickName=(TextView)view.findViewById(R.id.tvNickName);
		tvUserState=(TextView)view.findViewById(R.id.tvUserState);
		tvJiShiNum=(TextView)view.findViewById(R.id.tvJiShiNum);
		
		tvChiXuNum=(TextView)view.findViewById(R.id.tvChiXuNum);
		tvChangXiaoNum=(TextView)view.findViewById(R.id.tvChangXiaoNum);
		tvPublishContent=(TextView)view.findViewById(R.id.tvPublishContent);
		tvPublishTime=(TextView)view.findViewById(R.id.tvPublishTime);
		tvPraiseNum=(TextView)view.findViewById(R.id.tvPraiseNum);
		tvCommentNum=(TextView)view.findViewById(R.id.tvCommentNum);
		
		ivJiShiRaise=(ImageView)view.findViewById(R.id.ivJiShiRaise);
		ivChiXuRaise=(ImageView)view.findViewById(R.id.ivChiXuRaise);
		ivChangXiaoRaise=(ImageView)view.findViewById(R.id.ivChangXiaoRaise);
		ivPraise=(ImageView)view.findViewById(R.id.ivPraise);
		ivBig=(ImageView)view.findViewById(R.id.ivBig);
		
		gvUserIcon=(GridView)view.findViewById(R.id.gvUserIcon);
		
		view.findViewById(R.id.second_mask_hint).setOnClickListener(this);
		
		realListView.addHeaderView(view);
		
		loadData();
		loadDetail();
	}
	
	private void setValue(){
		
		gAdapter=new JoinUsersPicsDataListAdapter(arrayList, this);
		gvUserIcon.setAdapter(gAdapter);
		
		
		try{
			if(selectedDetailEntity!=null){
				
				/*if (entity.getUpload_img() != null&& !entity.getUpload_img().equals("")) {
					//UrlImageViewHelper.setUrlDrawable(ivBig,entity.getUpload_img());
					UserService.imageLoader.displayImage(""+entity.getUpload_img(), ivBig,
							PhotoUtils.articleImageOptions);
				}*/
				
				try{
					if (selectedDetailEntity.getUpload_img() != null&& !selectedDetailEntity.getUpload_img().equals("")) {
						UserService.imageLoader.displayImage(""+selectedDetailEntity.getUpload_img(), ivBig,
								PhotoUtils.articleImageOptions);
					//	KJBitmap.create().display(ivBig, entity.getUpload_img());
						
						if(selectedDetailEntity.getImg_width()!=0&&selectedDetailEntity.getImg_height()!=0){
							ImageParamSetter.setImageXFull(this,ivBig, selectedDetailEntity.getImg_height()*1.0/selectedDetailEntity.getImg_width(),0);
						}else{
							ImageParamSetter.setImageXFull(this,ivBig, 0,0);
						}
						
					}else{
						//ImageParamSetter.setImageXFull(this,ivBig, 0,0);
					}
				}catch(Exception e){
					e.printStackTrace();
				}
				
				if(selectedDetailEntity.getUser_avatar()!=null&&!selectedDetailEntity.getUser_avatar().equals("")){
					//UrlImageViewHelper.setUrlDrawable(civUserImage, entity.getUser_avatar(),R.drawable.my_pic_default);
					UserService.imageLoader.displayImage(""+ selectedDetailEntity.getUser_avatar(), civUserImage,
							PhotoUtils.myPicImageOptions);
				}else if(selectedDetailEntity.getUid()!=null&&!selectedDetailEntity.getUid().equals("")){
					/*UrlImageViewHelper.setUrlDrawable(civUserImage, HttpClientUtils.USER_IMAGE_URL+""+
							StringUtil.getPathByUid(String.valueOf(entity.getUid())), R.drawable.my_pic_default);*/
					UserService.imageLoader.displayImage(""+ HttpClientUtils.USER_IMAGE_URL+""+
							StringUtil.getPathByUid(String.valueOf(selectedDetailEntity.getUid())), civUserImage,
							PhotoUtils.myPicImageOptions);
				}
			
				
				tvNickName.setText(selectedDetailEntity.getNickname());
				tvUserState.setText(selectedDetailEntity.getSkin_type_name()+" "+selectedDetailEntity.getAge()+"岁");
				tvJiShiNum.setText(selectedDetailEntity.getTestbefore1()+"%");
				tvChiXuNum.setText(selectedDetailEntity.getTestbefore2()+"%");
				tvChangXiaoNum.setText(selectedDetailEntity.getTestbefore3()+"%");
				tvPublishContent.setText(selectedDetailEntity.getRemark()+"");
				tvPublishTime.setText("发布于 "+selectedDetailEntity.getAddtime()+"");
				
				
				tvPraiseNum.setText(selectedDetailEntity.getPraise_count()+"");
				tvCommentNum.setText("（共有"+selectedDetailEntity.getComments_count()+"条评论）");
				
				if(selectedDetailEntity.getTestbefore1()<0){
					ivJiShiRaise.setImageDrawable(getResources().getDrawable(R.drawable.facial_result_down));
				}
				if(selectedDetailEntity.getTestbefore2()<0){
					ivChiXuRaise.setImageDrawable(getResources().getDrawable(R.drawable.facial_result_down));
				}
				if(selectedDetailEntity.getTestbefore3()<0){
					ivChangXiaoRaise.setImageDrawable(getResources().getDrawable(R.drawable.facial_result_down));
				}
				
				
				ivPraise.setImageDrawable(getResources().getDrawable(R.drawable.home_support_1));
				
			}
		}catch(Exception e){
			
		}
		
		
	}
	
	
	private void replyAction(){
		final String content=mEditTextContent.getText().toString();
		if(!DeviceUtil.isOnline()){
			AppToast.toastMsgCenter(getApplicationContext(), R.string.ERROR_404).show();
			return ;
		}
		
		if(StringUtil.isBlank(content)){
			AppToast.toastMsgCenter(getApplicationContext(), R.string.no_comment).show();
			return ;
		}
		
		UserEntity user=DBService.getUserEntity();
		
		if(user==null){
			AppToast.toastMsg(this, "请您先登录");
			return;
		}
		
		final String  token=user.getToken();
		
		dialogUploadImage = NiftyDialogBuilder.getInstance(this,
				R.layout.dialog_login_layout);
		final View text = LayoutInflater.from(this).inflate(
				R.layout.dialog_login_view, null);
		TextView t = (TextView) text.findViewById(R.id.loading_text);
		t.setText("发布中....");
		dialogUploadImage.withTitle(null).withMessage(null)
				.withEffect(Effectstype.Fadein).withDuration(100)
				.isCancelableOnTouchOutside(false)
				.setCustomView(text, this.getApplicationContext()).show();
		
		//提交评论
		new PostHomeCommentService(SelectedSecondActivity.this, HttpTagConstantUtils.POST_HOME_COMMENT, SelectedSecondActivity.this)
		.doComment(token, sendAction, source_type, source_id, content);
		
		pageIndex=1;
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				
				loadData();
			}
		}, 500);
		
	}
	
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		
		case R.id.second_mask_hint:
			try{
				Log.e(TAG, "==========second_mask_hint");
				maskHintPopuwindow.show(this);
			}catch(Exception e){
				e.printStackTrace();
			}
			break;
		
		case R.id.backComment:
			finish();
			break;
		
		
		case R.id.root_comment_container:
			imm.hideSoftInputFromWindow(mEditTextContent.getWindowToken(), 0);
			break;
		case R.id.btn_send:
			if (!isLogin()) {
				AppToast.toastMsgCenter(getApplicationContext(), R.string.please_login).show();
				Intent i = new Intent(SelectedSecondActivity.this,LoginActivity.class);
				startActivity(i);
				return;
			}
			
			findViewById(R.id.btn_face).setVisibility(View.VISIBLE);
			btn_set_mode_keyboard.setVisibility(View.INVISIBLE);
			
			if (ll_facechoose.getVisibility() == View.VISIBLE) {
				ll_facechoose.setVisibility(View.GONE);
				
			} else {// 如果出现表情框，弹出软键盘
				ll_facechoose.setVisibility(View.VISIBLE);
				imm.hideSoftInputFromWindow(mEditTextContent.getWindowToken(), 0);
			}
			
			replyAction();
			
			
			break;
		case R.id.btn_set_mode_keyboard:
			
			findViewById(R.id.btn_face).setVisibility(View.VISIBLE);
			btn_set_mode_keyboard.setVisibility(View.INVISIBLE);
			// 隐藏表情选择框
			if (ll_facechoose.getVisibility() == View.VISIBLE) {
				ll_facechoose.setVisibility(View.GONE);
				imm.showSoftInput(mEditTextContent,
						InputMethodManager.SHOW_FORCED);
			} else {// 如果出现表情框，弹出软键盘
				ll_facechoose.setVisibility(View.VISIBLE);
				imm.hideSoftInputFromWindow(mEditTextContent.getWindowToken(), 0);
			}
			break;
		case R.id.btn_face:
			imm.showSoftInput(v,InputMethodManager.SHOW_FORCED);    
			btn_set_mode_keyboard.setVisibility(View.VISIBLE);
			findViewById(R.id.btn_face).setVisibility(View.INVISIBLE);
			// 隐藏表情选择框
			if (ll_facechoose.getVisibility() == View.VISIBLE) {
				ll_facechoose.setVisibility(View.GONE);
				imm.showSoftInput(mEditTextContent,
						InputMethodManager.SHOW_FORCED);
			} else {// 如果出现表情框，弹出软键盘
				ll_facechoose.setVisibility(View.VISIBLE);
				imm.hideSoftInputFromWindow(mEditTextContent.getWindowToken(), 0);
				// changeInput();
			}
			break;
		case R.id.et_sendmessage:
			//findViewById(R.id.btn_face).setVisibility(View.VISIBLE);
			//btn_set_mode_keyboard.setVisibility(View.INVISIBLE);
			// 隐藏表情选择框
			if (ll_facechoose.getVisibility() == View.VISIBLE) {
				ll_facechoose.setVisibility(View.GONE);
				imm.showSoftInput(mEditTextContent,
						InputMethodManager.SHOW_FORCED);
			} else {// 如果出现表情框，弹出软键盘
				ll_facechoose.setVisibility(View.VISIBLE);
				imm.hideSoftInputFromWindow(mEditTextContent.getWindowToken(), 0);
				// changeInput();
			}
			break;

		}
	}
	@Override
	public void dataCallBack(Object tag, int statusCode, Object result) {
		try{
			switch((Integer)tag){
			case HttpTagConstantUtils.GET_SELECT_COMMENT:
				mEditTextContent.setText("");
				if(dialogUploadImage!=null){
					dialogUploadImage.dismiss();
				}
				
				pullToRefreshListView.onRefreshComplete();
					@SuppressWarnings("unchecked")
					ArrayList<ParentCommentEntity> tem=(ArrayList<ParentCommentEntity>)result;
					if(pageIndex==1){
						data.clear();
					}
					if(tem!=null){
						data.addAll(tem);
						mAdapter.notifyDataSetChanged();
					}else{
						data.add(new ParentCommentEntity());
						mAdapter.notifyDataSetChanged();
					}
					
				break;
			case HttpTagConstantUtils.POST_HOME_COMMENT:
				if(statusCode==200||statusCode==201){
					selectedDetailEntity.setComments_count(selectedDetailEntity.getComments_count()+1);
					tvCommentNum.setText("（共有"+selectedDetailEntity.getComments_count()+"条评论）");
				}
				
				break;
			case HttpTagConstantUtils.GET_SELECTED_DETAIL:
				selectedDetailEntity=(SelectedDetailEntity)result;
				if(selectedDetailEntity!=null){
					Log.e(TAG, "==========selectedDetailEntity:"+selectedDetailEntity.toString());
					setValue();
					if(selectedDetailEntity.getPraise_users()!=null&&selectedDetailEntity.getPraise_users().size()>0){
						arrayList.clear();
						arrayList.addAll(selectedDetailEntity.getPraise_users());
						gAdapter.notifyDataSetChanged();
					}
				}
				break;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	@Override
	public void setupView() {
		initView();
		
	}
	@Override
	public void addListener() {
		
		mBtnSend.setOnClickListener(this);
		findViewById(R.id.second_seleced_root).setOnClickListener(this);
		btn_set_mode_keyboard.setOnClickListener(this);
		findViewById(R.id.btn_face).setOnClickListener(this);
		mEditTextContent.setOnClickListener(this);
		
	}
	@Override
	public void sendMessageHandler(int messageCode) {
		
	}
	@Override
	protected String getActivityName() {
		return TAG;
	}
	
	private void loadData(){
		try{
			new GetHomeCommentService(this, HttpTagConstantUtils.GET_SELECT_COMMENT, this)
			.getComment(action,source_type, source_id, pageIndex, pageSize);
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	//加载点赞参与者
	private void loadDetail(){
		new GetSelectedDetailService(this, HttpTagConstantUtils.GET_SELECTED_DETAIL, this).doGet(source_id, "facemarkDetail");
	}

	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		pageIndex++;
		loadData();
	}

	@Override
	public void close() {
		maskHintPopuwindow.dismiss();
	}
}

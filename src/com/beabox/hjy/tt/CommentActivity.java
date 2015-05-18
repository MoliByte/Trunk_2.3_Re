package com.beabox.hjy.tt;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.app.base.entity.ParentCommentEntity;
import com.app.base.entity.UserEntity;
import com.app.base.init.BaseActivity;
import com.app.base.init.MyApplication;
import com.app.service.GetHomeCommentService;
import com.app.service.PostHomeCommentService;
import com.avos.avoscloud.LogUtil.log;
import com.base.app.utils.DBService;
import com.base.app.utils.DeviceUtil;
import com.base.app.utils.StringUtil;
import com.base.dialog.lib.Effectstype;
import com.base.dialog.lib.NiftyDialogBuilder;
import com.base.pulltorefresh.view.PullToRefreshBase;
import com.base.pulltorefresh.view.PullToRefreshBase.Mode;
import com.base.pulltorefresh.view.PullToRefreshBase.OnRefreshListener2;
import com.base.pulltorefresh.view.PullToRefreshListView;
import com.base.service.action.constant.HttpTagConstantUtils;
import com.base.supertoasts.util.AppToast;
import com.skinrun.trunk.adapter.CommentAdapter;
import com.umeng.message.PushAgent;

public class CommentActivity extends BaseActivity implements OnClickListener, OnRefreshListener2<ListView>{
	
	
	private PullToRefreshListView commentListView;
	
	InputMethodManager imm;
	
	private Button mBtnSend;
	private ImageView btn_set_mode_keyboard;
	private EditText mEditTextContent;
	NiftyDialogBuilder dialogUploadImage;
	private View ll_facechoose;// 表情区域
	private ListView realListView;
	private CommentAdapter mAdapter;
	private ArrayList<ParentCommentEntity> data=new ArrayList<ParentCommentEntity>();
	
	private int pageIndex=1,pageSize=10;
	
	private String action="comments";
	private String source_id="";
	private String source_type="";
	private String sendAction="createComment";
	
	private String TAG="CommentActivity";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		MyApplication.getInstance().addActivity(this);
		PushAgent.getInstance(this).onAppStart();
		super.onCreate(savedInstanceState);
		
		source_id=getIntent().getStringExtra("SOURSE_ID");
		source_type=getIntent().getStringExtra("SOURSE_TYPE");
		
		
		setContentView(R.layout.comment_layout);
		setupView();
		addListener();
		
		loadData();
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.backComment:
			finish();
			break;
		case R.id.root_comment_container:
			imm.hideSoftInputFromWindow(mEditTextContent.getWindowToken(), 0);
			break;
		case R.id.btn_send:
			if (!isLogin()) {
				AppToast.toastMsgCenter(getApplicationContext(), R.string.please_login).show();
				Intent i = new Intent(CommentActivity.this,LoginActivity.class);
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
		new PostHomeCommentService(CommentActivity.this, HttpTagConstantUtils.POST_HOME_COMMENT, CommentActivity.this)
		.doComment(token, sendAction, source_type, Integer.parseInt(source_id), content);
		
		pageIndex=1;
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				
				loadData();
			}
		}, 100);
		
	}
	
	
	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.activity_enter_from_left, R.anim.activity_exit_to_right);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void dataCallBack(Object tag, int statusCode, Object result) {
		
		try{
			switch((Integer)tag){
			case HttpTagConstantUtils.GET_HOME_COMMENT:
				mEditTextContent.setText("");
				if(dialogUploadImage!=null){
					dialogUploadImage.dismiss();
				}
				
				if(statusCode==200||statusCode==201){
					@SuppressWarnings("unchecked")
					ArrayList<ParentCommentEntity> tem=(ArrayList<ParentCommentEntity>)result;
					log.e(TAG, "=============评论TEM的长度："+tem.size());
					if(pageIndex==1){
						data.clear();
					}
					if(tem!=null){
						data.addAll(tem);
						mAdapter.notifyDataSetChanged();
					}
					
				}
				
				break;
			case HttpTagConstantUtils.POST_HOME_COMMENT:
				
				
				
				break;
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			commentListView.onRefreshComplete();
		}
	}

	@Override
	public void setupView() {
		commentListView=(PullToRefreshListView)findViewById(R.id.commentListView);
		commentListView.setMode(Mode.BOTH);
		commentListView.setOnRefreshListener(this);
		
		realListView=commentListView.getRefreshableView();
		
		mAdapter=new CommentAdapter(data, this);
		realListView.setAdapter(mAdapter);
		
		mBtnSend=(Button)findViewById(R.id.btn_send);
		btn_set_mode_keyboard=(ImageView)findViewById(R.id.btn_set_mode_keyboard);
		mEditTextContent=(EditText)findViewById(R.id.et_sendmessage);
		ll_facechoose = findViewById(R.id.ll_facechoose);
	}

	@Override
	public void addListener() {
		findViewById(R.id.backComment).setOnClickListener(this);
		mBtnSend.setOnClickListener(this);
		findViewById(R.id.root_comment_container).setOnClickListener(this);
		btn_set_mode_keyboard.setOnClickListener(this);
		findViewById(R.id.btn_face).setOnClickListener(this);
		mEditTextContent.setOnClickListener(this);
	}

	@Override
	public void sendMessageHandler(int messageCode) {
		
	}

	@Override
	protected String getActivityName() {
		return "CommentActivity";
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		pageIndex=1;
		loadData();
		
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		pageIndex++;
		loadData();
	}
	private void loadData(){
		try{
			new GetHomeCommentService(this, HttpTagConstantUtils.GET_HOME_COMMENT, this)
			.getComment(action,source_type, Integer.parseInt(source_id), pageIndex, pageSize);
			
		}catch(Exception e){
			
		}
	}
}

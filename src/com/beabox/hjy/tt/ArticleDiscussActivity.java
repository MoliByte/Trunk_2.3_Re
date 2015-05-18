package com.beabox.hjy.tt;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.kymjs.aframe.utils.StringUtils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.app.base.entity.ArticleCommentEntity;
import com.app.base.entity.ArticleDiscussEntity;
import com.app.base.entity.ArticleReplayEntity;
import com.app.base.entity.HomeDataEntity;
import com.app.base.entity.PraiseJoinEntity;
import com.app.base.init.ACache;
import com.app.base.init.BaseActivity;
import com.app.base.init.MyApplication;
import com.app.service.ArticleDetailAsynTaskService;
import com.app.service.GetArticleCommentListAsynTaskService;
import com.app.service.PostArticleCommentAsynTaskService;
import com.app.service.PostCommentPraiseAsynTaskService;
import com.app.service.PostPraiseAsynTaskService;
import com.app.service.PostUserShareService;
import com.avos.avoscloud.LogUtil.log;
import com.avoscloud.chat.base.SmileUtils;
import com.avoscloud.chat.service.UserService;
import com.avoscloud.chat.util.PhotoUtils;
import com.base.app.utils.StringUtil;
import com.base.app.utils.UMShareUtil;
import com.base.app.utils.UMShareUtil.ShareAction;
import com.base.dialog.lib.Effectstype;
import com.base.dialog.lib.NiftyDialogBuilder;
import com.base.facedemo.FaceRelativeLayout;
import com.base.pulltorefresh.view.PullToRefreshBase;
import com.base.pulltorefresh.view.PullToRefreshBase.Mode;
import com.base.pulltorefresh.view.PullToRefreshBase.OnRefreshListener2;
import com.base.pulltorefresh.view.PullToRefreshListView;
import com.base.service.action.constant.HttpTagConstantUtils;
import com.base.service.impl.HttpAysnResultInterface;
import com.base.service.impl.HttpClientUtils;
import com.base.supertoasts.util.AppToast;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.skinrun.trunk.adapter.JoinUsersPicsDataListAdapter;
import com.umeng.message.PushAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.controller.listener.SocializeListeners.SocializeClientListener;

@SuppressWarnings("serial")
public class ArticleDiscussActivity extends BaseActivity implements
		OnItemClickListener,OnTouchListener {
	private static final String TAG = "ArticleDiscussActivity";
	private ArrayList<ArticleCommentEntity> article_comment_list = new ArrayList<ArticleCommentEntity>();
	private ArticleListAdapter adapter;
	private PullToRefreshListView pullToRefreshListView;
	private ImageView backBtn;
	private ListView realListView;
	private EditText comment_text;
	private SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
	InputMethodManager imm;

	HomeDataEntity articl;
	private Button mBtnSend;
	private ImageView btn_set_mode_keyboard;
	private EditText mEditTextContent;
	NiftyDialogBuilder dialogUploadImage;

	private View ll_facechoose;// 表情区域

	int pageIndex = 1;

	private static long current_to_uid = -1;//
	private static long current_comment_id = -1;
	
	private static int current_position = 0 ;
	
	private long articleId;
	private ArticleDiscussEntity result_entity= new ArticleDiscussEntity() ;
 ;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		PushAgent.getInstance(this).onAppStart();
		MyApplication.getInstance().addActivity(this);
		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.article_main_list);
		
		articleId=getIntent().getLongExtra("articleId", -1);
		
		if(articleId==-1){
			try{
				Bundle bun = getIntent().getExtras();
				articleId=Long.parseLong(bun.getString("id"));
				log.e(TAG, "========articleId:"+articleId+"  ");
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		current_position = 0 ;
		setupView();
		addListener();
	}

	@Override
	public void setupView() {
		pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.article_list_view);
		findViewById(R.id.popupShareBtn).setOnClickListener(this);
		mBtnSend = (Button) findViewById(R.id.btn_send);
		mBtnSend.setOnClickListener(this);
		mEditTextContent = (EditText) findViewById(R.id.et_sendmessage);
		backBtn = (ImageView) findViewById(R.id.backBtn);
		btn_set_mode_keyboard = (ImageView) findViewById(R.id.btn_set_mode_keyboard);
		ll_facechoose = findViewById(R.id.ll_facechoose);
		mEditTextContent.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean isOPen) {
				ll_facechoose.setVisibility(View.GONE);
				imm.showSoftInput(mEditTextContent,
						InputMethodManager.SHOW_FORCED);
			}
		});

		// mEditTextContent.setOnClickListener(this);
		btn_set_mode_keyboard.setOnClickListener(this);
		findViewById(R.id.btn_face).setOnClickListener(this);

		// imm.hideSoftInputFromWindow(comment_text.getWindowToken(), 0);//强制隐藏
		realListView = pullToRefreshListView.getRefreshableView();
		homeListViewAddHeader();
		pullToRefreshListView.setMode(Mode.BOTH);// 下拉加载更多
		pullToRefreshListView
				.setOnRefreshListener(new OnRefreshListener2<ListView>() {
					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						String label = format.format(new Date());
						refreshView.getLoadingLayoutProxy()
								.setLastUpdatedLabel("最后更新时间:" + label);
						pageIndex = 1 ;
						if(article_comment_list != null){
							article_comment_list.clear();
						}
						article_comment_list = new ArrayList<ArticleCommentEntity>();
						getData();
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						pageIndex++;
						getCommentList();
					}

				});

		Runnable task = new Runnable() {

			@Override
			public void run() {
				// FaceConversionUtil.getInstace().getFileText(getApplication());
				getData();
			}
		};

		new Handler().post(task);
		findViewById(R.id.root_article_container).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				imm.hideSoftInputFromWindow(mEditTextContent.getWindowToken(), 0); 
				resetReplay();
			}
		});
	}

	/**
	 * 添加ListView头部信息
	 **********************************/

	private TextView article_support_count,people,guan_jia_name,article_public_time;
	private WebView article_content ;
	private ImageView article_img,home_support_1,no_support_btn ,guan_jiao_pic;
	
	private GridView join_user_pic ;
	private JoinUsersPicsDataListAdapter joinUsersPicsDataListAdapter ;
	Map<Integer,String> join_users = new HashMap<Integer,String>();
	
	
	TextView title ;
	public void homeListViewAddHeader() {
		try {
			View view = LayoutInflater.from(this).inflate(
					R.layout.article_main_list_view_head, null);
			 title = (TextView) view.findViewById(R.id.title);
			guan_jia_name = (TextView) view.findViewById(R.id.guan_jia_name);
			article_public_time = (TextView) view.findViewById(R.id.article_public_time);
			
			people = (TextView) view.findViewById(R.id.people);
			article_img = (ImageView) view.findViewById(R.id.article_img);
			guan_jiao_pic = (ImageView) view.findViewById(R.id.guan_jiao_pic);
			
//			UrlImageViewHelper.setUrlDrawable(
//					guan_jiao_pic,
//					HttpClientUtils.AUTHOR_IMAGE_URL+""+article.getAuthor_img(),
//					R.drawable.guan_jian_pic);
			
			UserService.imageLoader.displayImage(
					"http://a.skinrun.cn/userpic/1423707249169.jpg", guan_jiao_pic,
					PhotoUtils.guanJiaImageOptions);
			
			home_support_1 = (ImageView) view.findViewById(R.id.home_support_1);
			no_support_btn = (ImageView) view.findViewById(R.id.no_support_btn);
			article_content = (WebView) view.findViewById(R.id.article_content);
			join_user_pic = (GridView) view.findViewById(R.id.join_user_pic);
			join_user_pic.setSelector(new ColorDrawable(Color.TRANSPARENT));
			View article_support = view.findViewById(R.id.article_support);
			
			//article_content.setText("" + article.getContent());
			article_support.setOnClickListener(this);
			article_support_count = (TextView) view
					.findViewById(R.id.article_support_count);// 点赞数目
			
			realListView.addHeaderView(view);
			
			article_content.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View arg0, MotionEvent arg1) {
					return true;
				}
			});
		} catch (Exception e) {
			
		}
		

	}

	protected void getData() {
		try {

			long article_id = articleId;
			String token = aCache.getAsString("Token");
			// 获取文章评论列表
			new ArticleDetailAsynTaskService(getApplicationContext(),
					HttpTagConstantUtils.ARTICLE_DETAIL, this)
					.doArticleAndCommentsList(article_id, pageIndex,token);

		} catch (Exception e) {
			Log.e(TAG, ""+e.toString());
		}
	}
	
	protected void getCommentList() {
		try {
			
			long action_id = articleId;
			// 获取专题评论列表
			new GetArticleCommentListAsynTaskService(getApplicationContext(),
					HttpTagConstantUtils.ARTICLE_COMMENT_LIST, this)
			.doArticleCommentsList(action_id, pageIndex);
			
		} catch (Exception e) {
			
		}
	}
	
	
	@Override
	public void dataCallBack(Object tag, int statusCode, Object result) {
		try {
			if (dialogUploadImage != null) {
				dialogUploadImage.dismiss();
			}

			if ((Integer) tag == HttpTagConstantUtils.ARTICLE_DETAIL) {// 文章详情
				pullToRefreshListView.onRefreshComplete();
				if (isSuccess(statusCode)) {
					result_entity = (ArticleDiscussEntity) result;
					//article_content.loadData(new String(result_entity.getContent().replaceAll("\r", "").replaceAll("\n", "").getBytes("UTF-8")), "text/html; charset=UTF-8", null);
					article_content.loadDataWithBaseURL(null,new String(result_entity.getContent().replaceAll("\r", "").replaceAll("\n", "").getBytes("UTF-8")), "text/html",  "utf-8", null);
					join_users = result_entity.getJoin_users();
					ArrayList<PraiseJoinEntity> list = new ArrayList<PraiseJoinEntity>();
					for (Integer key : join_users.keySet()) {
						PraiseJoinEntity p=new PraiseJoinEntity();
						p.setUid(key+"");
						list.add(p);
					}
					people.setText(""+result_entity.getJoin_users().size()+"人参与");
					joinUsersPicsDataListAdapter = new JoinUsersPicsDataListAdapter(list,this.getApplicationContext());
					
					
					
					article_public_time.setText(""+result_entity.getPublic_time());
					guan_jia_name.setText(""+result_entity.getAuthor_nick());
					title.setText("" + result_entity.getTitle());
					article_support_count.setText(""+result_entity.getPraise_count());
					
					
					join_user_pic.setAdapter(joinUsersPicsDataListAdapter);
					joinUsersPicsDataListAdapter.notifyDataSetChanged();
					
					article_support_count.setText(""+result_entity.getPraise_count());
					
					int praise_status = result_entity.getPraise_status() ;
					if(praise_status == 1){
						home_support_1.setVisibility(View.VISIBLE);
						no_support_btn.setVisibility(View.GONE);
					}else{
						home_support_1.setVisibility(View.GONE);
						no_support_btn.setVisibility(View.VISIBLE);
					}
					
					Log.e(TAG, "image_url-------->"+HttpClientUtils.IMAGE_URL
							+ result_entity.getImgURL());
					
					displayImage(article_img, "", HttpClientUtils.IMAGE_URL+ result_entity.getImgURL());
					//article_content.setText(StringUtil.ToDBC("" +  Html.fromHtml(result_entity.getContent())));
					/*article_content.setText(StringUtil.ToDBC("" +  Html.fromHtml(result_entity.getContent(),new ImageGetter() {
						
						@Override
						public Drawable getDrawable(String source) {
							 Drawable drawable = null;
			                 drawable = Drawable.createFromPath(source);  // Or fetch it from the URL
			                  // Important
			                 drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable
			                                 .getIntrinsicHeight());
			                 return drawable; 
						}
					},null)));*/
					
					ArrayList<ArticleCommentEntity> article_comment_list = result_entity
							.getCommentList();
					
					if (article_comment_list != null
							&& article_comment_list.size() > 0) {
						this.article_comment_list = article_comment_list ;
						adapter = new ArticleListAdapter(
								article_comment_list,
								getApplicationContext());
						realListView.setAdapter(adapter);
						current_position = current_position>0?current_position:0 ;
					}else{
						adapter = new ArticleListAdapter(
								this.article_comment_list,
								getApplicationContext());
						realListView.setAdapter(adapter);
					}
					realListView.setSelection(current_position);
					adapter.notifyDataSetChanged();

				} else {
					AppToast.toastMsgCenter(getApplicationContext(),
							getString(R.string.ERROR_404), 1500).show();
				}
			} else if ((Integer) tag == HttpTagConstantUtils.POST_COMMENT) {// 评论文章
				if (isSuccess(statusCode)) {
					current_position = 2 ;
					AppToast.toastMsgCenter(getApplicationContext(),"发布成功!").show();
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							mEditTextContent.setText("");
							getData();
						}
					});
					
				} else {
					AppToast.toastMsgCenter(getApplicationContext(),
							getString(R.string.ERROR_404), 1500).show();
				}
			} 
			else if ((Integer) tag == HttpTagConstantUtils.POST_COMMENT_REPLAY) {// 回复评论
				if (isSuccess(statusCode)) {
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							mEditTextContent.setText("");
							getData();
						}
					});
					
				} else {
					AppToast.toastMsgCenter(getApplicationContext(),
							getString(R.string.ERROR_404), 1500).show();
				}
			}
			else if ((Integer) tag == HttpTagConstantUtils.POST_PRAISE) {// 文章点赞
				if (isSuccess(statusCode)) {
					int praise_status = (Integer)result ;
					if(praise_status == 100){
						home_support_1.setVisibility(View.VISIBLE);
						no_support_btn.setVisibility(View.GONE);
						this.article_support_count.setText(""+(((Integer.valueOf(article_support_count.getText()+""))+1)));
					}else{
						home_support_1.setVisibility(View.GONE);
						no_support_btn.setVisibility(View.VISIBLE);
						int count =  (Integer.valueOf(article_support_count.getText() + "") - 1) ;
						if(count >=0){
							this.article_support_count.setText(""+ count);
						}else{
							this.article_support_count.setText("0");
						}
						
					}
					//article_support_count.setText(""+(((Integer.valueOf(article_support_count.getText()+""))+1)));
					//getData();
				} else {
					//AppToast.toastMsgCenter(getApplicationContext(),getString(R.string.ERROR_404), 1500).show();
				}
			}
			
			else if ((Integer) tag == HttpTagConstantUtils.ARTICLE_COMMENT_LIST) {// 专题评论列表
				ArrayList<ArticleCommentEntity> article_comment_list = (ArrayList<ArticleCommentEntity>) result;
				if (isSuccess(statusCode)) {
					if (article_comment_list != null) {
						if(article_comment_list.size() > 0){
							this.article_comment_list.addAll(article_comment_list);
							adapter = new ArticleListAdapter(this.article_comment_list,getApplicationContext());
							realListView.setAdapter(adapter);
							realListView.setSelection((pageIndex-1)*10+3);
							adapter.notifyDataSetChanged();
						}else if(article_comment_list.size() == 0){
							AppToast.toastMsgCenter(getApplicationContext(),"~_~没有更多了").show();
						}
					}else if(article_comment_list == null){
						AppToast.toastMsgCenter(getApplicationContext(),"~_~没有更多了").show();
					}
					
				}else{
					//AppToast.toastMsgCenter(getApplicationContext(),"~_~没有更多了").show();
				}
				
			}
		} catch (Exception e) {
			Log.e(TAG, ">>>"+e.toString());

		} finally {
			if (pullToRefreshListView != null) {
				pullToRefreshListView.onRefreshComplete();
			}
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					if (dialogUploadImage != null) {
						dialogUploadImage.dismiss();
					}
				}
			}, 1000);
		}

	}
	
	void displayImage(ImageView imageView, String localPath,
			String url){
		File file = new File(localPath);
		ImageLoader imageLoader = UserService.imageLoader;
		if (file.exists()) {
			imageLoader.displayImage("file://" + localPath, imageView,
					PhotoUtils.articleImageOptions);
		} else {
			imageLoader.displayImage(url, imageView,
					PhotoUtils.articleImageOptions);
		}
	}

	@Override
	public void addListener() {
		backBtn.setOnClickListener(this);
		pullToRefreshListView.setOnItemClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.backBtn:
			finish();
			break;
		case R.id.article_support:// 文章点赞
			if (!isLogin()) {
				AppToast.toastMsgCenter(getApplicationContext(), R.string.please_login).show();
				Intent i = new Intent(ArticleDiscussActivity.this,LoginActivity.class);
				startActivity(i);
				return;
			}
			praise_article();

			break;
		case R.id.btn_send:
			if (!isLogin()) {
				AppToast.toastMsgCenter(getApplicationContext(), R.string.please_login).show();
				Intent i = new Intent(ArticleDiscussActivity.this,LoginActivity.class);
				startActivity(i);
				return;
			}
			findViewById(R.id.btn_face).setVisibility(View.VISIBLE);
			btn_set_mode_keyboard.setVisibility(View.INVISIBLE);
			if (ll_facechoose.getVisibility() == View.VISIBLE) {
				ll_facechoose.setVisibility(View.GONE);
				/*imm.showSoftInput(mEditTextContent,
						InputMethodManager.SHOW_FORCED);*/
			} else {// 如果出现表情框，弹出软键盘
				ll_facechoose.setVisibility(View.VISIBLE);
				imm.hideSoftInputFromWindow(mEditTextContent.getWindowToken(), 0);
			}
			if (current_comment_id != -1 && current_to_uid != -1) {
				replay_someone();
			} else {
				replay_article();
			}
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
				// changeInput();
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
			
		case R.id.popupShareBtn:
			popupShare();
			break;

		default:
			break;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		current_position = 0;
		//getData();
	}

	private void praise_article() {
		final String token = aCache.getAsString("Token");
		new PostPraiseAsynTaskService(getApplicationContext(),
				HttpTagConstantUtils.POST_PRAISE,
				ArticleDiscussActivity.this).doPostArticlePraise(
						articleId,token);
	}
	
	private void replay_article() {
		final String comment = mEditTextContent.getText().toString();
		if(StringUtils.isEmpty(comment)){
			AppToast.toastMsg(getApplicationContext(), "请输入发布内容!").show();
			return ;
		}
		final String token = aCache.getAsString("Token");
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
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				new PostArticleCommentAsynTaskService(getApplicationContext(),
						HttpTagConstantUtils.POST_COMMENT,
						ArticleDiscussActivity.this).doPostArticleComment(
								articleId, result_entity.getAuthor_id(),
						comment, token);
			}
		}, 500);

		// Log.e(TAG, "发送内容>>>>>" + contString);
	}

	private void replay_someone() {
		final String comment = mEditTextContent.getText().toString();
		final String token = aCache.getAsString("Token");
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
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				new PostArticleCommentAsynTaskService(getApplicationContext(),
						HttpTagConstantUtils.POST_COMMENT_REPLAY,
						ArticleDiscussActivity.this).doPostComment(
						articleId, current_comment_id,
						current_to_uid, comment, token);
			}
		}, 500);

		// Log.e(TAG, "发送内容>>>>>" + contString);
	}

	@Override
	public void sendMessageHandler(int messageCode) {

	}

	public void resetReplay() {
		Log.e(TAG, "set hint empty.....");
		mEditTextContent.setHint("");
		current_to_uid = -1;
		current_comment_id = -1;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		findViewById(R.id.btn_face).setVisibility(View.VISIBLE);
		btn_set_mode_keyboard.setVisibility(View.INVISIBLE);
		if (ll_facechoose.getVisibility() == View.VISIBLE) {
			ll_facechoose.setVisibility(View.GONE);
			resetReplay();
			return true;
		} 
		
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& ((FaceRelativeLayout) findViewById(R.id.FaceRelativeLayout))
						.hideFaceView()) {

			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onItemClick(AdapterView<?> adapter, View v, int position,
			long _id) {
		if(imm.isActive()){
			imm.hideSoftInputFromWindow(mEditTextContent.getWindowToken(), 0); //强制隐藏键盘  
			resetReplay();
			return ;
		}
		// AppToast.toastMsgCenter(getApplicationContext(), ">>>" + position,
		// 2000).show();
		// Intent intent = new Intent(this,MasterInfoActivity.class);
		// startActivity(intent);
	}

	class ArticleListAdapter extends BaseAdapter {
		private final static String TAG = "ArticleListAdapter";
		private ArrayList<ArticleCommentEntity> arrayList;
		private Context context;
		private LayoutInflater layoutInflater;

		public ArticleListAdapter(ArrayList<ArticleCommentEntity> list,
				Context c) {
			arrayList = list;
			context = c;
			layoutInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			if (arrayList == null) {
				return 0;
			}
			return arrayList.size();
		}

		@Override
		public Object getItem(int position) {
			return arrayList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			final ArticleCommentEntity entity = arrayList.get(position);
			convertView = layoutInflater.inflate(
					R.layout.article_listview_item, null);
			if(entity.getId() == -1){
				convertView = layoutInflater.inflate(
						R.layout.empty_view, null);
				return convertView ;
			}

			View support_layout = convertView.findViewById(R.id.support_layout);
			View comment_layout = convertView.findViewById(R.id.comment_layout);

			TextView author = (TextView) convertView.findViewById(R.id.author);
			author.setText("" + entity.getFrom_nick());//评论作者

			TextView level = (TextView) convertView.findViewById(R.id.level);
			level.setText(""+StringUtil.getLevel(entity.getIntegral()));

			TextView time = (TextView) convertView.findViewById(R.id.time);
			time.setText("" + StringUtil.humanmizeTime(entity.getCreate_time()));

			TextView comment = (TextView) convertView
					.findViewById(R.id.comment);
			
//			SpannableString spannableString = FaceConversionUtil.getInstace()
//					.getExpressionString(context, entity.getContent());
//			comment.setText(spannableString);
			Spannable spannableString = SmileUtils.getSmiledText(context, entity.getContent());
			comment.setText(spannableString, BufferType.SPANNABLE);

			TextView support = (TextView) convertView
					.findViewById(R.id.support);
			support.setText("" + entity.getSupport_count());

			TextView comment_count = (TextView) convertView
					.findViewById(R.id.comment_count);
			comment_count.setText("" + entity.getComment_count());
			
			ImageView no_support_pic = (ImageView) convertView.findViewById(R.id.no_support_pic);
			ImageView support_pic = (ImageView) convertView.findViewById(R.id.support_pic);
			
			if(1 == entity.getPraise_status()){
				support_pic.setVisibility(View.VISIBLE);
				no_support_pic.setVisibility(View.GONE);
			}else{
				support_pic.setVisibility(View.GONE);
				no_support_pic.setVisibility(View.VISIBLE);
			}

			support_layout.setOnClickListener(new OnClickHandler(support,
					position,no_support_pic,support_pic));
			
			comment_layout.setOnClickListener(new OnClickHandler(comment_count,
					position,no_support_pic,support_pic));

			ImageView master_pic = (ImageView) convertView
					.findViewById(R.id.master_pic);
			
			//UrlImageViewHelper.setUrlDrawable(master_pic, HttpClientUtils.USER_IMAGE_URL+StringUtil.getPathByUid(String.valueOf(entity.getFrom_uid())),R.drawable.my_pic_default);// from_uid
			UserService.imageLoader.displayImage(
					HttpClientUtils.USER_IMAGE_URL
							+ StringUtil.getPathByUid(String.valueOf(entity
									.getFrom_uid())), master_pic,
					PhotoUtils.myPicImageOptions);

																	// 000/008/090/source.jpg
			LinearLayout comment_list_layout = (LinearLayout) convertView
					.findViewById(R.id.comment_list_layout);
			// LinearLayout.LayoutParams parms = new
			// LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);

			// comment_list_layout.setLayoutParams(parms);
			ArrayList<ArticleReplayEntity> arrayList = entity.getReplys();

			if (arrayList != null && arrayList.size() > 0) {
				convertView.findViewById(R.id.up_arrow).setVisibility(View.VISIBLE);
				comment_list_layout.removeAllViews();
				ArrayList<View> v = new ArrayList<View>();
				for (int j = 0; j < arrayList.size(); j++) {
					View convertView_ = LayoutInflater.from(context).inflate(
							R.layout.article_replay_list_view_item, null);
					final ArticleReplayEntity articleReplayEntity = arrayList.get(j);
					if (null != articleReplayEntity) {
						TextView from = (TextView) convertView_
								.findViewById(R.id.from);
						from.setText("" + articleReplayEntity.getFrom_nick());

						TextView to = (TextView) convertView_
								.findViewById(R.id.to);
						to.setText("" + entity.getFrom_nick()/*articleReplayEntity.getTo_nick()*/);

						TextView comment_ = (TextView) convertView_
								.findViewById(R.id.replay_comment);
						Spannable span_replay = SmileUtils.getSmiledText(context, articleReplayEntity.getContent());
						
						comment_.setText(span_replay, BufferType.SPANNABLE/*FaceConversionUtil.getInstace()
								.getExpressionString(context,
										articleReplayEntity.getContent())*/);
						v.add(convertView_);
						
						from.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View view) {
								if(checkAuthor(articleReplayEntity.getFrom_uid())){
									findViewById(R.id.btn_face).setVisibility(View.VISIBLE);
									btn_set_mode_keyboard.setVisibility(View.INVISIBLE);
									if (ll_facechoose.getVisibility() == View.VISIBLE) {
										ll_facechoose.setVisibility(View.GONE);
										imm.showSoftInput(mEditTextContent,
												InputMethodManager.SHOW_FORCED);
									} else {// 如果出现表情框，弹出软键盘
										ll_facechoose.setVisibility(View.VISIBLE);
										imm.hideSoftInputFromWindow(mEditTextContent.getWindowToken(), 0);
									}
									mEditTextContent.setHint("回复：" + articleReplayEntity.getFrom_nick());
									current_to_uid = articleReplayEntity.getFrom_uid();//entity.getTo_uid();//
									current_comment_id = entity.getId();
								}
								
							}
						});
						
						to.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View view) {
								if(checkAuthor(articleReplayEntity.getTo_uid())){
									findViewById(R.id.btn_face).setVisibility(View.VISIBLE);
									btn_set_mode_keyboard.setVisibility(View.INVISIBLE);
									if (ll_facechoose.getVisibility() == View.VISIBLE) {
										ll_facechoose.setVisibility(View.GONE);
										imm.showSoftInput(mEditTextContent,
												InputMethodManager.SHOW_FORCED);
									} else {// 如果出现表情框，弹出软键盘
										ll_facechoose.setVisibility(View.VISIBLE);
										imm.hideSoftInputFromWindow(mEditTextContent.getWindowToken(), 0);
									}
									mEditTextContent.setHint("回复：" + articleReplayEntity.getTo_nick());
									current_to_uid = articleReplayEntity.getTo_uid();
									current_comment_id = entity.getId();
								}
								
							}
						});

					}
				}
				for (int i = 0; i < v.size(); i++) {
					comment_list_layout.addView(v.get(i), i);
				}

			} else {
				comment_list_layout.setVisibility(View.GONE);
			}

			return convertView;
		}

		public boolean checkAuthor(long from_uid){
			try {
				if (!isLogin()) {
					AppToast.toastMsgCenter(getApplicationContext(), R.string.please_login).show();
					Intent i = new Intent(ArticleDiscussActivity.this,LoginActivity.class);
					startActivity(i);
					return false;
				}
				String uid = ACache.get(getApplicationContext()).getAsString("uid");
				if(from_uid == Integer.valueOf(uid)){
					AppToast.toastMsgCenter(getApplicationContext(), R.string.cant_replay_self).show();
					return false;
				}
				return true ;
			} catch (Exception e) {
				return false;
			}
		}
		
		// 处理回复
		class OnClickHandler implements View.OnClickListener,
				HttpAysnResultInterface {
			public TextView textView;
			ImageView no_support_pic ;
			ImageView support_pic ;
			public ArticleCommentEntity entity;
			public int position;

			public OnClickHandler(TextView textView, final int position,ImageView no_support_pic,ImageView support_pic) {
				this.textView = textView;
				this.no_support_pic = no_support_pic;
				this.support_pic = support_pic;
				this.position = position;
			}

			@Override
			public void onClick(View v) {
				if(!isLogin()){
					AppToast.toastMsgCenter(getApplicationContext(),"请登录!").show();
					Intent i = new Intent(ArticleDiscussActivity.this,LoginActivity.class);
					startActivity(i);
					return ;
				}
				current_position = position ;
				this.entity = arrayList.get(position);
				switch (v.getId()) {
				case R.id.support_layout:// 评论点赞
					//this.textView.setText(""+ (Integer.valueOf(textView.getText() + "") + 1));
					// AppToast.toastMsgCenter(getApplicationContext(),"support_layout---",
					// 2000).show();

					new PostCommentPraiseAsynTaskService(
							getApplicationContext(),
							HttpTagConstantUtils.POST_COMMENT_PRAISE,
							/*ArticleDiscussActivity.*/this).doPostCommentPraise(
							entity.getId(), aCache.getAsString("Token"));
					break;
				case R.id.comment_layout:// 评论回复
					// AppToast.toastMsgCenter(getApplicationContext(),
					// "comment_layout---",2000).show();

					findViewById(R.id.btn_face).setVisibility(View.VISIBLE);
					btn_set_mode_keyboard.setVisibility(View.INVISIBLE);
					if (ll_facechoose.getVisibility() == View.VISIBLE) {
						ll_facechoose.setVisibility(View.GONE);
						imm.showSoftInput(mEditTextContent,
								InputMethodManager.SHOW_FORCED);
					} else {// 如果出现表情框，弹出软键盘
						ll_facechoose.setVisibility(View.VISIBLE);
						imm.hideSoftInputFromWindow(mEditTextContent.getWindowToken(), 0);
					}
					mEditTextContent.setHint("回复：" + entity.getFrom_nick()/*entity.getTo_nick()*/);//楼主
					current_to_uid = entity.getFrom_uid();
					current_position = position + 2;
					current_comment_id = entity.getId();
					break;
				default:
					break;
				}

			}

			@Override
			public void dataCallBack(Object tag, int statusCode, Object result) {
				try {
					if (dialogUploadImage != null) {
						dialogUploadImage.dismiss();
					}
					if ((Integer) tag == HttpTagConstantUtils.POST_COMMENT_PRAISE) {
						if (isSuccess(statusCode)) {
							
							int praise_status = (Integer)result ;
							if(praise_status == 100){
								support_pic.setVisibility(View.VISIBLE);
								no_support_pic.setVisibility(View.GONE);
								this.textView.setText(""+ (Integer.valueOf(textView.getText() + "") + 1));
							}else{
								support_pic.setVisibility(View.GONE);
								no_support_pic.setVisibility(View.VISIBLE);
								int count =  (Integer.valueOf(textView.getText() + "") - 1) ;
								if(count >=0){
									this.textView.setText(""+ count);
								}else{
									this.textView.setText("0");
								}
								
							}
							// this.textView.setText(""+(Integer.valueOf(textView.getText()+"")+1));
							// getData();
						} else {
							AppToast.toastMsgCenter(getApplicationContext(),ArticleDiscussActivity.this.getResources().getString(R.string.ERROR_404)).show();
						}
					}
				} catch (Exception e) {

				} finally {
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							if (dialogUploadImage != null) {
								dialogUploadImage.dismiss();
							}
						}
					}, 1000);
				}

			}

		}

	}
	
	@Override
	protected void onPause() {
		super.onPause();
		resetReplay() ;
		current_position = 0 ;
	}
	
	//专题分享
	void popupShare() {
		try {
			logout(SHARE_MEDIA.SINA);
			logout(SHARE_MEDIA.QZONE);
			logout(SHARE_MEDIA.WEIXIN);
			// 使用友盟分享
			Bitmap logo = BitmapFactory.decodeResource(getResources(),
					R.drawable.app_icon);
			String img_name = "" ;
			if(this.result_entity!=null){
				img_name = this.result_entity.getImgURL() ;
				
			}
			UMShareUtil.getInstance().share(
					this,
					"" + result_entity.getTitle(),
					"" + result_entity.getTitle(),
					"http://napi.skinrun.cn/v1/archiveDetails/"
							+articleId, logo,
					HttpClientUtils.IMAGE_URL + img_name,
					new ShareAction() {
						
						@Override
						public void onSuccess() {
							try {
								String token = ACache.get(ArticleDiscussActivity.this).getAsString("Token");
								token = (token == null ?"":token) ;
								new PostUserShareService(ArticleDiscussActivity.this,
										HttpTagConstantUtils.SHARE_JIFEN,
										ArticleDiscussActivity.this).post_share(token);
									
								
							} catch (Exception e) {
								// TODO: handle exception
							}
						}
					});

		} catch (Exception e) {
			
		}
		
	}
	
	
	/**
     * 注销本次登录</br>
     */
    public void logout(final SHARE_MEDIA platform) {
    	UMShareUtil.getInstance().getUmsocialService().deleteOauth(this, platform, new SocializeClientListener() {

            @Override
            public void onStart() {

            }

            @Override
            public void onComplete(int status, SocializeEntity entity) {
                
            }
        });
    }
	
	
	/*void popupShare() {
		// 使用友盟分享
		Bitmap logo = BitmapFactory.decodeResource(getResources(),
				R.drawable.app_icon);
		UMShareUtil
				.getInstance()
				.share(this,
						"文章分享",
						"文章分享Test",
						"文章分享Test",
						logo,
						"http://sharepage.skinrun.me/nightnine/share/448a0a9008f64ba0b7fe13a356d0b4b2?from=singlemessage&isappinstalled=1",
						null);
		
	
	}*/

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		
		return false;
	}
	
	 @Override
	    protected String getActivityName() {
	        return "专题详情页面";
	    }

}

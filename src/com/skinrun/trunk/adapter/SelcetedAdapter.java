package com.skinrun.trunk.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.kymjs.aframe.bitmap.KJBitmap;
import org.kymjs.aframe.utils.SystemTool;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.base.entity.HomeSelectedEntity;
import com.app.base.entity.UserEntity;
import com.app.base.init.ACache;
import com.app.base.init.MyApplication;
import com.app.service.PostHomeCollectService;
import com.app.service.PostHomePraiseService;
import com.avoscloud.chat.service.UserService;
import com.avoscloud.chat.util.PhotoUtils;
import com.base.app.utils.DBService;
import com.base.app.utils.DensityUtil;
import com.base.app.utils.HomeTag;
import com.base.app.utils.ImageParamSetter;
import com.base.app.utils.NumDealer;
import com.base.app.utils.StringUtil;
import com.base.service.action.constant.HttpTagConstantUtils;
import com.base.service.impl.HttpAysnResultInterface;
import com.base.service.impl.HttpClientUtils;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.CommentActivity;
import com.beabox.hjy.tt.R;
import com.idongler.widgets.CircleImageView;
import com.skinrun.trunk.main.ShowTimeUtil;
import com.skinrun.trunk.test.popuwindow.MaskHintPopuwindow;
import com.skinrun.trunk.test.popuwindow.MaskHintPopuwindow.onCloseListener;

public class SelcetedAdapter extends BaseAdapter implements
		HttpAysnResultInterface, onCloseListener {
	private ArrayList<HomeSelectedEntity> data;
	private Context context;
	private String Token="";
	private Point point=MyApplication.screenSize;
	private MaskHintPopuwindow maskHintPopuwindow;
	
	SimpleDateFormat formatAll = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private String TAG = "SelcetedAdapter";

	public SelcetedAdapter(ArrayList<HomeSelectedEntity> data, Context context) {
		super();
		this.context = context;
		this.data = data;
		maskHintPopuwindow=new MaskHintPopuwindow(this);
	}

	public ArrayList<HomeSelectedEntity> getData() {
		return data;
	}

	public void setData(ArrayList<HomeSelectedEntity> data) {
		this.data = data;
	}

	@Override
	public int getCount() {
		if (data == null || data.size() <= 0) {
			return 0;
		}
		return data.size();
	}

	@Override
	public Object getItem(int arg0) {
		return data.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public int getViewTypeCount() {
		return 3;
	}

	@Override
	public int getItemViewType(int position) {
		if (data.get(position).getData_type()
				.equals(SelectedAdapterUtil.FACIALMASK)) {
			return 1;
		} else if(data.get(position).getType().equals("activity")){
			return 2;
		}else{
			return 0;
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		try {
			switch (getItemViewType(position)) {
			case 1:
				TestHolder testHolder = null;
				if (convertView == null) {
					convertView = LayoutInflater.from(context).inflate(
							R.layout.selected_list_item, null);
					testHolder = new TestHolder(convertView);
					convertView.setTag(testHolder);
				} else {
					testHolder = (TestHolder) convertView.getTag();
				}
				HomeSelectedEntity se = data.get(position);
				if(se.getUser_avatar()!=null&&!se.getUser_avatar().equals("")){
					//UrlImageViewHelper.setUrlDrawable(testHolder.civUserImage,  se.getUser_avatar(), R.drawable.my_pic_default);
					UserService.imageLoader.displayImage(""+se.getUser_avatar(), testHolder.civUserImage,
							PhotoUtils.myPicImageOptions);
				}else{
					/*UrlImageViewHelper.setUrlDrawable(testHolder.civUserImage, HttpClientUtils.USER_IMAGE_URL+""+
							StringUtil.getPathByUid(String.valueOf(se.getUid())), R.drawable.my_pic_default);*/
					UserService.imageLoader.displayImage(""+HttpClientUtils.USER_IMAGE_URL+StringUtil.getPathByUid(""+se.getUid()), testHolder.civUserImage,
							PhotoUtils.myPicImageOptions);
				}
				if(se.getNickname()==null||se.getNickname().equals("")){
					testHolder.tvNickName.setText("格格");
				}else{
					testHolder.tvNickName.setText(se.getNickname()+"");
				}
				
				testHolder.tvUserState.setText(se.getSkin_type_name()+" "+ se.getAge() + "岁");
				
				try{
					if (se.getUpload_img() != null&& !se.getUpload_img().equals("")) {
						//KJBitmap.create().display(testHolder.ivBig, se.getUpload_img());
						UserService.imageLoader.displayImage(""+se.getUpload_img(), testHolder.ivBig,
								PhotoUtils.articleImageOptions);
						
						if(se.getImg_width()!=0&&se.getImg_height()!=0){
							ImageParamSetter.setImageXFull(context,testHolder.ivBig, se.getImg_height()*1.0/se.getImg_width(),0);
						}else{
							ImageParamSetter.setImageXFull(context,testHolder.ivBig, 0,0);
						}
						
					}else{
						ImageParamSetter.setImageXFull(context,testHolder.ivBig, 0,0);
					}
				}catch(Exception e){
					e.printStackTrace();
				}
				
				try {
					if (Double.parseDouble(se.getTestbefore1()) < 0) {
						testHolder.ivJiShiRaise.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.facial_result_down));
						
					}else{
						testHolder.ivJiShiRaise.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.facialraise));
						
					}
					if (Double.parseDouble(se.getTestbefore2()) < 0) {
						testHolder.ivChiXuRaise.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.facial_result_down));
						
					}else{
						testHolder.ivChiXuRaise.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.facialraise));
					}
					if (Double.parseDouble(se.getTestbefore2()) < 0) {
						
						testHolder.ivChangXiaoRaise.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.facial_result_down));
						
					}else{
						testHolder.ivChangXiaoRaise.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.facialraise));
					}

					if (se.getCan_praise() == 0) {
						testHolder.ivSupport.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.home_support_1));
						
					}else{
						testHolder.ivSupport.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.no_support_btn));
					}
					if (se.getCan_favorite() == 0) {
						testHolder.ivCollect.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.collect_no));
					}else{
						testHolder.ivCollect.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.collect_yes));
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
				
				testHolder.praiseContain.setOnClickListener(new ClickHandler(testHolder.tvSupport, testHolder.ivSupport, position));
				testHolder.collectContain.setOnClickListener(new ClickHandler(null,testHolder.ivCollect, position));
				testHolder.commentContain.setOnClickListener(new ClickHandler(null, null, position));
				testHolder.mask_hint.setOnClickListener(new ClickHandler(null, null, 0));

				testHolder.tvJiShiNum.setText(NumDealer.getTestNum(se.getTestbefore1())+"%");
				testHolder.tvChiXuNum.setText(NumDealer.getTestNum(se.getTestbefore2())+"%");
				testHolder.tvChangXiaoNum.setText(NumDealer.getTestNum(se.getTestbefore3())+"%");
				
				if(se.getRemark()!=null&&!se.getRemark().equals("")){
					testHolder.tvPublishContent.setVisibility(View.VISIBLE);
					testHolder.tvPublishContent.setText(""+se.getRemark());
				}else{
					testHolder.tvPublishContent.setVisibility(View.GONE);
				}
				
				testHolder.tvComment.setText("评论 " + se.getComment_count() + "");
				testHolder.tvSupport.setText("赞 " + se.getPraise_count() + "");
				return convertView;
			case 2:
				ActivityHolder holder=null;
				
				HomeSelectedEntity ac = data.get(position);
				
				if (convertView == null) {
				      convertView = LayoutInflater.from(context).inflate(R.layout.activity_data_listview_item, null);
				      holder = new ActivityHolder(convertView);
				      convertView.setTag(holder);
				} else {
				    	holder = (ActivityHolder) convertView.getTag();
				}
				
				
	            
	            try{
	            	holder.action_type.setVisibility(View.GONE);  
		            holder.category.setVisibility(View.GONE);
	    	    	ImageParamSetter.setImageXFull(context, holder.action_img, 276*1.0/640, 10);	
	    	    }catch(Exception e){
	    	    	e.printStackTrace();
	    	    }
	    	    
	           
	            
	            try {
					Date now = new Date();
					Date tonight_date = formatAll.parse(ac.getEnd_time());// 
					long left_time = (now.getTime() - tonight_date.getTime()) / 1000;// 
					if (left_time < 0) {
						int day_ = (int) Math.abs(left_time / (3600 * 24)) ;
						int hour_ = (int) Math.abs(left_time % (3600*24) / 3600);
						int minute_ = (int) Math.abs(left_time % (3600*24) % 3600 / 60);
						holder.activity_left_time.setText("还剩"+day_+"天"+hour_+"小时"+minute_+"分");
					}else{
						holder.activity_left_time.setText("活动已结束");
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
	            try{
	            	UserService.imageLoader.displayImage(HttpClientUtils.IMAGE_URL
	  	    				+ "" + ac.getUpload_img(), holder.action_img,PhotoUtils.articleImageOptions);
	  	            holder.activity_title.setText(""+ac.getTitle());
	  	            holder.participant.setText(""+ac.getJoincount());
	            	
	            }catch(Exception e){
	            	e.printStackTrace();
	            }
	            
	          
				return convertView;
				
				
			case 0:
				SubjectHolder subjectHolder = null;
				if (convertView == null) {
					convertView = LayoutInflater.from(context).inflate(
							R.layout.home_data_listview_item, null);
					subjectHolder = new SubjectHolder(convertView);
					convertView.setTag(subjectHolder);
				} else {
					subjectHolder = (SubjectHolder) convertView.getTag();
				}

				HomeSelectedEntity s = data.get(position);

				subjectHolder.username.setText(s.getAuthor_nick());
				subjectHolder.release_time.setText(ShowTimeUtil.getYMDShowTime(s.getPublic_time()));
				subjectHolder.support.setText(s.getPraise_count() + "");
				subjectHolder.comment_count.setText(s.getComment_count() + "");
				subjectHolder.title.setText(s.getTitle());
				
				//if(s.getImgurl()!=null&&!s.getImgurl().equals("")){
					UserService.imageLoader.displayImage("http://a.skinrun.cn/userpic/1423707249169.jpg", subjectHolder.author_pic,PhotoUtils.myPicImageOptions);
				//}
				
				
				if(s.getImgurl()!=null&&!s.getImgurl().equals("")){
					if(s.getImg_height()!=0&&s.getImg_width()!=0){
						ImageParamSetter.setImageXFull(context, subjectHolder.article_picture, s.getImg_height()*1.0/s.getImg_width(), 0);
						KJBitmap.create().display(subjectHolder.article_picture,"http://a.skinrun.cn/images/"+ s.getImgurl());
					}else{
						ImageParamSetter.setImageXFull(context, subjectHolder.article_picture, 0, 0);
					}
					
					
				}else{
					ImageParamSetter.setImageXFull(context, subjectHolder.article_picture, 0, 0);
				}
				
				return convertView;
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}
	
	class ActivityHolder {
	    ImageView action_img;
	    TextView category;
	    TextView activity_title;
	    TextView activity_left_time;
	    TextView participant;
	    View action_type ;
	 
	    public ActivityHolder(View convertView) {
	    	action_type = convertView.findViewById(R.id.action_type);
			category = (TextView) convertView.findViewById(R.id.category);
			activity_title = (TextView) convertView.findViewById(R.id.activity_title);
			activity_left_time = (TextView) convertView.findViewById(R.id.activity_left_time);
			participant = (TextView) convertView.findViewById(R.id.participant);
			action_img = (ImageView) convertView.findViewById(R.id.action_img);
	    }
	  }
	
	
	class ClickHandler implements View.OnClickListener {
		TextView tv;
		ImageView icon;
		int position;

		public ClickHandler(TextView tv, ImageView icon, int position) {
			super();
			this.tv = tv;
			this.icon = icon;
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.praiseContain:
				if (!isLogin()) {
					AppToast.toastMsgCenter(context, "请您先登录~").show();
					return;
				}

				try {
					HomeSelectedEntity entity = data.get(position);
					if (entity.getCan_praise() == 0) {
						AppToast.toastMsgCenter(context, "亲，您已经点过赞哦~").show();
						return;
					}

					if (!SystemTool.checkNet(context)) {
						AppToast.toastMsgCenter(context,context.getResources().getString(R.string.no_network)).show();
						return;
					}

					new PostHomePraiseService(context,
							HttpTagConstantUtils.POST_HOME_PRAISE,
							SelcetedAdapter.this).doPraise(Token, "userPraise",
							"facemark", Integer.parseInt(entity.getId()));

					icon.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.home_support_1));
					entity.setPraise_count(entity.getPraise_count() + 1);
					entity.setCan_praise(0);
					tv.setText("赞 " + entity.getPraise_count() + "");

				} catch (Exception e) {
					e.printStackTrace();
				}

				break;
			case R.id.collectContain:
				if (!isLogin()) {
					AppToast.toastMsgCenter(context, "请您先登录~").show();
					return;
				}

				try {
					HomeSelectedEntity entity = data.get(position);
					if (entity.getCan_favorite() == 0) {
						AppToast.toastMsgCenter(context, "亲，您已经收藏过哦~").show();
						return;
					}

					if (!SystemTool.checkNet(context)) {
						AppToast.toastMsgCenter(context,context.getResources().getString(R.string.no_network)).show();
						return;
					}
					new PostHomeCollectService(context,HttpTagConstantUtils.POST_HOME_COLLECT,SelcetedAdapter.this).doCollect(Token,
							"userFavorite", HomeTag.FACE_MARK, entity.getId());
					entity.setCan_praise(0);
					icon.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.collect_no));

				} catch (Exception e) {

				}
				break;
			case R.id.commentContain:
				Intent intent=new Intent(context, CommentActivity.class);
				intent.putExtra("SOURSE_ID", data.get(position).getId());
				intent.putExtra("SOURSE_TYPE", HomeTag.FACE_MARK);
				context.startActivity(intent);
				break;
			case R.id.mask_hint:
				try{
					maskHintPopuwindow.show((Activity)context);
				}catch(Exception e){
					e.printStackTrace();
				}
				
				
				break;
			}
		}

	}

	@Override
	public void dataCallBack(Object tag, int statusCode, Object result) {

	}

	private boolean isLogin(){
		String Uid = ACache.get(context).getAsString("uid");
		Token = ACache.get(context).getAsString("Token");
		if(Uid == null || Token == null || "".equals(Token) || "".equals(Uid)){
			return false;
		}
		return true;
	}

	class SubjectHolder {

		TextView username;
		TextView release_time;
		TextView support;
		TextView comment_count;
		TextView title;
		TextView details;
		ImageView article_picture;
		ImageView author_pic;
		

		public SubjectHolder(View convertView) {
			username = (TextView) convertView.findViewById(R.id.username);
			release_time = (TextView) convertView.findViewById(R.id.release_time);
			support = (TextView) convertView.findViewById(R.id.support);
			comment_count = (TextView) convertView.findViewById(R.id.comment_count);
			title = (TextView) convertView.findViewById(R.id.title);
			details = (TextView) convertView.findViewById(R.id.details);
			article_picture = (ImageView) convertView.findViewById(R.id.article_picture);
			author_pic = (ImageView) convertView.findViewById(R.id.author_pic);
			
		}
	}

	class TestHolder {
		CircleImageView civUserImage;
		TextView tvNickName;
		TextView tvUserState;
		ImageView ivBig;
		ImageView ivJiShiRaise, ivChiXuRaise, ivChangXiaoRaise;
		TextView tvJiShiNum, tvChiXuNum, tvChangXiaoNum;
		TextView tvPublishContent;
		ImageView ivComment, ivSupport, ivCollect;
		TextView tvComment, tvSupport;
		View praiseContain;
		View collectContain;
		View commentContain;
		View mask_hint;

		public TestHolder(View convertView) {
			civUserImage = (CircleImageView) convertView.findViewById(R.id.civUserImage);
			tvNickName = (TextView) convertView.findViewById(R.id.tvNickName);
			tvUserState = (TextView) convertView.findViewById(R.id.tvUserState);
			ivBig = (ImageView) convertView.findViewById(R.id.ivBig);

			ivJiShiRaise = (ImageView) convertView.findViewById(R.id.ivJiShiRaise);
			ivChiXuRaise = (ImageView) convertView.findViewById(R.id.ivChiXuRaise);
			ivChangXiaoRaise = (ImageView) convertView.findViewById(R.id.ivChangXiaoRaise);

			tvJiShiNum = (TextView) convertView.findViewById(R.id.tvJiShiNum);
			tvChiXuNum = (TextView) convertView.findViewById(R.id.tvChiXuNum);
			tvChangXiaoNum = (TextView) convertView.findViewById(R.id.tvChangXiaoNum);

			tvPublishContent = (TextView) convertView.findViewById(R.id.tvPublishContent);

			ivComment = (ImageView) convertView.findViewById(R.id.ivComment);
			ivSupport = (ImageView) convertView.findViewById(R.id.ivSupport);
			ivCollect = (ImageView) convertView.findViewById(R.id.ivCollect);

			tvComment = (TextView) convertView.findViewById(R.id.tvComment);
			tvSupport = (TextView) convertView.findViewById(R.id.tvSupport);
			collectContain=convertView.findViewById(R.id.collectContain);
			praiseContain=convertView.findViewById(R.id.praiseContain);
			commentContain=convertView.findViewById(R.id.commentContain);
			mask_hint=convertView.findViewById(R.id.mask_hint);
		}
	}

	@Override
	public void close() {
		
		maskHintPopuwindow.dismiss();
	}

}

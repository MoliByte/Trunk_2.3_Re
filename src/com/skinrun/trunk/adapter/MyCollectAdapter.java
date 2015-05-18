package com.skinrun.trunk.adapter;

import java.util.ArrayList;

import org.kymjs.aframe.bitmap.KJBitmap;
import org.kymjs.aframe.utils.SystemTool;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.base.entity.CollectEntity;
import com.app.base.entity.DongTaiEntity;
import com.app.base.entity.HomeSelectedEntity;
import com.app.base.entity.UserEntity;
import com.app.service.PostHomePraiseService;
import com.avoscloud.chat.service.UserService;
import com.avoscloud.chat.util.PhotoUtils;
import com.base.app.utils.DBService;
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

public class MyCollectAdapter extends BaseAdapter implements HttpAysnResultInterface {

	private Context context;
	private ArrayList<CollectEntity> data;
	private UserEntity userEntity;

	public MyCollectAdapter(Context context, ArrayList<CollectEntity> data) {
		super();
		this.context = context;
		this.data = data;
	}

	public void setData(ArrayList<CollectEntity> data) {
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
	public Object getItem(int index) {
		return data.get(index);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getItemViewType(int position) {
		CollectEntity e = data.get(position);
		if (e.getData_type().equals(HomeTag.SKIN_TEST)) {
			return 1;
		} else {
			return 0;
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		try {
			switch (getItemViewType(position)) {
			case 1:
				SkinHolder skinHolder = null;
				if (convertView == null) {
					convertView = LayoutInflater.from(context).inflate(
							R.layout.dongtai_list_item, null);
					skinHolder = new SkinHolder(convertView);
					convertView.setTag(skinHolder);
				} else {
					skinHolder = (SkinHolder) convertView.getTag();
				}
				DongTaiEntity de = (DongTaiEntity) data.get(position)
						.getFavorite_info();

				skinHolder.tvNickName.setText(de.getNickname());
				skinHolder.tvUserState.setText(de.getSkin_type_name() + " "
						+ de.getAge() + "岁");
				skinHolder.tvPublishTime.setText(de.getTest_time());
				
				if (de.getUpload_img() != null&& !de.getUpload_img().equals("")) {
					if(de.getImg_width()!=0&&de.getImg_height()!=0){
						ImageParamSetter.setImageXFull(context, skinHolder.ivBig, de.getImg_height()*1.0/de.getImg_width(), 10);
					}else{
						ImageParamSetter.setImageXFull(context, skinHolder.ivBig, 0, 0);
					}
					//KJBitmap.create().display(skinHolder.ivBig, de.getUpload_img());
					UserService.imageLoader.displayImage(""+de.getUpload_img(), skinHolder.ivBig,
							PhotoUtils.articleImageOptions);
				}else{
					ImageParamSetter.setImageXFull(context, skinHolder.ivBig, 0, 0);
				}
				
				
				if(de.getUser_avatar()!=null&&!de.getUser_avatar().equals("")){
					//UrlImageViewHelper.setUrlDrawable(skinHolder.civUserImage, de.getUser_avatar());
					UserService.imageLoader.displayImage(""+de.getUser_avatar()+"?time="+System.currentTimeMillis(), skinHolder.civUserImage,
							PhotoUtils.myPicImageOptions);
					
				}else if(de.getUid()!=null&&!de.getUid().equals("")){
					/*UrlImageViewHelper.setUrlDrawable(skinHolder.civUserImage,HttpClientUtils.USER_IMAGE_URL+""
							+StringUtil.getPathByUid(String.valueOf(de.getUid())), R.drawable.my_pic_default);*/
					
					UserService.imageLoader.displayImage(""+HttpClientUtils.USER_IMAGE_URL+""
							+StringUtil.getPathByUid(String.valueOf(de.getUid())), skinHolder.civUserImage,
							PhotoUtils.myPicImageOptions);
				}

				skinHolder.tvTestPart.setText("完成" + de.getArea() + "区测试");
				skinHolder.tvWaterValue.setText(NumDealer.dealNum(de.getWater())+"%");
				skinHolder.tvOilValue.setText(NumDealer.dealNum(de.getOil())+"%");
				skinHolder.tvFlexibleValue.setText(NumDealer.dealFlexibleNum(de.getElasticity()));

				skinHolder.tvCommentNum.setText("评论 "+de.getComment_count() + "");
				skinHolder.tvSupportNum.setText("赞"+de.getPraise_count() + "");
				skinHolder.tvCollect.setText("收藏");

				
				//skinHolder.ivCollect.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),R.drawable.collect_no));
				
				skinHolder.LinearLayoutComment.setOnClickListener(new ClickHandler(null, null, position));
				//skinHolder.LinearLayoutSupport.setOnClickListener(new ClickHandler(skinHolder.tvSupportNum, skinHolder.ivSupport, position));
				
				return convertView;
			case 0:
				FaceMaskHolder faceMaskHolder = null;
				if (convertView == null) {
					convertView = LayoutInflater.from(context).inflate(R.layout.selected_list_item, null);
					faceMaskHolder = new FaceMaskHolder(convertView);
					convertView.setTag(faceMaskHolder);
				} else {
					faceMaskHolder = (FaceMaskHolder) convertView.getTag();
				}
				
				try{
					HomeSelectedEntity se = (HomeSelectedEntity)( data.get(position).getFavorite_info());
					
					if(se.getUser_avatar()!=null&&!se.getUser_avatar().equals("")){
						
						//UrlImageViewHelper.setUrlDrawable(faceMaskHolder.civUserImage,se.getUser_avatar(), R.drawable.my_pic_default);
						UserService.imageLoader.displayImage(se.getUser_avatar()+"?time="+System.currentTimeMillis(), faceMaskHolder.civUserImage,
								PhotoUtils.myPicImageOptions);
					}else if(se.getUid()!=null&&!se.getUid().equals("")){
						/*UrlImageViewHelper.setUrlDrawable(faceMaskHolder.civUserImage,HttpClientUtils.USER_IMAGE_URL+""
								+StringUtil.getPathByUid(String.valueOf(se.getUid())), R.drawable.my_pic_default);*/
						UserService.imageLoader.displayImage(""+HttpClientUtils.USER_IMAGE_URL+""
								+StringUtil.getPathByUid(String.valueOf(se.getUid())), faceMaskHolder.civUserImage,
								PhotoUtils.myPicImageOptions);
					}
					
					faceMaskHolder.tvNickName.setText(se.getNickname()+"");
					faceMaskHolder.tvUserState.setText(se.getSkin_type_name()+ se.getAge() + "岁");

					if (se.getUpload_img() != null&& !se.getUpload_img().equals("")) {
						if(se.getImg_width()!=0&&se.getImg_height()!=0){
							ImageParamSetter.setImageXFull(context, faceMaskHolder.ivBig, se.getImg_height()*1.0/se.getImg_width(), 0);
						}else{
							ImageParamSetter.setImageXFull(context, faceMaskHolder.ivBig, 0, 0);
						}
						UserService.imageLoader.displayImage(""+se.getUpload_img(), faceMaskHolder.ivBig,
								PhotoUtils.articleImageOptions);
						//KJBitmap.create().display(faceMaskHolder.ivBig, se.getUpload_img());
						
					}else{
						ImageParamSetter.setImageXFull(context, faceMaskHolder.ivBig, 0, 0);
					}
					
					
					if (Double.parseDouble(se.getTestbefore1()) < 0) {
						
						faceMaskHolder.ivJiShiRaise.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),R.drawable.facial_result_down));
					}else{
						faceMaskHolder.ivJiShiRaise.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),R.drawable.facialraise));
					}
					
					
					if (Double.parseDouble(se.getTestbefore2()) < 0) {
						faceMaskHolder.ivChiXuRaise.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),R.drawable.facial_result_down));
					}else{
						faceMaskHolder.ivChiXuRaise.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),R.drawable.facialraise));
					}
					
					
					if (Double.parseDouble(se.getTestbefore2()) < 0) {
						faceMaskHolder.ivChangXiaoRaise.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),R.drawable.facial_result_down));
					}else{
						faceMaskHolder.ivChangXiaoRaise.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),R.drawable.facialraise));
					}
					
					/*if(se.getCan_praise()==0){
						faceMaskHolder.ivSupport.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),R.drawable.home_support_1));
					}else{
						faceMaskHolder.ivSupport.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),R.drawable.no_support_btn));
					}*/

					faceMaskHolder.tvJiShiNum.setText(NumDealer.getTestNum(se.getTestbefore1())+"%");
					faceMaskHolder.tvChiXuNum.setText(NumDealer.getTestNum(se.getTestbefore2())+"%");
					faceMaskHolder.tvChangXiaoNum.setText(NumDealer.getTestNum(se.getTestbefore3())+"%");
					
					
					if(se.getRemark()==null||se.getRemark().equals("")){
						faceMaskHolder.tvPublishContent.setVisibility(View.GONE);
					}else{
						faceMaskHolder.tvPublishContent.setVisibility(View.VISIBLE);
						faceMaskHolder.tvPublishContent.setText(se.getRemark());
					}
					
					
					faceMaskHolder.tvComment.setText("评论 "+se.getComment_count() + "");
					faceMaskHolder.tvSupport.setText("赞 "+se.getPraise_count() + "");
					faceMaskHolder.tvCollect.setText("收藏");
					
					faceMaskHolder.commentContain.setOnClickListener(new ClickHandler(null, null, position));
					//faceMaskHolder.praiseContain.setOnClickListener(new ClickHandler(faceMaskHolder.tvSupport, faceMaskHolder.ivSupport, position));
					
				}catch(Exception e){
					e.printStackTrace();
				}
				return convertView;
			}
		} catch (Exception e) {

		}

		return null;
	}

	class SkinHolder {
		CircleImageView civUserImage;
		TextView tvNickName;
		TextView tvUserState;
		TextView tvPublishTime;
		ImageView ivBig;
		TextView tvTestPart;
		TextView tvWaterValue, tvOilValue, tvFlexibleValue;
		ImageView ivComment, ivSupport, ivCollect;
		TextView tvCommentNum, tvSupportNum, tvCollect;
		View LinearLayoutComment,LinearLayoutSupport,LinearLayoutCollect;

		public SkinHolder(View convertView) {
			civUserImage = (CircleImageView) convertView.findViewById(R.id.circleImageViewUserImage);
			tvNickName = (TextView) convertView.findViewById(R.id.textViewNickname);
			tvUserState = (TextView) convertView.findViewById(R.id.textViewUserState);
			tvPublishTime = (TextView) convertView.findViewById(R.id.textViewPublishTime);
			ivBig = (ImageView) convertView.findViewById(R.id.imageViewBig);
			tvTestPart = (TextView) convertView.findViewById(R.id.textViewTestPart);

			tvWaterValue = (TextView) convertView.findViewById(R.id.tvWaterValue);
			tvOilValue = (TextView) convertView.findViewById(R.id.tvOilValue);
			tvFlexibleValue = (TextView) convertView.findViewById(R.id.tvFlexibleValue);

			ivComment = (ImageView) convertView.findViewById(R.id.ivComment);
			ivSupport = (ImageView) convertView.findViewById(R.id.ivSupport);
			ivCollect = (ImageView) convertView.findViewById(R.id.ivCollect);

			tvCommentNum = (TextView) convertView.findViewById(R.id.tvCommentNum);
			tvSupportNum = (TextView) convertView.findViewById(R.id.tvSupportNum);
			tvCollect = (TextView) convertView.findViewById(R.id.tvCollect);
			LinearLayoutComment=convertView.findViewById(R.id.LinearLayoutComment);
			LinearLayoutSupport=convertView.findViewById(R.id.LinearLayoutSupport);
			LinearLayoutCollect=convertView.findViewById(R.id.LinearLayoutCollect);
			
		}
	}

	class FaceMaskHolder {
		CircleImageView civUserImage;
		TextView tvNickName;
		TextView tvUserState;
		ImageView ivBig;
		ImageView ivJiShiRaise, ivChiXuRaise, ivChangXiaoRaise;
		TextView tvJiShiNum, tvChiXuNum, tvChangXiaoNum;
		TextView tvPublishContent;
		ImageView ivComment, ivSupport, ivCollect;
		TextView tvComment, tvSupport, tvCollect;
		View commentContain,praiseContain,collectContain;

		public FaceMaskHolder(View convertView) {
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
			tvCollect = (TextView) convertView.findViewById(R.id.tvCollect);
			commentContain=convertView.findViewById(R.id.commentContain);
			praiseContain=convertView.findViewById(R.id.praiseContain);
			collectContain=convertView.findViewById(R.id.collectContain);
		}
	}
	
	class ClickHandler implements View.OnClickListener{
		private TextView tv;
		private ImageView iv;
		private int position;
		

		public ClickHandler(TextView tv, ImageView iv, int position) {
			super();
			this.tv = tv;
			this.iv = iv;
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.commentContain:
				Intent intent=new Intent(context, CommentActivity.class);
				HomeSelectedEntity he=(HomeSelectedEntity)data.get(position).getFavorite_info();
				intent.putExtra("SOURSE_ID", he.getId());
				intent.putExtra("SOURSE_TYPE", data.get(position).getData_type());
				context.startActivity(intent);
			break;
			
			case R.id.praiseContain:
				if (!isLogin()) {
					AppToast.toastMsgCenter(context, "请您先登录~").show();
					return;
				}
				String token = userEntity.getToken();

				try {
					HomeSelectedEntity entity = (HomeSelectedEntity) data.get(position).getFavorite_info();
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
							MyCollectAdapter.this).doPraise(token, "userPraise","facemark", Integer.parseInt(entity.getId()));

					iv.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.home_support_1));
					entity.setPraise_count(entity.getPraise_count() + 1);
					entity.setCan_praise(0);
					tv.setText("赞 " + entity.getPraise_count() + "");

				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case R.id.collectContain:
				break;
			case R.id. LinearLayoutComment:
				Intent i=new Intent(context, CommentActivity.class);
				DongTaiEntity de=(DongTaiEntity)data.get(position).getFavorite_info();
				i.putExtra("SOURSE_ID", de.getId());
				i.putExtra("SOURSE_TYPE", data.get(position).getData_type());
				context.startActivity(i);
				break;
			case R.id.LinearLayoutSupport:
				if(!isLogin()){
					AppToast.toastMsgCenter(context,"请登录!").show();
					return ;
				}
				DongTaiEntity entity=(DongTaiEntity) data.get(position).getFavorite_info();
				if(entity.getCan_praise()==0){
					AppToast.toastMsgCenter(context, "亲，您已经点过赞哦~").show();
					return;
				}
				
				if (!SystemTool.checkNet(context)) {
					AppToast.toastMsgCenter(context,context.getResources().getString(R.string.no_network)).show();
					return;
				}
				
				
				
				try{
					new PostHomePraiseService(context,
							HttpTagConstantUtils.POST_HOME_PRAISE,
							MyCollectAdapter.this).doPraise(userEntity.getToken(), "userPraise",
									HomeTag.SKIN_TEST, Integer.parseInt(entity.getId()));
					iv.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.home_support_1));
					entity.setPraise_count(entity.getPraise_count()+1);
					entity.setCan_praise(0);
					
					tv.setText("赞 "+entity.getPraise_count());
					
					
				}catch(Exception e){
					e.printStackTrace();
				}
				
				break;
			case R.id.LinearLayoutCollect:
				break;

			}
		}
		
	}
	
	public boolean isLogin() {
		userEntity = DBService.getUserEntity();

		if (userEntity != null && userEntity.getToken() != null
				&& !userEntity.getToken().equals("")) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void dataCallBack(Object tag, int statusCode, Object result) {
		
	}
}

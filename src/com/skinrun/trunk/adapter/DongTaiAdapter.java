package com.skinrun.trunk.adapter;

import java.util.ArrayList;

import org.kymjs.aframe.bitmap.KJBitmap;
import org.kymjs.aframe.utils.SystemTool;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.base.entity.DongTaiEntity;
import com.app.base.entity.UserEntity;
import com.app.base.init.ACache;
import com.app.base.init.MyApplication;
import com.app.service.PostHomeCollectService;
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

public class DongTaiAdapter extends BaseAdapter implements HttpAysnResultInterface {
	private Context context;
	private ArrayList<DongTaiEntity> data;
	
	private String TAG="DongTaiAdapter";
	public String Token="";
	private Point point=MyApplication.screenSize;
	public DongTaiAdapter(Context context, ArrayList<DongTaiEntity> data) {
		super();
		this.context = context;
		this.data = data;
	}

	public void setData(ArrayList<DongTaiEntity> data) {
		this.data = data;
	}

	@Override
	public int getCount() {
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
	public View getView(int position, View convertView, ViewGroup parent) {
		try{
			TestHolder testHolder=null;
			if(convertView==null){
				convertView=LayoutInflater.from(context).inflate(R.layout.dongtai_list_item, null);
				testHolder=new TestHolder(convertView);
				convertView.setTag(testHolder);
			}else{
				testHolder=(TestHolder)convertView.getTag();
			}
			DongTaiEntity de=data.get(position);
			
			
			if(de.getUser_avatar()!=null&&!de.getUser_avatar().equals("")){
				//UrlImageViewHelper.setUrlDrawable(testHolder.civUserImage,  de.getUser_avatar(), R.drawable.my_pic_default);
				UserService.imageLoader.displayImage(""+de.getUser_avatar(),testHolder.civUserImage,
						PhotoUtils.myPicImageOptions);
			}else{
				/*UrlImageViewHelper.setUrlDrawable(testHolder.civUserImage, HttpClientUtils.USER_IMAGE_URL+""
						+StringUtil.getPathByUid(String.valueOf(de.getUid())), R.drawable.my_pic_default);*/
				UserService.imageLoader.displayImage(""+HttpClientUtils.USER_IMAGE_URL+""+StringUtil
						.getPathByUid(String.valueOf(de.getUid())), testHolder.civUserImage,
						PhotoUtils.myPicImageOptions);
			}
			if(de.getNickname()==null||de.getNickname().equals("")){
				testHolder.tvNickName.setText("格格");
			}else{
				testHolder.tvNickName.setText(de.getNickname()+"");
			}
			
			testHolder.tvUserState.setText(de.getSkin_type_name()+" "+de.getAge()+"岁");
			
			testHolder.tvPublishTime.setText(de.getTest_time());
			
			try{
				if (de.getUpload_img() != null&& !de.getUpload_img().equals("")) {
					
					if(de.getImg_width()!=0&&de.getImg_height()!=0){
						Log.e(TAG, "=============图片高度："+de.getImg_height()+" 宽度："+de.getImg_width());
						ImageParamSetter.setImageXFull(context,testHolder.ivBig, de.getImg_height()*1.0/de.getImg_width(),10);
						UserService.imageLoader.displayImage(de.getUpload_img(), testHolder.ivBig, PhotoUtils.articleImageOptions);
					}else{
						ImageParamSetter.setImageXFull(context,testHolder.ivBig, 0,0);
					}
					
				}else{
					ImageParamSetter.setImageXFull(context,testHolder.ivBig, 0,0);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			if(de.getArea().equals("B")||de.getArea().equals("F")){
				testHolder.tvTestPart.setText("完成 手背 测试");
			}else if(de.getArea().equals("E")){
				testHolder.tvTestPart.setText("完成 眼周 测试");
			}else{
				testHolder.tvTestPart.setText("完成 "+de.getArea()+"区 测试");
			}
			
			
			testHolder.tvWaterValue.setText(NumDealer.dealNum(de.getWater())+"%");
			testHolder.tvOilValue.setText(NumDealer.dealNum(de.getOil())+"%");
			testHolder.tvFlexibleValue.setText(NumDealer.dealFlexibleNum(de.getElasticity()));
			
			testHolder.tvCommentNum.setText("评论 "+de.getComment_count()+"");
			testHolder.tvSupportNum.setText("赞 "+de.getPraise_count()+"");
			try{
				if(de.getCan_favorite()==0){
					testHolder.ivCollect.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.collect_no));
				}else{
					testHolder.ivCollect.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.collect_yes));
				}
				if(de.getCan_praise()==0){
					testHolder.ivSupport.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.home_support_1));
				}else{
					testHolder.ivSupport.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.no_support_btn));
				}
				
				
			}catch(Exception e){
				e.printStackTrace();
			}
			
			testHolder.LinearLayoutSupport.setOnClickListener(new ClickHandler(testHolder.ivSupport, testHolder.tvSupportNum, position));
			testHolder.LinearLayoutCollect.setOnClickListener(new ClickHandler(testHolder.ivCollect, null, position));
			testHolder.LinearLayoutComment.setOnClickListener(new ClickHandler(null, null, position));
			
			return convertView;
		}catch(Exception e){
			return null;
		}
	}
	
	
	class TestHolder{
		CircleImageView civUserImage;
		TextView tvNickName;
		TextView tvUserState;
		TextView tvPublishTime;
		ImageView ivBig;
		TextView tvTestPart;
		TextView tvWaterValue,tvOilValue,tvFlexibleValue;
		ImageView ivSupport,ivCollect;
		TextView tvCommentNum,tvSupportNum;
		View LinearLayoutSupport,LinearLayoutCollect,LinearLayoutComment;
		
		
		public TestHolder(View convertView){
			civUserImage=(CircleImageView)convertView.findViewById(R.id.circleImageViewUserImage);
			tvNickName=(TextView)convertView.findViewById(R.id.textViewNickname);
			tvUserState=(TextView)convertView.findViewById(R.id.textViewUserState);
			tvPublishTime=(TextView)convertView.findViewById(R.id.textViewPublishTime);
			ivBig=(ImageView)convertView.findViewById(R.id.imageViewBig);
			tvTestPart=(TextView)convertView.findViewById(R.id.textViewTestPart);
			
			tvWaterValue=(TextView)convertView.findViewById(R.id.tvWaterValue);
			tvOilValue=(TextView)convertView.findViewById(R.id.tvOilValue);
			tvFlexibleValue=(TextView)convertView.findViewById(R.id.tvFlexibleValue);
			
			ivSupport=(ImageView)convertView.findViewById(R.id.ivSupport);
			ivCollect=(ImageView)convertView.findViewById(R.id.ivCollect);
			
			tvCommentNum=(TextView)convertView.findViewById(R.id.tvCommentNum);
			tvSupportNum=(TextView)convertView.findViewById(R.id.tvSupportNum);
			
			LinearLayoutCollect=convertView.findViewById(R.id.LinearLayoutCollect);
			LinearLayoutSupport=convertView.findViewById(R.id.LinearLayoutSupport);
			LinearLayoutComment=convertView.findViewById(R.id.LinearLayoutComment);
		}
	}

	class ClickHandler implements View.OnClickListener{
		ImageView icon;
		TextView text;
		int position;
		
		public ClickHandler(ImageView icon, TextView text, int position) {
			super();
			this.icon = icon;
			this.text = text;
			this.position = position;
		}



		@Override
		public void onClick(View v) {
			
			switch(v.getId()){
			
			case R.id.LinearLayoutSupport:
				if(!isLogin()){
					AppToast.toastMsgCenter(context,"请登录!").show();
					return ;
				}
				
				if(data.get(position).getCan_praise()==0){
					AppToast.toastMsgCenter(context, "亲，您已经点过赞哦~").show();
					return;
				}
				
				if (!SystemTool.checkNet(context)) {
					AppToast.toastMsgCenter(context,context.getResources().getString(R.string.no_network)).show();
					return;
				}
				
				DongTaiEntity entity=data.get(position);
				
				try{
					new PostHomePraiseService(context,
							HttpTagConstantUtils.POST_HOME_PRAISE,
							DongTaiAdapter.this).doPraise(Token, "userPraise",
									HomeTag.SKIN_TEST, Integer.parseInt(entity.getId()));
					icon.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.home_support_1));
					entity.setPraise_count(entity.getPraise_count()+1);
					entity.setCan_praise(0);
					
					text.setText("赞 "+entity.getPraise_count());
					
					
				}catch(Exception e){
					e.printStackTrace();
				}
				break;
			case R.id.LinearLayoutCollect:
				if(!isLogin()){
					AppToast.toastMsgCenter(context,"请登录!").show();
					return ;
				}
				
				if(data.get(position).getCan_favorite()==0){
					AppToast.toastMsgCenter(context, "亲，您已经收藏过哦~").show();
					return;
				}
				
				if (!SystemTool.checkNet(context)) {
					AppToast.toastMsgCenter(context,context.getResources().getString(R.string.no_network)).show();
					return;
				}
				try{
					DongTaiEntity e=data.get(position);
					new PostHomeCollectService(context,HttpTagConstantUtils.POST_HOME_COLLECT,DongTaiAdapter.this).doCollect(""+Token,
							"userFavorite", HomeTag.SKIN_TEST, e.getId());
					
					e.setCan_favorite(0);
					icon.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.collect_no));
					
				}catch(Exception e){
					
				}
				break;
				
			case R.id.LinearLayoutComment:
				Intent intent=new Intent(context, CommentActivity.class);
				intent.putExtra("SOURSE_ID", data.get(position).getId());
				intent.putExtra("SOURSE_TYPE", HomeTag.SKIN_TEST);
				context.startActivity(intent);
				
				break;
			}
		}
		
	}
	private boolean isLogin(){
		String Uid = ACache.get(context).getAsString("uid");
		Token = ACache.get(context).getAsString("Token");
		if(Uid == null || Token == null || "".equals(Token) || "".equals(Uid)){
			return false;
		}
		return true;
	}

	@Override
	public void dataCallBack(Object tag, int statusCode, Object result) {
		
	}
}

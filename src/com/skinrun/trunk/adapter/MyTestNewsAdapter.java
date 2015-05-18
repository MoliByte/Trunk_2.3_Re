package com.skinrun.trunk.adapter;

import java.util.ArrayList;

import com.app.base.entity.TestNewsEntity;
import com.avoscloud.chat.base.SmileUtils;
import com.avoscloud.chat.service.UserService;
import com.avoscloud.chat.util.PhotoUtils;
import com.base.app.utils.HomeTag;
import com.base.app.utils.ImageParamSetter;
import com.base.app.utils.StringUtil;
import com.base.service.impl.HttpClientUtils;
import com.beabox.hjy.tt.R;
import com.beabox.hjy.tt.SelectedSecondActivity;
import com.idongler.widgets.CircleImageView;

import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.BufferType;

public class MyTestNewsAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<TestNewsEntity> data;
	
	public MyTestNewsAdapter(Context context, ArrayList<TestNewsEntity> data) {
		super();
		this.context = context;
		this.data = data;
	}

	@Override
	public int getCount() {
		if(data==null||data.size()<=0){
			return 0;
		}
		
		return data.size();
	}

	@Override
	public Object getItem(int index) {
		return data.get(index);
	}

	@Override
	public long getItemId(int index) {
		return index;
	}
	
	@Override
	public int getItemViewType(int position) {
		if(data.get(position).getTest_type().equals(HomeTag.FACE_MARK)){
			return 0;
		}else {
			return 1;
			
		}
	}
	@Override
	public int getViewTypeCount() {
		return 2;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		TestNewsEntity entity=data.get(position);
		switch(getItemViewType(position)){
		case 0:
			MaskNewsHolder maskNewsHolder=null;
			
			if(convertView==null){
				convertView=LayoutInflater.from(context).inflate(R.layout.my_test_mask_message_item, null);
				maskNewsHolder=new MaskNewsHolder(convertView);
				convertView.setTag(maskNewsHolder);
			}else{
				maskNewsHolder=(MaskNewsHolder)convertView.getTag();
			}
			
			maskNewsHolder.maskJoinerName.setText(""+entity.getFrom_nickname());
			maskNewsHolder.joinerAction.setText(""+entity);
			
			if(entity.getFrom_avatar()!=null&&!entity.getFrom_avatar().equals("")){
				UserService.imageLoader.displayImage(""+entity.getFrom_avatar(),maskNewsHolder.maskJoinerIcon,
						PhotoUtils.myPicImageOptions);
			}else{
				UserService.imageLoader.displayImage(""+HttpClientUtils.USER_IMAGE_URL+""+StringUtil
						.getPathByUid(String.valueOf(entity.getFrom_uid())), maskNewsHolder.maskJoinerIcon,
						PhotoUtils.myPicImageOptions);
			}
			
			if(entity.getMsg_type().equals("praises")){
				maskNewsHolder.joinerAction.setText("赞了你的测试");
				
			}else if(entity.getMsg_type().equals("comment")){
				Spannable span_replay = SmileUtils.getSmiledText(context, "评论了你："+entity.getContent());
				maskNewsHolder.joinerAction.setText(span_replay, BufferType.SPANNABLE);  
			}
			//测试时发表的心情
			if(entity.getRemark()==null||entity.getRemark().equals("")){
				maskNewsHolder.containMaskMood.setVisibility(View.GONE);
			}else{
				Spannable span_mood = SmileUtils.getSmiledText(context, ""+entity.getRemark());
				maskNewsHolder.maskMood.setText(span_mood, BufferType.SPANNABLE);  
			}
			maskNewsHolder.tvJoinTime.setText(""+entity.getCreate_time());
			
			if(entity.getUpload_img()!=null&&!entity.getUpload_img().equals("")){
				UserService.imageLoader.displayImage(entity.getUpload_img(), maskNewsHolder.ivMaskPic, PhotoUtils.articleImageOptions);
			}
			
			convertView.setOnClickListener(new clickHandler(position));
			
			break;
		case 1:
			SkinNewsHolder skinNewsHolder=null;
			if(convertView==null){
				convertView=LayoutInflater.from(context).inflate(R.layout.my_test_skin_message_item, null);
				skinNewsHolder=new SkinNewsHolder(convertView);
				convertView.setTag(skinNewsHolder);
				
			}else{
				skinNewsHolder=(SkinNewsHolder) convertView.getTag();
			}
			
			skinNewsHolder.skinJoinerName.setText(""+entity.getFrom_nickname());
			if(entity.getFrom_avatar()!=null&&!entity.getFrom_avatar().equals("")){
				UserService.imageLoader.displayImage(""+entity.getFrom_avatar(),skinNewsHolder.skinJoinerIcon,
						PhotoUtils.myPicImageOptions);
			}else{
				UserService.imageLoader.displayImage(""+HttpClientUtils.USER_IMAGE_URL+""+StringUtil
						.getPathByUid(String.valueOf(entity.getFrom_uid())), skinNewsHolder.skinJoinerIcon,
						PhotoUtils.myPicImageOptions);
			}
			
			if(entity.getMsg_type().equals("praises")){
				skinNewsHolder.skinJoinAction.setText("赞了你的测试");
				
			}else if(entity.getMsg_type().equals("comment")){
				Spannable span_replay = SmileUtils.getSmiledText(context, "评论了你："+entity.getContent());
				
				skinNewsHolder.skinJoinAction.setText(span_replay, BufferType.SPANNABLE);  
			}
			
			skinNewsHolder.tvSkinPart.setText("完成 "+entity.getArea()+"区 测试");
			skinNewsHolder.tvWaterValue.setText(""+entity.getWater());
			skinNewsHolder.tvOilValue.setText(""+entity.getOil());
			skinNewsHolder.tvFlexibleValue.setText(""+entity.getElasticity());
			
			skinNewsHolder.skinJoinTime.setText(""+entity.getCreate_time());
			break;
		}
		
		return convertView;
	}

	class SkinNewsHolder{
		CircleImageView skinJoinerIcon;
		TextView skinJoinerName;
		TextView skinJoinAction;
		TextView tvSkinPart;
		TextView tvWaterValue;
		TextView tvOilValue;
		TextView tvFlexibleValue;
		TextView skinJoinTime;
		
		public SkinNewsHolder(View convertView){
			skinJoinerIcon=(CircleImageView)convertView.findViewById(R.id.skinJoinerIcon);
			
			skinJoinerName=(TextView)convertView.findViewById(R.id.skinJoinerName);
			skinJoinAction=(TextView)convertView.findViewById(R.id.skinJoinAction);
			tvSkinPart=(TextView)convertView.findViewById(R.id.tvSkinPart);
			tvWaterValue=(TextView)convertView.findViewById(R.id.tvWaterValue);
			tvOilValue=(TextView)convertView.findViewById(R.id.tvOilValue);
			tvFlexibleValue=(TextView)convertView.findViewById(R.id.tvFlexibleValue);
			skinJoinTime=(TextView)convertView.findViewById(R.id.skinJoinTime);
		}
		
	}
	
	class MaskNewsHolder{
		CircleImageView maskJoinerIcon;
		TextView maskJoinerName;
		TextView joinerAction;
		TextView maskMood;
		TextView tvJoinTime;
		ImageView ivMaskPic;
		View containMaskMood;
		public MaskNewsHolder(View convertView){
			maskJoinerIcon=(CircleImageView)convertView.findViewById(R.id.maskJoinerIcon);
			
			maskJoinerName=(TextView)convertView.findViewById(R.id.maskJoinerName);
			joinerAction=(TextView)convertView.findViewById(R.id.joinerAction);
			maskMood=(TextView)convertView.findViewById(R.id.maskMood);
			tvJoinTime=(TextView)convertView.findViewById(R.id.tvJoinTime);
			
			ivMaskPic=(ImageView)convertView.findViewById(R.id.ivMaskPic);
			containMaskMood=convertView.findViewById(R.id.containMaskMood);
		}
	}
	
	
	class clickHandler implements View.OnClickListener{
		private int position;
		
		
		public clickHandler(int position) {
			super();
			this.position = position;
		}


		@Override
		public void onClick(View arg0) {
			Intent i=new Intent(context, SelectedSecondActivity.class);
			
			i.putExtra("ID",data.get(position).getTest_id());
			i.putExtra("SOURSE_TYPE", HomeTag.FACE_MARK);
			
			context.startActivity(i);
		}
	}
	
}

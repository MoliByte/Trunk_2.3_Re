package com.skinrun.trunk.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.app.base.entity.MineMessageEntity;
import com.app.base.entity.MyMessageEntity;
import com.avoscloud.chat.base.SmileUtils;
import com.avoscloud.chat.service.UserService;
import com.avoscloud.chat.util.PhotoUtils;
import com.base.app.utils.StringUtil;
import com.base.service.impl.HttpClientUtils;
import com.beabox.hjy.tt.ActionDiscussActivity;
import com.beabox.hjy.tt.ArticleDiscussActivity;
import com.beabox.hjy.tt.ExterNaLUrlActivity;
import com.beabox.hjy.tt.R;
import com.idongler.widgets.CircleImageView;

public class MyMessageListAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<MineMessageEntity> data;
	
	public MyMessageListAdapter(Context context,
			ArrayList<MineMessageEntity> data) {
		super();
		this.context = context;
		this.data = data;
	}

	@Override
	public int getCount() {
		if(data==null||data.size()==0){
			return 0 ;
		}
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		SysMesViewHolder sysMesViewHolder=null;
		if(convertView==null){
			convertView=LayoutInflater.from(context).inflate(R.layout.my_message_item, null);
			sysMesViewHolder=new SysMesViewHolder(convertView);
			convertView.setTag(sysMesViewHolder);
			
		}else{
			sysMesViewHolder=(SysMesViewHolder) convertView.getTag();
		}
		MineMessageEntity entity=data.get(position);
		
		
		Log.e("MyMessageListAdapter", "=============entity.getFrom_user_avatar():"+entity.getFrom_user_avatar());
		Log.e("MyMessageListAdapter", "=============entity.getFrom_uid():"+entity.getFrom_uid());
		
		
		if(entity.getFrom_user_avatar()!=null&&!entity.getFrom_user_avatar().equals("")){
			if(entity.getFrom_user_avatar().startsWith("http")||entity.getFrom_user_avatar().startsWith("Http")){
				UserService.imageLoader.displayImage(entity.getFrom_user_avatar(), sysMesViewHolder.message_icon,
						PhotoUtils.myPicImageOptions);
			}else{
				UserService.imageLoader.displayImage(HttpClientUtils.IMAGE_URL+entity.getFrom_user_avatar(), sysMesViewHolder.message_icon,
						PhotoUtils.myPicImageOptions);
			}
		}else{
			UserService.imageLoader.displayImage(""+HttpClientUtils.AUTHOR_IMAGE_URL+StringUtil.getPathByUid(""+entity.getFrom_uid()), sysMesViewHolder.message_icon,
					PhotoUtils.myPicImageOptions);
		}
		
		if(entity.getFrom_nickname()==null||entity.getFrom_nickname().equals("")||entity.equals("null")){
			sysMesViewHolder.message_sender_name.setText("凯文");
		}else{
			sysMesViewHolder.message_sender_name.setText(entity.getFrom_nickname());
		}
		Spannable span_replay = SmileUtils.getSmiledText(context, ""+entity.getMsg_content());
		sysMesViewHolder.tvMessageContent.setText(span_replay, BufferType.SPANNABLE);
		
		sysMesViewHolder.tvNoticeTime.setText(""+entity.getMsg_time());
		
		if(entity.getUrl_type().equals("txt")){
			sysMesViewHolder.contain_linked.setVisibility(View.GONE);
		}else{
			sysMesViewHolder.contain_linked.setVisibility(View.VISIBLE);
			
			UserService.imageLoader.displayImage(""+entity.getActivity_img(), 
					sysMesViewHolder.ivActionImage,PhotoUtils.myPicImageOptions);
			sysMesViewHolder.tvActionImage.setText(""+entity.getActivity_title());
			sysMesViewHolder.contain_linked.setOnClickListener(new ClickHandler(position));
		}
		

		return convertView;
	}
	
	class ClickHandler implements View.OnClickListener{
		private int position;
		
		public ClickHandler(int position) {
			super();
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.contain_linked:
				MineMessageEntity entity=data.get(position);
				
				Intent intent=null;
				if(entity.getUrl_type().equals("inner_activity")){
					intent=new Intent(context, ActionDiscussActivity.class);
					Bundle bundle=new Bundle();
					bundle.putLong("activityId", Long.parseLong(""+entity.getActivity_id()));
					intent.putExtras(bundle);
					
				}else if(entity.getUrl_type().equals("inner_article")){
					intent=new Intent(context, ArticleDiscussActivity.class);
					Bundle bundle=new Bundle();
					bundle.putLong("articleId", Long.parseLong(""+entity.getActivity_id()));
					intent.putExtras(bundle);
					
				}else if(entity.getUrl_type().equals("external_url")){
					intent=new Intent(context, ExterNaLUrlActivity.class);
					Bundle bundle=new Bundle();
					bundle.putString("URL", ""+entity.getActivity_external_url());
					bundle.putString("titleName", "详情");
					intent.putExtras(bundle);
				}
				if(intent!=null){
					context.startActivity(intent);
					
				}
				
				break;
			}
		}
	}
	
	class SysMesViewHolder{
		CircleImageView message_icon;
		TextView message_sender_name;
		TextView tvMessageContent;
		ImageView ivActionImage;
		TextView tvActionImage;
		TextView tvNoticeTime;
		View contain_linked;
		
		public SysMesViewHolder(View convertView){
			message_icon=(CircleImageView)convertView.findViewById(R.id.message_icon);
			message_sender_name=(TextView)convertView.findViewById(R.id.message_sender_name);
			tvMessageContent=(TextView)convertView.findViewById(R.id.tvMessageContent);
			ivActionImage=(ImageView)convertView.findViewById(R.id.ivActionImage);
			tvActionImage=(TextView)convertView.findViewById(R.id.tvActionImage);
			tvNoticeTime=(TextView)convertView.findViewById(R.id.tvNoticeTime);
			contain_linked=convertView.findViewById(R.id.contain_linked);
		}
	}
}

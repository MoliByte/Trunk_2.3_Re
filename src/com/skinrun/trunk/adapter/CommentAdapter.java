package com.skinrun.trunk.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.app.base.entity.ParentCommentEntity;
import com.avoscloud.chat.base.SmileUtils;
import com.avoscloud.chat.service.UserService;
import com.avoscloud.chat.util.PhotoUtils;
import com.base.app.utils.StringUtil;
import com.base.service.impl.HttpClientUtils;
import com.beabox.hjy.tt.R;
import com.idongler.widgets.CircleImageView;

public class CommentAdapter extends BaseAdapter {
	private ArrayList<ParentCommentEntity> data;
	private Context context;
	private String TAG="CommentAdapter";
	
	public CommentAdapter(ArrayList<ParentCommentEntity> data, Context context) {
		super();
		this.data = data;
		this.context = context;
	}
	
	public void setData(ArrayList<ParentCommentEntity> data) {
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
	public long getItemId(int arg0) {
		return arg0;
	}
	
	@Override
	public int getViewTypeCount() {
		return 2;
	}
	
	@Override
	public int getItemViewType(int position) {
		if(data.get(position).getId().equals("-1")){
			return 1;
		}else{
			return 0;
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		switch(getItemViewType(position)){
		case 0:
			Holder holder=null;
			if(convertView==null){
				convertView=LayoutInflater.from(context).inflate(R.layout.comment_item, null);
				holder=new Holder(convertView);
				convertView.setTag(holder);
			}else{
				holder=(Holder) convertView.getTag();
			}
			ParentCommentEntity pe=data.get(position);
			try{
				/*UrlImageViewHelper.setUrlDrawable(holder.userIcon, HttpClientUtils.USER_IMAGE_URL+""+StringUtil
						.getPathByUid(String.valueOf(pe.getUid())),R.drawable.my_pic_default);*/
				
				UserService.imageLoader.displayImage(""+HttpClientUtils.USER_IMAGE_URL+""+StringUtil
						.getPathByUid(String.valueOf(pe.getUid())), holder.userIcon,PhotoUtils.myPicImageOptions);
				
				holder.tvUserNick.setText(pe.getNickname());
				holder.tvCreateTime.setText(pe.getCreate_time());
				
				/*SpannableString spannableString = FaceConversionUtil.getInstace()
						.getExpressionString(context, pe.getContent());*/
				
				Spannable span_replay = SmileUtils.getSmiledText(context, pe.getContent());
				
				holder.tvContent.setText(span_replay, BufferType.SPANNABLE);
				
				//holder.tvContent.setText(spannableString);
			}catch(Exception e){
				e.printStackTrace();
			}
			break;
		case 1:
			convertView=LayoutInflater.from(context).inflate(R.layout.empty_view, null);
			break;
		}
		
		
		
		
		return convertView;
	}
	
	class Holder{
		CircleImageView userIcon;
		TextView tvUserNick;
		TextView tvCreateTime;
		TextView tvContent;
		
		public Holder(View convertView){
			userIcon=(CircleImageView)convertView.findViewById(R.id.civUserIcon);
			tvUserNick=(TextView)convertView.findViewById(R.id.tvUserNick);
			tvCreateTime=(TextView)convertView.findViewById(R.id.tvCreateTime);
			tvContent=(TextView)convertView.findViewById(R.id.tvContent);
		}
	}
}

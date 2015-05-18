package com.skinrun.trunk.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.app.base.entity.SuggestionFeedBackDataEntity;
import com.app.base.init.ACache;
import com.avoscloud.chat.service.UserService;
import com.avoscloud.chat.util.PhotoUtils;
import com.base.service.impl.HttpClientUtils;
import com.beabox.hjy.tt.R;
import com.idongler.widgets.CircleImageView;

public class SuggestionFeedBackAdapter extends BaseAdapter {
	SimpleDateFormat formatAll = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private ArrayList<SuggestionFeedBackDataEntity> arrayList;
	private Context context;
	private LayoutInflater layoutInflater;
	 RequestQueue mQueue ; 
	public SuggestionFeedBackAdapter(ArrayList<SuggestionFeedBackDataEntity> list, Context c) {
		arrayList = list;
		context = c;
		layoutInflater = LayoutInflater.from(context) ;
		mQueue = Volley.newRequestQueue(context);
	}

	@Override
	public int getCount() {
		if(arrayList==null&&arrayList.size()==0){
			return 0 ;
		}
		//Collections.sort(arrayList);
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

	public static String type = "" ;
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		SuggestionFeedBackDataEntity entity = arrayList.get(position) ;
		final ViewHolder viewHolder;
		viewHolder = new ViewHolder();
		convertView = createViewByType(entity.getType());
		
		viewHolder.release_time = (TextView) convertView
				.findViewById(R.id.release_time);
		viewHolder.tv_chatcontent = (TextView) convertView
				.findViewById(R.id.tv_chatcontent);
		viewHolder.tv_userid = (TextView) convertView
				.findViewById(R.id.tv_userid);
		viewHolder.iv_userhead = (CircleImageView) convertView
				.findViewById(R.id.iv_userhead);
	    
		convertView.setTag(viewHolder);
	    
	    if(entity.getType() != 1){
	    	UserService.imageLoader.displayImage(/*HttpClientUtils.IMAGE_URL
		    		+"" + entity.getUser_img()+*/ACache.get(context).getAsString("avatar")+"?time="+System.currentTimeMillis(), viewHolder.iv_userhead,PhotoUtils.myPicImageOptions);
	    	viewHolder.tv_userid.setText(ACache.get(context).getAsString("nickname"));
	    }else{
		    UserService.imageLoader.displayImage(/*HttpClientUtils.IMAGE_URL
					+ */"" + entity.getGuanjia_img(), viewHolder.iv_userhead,PhotoUtils.myPicImageOptions);
		    viewHolder.tv_userid.setText(entity.getUname()+"");
		    
	    }
	    viewHolder.tv_chatcontent.setText(entity.getContent());
	    viewHolder.release_time.setText(entity.getRelease_time());
	    
	    return convertView;
	}
	
	class ViewHolder {
		public TextView release_time; //发送时间
		public TextView tv_chatcontent; //发送者内容
		public TextView tv_userid; //发送者昵称
		public CircleImageView iv_userhead; //用户图像
	}
	
	public View createViewByType(int type) {
		View baseView = null;
		if (1 == type) {
			baseView = layoutInflater.inflate(R.layout.suggestion_data_listview_item_left,
					null);
		} else {
			baseView = layoutInflater.inflate(R.layout.suggestion_data_listview_item_right,
					null);
		} 
		return baseView;
	}

}

package com.skinrun.trunk.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.app.base.entity.ActivityDataEntity;
import com.avoscloud.chat.service.UserService;
import com.avoscloud.chat.util.PhotoUtils;
import com.base.app.utils.ImageParamSetter;
import com.base.service.impl.HttpClientUtils;
import com.beabox.hjy.tt.R;

public class ActivityDataListAdapter extends BaseAdapter {
	SimpleDateFormat formatAll = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private ArrayList<ActivityDataEntity> arrayList;
	private Context context;
	private LayoutInflater layoutInflater;
	 RequestQueue mQueue ; 
	public ActivityDataListAdapter(ArrayList<ActivityDataEntity> list, Context c) {
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
		Collections.sort(arrayList);
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

		final ViewHolder holder;
	    if (convertView == null) {
	      convertView = layoutInflater.inflate(R.layout.activity_data_listview_item, null);
	      holder = new ViewHolder(convertView);
	      convertView.setTag(holder);
	    } else {
	    	 holder = (ViewHolder) convertView.getTag();
	    }
	    
	    ActivityDataEntity entity = arrayList.get(position) ;
	    
	    if (position == 0) {  
            holder.action_type.setVisibility(View.VISIBLE);  
            holder.category.setText(""+entity.getCategory());
        } else {  
            String lastCatalog = arrayList.get(position - 1).getCategory();  
            if (entity.getCategory().equals(lastCatalog)) {  
                holder.action_type.setVisibility(View.GONE);  
            } else {  
                holder.action_type.setVisibility(View.VISIBLE);  
                holder.category.setText(""+entity.getCategory());
            }  
        }  
	    try{
	    	ImageParamSetter.setImageXFull(context, holder.action_img, 276*1.0/640, 10);
	    	
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
	    
	    UserService.imageLoader.displayImage(HttpClientUtils.IMAGE_URL
				+ "" + entity.getActivity_img(), holder.action_img,PhotoUtils.articleImageOptions);
	    //缓存
//		ImageRequest imageRequest = new ImageRequest(HttpClientUtils.IMAGE_URL
//				+ "" + entity.getActivity_img(),
//				new Response.Listener<Bitmap>() {
//					@Override
//					public void onResponse(Bitmap response) {
//						holder.action_img.setImageBitmap(response);
//					}
//				}, 0, 0, Config.RGB_565, new Response.ErrorListener() {
//					@Override
//					public void onErrorResponse(VolleyError error) {
//						//holder.action_img.setImageResource(R.drawable.home_default);
//					}
//				});
//		mQueue.add(imageRequest);  
	    
	   //UrlImageViewHelper.setUrlDrawable(holder.action_img, HttpClientUtils.IMAGE_URL+""+entity.getActivity_img(), R.drawable.home_default);
	   
	    holder.activity_title.setText(""+entity.getActivity_title());
		//activity_name.setText(""+entity.getActivity_name());
		//[99:未公布，100：火热进行中，101：活动已结束，等待公布!，102：获奖公布中，103：往期回顾]
		
	    /*if(type != entity.getCategory()){
			holder.action_type.setVisibility(View.VISIBLE);
			type = entity.getCategory() ;
		}else if(type == entity.getCategory()){
			holder.action_type.setVisibility(View.GONE);
			
		}*/
	    //holder.category.setText(""+entity.getCategory());
	   
	    
		if(entity.getStatus() == 100){
			try {
				Date now = new Date();
				Date tonight_date = formatAll.parse(entity.getActivity_left_time());// 
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
				
			}
			
		}else{
			holder.activity_left_time.setText("活动已结束");
		}
		
		holder.participant.setText(""+entity.getParticipant());
	    return convertView;
	}
	
	class ViewHolder {
	    ImageView action_img;
	    TextView category;
	    TextView activity_title;
	    TextView activity_left_time;
	    TextView participant;
	    View action_type ;
	 
	    public ViewHolder(View convertView) {
	    	action_type = convertView.findViewById(R.id.action_type);
			category = (TextView) convertView.findViewById(R.id.category);
			activity_title = (TextView) convertView.findViewById(R.id.activity_title);
			activity_left_time = (TextView) convertView.findViewById(R.id.activity_left_time);
			participant = (TextView) convertView.findViewById(R.id.participant);
			action_img = (ImageView) convertView.findViewById(R.id.action_img);
	    }
	  }

}

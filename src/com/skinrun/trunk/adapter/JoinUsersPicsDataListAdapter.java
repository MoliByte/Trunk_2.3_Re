package com.skinrun.trunk.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.app.base.entity.PraiseJoinEntity;
import com.avoscloud.chat.service.UserService;
import com.avoscloud.chat.util.PhotoUtils;
import com.base.app.utils.StringUtil;
import com.base.service.impl.HttpClientUtils;
import com.beabox.hjy.tt.R;

public class JoinUsersPicsDataListAdapter extends BaseAdapter {

	private ArrayList<PraiseJoinEntity> arrayList;
	private Context context;
	private LayoutInflater layoutInflater;
	public JoinUsersPicsDataListAdapter(ArrayList<PraiseJoinEntity> list, Context c) {
		arrayList = list;
		context = c;
	}
	private String TAG="JoinUsersPicsDataListAdapter";

	@Override
	public int getCount() {
		if(arrayList==null){
			return 0 ;
		}
		if(arrayList!=null && arrayList.size() >= 7){
			return 7 ;
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
		
		if (convertView == null) {
			convertView = layoutInflater.from(context).inflate(R.layout.join_user_pic_item, null);;
		}
		//int size = arrayList.keySet().size() ;
		
			ImageView join_user_pic = (ImageView) convertView.findViewById(R.id.join_user_pic);
			PraiseJoinEntity p=arrayList.get(position);
			if(p.getUser_avatar()!=null&&!p.getUser_avatar().equals("")){
				//UrlImageViewHelper.setUrlDrawable(join_user_pic, p.getUser_avatar(), R.drawable.my_pic_default);
				UserService.imageLoader.displayImage(""+p.getUser_avatar(), join_user_pic,
						PhotoUtils.myPicImageOptions);
			}else{
				Log.e(TAG, "=======用UID取头像："+arrayList.get(position).getUid());
//				UrlImageViewHelper.setUrlDrawable(join_user_pic, HttpClientUtils.USER_IMAGE_URL+""+
//						StringUtil.getPathByUid(String.valueOf(arrayList.get(position).getUid())), R.drawable.my_pic_default);
				
				UserService.imageLoader.displayImage(""+HttpClientUtils.USER_IMAGE_URL+""+
						StringUtil.getPathByUid(String.valueOf(arrayList.get(position).getUid())), join_user_pic,
						PhotoUtils.myPicImageOptions);
			}
		
		return convertView;
	}

}

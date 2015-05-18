package com.skinrun.trunk.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.base.entity.MyMasterEntity;
import com.avoscloud.chat.service.UserService;
import com.avoscloud.chat.util.PhotoUtils;
import com.beabox.hjy.tt.R;

public class MyMasterListAdapter extends BaseAdapter {

	private ArrayList<MyMasterEntity> arrayList;
	private Context context;
	private LayoutInflater layoutInflater;
	public MyMasterListAdapter(ArrayList<MyMasterEntity> list, Context c) {
		arrayList = list;
		context = c;
	}

	@Override
	public int getCount() {
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

		MyMasterEntity entity = arrayList.get(position) ;
		if (convertView == null) {
			convertView = layoutInflater.from(context).inflate(R.layout.my_master_listview_item, null);;
		}
		
		/*private String master_name ;//导师名称
		private String master_pic;//导师图像
		private String master_level;//导师级别
		private String master_organization;//导师机构
*/		TextView master_name = (TextView) convertView.findViewById(R.id.master_name);
		master_name.setText(""+entity.getMaster_name());
		
		TextView master_level = (TextView) convertView.findViewById(R.id.master_level);
		master_level.setText(""+entity.getMaster_level());
		
		TextView master_organization = (TextView) convertView.findViewById(R.id.master_organization);
		master_organization.setText(""+entity.getMaster_organization());
		
		ImageView master_pic = (ImageView) convertView.findViewById(R.id.master_pic);
		//UrlImageViewHelper.setUrlDrawable(master_pic, entity.getMaster_pic());
		UserService.imageLoader.displayImage(""+entity.getMaster_pic(), master_pic,
				PhotoUtils.myPicImageOptions);
		
		return convertView;
	}

}

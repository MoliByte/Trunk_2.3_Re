package com.skinrun.trunk.adapter;

import java.util.ArrayList;

import com.app.base.entity.MyLovedMerchantEntity;
import com.avoscloud.chat.service.UserService;
import com.avoscloud.chat.util.PhotoUtils;
import com.beabox.hjy.tt.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MerchantAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<MyLovedMerchantEntity> data;

	public MerchantAdapter(Context context, ArrayList<MyLovedMerchantEntity> data) {
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
	public Object getItem(int arg0) {
		return data.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder=null;
		if(convertView==null){
			convertView=LayoutInflater.from(context).inflate(R.layout.merchant_list_item, null);
			holder=new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder) convertView.getTag();
		}
		
		MyLovedMerchantEntity entity=data.get(position);
		if(entity.getLogo()!=null&&!entity.getLogo().equals("")){
			UserService.imageLoader.displayImage(entity.getLogo(), holder.ivMerchantLogo,PhotoUtils.myPicImageOptions);
		}
		
		holder.tvMerchantName.setText(entity.getName());
		
		return convertView;
	}
	
	
	class ViewHolder {
		ImageView ivMerchantLogo;
		TextView tvMerchantName;
		public ViewHolder(View convertView){
			ivMerchantLogo=(ImageView)convertView.findViewById(R.id.ivMerchantLogo);
			tvMerchantName=(TextView)convertView.findViewById(R.id.tvMerchantName);
		}
	}
}

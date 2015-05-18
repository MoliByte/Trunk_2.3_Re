package com.skinrun.trunk.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.app.base.entity.GoldCoinsEntity;
import com.base.app.utils.StringUtil;
import com.beabox.hjy.tt.R;

public class CoinsRecordListAdapter extends BaseAdapter {

	private ArrayList<GoldCoinsEntity> arrayList;
	private Context context;

	public CoinsRecordListAdapter(ArrayList<GoldCoinsEntity> list, Context c) {
		arrayList = list;
		context = c;
	}

	@Override
	public int getCount() {
		if(arrayList == null){
			return 0 ;
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

		GoldCoinsEntity entity = arrayList.get(position);
		
		ViewHolder holder=null;
		
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.coin_record_listview_item, null);
			holder=new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder) convertView.getTag();
		}

		holder.record_type.setText("" + entity.getType());

	
		holder.record_time.setText("" + entity.getCreate_time());

		holder.consumption.setText("" + StringUtil.ToDBC(entity.getCredit()));

		return convertView;
	}
	class ViewHolder{
		TextView record_type;
		TextView record_time;
		TextView consumption;
		
		public ViewHolder(View convertView){
			 record_type = (TextView) convertView.findViewById(R.id.record_type);
			 record_time = (TextView) convertView.findViewById(R.id.record_time);
			 consumption = (TextView) convertView.findViewById(R.id.consumption);
		}
	}

}

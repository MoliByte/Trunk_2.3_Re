package com.skinrun.trunk.adapter;

import java.util.ArrayList;

import com.app.base.entity.BrandProductEntity;
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

public class BrandProductAdapter extends BaseAdapter {
	
	private Context context;
	ArrayList<BrandProductEntity> data;
	
	public BrandProductAdapter(Context context,
			ArrayList<BrandProductEntity> data) {
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
			convertView=LayoutInflater.from(context).inflate(R.layout.product_grid_item, null);
			holder=new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder) convertView.getTag();
		}
		BrandProductEntity entity=data.get(position);
		if(entity.getImg_url()!=null&&!entity.getImg_url().equals("")){
			UserService.imageLoader.displayImage(entity.getImg_url(), holder.ivProductIcon,PhotoUtils.articleImageOptions);
		}
		
		holder.tvBrandName.setText("");
		holder.tvBrandName.setVisibility(View.GONE);
		holder.tvProductName.setText(entity.getProduct_name()+"");
		
		holder.tvPraiseTimes.setText(entity.getPraise_count()+"");
		holder.tvTestTimes.setText(entity.getTest_count()+"");
		
		new ClickHandler(holder.tvPraiseTimes, position);
		
		return convertView;
	}
	
	class ViewHolder{
		ImageView ivProductIcon;
		TextView tvBrandName;
		TextView tvProductName;
		TextView tvTestTimes;
		TextView tvPraiseTimes;
		View praiseContainer;
		
		public ViewHolder( View convertView){
			ivProductIcon=(ImageView)convertView.findViewById(R.id.ivProductIcon);
			tvBrandName=(TextView)convertView.findViewById(R.id.tvBrandName);
			tvProductName=(TextView)convertView.findViewById(R.id.tvProductName);
			tvTestTimes=(TextView)convertView.findViewById(R.id.tvTestTimes);
			tvPraiseTimes=(TextView)convertView.findViewById(R.id.tvPraiseTimes);
			praiseContainer=convertView.findViewById(R.id.praiseContainer);
		}
	}
	
	class ClickHandler implements View.OnClickListener{
		TextView tvPraiseNum;
		int position;
		
		public ClickHandler(TextView tvPraiseNum, int position) {
			super();
			this.tvPraiseNum = tvPraiseNum;
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.praiseContainer:
				BrandProductEntity entity=data.get(position);
				entity.setPraise_count(entity.getPraise_count()+1);
				tvPraiseNum.setText(entity.getPraise_count()+"");
				
				break;
			}
		}
	}
}

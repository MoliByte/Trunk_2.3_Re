package com.skinrun.trunk.facial.mask.test;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.app.base.entity.ProductEntity;
import com.beabox.hjy.tt.R;

public class ProductAdapter extends BaseAdapter {
	private ArrayList<ProductEntity> products;
	private Context context;
	
	public ProductAdapter(ArrayList<ProductEntity> products, Context context) {
		super();
		this.products = products;
		this.context=context;
	}
	
	

	public void setProducts(ArrayList<ProductEntity> products) {
		this.products = products;
	}



	@Override
	public int getCount() {
		if(products==null||products.size()==0){
			return 0;
		}
		return products.size();
	}

	@Override
	public Object getItem(int arg0) {
		return products.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View convertView, ViewGroup parent) {
		Holder holder=null;
		if(convertView==null){
			convertView=LayoutInflater.from(context).inflate(R.layout.product_item, null);
			holder=new Holder();
			holder.tv=(TextView) convertView.findViewById(R.id.tvProductItem);
			convertView.setTag(holder);
		}else{
			holder=(Holder) convertView.getTag();
		}
		holder.tv.setText(products.get(arg0).getPro_brand_name());
		return convertView;
	}
	
	class Holder{
		TextView tv;
	}
}

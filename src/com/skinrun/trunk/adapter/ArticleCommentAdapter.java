package com.skinrun.trunk.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.app.base.entity.ArticleCommentEntity;
import com.beabox.hjy.tt.R;

public class ArticleCommentAdapter extends BaseAdapter {
	private final static String TAG  = "ArticleCommentAdapter" ;
	private ArrayList<ArticleCommentEntity> arrayList;
	private Context context;
	public ArticleCommentAdapter(ArrayList<ArticleCommentEntity> list, Context c) {
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

		ArticleCommentEntity entity = arrayList.get(position) ;
		MyHolder holder=null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.article_replay_list_view_item, null);
			holder=new MyHolder();
			holder.from=(TextView) convertView.findViewById(R.id.from);
			holder.to=(TextView) convertView.findViewById(R.id.to);
			holder.comment= (TextView) convertView.findViewById(R.id.comment);
			convertView.setTag(holder);
		}else{
			holder=(MyHolder) convertView.getTag();
		}
		holder.from.setText(""+entity.getFrom_nick());
		
		holder.to.setText(""+entity.getTo_nick());
		
		holder.comment.setText(""+entity.getContent());
		
		return convertView;
	}
	
	class MyHolder{
		TextView from;
		TextView to;
		TextView comment;
	}

}

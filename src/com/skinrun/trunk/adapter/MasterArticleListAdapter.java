package com.skinrun.trunk.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.base.entity.MasterArticleEntity;
import com.avoscloud.chat.service.UserService;
import com.avoscloud.chat.util.PhotoUtils;
import com.beabox.hjy.tt.R;

public class MasterArticleListAdapter extends BaseAdapter {

	private ArrayList<MasterArticleEntity> arrayList;
	private Context context;
	private LayoutInflater layoutInflater;
	public MasterArticleListAdapter(ArrayList<MasterArticleEntity> list, Context c) {
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

		MasterArticleEntity entity = arrayList.get(position) ;
		if (convertView == null) {
			convertView = layoutInflater.from(context).inflate(R.layout.master_article_listview_item, null);;
		}
		
		TextView title = (TextView) convertView.findViewById(R.id.title);
		title.setText(""+entity.getTitle());
		
		TextView release_time = (TextView) convertView.findViewById(R.id.release_time);
		release_time.setText(""+entity.getRelease_time());
		
		TextView content = (TextView) convertView.findViewById(R.id.content);
		content.setText(""+entity.getContent());
		
		TextView support = (TextView) convertView.findViewById(R.id.support);
		support.setText(""+entity.getSupport_count());
		
		TextView comment_count = (TextView) convertView.findViewById(R.id.comment_count);
		comment_count.setText(""+entity.getComment_count());
		
		ImageView pic_url = (ImageView) convertView.findViewById(R.id.pic_url);
		//UrlImageViewHelper.setUrlDrawable(pic_url, entity.getPic_url());
		UserService.imageLoader.displayImage(""+entity.getPic_url(), pic_url,
				PhotoUtils.normalImageOptions);
		return convertView;
	}

}

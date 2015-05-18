package com.skinrun.trunk.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.base.entity.HomeDataEntity;
import com.avoscloud.chat.service.UserService;
import com.avoscloud.chat.util.PhotoUtils;
import com.base.app.utils.StringUtil;
import com.base.service.impl.HttpClientUtils;
import com.beabox.hjy.tt.R;

public class HomeDataListAdapter extends BaseAdapter {
	
	private ArrayList<HomeDataEntity> arrayList;
	private Context context;
	public HomeDataListAdapter(ArrayList<HomeDataEntity> list, Context c) {
		arrayList = list;
		context = c;
	}

	@Override
	public int getCount() {
		if(arrayList==null){
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

		HomeDataEntity entity = arrayList.get(position) ;
		
		ViewHolder holder=null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.home_data_listview_item, null);
			holder=new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder) convertView.getTag();
		}
		
		holder.username.setText(""+entity.getUsername());
		
		holder.release_time.setText(""+StringUtil.humanmizeTime(entity.getReleaseTime()));
		
		holder.support.setText(""+entity.getSupport_count());
		
		holder.comment_count.setText(""+entity.getComment_count());
		
		//TextView introduce = (TextView) convertView.findViewById(R.id.introduce);
		//introduce.setText(""+entity.getContent());
		
		holder.title.setText(""+entity.getTitle());
		
		holder.details.setText(""+StringUtil.ToDBC(entity.getDetails()));
		
		//com.idongler.widgets.CircleImageView author_pic = (com.idongler.widgets.CircleImageView) convertView.findViewById(R.id.author_pic);
		//article_picture.setAdjustViewBounds(true);
		//article_picture.setScaleType(ScaleType.FIT_CENTER);
		
		//UrlImageViewHelper.setUrlDrawable(holder.article_picture, HttpClientUtils.IMAGE_URL+entity.getAdPic(), R.drawable.home_default);
		//UrlImageViewHelper.setUrlDrawable(holder.author_pic, HttpClientUtils.AUTHOR_IMAGE_URL+entity.getAuthor_img(), R.drawable.my_pic_default);
		
		UserService.imageLoader.displayImage(""+HttpClientUtils.IMAGE_URL+entity.getAdPic(), holder.article_picture,
				PhotoUtils.articleImageOptions);
		UserService.imageLoader.displayImage(""+HttpClientUtils.AUTHOR_IMAGE_URL+entity.getAuthor_img(), holder.author_pic,
				PhotoUtils.myPicImageOptions);
		
		//KJLoger.debug("HomeDataListAdapter :"+HttpClientUtils.AUTHOR_IMAGE_URL+entity.getAuthor_img());
		//KJBitmap.create().display(holder.author_pic, HttpClientUtils.AUTHOR_IMAGE_URL+entity.getImgURL());
		
		return convertView;
	}
	class ViewHolder{
		
		TextView username;
		TextView release_time;
		TextView support;
		TextView comment_count;
		TextView title;
		TextView details;
		ImageView article_picture;
		ImageView author_pic;
		
		public ViewHolder(View convertView){
			 username = (TextView) convertView.findViewById(R.id.username);
			 release_time = (TextView) convertView.findViewById(R.id.release_time);
			 support = (TextView) convertView.findViewById(R.id.support);
			 comment_count = (TextView) convertView.findViewById(R.id.comment_count);
			 title = (TextView) convertView.findViewById(R.id.title);
			 details = (TextView) convertView.findViewById(R.id.details);
			 article_picture = (ImageView) convertView.findViewById(R.id.article_picture);
			 author_pic = (ImageView) convertView.findViewById(R.id.author_pic);
		}
	}
}

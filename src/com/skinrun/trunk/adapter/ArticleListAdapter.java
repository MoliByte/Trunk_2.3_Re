package com.skinrun.trunk.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.base.entity.ArticleCommentEntity;
import com.app.base.entity.ArticleReplayEntity;
import com.base.facedemo.FaceConversionUtil;
import com.beabox.hjy.tt.R;

public class ArticleListAdapter extends BaseAdapter {
	private final static String TAG  = "ArticleListAdapter" ;
	private ArrayList<ArticleCommentEntity> arrayList;
	private Context context;
	public ArticleListAdapter(ArrayList<ArticleCommentEntity> list, Context c) {
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

		ArticleCommentEntity entity = arrayList.get(position) ;
		
		ViewHolder viewHolder=null;
		if(convertView==null){
			convertView = LayoutInflater.from(context).inflate(R.layout.article_listview_item, null);
			viewHolder=new ViewHolder(convertView);
			convertView.setTag(viewHolder);
		}else{
			viewHolder=(ViewHolder) convertView.getTag();
		}
		
		viewHolder.author.setText(""+entity.getFrom_nick());
		
		viewHolder.level.setText("Lv1");
		
		viewHolder.time.setText(""+entity.getCreate_time());
		
		SpannableString spannableString = FaceConversionUtil.getInstace().getExpressionString(context, entity.getContent());
		viewHolder.comment.setText(spannableString);
		
		viewHolder.support.setText(""+entity.getSupport_count());
		
		viewHolder.comment_count.setText(""+entity.getComment_count());
		
		
		//UrlImageViewHelper.setUrlDrawable(viewHolder.master_pic, "");//from_uid     000/008/090/source.jpg
		
		viewHolder.comment_list_layout =  (LinearLayout) convertView.findViewById(R.id.comment_list_layout); 
		//LinearLayout.LayoutParams  parms = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		
		//comment_list_layout.setLayoutParams(parms);
		ArrayList<ArticleReplayEntity> arrayList = entity.getReplys() ;
		
		if(arrayList!=null&&arrayList.size()>0){
			viewHolder.comment_list_layout.removeAllViews() ;
			ArrayList<View>   v = new ArrayList<View>();
			for (int j = 0 ;j < arrayList.size();j++) {
				View convertView_ = LayoutInflater.from(context).inflate(R.layout.article_replay_list_view_item, null);
				ArticleReplayEntity articleReplayEntity = arrayList.get(j);
				if(null!=articleReplayEntity){
					TextView from = (TextView) convertView_.findViewById(R.id.from);
					from.setText(""+articleReplayEntity.getFrom_nick());
					
					TextView to = (TextView) convertView_.findViewById(R.id.to);
					to.setText(""+articleReplayEntity.getTo_nick());
					
					TextView comment_ = (TextView) convertView_.findViewById(R.id.replay_comment);
					comment_.setText(""+articleReplayEntity.getContent());
					v.add(convertView_);
					
				}
			}
			for (int i = 0; i < v.size(); i++) {
				viewHolder.comment_list_layout.addView(v.get(i),i);
			}
			
			/*ArticleReplayEntity articleReplayEntity = entity.getReplys() .get(0);
			TextView from = (TextView) convertView.findViewById(R.id.from);
			from.setText(""+articleReplayEntity.getFrom_nick());
			
			TextView to = (TextView) convertView.findViewById(R.id.to);
			to.setText(""+articleReplayEntity.getTo_nick());
			
			TextView comment_ = (TextView) convertView.findViewById(R.id.replay_comment);
			comment_.setText(""+articleReplayEntity.getContent());*/
		}else{
			viewHolder.comment_list_layout.setVisibility(View.GONE);
		}
		
		/*
		LinearLayout comment_list_layout =  (LinearLayout) convertView.findViewById(R.id.comment_list_layout); 
		LinearLayout.LayoutParams  parms = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		comment_list_layout.setLayoutParams(parms);
		ArrayList<ArticleCommentEntity> arrayList = entity.getCommentList() ;
		for (int j = 0 ;j < arrayList.size();j++) {
			View convertView_ = LayoutInflater.from(context).inflate(R.layout.article_replay_list_view_item, null);
			ArticleCommentEntity articleCommentEntity = arrayList.get(j);
			Log.e(TAG, ""+articleCommentEntity.getFrom()+";"+articleCommentEntity.getTo()+";"+articleCommentEntity.getComment());
			TextView from = (TextView) convertView_.findViewById(R.id.from);
			from.setText(""+articleCommentEntity.getFrom());
			
			TextView to = (TextView) convertView_.findViewById(R.id.to);
			to.setText(""+articleCommentEntity.getTo());
			
			TextView comment_ = (TextView) convertView_.findViewById(R.id.comment);
			comment_.setText(""+articleCommentEntity.getComment());
			comment_list_layout.addView(convertView_, j);
		}*/
		
		/*PullToRefreshListView article_comment_listview = (PullToRefreshListView) convertView.findViewById(R.id.article_comment_listview);
		Log.e("comment_list_size ---->", ""+entity.getCommentList().size());
		ArticleCommentAdapter adapter = new ArticleCommentAdapter(entity.getCommentList(), context);
		article_comment_listview.getRefreshableView().setAdapter(adapter);
		adapter.notifyDataSetChanged() ;*/
		
		return convertView;
	}
	class ViewHolder{
		TextView author;
		TextView level;
		TextView time;
		TextView comment;
		TextView support;
		TextView comment_count;
		ImageView master_pic;
		LinearLayout comment_list_layout;
		public ViewHolder(View convertView){
			author=(TextView) convertView.findViewById(R.id.author);
			level = (TextView) convertView.findViewById(R.id.level);
			time = (TextView) convertView.findViewById(R.id.time);
			comment = (TextView) convertView.findViewById(R.id.comment);
			support = (TextView) convertView.findViewById(R.id.support);
			comment_count = (TextView) convertView.findViewById(R.id.comment_count);
			master_pic = (ImageView) convertView.findViewById(R.id.master_pic);
			comment_list_layout =  (LinearLayout) convertView.findViewById(R.id.comment_list_layout); 
		}
	}

}

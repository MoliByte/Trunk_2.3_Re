package com.avoscloud.chat.base;

import java.io.File;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.avoscloud.chat.entity.Msg;
import com.avoscloud.chat.service.UserService;
import com.avoscloud.chat.util.PhotoUtils;
import com.beabox.hjy.tt.ImageBrowerActivity;
import com.beabox.hjy.tt.R;
import com.idongler.widgets.CircleImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 
 ****************************************** 
 * @文件名称 : ChatMsgAdapter.java
 * @创建时间 : 2013-1-27 下午02:33:16
 * @文件描述 : 消息数据填充起
 ****************************************** 
 */
public class ChatMsgAdapter extends BaseAdapter {
	private String TAG = "ChatMsgAdapter";
	
	//static KJBitmap kjBitmap = KJBitmap.create() ;
	
	public static interface IMsgViewType {
		int IMVT_COM_MSG = 0;
		int IMVT_TO_MSG = 1;
	}

	private List<ChatMsgEntity> coll;
	private LayoutInflater mInflater;
	private Context context;
	private Activity activity ;

	public ChatMsgAdapter(Context context,Activity activity , List<ChatMsgEntity> coll) {
		this.coll = coll;
		mInflater = LayoutInflater.from(context);
		this.context = context;
		this.activity = activity;
//		KJBitmapConfig cfg = new KJBitmapConfig() ;
//		kjBitmap.setConfig(cfg);
//		kjBitmap.configOpenDiskCache(true);
	}

	public int getCount() {
		if(coll == null){
			return 0 ;
		}
		return coll.size();
	}

	public Object getItem(int position) {
		return coll.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	/**
	 * 根据数据源的position返回需要显示的的layout的type
	 * 
	 * */
	@Override
	public int getItemViewType(int position) {
		ChatMsgEntity entity = coll.get(position);

		if (entity.getMsgType()) {
			return IMsgViewType.IMVT_COM_MSG;
		} else {
			return IMsgViewType.IMVT_TO_MSG;
		}
	}

	/**
	 * 返回所有的layout的数量
	 * 
	 * */
	@Override
	public int getViewTypeCount() {
		return 2;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		final ChatMsgEntity entity = coll.get(position);
		boolean isComMsg = entity.getMsgType();

		final ViewHolder viewHolder;
		//if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = createViewByType(entity.getType(), isComMsg);
			if (Msg.Type.Text == entity.getType()) {
//				Log.e(TAG, LeanCloudChatActivity.MSG_TEXT_TYPE);
				viewHolder.tvSendTime = (TextView) convertView
						.findViewById(R.id.tv_sendtime);
				viewHolder.tvContent = (TextView) convertView
						.findViewById(R.id.tv_chatcontent);
				viewHolder.name = (TextView) convertView
						.findViewById(R.id.tv_userid);
				viewHolder.avatar = (CircleImageView) convertView
						.findViewById(R.id.iv_userhead);
				viewHolder.isComMsg = isComMsg;
				
			} else if (Msg.Type.Image == entity.getType()) {
//				Log.e(TAG, LeanCloudChatActivity.MSG_IMG_TYPE);
				viewHolder.tvSendTime = (TextView) convertView
						.findViewById(R.id.tv_sendtime);
				viewHolder.iv_sendPicture = (ImageView) convertView
						.findViewById(R.id.iv_sendPicture);
				viewHolder.isComMsg = isComMsg;
				
				viewHolder.avatar = (CircleImageView) convertView
						.findViewById(R.id.iv_userhead);
				
				viewHolder.name = (TextView) convertView
						.findViewById(R.id.tv_userid);
			}
			convertView.setTag(viewHolder);
//		} else {
			//viewHolder = (ViewHolder) convertView.getTag();
//		}
		
		if(Msg.Type.Text == entity.getType()){
// 			viewHolder.tvSendTime.setText(entity.getDate());
			Spannable span = SmileUtils.getSmiledText(context, entity.getText());
			viewHolder.tvContent.setText(span, BufferType.SPANNABLE);
			viewHolder.name.setText(""+entity.getName());
			try {
				//显示用户图像
				/*displayUserImageByUri(
						viewHolder.avatar,
						"",
						HttpClientUtils.USER_IMAGE_URL
								+ StringUtil.getPathByUid("" + entity.getUid())
								+ "?position=" + position);*/
				displayUserImageByUri(
						viewHolder.avatar,
						"",
						entity.getAvatar()==null ?"" :entity.getAvatar()+"?position="+position
						);
				
				/*if(!SHARE_MEDIA.WEIXIN.equals(ACache.get(context).getAsString("type"))
						&&!SHARE_MEDIA.SINA.equals(ACache.get(context).getAsString("type"))
						&&!SHARE_MEDIA.QZONE.equals(ACache.get(context).getAsString("type"))){
					displayUserImageByUri(
							viewHolder.avatar,
							"",
							HttpClientUtils.USER_IMAGE_URL
									+ StringUtil.getPathByUid("" + entity.getUid())
									+ "?position=" + position);
						
				}else{
					displayUserImageByUri(
							viewHolder.avatar,
							"",
							""+ACache.get(context).getAsString("avatar"));
				}*/
				
				Log.e(TAG, "txt avatar = "+entity.getAvatar());
				//				UrlImageViewHelper.setUrlDrawable(viewHolder.avatar, HttpClientUtils.USER_IMAGE_URL+StringUtil.getPathByUid(""+entity.getUid()), R.drawable.mini_avatar_shadow);
//				kjBitmap.display(viewHolder.avatar, HttpClientUtils.USER_IMAGE_URL+StringUtil.getPathByUid(""+entity.getUid()), false);
//				UserService.displayAvatar(HttpClientUtils.USER_IMAGE_URL+StringUtil.getPathByUid(""+entity.getUid()), viewHolder.avatar);
			} catch (Exception e) {
				// TODO: handle exception
			} catch(OutOfMemoryError e){
				
			}
			
		}else if(Msg.Type.Image == entity.getType()){
			//KJBitmap.create().display(viewHolder.img_chatcontent,entity.getText());
			viewHolder.name.setText(""+entity.getName());
			try {
//				kjBitmap.display(viewHolder.iv_sendPicture, entity.getText(), false);
//				if(null == entity.getLocalpath() || "".equals(entity.getLocalpath())){
//					kjBitmap.display(viewHolder.iv_sendPicture, entity.getText(), false);
//				}
//				
//				if(!"".equals(entity.getLocalpath()) && null != entity.getLocalpath()){
//					
//				}
				
				displayUserImageByUri(
						viewHolder.avatar,
						"",
						entity.getAvatar()==null ?"" :entity.getAvatar().replaceAll("\\\\", "")
						+"?position="+position);
				
				displayImageByUri(viewHolder.iv_sendPicture, entity.getLocalpath(),entity.getText() );
				setImageOnClickListener(entity.getLocalpath(), entity.getText(), viewHolder.iv_sendPicture);
//				PhotoViewAttacher mAttacher = new PhotoViewAttacher(viewHolder.iv_sendPicture);
				
//				if(entity.isComMeg()){
//					displayImageByUri(viewHolder.iv_sendPicture, entity.getLocalpath(), entity.getText());
//				}else{
//					
//				}
				
				
				/*if(!SHARE_MEDIA.WEIXIN.equals(ACache.get(context).getAsString("type"))
						&&!SHARE_MEDIA.SINA.equals(ACache.get(context).getAsString("type"))
						&&!SHARE_MEDIA.QZONE.equals(ACache.get(context).getAsString("type"))){
					displayUserImageByUri(
							viewHolder.avatar,
							"",
							HttpClientUtils.USER_IMAGE_URL
									+ StringUtil.getPathByUid("" + entity.getUid())
									+ "?position=" + position);
						
				}else{
					
					displayUserImageByUri(
							viewHolder.avatar,
							"",
							""+ACache.get(context).getAsString("avatar"));
				}*/
				
//				UrlImageViewHelper.setUrlDrawable(viewHolder.avatar, HttpClientUtils.USER_IMAGE_URL+StringUtil.getPathByUid(""+entity.getUid()), R.drawable.mini_avatar_shadow);
//				kjBitmap.display(viewHolder.avatar, HttpClientUtils.USER_IMAGE_URL+StringUtil.getPathByUid(""+entity.getUid()), false);
//				UserService.displayAvatar(HttpClientUtils.USER_IMAGE_URL+StringUtil.getPathByUid(""+entity.getUid()), viewHolder.avatar);
				
				Log.e(TAG, "img avatar = "+entity.getAvatar());
			} catch (Exception e) {
				
			} catch(OutOfMemoryError e){
				
			}
			
//			viewHolder.tvSendTime.setText(entity.getDate());
		}
		
		if (position == 0) {  
			viewHolder.tvSendTime.setVisibility(View.VISIBLE);  
			viewHolder.tvSendTime.setText(""+entity.getDate().substring(0, 16));
        } else {  
            String lastCatalog = coll.get(position - 1).getDate().substring(0, 16);  
            if (entity.getDate().substring(0, 16).equals(lastCatalog)) {  
            	viewHolder.tvSendTime.setVisibility(View.GONE);  
            } else {  
            	viewHolder.tvSendTime.setVisibility(View.VISIBLE);  
            	viewHolder.tvSendTime.setText(""+entity.getDate().substring(0, 16));
            }  
        }  

		return convertView;
	}

	class ViewHolder {
		public TextView tvSendTime; //发送时间
		public TextView tvContent; //发送者内容
		public TextView name; //发送者昵称
		public ImageView iv_sendPicture; //发送的图片路径
		public CircleImageView avatar; //用户图像
		public boolean isComMsg = true; //[接受者 或者 发送者]
	}
	
	private void setImageOnClickListener(final String path, final String url,
			ImageView imageView) {
		imageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, ImageBrowerActivity.class);
				intent.putExtra("path", path);
				intent.putExtra("url", url);
				context.startActivity(intent);
				activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			}
		});
	}

	public static void displayImageByUri(ImageView imageView, String localPath,
			String url) {
		File file = new File(localPath);
		ImageLoader imageLoader = UserService.imageLoader;
		if (file.exists()) {
			imageLoader.displayImage("file://" + localPath, imageView,
					PhotoUtils.normalImageOptions);
		} else {
			imageLoader.displayImage(url, imageView,
					PhotoUtils.normalImageOptions);
		}
	}
	
	public static void displayUserImageByUri(ImageView imageView, String localPath,
			String url) {
		File file = new File(localPath);
		ImageLoader imageLoader = UserService.imageLoader;
		if (file.exists()) {
			imageLoader.displayImage("file://" + localPath, imageView,
					PhotoUtils.guanJiaImageOptions);
		} else {
			imageLoader.displayImage(url, imageView,
					PhotoUtils.guanJiaImageOptions);
		}
	}

	public View createViewByType(Msg.Type type, boolean isComeMsg) {
		View baseView = null;
		if (isComeMsg && Msg.Type.Text == type) {
			baseView = mInflater.inflate(R.layout.pm9_chatting_item_msg_text_left,
					null);
		} else if (!isComeMsg && Msg.Type.Text == type) {
			baseView = mInflater.inflate(R.layout.pm9_chatting_item_msg_text_right,
					null);
		} else if (isComeMsg && Msg.Type.Image == type) {
			baseView = mInflater.inflate(R.layout.pm9_chatting_item_msg_img_left,null);
//			baseView = mInflater.inflate(R.layout.pm9_row_received_picture,null);
		} else if (!isComeMsg && Msg.Type.Image == type) {
			baseView = mInflater.inflate(R.layout.pm9_chatting_item_msg_img_right,null);
//			baseView = mInflater.inflate(R.layout.pm9_row_sent_picture,null);
		}
		return baseView;
	}


	/**
	 * 刷新页面
	 */
	public void refresh() {
		notifyDataSetChanged();
	}

	public void addItem(ChatMsgEntity item) {
		this.coll.add(item);
		refresh();
	}

	public void setDatas(List<ChatMsgEntity> datas) {
		this.coll.addAll(datas);
		notifyDataSetChanged();
	}

	public void addAll(List<ChatMsgEntity> subDatas) {
		// this.coll.addAll(subDatas);
		notifyDataSetChanged();
	}

}

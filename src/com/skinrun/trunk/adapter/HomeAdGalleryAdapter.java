package com.skinrun.trunk.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.app.base.entity.ADInfo;
import com.avoscloud.chat.service.UserService;
import com.avoscloud.chat.util.PhotoUtils;
import com.base.service.impl.HttpClientUtils;

/** 
 * Gallery
 * */  
public class HomeAdGalleryAdapter extends BaseAdapter{  

    private Context context_;  
    private ArrayList<ADInfo> data ;
    public HomeAdGalleryAdapter(Context context,ArrayList<ADInfo> infoIds){  
        context_ = context;  
        this.data = infoIds;
    }  
    @Override  
    public int getCount() {  
    	if(data==null){
    		return 0 ;
    	}
        return data.size();  
    }  

    @Override  
    public Object getItem(int position) {  
        return data.get(position);  
    }  

    @Override  
    public long getItemId(int position) {  
        return position;  
    }  

    @Override  
    public View getView(int position, View convertView, ViewGroup parent) {  
        ImageView img = new ImageView(context_);  
        img.setLayoutParams(new Gallery.LayoutParams(Gallery.LayoutParams.FILL_PARENT,   
                Gallery.LayoutParams.FILL_PARENT));  
//        img.setBackgroundResource(R.drawable.ad_img_01);
        ADInfo info = data.get(position);
       // UrlImageViewHelper.setUrlDrawable(img, HttpClientUtils.IMAGE_URL+info.getAdImage(), R.drawable.home_default);
        UserService.imageLoader.displayImage(""+HttpClientUtils.IMAGE_URL+info.getAdImage(), img,
				PhotoUtils.articleImageOptions);
        img.setScaleType(ScaleType.FIT_XY);  
        return img;  
    }  
      
}  
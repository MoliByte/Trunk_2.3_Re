package com.skinrun.trunk.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.app.base.entity.ADInfo;
import com.beabox.hjy.tt.R;

/** 
 * Gallery
 * */  
public class HomeGridGuideAdapter extends BaseAdapter{  

	private int currentIndex_ = 0;
	
    private Context context_;  
    private ArrayList<ADInfo> data ;
    private Integer[] thumbIds = {  
            R.drawable.ball_selected,R.drawable.ball_normal  
    }; 
    public HomeGridGuideAdapter(Context context, ArrayList<ADInfo> data){  
        context_ = context;  
        this.data = data ;
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
        int index = 0 ;  
        if(position == currentIndex_){  
            index = 0;  
        }else{  
            index = 1;  
        }  
        return thumbIds[index];  
    }   

    @Override  
    public long getItemId(int position) {  
        return position;  
    }  

    @Override  
    public View getView(int position, View convertView, ViewGroup parent) {  
    	ImageView img = new ImageView(context_);  
        img.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.WRAP_CONTENT,   
                GridView.LayoutParams.WRAP_CONTENT));  
        if(position == currentIndex_){  
            img.setImageResource(thumbIds[0]);  
        }else{  
            img.setImageResource(thumbIds[1]);  
        }  
        img.setScaleType(ScaleType.FIT_CENTER);  
        return img;  
    }  
    
    public void setCurrentIndex(int currentIndex_){
    	this.currentIndex_ = currentIndex_ ;
    	
    }
      
}  
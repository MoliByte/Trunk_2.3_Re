package com.avoscloud.chat.base;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.beabox.hjy.tt.R;

/** 
 * Gallery
 * */  
public class ExpressionGridGuideAdapter extends BaseAdapter{  

	private int currentIndex_ = 0;
	
    private Context context_;  
    private int data ;
    private Integer[] thumbIds = {  
            R.drawable.d2,R.drawable.d1 
    }; 
    public ExpressionGridGuideAdapter(Context context, int data){  
        context_ = context;  
        this.data = data ;
    }  
    @Override  
    public int getCount() {  
    	return data ; 
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
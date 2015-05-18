/**
 * ImageBrowseActivity.java
 * ImageChooser
 * 
 * Created by likebamboo on 2014-4-22
 * Copyright (c) 1998-2014 http://likebamboo.github.io/ All rights reserved.
 */

package com.imagechooser.ui;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.avoscloud.chat.base.ChatMsgAdapter;
import com.beabox.hjy.tt.R;
import com.imagechooser.ui.adapter.ImagePagerAdapter;

/**
 * 大图浏览Activity
 * 
 * @author likebamboo
 */
public class ImageBrowseActivity extends Activity {
    /**
     * 图片列表
     */
    public static final String EXTRA_IMAGES = "extra_images";

    /**
     * 位置
     */
    public static final String EXTRA_INDEX = "extra_index";

    /**
     * 图片列表数据源
     */
    private ArrayList<String> mDatas = new ArrayList<String>();

    /**
     * 进入到该界面时的索引
     */
    private int mPageIndex = 0;

    /**
     * 图片适配器
     */
    private ImagePagerAdapter mImageAdapter = null;

    /**
     * viewpager
     */
    private ViewPager mViewPager = null;
    
    String url;
	String path;
	ImageView imageView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_browse);
        Intent intent = getIntent();
        imageView = (ImageView) findViewById(R.id.imageView);
        if (intent.hasExtra(EXTRA_IMAGES)) {
          mDatas = intent.getStringArrayListExtra(EXTRA_IMAGES);
          mPageIndex = intent.getIntExtra(EXTRA_INDEX, 0);
          String path = mDatas.get(mPageIndex);
          ChatMsgAdapter.displayImageByUri(imageView, path, "");
//          imageView.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Intent i = new Intent();  
//                i.setData(Uri.fromFile(new File(mDatas.get(mPageIndex))));  
//                setResult(Activity.RESULT_OK, i);  
//                finish(); 
//			}
//          });
        }
        
        findViewById(R.id.send_img).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i = new Intent();  
                i.setData(Uri.fromFile(new File(mDatas.get(mPageIndex))));  
                setResult(Activity.RESULT_OK, i);  
                finish(); 
			}
		});
        
        findViewById(R.id.send_cancel).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
                finish(); 
			}
		});
        
        findViewById(R.id.backHome).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
                finish(); 
			}
		});
        
		//ChatMsgAdapter.displayImageByUri(imageView, path, url);
        
//        mViewPager = (ViewPager)findViewById(R.id.image_vp);
//        Intent intent = getIntent();
//        if (intent.hasExtra(EXTRA_IMAGES)) {
//            mDatas = intent.getStringArrayListExtra(EXTRA_IMAGES);
//            mPageIndex = intent.getIntExtra(EXTRA_INDEX, 0);
//            mImageAdapter = new ImagePagerAdapter(mDatas);
//            mViewPager.setAdapter(mImageAdapter);
//            mViewPager.setCurrentItem(mPageIndex);
//        }
    }
    


}

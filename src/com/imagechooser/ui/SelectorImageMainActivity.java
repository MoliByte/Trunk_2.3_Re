/**
 * MainActivity.java
 * ImageChooser
 * 
 * Created by likebamboo on 2014-4-22
 * Copyright (c) 1998-2014 http://likebamboo.github.io/ All rights reserved.
 */

package com.imagechooser.ui;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.beabox.hjy.tt.R;
import com.imagechooser.listener.OnTaskResultListener;
import com.imagechooser.model.ImageGroup;
import com.imagechooser.task.ImageLoadTask;
import com.imagechooser.ui.adapter.ImageGroupAdapter;
import com.imagechooser.utils.SDcardUtil;
import com.imagechooser.utils.TaskUtil;
import com.imagechooser.widget.LoadingLayout;

/**
 * 图片选择主界面，列出所有图片文件夹
 * 
 * @author likebamboo
 */
public class SelectorImageMainActivity extends BaseActivity implements OnItemClickListener {
    /**
     * loading布局
     */
    private LoadingLayout mLoadingLayout = null;

    /**
     * 图片组GridView
     */
    private GridView mGroupImagesGv = null;

    /**
     * 适配器
     */
    private ImageGroupAdapter mGroupAdapter = null;

    /**
     * 图片扫描一般任务
     */
    private ImageLoadTask mLoadTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_choose_main);
        initView();
        loadImages();
    }

    /**
     * 初始化界面元素
     */
    private void initView() {
        mLoadingLayout = (LoadingLayout)findViewById(R.id.loading_layout);
        mGroupImagesGv = (GridView)findViewById(R.id.images_gv);
        findViewById(R.id.backHome).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
    }

    /**
     * 加载图片
     */
    private void loadImages() {
        mLoadingLayout.showLoading(true);
        if (!SDcardUtil.hasExternalStorage()) {
            mLoadingLayout.showEmpty(getString(R.string.donot_has_sdcard));
            return;
        }

        // 线程正在执行
        if (mLoadTask != null && mLoadTask.getStatus() == Status.RUNNING) {
            return;
        }

        mLoadTask = new ImageLoadTask(this, new OnTaskResultListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onResult(boolean success, String error, Object result) {
                mLoadingLayout.showLoading(false);
                // 如果加载成功
                if (success && result != null && result instanceof ArrayList) {
                    setImageAdapter((ArrayList<ImageGroup>)result);
                } else {
                    // 加载失败，显示错误提示
                    mLoadingLayout.showFailed(getString(R.string.loaded_fail));
                }
            }
        });
        TaskUtil.execute(mLoadTask);
    }

    /**
     * 构建GridView的适配器
     * 
     * @param data
     */
    private void setImageAdapter(ArrayList<ImageGroup> data) {
        if (data == null || data.size() == 0) {
            mLoadingLayout.showEmpty(getString(R.string.no_images));
        }
        mGroupAdapter = new ImageGroupAdapter(this, data, mGroupImagesGv);
        mGroupImagesGv.setAdapter(mGroupAdapter);
        mGroupImagesGv.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View view, int position, long id) {
        ImageGroup imageGroup = mGroupAdapter.getItem(position);
        if (imageGroup == null) {
            return;
        }
        ArrayList<String> childList = imageGroup.getImages();
        Intent mIntent = new Intent(SelectorImageMainActivity.this, ImageListActivity.class);
        mIntent.putExtra(ImageListActivity.EXTRA_TITLE, imageGroup.getDirName());
        mIntent.putStringArrayListExtra(ImageListActivity.EXTRA_IMAGES_DATAS, childList);
        startActivityForResult(mIntent, SECOND_REQUEST_CODE);
    }
    
    private final int SECOND_REQUEST_CODE = 2;  
    
    //等待选中图片
    @Override  
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {       
        super.onActivityResult(requestCode, resultCode, data);        
        if(requestCode == SECOND_REQUEST_CODE && data != null){  
            setResult(Activity.RESULT_OK, data);  
            overridePendingTransition(R.anim.activity_enter_from_right, R.anim.activity_exit_to_left);
            finish();  
        }  
    }   
}

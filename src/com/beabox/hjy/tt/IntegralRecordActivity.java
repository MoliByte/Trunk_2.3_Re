package com.beabox.hjy.tt;

import android.os.Bundle;
import android.view.View;

import com.app.base.init.BaseActivity;
import com.app.base.init.MyApplication;
import com.umeng.message.PushAgent;

/**
 * 积分规则页面
 * Created by beabox on 14-12-22.
 */
public class IntegralRecordActivity extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	MyApplication.getInstance().addActivity(this);
    	PushAgent.getInstance(this).onAppStart();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.integral_rule);
    }

    @Override
    public void setupView() {

    }

    @Override
    public void addListener() {

    }

    @Override
    public void sendMessageHandler(int messageCode) {

    }

    @Override
    public void dataCallBack(Object tag,int statusCode ,  Object result) {

    }

    @Override
    public void onClick(View view) {

    }
    
    @Override
    protected String getActivityName() {
        return "积分规则页面";
    }
}

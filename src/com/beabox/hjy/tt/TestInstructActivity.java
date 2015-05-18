package com.beabox.hjy.tt;

import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.base.init.MyApplication;
import com.umeng.message.PushAgent;


public class TestInstructActivity extends Activity implements OnClickListener {
	private ImageView imageViewContent;
	private TextView tvTitile;
	
	private Bitmap mBitmap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		MyApplication.getInstance().addActivity(this);
    	PushAgent.getInstance(this).onAppStart();
		Intent intent=getIntent();
		int tag=intent.getIntExtra("CONTENT_TYPE", -1);
		super.onCreate(savedInstanceState);
		try{
			setContentView(R.layout.test_instruct_layout);
			imageViewContent=(ImageView)findViewById(R.id.imageViewContent);
			tvTitile=(TextView)findViewById(R.id.tvTitile);
			findViewById(R.id.TestInstructBackBtn).setOnClickListener(this);
			
			
			switch(tag){
			case 1:
				BitmapFactory.Options opts = new BitmapFactory.Options();
				opts.inSampleSize =2;
				InputStream is =getResources().openRawResource(R.drawable.instruct);
				mBitmap=BitmapFactory.decodeStream(is);
				tvTitile.setText("说明页");
				imageViewContent.setImageBitmap(mBitmap);
				break;
			case 2:
				tvTitile.setText("");
				InputStream inputStream =getResources().openRawResource(R.drawable.ceshizhinan);
				mBitmap=BitmapFactory.decodeStream(inputStream);
				imageViewContent.setImageBitmap(mBitmap);
				tvTitile.setText("测试指南");
				break;
			}
			
			
		}catch(Exception e){
			
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		 if(!mBitmap.isRecycled()){
			 mBitmap.recycle() ;  //回收图片所占的内存
			 System.gc();	
		 }
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.TestInstructBackBtn:
			finish();
			break;
		}
		
	}
	 @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.activity_enter_from_left, R.anim.activity_exit_to_right);
    }
}



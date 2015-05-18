package com.beabox.hjy.tt;

import com.app.base.init.MyApplication;
import com.umeng.message.PushAgent;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ExterNaLUrlActivity extends Activity implements OnClickListener{
	private TextView title;
	private WebView webView;
	private ProgressBar loading;
	private String url;
	private String titleName;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(this);
    	PushAgent.getInstance(this).onAppStart();
    	
    	Bundle bundle=getIntent().getExtras();
    	url=bundle.getString("URL");
    	titleName=bundle.getString("titleName");
    	
    	setContentView(R.layout.extenal_url_activity);
    	initView();
	}
	
	private void initView(){
		title=(TextView)findViewById(R.id.title);
		title.setText(""+titleName);
		webView=(WebView)findViewById(R.id.webView);
		loading=(ProgressBar)findViewById(R.id.loading);
		findViewById(R.id.backBtn).setOnClickListener(this);
		
		 WebSettings settings = webView.getSettings();
	        // 这样网页就可加载JavaScript了
	        settings.setJavaScriptEnabled(true);
	        settings.setJavaScriptCanOpenWindowsAutomatically(true);
	        settings.setBuiltInZoomControls(true);// 显示放大缩小按钮
	        settings.setSupportZoom(true);// 允许放大缩小
	        webView.setWebViewClient(new WebViewClient() {
	            @Override
	            public void onPageStarted(WebView view, String url, Bitmap favicon) {
	                super.onPageStarted(view, url, favicon);
	                loading.setVisibility(View.VISIBLE);
	            }

	            @Override
	            public void onPageFinished(WebView view, String url) {
	                super.onPageFinished(view, url);
	                loading.setVisibility(View.GONE);
	            }

	            @Override
	            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
	                super.onReceivedError(view, errorCode, description, failingUrl);
	                loading.setVisibility(View.GONE);
	            }
	        });
	        webView.requestFocusFromTouch();
	        webView.loadUrl(url);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.backBtn:
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

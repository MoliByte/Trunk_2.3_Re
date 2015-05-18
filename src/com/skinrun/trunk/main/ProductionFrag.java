package com.skinrun.trunk.main;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebSettings.ZoomDensity;
import android.widget.ProgressBar;

import com.baidu.mobstat.StatService;
import com.beabox.hjy.tt.R;
import com.umeng.analytics.MobclickAgent;

/**
 * 产品页面
 * 
 * @author zhup
 * 
 */
public class ProductionFrag extends Fragment implements OnClickListener {

	WebView webView;
	ProgressBar loading;

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.product_fragment, container,
				false);
		webView = (WebView) view.findViewById(R.id.webView);
		loading = (ProgressBar) view.findViewById(R.id.loading);

		// webView.setVerticalScrollBarEnabled(false);
		// webView.setHorizontalScrollBarEnabled(false);

		WebSettings settings = webView.getSettings();
		// 这样网页就可加载JavaScript了
		settings.setJavaScriptEnabled(true);
		settings.setJavaScriptCanOpenWindowsAutomatically(true);
		settings.setBuiltInZoomControls(false);// 显示放大缩小按钮
		settings.setSupportZoom(false);// 允许放大缩小

		// settings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
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
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				super.onReceivedError(view, errorCode, description, failingUrl);
				loading.setVisibility(View.GONE);
			}
			
			// 页面内部跳转
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url); // 在当前的webview中跳转到新的url
				return true;
			}
			
			
		});

		// settings.setUseWideViewPort(true);
		// settings.setLoadWithOverviewMode(true);

		DisplayMetrics metrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay()
				.getMetrics(metrics);
		int mDensity = metrics.densityDpi;
		Log.d("maomao", "densityDpi = " + mDensity);
		if (mDensity == 240) {
			settings.setDefaultZoom(ZoomDensity.FAR);
		} else if (mDensity == 160) {
			settings.setDefaultZoom(ZoomDensity.MEDIUM);
		} else if (mDensity == 120) {
			settings.setDefaultZoom(ZoomDensity.CLOSE);
		} else if (mDensity == DisplayMetrics.DENSITY_XHIGH) {
			settings.setDefaultZoom(ZoomDensity.FAR);
		} else if (mDensity == DisplayMetrics.DENSITY_TV) {
			settings.setDefaultZoom(ZoomDensity.FAR);
		} else {
			settings.setDefaultZoom(ZoomDensity.MEDIUM);
		}

		// http://skinrun.renzhi.net/merchant/index/login.html
		// http://merchant.skinrun.me

		/**
		 * 用WebView显示图片，可使用这个参数 设置网页布局类型： 1、LayoutAlgorithm.NARROW_COLUMNS ：
		 * 适应内容大小 2、LayoutAlgorithm.SINGLE_COLUMN:适应屏幕，内容将自动缩放
		 */
		settings.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
		settings.setUseWideViewPort(true);

		webView.requestFocusFromTouch();
		webView.loadUrl(getResources().getString(R.string.merchant_page_url));
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onClick(View v) {

	}
	
	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(getActivity());
		StatService.onResume(getActivity());
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		MobclickAgent.onPause(getActivity());
		StatService.onPause(getActivity());
	}

}

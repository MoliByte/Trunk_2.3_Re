package com.skinrun.trunk.main;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.base.service.impl.HttpAysnResultInterface;
import com.beabox.hjy.tt.R;

/**
 * 导师简介页面
 * 
 * @author zhup
 * 
 */
public class MasterIntroduceFrag extends Fragment implements OnClickListener,
		HttpAysnResultInterface {
	private String mContent = "";

	public static MasterIntroduceFrag newInstance(String content) {
		MasterIntroduceFrag fragment = new MasterIntroduceFrag();

		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < 20; i++) {
			builder.append(content).append(" ");
		}
		builder.deleteCharAt(builder.length() - 1);
		fragment.mContent = builder.toString();

		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.master_intro_fragment, container,
				false);
		

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
	public void dataCallBack(Object tag,  int statusCode,Object result) {
		try {

		} catch (Exception e) {

		} finally {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {

				}
			}, 1000);
		}

	}

}

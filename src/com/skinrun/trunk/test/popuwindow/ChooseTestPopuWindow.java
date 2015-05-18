package com.skinrun.trunk.test.popuwindow;

import android.app.Activity;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.PopupWindow;

import com.base.app.utils.DensityUtil;
import com.beabox.hjy.tt.R;

public class ChooseTestPopuWindow implements OnClickListener {
	public interface OnChooseClickListener {
		public void onChooseSkinTest();

		public void onChooseFacialTest();

		public void onChooseNone();
	}

	private OnChooseClickListener onChooseClickListener;
	private PopupWindow popupWindow;
	private Activity context;

	public void setOnChooseClickListener(
			OnChooseClickListener onChooseClickListener) {
		this.onChooseClickListener = onChooseClickListener;
	}

	public ChooseTestPopuWindow() {
		super();
	}

	public void show(Activity context) {
		this.context = context;
		if (popupWindow == null) {
			Rect frame = new Rect();
			context.getWindow().getDecorView()
					.getWindowVisibleDisplayFrame(frame);

			int width = frame.width();
			int height = frame.height();

			LayoutInflater layoutInflater = LayoutInflater.from(this.context);
			View view = layoutInflater.inflate(R.layout.selecttesttype, null);
			int px=DensityUtil.dip2px(context, 180);
			initView(view);

			popupWindow = new PopupWindow(view, width, px);
			popupWindow.setFocusable(true);
			popupWindow.setAnimationStyle(R.style.AnimBottom);
		}

		View rootView = context.getWindow().getDecorView()
				.findViewById(android.R.id.content);
		// popupWindow.setAnimationStyle(R.style.DialogAnimation);
		popupWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
	}

	private void initView(View view) {
		view.findViewById(R.id.ivChooseSkin).setOnClickListener(this);
		view.findViewById(R.id.ivChooseFacial).setOnClickListener(this);
		view.findViewById(R.id.ivChooseNone).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ivChooseSkin:
			if (onChooseClickListener != null) {
				onChooseClickListener.onChooseSkinTest();
			}
			break;
		case R.id.ivChooseFacial:
			if (onChooseClickListener != null) {
				onChooseClickListener.onChooseFacialTest();
			}
			break;
		case R.id.ivChooseNone:
			if (onChooseClickListener != null) {
				onChooseClickListener.onChooseNone();
			}
			break;
		}
	}

	public void dismiss() {
		if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.dismiss();
		}
	}

	public void destory() {
		context = null;
		popupWindow = null;
		onChooseClickListener = null;
	}
}

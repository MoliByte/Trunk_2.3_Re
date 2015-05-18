package com.skinrun.trunk.facial.mask.test;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

public class ListViewHeightSetter {
	/*public static void setListViewHeightBasedOnChildren(ListView listView) {
		ProductAdapter adapter = (ProductAdapter) listView.getAdapter();
		if (adapter == null) {
			return;
		}

		int totalHeight = 0;
		for (int i = 0; i < adapter.getCount(); i++) {
			View listItem = adapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}

		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (adapter.getCount() - 1));
		listView.setLayoutParams(params);
	}*/

	public static void setListViewHeight( ListView listView) {
		BaseAdapter adapter=(BaseAdapter) listView.getAdapter();
		if (adapter == null) {
			return;
		}
		int totalHeight = 0;
		for (int i = 0; i < adapter.getCount(); i++) {
			View listItem = adapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}
		 ViewGroup.LayoutParams params = listView.getLayoutParams();  
	     params.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));  
	     listView.setLayoutParams(params);  
	}
}

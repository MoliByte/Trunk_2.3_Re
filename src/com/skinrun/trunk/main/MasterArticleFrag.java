package com.skinrun.trunk.main;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;

import com.app.base.entity.MasterArticleEntity;
import com.app.service.MasterArticleAsynTaskService;
import com.base.pulltorefresh.view.PullToRefreshBase;
import com.base.pulltorefresh.view.PullToRefreshBase.Mode;
import com.base.pulltorefresh.view.PullToRefreshBase.OnRefreshListener2;
import com.base.pulltorefresh.view.PullToRefreshListView;
import com.base.service.action.constant.HttpTagConstantUtils;
import com.base.service.impl.HttpAysnResultInterface;
import com.beabox.hjy.tt.R;
import com.skinrun.trunk.adapter.MasterArticleListAdapter;

/**
 * 导师简介页面
 * 
 * @author zhup
 * 
 */
public class MasterArticleFrag extends Fragment implements OnClickListener,
		HttpAysnResultInterface {

	private String mContent = "";

	private ArrayList<MasterArticleEntity> masterEntityList = new ArrayList<MasterArticleEntity>();
	private MasterArticleListAdapter adapter;
	private PullToRefreshListView pullToRefreshListView;
	private ListView realListView;
	private SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");

	public static MasterArticleFrag newInstance(String content) {
		MasterArticleFrag fragment = new MasterArticleFrag();

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
		View view = inflater.inflate(R.layout.master_article_fragment,
				container, false);

		pullToRefreshListView = (PullToRefreshListView) view
				.findViewById(R.id.master_article);
		realListView = pullToRefreshListView.getRefreshableView();
		pullToRefreshListView.setMode(Mode.DISABLED);
		pullToRefreshListView
				.setOnRefreshListener(new OnRefreshListener2<ListView>() {
					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						String label = format.format(new Date());
						refreshView.getLoadingLayoutProxy()
								.setLastUpdatedLabel("最后更新时间:" + label);
						getData();
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {

					}

				});
		Runnable task = new Runnable() {
			
			@Override
			public void run() {
				getData();
			}
		};
		
		new Handler().post(task);

		return view;
	}

	protected void getData() {
		new MasterArticleAsynTaskService(getActivity(),
				HttpTagConstantUtils.MASTER_ARTILE, this).doMasterArticle();
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
			if(((Integer)tag) == HttpTagConstantUtils.MASTER_ARTILE){
				masterEntityList = (ArrayList<MasterArticleEntity>) result ;
				adapter = new MasterArticleListAdapter(masterEntityList, getActivity());
				pullToRefreshListView.setAdapter(adapter);
				adapter.notifyDataSetChanged();
			}
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

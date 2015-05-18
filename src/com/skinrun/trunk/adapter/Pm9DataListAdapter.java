package com.skinrun.trunk.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.app.base.entity.Pm9HistoryDataEntity;
import com.avoscloud.chat.service.UserService;
import com.avoscloud.chat.util.PhotoUtils;
import com.base.app.utils.StringUtil;
import com.base.service.impl.HttpClientUtils;
import com.beabox.hjy.tt.R;

public class Pm9DataListAdapter extends BaseAdapter {
	SimpleDateFormat formatAll = new SimpleDateFormat("yyyy.MM.dd");
	private ArrayList<Pm9HistoryDataEntity> arrayList;
	private Context context;
	private LayoutInflater layoutInflater;
	 RequestQueue mQueue ; 
	public Pm9DataListAdapter(ArrayList<Pm9HistoryDataEntity> list, Context c) {
		arrayList = list;
		context = c;
		layoutInflater = LayoutInflater.from(context) ;
		mQueue = Volley.newRequestQueue(context);
	}

	@Override
	public int getCount() {
		if(arrayList==null&&arrayList.size()==0){
			return 0 ;
		}
		return arrayList.size();
	}

	@Override
	public Object getItem(int position) {
		return arrayList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public static String type = "" ;
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final ViewHolder holder;
	    if (convertView == null) {
	      convertView = layoutInflater.inflate(R.layout.pm9_history_listview_item, null);
	      holder = new ViewHolder(convertView);
	      convertView.setTag(holder);
	    } else {
	    	 holder = (ViewHolder) convertView.getTag();
	    }
	    
	    Pm9HistoryDataEntity entity = arrayList.get(position) ;
	    
	    UserService.imageLoader.displayImage(/*HttpClientUtils.IMAGE_URL
				+ */ entity.getNine_clock_logo(), holder.nine_clock_logo,PhotoUtils.articleImageOptions);
	    
		holder.participant.setText(""+entity.getParticipant()+"人参与");
		holder.pm9_time.setText(""+entity.getPm9_time());
		holder.prize.setText(StringUtil.ToDBC("本期奖品："+entity.getPrize()));
		holder.awarded_list.setText(StringUtil.ToDBC("获奖名单："+entity.getAwarded_list()));
	    return convertView;
	}
	
	class ViewHolder {
	    ImageView nine_clock_logo;
	    TextView pm9_time;
	    TextView participant;
	    
	    TextView prize;
	    TextView awarded_list;
	 
	    public ViewHolder(View convertView) {
	    	nine_clock_logo = (ImageView) convertView.findViewById(R.id.nine_clock_logo);
	    	pm9_time = (TextView) convertView.findViewById(R.id.pm9_time);
	    	participant = (TextView) convertView.findViewById(R.id.participant);
	    	prize = (TextView) convertView.findViewById(R.id.prize);
	    	awarded_list = (TextView) convertView.findViewById(R.id.awarded_list);
	    }
	  }

}

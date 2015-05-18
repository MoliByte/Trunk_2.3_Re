package com.skinrun.trunk.main;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.base.entity.UserEntity;
import com.app.base.init.ACache;
import com.app.base.init.MyApplication;
import com.avoscloud.chat.service.UserService;
import com.avoscloud.chat.util.PhotoUtils;
import com.baidu.mobstat.StatService;
import com.base.app.utils.DBService;
import com.base.app.utils.DateUtil;
import com.base.dialog.lib.NiftyDialogBuilder;
import com.base.service.action.constant.HttpTagConstantUtils;
import com.base.service.impl.HttpAysnResultInterface;
import com.beabox.hjy.tt.MyIntegralActivity;
import com.beabox.hjy.tt.MyMasterActivity;
import com.beabox.hjy.tt.MyMessageActivity;
import com.beabox.hjy.tt.R;
import com.beabox.hjy.tt.SweepActivity;
import com.beabox.hjy.tt.SysSettingActivity;
import com.beabox.hjy.tt.UpdateBaseInfoActivity;
import com.idongler.widgets.CircleImageView;
import com.umeng.analytics.MobclickAgent;

/**
 * 我的个人信息页面
 * 
 * @author zhup
 * 
 */
public class UserInfoFrag extends Fragment implements OnClickListener,
		HttpAysnResultInterface {
	private final static String TAG = "MineFrag" ;
	private View my_master; 		// 我的导师
	private View sys_settings; 		// 系统设置
	private View sweep_scan; 		// 扫一扫
	private View my_info; 			// 个人信息
	private View my_integral;		// 我的积分
	UserEntity entity;

	private TextView nick_name; 	// 昵称
	private TextView regionNames; 	// 区域名称
	private TextView skinType; 		// 皮肤类型
	private TextView realname; 		// 正式名称
	private TextView level; 		// 等级
	private TextView age; 			// 年龄

	private View my_message_layout;
	private CircleImageView avatar_img;
	private RelativeLayout mine_container;
	ACache aCache;
	NiftyDialogBuilder dialogUploadImage;
	
	SimpleDateFormat format = new SimpleDateFormat("yyyy");

	private View forget_pwd;// 忘记密码
	
	protected String mCurrentProviceName = "" ;
	protected String mCurrentCityName = "" ;
	protected String mCurrentDistrictName = "";
	protected Map<String, String> mProvinceMap = new HashMap<String, String>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		entity = DBService.getUserEntity() ;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = LayoutInflater.from(getActivity()).inflate(
				R.layout.mine_main_fragment, null);
		my_master = view.findViewById(R.id.my_master);
		avatar_img = (CircleImageView) view.findViewById(R.id.avatar_img);
		avatar_img.setBackgroundDrawable(null);
		TextView title_name = (TextView) view.findViewById(R.id.title_name);
		nick_name = (TextView) view.findViewById(R.id.nick_name);
		skinType = (TextView) view.findViewById(R.id.skin_type);
		age = (TextView) view.findViewById(R.id.age);
		regionNames = (TextView) view.findViewById(R.id.regionNames);
		my_message_layout = view.findViewById(R.id.my_message_layout);
		// realname = (TextView) view.findViewById(R.id.realname);
		level = (TextView) view.findViewById(R.id.level);
		avatar_img.setBackgroundDrawable(null);
		
		if (null != entity) {
			String avatar_str = entity.getAvatar();
			String avatar = avatar_str == null ? "" : avatar_str.replaceAll("\\\\",
					"");
			Log.e("MineFrag-------->", avatar);
			
			UserService.imageLoader.displayImage(ACache.get(getActivity()).getAsString("avatar") + "", avatar_img,
					PhotoUtils.myPicImageOptions);
			
			nick_name.setText("" + entity.getNiakname());
			skinType.setText("" + HttpTagConstantUtils.getSkinTypeByKey(entity.getSkinType()));
			//regionNames.setText("" + entity.getRegionNames());
			String birthday = entity.getBirthday()==null||"".equals(entity.getBirthday())?"1990-01-01": entity.getBirthday();
			age.setText(DateUtil.getAge(birthday) + "岁");
			
			String region = entity.getRegion() == null
					|| "".equals(entity.getRegion()) ? "110101"
					:entity.getRegion().length() < 6 ? "110101"
							: entity.getRegion();
			
			for (String key : MyApplication.mProvinceMap.keySet()) {
				String code = MyApplication.mProvinceMap.get(key);
				if (code.equals(region.substring(0, 2) + "0000")) {
					mCurrentProviceName = key;
				}
				if (code.equals(region.substring(0, 4) + "00")) {
					mCurrentCityName = key;
				}
				if (code.equals(region)) {
					mCurrentDistrictName = key;
				}

			}

			Log.e(TAG, "mCurrentProviceName-->"+mCurrentProviceName);
			Log.e(TAG, "mCurrentCityName-->"+mCurrentCityName);
			this.regionNames.setText("" + mCurrentProviceName+ mCurrentCityName);
		}// 否则赋予默认值
		String home_title_str = getResources().getString(
				R.string.mine_title_name);
		title_name.setText("" + home_title_str);
		my_master.setOnClickListener(this);
		my_integral = view.findViewById(R.id.my_integral);
		my_integral.setOnClickListener(this);
		sys_settings = view.findViewById(R.id.sys_settings);
		sys_settings.setOnClickListener(this);

		sweep_scan = view.findViewById(R.id.sweep_scan);
		sweep_scan.setOnClickListener(this);

		my_info = view.findViewById(R.id.my_info);
		my_info.setOnClickListener(this);
		my_message_layout.setOnClickListener(this);
		return view;
	}



	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		aCache = ACache.get(activity);
	}

	@Override
	public void onResume() {
		super.onResume();
		entity = DBService.getUserEntity() ;
		MobclickAgent.onResume(getActivity());
		StatService.onResume(getActivity());
	}
	
	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(getActivity());
		StatService.onPause(getActivity());
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.my_master:// 我的导师
			intent = new Intent(getActivity(), MyMasterActivity.class);
			startActivity(intent);
			getActivity().overridePendingTransition(
					R.anim.activity_enter_from_right,
					R.anim.activity_exit_to_left);
			break;

		case R.id.my_integral: // 我的积分
			intent = new Intent(getActivity(), MyIntegralActivity.class);
			startActivity(intent);
			getActivity().overridePendingTransition(
					R.anim.activity_enter_from_right,
					R.anim.activity_exit_to_left);
			break;
		case R.id.sys_settings:// 系统设置
			intent = new Intent(getActivity(), SysSettingActivity.class);
			startActivity(intent);
			getActivity().overridePendingTransition(
					R.anim.activity_enter_from_right,
					R.anim.activity_exit_to_left);
			break;
		case R.id.sweep_scan:// 扫一扫
			new Handler().post(new Runnable() {

				@Override
				public void run() {
					Intent intent = new Intent(getActivity(),
							SweepActivity.class);
					startActivity(intent);
					getActivity().overridePendingTransition(
							R.anim.activity_enter_from_right,
							R.anim.activity_exit_to_left);
				}
			});
			break;

		case R.id.my_info: //个人信息
			intent = new Intent(getActivity(), UpdateBaseInfoActivity.class);
			intent.putExtra("userinfo", entity);
			startActivity(intent);
			getActivity().overridePendingTransition(
					R.anim.activity_enter_from_right,
					R.anim.activity_exit_to_left);
			break;
		case R.id.my_message_layout: // 我的消息列表
			intent = new Intent(getActivity(), MyMessageActivity.class);
			startActivity(intent);
			getActivity().overridePendingTransition(
					R.anim.activity_enter_from_right,
					R.anim.activity_exit_to_left);
			break;
		default:
			break;
		}
	}

	@Override
	public void dataCallBack(Object tag, int statusCode, Object result) {
		try {
			if (null != dialogUploadImage) {
				dialogUploadImage.dismiss();
			}
		} catch (Exception e) {
			// AppToast.toastMsgCenter(getActivity(), "登陆失败!", 2000).show();
		} finally {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {

				}
			}, 1000);
		}

	}

}

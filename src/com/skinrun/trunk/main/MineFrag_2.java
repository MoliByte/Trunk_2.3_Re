package com.skinrun.trunk.main;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpStatus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.base.entity.UserEntity;
import com.app.base.init.ACache;
import com.app.service.LoginAsynTaskService;
import com.base.app.utils.DBService;
import com.base.app.utils.StringUtil;
import com.base.dialog.lib.Effectstype;
import com.base.dialog.lib.NiftyDialogBuilder;
import com.base.service.action.constant.HttpTagConstantUtils;
import com.base.service.impl.HttpAysnResultInterface;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.MyIntegralActivity;
import com.beabox.hjy.tt.MyMasterActivity;
import com.beabox.hjy.tt.MyMessageActivity;
import com.beabox.hjy.tt.R;
import com.beabox.hjy.tt.RegisterActivity_1;
import com.beabox.hjy.tt.ResetPassword;
import com.beabox.hjy.tt.SweepActivity;
import com.beabox.hjy.tt.SysSettingActivity;
import com.beabox.hjy.tt.UpdateBaseInfoActivity;
import com.idongler.widgets.CircleImageView;

/**
 * 我的个人信息页面
 * 
 * @author zhup
 * 
 */
public class MineFrag_2 extends Fragment implements OnClickListener,
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

	// login -----view
	private EditText username;
	private EditText password;
	private ImageView backBtn;
	private ImageView login_btn;
	private View toRegisterBtn;
	NiftyDialogBuilder dialogUploadImage;
	
	SimpleDateFormat format = new SimpleDateFormat("yyyy");

	private View forget_pwd;// 忘记密码
	
	protected String mCurrentProviceName = "" ;
	protected String mCurrentCityName = "" ;
	protected String mCurrentDistrictName = "";
	protected Map<String, String> mProvinceMap = new HashMap<String, String>();
	
	private FragmentManager fmgr;
	private LoginFrag loginFrag ;
	private UserInfoFrag userinfoFrag ;
	private Fragment[] fragments; 

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		fmgr = getFragmentManager();
		
		entity = DBService.getUserEntity();
		loginFrag = new LoginFrag();
		userinfoFrag = new UserInfoFrag();
		fragments = new Fragment[] { loginFrag, userinfoFrag};
		
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.mine_page, container, false);
		mine_container = (RelativeLayout) view
				.findViewById(R.id.mine_container);
		fmgr.beginTransaction()
		.add(R.id.mine_container, loginFrag)
		.add(R.id.mine_container, userinfoFrag)
		.hide(userinfoFrag).hide(loginFrag).commit();
		
		if(isAdded()){
			FragmentTransaction trx = fmgr.beginTransaction() ;
			if (entity == null) {
				trx.hide(fragments[1]).show(fragments[0]).commit();
				/*fmgr.beginTransaction()
						.add(R.id.mine_container, userinfoFrag)
						.hide(userinfoFrag).show(loginFrag)
						.commit();*/
			} else {
				// 如果用户没有登录则显示登陆页面，如果Token在的话就显示用户的个人信息
				/*fmgr.beginTransaction()
					.add(R.id.mine_container, userinfoFrag)
					.hide(loginFrag).show(userinfoFrag)
					.commit();*/
				trx.hide(fragments[0]).show(fragments[1]).commit();
			}
		}
		return view;
	}

	public boolean isLogin() {
		if (entity != null && null != entity.getToken()
				&& !"".equals(entity.getToken())) {
			updateUserInfo(entity);
			return true;
		} else {
			return false;
		}
	}

	public void login() {
		final String loginName = username.getText().toString();
		final String password_ = password.getText().toString();

		if (StringUtil.isBlank(loginName)) {
			AppToast.toastMsgCenter(getActivity(),
					getString(R.string.login_loginNameBlankMessage),
					Toast.LENGTH_LONG).show();
			return;
		}

		if (StringUtil.isEmpty(password_)) {
			AppToast.toastMsgCenter(getActivity(),
					getString(R.string.login_passwordBlankMessage),
					Toast.LENGTH_LONG).show();
			return;
		}

		dialogUploadImage = NiftyDialogBuilder.getInstance(getActivity(),
				R.layout.dialog_login_layout);
		final View text = LayoutInflater.from(getActivity()).inflate(
				R.layout.dialog_login_view, null);
		TextView t = (TextView) text.findViewById(R.id.loading_text);
		t.setText("正在登陆....");
		dialogUploadImage.withTitle(null).withMessage(null)
				.withEffect(Effectstype.Fadein).withDuration(100)
				.isCancelableOnTouchOutside(false)
				.setCustomView(text, getActivity().getApplicationContext())
				.show();
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				new LoginAsynTaskService(getActivity(),
						HttpTagConstantUtils.LOGIN_TAG, MineFrag_2.this).doLogin(
						loginName, password_);

			}
		}, 500);
	}

	public void getAccount() {
		new LoginAsynTaskService(getActivity(), HttpTagConstantUtils.LOGIN_TAG,
				this).doLogin(username.getText().toString(), password.getText()
				.toString());
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
		if (null != mine_container) {
			mine_container.removeAllViews();
			//String token = aCache.getAsString("Token");
			entity = DBService.getUserEntity();
			FragmentTransaction trx = fmgr.beginTransaction() ;
			/*if (!fragments[0].isAdded()) {
				trx.add(R.id.mine_container, fragments[0]);
			}
			if (!fragments[1].isAdded()) {
				trx.add(R.id.mine_container, fragments[1]);
			}*/
			if(isAdded()){
				if (entity == null) {
					trx.hide(fragments[1]).show(fragments[0]).commit();
				} else {
					// 如果用户没有登录则显示登陆页面，如果Token在的话就显示用户的个人信息
					trx.hide(fragments[0]).show(fragments[1]).commit();
				}
			}
			/*if (!isLogin()) {
				//mine_container.addView(loginPage());
			} else {
				 new GetUserInfoService(getActivity(),
				 HttpTagConstantUtils.USER_INFO, this)
				 .getUserInfo(entity.getToken());

			}*/
		}
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

		// login --------
		case R.id.login_btn:
			login();
			break;
		case R.id.forget_pwd:
			intent = new Intent(getActivity(), ResetPassword.class);
			startActivity(intent);
			getActivity().overridePendingTransition(
					R.anim.activity_enter_from_right,
					R.anim.activity_exit_to_left);
			break;
		case R.id.toRegisterBtn:// 到注册页面
			intent = new Intent(getActivity(), RegisterActivity_1.class);
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
			if ((Integer) (tag) == HttpTagConstantUtils.LOGIN_TAG) {
				if (statusCode == HttpStatus.SC_OK
						|| statusCode == HttpStatus.SC_CREATED) {
					UserEntity entity = (UserEntity) result;
					entity.setMobile(username.getText().toString().trim());
					//DBService.saveOrUpdateUserEntity(entity);
					this.entity = entity;
					updateUserInfo(entity);
					AppToast.toastMsgCenter(getActivity(), "登陆成功!", 2000).show();
					FragmentTransaction trx = fmgr.beginTransaction();
					
					trx.hide(fragments[0]).show(fragments[1]).commit();
					/*new GetUserInfoService(getActivity(),
							HttpTagConstantUtils.USER_INFO, this)
							.getUserInfo(entity.getToken());*/
				} else if (HttpStatus.SC_UNAUTHORIZED == statusCode) {
					AppToast.toastMsgCenter(getActivity(), "登录失败，用户名或者密码错误!", 2000)
							.show();
				} else {
					AppToast.toastMsgCenter(getActivity(),
							"Error Code:" + statusCode, 2000).show();
				}

			}
		} catch (Exception e) {
			 AppToast.toastMsgCenter(getActivity(), ""+e.toString(), 2000).show();
		} finally {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {

				}
			}, 1000);
		}

	}

	public void updateUserInfo(UserEntity entity) {
		aCache.put("Token", entity.getToken());// 保存Token
		ACache.get(getActivity()).put("nickname", entity.getNiakname());
		ACache.get(getActivity()).put("mobile", entity.getMobile());
		ACache.get(getActivity()).put("skin_type", entity.getSkinType());
		ACache.get(getActivity()).put("region_name", entity.getRegionNames());
		ACache.get(getActivity()).put("region", entity.getRegion());
		ACache.get(getActivity()).put("realname", entity.getRealname());
		ACache.get(getActivity()).put("birthday", entity.getBirthday());
		ACache.get(getActivity()).put("gender", entity.getGender());
		ACache.get(getActivity()).put("avatar", entity.getAvatar());
	}

}

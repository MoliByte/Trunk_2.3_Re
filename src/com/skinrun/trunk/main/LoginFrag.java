package com.skinrun.trunk.main;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import com.baidu.mobstat.StatService;
import com.base.app.utils.StringUtil;
import com.base.dialog.lib.Effectstype;
import com.base.dialog.lib.NiftyDialogBuilder;
import com.base.service.action.constant.HttpTagConstantUtils;
import com.base.service.impl.HttpAysnResultInterface;
import com.base.supertoasts.util.AppToast;
import com.beabox.hjy.tt.R;
import com.beabox.hjy.tt.RegisterActivity_1;
import com.beabox.hjy.tt.ResetPassword;
import com.idongler.widgets.CircleImageView;
import com.umeng.analytics.MobclickAgent;

/**
 * 登录碎片
 * @author zhup
 * 
 */
public class LoginFrag extends Fragment implements OnClickListener,
		HttpAysnResultInterface {
	private final static String TAG = "LoginFrag" ;

	private View my_message_layout;
	private CircleImageView avatar_img;
	private RelativeLayout mine_container;
	ACache aCache;
	FragmentManager fmgr;

	// login -----view
	private EditText username;
	private EditText password;
	private ImageView backBtn;
	private ImageView login_btn;
	private View toRegisterBtn;
	NiftyDialogBuilder dialogUploadImage;
	private UserEntity entity ;
	
	SimpleDateFormat format = new SimpleDateFormat("yyyy");

	private View forget_pwd;// 忘记密码
	
	protected String mCurrentProviceName = "" ;
	protected String mCurrentCityName = "" ;
	protected String mCurrentDistrictName = "";
	protected Map<String, String> mProvinceMap = new HashMap<String, String>();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = LayoutInflater.from(getActivity()).inflate(
				R.layout.activity_login, null);
		backBtn = (ImageView) view.findViewById(R.id.backBtn);
		login_btn = (ImageView) view.findViewById(R.id.login_btn);
		username = (EditText) view.findViewById(R.id.username);
		password = (EditText) view.findViewById(R.id.password);
		toRegisterBtn = view.findViewById(R.id.toRegisterBtn);
		forget_pwd = view.findViewById(R.id.forget_pwd);
		String username = aCache.getAsString("mobile") == null ? "" : aCache
				.getAsString("mobile");
		this.username.setText(username + "");
		this.username.setSelection(username.length());
		backBtn.setVisibility(View.GONE);
		backBtn.setOnClickListener(this);
		login_btn.setOnClickListener(this);
		toRegisterBtn.setOnClickListener(this);
		forget_pwd.setOnClickListener(this);

		return view;
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
						HttpTagConstantUtils.LOGIN_TAG, LoginFrag.this).doLogin(
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
			//entity = DBService.getUserEntity();//DDBService.getEntityByToken(token);
		}
		
		MobclickAgent.onResume(getActivity());
		StatService.onResume(getActivity());
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
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
	
	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(getActivity());
		StatService.onPause(getActivity());
	}

}

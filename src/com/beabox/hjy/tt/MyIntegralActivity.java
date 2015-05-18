package com.beabox.hjy.tt;

import org.apache.http.HttpStatus;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.base.entity.UserEntity;
import com.app.base.init.ACache;
import com.app.base.init.BaseActivity;
import com.app.base.init.MyApplication;
import com.app.service.GetUserInfoService;
import com.avoscloud.chat.service.UserService;
import com.avoscloud.chat.util.PhotoUtils;
import com.base.app.utils.StringUtil;
import com.base.service.action.constant.HttpTagConstantUtils;
import com.umeng.message.PushAgent;

/**
 * 管家金币
 */
public class MyIntegralActivity extends BaseActivity {

    private ImageView backBtn;
    private TextView integralRecordTxt;   //积分记录
    private Button integralDstBtn;        //积分说明
    private Button integralRBtn;          //积分规则
    
    private View search_records ;//查看积分记录
    
    private TextView intro ,level;
    private TextView rules ;
    private TextView gold_coins ;
    
    private View intro_layout ;
    private View rule_layout ;
    
    private ImageView coin_intro ;
    private ImageView coin_rule ;
   

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	MyApplication.getInstance().addActivity(this);
    	PushAgent.getInstance(this).onAppStart();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_integral);
        setupView();
        addListener();
        String token = ACache.get(getApplicationContext()).getAsString("Token");
        token = (token == null ?"":token) ;
        new GetUserInfoService(getApplicationContext(), HttpTagConstantUtils.USER_INFO, this).getUserInfo(token);
    }

    @Override
    public void setupView() {
    	try {
    		backBtn= (ImageView) findViewById(R.id.backBtn);
            integralRecordTxt= (TextView) findViewById(R.id.integralRecordTxt);
            gold_coins= (TextView) findViewById(R.id.gold_coins);
            integralDstBtn= (Button) findViewById(R.id.integralDstBtn);
            integralRBtn= (Button) findViewById(R.id.integralRBtn);
            search_records=  findViewById(R.id.search_records);
            intro=  (TextView) findViewById(R.id.intro);
            rules=  (TextView) findViewById(R.id.rules);
            
            intro_layout=  (View) findViewById(R.id.intro_layout);
            rule_layout=  (View) findViewById(R.id.rule_layout);
            
            coin_intro = (ImageView) findViewById(R.id.intro_img); 
            coin_rule = (ImageView) findViewById(R.id.rule_img); 
            
            intro.setTextColor(0xffff73bc);
            coin_intro.setVisibility(View.VISIBLE);
        	coin_rule.setVisibility(View.GONE);
        	((TextView)findViewById(R.id.username)).setText(""+aCache.getAsString("nickname"));
        	
        	level = (TextView)findViewById(R.id.level);
    		String integral = ACache.get(this).getAsObject("integral")+"";
    		level.setText(""+StringUtil.getLevel(Integer.valueOf(integral==null||"null".equals(integral)|| "".equals(integral)?"0":integral)));
        	
        	gold_coins.setText(""+integral);
        	
        	//avatar_img.setBackgroundDrawable(null);
    		String avatar_str = aCache.getAsString("avatar");
    		String avatar = avatar_str == null ? "" : avatar_str.replaceAll("\\\\",
    				"");
    		Log.e("MineFrag-------->", avatar);
    		
    		UserService.imageLoader.displayImage(""+ avatar + "?time="
    				+ System.currentTimeMillis(), (ImageView)findViewById(R.id.avatarImg),
					PhotoUtils.myPicImageOptions);
		} catch (Exception e) {
			// TODO: handle exception
		}
        


    }

    @Override
    public void addListener() {
    	backBtn.setOnClickListener(this);
    	search_records.setOnClickListener(this);
    	intro_layout.setOnClickListener(this);
    	rule_layout.setOnClickListener(this);
    }

    @Override
    public void sendMessageHandler(int messageCode) {

    }

    @Override
    public void dataCallBack(Object tag, int statusCode , Object result) {
    	try {
    		if ((Integer) (tag) == HttpTagConstantUtils.USER_INFO) {
	    		if (statusCode == HttpStatus.SC_OK
	    				|| statusCode == HttpStatus.SC_CREATED) {
	    			UserEntity entity = (UserEntity) result;
	    			updateUserInfo(entity);
	    		}else{
	    			//AppToast.toastMsgCenter(this,"Error Code:" + statusCode).show();
	    		}
    		}
		} catch (Exception e) {
			
		}
    	
    }
    

    @Override
    public void onClick(View v) {
        Intent intent = null ;
        switch (v.getId()){
            case R.id.backBtn:
                finish();
                break;
            case R.id.search_records:
            	intent =new Intent(this,IntegralJiLuActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.activity_enter_from_right, R.anim.activity_exit_to_left);
            	break;
            case R.id.intro_layout:
            	intro.setTextColor(0xffff73bc);//#AARRGGBB
            	rules.setTextColor(0xffc7c7c7);
            	coin_intro.setVisibility(View.VISIBLE);
            	coin_rule.setVisibility(View.GONE);
            	break;
            case R.id.rule_layout:
            	rules.setTextColor(0xffff73bc);
            	intro.setTextColor(0xffc7c7c7);
            	coin_intro.setVisibility(View.GONE);
            	coin_rule.setVisibility(View.VISIBLE);
            	break;

        }


    }
    
    @Override
    protected String getActivityName() {
        return "管家金币";
    }
    
    public void updateUserInfo(UserEntity entity) {
		try {
			aCache.put("Token", entity.getToken());// 保存Token
			ACache.get(this).put("nickname", entity.getNiakname());
			ACache.get(this).put("mobile", entity.getMobile());
			ACache.get(this).put("username", entity.getMobile());
			ACache.get(this).put("skin_type", entity.getSkinType());
			ACache.get(this).put("region_name", entity.getRegionNames());
			ACache.get(this).put("region", entity.getRegion());
			ACache.get(this).put("realname", entity.getRealname());
			ACache.get(this).put("birthday", entity.getBirthday());
			ACache.get(this).put("gender", entity.getGender());
			ACache.get(this).put("avatar", entity.getAvatar());
			ACache.get(this).put("integral", entity.getIntegral());
			
			
			String integral = entity.getIntegral()+"";
    		level.setText(""+StringUtil.getLevel(Integer.valueOf(integral==null||"null".equals(integral)|| "".equals(integral)?"0":integral)));
        	
        	gold_coins.setText(""+entity.getIntegral());
        	
    		String avatar_str = entity.getAvatar()+"";
    		String avatar = avatar_str == null ? "" : avatar_str.replaceAll("\\\\",
    				"");
    		Log.e("MineFrag-------->", avatar);
    		UserService.imageLoader.displayImage(""+ avatar + "?time="
    				+ System.currentTimeMillis(), (ImageView)findViewById(R.id.avatarImg),
					PhotoUtils.myPicImageOptions);
    		
		} catch (Exception e) {
			
		}
		
	}
}


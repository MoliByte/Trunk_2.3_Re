package com.beabox.hjy.tt.main.skintest.component;

import junit.framework.Assert;
import android.app.Activity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.app.base.entity.UserEntity;
import com.base.app.utils.DBService;
import com.beabox.hjy.tt.R;

/**
 * 用户信息组件
 * Created by fangjilue on 14-10-3.
 */
public class UserInfoComp {

    ViewGroup viewGroup;
    Activity context;

    //CircleImageView avatarImg;
    TextView nickNameTxt;

    public UserInfoComp(Activity context, ViewGroup viewGroup,ViewGroup container) {
        this.context = context;
        this.viewGroup = viewGroup;
        //this.avatarImg = (CircleImageView) viewGroup.findViewById(R.id.avatarImg);
        this.nickNameTxt = (TextView) viewGroup.findViewById(R.id.nickNameTxt);
        //this.avatarImg.setPadding(DensityUtil.dip2px(context,1));

        CartoonCompListener listener = new CartoonCompListener(context,viewGroup,container,OverLayTypeUtil.UserInfoComp);
        this.viewGroup.setLongClickable(true);
        this.viewGroup.setOnTouchListener(listener);

        localUpdateView();
    }

    public MarginParams getMarginParams(){
        FrameLayout.MarginLayoutParams lp = (FrameLayout.MarginLayoutParams) viewGroup.getLayoutParams();
        return new MarginParams(lp.leftMargin,lp.topMargin);
    }

    public void setMarginParams(MarginParams marginParams){
        FrameLayout.MarginLayoutParams lp = (FrameLayout.MarginLayoutParams) viewGroup.getLayoutParams();
        lp.leftMargin = marginParams.getLeftMargin();
        lp.topMargin = marginParams.getTopMargin();

        viewGroup.setLayoutParams(lp);
    }


    private void localUpdateView(){
        UserEntity user = DBService.getUserEntity();
        Assert.assertNotNull(user);

        //String avatar = user.getAvatar();
        if(user!=null){
        	 String nickName = user.getNiakname();
        	 if(nickName!=null){
        		 nickNameTxt.setText(nickName);
        	 }
        }
        
       

        /*if (!StringUtil.isBlank(avatar)) {
            String url = ApiInvoker.getInstance().getApiBaseUrl() + avatar.trim();
            LoadIconTask task = new LoadIconTask(context, url, avatarImg, null);
            task.execute(url);
        }*/

       
    }

    public void destroy(){
        context = null;
        viewGroup = null;
        nickNameTxt = null;
    }
}

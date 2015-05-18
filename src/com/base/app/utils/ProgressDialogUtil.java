package com.base.app.utils;

import android.app.ProgressDialog;
import android.content.Context;

import com.beabox.hjy.tt.R;

/**
 * Created by fangjilue on 14-5-7.
 */
public class ProgressDialogUtil {

    /**
     * 可以取消 dialog
     * @param context
     * @param title
     * @param message
     * @return
     */
    public static ProgressDialog show(Context context,String title,String message){
        ProgressDialog progressDialog = ProgressDialog.show(context,title,message);
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(true);
        return progressDialog;
    }

    /**
     * 普通 dialog
     * @param context
     * @param title
     * @param message
     * @param isCanceled true: 可以取消，false: 不可以
     * @return
     */
    public static ProgressDialog show(Context context,String title,String message,boolean isCanceled){
        ProgressDialog progressDialog = ProgressDialog.show(context,title,message);
        progressDialog.setCancelable(isCanceled);
        progressDialog.setCanceledOnTouchOutside(isCanceled);
        return progressDialog;
    }

    /**
     * 无标题，message: R.string.alert_loading_msg
     * @param context
     * @param isCanceled true: 可以取消，false: 不可以
     * @return
     */
    public static ProgressDialog show(Context context,boolean isCanceled){
        ProgressDialog progressDialog = ProgressDialog.show(context,null,context.getString(R.string.alert_loading_msg));
        progressDialog.setCancelable(isCanceled);
        progressDialog.setCanceledOnTouchOutside(isCanceled);
        return progressDialog;
    }
}

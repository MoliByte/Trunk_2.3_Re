package com.idongler.widgets;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;

import com.beabox.hjy.tt.R;

/**
 * Created by fangjilue on 14-6-17.
 */
public class ShareDialog extends OperateSelector {
    //分享图片文字url
    public static final int SHARE_MODE_NARMAL = 0;
    //只分享图片
    public static final int SHARE_MODE_IMAGE = 1;

    private Action action;

    private int shareMode;

    public ShareDialog(Activity activity) {
        super(activity, new Configuration(Gravity.BOTTOM, 0, R.style.DialogAnimation));
        this.shareMode = SHARE_MODE_NARMAL;
    }

    public ShareDialog(Activity activity, int shareMode) {
        super(activity, new Configuration(Gravity.BOTTOM, 0, R.style.DialogAnimation));
        this.shareMode = shareMode;
    }

    @Override
    protected int getLayoutResId() {
        if (shareMode == SHARE_MODE_IMAGE) {
            return R.layout.share_image_dialog;
        }
        return R.layout.share_dialog;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    @Override
    protected void postOnShow(final CustomDialog dialog) {
        View cancel = dialog.getWindow().findViewById(R.id.cancelBut);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        View qqBut = dialog.getWindow().findViewById(R.id.qqBut);
        if (qqBut != null) {
            qqBut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (action != null) {
                        action.showQQ();
                    }
                }
            });
        }

        View wxBut = dialog.getWindow().findViewById(R.id.wxBut);
        if (wxBut != null) {
            wxBut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (action != null) {
                        action.showWx();
                    }
                }
            });
        }

        View wxbBut = dialog.getWindow().findViewById(R.id.wxbBut);
        if (wxbBut != null) {
            wxbBut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (action != null) {
                        action.showWxb();
                    }
                }
            });
        }

        View smBut = dialog.getWindow().findViewById(R.id.smBut);
        if (smBut != null) {
            smBut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (action != null) {
                        action.showSm();
                    }
                }
            });
        }

        View wbBut = dialog.getWindow().findViewById(R.id.wbBut);
        if (wbBut != null) {
            wbBut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (action != null) {
                        action.showWb();
                    }
                }
            });
        }
    }

    @Override
    protected void preOnShow(CustomDialog dialog) {
        dialog.setCancel(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);

    }


    public interface Action {
        /**
         * qq 好友
         */
        void showQQ();

        /**
         * 微信好友
         */
        void showWx();

        /**
         * 微信朋友圈
         */
        void showWxb();

        /**
         * 短信
         */
        void showSm();

        /**
         * 微博
         */
        void showWb();

    }
}

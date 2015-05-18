package com.idongler.widgets;

import android.app.Activity;
import android.view.Gravity;

import com.beabox.hjy.tt.R;


/**
 * 操作选择器
 * Created by fangjilue on 14-5-24.
 */
public abstract class OperateSelector {

    private Activity context;
    private Configuration config;

    public OperateSelector(Activity context) {
        this.config = new Configuration();
        this.context = context;
    }

    public OperateSelector(Activity context, Configuration config) {
        this.config = config;
        this.context = context;
    }

    /**
     * 选择器对话框布局资源文件
     *
     * @return
     */
    protected abstract int getLayoutResId();

    /**
     * 选择器显示后执行
     *
     * @param dialog
     */
    protected abstract void postOnShow(CustomDialog dialog);

    /**
     * 选择器显示前执行
     */
    protected abstract void preOnShow(CustomDialog dialog);

    protected Activity getContext() {
        return context;
    }

    public CustomDialog showDialog() {
        final CustomDialog dialog = new CustomDialog(context, R.style.CustomDialogTheme);
        dialog.setContentView(getLayoutResId());
        dialog.setOffsetWidthWrapContentHeight(config.getPaddingLeftAndRight());
        if(config.getDialogAnimation() != -1){
            dialog.getWindow().setWindowAnimations(config.getDialogAnimation());
        }
        dialog.getWindow().setGravity(config.getGravity());
        dialog.setCancel(false);

        preOnShow(dialog);

        dialog.show();

        postOnShow(dialog);

        return dialog;
    }


    public static class Configuration {
        private int paddingLeftAndRight;
        private int gravity;
        private int dialogAnimation;

        public Configuration() {
            gravity = Gravity.BOTTOM;
            paddingLeftAndRight = 20;
            dialogAnimation = R.style.DialogAnimation;
        }

        public Configuration(int gravity, int paddingLeftAndRight, int dialogAnimation) {
            this.gravity = gravity;
            this.paddingLeftAndRight = paddingLeftAndRight;
            this.dialogAnimation = dialogAnimation;
        }

        public int getPaddingLeftAndRight() {
            return paddingLeftAndRight;
        }

        public void setPaddingLeftAndRight(int paddingLeftAndRight) {
            this.paddingLeftAndRight = paddingLeftAndRight;
        }

        public int getGravity() {
            return gravity;
        }

        public void setGravity(int gravity) {
            this.gravity = gravity;
        }

        public int getDialogAnimation() {
            return dialogAnimation;
        }

        public void setDialogAnimation(int dialogAnimation) {
            this.dialogAnimation = dialogAnimation;
        }
    }
}

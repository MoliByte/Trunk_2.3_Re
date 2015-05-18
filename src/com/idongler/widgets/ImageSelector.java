package com.idongler.widgets;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;

import com.base.app.utils.FileUtil;
import com.beabox.hjy.tt.R;

/**
 * 图片选择器
 * Created by fangjilue on 14-5-19.
 */
public class ImageSelector extends OperateSelector{
    private FileUtil fileUtil;

    private Action action;

    private String title;
    /**
     * 打开系统相机
     */
    public static final int TAKE_PICTURE = 1;
    /**
     * 打开系统图库
     */
    public static final int REQ_PICK_THUMB = 2;
    /**
     * 打开系统裁剪图片
     */
    public static final int CROP_PICTURE = 3;
    /**
     * 打开系统相机图片保存文件名
     */
    public static final String TmpImage = "tmp.j";


    public ImageSelector(Activity activity) {
        super(activity);
        this.fileUtil = new FileUtil(activity);
    }

    public ImageSelector(Activity activity, Configuration config) {
        super(activity, config);
        this.fileUtil = new FileUtil(activity);
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.image_selector;
    }

    @Override
    protected void postOnShow(CustomDialog dialog) {
        doAction(dialog);
    }

    @Override
    protected void preOnShow(CustomDialog dialog) {
        if(this.title != null){
            TextView view =(TextView) dialog.getWindow().findViewById(R.id.dialogTitle);
            view.setText(this.title);
        }
    }

    void doAction(final CustomDialog dialog) {

        View cancel = dialog.getWindow().findViewById(R.id.cancelBut);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(action != null){
                    action.cancel();
                }
            }
        });

        //打开系统相机拍照
        View gotoPhoto = dialog.getWindow().findViewById(R.id.gotoPhotoBut);
        gotoPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(action != null){
                    action.openCamera();
                }
            }
        });
        //打开系统图库
        View gotoImageLib = dialog.getWindow().findViewById(R.id.gotoImageLibBut);
        gotoImageLib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(action != null){
                    action.openPhotoAlbum();
                }
            }
        });
    }

    public interface  Action {
        /**
         * 打开相机
         */
        public void openCamera();

        /**
         * 打开相册
         */
        public void openPhotoAlbum();

        /**
         * 取消选择器
         */
        public void cancel();
    }

    public Action createBitmapDataAction(){
        Action action = new Action() {
            @Override
            public void openCamera() {
                Intent intent = new Intent();
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                //intent.addCategory(Intent.CATEGORY_DEFAULT);

                getContext().startActivityForResult(intent, TAKE_PICTURE);
            }

            @Override
            public void openPhotoAlbum() {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                /* 取得相片后返回本画面 */
                getContext().startActivityForResult(intent, REQ_PICK_THUMB);
            }

            @Override
            public void cancel() {

            }
        };

        return action;
    }

    public Action createImageFileAction(){
        Action action = new Action() {
            @Override
            public void openCamera() {
                Intent intent = new Intent();
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                //intent.addCategory(Intent.CATEGORY_DEFAULT);

                // 根据文件地址创建文件，先删除原文件
                fileUtil.deleteImageFile(TmpImage);
                // 把文件地址转换成Uri格式
                Uri uri = fileUtil.getImageFileUri(TmpImage);
                // 设置系统相机拍摄照片完成后图片文件的存放地址
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                getContext().startActivityForResult(intent, TAKE_PICTURE);
            }

            @Override
            public void openPhotoAlbum() {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                /* 取得相片后返回本画面 */
                getContext().startActivityForResult(intent, REQ_PICK_THUMB);
            }

            @Override
            public void cancel() {

            }
        };

        return action;
    }

}

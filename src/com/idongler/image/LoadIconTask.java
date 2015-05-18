/**
 *
 */
package com.idongler.image;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.base.app.utils.FileUtil;

/**
 * icon图片异步下载更新
 *
 * @author fangjilue
 */
public class LoadIconTask extends AsyncTask<String, Void, Bitmap> {

    /**
     * 图片的URL地址
     */
    private String mImageUrl;

    /**
     * ImageView
     */
    private ImageView mImageView;

    private FileUtil fileUtil;

    /**
     * 图片显示控件宽度为0 表示默认mImageView的宽度
     */
    private int mImageViewWidth;

    private ProgressBar mProgressBar;

    private boolean autoHeight;

    private int imageWidth;

    /**
     * 设置图片宽度，等级缩放高度
     * @param imageWidth
     */
    public void setImageWidthAutoHeight(int imageWidth) {
        this.imageWidth = imageWidth;
        this.autoHeight = true;
    }

    /**
     * @param imageUrl
     * @param imageView
     */
    public LoadIconTask(Context context, String imageUrl, ImageView imageView, ProgressBar progressBar) {
        mImageView = imageView;
        mImageUrl = imageUrl;
        fileUtil = new FileUtil(context);
        mProgressBar = progressBar;

        //DisplayMetrics dm = new DisplayMetrics();
        //dm = context.getResources().getDisplayMetrics();
        //float density = dm.density; // 屏幕密度（像素比例：0.75/1.0/1.5/2.0）
        //int densityDPI = dm.densityDpi; // 屏幕密度（每寸像素：120/160/240/320）
        //float xdpi = dm.xdpi;
        //float ydpi = dm.ydpi;

        //mImageViewWidth = dm.widthPixels; // 屏幕宽（像素，如：480px）
        //screenHeight = dm.heightPixels; // 屏幕高（像素，如：800px）
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        // 选中内存中加载
        Bitmap imageBitmap = loadImage(mImageUrl);

        return imageBitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (bitmap != null) {
            addImage(bitmap);
        }
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.GONE);
        }
    }

    /**
     * 根据传入的URL，对图片进行加载。如果这张图片已经存在于SD卡中，则直接从SD卡里读取，否则就从网络上下载。
     *
     * @param imageUrl 图片的URL地址
     * @return 加载到内存的图片。
     */
    private Bitmap loadImage(String imageUrl) {

        String path = fileUtil.getImageLocalPath(imageUrl);
        File imageFile = new File(path);
        boolean ok = false;
        if (!imageFile.exists()) {
            ok = fileUtil.downloadImage(imageUrl, path);
        } else {
            ok = true;
        }
        if (imageUrl != null && ok) {
            Bitmap bitmap = fileUtil.getBitmapByPath(imageFile.getPath());
            return bitmap;
        }
        return null;
    }

    /**
     * 向ImageView中添加一张图片
     *
     * @param bitmap 待添加的图片
     */
    private void addImage(Bitmap bitmap) {
        clearImageCache(bitmap);

        if(imageWidth !=0 && autoHeight){
           // AppLog.debug("imageViewWidth="+ imageWidth + ",src bitmap h=" + bitmap.getHeight() + ",w=" + bitmap.getWidth());
            /*
            //方案一，生成一张新的图片高度为指定高度宽为等比缩放
            Bitmap newBitmap = ImageUtil.createScaleBitmapWithWidth(bitmap,imageWidth);
            AppLog.debug("new image h=" + newBitmap.getHeight() + ",w=" + newBitmap.getWidth());
            if(newBitmap != bitmap){
                if(!bitmap.isRecycled()){
                    bitmap.recycle();
                }
            }
            mImageView.setImageBitmap(newBitmap);*/

            //方案二，使用图片scaleType=centerCrop属性然后重新设置高度。
            ViewGroup.LayoutParams params = mImageView.getLayoutParams();
           // AppLog.debug("src params h="+ params.height + ",w="+params.width);
            params.width = imageWidth;
            params.height =(int)((imageWidth / (float)bitmap.getWidth()) * bitmap.getHeight());
            //AppLog.debug("new params h="+ params.height + ",w="+params.width);
            mImageView.setLayoutParams(params);
        }
        mImageView.setImageBitmap(bitmap);

    }

    private void clearImageCache(Bitmap destBitmap) {
        //清除图片
        BitmapDrawable bd = ((BitmapDrawable) mImageView.getDrawable());
        if (bd != null) {
            Bitmap bitmap = bd.getBitmap();
            mImageView.setImageDrawable(null);
            if (bitmap != null && !bitmap.isRecycled() && bitmap != destBitmap) {
                bitmap.recycle();
            }
            bitmap = null;
        }
    }
}

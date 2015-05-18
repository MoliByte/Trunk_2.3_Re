/**
 *
 */
package com.idongler.image;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import com.base.app.utils.FileUtil;

/**
 * 图片异步下载更新
 *
 * @author fangjilue
 */
public class LoadImageTask extends AsyncTask<String, Void, Bitmap> {

    /**
     * 图片的URL地址
     */
    private String mImageUrl;

    /**
     * 可重复使用的ImageView
     */
    private ZoomImageView mImageView;

    private FileUtil fileUtil;

    /**
     * 图片显示控件宽度为0 表示默认mImageView的宽度
     */
    private int mImageViewWidth;

    private ProgressBar mProgressBar;

    /**
     * @param imageUrl
     * @param imageView
     */
    public LoadImageTask(Context context, String imageUrl, ZoomImageView imageView, ProgressBar progressBar) {
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
        mImageView.setImageBitmap(bitmap);
    }
}
